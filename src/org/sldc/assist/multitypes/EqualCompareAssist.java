package org.sldc.assist.multitypes;

import org.sldc.assist.CSQLUtils;
import org.sldc.exception.InvalidType;

public class EqualCompareAssist {
	public static Boolean isEqual(Object o1, Object o2) {
		if(CSQLUtils.isString(o2))
		{
			Object o = o1;
			o1 = o2;
			o2 = o;
		}
		
		if(CSQLUtils.isString(o1))
		{
			String e1 = CSQLUtils.removeStringBounds((String) o1);
			if(CSQLUtils.isString(o2))
			{
				String e2 = CSQLUtils.removeStringBounds((String) o2);
				return e1.equals(e2);
			}
			else
				return e1.equals(o2);
		}
		else{
			if(CSQLUtils.isInt(o1)&&CSQLUtils.isInt(o2)){
				try{
					Long v1 = CSQLUtils.convertToInt(o1);
					Long v2 = CSQLUtils.convertToInt(o2);
					return v1==v2;
				}catch(InvalidType e){
					return false;
				}
			}else if(CSQLUtils.isNumeric(o1)&&CSQLUtils.isNumeric(o2)){
				try{
					Double d1 = CSQLUtils.convertToDbl(o1);
					Double d2 = CSQLUtils.convertToDbl(o2);
					return d1==d2;
				}catch(InvalidType e){
					return false;
				}
			}else
				return o1.equals(o2);
		}
	}
}
