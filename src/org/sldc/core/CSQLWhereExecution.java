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

	private static final String VAR_WITHOUT_HEADING = "$$";
	private static final String VAR_WITH_HEADING	= "_$$";
	
	//public static final String _in_Method = "$$METHOD";
	public static final String _in_Post = "$$BODY";
	public static final String _in_Body_delimeter = "$$BODY_DELI";
	
	private ArrayList<String> variables = new ArrayList<String>();
	private String varOfAddr = null;
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
	
	public CSQLWhereExecution(Scope scope, String addrKey) {
		super(scope);
		this.varOfAddr = addrKey;
	}

	private boolean isPreDefined(String var) {
		return var.equals(_in_Post)||var.equals(_in_Body_delimeter);
	}
	
	private boolean isSpecificScopeVariable(String id) {
		if(varOfAddr==null)
			return id.startsWith(VAR_WITHOUT_HEADING);
		else
			return id.startsWith(varOfAddr+VAR_WITH_HEADING);
	}
	
	@Override 
	public Object visitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		String id = ctx.Identifier().getText();
		
		//if(!isSpecificScopeVariable(id))
		//	return new SyntaxException(new Throwable(), "Variable that is defined in where clause must begin with '$$' or '<varname>_$$'.");
		
		Object value = null;
		if(ctx.expr()!=null)
			value = visit(ctx.expr());
//		else if(ctx.arrayValues()!=null)
//			value = visit(ctx.arrayValues());
		else
			return value;
		
		if(CSQLUtils.isString(value))
			value = CSQLUtils.removeStringBounds((String) value);
		
		if(isSpecificScopeVariable(id)) {
			if(id.startsWith(VAR_WITHOUT_HEADING))
				id = id.substring(VAR_WITHOUT_HEADING.length());
			else if(varOfAddr!=null&&id.startsWith(varOfAddr+VAR_WITH_HEADING)){
				id = id.substring(id.indexOf(VAR_WITH_HEADING)+VAR_WITH_HEADING.length());
			}
			else
				return new SyntaxException(new Throwable(), "Variable that is defined in where clause must begin with '"+VAR_WITHOUT_HEADING+"' or '"+varOfAddr+VAR_WITH_HEADING+"'.");
			// for pre-defined, keep original
			if(isPreDefined(VAR_WITHOUT_HEADING+id))
				id = VAR_WITHOUT_HEADING + id;
		}
		else
			return null;
		variables.add(id);
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
