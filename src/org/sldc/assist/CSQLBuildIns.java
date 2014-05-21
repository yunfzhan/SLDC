package org.sldc.assist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.sldc.CSQLExtensions;
import org.sldc.ISaveInterface;
import org.sldc.assist.multitypes.BuildInLength;
import org.sldc.assist.multitypes.BuildInPrint;
import org.sldc.assist.multitypes.BuildInSearchFunction;
import org.sldc.assist.multitypes.BuildInStrConv;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;
import org.sldc.exception.SLDCException;

/**
 * @version 0.3
 * @author Yunfei
 * Buildin functions executed module
 */

public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	private static Map<String, Map<Integer, Method>> _internalFuncs = new HashMap<String, Map<Integer, Method>>();
	private static Scope currentScope = null;
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("$isvoid", "isNull");
		functions.put("$print", "print");
		functions.put("$pow", "Pow");
		functions.put("$count", "getLength");
		functions.put("$str", "convertToString");
		functions.put("$cut", "splitString");
		functions.put("$find", "findFirst");
		functions.put("$save", "save");
		Method[] methods = CSQLBuildIns.class.getDeclaredMethods();
		for(Method method : methods)
		{
			String name = method.getName();
			int modifier = method.getModifiers();
			if(name.equals("invoke")||!Modifier.isPublic(modifier)) continue;
			
			int numOfParams = method.getParameterTypes().length;
			Map<Integer, Method> item = null;
			if(_internalFuncs.containsKey(name)){
				item = _internalFuncs.get(name);
			}else{
				item = new HashMap<Integer, Method>();
				_internalFuncs.put(name, item);
			}
			
			item.put(numOfParams, method);
		}
	}
	/**
	 * 
	 * @param funcName: function name to be called
	 * @param params: parameters passed to the function
	 * @param scope: containing variables and functions defined. But it's not safe in multi-thread environment since it is stored in a static variable.
	 * @return execution result
	 */
	public static Object invoke(String funcName, Object[] params, Scope scope)
	{
		Object result = new NotBuildInFunction(funcName, new Throwable());
		if(functions.containsKey(funcName))
		{
			Map<Integer, Method> map = _internalFuncs.get(functions.get(funcName));
			
			try {
				Method method = map.get(params.length);
				if(method!=null)
				{
					currentScope = scope;
					result = method.invoke(CSQLUtils.class, params);
				}
			} catch (Exception e) {
				result = e;
			}
		}else{ // support 3rd party extended functions
			try {
				result = CSQLExtensions.execExtFunction(funcName, params);
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
				result = e;
			} catch (InvocationTargetException e) {
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
	
	public static int findString(String o, String sub) {
		sub = CSQLUtils.removeStringBounds(sub);
		return o.indexOf(sub);
	}
	
	public static Object _InCore(Object contents, String plain){
		return _InCore(contents, plain, "p");
	}
	
	public static Object _InCore(Object contents, String srchable, String indicator) {
		return _InCore(contents,srchable,indicator,null);
	}
	
	public static Object _InCore(Object contents, String srchable, String indicator, String condition/*Up to now, it's only valid while searchByTag*/) {
		if(srchable==null||srchable.equals("")) return false;
		srchable = CSQLUtils.removeStringBounds(srchable);
		if(srchable.equals("")) return false; // string to search can't be empty after removing quotes.
		indicator = CSQLUtils.removeStringBounds(indicator);
		
		return BuildInSearchFunction.search(contents, srchable, indicator, condition, currentScope);
	}

	public static Double Pow(Object base, Object pow) throws InvalidType
	{
		return Math.pow(CSQLUtils.ToDbl(base), CSQLUtils.ToDbl(pow));
	}
	
	public static void print(Object obj){
		System.out.println(BuildInPrint.print(obj));
	}
	
	public static void print(Object obj, Object flag){
		System.out.print(BuildInPrint.print(obj));
	}
	
	public static void save(Object o) {
		ISaveInterface ext = CSQLExtensions.createExtSaveClass();
		ext.save(o);
	}
}
