package org.sldc;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
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
	
	public CSQLValidator(ParseTree tree)
	{
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
				scope = this.currentScope.addAnonymous(ctx.stats());
			}
			this.currentScope = scope;
		} catch (DefConflictException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void exitBlock(@NotNull cSQLParser.BlockContext ctx) { 
		this.currentScope = this.currentScope.pop();
	}
	
	@Override 
	public void enterSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {
		this.currentScope = this.currentScope.addAnonymous(ctx);
	}
	
	@Override 
	public void exitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {	
		this.currentScope = this.currentScope.pop();
	}
}
