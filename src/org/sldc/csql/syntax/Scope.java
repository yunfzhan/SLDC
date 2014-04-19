package org.sldc.csql.syntax;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;

public class Scope {
	private String _name;
	private ParseTree _node;
	private ParseTreeProperty<Scope> _anonymous = new ParseTreeProperty<Scope>();
	private Map<String, Scope> _functions = new HashMap<String, Scope>();
	private Map<String, Object> _variables = new HashMap<String, Object>();
	private Scope upper = null;
	
	public Scope(){
		this(null);
	}
	
	public Scope(Scope upper){
		this.upper = upper;
	}
	
	public String getName()
	{
		return this._name;
	}
	
	public void setName(String name)
	{
		this._name = name;
	}
	
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
		_anonymous.put(node, scope);
		return scope;
	}
	
	public Scope getAnonymous(ParseTree node) {
		return _anonymous.get(node);
	}
	
	public Scope addFunction(String name) throws DefConflictException {
		if(_functions.containsKey(name)||_variables.containsKey(name)) throw new DefConflictException(name);
		Scope scope = new Scope(this);
		_functions.put(name, scope);
		return scope;
	}
	
	public void addVariable(String name, Object value) throws DefConflictException {
		if(_variables.containsKey(name)||_functions.containsKey(name)) throw new DefConflictException(name);
		_variables.put(name, value);
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
			throw new DefNotDeclException(name);
	}
	
	public boolean containVar(String name)
	{
		return _variables.containsKey(name)||(upper!=null&&upper.containVar(name));
	}
	
	public Object getVarValue(String name) {
		if(_variables.containsKey(name))
			return _variables.get(name);
		else if(upper!=null&&upper.containVar(name))
			return upper.getVarValue(name);
		else
			return new DefNotDeclException(name);
	}
	
	public void setVarValue(String name, Object value) throws DefNotDeclException {
		if(_variables.containsKey(name))
			_variables.put(name, value);
		else if(upper!=null)
			upper.setVarValue(name, value);
		else
			throw new DefNotDeclException(name);
	}
}
