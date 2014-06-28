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
				if(end>=0) {
					return s.substring(beg.intValue(),(int) (beg+end));
				}
				else {
					return s.substring(beg.intValue(), s.length()+1+end.intValue());
				}
			}else if(CSQLUtils.isArray(o)){
				Object[] src = (Object[])o;
				int len = end.intValue();
				if(end<0) {
					len = src.length-beg.intValue()+end.intValue()+1;
				}
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
