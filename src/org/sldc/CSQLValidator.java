package org.sldc;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
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
	
	public Scope getScope()
	{
		return this.currentScope;
	}
	
	public List<SLDCException> getErrors()
	{
		return this.exceptions;
	}
	
	@Override 
	public void exitVarDecl(@NotNull cSQLParser.VarDeclContext ctx) 
	{
		List<cSQLParser.VarAssignContext> list = ctx.varAssign();
		for(int i=0;i<list.size();i++)
		{
			cSQLParser.VarAssignContext vac = list.get(i);
			TerminalNode id = vac.Identifier();
			Object value = null;
			if(vac.EQU()!=null)
			{
				CSQLExecutable run = new CSQLExecutable(this.currentScope);
				value = run.visit(vac);
			}
			try {
				this.currentScope.addVariables(id.getText(), value);
			} catch (DefConflictException e) {
				this.exceptions.add(e);
			}
		}
	}
	
	@Override 
	public void enterBlock(@NotNull cSQLParser.BlockContext ctx) {
		try {
			Scope scope = null;
			if(ctx.parent instanceof cSQLParser.FundeclContext)
			{
				cSQLParser.FundeclContext parent = (FundeclContext) ctx.parent;
				scope = this.currentScope.addFunction(parent.Identifier().getText());
			}
			else
			{
				scope = this.currentScope.addAnonymous();
			}
			this.currentScope = scope;
			scope.setInput(ctx.getText());
		} catch (DefConflictException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void exitBlock(@NotNull cSQLParser.BlockContext ctx) { 
		this.currentScope = this.currentScope.getUpperScope();
	}

}
