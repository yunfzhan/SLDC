package org.sldc.core;

import java.util.ArrayList;

import org.antlr.v4.runtime.misc.NotNull;
import org.sldc.assist.CSQLUtils;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.SyntaxException;

public class CSQLWhereExecution extends CSQLExecutable {

	//public static final String _in_Method = "$$METHOD";
	public static final String _in_Post = "$$BODY";
	public static final String _in_Body_delimeter = "$$BODY_DELI";
	
	private ArrayList<String> variables = new ArrayList<String>();
	
	/**
	 * 
	 * @return variables that are defined in where clause.
	 */
	public ArrayList<String> getVars() {
		return variables;
	}
	
	public Object getValue(String key) {
		return this.getScope().getVarValue(key);
	}
	
	public CSQLWhereExecution(Scope scope) {
		super(scope);
	}

	private boolean isPreDefined(String var) {
		return var.equals(_in_Post)||var.equals(_in_Body_delimeter);
	}
	
	@Override 
	public Object visitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		String id = ctx.Identifier().getText();
		
		if(!id.startsWith("$$"))
			return new SyntaxException(new Throwable(), "Variable must begin with '$$' defined in where clause.");
		
		Object value = null;
		if(ctx.expr()!=null)
			value = visit(ctx.expr());
//		else if(ctx.arrayValues()!=null)
//			value = visit(ctx.arrayValues());
		else
			return value;
		
		if(CSQLUtils.isString(value))
			value = CSQLUtils.removeStringBounds((String) value);
		
		if(!isPreDefined(id)) variables.add(id);
		try{
			this.getScope().setVarValue(id, value);
		} catch (DefNotDeclException e) {
			try {
				this.getScope().addVariable(id, value);
			} catch (DefConflictException e1) {
				value = e1;
			}
		}
		return value;
	}
}
