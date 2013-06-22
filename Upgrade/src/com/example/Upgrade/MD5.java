package com.example.Upgrade;

import java.security.MessageDigest;
import java.io.FileInputStream;
import java.io.InputStream;

public class MD5 {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f' };

	public String toHexString(byte[] b) {  
		 StringBuilder sb = new StringBuilder(b.length * 2); 
		 
		 for(int i = 0; i < b.length; i++) {  
		     sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
		     sb.append(HEX_DIGITS[b[i] & 0x0f]);  
		 }  
		 return sb.toString();  
	}

	public String md5sum(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while((numRead=fis.read(buffer)) > 0) {
				md5.update(buffer,0,numRead);
			}
			fis.close();
			return toHexString(md5.digest());	
		}
		catch(Exception e) {
			return null;
		}
	}
}

