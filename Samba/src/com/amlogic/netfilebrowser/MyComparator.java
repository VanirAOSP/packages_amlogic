package com.amlogic.netfilebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.math.BigDecimal;

public class MyComparator implements Comparator<File> {
	public static final int NO_SORT = 0;
	public static final int NAME_ASCEND = 1;
	public static final int NAME_DESCEND = 2;
	public static final int SIZE_ASCEND = 3;
	public static final int SIZE_DESCEND = 4;
	public static final int TYPE_ASCEND = 5;
	public static final int TYPE_DESCEND = 6;
	public static final int MODIFIED_ASCEND = 7;
	public static final int MODIFIED_DESCEND = 8;
	private int sort_mode = NO_SORT;
	private int r_offset = 0;
	private int l_offset = 0;
	
	public MyComparator(int mode) {
		if((mode >= NO_SORT) && (mode <= MODIFIED_DESCEND))
			sort_mode = mode;
	}
	
	public int compare(File f1, File f2) {
		int r_result = -1;
		long cmp_result = 0;
		
		if((!f1.exists()) || (!f2.exists()))
			return r_result;
		
		r_offset = 0;
		l_offset = 0;
		
		switch (sort_mode) {
			case NAME_ASCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = -1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = 1;
				else {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = 1;
					else if(cmp_result < 0)
						r_result = -1;
					else
						r_result = 0;
				}
				break;
			case NAME_DESCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = 1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = -1;
				else {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = -1;
					else if(cmp_result < 0)
						r_result = 1;
					else
						r_result = 0;
				}
				break;
			case SIZE_ASCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = -1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = 1;
				else if(f1.isDirectory() && f2.isDirectory()) {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = 1;
					else if(cmp_result < 0)
						r_result = -1;
					else
						r_result = 0;
				}
				else {
					double diff = getFileSize(f1)-getFileSize(f2);
					if(diff > 0)   
						r_result = 1;  
					else if(diff == 0)   
						r_result = 0; 
					else  
						r_result = -1;
				}
				break;
			case SIZE_DESCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = 1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = -1;
				else if(f1.isDirectory() && f2.isDirectory()) {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = -1;
					else if(cmp_result < 0)
						r_result = 1;
					else
						r_result = 0;
				}
				else {
					double diff = getFileSize(f1)-getFileSize(f2);
					if(diff > 0)   
						r_result = -1;  
					else if(diff == 0)   
						r_result = 0; 
					else  
						r_result = 1;
				}
				break;
			case TYPE_ASCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = -1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = 1;
				else if(f1.isDirectory() && f2.isDirectory()) {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = 1;
					else if(cmp_result < 0)
						r_result = -1;
					else
						r_result = 0;
				}
				else {
					String r_name=f1.getName();
					String l_name=f2.getName();
					String r_suf = r_name.substring(r_name.lastIndexOf(".")+1,r_name.length()).toLowerCase();
					String l_suf = l_name.substring(l_name.lastIndexOf(".")+1,l_name.length()).toLowerCase();
					if(r_suf.equals(l_suf)) {
						cmp_result = strCmpByUnicode(returnString(r_name, 0), returnString(l_name, 0));
						if(cmp_result > 0)
							r_result = 1;
						else if(cmp_result < 0)
							r_result = -1;
						else
							r_result = 0;
					}
					else {
						cmp_result = strCmpByUnicode(returnString(r_suf, 0), returnString(l_suf, 0));
						if(cmp_result > 0)
							r_result = 1;
						else if(cmp_result < 0)
							r_result = -1;
						else
							r_result = 0;
					}
				}
				break;
			case TYPE_DESCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = -1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = 1;
				else if(f1.isDirectory() && f2.isDirectory()) {
					cmp_result = strCmpByUnicode(returnString(f1.getName(), 0), returnString(f2.getName(), 0));
					if(cmp_result > 0)
						r_result = 1;
					else if(cmp_result < 0)
						r_result = -1;
					else
						r_result = 0;
				}
				else {
					String r_name=f1.getName();
					String l_name=f2.getName();
					String r_suf = r_name.substring(r_name.lastIndexOf(".")+1,r_name.length()).toLowerCase();
					String l_suf = l_name.substring(l_name.lastIndexOf(".")+1,l_name.length()).toLowerCase();
					if(r_suf.equals(l_suf)) {
						cmp_result = strCmpByUnicode(returnString(r_name, 0), returnString(l_name, 0));
						if(cmp_result > 0)
							r_result = -1;
						else if(cmp_result < 0)
							r_result = 1;
						else
							r_result = 0;
					}
					else {
						cmp_result = strCmpByUnicode(returnString(r_suf, 0), returnString(l_suf, 0));
						if(cmp_result > 0)
							r_result = -1;
						else if(cmp_result < 0)
							r_result = 1;
						else
							r_result = 0;
					}
				}
				break;
			case MODIFIED_ASCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = -1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = 1;
				else {
					cmp_result = f1.lastModified()-f2.lastModified(); 
					if(cmp_result > 0)   
						r_result = 1;  
					else if(cmp_result == 0)   
						r_result = 0; 
					else  
						r_result = -1;
				}
				break;
			case MODIFIED_DESCEND:
				if(f1.isDirectory() && f2.isFile())
					r_result = 1;
				else if(f1.isFile() && f2.isDirectory())
					r_result = -1;
				else {
					cmp_result = f1.lastModified()-f2.lastModified(); 
					if(cmp_result > 0)   
						r_result = -1;  
					else if(cmp_result == 0)   
						r_result = 0; 
					else  
						r_result = 1;
				}
				break;
			case NO_SORT:
			default:
				break;
		}
	
		return r_result;
	}

