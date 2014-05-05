package org.sldc.assist.multitypes;

import java.util.Map;

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
		for(int i=0;i<objs.length;i++)
		{
			sb.append(print(objs[i])+"\n");
		}
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public static String print(Object obj){
		if(obj instanceof Map)
			return printMap((Map)obj);
		else if(obj.getClass().isArray())
			return printArray((Object[]) obj);
		else
			return obj.toString();
	}
}
