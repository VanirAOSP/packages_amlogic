package com.amlogic.netfilebrowser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.util.Log;

public class NetBrowserOp {
	private static final String TAG = "NetBrowserOp";
    public static final String BROWSE = "browse";
    public static final String SCAN = "scan";
    public static final String LIST = "list";
    public static final String THUMB = "thumb";
    public static final String BY_NAME = "by_name";
	private final static String ACTION_TYPE = "action_type";
	private final static String DISP_MODE = "display_mode";
	private final static String SORT_MODE = "sort_mode";
	private final static String CONFIG_PATH = "/data/data/com.amlogic.netfilebrowser/files/netfilebrowser.cfg";
	private File mFile;
	
	private String action_type;
	private String display_mode;
	private String sort_mode;
	
	public NetBrowserOp() {
		mFile = new File(CONFIG_PATH);
		int ret = load();
		if(ret < 0) {
			if(action_type == null)
				action_type = BROWSE;
			if(display_mode == null)
				display_mode = LIST;
			if(sort_mode == null)
				sort_mode = BY_NAME;
			save();
		}
	}
	
	public String getData(String type) {
		if(type != null) {
			if(type.equals(ACTION_TYPE)) {
				return getActType();
			}
			else if(type.equals(DISP_MODE)) {
				return getDispMode();
			}
			else if(type.equals(SORT_MODE)) {
				return getSortMode();
			}
		}
		return null;
	}
	
	public void setData(String type, String data) {
		if((type != null) && (data != null)) {
			if(type.equals(ACTION_TYPE)) {
				setActType(data);
			}
			else if(type.equals(DISP_MODE)) {
				setDispMode(data);
			}
			else if(type.equals(SORT_MODE)) {
				setSortMode(data);
			}
		}
	}
	
	public String getActType() {
		return action_type;
	}

	public String getDispMode() {
		return display_mode;
	}

	public String getSortMode() {
		return sort_mode;
	}
	
	public void setActType(String action_type) {
		this.action_type = action_type;
		save();
	}

	public void setDispMode(String display_mode) {
		this.display_mode = display_mode;
		save();
	}

	public void setSortMode(String sort_mode) {
		this.sort_mode = sort_mode;
		save();
	}
	
	public int load() {
		if(!mFile.exists()) {
			try {
				mFile.createNewFile();
			}
			catch (Exception e) {
				Log.e(TAG, e.getMessage().toString());
				return -1;
			}
		}
		
		try {
			FileReader fr = new FileReader(mFile);
			char[] buf = new char[100];
			
			int len = fr.read(buf);

			String r_string = new String(buf, 0, len);
			Log.d(TAG, "load string is " + r_string);

			action_type = getString(r_string, ACTION_TYPE);
			display_mode = getString(r_string, DISP_MODE);
			sort_mode = getString(r_string, SORT_MODE);
			fr.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
			return -1;
		}
		if((action_type == null) || (display_mode == null) || (sort_mode == null))
			return -1;
		return 1;
	}
	
	public boolean save() {
		if(!mFile.getParentFile().exists()) {
			mFile.getParentFile().mkdirs();
		}
		
		if(!mFile.exists()) {
			try {
				mFile.createNewFile();
			}
			catch (Exception e) {
				Log.e(TAG, e.getMessage().toString());
				return false;
			}
		}
		
		String s_string = new String(ACTION_TYPE + ":" + action_type + ";"
				+ DISP_MODE + ":" + display_mode + ";"
				+ SORT_MODE + ":" + sort_mode + ";");
		Log.d(TAG, "save string is " + s_string);
		try {
			FileWriter fw = new FileWriter(mFile);
			fw.write(s_string);
			fw.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
			return false;
		}
		return true;
	}
	
	private String getString(String res_str, String start_str) {
		int start = 0;
		int end = 0;
		int len = 0;
		int count = 0;
		String return_str = null;
		
		len = res_str.length();
		start = res_str.indexOf(start_str+":");
		if(start >= 0) {
			start += start_str.length()+1;
			while(res_str.charAt(start + count) != ';') {
				count++;
			}
			end = start+count;
			
			if((start >= len) || (end >= len)) {
				Log.e(TAG, "index error, when get " + start_str);
			}
			else {
				Log.d(TAG, start_str+", start:"+Integer.toString(start)
						+",end:"+Integer.toString(end));
				return_str = res_str.substring(start, end);
				Log.d(TAG, "load "+start_str+" is " + return_str);
			}
		}
		
		return return_str;
	}
}
