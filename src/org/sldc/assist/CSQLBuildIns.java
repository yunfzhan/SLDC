package org.sldc.assist;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.sldc.assist.multitypes.BuildInLength;
import org.sldc.assist.multitypes.BuildInPrint;
import org.sldc.assist.multitypes.BuildInSearchFunction;
import org.sldc.assist.multitypes.BuildInStrConv;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;
import org.sldc.exception.SLDCException;

public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("isvoid", "isNull");
		functions.put("print", "print");
		functions.put("pow", "Pow");
		functions.put("len", "getLength");
		functions.put("string", "convertToString");
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
		return obj==null||obj.equals(Scope.UnDefined)||(obj instanceof SLDCException);
	}
	
	public static long getLength(Object o) {
		return BuildInLength.length(o);
	}
	
	public static String convertToString(Object o) {
		return BuildInStrConv.toString(o);
	}
	
	public static Object _InCore(Object contents, String plain){
		return _InCore(contents, plain, "p");
	}
	
	public static Object _InCore(Object contents, String srchable, String indicator) {
		if(srchable==null||srchable.equals("")) return false;
		srchable = CSQLUtils.removeStringBounds(srchable);
		if(srchable.equals("")) return false; // string to search can't be empty after removing quotes.
		indicator = CSQLUtils.removeStringBounds(indicator);
		
		return BuildInSearchFunction.search(contents, srchable, indicator);
	}

	public static Double Pow(Object base, Object pow) throws InvalidType
	{
		return Math.pow(CSQLUtils.convertToDbl(base), CSQLUtils.convertToDbl(pow));
	}
	
	public static void print(Object obj){
		System.out.println(BuildInPrint.print(obj));
	}
}
