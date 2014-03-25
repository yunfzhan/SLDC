package org.sldc;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLBaseVisitor;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.InvalidType;

public class CSQLExecutable extends cSQLBaseVisitor<Object> {
	
	private Scope baseScope = null;
	
	public CSQLExecutable(Scope scope)
	{
		this.baseScope = scope;
	}
	
	private boolean isNumber(Object obj)
	{
		return (obj instanceof Integer) || (obj instanceof Float) || (obj instanceof Double);
	}
	
	@Override 
	public Object visitMulDiv(@NotNull cSQLParser.MulDivContext ctx) 
	{
		Object left = visit(ctx.expr(0));
		Object right = visit(ctx.expr(1));
		
		if(!isNumber(left)||!isNumber(right))
			return new InvalidType();
		
		if(ctx.MULDIV().getText().equals("*")) return (Double)left*(Double)right;
		else return (Double)left/(Double)right;
	}
	
	@Override 
	public Object visitAddSub(@NotNull cSQLParser.AddSubContext ctx) 
	{
		Object left = visit(ctx.expr(0));
		Object right = visit(ctx.expr(1));
		
		if(!isNumber(left)||!isNumber(right))
			return new InvalidType();
		
		if(ctx.ADDSUB().getText().equals("+")) return (Double)left+(Double)right;
		else return (Double)left-(Double)right;
	}
	
	@Override 
	public Object visitVar(@NotNull cSQLParser.VarContext ctx) 
	{
		String Id = ctx.getText();
		try {
			Object value = this.baseScope.getVarValue(Id);
			return value;
		} catch (DefNotDeclException e) {
			return e;
		}
	}
	
	@Override
	public Object visitInt(@NotNull cSQLParser.IntContext ctx) 
	{
		return ctx.INT().getText();
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
