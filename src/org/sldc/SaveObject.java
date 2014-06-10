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
	private Object attachment = null;
	
	public SaveObject(Object o, Object attach) {
		this.internalObject = o;
		this.attachment = attach;
	}

	public String toString() {
		return internalObject.toString();		
	}
	
	public Object getAttachment() {
		return this.attachment;
	}
	
	public boolean isMap() {
		return isMap(this.internalObject);
	}
	
	public boolean isMap(Object o) {
		return o instanceof Map;
	}
	
	public boolean isCollection() {
		return isCollection(this.internalObject);
	}
	
	public boolean isCollection(Object o) {
		return CSQLUtils.isArray(o)||CSQLUtils.isCollection(o);
	}
	
	public boolean isDataObject() {
		return isDataObject(this.internalObject);
	}
	
	public boolean isDataObject(Object o) {
		return o instanceof CSQLChunkDataImpl;
	}
	
	public boolean isString() {
		return isString(this.internalObject);
	}
	
	public boolean isString(Object o) {
		return CSQLUtils.isString(o);
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
