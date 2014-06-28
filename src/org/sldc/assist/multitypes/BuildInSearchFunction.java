package org.sldc.assist.multitypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sldc.assist.CSQLBuildIns;
import org.sldc.assist.CSQLUtils;
import org.sldc.assist.HTMLAnalyzer;
import org.sldc.assist.ItemIterator;
import org.sldc.core.CSQLExecutable;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.protocols.CSQLChunkDataImpl;

public class BuildInSearchFunction {
	
	/**
	 * Regarding performance, I separate search functions into one without condition and the other with it.
	 * @param o - entity to be performed search on
	 * @param srchable - string to search for
	 * @param indicator - a flag to determine which search function will be performed
	 * @param cond - additional string condition that is run while searching
	 * @param scope - Scope object that relates to current scope
	 * @return
	 */
	public static Object search(Object o, String srchable, String indicator, String cond, Scope scope) {
		if(indicator.equalsIgnoreCase("p"))
			return searchPlain(o, srchable);
		else if(indicator.equalsIgnoreCase("r"))
			return searchRE(o, srchable);
		else if(indicator.equalsIgnoreCase("t"))
			return cond==null?searchTag(o, srchable):searchTag(o, srchable, cond, scope);
		return false;
	}
	
	private static Scope initEval(Object param, Scope parentScope) {
		Scope scope = parentScope.push();
		try {
			scope.addVariable("$line", param);
		} catch (DefConflictException e) {
			e.printStackTrace();
		}
		return scope;	
	}
	
	private static boolean boolEval(String cond, Object param, Scope scope) {
		try {
			cSQLParser parser = CSQLExecutable.getWalkTree(cond);
			ParseTree node = parser.expr();
			
			CSQLExecutable runner = CSQLExecutable.getSingleInstance(initEval(param, scope));
			return CSQLBuildIns.toBoolean(runner.visit(node));
		} catch (Exception e) {
			return false;
		}		
	}
	
	private static Object searchPlain(Object o, String srchable) {
		if(CSQLUtils.isString(o)){
			ArrayList<Integer> res = CSQLUtils.BoyerMoore(srchable, (String) o);
			return res.size();
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
			
			return r;
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
		}else if(CSQLUtils.isCollection(o)||CSQLUtils.isArray(o)){
			ArrayList<Object> res = new ArrayList<Object>();
			ItemIterator objs = new ItemIterator(o);
			for(Object v : objs)
				res.add(searchTag(v, srchable));
			return res;
		}
		return false;
	}
	
	private static boolean isEntityEmpty(Object o) {
		if(CSQLUtils.isString(o))
			return o==null||((String)o).equals("");
		else if(o instanceof CSQLChunkDataImpl)
			return o==null||((CSQLChunkDataImpl)o).size()==0;
		else if(CSQLUtils.isArray(o))
			return ((Object[])o).length==0;
		else if(CSQLUtils.isCollection(o))
			return ((Collection<?>)o).size()==0;
		return false;
	}
	
	private static Object searchTag(Object o, String srchable, String cond, Scope scope) {
		cond = CSQLUtils.removeStringBounds(cond);
		if(CSQLUtils.isString(o)){
			try {
				ArrayList<String> res = HTMLAnalyzer.startAnalyze((String) o, srchable);
				for(int i=res.size()-1;i>=0;i--)
				{
					if(!boolEval(cond, res.get(i), scope))
						res.remove(i);
				}
				return res;
			} catch (IOException e) {
				return e;
			}
		}else if(o instanceof CSQLChunkDataImpl) {
			Object r = ((CSQLChunkDataImpl)o).searchByTag(srchable);
			if(r instanceof ArrayList<?>)
			{
				ArrayList<?> res = (ArrayList<?>)r;
				for(int i=res.size()-1;i>=0;i--)
				{
					if(!boolEval(cond, res.get(i), scope))
						res.remove(i);
				}
				r = res;
			}
			return r;
		}else if(CSQLUtils.isCollection(o)||CSQLUtils.isArray(o)){
			ArrayList<Object> res = new ArrayList<Object>();
			ItemIterator objs = new ItemIterator(o);
			for(Object v : objs)
			{
				Object obj = searchTag(v, srchable, cond, scope);
				if(!isEntityEmpty(obj))
					res.add(obj);
			}
			return res;
		}
		return false;
	}
}
