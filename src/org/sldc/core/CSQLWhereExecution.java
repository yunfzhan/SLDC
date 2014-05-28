package org.sldc.core;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.NotSupportOperation;

public class CSQLWhereExecution extends CSQLExecutable {

	//public static final String _in_Method = "$$METHOD";
	public static final String _in_Cookie = "$$COOKIE";
	public static final String _in_Post = "$$BODY";
	
	private Map<String, Object> configs = new HashMap<String, Object>();
	
	public Object getValue(String key) {
		return this.currentScope.getVarValue(key);
	}
	
	public CSQLWhereExecution(Scope scope) {
		super(scope);
		
		//configs.put(_in_Method, null);
		configs.put(_in_Cookie, null);
		configs.put(_in_Post, null);
	}

	@Override 
	public Object visitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		String id = ctx.Identifier().getText();
		
		if(!configs.containsKey(id))
			return new NotSupportOperation(new Throwable());
		
		Object value = null;
		if(ctx.expr()!=null)
			value = visit(ctx.expr());
		else if(ctx.arrayValues()!=null)
			value = visit(ctx.arrayValues());
		else
			return value;
		
		try{
			this.currentScope.setVarValue(ctx.Identifier().getText(), value);
		} catch (DefNotDeclException e) {
			try {
				this.currentScope.addVariable(ctx.Identifier().getText(), value);
			} catch (DefConflictException e1) {
				value = e1;
			}
		}
		return value;
	}
}