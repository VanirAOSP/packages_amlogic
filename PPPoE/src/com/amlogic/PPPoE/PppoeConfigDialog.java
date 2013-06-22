package com.amlogic.PPPoE;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Message;
import android.os.Handler;
import android.os.SystemProperties;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton; 
import android.widget.EditText;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;  
import android.widget.ArrayAdapter;  
import android.widget.Spinner;  
import android.widget.TextView;

import android.net.ethernet.EthernetManager;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.ArrayList;
import java.net.SocketException;

import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeStateTracker;

import com.amlogic.pppoe.PppoeOperation;

public class PppoeConfigDialog extends AlertDialog implements DialogInterface.OnClickListener
{
    private static final String PPPOE_DIAL_RESULT_ACTION =
            "PppoeConfigDialog.PPPOE_DIAL_RESULT";

    private static final int PPPOE_STATE_UNDEFINED = 0;
    private static final int PPPOE_STATE_DISCONNECTED = 1;
    private static final int PPPOE_STATE_CONNECTING = 2;
    private static final int PPPOE_STATE_DISCONNECTING = 3;
    private static final int PPPOE_STATE_CONNECT_FAILED = 4;
    private static final int PPPOE_STATE_CONNECTED = 5;

    private static final int MSG_CONNECT_TIMEOUT = 0xabcd0000;
    private static final int MSG_DISCONNECT_TIMEOUT = 0xabcd0010;
    
    private static final String EXTRA_NAME_STATUS = "status";
    private static final String EXTRA_NAME_ERR_CODE = "err_code";

    public static final String INFO_USERNAME = "name";
    public static final String INFO_PASSWORD = "passwd";
    public static final String INFO_NETWORK_INTERFACE_SELECTED = "network_if_selected";
    public static final String INFO_AUTO_DIAL_FLAG = "auto_dial_flag";
    
    private final String TAG = "PppoeCfgDlg";
    private View mView;
    private EditText mPppoeName;
    private EditText mPppoePasswd;
    private String user_name = null;
    private String user_passwd = null;
    private ProgressDialog waitDialog = null;
    private PppoeOperation operation = null;
    Context context = null;
    private AlertDialog alertDia = null;
    private PppoeReceiver pppoeReceiver = null;
    
    private CheckBox mCbAutoDial;

    Timer connect_timer = null;   
    Timer disconnect_timer = null; 

    private int pppoe_state = PPPOE_STATE_UNDEFINED;

    public static final String pppoe_running_flag = "net.pppoe.running";

    private TextView mNetworkInterfaces;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private EthernetManager mEthManager;
    private ArrayList<String> network_if_list;
    private String mNetIfSelected;
    private String tmp_name, tmp_passwd;
    private boolean mAtuoDialFlag = false;

    private boolean is_pppoe_running()
    {
        String propVal = SystemProperties.get(pppoe_running_flag);
        int n = 0;
        if (propVal.length() != 0) {
            try {
                n = Integer.parseInt(propVal);
            } catch (NumberFormatException e) {}
        } else {
            Log.d(TAG, "net.pppoe.running not FOUND");
        }

        return (n != 0); 
    }


    private void set_pppoe_running_flag()
    {
        SystemProperties.set(pppoe_running_flag, "100");
        String propVal = SystemProperties.get(pppoe_running_flag);
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


    private void clear_pppoe_running_flag()
    {
        SystemProperties.set(pppoe_running_flag, "0");
        String propVal = SystemProperties.get(pppoe_running_flag);
        int n = 0;
        if (propVal.length() != 0) {
            try {
                n = Integer.parseInt(propVal);
                Log.d(TAG, "clear_pppoe_running_flag as " + n);
            } catch (NumberFormatException e) {}
        } else {
            Log.d(TAG, "failed to clear_pppoe_running_flag");
        }

        return;
    }


    public PppoeConfigDialog(Context context)
    {
        super(context);
        this.context = context;
        operation = new PppoeOperation();
        buildDialog(context);
        waitDialog = new ProgressDialog(this.context); 

        pppoeReceiver = new PppoeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
        Log.d(TAG, "registerReceiver pppoeReceiver");
        context.registerReceiver(pppoeReceiver, filter);
    }

    class SpinnerSelectedListener implements OnItemSelectedListener{  
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) {
            mNetIfSelected = network_if_list.get(arg2);
            Log.d(TAG, "interface selected: " + mNetIfSelected);
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {  
        }  
    }

    private void buildDialog(Context context)
    {
        Log.d(TAG, "buildDialog");
        setTitle(R.string.pppoe_config_title);
        this.setView(mView = getLayoutInflater().inflate(R.layout.pppoe_configure, null));
        mPppoeName = (EditText)mView.findViewById(R.id.pppoe_name_edit);
        mPppoePasswd = (EditText)mView.findViewById(R.id.pppoe_passwd_edit);
        mCbAutoDial = (CheckBox)mView.findViewById(R.id.auto_dial_checkbox);
        mCbAutoDial.setVisibility(View.VISIBLE);
        mCbAutoDial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
                    @Override 
                    public void onCheckedChanged(CompoundButton buttonView, 
                            boolean isChecked) { 
                        // TODO Auto-generated method stub 
                        if(isChecked){ 
                            Log.d(TAG, "Selected");
                        }else{ 
                            Log.d(TAG, "NO Selected");
                        } 
                    } 
                }); 

