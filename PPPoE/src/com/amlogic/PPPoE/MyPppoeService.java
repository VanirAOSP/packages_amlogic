package com.amlogic.PPPoE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.amlogic.pppoe.PppoeOperation;


public class MyPppoeService extends Service
{
    private static final String TAG = "MyPppoeService";
    private NotificationManager mNM;
    private Handler mHandler;
    private PppoeOperation operation = null;
    
    
    @Override
    public void onCreate() {
        Log.d(TAG, ">>>>>>onCreate");

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mHandler = new DMRHandler();

        /* start check after 5s */
        mHandler.sendEmptyMessageDelayed(0, 5000);

        IntentFilter f = new IntentFilter();

        f.addAction(Intent.ACTION_SHUTDOWN);        
        f.addAction(Intent.ACTION_SCREEN_OFF);        
        registerReceiver(mShutdownReceiver, new IntentFilter(f));

    }

    @Override
    public void onDestroy() {
        //unregisteReceiver
        unregisterReceiver(mShutdownReceiver);
        // Cancel the persistent notification.
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class DMRHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "handleMessage");
            /* check per 10s */
            mHandler.sendEmptyMessageDelayed(0, 1000000);
        }
    }


private BroadcastReceiver mShutdownReceiver = 
    new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG , "onReceive :" +intent.getAction());
       
        if ((Intent.ACTION_SCREEN_OFF).equals(intent.getAction())) {
                operation = new PppoeOperation();
                operation.disconnect();
        }
    }
};

}

