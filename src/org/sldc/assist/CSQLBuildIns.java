package org.sldc.assist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.CSQLExtensions;
import org.sldc.ISaveInterface;
import org.sldc.SaveObject;
import org.sldc.assist.multitypes.BuildInLength;
import org.sldc.assist.multitypes.BuildInPrint;
import org.sldc.assist.multitypes.BuildInSearchFunction;
import org.sldc.assist.multitypes.BuildInStrConv;
import org.sldc.assist.multitypes.HttpHeaderAssist;
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
		//functions.put("$", "_InCore");	// '$' major core function
		// output functions
		functions.put("$echo", "print");
		functions.put("$print", "println");
		// assistant
		functions.put("$isvoid", "isInvalid");
		functions.put("$pow", "Pow");
		functions.put("$count", "getLength");
		// conversions
		functions.put("$bool", "toBoolean");
		functions.put("$str", "convertToString");
		// Save function that can be extended by 3rd code
		functions.put("$save", "save");
		// String utilities
		functions.put("$cut", "splitString");
		functions.put("$match", "matchRegex");
		// For CSQLHttpChunkImpl only
		functions.put("$keys", "getKeys");
		functions.put("$header", "getHeaderItem");
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
	
	public static Object invokeObject(Object o, String method, ArrayList<String> params, Scope scope)
	{
		Object result = new NoSuchMethodException();
		
		currentScope = scope;
		if(isInvalid(params)) {
			result = _InCore(o, null, method);
		}else{
			if(method.endsWith("If")&&params.size()==2){
				result = _InCore(o, params.get(0), method.substring(0, method.length()-2), params.get(1));
			}else{
				result = _InCore(o, params.get(0), method);
			}
		}
		
		return result; 
	}
	
	public static boolean isInvalid(Object obj) {
		return obj==null||obj.equals(Scope.UnDefined)||(obj instanceof SLDCException);
	}
	
	public static long getLength(Object o) {
		return BuildInLength.length(o);
	}
	
	public static boolean toBoolean(Object o) {
		if(CSQLUtils.isInt(o))
			return !o.toString().equals("0");
		else
			return (o==null||o.equals(Scope.UnDefined))?false:Boolean.parseBoolean(CSQLUtils.removeStringBounds(o.toString()));
	}
	
	public static String convertToString(Object o) {
		return BuildInStrConv.toString(o);
	}
	
	public static String[] splitString(String o, String deli) {
		deli = CSQLUtils.removeStringBounds(deli);
		return o.split(deli);
	}
	
	public static boolean matchRegex(Object o, String regex) {
		if(!CSQLUtils.isString(o)) return false;
		String src = o.toString();
		regex = CSQLUtils.removeStringBounds(regex);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(src);
		return m.find();
	}
	
//	private static Object _InCore(Object contents, String plain){
//		return _InCore(contents, plain, "p");
//	}
	
	private static Object _InCore(Object contents, String srchable, String indicator) {
		return _InCore(contents,srchable,indicator,null);
	}
	
	private static Object _InCore(Object contents, String srchable, String indicator, String condition/*Up to now, it's only valid while searchByTag*/) {
		//if(srchable==null||srchable.equals("")) return false;
		if(srchable!=null&&!srchable.equals("")) {
			srchable = CSQLUtils.removeStringBounds(srchable);
			if(srchable.equals("")) return false; // string to search can't be empty after removing quotes.
		}
		indicator = CSQLUtils.removeStringBounds(indicator);
		
		return BuildInSearchFunction.search(contents, srchable, indicator, condition, currentScope);
	}

	public static Double Pow(Object base, Object pow) throws InvalidType
	{
		return Math.pow(CSQLUtils.ToDbl(base), CSQLUtils.ToDbl(pow));
	}
	
	public static void println(Object obj){
		System.out.println(BuildInPrint.print(obj));
	}
	
	public static void print(Object obj){
		System.out.print(BuildInPrint.print(obj));
	}
	
	public static void save(Object o) {
		save(o,null);
	}
	
	public static void save(Object o, Object attach) {
		ISaveInterface ext = CSQLExtensions.createExtSaveClass();
		ext.save(new SaveObject(o, attach));
	}
	
	// Designed for CSQLHttpChunkImpl since it's not a common utility that I don't like.
	// TODO change
	public static Object[] getKeys(Object o) {
		return HttpHeaderAssist.getKeys(o);
	}
	
	public static Object getHeaderItem(Object o, Object key) {
		return HttpHeaderAssist.getHeaderItem(o, key);
	}
}
