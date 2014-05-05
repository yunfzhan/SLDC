package org.sldc.assist.multitypes;

import java.util.Collection;
import java.util.Map;

import org.sldc.assist.CSQLChunkDataIntf;
import org.sldc.assist.CSQLUtils;
import org.sldc.exception.InvalidType;

public class ArrayFetchAssist {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object get(Object arr, Object idx) {
		try{
			if(arr instanceof Map<?, ?>){
				return getMapItem((Map<String, Object>) arr,idx);
			}else if(arr instanceof Collection) {
				return getListItem((Collection) arr,idx);
			}else if(CSQLUtils.isArray(arr)) {
				return getArrayItem((Object[]) arr,idx);
			}else if(arr instanceof String) {
				return getStringItem((String) arr, idx);
			}else if(arr instanceof CSQLChunkDataIntf) {
				return ((CSQLChunkDataIntf)arr).getItem(idx);
			}
			return arr;
		}catch(Exception e) {
			return e;
		}
	}
	
	private static Object getMapItem(Map<String, Object> arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Integer index = checkIntegerIndex(idx);
		if(index!=null) {
			for(String key : arr.keySet())
			{
				if(index--==0)
					return arr.get(key);
			}
			return new ArrayIndexOutOfBoundsException((Integer)idx);
		}else if(idx instanceof String) {
			idx = CSQLUtils.removeStringBounds((String) idx);
			return arr.get(idx);
		}
		else
			throw new InvalidType(new Throwable());
	}
	
	private static Object getListItem(@SuppressWarnings("rawtypes") Collection arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Integer index = checkIntegerIndex(idx);
		if(index==null) throw new InvalidType(new Throwable());
		
		for(Object item : arr)
		{
			if(index--==0)
				return item;
		}
		return new ArrayIndexOutOfBoundsException((Integer)idx);
	}
	
	private static Object getArrayItem(Object[] arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Integer index = checkIntegerIndex(idx);
		if(index==null) throw new InvalidType(new Throwable());
		return arr[(Integer) index];
	}
	
	private static Character getStringItem(String arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Integer index = checkIntegerIndex(idx);
		if(index==null) throw new InvalidType(new Throwable());
		return arr.charAt((Integer) index);
	}
	
	private static Integer checkIntegerIndex(Object idx){
		try{
			if(idx instanceof String) {
				return Integer.valueOf((String)idx);
			}
			Class<? extends Object> c = idx.getClass();
			if(c==Integer.class||c==int.class)
				return (Integer)idx;
		}catch(NumberFormatException ex){
		}
		return null;
	}
}
