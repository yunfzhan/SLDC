package org.sldc.assist.multitypes;

import java.util.Map;

import org.sldc.exception.InvalidType;
import org.sldc.protocols.http.CSQLHttpChunkImpl;

public class HttpHeaderAssist {
	// Designed for CSQLHttpChunkImpl since it's not a common utility that I don't like.
	// TODO change
	public static Object[] getKeys(Object o) {
		if(o instanceof Map) {
			return ((Map<?,?>)o).keySet().toArray();
		}else if(o instanceof CSQLHttpChunkImpl) {
			return ((CSQLHttpChunkImpl)o).getHeaderKeys();
		}
		return new Object[]{new InvalidType(new Throwable())};
	}
	
	public static Object getHeaderItem(Object o, Object key) {
		if(o instanceof Map) {
			return ((Map<?,?>)o).get(key);
		}else if(o instanceof CSQLHttpChunkImpl) {
			return ((CSQLHttpChunkImpl)o).getHeaderItem(key);
		}
		return new InvalidType(new Throwable());
	}
}
