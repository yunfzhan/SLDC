package org.sldc.core;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.assist.CSQLUtils;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.SLDCException;
import org.sldc.exception.SyntaxException;

public class CSQLoopExecution extends CSQLExecutable {

	private static final int LOOP_NONE = 0;
	private static final int LOOP_BREAK = 1;
	private static final int LOOP_CONTINUE = 2;
	private static final int LOOP_RETURN = 3;
	
	private int currentFlow = LOOP_NONE;
	private Object returnValue = null;
	
	public CSQLoopExecution(Scope scope) {
		super(scope);
	}

	@Override 
	public Object visitStatReturn(@NotNull cSQLParser.StatReturnContext ctx) { 
		currentFlow = LOOP_RETURN;
		returnValue = super.visitStatReturn(ctx);
		return returnValue;
	}
	
	@Override 
	public Object visitStatBreak(@NotNull cSQLParser.StatBreakContext ctx) { 
		currentFlow = LOOP_BREAK;
		return null;
	}
	
	@Override 
	public Object visitStatContinue(@NotNull cSQLParser.StatContinueContext ctx) { 
		currentFlow = LOOP_CONTINUE;
		return null;
	}
	
	@Override 
	public Object visitStat(@NotNull cSQLParser.StatContext ctx) { 
		if(currentFlow == LOOP_NONE)
			return visitChildren(ctx); 
		else
			return null;
	}
	
	@Override 
	public Object visitForStat(@NotNull cSQLParser.ForStatContext ctx) {
		Object r = visit(ctx.varAssign());
		if(r instanceof SLDCException) return r;
		Object cond = visit(ctx.expr(0));
		if(!CSQLUtils.isBool(cond)) return new SyntaxException(new Throwable());
		while((Boolean)cond)
		{
			visit(ctx.stat());
			if(currentFlow!=LOOP_NONE&&currentFlow!=LOOP_CONTINUE) 
				break;
			else if(currentFlow == LOOP_CONTINUE)
				currentFlow = LOOP_NONE;
			r = visit(ctx.expr(1));
			cond = visit(ctx.expr(0));
		}
		return returnValue;
	}
	
	@Override 
	public Object visitWhileStat(@NotNull cSQLParser.WhileStatContext ctx) {
		Object r = visit(ctx.expr());
		if(!CSQLUtils.isBool(r)) return r;
		Boolean cond = (Boolean)r;
		while(cond)
		{
			visit(ctx.stat());
			if(currentFlow!=LOOP_NONE&&currentFlow!=LOOP_CONTINUE) 
				break;
			else if(currentFlow == LOOP_CONTINUE)
				currentFlow = LOOP_NONE;
			cond = (Boolean)visit(ctx.expr());
		}
		return returnValue;
	}
}
