package org.sldc.csql.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;

public class Scope {
	private String _name;
	private String _input;
	private List<Scope> _anonymous = new ArrayList<Scope>();
	private Map<String, Scope> _functions = new HashMap<String, Scope>();
	private Map<String, Object> _variables = new HashMap<String, Object>();
	private Map<String, Object> _alias = new HashMap<String, Object>();
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
	
	public String getInput()
	{
		return this._input;
	}
	
	public void setInput(String input)
	{
		this._input = input;
	}
	
	public Scope getUpperScope(){
		return this.upper;
	}
	
	public Scope addAnonymous() {
		Scope scope = new Scope(this);
		_anonymous.add(scope);
		return scope;
	}
	
	public Scope addFunction(String name) throws DefConflictException {
		if(_functions.containsKey(name)||_variables.containsKey(name)) throw new DefConflictException();
		Scope scope = new Scope(this);
		_functions.put(name, scope);
		return scope;
	}
	
	public void addVariables(String name, Object value) throws DefConflictException {
		if(_variables.containsKey(name)||_functions.containsKey(name)) throw new DefConflictException();
		_variables.put(name, value);
	}
	
	public void addAlias(String name, Object value) throws DefConflictException {
		if(_alias.containsKey(name)) throw new DefConflictException();
		_alias.put(name, value);
	}
	
	public Object getAlias(String name)
	{
		if(!_alias.containsKey(name)) return new DefNotDeclException();
		return _alias.get(name);
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
			throw new DefNotDeclException();
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
			return new DefNotDeclException();
	}
	
	public void setVarValue(String name, Object value) throws DefNotDeclException {
		if(_variables.containsKey(name))
			_variables.put(name, value);
		else if(upper!=null)
			upper.setVarValue(name, value);
		else
			throw new DefNotDeclException();
	}
}
