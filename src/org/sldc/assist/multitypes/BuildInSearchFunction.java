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
		if(o instanceof String)
			return searchByType((String)o, srchable, indicator);
		else if(o instanceof CSQLChunkDataImpl)
			return searchByType((CSQLChunkDataImpl)o, srchable, indicator);
		return false;
	}
	
	/**
	 * 
	 * @param o - internal data from select expression
	 * @param srchable
	 * @param indicator - p(plain), r(regular expression), t(tag, only valid in html-like file)
	 * @return
	 */
	private static Object searchByType(CSQLChunkDataImpl o, String srchable, String indicator) {
		if(indicator.equalsIgnoreCase("p")||indicator.equalsIgnoreCase("r")){
			Object r = o.search(srchable);
			if(CSQLUtils.isBool(r)&&(Boolean)r==false) return false;
			
			return (indicator.equalsIgnoreCase("p"))?true:(String[])r;
		}else if(indicator.equalsIgnoreCase("t")){
			return o.searchByTag(srchable);
		}
		return false;
	}
	
	private static Object searchByType(String contents, String srchable, String indicator) {
		if(indicator.equalsIgnoreCase("p")){
			ArrayList<Integer> res = CSQLUtils.BoyerMoore(srchable, (String) contents);
			return res.size()!=0;
		}else if(indicator.equalsIgnoreCase("r")){
			Pattern p = Pattern.compile(srchable);
			Matcher m = p.matcher(contents);
			
			ArrayList<String> rs = new ArrayList<String>();
			while(m.find()){
				rs.add(m.group());
			}
			return rs.toArray();
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
}
