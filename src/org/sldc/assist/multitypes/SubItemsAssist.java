package org.sldc.assist.multitypes;

import java.util.Collection;

import org.sldc.assist.CSQLUtils;

public class SubItemsAssist {
	public static boolean isSupportType(Object o) {
		return CSQLUtils.isArray(o)||CSQLUtils.isString(o)||CSQLUtils.isCollection(o);
	}
	
	public static boolean isParamLegal(Object[] params) {
		return (params.length==1)?CSQLUtils.isInt(params[0]):(CSQLUtils.isInt(params[0])&&CSQLUtils.isInt(params[1]));
	}
	
	public static Object subItems(Object o, Object[] params) {
		try {
			Long beg = CSQLUtils.ToInt(params[0]);
			Long end = (params.length==2)?CSQLUtils.ToInt(params[1]):-1;
			if(CSQLUtils.isString(o)){
				String s = CSQLUtils.removeStringBounds((String) o);
				return end==-1?s.substring(beg.intValue()):s.substring(beg.intValue(),(int) (beg+end));
			}else if(CSQLUtils.isArray(o)){
				Object[] src = (Object[])o;
				int len = (int) (end==-1?(src.length-beg):end);
				Object[] dst = new Object[len];
				System.arraycopy(src, beg.intValue(), dst, 0, len);
				return dst;
			}else if(CSQLUtils.isCollection(o)){
				@SuppressWarnings("unchecked")
				Object[] c = ((Collection<Object>)o).toArray();
				return subItems(c, params);
			}
		} catch (Exception e) {}
		
		return false;
	}
}
