package org.sldc.assist.multitypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.CSQLUtils;
import org.sldc.exception.InvalidType;

public class ArrayFetchAssist {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object get(Object arr, Object idx) {
		try{
			if(arr instanceof Map<?, ?>){
				return getMapItem((Map<String, Object>) arr,idx);
			}else if(CSQLUtils.isCollection(arr)) {
				return getListItem((Collection) arr,idx);
			}else if(CSQLUtils.isArray(arr)) {
				return getArrayItem((Object[]) arr,idx);
			}else if(CSQLUtils.isString(arr)) {
				return getStringItem((String) arr, idx);
			}else if(arr instanceof IChunkDataIntf) {
				return ((IChunkDataIntf)arr).getItem(idx);
			}
			return arr;
		}catch(Exception e) {
			return e;
		}
	}
	
	private static Object getMapItem(Map<String, Object> arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Long index = checkIntegerIndex(idx);
		if(index!=null) {
			for(String key : arr.keySet())
			{
				if(index--==0)
					return arr.get(key);
			}
			return new ArrayIndexOutOfBoundsException(CSQLUtils.ToInt(idx).intValue());
		}else if(CSQLUtils.isString(idx)) {
			idx = CSQLUtils.removeStringBounds((String) idx);
			return arr.get(idx);
		}
		else
			throw new InvalidType(new Throwable());
	}
	
	private static Object getListItem(@SuppressWarnings("rawtypes") Collection arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Long index = checkIntegerIndex(idx);
		if(index==null) throw new InvalidType(new Throwable());
		
		for(Object item : arr)
		{
			if(index--==0)
				return item;
		}
		return new ArrayIndexOutOfBoundsException(CSQLUtils.ToInt(idx).intValue());
	}
	
	private static Object getArrayItem(Object[] arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Long index = checkIntegerIndex(idx);
		if(index==null) throw new InvalidType(new Throwable());
		return arr[index.intValue()];
	}
	
	private static Object getStringItem(String arr, Object idx) throws ArrayIndexOutOfBoundsException, InvalidType {
		Long index = checkIntegerIndex(idx);
		if(index!=null)
			return arr.charAt(index.intValue());
		else if(CSQLUtils.isString(idx))
		{
			String prefix = CSQLUtils.removeStringBounds((String)idx);
			// re='...' or re="..." or re=...<WS>
			String re = prefix+"=(\".*?\"|\'.*?\'|.*?\\s)";
			Pattern p = Pattern.compile(re);
			Matcher m = p.matcher(arr);
			ArrayList<String> res = new ArrayList<String>();
			while(m.find()){
				String value = m.group().substring(prefix.length()+1);
				res.add(value);
			}
			return res;
		}
		else
			throw new InvalidType(new Throwable());
	}
	
	private static Long checkIntegerIndex(Object idx){
		try{
			if(CSQLUtils.isInt(idx))
				return CSQLUtils.ToInt(idx);
		}catch (InvalidType e) {}
		return null;
	}
}
