package org.sldc.csql.syntax;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.sldc.assist.CSQLUtils;
import org.sldc.csql.cSQLParser;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.SyntaxException;

/**
 * @version 1.0
 * @author Yunfei
 * Include variables and functions defined in current visibility. It stands for a scope of visibility.
 */
public class Scope {
//	private String _name;
	private ParseTree _node;
	private ParseTreeProperty<Scope> _anonymousScope = new ParseTreeProperty<Scope>();
	private Map<String, Scope> _functions = new HashMap<String, Scope>();
	private Map<String, Object> _namedVars = Collections.synchronizedMap(new HashMap<String, Object>());
	private ParseTreeProperty<Object> _anonymousVars = new ParseTreeProperty<Object>();
	private Scope upper = null;
	
	public static final Object UnDefined = new Object();
	
	public Scope(){
		this(null);
	}
	
	public Scope(Scope upper){
		this.upper = upper;
	}
	
//	public String getName()
//	{
//		return this._name;
//	}
//	
//	public void setName(String name)
//	{
//		this._name = name;
//	}
//	
	public ParseTree getInput()
	{
		return this._node;
	}
	
	public void setInput(ParseTree node)
	{
		this._node = node;
	}
	
	public Scope getUpperScope(){
		return this.upper;
	}
	
	public Scope addAnonymous(ParseTree node) {
		Scope scope = new Scope(this);
		_anonymousScope.put(node, scope);
		scope.setInput(node);
		return scope;
	}
	
	public Scope getAnonymous(ParseTree node) {
		return _anonymousScope.get(node);
	}
	
	public Scope addFunction(String name) throws DefConflictException {
		if(_functions.containsKey(name)||_namedVars.containsKey(name)) throw new DefConflictException(name, new Throwable());
		Scope scope = new Scope(this);
		_functions.put(name, scope);
		return scope;
	}
	
	public boolean containFunc(String name)
	{
		return _functions.containsKey(name)||(upper!=null&&upper.containFunc(name));
	}
	
	public Scope getFuncValue(String name) throws DefNotDeclException {
		if(_functions.containsKey(name))
			return _functions.get(name);
		else if(upper!=null&&upper.containFunc(name))
			return upper.getFuncValue(name);
		else
			throw new DefNotDeclException(name, new Throwable());
	}
	
	public void addVariable(String name, Object value) throws DefConflictException {
		if(_namedVars.containsKey(name)||_functions.containsKey(name)) throw new DefConflictException(name, new Throwable());
		_namedVars.put(name, value);
	}
	
	public void addVariable(ParseTree node, Object value) {
		_anonymousVars.put(node, value);
	}
	
	public void addVariable(Object key, Object value) throws DefConflictException {
		if(key instanceof ParseTree)
			addVariable((ParseTree)key, value);
		else if(key instanceof String)
			addVariable((String)key, value);
	}
	
	public boolean containVar(String name)
	{
		return _namedVars.containsKey(name)||(upper!=null&&upper.containVar(name));
	}
	
	public boolean containVar(ParseTree node){
		return this._anonymousVars.get(node)!=null;
	}
	
	public Object getVarValue(String name) {
		if(_namedVars.containsKey(name))
			return _namedVars.get(name);
		else if(upper!=null&&upper.containVar(name))
			return upper.getVarValue(name);
		else
			return new DefNotDeclException(name, new Throwable());
	}
	
	public Object getVarValue(ParseTree node) {
		if(containVar(node))
			return _anonymousVars.get(node);
		else if(upper!=null&&upper.containVar(node))
			return upper.getVarValue(node);
		else
			return new DefNotDeclException("Anonymous var "+node.getText(), new Throwable());
	}
	
	public void setVarValue(String name, Object value) throws DefNotDeclException {
		if(_namedVars.containsKey(name))
			_namedVars.put(name, value);
		else if(upper!=null)
			upper.setVarValue(name, value);
		else
			throw new DefNotDeclException(name, new Throwable());
	}
	
	public void setVarValue(ParseTree node, Object value) throws DefNotDeclException {
		if(_namedVars.containsKey(node))
			_anonymousVars.put(node, value);
		else if(upper!=null)
			upper.setVarValue(node, value);
		else
			throw new DefNotDeclException("Anonymous var "+node.getText(), new Throwable());
	}
	
	public void assignFunValues(Object[] params) throws SyntaxException, DefNotDeclException, DefConflictException {
		cSQLParser.FundeclContext node = CSQLUtils.getFuncDeclaration(getInput());
		if(node==null) throw new SyntaxException(new Throwable());
		//add support of default parameter if the number of formal parameters are not equal to the one of real parameters later.
		int fpsize = node.funcParms()==null?0:node.funcParms().Identifier().size();
		int rpsize = params.length;
		if(rpsize>=fpsize)
		{
			int j = 0;
			for(int i=0;i<fpsize;j++,i++)
			{
				String varName = node.funcParms().Identifier(i).getText();
				if(this._namedVars.containsKey(varName))
					setVarValue(varName, params[j]);
				else
					addVariable(varName, params[j]);
			}
		}else{
			int j=0;
			for(int i=0;i<rpsize;i++,j++)
			{
				String varName = node.funcParms().Identifier(j).getText();
				if(this._namedVars.containsKey(varName))
					setVarValue(varName, params[i]);
				else
					addVariable(varName, params[i]);
			}
			for(int i=j;i<fpsize;i++)
			{
				String varName = node.funcParms().Identifier(i).getText();
				if(this._namedVars.containsKey(varName))
					setVarValue(varName, UnDefined);
				else
					addVariable(varName, UnDefined);
			}
		}
	}
}
