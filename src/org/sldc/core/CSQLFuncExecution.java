package org.sldc.core;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;

public class CSQLFuncExecution extends CSQLExecutable {

	private static final int FUNC_NONE = 0;
	private static final int FUNC_RETURN = 1;
	
	private int currentFlow = FUNC_NONE;
	private Object returnValue = null;
	
	public CSQLFuncExecution(Scope scope) {
		super(scope);
	}
	
	@Override 
	public Object visitStatReturn(@NotNull cSQLParser.StatReturnContext ctx) { 
		currentFlow = FUNC_RETURN;
		returnValue = super.visitStatReturn(ctx);
		return null;
	}
	
	@Override 
	public Object visitStat(@NotNull cSQLParser.StatContext ctx) { 
		if(currentFlow == FUNC_NONE)
			return visitChildren(ctx); 
		else
			return returnValue;
	}
}
