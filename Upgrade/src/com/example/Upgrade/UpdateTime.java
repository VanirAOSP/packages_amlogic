package com.example.Upgrade;

import android.util.Log;

public class UpdateTime {
	private static final String TAG = "UpgradeActivity.UpdateTime";
	public final static int DEFAULT_YEAR = 2010;
	public final static int WEEK_DAYS = 7;
	
	public boolean checkTimeWeek() {
		SysTime systime = new SysTime();
		SaveTime savetime = new SaveTime();
		int sys_year = systime.getYear();
		int sys_day = systime.getDay();
		
		if(savetime.load() == false) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "SaveTime.load() Exception!");
			return false;
		}
		
		int save_year = savetime.getYear();
		int save_day = savetime.getDay();
		
		if((save_year <= DEFAULT_YEAR) || (save_day <= 0)) {
			savetime.saveYear(sys_year);
			savetime.saveDay(sys_day);
			if(savetime.save() == false) {
				if(UpgradeActivity.DEBUG) Log.d(TAG, "SaveTime.save() Exception!");
			}
			return false;
		}
		
		if(save_year > sys_year) {
			savetime.saveYear(sys_year);
			savetime.saveDay(sys_day);
			if(savetime.save() == false) {
				if(UpgradeActivity.DEBUG) Log.d(TAG, "SaveTime.save() Exception!");
			}
			return false;
		}
		else if(save_year == sys_year){
			if(save_day > sys_day) {
				savetime.saveYear(sys_year);
				savetime.saveDay(sys_day);
				if(savetime.save() == false) {
					if(UpgradeActivity.DEBUG) Log.d(TAG, "SaveTime.save() Exception!");
				}
				return false;
			}
			else {
				if(sys_day >= (save_day + WEEK_DAYS)) {
					return true;
				}
				return false;
			}
		}
		else if((save_year+1) == sys_year){
			if((CalcDayOfYearEnd(save_year, save_day) + sys_day) >= WEEK_DAYS)
				return true;
			else
				return false;
		}

		return true;
	}
	
	private int CalcDayOfYearEnd(int myear, int mday) {
		int whole_year = 365;
		
		if(mday > whole_year) {
			return 0;
		}
		if((myear%400 == 0) || ((myear%4 == 0) && (myear%100 != 0)))
			whole_year = 366;
		
		return whole_year-mday;
	}
}