	public boolean equals(Object obj){   
		return true;   
	}
	
	private int strCheckDigit(String r_str, String l_str)
	{
		int offset1 = 0;
		int offset2 = 0;
		
		if((r_str == null) ||(l_str == null))
			return -1;
		if((offset1 >= r_str.length()) || (offset2 >= l_str.length()))
			return -1;
		r_offset = 0;
		l_offset = 0;
		while(Character.isDigit(r_str.charAt(offset1)) == true) {
			r_offset++;
			offset1++;
			if(offset1 >= r_str.length())
				break;
		}
		
		while(Character.isDigit(l_str.charAt(offset2)) == true) {
			l_offset++;
			offset2++;
			if(offset2 >= l_str.length())
				break;
		}

		if((r_offset == 0)||(l_offset == 0))
			return -1;
		else
			return 0;
	}

	private long strDigitCmp(String r_str, String l_str)
	{
		int str1_len = 0;
		int str2_len = 0;
		int temp_len = 0;
		int i = 0;
		int str1_offset = r_offset;
		int str2_offset = l_offset;
		long result = -1;
		
		if((r_str == null) ||(l_str == null))
			return result;
			
		str1_len = r_str.length();
		str2_len = l_str.length();
			
		if((str1_offset<1)||(str2_offset<1)||(str1_len < 1)||(str2_len < 1))
			return result;

		if(str1_len>str2_len)
			temp_len = str1_len;
		else
			temp_len = str2_len;

		StringBuffer strBuf1 = new StringBuffer(temp_len+1);
		StringBuffer strBuf2 = new StringBuffer(temp_len+1);
			
		strBuf1.insert(0, r_str);
		strBuf2.insert(0, l_str);
			
		if(str1_offset<str1_len)
			strBuf1.setLength(str1_offset);
		if(str2_offset<str2_len)
			strBuf2.setLength(str2_offset);
			
		str1_len = strBuf1.length();
		str2_len = strBuf2.length();
		if(str1_len>str2_len)
			temp_len = str1_len;
		else
			temp_len = str2_len;

		StringBuffer temp1 = new StringBuffer(temp_len+1);
		StringBuffer temp2 = new StringBuffer(temp_len+1);
			
		if((temp_len-str1_len) > 0) {
			char[] temp1_char = new char[temp_len-str1_len];
			for(i=0;i<(temp_len-str1_len);i++) {
				temp1_char[i] = '0';
			}
			temp1.insert(0, temp1_char);
		}
		if(temp1.length() > 0) {
			strBuf1.insert(0, temp1);
		}
			
		if((temp_len-str2_len) > 0) {
			char[] temp2_char = new char[temp_len-str2_len];
			for(i=0;i<(temp_len-str2_len);i++) {
				temp2_char[i] = '0';
			}
			temp2.insert(0, temp2_char);
		}
		if(temp2.length() > 0) {
			strBuf2.insert(0, temp2);
		}
			
		String str1 = new String(strBuf1);
		String str2 = new String(strBuf2);
		result = str1.compareTo(str2);
		return result;
	}
	
