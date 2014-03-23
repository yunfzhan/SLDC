package org.sldc;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLBaseVisitor;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;

public class CSQLExecutable extends cSQLBaseVisitor<Object> {
	
	private Scope baseScope = null;
	
	public CSQLExecutable(Scope scope)
	{
		this.baseScope = scope;
	}
	
	@Override 
	public Object visitNum(@NotNull cSQLParser.NumContext ctx) 
	{ 
		return ctx.Number().getText();
	}
	
	@Override 
	public Object visitString(@NotNull cSQLParser.StringContext ctx) 
	{ 
		return ctx.String().getText(); 
	}
}
