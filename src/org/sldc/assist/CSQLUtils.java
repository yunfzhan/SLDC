package org.sldc.assist;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.sldc.assist.multitypes.ArrayFetchAssist;
import org.sldc.exception.InvalidType;

public class CSQLUtils {
	
	private static final int MAX_CHAR = 256;
	
	public static String MD5Code(String str) {
        String result = null;
        try {
            result = new String(str);
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            return new String(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }
	
	public static boolean isBool(Object o) {
		return (o instanceof Boolean||o.getClass().equals(boolean.class));
	}
	
	public static boolean isArray(Object o) {
		return o.getClass().isArray();
	}
	
	public static boolean isNumeric(Object obj)
	{
		if((obj instanceof Double)||(obj instanceof Float)||(obj instanceof Integer)||(obj instanceof Long))
			return true;
		else if(isString(obj))
		{
			String var = (String)obj;
			Pattern pattern = Pattern.compile("^[-+]?[\\d]*([.][\\d]+)?$");    
		    return pattern.matcher(var).matches(); 
		}
		else
			return false;
	}
	
	public static boolean isInt(Object o)
	{
		if(o instanceof Integer||o.getClass().equals(int.class))
			return true;
		else if(o instanceof Long||o.getClass().equals(long.class))
			return true;
		else if(isString(o))
		{
			try{
				Integer.parseInt((String) o);
				return true;
			}catch(NumberFormatException e)
			{return false;}
		}
		else
			return false;
	}
	
	public static boolean isString(Object o){
		return o instanceof String;
	}
	
	public static Long convertToInt(Object o) throws InvalidType {
		if(isInt(o))
			return Long.valueOf(o.toString());
		else if(isString(o))
			return Long.valueOf((String)o);
		else
			throw new InvalidType(new Throwable());
	}
	
	public static Double convertToDbl(Object obj) throws InvalidType
	{
		if(isInt(obj))
			return (Long)obj*1.0;
		else if(isNumeric(obj))
			return new Double((Double)obj);
		else if(isString(obj))
			return Double.valueOf((String)obj);
		else
			throw new InvalidType(new Throwable());
	}
	
	public static String removeStringBounds(String input) {
		String ret = input;
		if(ret.startsWith("\"")||ret.startsWith("'"))
			ret = ret.substring(1);
		if(ret.endsWith("\"")||ret.endsWith("'"))
			ret = ret.substring(0,ret.length()-1);
		return ret;
	}
	
	public static Object fetchArray(Object arr, Object idx) {
		return ArrayFetchAssist.get(arr, idx);
	}
	 
	private static int[] PreBmBc(String pattern)
	{	 
		byte[] patternBytes = pattern.getBytes();
		int m = pattern.length();
		int[] bmBc = new int[MAX_CHAR];
	    for(int i = 0; i < MAX_CHAR; i++)
	        bmBc[i] = m;
	 
	    for(int i = 0; i < m - 1; i++)
	        bmBc[patternBytes[i]&0xFF] = m - 1 - i;
	    return bmBc;
	 }
	
	private static int[] suffix(String pattern) {
		byte[] patternBytes = pattern.getBytes();
		int[] suff = new int[MAX_CHAR];
		int m = pattern.length();
		
		int f = 0;   
		suff[m - 1] = m;
		int g = m - 1;
		for (int i = m - 2; i >= 0; --i) {
			if (i > g && suff[i + m - 1 - f] < i - g)
				suff[i] = suff[i + m - 1 - f];
			else {
				if (i < g)
					g = i;
				f = i;
				while (g >= 0 && patternBytes[g] == patternBytes[g + m - 1 - f])
					--g;
				suff[i] = f - g;
			}
		}
		return suff;
	}
	
	private static int[] PreBmGs(String pattern)
	{
	    int[] bmGs = new int[MAX_CHAR];
	    
	    int m = pattern.length();
	    int[] suff = suffix(pattern);
	    for(int i = 0; i < m; i++)
	        bmGs[i] = m;
	 
	    // Case2
	    int j = 0;
	    for(int i = m - 1; i >= 0; i--)
	    {
	        if(suff[i] == i + 1)
	        {
	            for(; j < m - 1 - i; j++)
	            {
	                if(bmGs[j] == m)
	                    bmGs[j] = m - 1 - i;
	            }
	        }
	    }
	 
	    // Case1
	    for(int i = 0; i <= m - 2; i++)
	        bmGs[m - 1 - suff[i]] = m - 1 - i;
	    return bmGs;
	}
	
	public static ArrayList<Integer> BoyerMoore(String pattern, String text)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
	    // Preprocessing
	    int[] bmBc = PreBmBc(pattern);
	    int[] bmGs = PreBmGs(pattern);
	    int m = pattern.length();
	    int n = text.length();
	    try{
		    // Searching
		    int j = 0;
		    while(j <= n - m)
		    {
		    	int i;
		        for(i = m - 1; i >= 0 && pattern.charAt(i) == text.charAt(i + j); i--);
		        if(i < 0)
		        {
		            //printf("Find it, the position is %d\n", j);
		        	res.add(j);
		            j += bmGs[0];
		            //return;
		        }
		        else
		        {
		        	byte b = (byte)text.charAt(i+j);
		        	//System.out.println("char="+text.charAt(i+j)+",byte="+(b&0xFF));
		            j += Math.max(bmBc[b&0xFF] - m + 1 + i, bmGs[i]);
		        }
		    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    //printf("No find.\n");
	    return res;
	}
}