	private long strCmpByUnicode(String r_str, String l_str)
	{
		long result = -1; 
		int offset1 = 0;
		int offset2 = 0;
		Character char_1 = null;
		Character char_2 = null;
		
		if((r_str == null) ||(l_str == null))
			return result;
			
		if((offset1 >= r_str.length()) || (offset2 >= l_str.length()))
			return result;
		
		char_1 = new Character(Character.toLowerCase(r_str.charAt(offset1)));
		char_2 = new Character(Character.toLowerCase(l_str.charAt(offset2)));
		
		while(char_1.equals(char_2)) {
			if(strCheckDigit(returnString(r_str, offset1), returnString(l_str, offset2))==0) {
				result=strDigitCmp(returnString(r_str, offset1), returnString(l_str, offset2));
				if(result==0) {
					offset1 = offset1+r_offset;
					offset2 = offset2+l_offset;
				}
				else
					return result;
			}
			else {
				offset1++;
				offset2++;
			}
			if(offset1 >= r_str.length() || offset2 >= l_str.length()) {
				break;
			}
			char_1 = new Character(Character.toLowerCase(r_str.charAt(offset1)));
			char_2 = new Character(Character.toLowerCase(l_str.charAt(offset2)));
		}
		
		if(strCheckDigit(returnString(r_str, offset1), returnString(l_str, offset2))==0) {
			result=strDigitCmp(returnString(r_str, offset1), returnString(l_str, offset2));
			if(result==0) {
				offset1 = offset1+r_offset;
				offset2 = offset2+l_offset;
				result = strCmpByUnicode(returnString(r_str, offset1), returnString(l_str, offset2));
				return result;
			}
			else
				return result;
		}
		else {
			if(offset1 >= r_str.length() || offset2 >= l_str.length())
				return result;

			char_1 = new Character(Character.toLowerCase(r_str.charAt(offset1)));
			char_2 = new Character(Character.toLowerCase(l_str.charAt(offset2)));
			result = char_1.compareTo(char_2);
			return result;
		}
	}

	private String returnString(String str, int offset) {
		if((offset < 0) || (offset >= str.length()))
			return null;
		int len = str.length()-offset;
		char[]  temp_char = new char[len];
		for(int i=0;i<len;i++)
			temp_char[i] = str.charAt(offset+i);
		String temp = new String(temp_char);
		return temp;
	}
	
	private double getFileSize(File file) {
		double size=0;
		if (file.exists()) {
			try {
				FileInputStream fis = null;
				fis = new FileInputStream(file);
				size= fis.available();
			}
			catch(Exception e) {
				return 0;
			}
		} 
		else {
			return 0;
		}
		size = size/1024.00;
		size=Double.parseDouble(size+"");
		BigDecimal b = new BigDecimal(size); 
		double y1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
		java.text.DecimalFormat df =new java.text.DecimalFormat("#.00"); 
		return Double.parseDouble(df.format(y1));
	}
} 
