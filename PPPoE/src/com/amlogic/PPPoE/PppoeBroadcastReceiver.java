package com.amlogic.PPPoE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.ethernet.EthernetManager;
import android.content.SharedPreferences;
import com.amlogic.pppoe.PppoeOperation;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;

public class PppoeBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PppoeBroadcastReceiver";
    private static final String ACTION_BOOT_COMPLETED =
        "android.intent.action.BOOT_COMPLETED";

    private Handler mHandler;
    private boolean mAutoDialFlag = false;
    private String mInterfaceSelected = null;
    private String mUserName = null;
    private String mPassword = null;
    private PppoeOperation operation = null;

    private String getNetworkInterfaceSelected(Context context)
    {
        SharedPreferences sharedata = context.getSharedPreferences("inputdata", 0);
        if(sharedata != null && sharedata.getAll().size() > 0)
        {
            return sharedata.getString(PppoeConfigDialog.INFO_NETWORK_INTERFACE_SELECTED, null); 
        }
        return null;
    }


    private boolean getAutoDialFlag(Context context)
    {
        SharedPreferences sharedata = context.getSharedPreferences("inputdata", 0);
        if(sharedata != null && sharedata.getAll().size() > 0)
        {
            return sharedata.getBoolean(PppoeConfigDialog.INFO_AUTO_DIAL_FLAG, false); 
        }
        return false;
    }

    private String getUserName(Context context)
    {
        SharedPreferences sharedata = context.getSharedPreferences("inputdata", 0);
        if(sharedata != null && sharedata.getAll().size() > 0)
        {
            return sharedata.getString(PppoeConfigDialog.INFO_USERNAME, null); 
        }
        return null;
    }

    private String getPassword(Context context)
    {
        SharedPreferences sharedata = context.getSharedPreferences("inputdata", 0);
        if(sharedata != null && sharedata.getAll().size() > 0)
        {
            return sharedata.getString(PppoeConfigDialog.INFO_PASSWORD, null); 
        }
        return null;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mInterfaceSelected = getNetworkInterfaceSelected(context);
        mAutoDialFlag = getAutoDialFlag(context);

        mUserName = getUserName(context);
        mPassword = getPassword(context);
        
		if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			context.startService(new Intent(context, 
					MyPppoeService.class));				
		}

        if (null == mInterfaceSelected
            || !mAutoDialFlag
            || null == mUserName
            || null == mPassword)
            return;
        

        if (EthernetManager.ETH_STATE_CHANGED_ACTION.equals(action)) {
            if (!mInterfaceSelected.startsWith("eth"))
                return;

            Log.d(TAG , ">>>>>onReceive :" +intent.getAction());
            int event = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE, -1);
            if (event == EthernetStateTracker.EVENT_HW_DISCONNECTED ) {
                Log.d(TAG, "EVENT_HW_DISCONNECTED");
            }
            else if (event == EthernetStateTracker.EVENT_HW_PHYCONNECTED ) {
                Log.d(TAG, "EVENT_HW_PHYCONNECTED");
                Log.d(TAG , "InterfaceSelected = " + mInterfaceSelected);
                operation = new PppoeOperation();
                set_pppoe_running_flag();
                operation.terminate();

                mHandler = new PppoeHandler();
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }
            else if (event == EthernetStateTracker.EVENT_HW_CONNECTED )
                Log.d(TAG, "EVENT_HW_CONNECTED");
            else
                Log.d(TAG, "EVENT=" + event);
        }

        if ((ConnectivityManager.CONNECTIVITY_ACTION).equals(action)) {
            NetworkInfo netInfo = (NetworkInfo) intent.getExtra(WifiManager.EXTRA_NETWORK_INFO, null);
            if((netInfo != null) && (netInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED))
            {
                if (!mInterfaceSelected.startsWith("wlan"))
                    return;

                operation = new PppoeOperation();
                set_pppoe_running_flag();
                operation.disconnect();

                mHandler = new PppoeHandler();
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }
        }
    }

    void set_pppoe_running_flag()
    {
        SystemProperties.set(PppoeConfigDialog.pppoe_running_flag, "100");
        String propVal = SystemProperties.get(PppoeConfigDialog.pppoe_running_flag);
        int n = 0;
        if (propVal.length() != 0) {
            try {
                n = Integer.parseInt(propVal);
                Log.d(TAG, "set_pppoe_running_flag as " + n);
            } catch (NumberFormatException e) {}
        } else {
            Log.d(TAG, "failed to set_pppoe_running_flag");
        }

        return;
    }

    private class PppoeHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "handleMessage");

            operation.connect(mInterfaceSelected, mUserName, mPassword);
        }
    }
}