        mPppoeName.setEnabled(true);
        mPppoePasswd.setEnabled(true);
        this.setInverseBackgroundForced(true);

        network_if_list=new ArrayList<String>();

        try {
            for(Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces(); list.hasMoreElements();)
            {
                NetworkInterface netIf = list.nextElement();
                //Log.d(TAG, "network_interface: " + netIf.getDisplayName());
                if (netIf.isUp() && 
                    !netIf.isLoopback() && 
                    !netIf.isPointToPoint() && 
                    !netIf.getDisplayName().startsWith("sit"))                
                    network_if_list.add(netIf.getDisplayName());                
            }
        } catch (SocketException e) {
            return;
        }

        if (network_if_list.size() == 0 )
            network_if_list.add("eth0");                

        mNetIfSelected = network_if_list.get(0);

        /*
        boolean eth_found = false;
        for(int i = 0; i < network_if_list.size(); i++) {
            if (network_if_list.get(i).startsWith("eth"))
                eth_found = true;
        }

        if (!eth_found) {
                network_if_list.add("eth0");                
        }
        */
        
        /*
        mEthManager = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
        String[] network_if_list = mEthManager.getDeviceNameList();
        */

        mNetworkInterfaces = (TextView) mView.findViewById(R.id.network_interface_list_text);
        spinner = (Spinner) mView.findViewById(R.id.network_inteface_list_spinner); 
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,network_if_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());  
        spinner.setVisibility(View.VISIBLE);

        if(connectStatus() != PppoeOperation.PPP_STATUS_CONNECTED)
        {
            this.setButton(BUTTON_POSITIVE, context.getText(R.string.pppoe_dial), this);
        }
        else {
            Log.d(TAG, "connectStatus is CONNECTED");

            //hide AutoDial CheckBox
            mCbAutoDial.setVisibility(View.GONE);

            //hide network interfaces
            mNetworkInterfaces.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);

            //hide Username
            mView.findViewById(R.id.user_pppoe_text).setVisibility(View.GONE);
            mPppoeName.setVisibility(View.GONE);

            //hide Password
            mView.findViewById(R.id.passwd_pppoe_text).setVisibility(View.GONE);
            mPppoePasswd.setVisibility(View.GONE);

            this.setButton(BUTTON_POSITIVE, context.getText(R.string.pppoe_disconnect), this);

            /*
            if (!is_pppoe_running()) {
                showAlertDialog(context.getResources().getString(R.string.pppoe_ppp_interface_occupied));
                return;
            }
            */
        }
        
        this.setButton(BUTTON_NEGATIVE, context.getText(R.string.menu_cancel), this);

        getInfoData();

        if(user_name != null 
          && user_passwd != null
          && user_name.equals("")== false)
        {
            mPppoeName.setText(user_name);
            mPppoePasswd.setText(user_passwd);
        }
        else
        {
            mPppoeName.setText("");
            mPppoePasswd.setText("");
        }

        mCbAutoDial.setChecked(mAtuoDialFlag);
    }
    

    @Override
    public void show()
    {
        Log.d(TAG, "show");
        
        super.show();
    }
    
    void showWaitDialog(int id)
    {
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
        waitDialog.setTitle(""); 
        waitDialog.setMessage(this.context.getResources().getString(id));
        waitDialog.setIcon(null); 

        if (id == R.string.pppoe_dial_waiting_msg){ 
            waitDialog.setButton(android.content.DialogInterface.BUTTON_POSITIVE,this.context.getResources().getString(R.string.menu_cancel),cancelBtnClickListener); 
        }
        
        waitDialog.setIndeterminate(false); 
        waitDialog.setCancelable(true); 
        waitDialog.show();
          
        Button button = waitDialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.requestFocus();
        button.requestFocusFromTouch();
    }

    private void saveInfoData()
    {
        SharedPreferences.Editor sharedata = this.context.getSharedPreferences("inputdata", 0).edit();
        sharedata.clear();
        sharedata.putString(INFO_USERNAME, mPppoeName.getText().toString());
        sharedata.putString(INFO_PASSWORD, mPppoePasswd.getText().toString()); 
        sharedata.putString(INFO_NETWORK_INTERFACE_SELECTED, mNetIfSelected); 
        sharedata.putBoolean(INFO_AUTO_DIAL_FLAG, mCbAutoDial.isChecked()); 
        sharedata.commit();  
    }

    private void getInfoData()
    {
        SharedPreferences sharedata = this.context.getSharedPreferences("inputdata", 0);
        if(sharedata != null && sharedata.getAll().size() > 0)
        {
            user_name = sharedata.getString(INFO_USERNAME, null);   
            user_passwd = sharedata.getString(INFO_PASSWORD, null); 
            mNetIfSelected = sharedata.getString(INFO_NETWORK_INTERFACE_SELECTED, null); 
            mAtuoDialFlag = sharedata.getBoolean(INFO_AUTO_DIAL_FLAG, false); 
        }
        else
        {
            user_name = null;
            user_passwd = null;
        }
    }
    
    private int connectStatus()
    {
        if (null == mNetIfSelected){
            Log.d(TAG, "mNetIfSelected is null");
            return PppoeOperation.PPP_STATUS_DISCONNECTED;
        }
        
        return operation.status(mNetIfSelected);
    }
    

    private void showAlertDialog(final String msg)
    {
        Log.d(TAG, "showAlertDialog");
        AlertDialog.Builder ab = new AlertDialog.Builder(context); 
        alertDia = ab.create();  
        alertDia.setTitle(" "); 
        alertDia.setMessage(msg);
        alertDia.setIcon(null); 
        
        alertDia.setButton(android.content.DialogInterface.BUTTON_POSITIVE,this.context.getResources().getString(R.string.amlogic_ok),AlertClickListener); 

        alertDia.setCancelable(true); 
        alertDia.setInverseBackgroundForced(true);
        alertDia.show();

        Button button = alertDia.getButton(android.content.DialogInterface.BUTTON_POSITIVE);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.requestFocus();
        button.requestFocusFromTouch();
    }
    
    OnClickListener AlertClickListener = new OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which) 
        {
            switch (which) {
            case android.content.DialogInterface.BUTTON_POSITIVE:
                {
                    alertDia.cancel();
                    Log.d(TAG, "User click OK button, exit APK");
                    clearSelf();
                }
                break;
            case android.content.DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                break;
            }
            
        }
    };
    
    private void handleStartDial()
    {
        Log.d(TAG, "handleStartDial");
        tmp_name = mPppoeName.getText().toString();
        tmp_passwd = mPppoePasswd.getText().toString();
        if(tmp_name != null && tmp_passwd != null)
        {
            saveInfoData();
            
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_CONNECT_TIMEOUT:
                            pppoe_state = PPPOE_STATE_CONNECT_FAILED;
                            waitDialog.cancel();
                            showAlertDialog(context.getResources().getString(R.string.pppoe_connect_failed));
							SystemProperties.set("net.pppoe.isConnected", "false");
                            break;
                    }

                    Log.d(TAG, "handleStartDial.handler");
                    super.handleMessage(msg);
                }
            };

            connect_timer = new Timer();   
            TimerTask check_task = new TimerTask()
            {   
                public void run() 
                {   
                    Message message = new Message();
                    Log.d(TAG, "Send MSG_CONNECT_TIMEOUT");                     
                    message.what = MSG_CONNECT_TIMEOUT;
                    handler.sendMessage(message);
                }   
            };

            connect_timer.schedule(check_task, 60000 * 2);

            showWaitDialog(R.string.pppoe_dial_waiting_msg);
            pppoe_state = PPPOE_STATE_CONNECTING;
            set_pppoe_running_flag();
            operation.connect(mNetIfSelected, tmp_name, tmp_passwd);
        }
    }
    
    private void handleStopDial()
    {
        Log.d(TAG, "handleStopDial");
        pppoe_state = PPPOE_STATE_DISCONNECTING;
        boolean result = operation.disconnect();

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_DISCONNECT_TIMEOUT:
                    waitDialog.cancel();
                    showAlertDialog(context.getResources().getString(R.string.pppoe_disconnect_failed));
                    pppoe_state = PPPOE_STATE_DISCONNECTED;
                    clear_pppoe_running_flag();
					SystemProperties.set("net.pppoe.isConnected", "false");
                    break;
                }
                
                Log.d(TAG, "handleStopDial.handler");
                super.handleMessage(msg);
            }
        };

        disconnect_timer = new Timer();   
        TimerTask check_task = new TimerTask()
        {   
            public void run() 
            {   
                Message message = new Message();
                message.what = MSG_DISCONNECT_TIMEOUT;
                Log.d(TAG, "Send MSG_DISCONNECT_TIMEOUT");                     
                handler.sendMessage(message);
            }   
        };

        //Timeout after 50 seconds
        disconnect_timer.schedule(check_task, 50000);
        
        showWaitDialog(R.string.pppoe_hangup_waiting_msg);
    }


    private void handleCancelDial()
    {
        operation.disconnect();
    }
    

    OnClickListener cancelBtnClickListener = new OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which) 
        {
            Log.d(TAG, "Cancel button is clicked");
            if (disconnect_timer != null)
                disconnect_timer.cancel();
            
            handleCancelDial();
            waitDialog.cancel();
            clearSelf();
        }
    };


    //@Override
    public void onClick(DialogInterface dialog, int which) 
    {
        switch (which) {
        case BUTTON_POSITIVE:
            if(connectStatus() == PppoeOperation.PPP_STATUS_CONNECTED)
                handleStopDial();
            else
                handleStartDial();
            break;
        case BUTTON_NEGATIVE:
            Log.d(TAG, "User click Discard button, exit APK");
            clearSelf();
            break;
        default:
            break;
        }
    }

    public class PppoeReceiver extends BroadcastReceiver 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            Log.d(TAG, "#####PppoeReceiver: " + action);

            if(action.equals(PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
                int event = intent.getIntExtra(PppoeManager.EXTRA_PPPOE_STATE,PppoeManager.PPPOE_STATE_UNKNOWN);
                Log.d(TAG, "#####event " + event);
                if(event == PppoeStateTracker.EVENT_CONNECTED)
                {
                    if (pppoe_state == PPPOE_STATE_CONNECTING) {
                        waitDialog.cancel();
                        connect_timer.cancel();
                    }
                    pppoe_state = PPPOE_STATE_CONNECTED;
                    showAlertDialog(context.getResources().getString(R.string.pppoe_connect_ok));
					SystemProperties.set("net.pppoe.isConnected", "true");
                }

                if(event == PppoeStateTracker.EVENT_DISCONNECTED)
                {
                    if (pppoe_state == PPPOE_STATE_DISCONNECTING) {
                        waitDialog.cancel();
                        disconnect_timer.cancel();
                        clear_pppoe_running_flag();
                    }
                    pppoe_state = PPPOE_STATE_DISCONNECTED;
                    showAlertDialog(context.getResources().getString(R.string.pppoe_disconnect_ok));
					SystemProperties.set("net.pppoe.isConnected", "false");
                }

                if(event == PppoeStateTracker.EVENT_CONNECT_FAILED)
                {
                    String ppp_err = intent.getStringExtra(PppoeManager.EXTRA_PPPOE_ERRCODE);
                    Log.d(TAG, "#####errcode: " + ppp_err);

                    if (pppoe_state == PPPOE_STATE_CONNECTING) {
                        waitDialog.cancel();
                        connect_timer.cancel();
                        clear_pppoe_running_flag();
                    }

                    pppoe_state = PPPOE_STATE_CONNECT_FAILED;
                    String reason = "";
                    if (ppp_err.equals("691"))
                        reason = context.getResources().getString(R.string.pppoe_connect_failed_auth);
                    else if (ppp_err.equals("650"))
                        reason = context.getResources().getString(R.string.pppoe_connect_failed_server_no_response);

                    showAlertDialog(context.getResources().getString(R.string.pppoe_connect_failed) + "\n" + reason);
					SystemProperties.set("net.pppoe.isConnected", "false");
                }
            }
        }
    }
    
    private void clearSelf()
    {
        if(pppoeReceiver != null) {
            Log.d(TAG, "unregisterReceiver pppoeReceiver");
            context.unregisterReceiver(pppoeReceiver);
        }
        ((PPPoEActivity)context).finish();

        /*
        Log.d(TAG, "killProcess");
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);    
        */
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "User BACK operation, exit APK");
            clearSelf();
            return true;
        }

        Log.d(TAG, "keyCode " + keyCode + " is down, do nothing");
        return super.onKeyDown(keyCode, event);
    }
    
}
