package org.sldc.assist;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.csql.syntax.Scope;
import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;
import org.sldc.exception.SLDCException;
import org.sldc.protocols.CSQLChunkDataImpl;
import org.sldc.protocols.HTMLAnalyzer;

public class CSQLBuildIns {
	private static Map<String, String> functions = new HashMap<String, String>();
	
	static{
		functions.put("$", "_InCore");	// '$'
		functions.put("isvoid", "isNull");
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
		return obj==null||obj.equals(Scope.UnDefined)||(obj instanceof SLDCException);
	}
	
	public static Object _InCore(Object contents, String plain){
		return _InCore(contents, plain, "p");
	}
	
	public static Object _InCore(Object contents, String srchable, String indicator) {
		if(srchable==null||srchable.equals("")) return false;
		srchable = CSQLUtils.removeStringBounds(srchable);
		indicator = CSQLUtils.removeStringBounds(indicator);
		
		if(contents instanceof String)
			return coreByType((String)contents, srchable, indicator);
		else if(contents instanceof CSQLChunkDataImpl)
			return coreByType((CSQLChunkDataImpl)contents, srchable, indicator);
		return false;
	}
	
	private static Object coreByType(CSQLChunkDataImpl o, String srchable, String indicator) {
		if(indicator.equalsIgnoreCase("p")||indicator.equalsIgnoreCase("r")){
			Object r = o.search(srchable);
			if(CSQLUtils.isBool(r)&&(Boolean)r==false) return false;
			
			return (indicator.equalsIgnoreCase("p"))?true:(String[])r;
		}else if(indicator.equalsIgnoreCase("t")){
			return o.searchByTag(srchable);
		}
		return false;
	}
	
	private static Object coreByType(String contents, String srchable, String indicator) {
		if(indicator.equalsIgnoreCase("p")){
			ArrayList<Integer> res = CSQLUtils.BoyerMoore(srchable, (String) contents);
			return res.size()!=0;
		}else if(indicator.equalsIgnoreCase("r")){
			Pattern p = Pattern.compile(srchable);
			Matcher m = p.matcher(contents);
			if(!m.find()) return false;
			
			String[] res = new String[m.groupCount()];
			for(int i=0;i<res.length;i++)
			{
				res[i]=m.group(i);
			}
			return res;
		}else if(indicator.equalsIgnoreCase("t")){
			try {
				String[] res = HTMLAnalyzer.startAnalyze((String) contents, srchable);
				return res;
			} catch (IOException e) {
				return e;
			}
		}
		return false;
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
		System.out.println(printObject(obj));
	}
}
