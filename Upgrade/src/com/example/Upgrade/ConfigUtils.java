package com.example.Upgrade;

import android.content.Context;

public class ConfigUtils {	
	private final static String TAG = "Upgrade.ConfigUtils";
	
    public static boolean getBooleanConfig(Context context, int id) {
    	return context.getResources().getBoolean(id); 
    }    

    public static String getStringConfig(Context context, int id) {
    	return context.getResources().getString(id);
    }  	
}
