package org.sldc;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;

public class SQLListener extends cSQLBaseListener {
	@Override
	public void enterSelectExpr(@NotNull cSQLParser.SelectExprContext ctx)
	{
		
	}
	
	@Override
	public void exitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx)
	{

	}
}
