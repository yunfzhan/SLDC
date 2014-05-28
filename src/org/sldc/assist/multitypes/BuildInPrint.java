package org.sldc.assist.multitypes;

import java.util.Map;

import org.sldc.assist.CSQLUtils;
import org.sldc.exception.SLDCException;

public class BuildInPrint {
	@SuppressWarnings("rawtypes")
	private static String printMap(Map map) {
		Object[] keys=map.keySet().toArray();
		StringBuilder sb = new StringBuilder();
		for(Object key : keys)
		{
			sb.append("|\t"+key+"\n | ");
			sb.append(print(map.get(key))+"\n");
		}
		return sb.toString();
	}
	
	private static String printArray(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		for(Object o : objs)
		{
			sb.append(print(o)+"\n");
		}
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public static String print(Object obj){
		if(obj instanceof Map)
			return printMap((Map)obj);
		else if(CSQLUtils.isArray(obj))
			return printArray((Object[]) obj);
		else if(CSQLUtils.isString(obj))
			return CSQLUtils.removeStringBounds((String)obj);
		else if(obj instanceof SLDCException)
			return "";
		else
			return obj.toString();
	}
}
