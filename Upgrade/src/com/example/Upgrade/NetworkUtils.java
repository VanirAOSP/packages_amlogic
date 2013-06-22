package com.example.Upgrade;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	private final static String TAG = "Upgrade.NetworkUtils";
	
	public static boolean isNetworkUp(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) 
        	return info.isAvailable();
        else
        	return false;
	}
}