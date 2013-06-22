package com.amlogic.PPPoE;

import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeDevInfo;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class PPPoEActivity extends Activity {
    private final String TAG = "PPPoEActivity";
    private PppoeConfigDialog mPppoeConfigDialog;
    private PppoeDevInfo mPppoeInfo;
    private PppoeManager mPppoeManager;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d(TAG, "Create PppoeConfigDialog");
        mPppoeConfigDialog = new PppoeConfigDialog(this);

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService
                                        ( Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = cm.getActiveNetworkInfo(); 
        if (info != null) {
           Log.d(TAG, info.toString());
        }

        mPppoeManager = (PppoeManager) this.getSystemService(Context.PPPOE_SERVICE);

        mPppoeInfo = mPppoeManager.getSavedPppoeConfig();
        if (mPppoeInfo != null) {
            Log.d(TAG, "IP: " + mPppoeInfo.getIpAddress());
            Log.d(TAG, "MASK: " + mPppoeInfo.getNetMask());
            Log.d(TAG, "GW: " + mPppoeInfo.getRouteAddr());
            Log.d(TAG, "DNS: " + mPppoeInfo.getDnsAddr());
        }

        Log.d(TAG, "Show PppoeConfigDialog");
        mPppoeConfigDialog.show();
    }
}
