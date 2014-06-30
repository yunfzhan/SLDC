package org.sldc.assist;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.core.CSQLoopExecution;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;

public class LoopAssist {
	private CSQLoopExecution looper = null;
	
	public LoopAssist(Scope parent) {
		looper = new CSQLoopExecution(parent);
	}
	
	public Object visitFor(@NotNull cSQLParser.ForStatContext ctx) {
		return looper.visit(ctx);
	}
	
	public Object visitWhile(@NotNull cSQLParser.WhileStatContext ctx) {
		return looper.visit(ctx);
	}
}
