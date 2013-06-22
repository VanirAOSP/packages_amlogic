package com.example.Upgrade;

import java.util.Calendar;

public class SysTime {
	private final Calendar mCalendar;
	
	public SysTime() {
		mCalendar = Calendar.getInstance();
		long time = System.currentTimeMillis();
		mCalendar.setTimeInMillis(time);
	}
	
	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}
	
	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_YEAR);
	}
}
