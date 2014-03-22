package org.sldc;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;

public class CSQLValidator extends cSQLBaseListener {
	private Scope currentScope = new Scope();
	
	public Scope getScope()
	{
		return this.currentScope;
	}
	
	@Override 
	public void exitVarDecl(@NotNull cSQLParser.VarDeclContext ctx) {
		
	}
	
	@Override 
	public void enterBlock(@NotNull cSQLParser.BlockContext ctx) {
		
	}
	
	@Override 
	public void exitBlock(@NotNull cSQLParser.BlockContext ctx) { 
		
	}

	@Override 
	public void exitFundecl(@NotNull cSQLParser.FundeclContext ctx) { 
		
	}

}
