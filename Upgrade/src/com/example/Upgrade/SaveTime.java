package com.example.Upgrade;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SaveTime {
	private final static String UCT_YEAR = "year";
	private final static String UCT_DAY = "day";
	private final static String CONFIG_PATH = "/data/data/com.amlogic.OnlineUpdate/files/update.cfg";
	private File mFile;
	
	private int year;
	private int day;
	
	public SaveTime() {
		mFile = new File(CONFIG_PATH);
		
		if(!mFile.getParentFile().exists()) {
			mFile.getParentFile().mkdirs();
		}
		
		if(!mFile.exists()) {
			try {
				mFile.createNewFile();
			}
			catch (Exception e) {
			}
		}
	}
	
	public int getYear() {
		return year;
	}

	public int getDay() {
		return day;
	}

	public void saveYear(int myear) {
		year = myear;
	}

	public void saveDay(int mday) {
		day = mday;
	}
	
	public boolean load() {
		if(!mFile.getParentFile().exists()) {
			mFile.getParentFile().mkdirs();
		}
		
		if(!mFile.exists()) {
			try {
				mFile.createNewFile();
			}
			catch (Exception e) {
				return false;
			}
		}
		
		try {
			FileReader fr = new FileReader(mFile);
			char[] buf = new char[100];
			
			fr.read(buf);
			
			String r_string = new String(buf);
			int len = r_string.length();
			
			int y_start = r_string.indexOf(UCT_YEAR+":");
			if(y_start >= 0) {
				y_start += 5;
				int y_end = y_start;
				for(int i=y_start; i<len; i++) {
					if((r_string.charAt(i) < '0') || (r_string.charAt(i) > '9'))
						break;
					else
						y_end += 1;
				}
				
				if(y_end == y_start) {
					year = 0;
				}
				else {
					String year_string = r_string.substring(y_start, y_end);
					year = Integer.valueOf(year_string);
				}
			}
			else {
				year = 0;
			}
			
			int d_start = r_string.indexOf(UCT_DAY+":");
			if(d_start >= 0) {
				d_start += 4;
				int d_end = d_start;
				for(int i=d_start; i<len; i++) {
					if((r_string.charAt(i) < '0') || (r_string.charAt(i) > '9'))
						break;
					else
						d_end += 1;
				}
				
				if(d_end == d_start) {
					day = 0;
				}
				else {
					String day_string = r_string.substring(d_start, d_end);
					day = Integer.valueOf(day_string);
				}
			}
			else {
				day = 0;
			}
			fr.close();
		}
		catch (Exception e) {
			return false;
		}
		return true;
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
				return false;
			}
		}
		
		String s_string = new String(UCT_YEAR+":"+String.valueOf(year)+"\n"
				+UCT_DAY+":"+String.valueOf(day));

		try {
			FileWriter fw = new FileWriter(mFile);
			fw.write(s_string);
			fw.close();
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}	
}
