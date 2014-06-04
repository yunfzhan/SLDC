package org.sldc.core;

import java.util.concurrent.Callable;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sldc.assist.CSQLUtils;
import org.sldc.assist.multitypes.ProtocolsHelper;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.cSQLParser.SelectExprContext;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.SLDCException;

public class ProtocolThread implements Callable<Object[]> {

	private Object key = null;
	private Object value = null;
	
	private CSQLExecutable runner = null;
	private cSQLParser.ProtocolsContext currentNode;
	
	public ProtocolThread(CSQLExecutable exec, @NotNull cSQLParser.ProtocolsContext ctx) {
		runner = exec;
		this.currentNode = ctx;
	}
	
	private cSQLParser.SelectExprContext getSelectExpr(cSQLParser.ProtocolsContext ctx) {
		return (SelectExprContext) ctx.parent.parent;
	}
	
	@Override
	public Object[] call() throws Exception {
		TerminalNode node = (currentNode.protocol()==null)?currentNode.Identifier(1):currentNode.Identifier(0);
		key = (node!= null)?node.getText():currentNode;
		value = CSQLUtils.isString(key)?runner.getScope().getVarValue((String) key):runner.getScope().getVarValue((ParseTree)key);
		if(!(value instanceof SLDCException))
			return new Object[]{key, value};
		
		try {
			cSQLParser.SelectExprContext selectExpr = getSelectExpr(currentNode);
			Scope scope = new Scope(runner.getScope());
			scope.setInput(selectExpr.condition());
			
			Object addr=(currentNode.protocol()==null)?runner.visit(currentNode.Identifier(0)):currentNode.protocol().getText();
			value = ProtocolsHelper.Retrieve(addr, scope);
		} catch (SLDCException e) {
			value = e;
		}
		
		return new Object[]{key, value};
	}

}
