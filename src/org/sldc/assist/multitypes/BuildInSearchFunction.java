package org.sldc.assist.multitypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.assist.CSQLUtils;
import org.sldc.assist.HTMLAnalyzer;
import org.sldc.protocols.CSQLChunkDataImpl;

public class BuildInSearchFunction {
	public static Object search(Object o, String srchable, String indicator) {
		if(indicator.equalsIgnoreCase("p"))
			return searchPlain(o, srchable);
		else if(indicator.equalsIgnoreCase("r"))
			return searchRE(o, srchable);
		else if(indicator.equalsIgnoreCase("t"))
			return searchTag(o, srchable);
		return false;
	}
	
	private static Object searchPlain(Object o, String srchable) {
		if(CSQLUtils.isString(o)){
			ArrayList<Integer> res = CSQLUtils.BoyerMoore(srchable, (String) o);
			return res.size()!=0;
		}else if(o instanceof CSQLChunkDataImpl){
			Object r = ((CSQLChunkDataImpl)o).search(srchable);
			if(CSQLUtils.isBool(r)&&(Boolean)r==false) return false;
			
			return true;
		}
		return false;
	}
	
	private static Object searchRE(Object o, String srchable) {
		if(CSQLUtils.isString(o)){
			Pattern p = Pattern.compile(srchable);
			Matcher m = p.matcher((String)o);
			
			ArrayList<String> rs = new ArrayList<String>();
			while(m.find()){
				rs.add(m.group());
			}
			return rs;
		}else if(o instanceof CSQLChunkDataImpl){
			Object r = ((CSQLChunkDataImpl)o).search(srchable);
			if(CSQLUtils.isBool(r)&&(Boolean)r==false) return false;
			
			return (String[])r;
		}
		return false;
	}
	
	private static Object searchTag(Object o, String srchable) {
		if(CSQLUtils.isString(o)){
			try {
				return HTMLAnalyzer.startAnalyze((String) o, srchable);
			} catch (IOException e) {
				return e;
			}
		}else if(o instanceof CSQLChunkDataImpl) {
			return ((CSQLChunkDataImpl)o).searchByTag(srchable);
		}
		return false;
	}
}
