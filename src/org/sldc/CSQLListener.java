package org.sldc;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;

public class CSQLListener extends cSQLBaseListener {
	@Override 
	public void exitFunc(@NotNull cSQLParser.FuncContext ctx) 
	{
		String text = ctx.FUNCID().getText();
		System.out.println("FuncID: "+text);
	}
}
