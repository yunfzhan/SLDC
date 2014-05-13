package org.sldc;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sldc.assist.CSQLProtocolFactory;
import org.sldc.assist.multitypes.ProtocolsHelper;
import org.sldc.core.CSQLExecutable;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.cSQLParser.FundeclContext;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.IRuntimeError;
import org.sldc.exception.SLDCException;

public class CSQLValidator extends cSQLBaseListener implements IRuntimeError {
	private Scope currentScope = new Scope();
	private List<SLDCException> exceptions = new ArrayList<SLDCException>();
	private CSQLProtocolFactory _pFactory = null;
	public CSQLValidator(ParseTree tree, CSQLProtocolFactory factory)
	{
		this._pFactory = factory;
		this.currentScope.setInput(tree);
	}
	
	public Scope getScope()
	{
		return this.currentScope;
	}
	
	public List<SLDCException> getErrors()
	{
		return this.exceptions;
	}
	
	@Override 
	public void exitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		CSQLExecutable runner = new CSQLExecutable(this.currentScope);
		Object r = runner.visit(ctx);
		if(r instanceof SLDCException)
			this.exceptions.add((SLDCException) r);
	}
	
	@Override 
	public void enterBlock(@NotNull cSQLParser.BlockContext ctx) {
		try {
			Scope scope = null;
			if(ctx.parent instanceof cSQLParser.FundeclContext)
			{
				cSQLParser.FundeclContext parent = (FundeclContext) ctx.parent;
				String funcName = parent.Identifier().getText();
				scope = this.currentScope.addFunction(funcName);
				scope.setInput(ctx.stats());
			}
			else
			{
				scope = this.currentScope.addAnonymous(ctx);
			}
			this.currentScope = scope;
		} catch (DefConflictException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void exitBlock(@NotNull cSQLParser.BlockContext ctx) { 
		this.currentScope = this.currentScope.getUpperScope();
	}
	
	@Override 
	public void exitProtocols(@NotNull cSQLParser.ProtocolsContext ctx) {
		Object key = ctx.Identifier(1) != null?ctx.Identifier(1).getText():ctx;
		
		try {
			Object r = null;
			if(ctx.Identifier(0)!=null){
				CSQLExecutable runner = new CSQLExecutable(this.currentScope);
				Object addr = runner.visit(ctx.Identifier(0));
				r = ProtocolsHelper.Retrieve(_pFactory, addr);
			}else{
				String addr = ctx.protocol().getText();
				r = ProtocolsHelper.Retrieve(_pFactory, addr);
			}
			
			this.currentScope.addVariable(key, r);
		} catch (SLDCException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void enterSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {
		Scope scope = this.currentScope.addAnonymous(ctx);
		this.currentScope = scope;
	}
	
	@Override 
	public void exitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {	
		CSQLExecutable runner = new CSQLExecutable(this.currentScope);
		this.currentScope = this.currentScope.getUpperScope();
		this.currentScope.addVariable(ctx, runner.visit(ctx));
	}
}
