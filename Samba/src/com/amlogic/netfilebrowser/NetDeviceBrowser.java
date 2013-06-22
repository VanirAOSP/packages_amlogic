package com.amlogic.netfilebrowser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import com.amlogic.netfilebrowser.smbmnt.SmbClientMnt;
import com.amlogic.netfilebrowser.db.DBOpenHelper;
import com.amlogic.netfilebrowser.db.DataBases;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class NetDeviceBrowser extends Activity {
	private static final String TAG = "NetDeviceBrowser";
    public static final int MARK = 1;
    public static final int UNMARK = 0;
    private final int DIALOG_CLOSE_MESSAGE = 3;
    private final int DIALOG_ERROR_IP = 4;
    private final int DIALOG_ERROR_MSG1 = 5;
    private final int DIALOG_ERROR_MSG2 = 6;
    private final int DIALOG_ERROR_NET_RES = 7;
    private final int DIALOG_SORT = 8;
    private final int DIALOG_HELP = 9;
	private Timer searchTimer = null;
	private ProgressDialog progressDialog = null;
	private SmbClientMnt MntInfo = null;
	private DataBases mDataBases = null;
	private ArrayList<String> deviceList = null;
	private ListView mListView = null;
	private GridView mGridView = null;
	private TextView mPath = null;
	private Button btn_home = null;
	private Button btn_browse_scan = null;
	private Button btn_sort = null;
	private Button btn_parent = null;
	private Button btn_list_thumb = null;
	private Button btn_help = null;
	private Button btn_close = null;
	private EditText computerIp = null;
	private EditText computerDir = null;
	private EditText user_edit = null;
	private EditText passwd_edit = null;
	private CheckBox mCheckBox = null;
	private AlertDialog sort_dialog = null;
	private AlertDialog help_dialog = null;
	private NetBrowserOp mNetBrowserOp = null;
	private int searchCount = 0;
	private String open_path = null;
	
	//private static final int noComputerDialog = 1;
	private static final int inputPasswdDialog = 1;
	private static final int inputIpDialog = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetInfo == null) {
			showDialog(DIALOG_CLOSE_MESSAGE, null);
			return;
		}
		
		MntInfo = new SmbClientMnt();
		mDataBases = new DataBases(NetDeviceBrowser.this);
		deviceList = mDataBases.getMarkDevice(MARK);
		mNetBrowserOp = new NetBrowserOp();
        if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
        	initListView();
        }
        else {
        	initThumbView();
        }
        
        initTopBar();
        if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
        	searchDeviceList();
        }
	}
	
	private void initTopBar() {
        btn_home = (Button) findViewById(R.id.btn_home);  
        btn_home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
			}        	
        });   
        
        btn_browse_scan = (Button) findViewById(R.id.btn_browse_scan); 
        if((deviceList.size() <= 0) || (mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN))) {
        	mNetBrowserOp.setActType(NetBrowserOp.SCAN);
        	btn_browse_scan.setBackgroundResource(R.drawable.btn_scan);
        }
        else {
        	mNetBrowserOp.setActType(NetBrowserOp.BROWSE);
        	btn_browse_scan.setBackgroundResource(R.drawable.btn_browse);
        }
        btn_browse_scan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Log.d(TAG, "get action : " + mNetBrowserOp.getActType());
				if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
					ArrayList<String> tempList = null;
					tempList = mDataBases.getMarkDevice(MARK);
		        	deviceList = tempList;
		        	if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
		        		displayListView();
		        	}
		        	else {
		        		displayThumbView();
		        	}
		        	mNetBrowserOp.setActType(NetBrowserOp.BROWSE);
		        	btn_browse_scan.setBackgroundResource(R.drawable.btn_browse);
				}
				else {
					mNetBrowserOp.setActType(NetBrowserOp.SCAN);
		        	btn_browse_scan.setBackgroundResource(R.drawable.btn_scan);
		        	searchDeviceList();
				}
			}        	
        });

        btn_sort = (Button) findViewById(R.id.btn_sort);  
        btn_sort.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_SORT, null);
			}        	
        });
        
        btn_parent = (Button) findViewById(R.id.btn_parent);  
        btn_parent.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}        	
        });

        btn_list_thumb = (Button) findViewById(R.id.btn_list_thumb);  
        if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
        	btn_list_thumb.setBackgroundResource(R.drawable.btn_list);
        }
        else {
        	btn_list_thumb.setBackgroundResource(R.drawable.btn_thumb);
        }
        btn_list_thumb.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    	        if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
    	        	mNetBrowserOp.setDispMode(NetBrowserOp.THUMB);
    	        	initThumbView();
    	        	
    	        }
    	        else {
    	        	mNetBrowserOp.setDispMode(NetBrowserOp.LIST);
    	        	initListView();
    	        }
    	        initTopBar();
	            btn_list_thumb.requestFocus();
    		}   			       		
        });
        
        btn_help = (Button) findViewById(R.id.btn_help);  
        btn_help.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_HELP, null);
			}
        });  
        
        btn_close = (Button) findViewById(R.id.btn_close);  
        btn_close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}        	
        });  
	}
	
	private void initListView() {
        setContentView(R.layout.netbrowser_list);
        mPath = (TextView) findViewById(R.id.path);  
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(getDeviceAdapter(deviceList, NetBrowserOp.LIST));
        mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
				String pathname = (String) item.get("item_name");
				Map<String, Object> data = null;
				if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
					data = new HashMap<String, Object>();
					data.put(DBOpenHelper.NAME, pathname);
					if (item.get("item_sel").equals(R.drawable.item_img_unsel)) {
						data.put(DBOpenHelper.IS_SELECTED, MARK);
						item.put("item_sel", R.drawable.item_img_sel);
					}
					else if (item.get("item_sel").equals(R.drawable.item_img_sel)) {
						data.put(DBOpenHelper.IS_SELECTED, UNMARK);
						item.put("item_sel", R.drawable.item_img_unsel);
					}
					mDataBases.update(data);
					updateListView();
				}
				else {
					String user = null;
					String password = null;
					data = mDataBases.getUserPassword(pathname);
;
					if(data != null) {
						user = (String) data.get(DBOpenHelper.USER);
						password = (String) data.get(DBOpenHelper.PASSWORD);
					}
					openNetDevice(pathname, user, password);
				}
			}        	
        });
	}

	private void initThumbView() {
        setContentView(R.layout.netbrowser_thumb);
        mPath = (TextView) findViewById(R.id.thumb_path);  
        mGridView = (GridView) findViewById(R.id.mygridview);
        mGridView.setAdapter(getDeviceAdapter(deviceList, NetBrowserOp.THUMB));
        mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
				String pathname = (String) item.get("item_name");
				Map<String, Object> data = null;
				if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
					data = new HashMap<String, Object>();
					data.put(DBOpenHelper.NAME, pathname);
					if (item.get("item_sel").equals(R.drawable.item_img_unsel)) {
						data.put(DBOpenHelper.IS_SELECTED, MARK);
						item.put("item_sel", R.drawable.item_img_sel);
					}
					else if (item.get("item_sel").equals(R.drawable.item_img_sel)) {
						data.put(DBOpenHelper.IS_SELECTED, UNMARK);
						item.put("item_sel", R.drawable.item_img_unsel);
					}
					mDataBases.update(data);
					updateThumbView();	
				}
				else {
					String user = null;
					String password = null;
					data = mDataBases.getUserPassword(pathname);
;
					if(data != null) {
						user = (String) data.get(DBOpenHelper.USER);
						password = (String) data.get(DBOpenHelper.PASSWORD);
					}
					openNetDevice(pathname, user, password);
				}
			}
		});
	}
	
	private void searchDeviceList() {
		mDataBases.deleteAll();
        MntInfo.FreshList();
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(this.getResources().getString(R.string.searching));
        progressDialog.setMessage(this.getResources().getString(R.string.pleasewait));
        progressDialog.setCancelable(true);
        progressDialog.show();
       
        createSearchTimer();
	}
	
	public void onDestroy() {
	    stopSearchTime();
	    stopSearchDialog();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {	
		super.onConfigurationChanged(newConfig);
	}
	
	protected Dialog onCreateDialog(int id, Bundle data) {
		switch (id) {
		    case DIALOG_CLOSE_MESSAGE://no network connect
		    	return new AlertDialog.Builder(this)
                .setTitle(R.string.network_not_ready)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	finish();
                    }
                })
                .create();
			/*
			case noComputerDialog:
			   return buildIpDialog(this);
			*/
	        case DIALOG_SORT:
	        	LayoutInflater inflater = (LayoutInflater) this
	        	.getSystemService(LAYOUT_INFLATER_SERVICE);
	        	View layout_sort = inflater.inflate(R.layout.sort_dialog_layout,
	        			(ViewGroup) findViewById(R.id.layout_root_sort));
	        	sort_dialog = new AlertDialog.Builder(this)   
	        	.setView(layout_sort)
	            .create(); 
	        	return sort_dialog;
	        case DIALOG_HELP:
	        	LayoutInflater inflater_help = (LayoutInflater) this
	        	.getSystemService(LAYOUT_INFLATER_SERVICE);
	        	View layout_help = inflater_help.inflate(R.layout.help_dialog_layout,
	        			(ViewGroup) findViewById(R.id.layout_root_help));
	        	help_dialog = new AlertDialog.Builder(this)   
	        	.setView(layout_help)
	            .create(); 
	        	return help_dialog;
		    case inputIpDialog://no find smaba device
		    	return buildIpEntryDialog(this);
		    case inputPasswdDialog:
		    	return buildPasswdEntryDialog(this, data);	
		    case DIALOG_ERROR_IP://ip is error
		    	return new AlertDialog.Builder(this)
                .setTitle(R.string.error_ip)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
		    case DIALOG_ERROR_MSG1://ip can not connect
		    	return new AlertDialog.Builder(this)
                .setTitle(R.string.error_msg1)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
		    case DIALOG_ERROR_MSG2:
		    	return new AlertDialog.Builder(this)
                .setTitle(R.string.error_msg2)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
		    case DIALOG_ERROR_NET_RES://
				return new AlertDialog.Builder(NetDeviceBrowser.this)
    	        .setMessage(NetDeviceBrowser.this.getResources().getString(R.string.neterror))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
		    default:
		    	return null;
		}	
	}
	
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        LayoutParams lp = dialog.getWindow().getAttributes();
        
        if (display.getWidth() > display.getHeight()) {            	
        	lp.width = (int) (display.getWidth() * 0.5);       	
    	} else {        		
    		lp.width = (int) (display.getWidth() * 0.1);            	
    	}
        dialog.getWindow().setAttributes(lp); 
        
    	 switch (id) {
    	 	case DIALOG_SORT:
	            ListView sort_lv = (ListView) sort_dialog.getWindow().findViewById(R.id.sort_listview);  
	            sort_lv.setAdapter(getDialogListAdapter(DIALOG_SORT));	
	            
	            sort_lv.setOnItemClickListener(new OnItemClickListener() {
	            	public void onItemClick(AdapterView<?> parent, View view, int pos,
	    					long id) {
	            		sort_dialog.dismiss();			
	    			}
	            	
	            });
		    	Button sort_btn_close = (Button) sort_dialog.getWindow().findViewById(R.id.sort_btn_close);  
		    	sort_btn_close.setOnClickListener(new OnClickListener() {
		    		public void onClick(View v) {
		    			sort_dialog.dismiss();
		    		}        	
		        });		
	            break;
            
	    	case DIALOG_HELP:
	            ListView help_lv = (ListView) help_dialog.getWindow().findViewById(R.id.help_listview);  
	            help_lv.setAdapter(getDialogListAdapter(DIALOG_HELP));
	            
		    	Button help_btn_close = (Button) help_dialog.getWindow().findViewById(R.id.help_btn_close);  
		    	help_btn_close.setOnClickListener(new OnClickListener() {
		    		public void onClick(View v) {
		    			help_dialog.dismiss();
		    		}        	
		        });
	    		break;
    	}
    }

    private SimpleAdapter getDialogListAdapter(int id) {
        return new SimpleAdapter(this,
        		getDialogListData(id), R.layout.dialog_item,        		
                new String[]{"item_type", "item_name", "item_sel"},        		
                new int[]{R.id.dialog_item_type, R.id.dialog_item_name, R.id.dialog_item_sel,});  
    }

    private List<Map<String, Object>> getDialogListData(int id) { 
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
    	Map<String, Object> map; 
    	
    	switch (id) {
    	    case DIALOG_SORT:  	
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_item_type_name);  
    	    	map.put("item_name", getText(R.string.sort_dialog_name_str));
    	    	map.put("item_sel", R.drawable.dialog_item_img_sel);  
    	    	list.add(map);    	
    	    	break;

    	    case DIALOG_HELP:
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_home);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_home));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_list);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_list));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_sort);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_sort));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_parent);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_parent));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_thumb);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_thumb));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	    	map = new HashMap<String, Object>();     		
    	    	map.put("item_type", R.drawable.dialog_help_item_close);  
    	    	map.put("item_name", getText(R.string.dialog_help_str_close));
    	    	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
    	    	list.add(map);
    	}
    	return list;
    }
    
	boolean CheckIPAddress(String ip) {
		StringTokenizer st = new StringTokenizer(ip, "~!@#$%^&*()_+-=<>?.");
		int count = st.countTokens();
		
		int temp;
		
		if(count > 4)
			return false;
		
		for(int i=0; i<count ; i++) {
			temp = Integer.valueOf(st.nextToken());
			
			if(temp >= 254)
				return false;
		}
		
		return true;
	}
	
	private void openNetDevice(String pathname, String user, String password) {
    	String mtpath = null;
    	int ret = -1;
    	Log.d(TAG, "openNetDevice, name:" + pathname
    			+ ", user:" + user + ", password:" + password);
    	if(user != null) {
    		if(password != null) {
    			mtpath = "mount " + pathname + " /mnt/NetShareDirs" 
    			    + " username=" + user +",password=" + password;
    		}
    		else {
    			mtpath = "mount " + pathname + " /mnt/NetShareDirs" 
    			    + " username=" + user;
    		}
    	}
    	else {
    		mtpath = "mount " + pathname + " /mnt/NetShareDirs" + " username=guest";
    	}
    	ret = MntInfo.SmbUnMount("umount /mnt/NetShareDirs");
    	
    	if(ret == (int)0xabcd) {
    		showDialog(DIALOG_ERROR_MSG2, null);		    		
    	}
    	
    	ret = MntInfo.SmbMount(mtpath);
    	
    	if(ret == (int)0xabcd) {
    		showDialog(DIALOG_ERROR_MSG1, null);		    		
    	}
    	else if(ret < 0) {
    		Bundle bundle = new Bundle();
    		bundle.putString("pathname", pathname);
    		bundle.putString("user", user);
    		bundle.putString("password", password);
    		showDialog(inputPasswdDialog, bundle);
    	}
    	else {
    		Intent intent = new Intent();
    		intent.setClass(NetDeviceBrowser.this, NetFileBrowser.class);
    		startActivity(intent);
    		finish();
    	}
	}
	
	private Dialog buildIpEntryDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog_ip_text_entry, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle(R.string.inputbyhand);
		builder.setView(textEntryView);
		computerIp = (EditText)textEntryView.findViewById(R.id.computer_id);
		computerDir = (EditText)textEntryView.findViewById(R.id.computer_dir);
		builder.setPositiveButton(R.string.confirm,
		    new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	int i = 0;
			    	String temp2 = computerDir.getText().toString();
			    	String temp1 = computerIp.getText().toString();
			    	
			    	if(temp2.length() != 0 && !temp2.substring(0, 1).equals("/"))
			    		temp2 = "/" + temp2;
			    	
			    	if(temp1.length() != 0 && temp2.length() != 0) {
			    		if(CheckIPAddress(temp1)==false) {
			    			showDialog(DIALOG_ERROR_IP, null);
			    			return;
			    		}
			    		
			    		temp1 = "//"+temp1 + temp2;
			    		for(i = 0; i < deviceList.size(); i++) {
			    			if(temp1.equals(deviceList.get(i)))
			    				break;
			    		}
			    		if(i == deviceList.size()) {
			    			deviceList.add(temp1);
			    			saveDabaBases();
			    			if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
			    				displayListView();
			    			}
			    			else {
			    				displayThumbView();
			    			}
			    		}
			    	}
			    }
			 }
		);
		builder.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    
			    }
			}
		);
		return builder.create();
	}

	private Dialog buildPasswdEntryDialog(Context context, Bundle data) {
		LayoutInflater inflater = LayoutInflater.from(this);
		String user = data.getString("user");
		String password = data.getString("password");
		open_path = data.getString("pathname");
		final View textEntryView = inflater.inflate(R.layout.dialog_passwd_text_entry, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String title = context.getResources().getString(R.string.user_info) 
		    + "\n" + open_path;
		builder.setTitle(title);
		builder.setView(textEntryView);
		user_edit = (EditText)textEntryView.findViewById(R.id.user_id);
		passwd_edit = (EditText)textEntryView.findViewById(R.id.password_id);
		mCheckBox = (CheckBox)textEntryView.findViewById(R.id.check_save);
		if(user != null)
			user_edit.setText(user);
		if(password != null)
			passwd_edit.setText(password);
		builder.setPositiveButton(R.string.confirm,
		    new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	String user = user_edit.getText().toString();
			    	String password = passwd_edit.getText().toString();
					String mtpath = null;
					boolean check = mCheckBox.isChecked();
					if(user_edit.length() <= 0) {
						mtpath = "mount " + open_path + " /mnt/NetShareDirs" 
						    + " username=guest";
					}
					else {
						Map<String, Object> data = new HashMap<String, Object>();
						if(passwd_edit.length()>0) {
							mtpath = "mount " + open_path + " /mnt/NetShareDirs" 
							    + " username=" + user +",password=" + password;
							if(check) {
								data.put(DBOpenHelper.NAME, open_path);
								data.put(DBOpenHelper.USER, user);
								data.put(DBOpenHelper.PASSWORD, password);
								data.put(DBOpenHelper.IS_SELECTED, MARK);
								mDataBases.update(data);
							}
						} 
						else {
							mtpath = "mount " + open_path + " /mnt/NetShareDirs" 
							    + " username=" + user;
							if(check) {
								data.put(DBOpenHelper.NAME, open_path);
								data.put(DBOpenHelper.USER, user);
								data.put(DBOpenHelper.IS_SELECTED, MARK);
								mDataBases.update(data);
							}
						}
						
					}
					if(MntInfo.SmbMount(mtpath) >= 0) {
						Intent intent = new Intent();
						intent.setClass(NetDeviceBrowser.this, NetFileBrowser.class);
						startActivity(intent);
						finish();
					}
					else {
						showDialog(DIALOG_ERROR_NET_RES, null);
					}
			    }
			 }
		);
		builder.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    
			    }
			}
		);
		return builder.create();
	}
	/*
	private Dialog buildIpDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(this.getResources().getString(R.string.nocomputer));
		builder.setPositiveButton(this.getResources().getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				});
		return builder.create();
	}
	*/
	private void createSearchTimer() {
		stopSearchTime();
		searchTimer = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				searchHandler.sendMessage(msg);
				
			}
		};
		searchTimer.schedule(task, 0, 2000);
	}
	
	private void stopSearchTime() {
		if(searchTimer != null) {
			searchTimer.cancel();
			searchTimer = null;
		}
	}
	private void stopSearchDialog() {	
		if(progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}
	
    private SimpleAdapter getDeviceAdapter(ArrayList<String> list, String type) {
    	int resid = type.equals(NetBrowserOp.LIST)?R.layout.devicelist_item:R.layout.devicegrid_item;
        return new SimpleAdapter(NetDeviceBrowser.this,
        		getDeviceData(list, type),
        		resid,        		
                new String[]{"item_type", "item_name", "item_sel"},        		
                new int[]{R.id.item_type, R.id.item_name, R.id.item_sel,});
    }

    private List<Map<String, Object>> getDeviceData(ArrayList<String> list, String type) {    	
    	List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();  
    	int resid = type.equals(NetBrowserOp.LIST)?R.drawable.item_type_networkip:R.drawable.item_preview_networkip;
    	try {
    		int count = list.size();
    		for(int i = 0; i<count; i++) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("item_type", resid);
    			String name = list.get(i);
    			Log.d(TAG, "getDeviceData, name:"+name);
    			map.put("item_name", name);
    			if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
        			if(mDataBases.getMark(name) == MARK)
        				map.put("item_sel", R.drawable.item_img_sel);
        			else
        				map.put("item_sel", R.drawable.item_img_unsel);
    			}
    			else
    				map.put("item_sel", R.drawable.item_img_unsel);
    			lists.add(map);
    		}
    		updatePathShow(new String(getResources().getString(R.string.device_list)));
    	} 
    	catch (Exception e) {
    		Log.e(TAG, "Exception when getDeviceListData(): ", e);
    		return lists;
		}   
    	if (!lists.isEmpty()) {    	
    		Collections.sort(lists, new Comparator<Map<String, Object>>() {
				
				public int compare(Map<String, Object> object1,
						Map<String, Object> object2) {	
					return ((String) object1.get("item_name")).compareTo((String) object2.get("item_name"));					
				}    			
    		});
    	}
    	return lists;
 	}  
    
    private void updatePathShow(String path) {      	
        if(mPath == null)
        	return;
        mPath.setText(path);   	
    }
    
	private void displayThumbView() {
		mGridView.setAdapter(getDeviceAdapter(deviceList, NetBrowserOp.THUMB));
	}
	
	private void updateThumbView() {
		((BaseAdapter) mGridView.getAdapter()).notifyDataSetChanged();
	}
	
	private void displayListView() {
		mListView.setAdapter(getDeviceAdapter(deviceList, NetBrowserOp.LIST));
	}
	
	private void updateListView() {
		((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
	}
	
	private void saveDabaBases() {
		mDataBases.deleteAll();
		int count = deviceList.size();
		for(int i = 0; i<count; i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(DBOpenHelper.NAME, deviceList.get(i));
			data.put(DBOpenHelper.IS_SELECTED, UNMARK);
			mDataBases.save(data);
		}
	}
	
	private Handler searchHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case 0:
					int tmpnum = MntInfo.SmbGetNum();
					if(tmpnum > 0) {
						searchCount = 0;
						stopSearchTime();
						stopSearchDialog();
						deviceList = MntInfo.GetListNum();
						saveDabaBases();
						if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
							displayListView();
						}
						else {
							displayThumbView();
						}
					}
					else {
						searchCount++;
						if(searchCount >= 60) {
							stopSearchTime();
							stopSearchDialog();
							deviceList = new ArrayList<String>();
							showDialog(inputIpDialog, null);
						}
					}
					break;
				default:
					break;
			}
		}
	};
}