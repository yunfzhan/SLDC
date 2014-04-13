package org.sldc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CSQLUtils {
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
}
