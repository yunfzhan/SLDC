package org.sldc.assist.multitypes;

import org.sldc.assist.CSQLUtils;
import org.sldc.exception.InvalidType;

public class EqualCompareAssist {
	public static Boolean isEqual(Object o1, Object o2, boolean isPlain1, boolean isPlain2) {
		if(CSQLUtils.isString(o2))
		{
			Object o = o1;
			o1 = o2;
			o2 = o;
			boolean b = isPlain1;
			isPlain1 = isPlain2;
			isPlain2 = b;
		}
		
		if(CSQLUtils.isString(o1))
		{
			String e1 = isPlain1?CSQLUtils.removeStringBounds((String) o1):(String)o1;
			if(CSQLUtils.isString(o2))
			{
				String e2 = isPlain2?CSQLUtils.removeStringBounds((String) o2):(String)o2;
				return e1.equals(e2);
			}
			else
				return e1.equals(o2);
		}
		else{
			if(CSQLUtils.isInt(o1)&&CSQLUtils.isInt(o2)){
				try{
					Long v1 = CSQLUtils.ToInt(o1);
					Long v2 = CSQLUtils.ToInt(o2);
					return v1==v2;
				}catch(InvalidType e){
					return false;
				}
			}else if(CSQLUtils.isNumeric(o1)&&CSQLUtils.isNumeric(o2)){
				try{
					Double d1 = CSQLUtils.ToDbl(o1);
					Double d2 = CSQLUtils.ToDbl(o2);
					return d1==d2;
				}catch(InvalidType e){
					return false;
				}
			}else
				return o1.equals(o2);
		}
	}
}
