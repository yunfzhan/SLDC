package org.sldc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;

public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("print", "print");
		functions.put("pow", "Pow");
	}
	
	public static Object invoke(String funcName, Object[] params)
	{
		Object result = new NotBuildInFunction();
		if(functions.containsKey(funcName))
		{
			Class<?> self = CSQLBuildIns.class;
			Method[] methods = self.getDeclaredMethods();
			
			for(int i=0;i<methods.length;i++)
				if(methods[i].getName().equals(functions.get(funcName)))
				{
					try {
						result = methods[i].invoke(self, params);
						break;
					} catch (Exception e) {	}
				}
		}
		return result;
	}
	
	public static Double convertToDbl(Object obj) throws InvalidType
	{
		if(obj instanceof Double||obj instanceof Float||obj instanceof Integer)
			return new Double((Double)obj);
		else if(obj instanceof String)
			return Double.valueOf((String)obj);
		else
			throw new InvalidType();
	}
	
	public static String _InCore(Object param){
		return "Not implemented yet";
	}
	
	public static Double Pow(Object base, Object pow) throws InvalidType
	{
		return Math.pow(convertToDbl(base), convertToDbl(pow));
	}
	
	public static void print(Object obj){
		System.out.print(obj);
	}
}
