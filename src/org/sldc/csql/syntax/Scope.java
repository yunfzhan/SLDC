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
		_functions.put(name, null);
		return scope;
	}
	
	public void addVariables(String name, Object value) throws DefConflictException {
		if(_variables.containsKey(name)||_functions.containsKey(name)) throw new DefConflictException();
		_variables.put(name, value);
	}
	
	public Scope getFuncValue(String name) throws DefNotDeclException {
		if(!_functions.containsKey(name)) throw new DefNotDeclException();
		return _functions.get(name);
	}
	
	public Object getVarValue(String name) throws DefNotDeclException {
		if(!_variables.containsKey(name)) throw new DefNotDeclException();
		return _variables.get(name);
	}
	
	public void setVarValue(String name, Object value) throws DefNotDeclException {
		if(!_variables.containsKey(name)) throw new DefNotDeclException();
		_variables.put(name, value);
	}
}
