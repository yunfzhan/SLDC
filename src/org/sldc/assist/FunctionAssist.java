package org.sldc.assist;

import java.io.IOException;

import org.sldc.core.CSQLFuncExecution;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.SLDCException;

public class FunctionAssist {
	private Scope currentScope = null;
	
	public FunctionAssist(Scope scope) {
		this.currentScope = scope;
	}
	
	public Object execute() throws IOException, SLDCException {
		CSQLFuncExecution exec = new CSQLFuncExecution(this.currentScope);
		return exec.run();
	}
}
