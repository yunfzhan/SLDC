package org.sldc.csql.syntax;

import java.util.HashMap;
import java.util.Map;

import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;

public class Scope {
	private String _name;
	private Map<String, Object> _functions = new HashMap<String, Object>();
	private Map<String, Object> _variables = new HashMap<String, Object>();
	private Scope next = null;
	
	public Scope(){
		this(null);
	}
	
	public Scope(Scope next){
		this.next = next;
	}
	
	public String getName()
	{
		return this._name;
	}
	
	public void setName(String name)
	{
		this._name = name;
	}
	
	public void addFunction(String name, Object value) throws DefConflictException {
		if(_functions.containsKey(name)||_variables.containsKey(name)) throw new DefConflictException();
		_functions.put(name, value);
	}
	
	public void addVariables(String name, Object value) throws DefConflictException {
		if(_variables.containsKey(name)||_functions.containsKey(name)) throw new DefConflictException();
		_variables.put(name, value);
	}
	
	public Object getFuncValue(String name) throws DefNotDeclException {
		if(!_functions.containsKey(name)) throw new DefNotDeclException();
		return _functions.get(name);
	}
	
	public void setFuncValue(String name, Object value) throws DefNotDeclException {
		if(!_functions.containsKey(name)) throw new DefNotDeclException();
		_functions.put(name, value);
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
