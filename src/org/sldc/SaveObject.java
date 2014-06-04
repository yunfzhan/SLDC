package org.sldc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.sldc.assist.CSQLUtils;
import org.sldc.assist.ItemIterator;
import org.sldc.exception.InvalidType;
import org.sldc.protocols.CSQLChunkDataImpl;


public class SaveObject implements Iterable<Object> {

	private Object internalObject = null;
	
	public SaveObject(Object o) {
		this.internalObject = o;
	}

	public String toString() {
		return internalObject.toString();		
	}
	
	public boolean isMap() {
		return internalObject instanceof Map;
	}
	
	public boolean isCollection() {
		return CSQLUtils.isArray(internalObject)||CSQLUtils.isCollection(internalObject);
	}
	
	public boolean isDataObject() {
		return internalObject instanceof CSQLChunkDataImpl;
	}
	
	public boolean isString() {
		return CSQLUtils.isString(internalObject);
	}
	
	@SuppressWarnings("rawtypes")
	public long size() {
		if(isMap())
			return ((Map)internalObject).size();
		else if(isCollection())
			try{
				Collection r = (Collection)internalObject;
				return r.size();
			}catch(Exception e){
				return ((Object[])internalObject).length;
			}
		else if(isDataObject())
			return ((CSQLChunkDataImpl)internalObject).size();
		else if(isString())
			return ((String)internalObject).length();
		return 0;
	}

	@Override
	public Iterator<Object> iterator() {
		if(isCollection())
			return new ItemIterator(internalObject).iterator();
		else if(isMap())
			return new ItemIterator(((Map<?, ?>)internalObject).keySet()).iterator();
		return null;
	}
	
	public Object getValue(Object key) {
		if(isMap())
			return ((Map<?, ?>)internalObject).get(key);
		return new InvalidType(new Throwable());
	}
}
