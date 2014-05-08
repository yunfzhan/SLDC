package org.sldc.assist;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

@SuppressWarnings("unchecked")
public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	private static Map<String, Object> _internalFuncs = new HashMap<String, Object>();
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("isvoid", "isNull");
		functions.put("print", "print");
		functions.put("println", "println");
		functions.put("pow", "Pow");
		functions.put("len", "getLength");
		functions.put("str", "convertToString");
		functions.put("cut", "splitString");
		functions.put("suck", "extractString");
		functions.put("find", "findFirst");
		Method[] methods = CSQLBuildIns.class.getDeclaredMethods();
		for(int i=0;i<methods.length;i++)
		{
			String name = methods[i].getName();
			int modifier = methods[i].getModifiers();
			if(name.equals("invoke")||!Modifier.isPublic(modifier)) continue;
			if(_internalFuncs.containsKey(name))
			{
				Object o = _internalFuncs.get(name);
				Map<Class<?>[], Method> duplicates = null;
				if(o instanceof Method)
				{
					duplicates = new HashMap<Class<?>[], Method>();
					Method m = (Method)o;
					duplicates.put(m.getParameterTypes(), m);
				}
				else
					duplicates = (Map<Class<?>[], Method>) o; // in case of duplicated names.
				duplicates.put(methods[i].getParameterTypes(), methods[i]);
				_internalFuncs.put(name, duplicates);
				continue;
			}
			_internalFuncs.put(name, methods[i]);
		}
	}
	/**
	 * 
	 * @param funcName: function name to be called
	 * @param params: parameters passed to the function
	 * @return execution result
	 */
	public static Object invoke(String funcName, Object[] params)
	{
		Object result = new NotBuildInFunction(funcName, new Throwable());
		if(functions.containsKey(funcName))
		{
			Object o = _internalFuncs.get(functions.get(funcName));
			
			try {
				if(o instanceof Method)
				{
					Method method = (Method)o;
					result = method.invoke(CSQLUtils.class, params);
				}else{
					Map<Class<?>[], Method> m = (Map<Class<?>[], Method>)o;
					for(Class<?>[] ptypes : m.keySet())
						if(ptypes.length==params.length)
						{
							result = m.get(ptypes).invoke(CSQLUtils.class, params);
							break;
						}
				}				
			} catch (Exception e) {
				result = e;
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
	
	public static String[] splitString(String o, String deli) {
		deli = CSQLUtils.removeStringBounds(deli);
		return o.split(deli);
	}
	
	public static String extractString(String o, int beg, int end) {
		return o.substring(beg, end);
	}
	
	public static int findString(String o, String sub) {
		sub = CSQLUtils.removeStringBounds(sub);
		return o.indexOf(sub);
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
		System.out.print(BuildInPrint.print(obj));
	}
	
	public static void println(Object obj){
		System.out.println(BuildInPrint.print(obj));
	}
}
