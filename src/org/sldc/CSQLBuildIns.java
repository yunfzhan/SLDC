package org.sldc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;
import org.sldc.protocols.HTMLAnalyzer;

public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("isnull", "isNull");
		functions.put("print", "print");
		functions.put("pow", "Pow");
	}
	
	public static Object invoke(String funcName, Object[] params)
	{
		Object result = new NotBuildInFunction(funcName, new Throwable());
		if(functions.containsKey(funcName))
		{
			Class<?> self = CSQLBuildIns.class;
			Method[] methods = self.getDeclaredMethods();
			
			for(int i=0;i<methods.length;i++)
				if(methods[i].getName().equals(functions.get(funcName)))
				{
					try {
						Class<?>[] ptypes = methods[i].getParameterTypes();
						if(ptypes.length!=params.length) continue;
						result = methods[i].invoke(self, params);
						break;
					} catch (Exception e) {
						result = e;
					}
				}
		}
		return result;
	}
	
	public static boolean isNull(Object obj) {
		return obj==null;
	}
	
	
	
	public static Object _InCore(Object contents, String plain){
		return _InCore(contents, plain, "p");
	}
	
	public static Object _InCore(Object contents, String srchable, String indicator) {
		if(srchable==null||srchable.equals("")) return "";
		srchable = CSQLUtils.removeStringBounds(srchable);
		indicator = CSQLUtils.removeStringBounds(indicator);
		
		if(indicator.equalsIgnoreCase("p")){
			ArrayList<Integer> res = CSQLUtils.BoyerMoore(srchable, (String) contents);
			return res.size()!=0;
		}else if(indicator.equalsIgnoreCase("r")){
			
		}else if(indicator.equalsIgnoreCase("t")){
			try {
				String[] res = HTMLAnalyzer.startAnalyze((String) contents, srchable);
				return res;
			} catch (IOException e) {
				return e;
			}
		}
		return "";
	}

	public static Double Pow(Object base, Object pow) throws InvalidType
	{
		return Math.pow(CSQLUtils.convertToDbl(base), CSQLUtils.convertToDbl(pow));
	}
	
	@SuppressWarnings("rawtypes")
	private static String printMap(Map map) {
		Object[] keys=map.keySet().toArray();
		StringBuilder sb = new StringBuilder();
		for(Object key : keys)
		{
			sb.append("|\t"+key+"\n | ");
			sb.append(printObject(map.get(key))+"\n");
		}
		return sb.toString();
	}
	
	private static String printArray(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<objs.length;i++)
		{
			sb.append(printObject(objs[i])+"\n");
		}
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private static String printObject(Object obj){
		if(obj instanceof Map)
			return printMap((Map)obj);
		else if(obj.getClass().isArray())
			return printArray((Object[]) obj);
		else
			return obj.toString();
	}
	
	public static void print(Object obj){
		System.out.print(printObject(obj));
	}
}
