package com.example.Upgrade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.io.FilenameFilter;
import java.util.regex.Pattern;
import android.os.Environment;
import android.widget.ProgressBar;
import android.os.PowerManager;
import android.widget.LinearLayout;
import android.content.res.Resources;
import android.widget.Button;
import android.os.RecoverySystem;
//import android.view.View.OnFocusChangeListener;
//import android.view.View.OnTouchListener;



public class UpgradeActivity extends Activity {
	private final static String TAG = "UpgradeActivity";
	public static final boolean DEBUG = true;
	private final static int LIST_MAIN = 0;
	private final static int LIST_LOCAL = 1;
	private final static int LIST_NET = 2;
	private final static int LIST_CONFIRM = 3;
	
	private ListView mListCenter;
	private TextView mTextBottom;
	private int mListType;
	private FileUtils mFileUtils;
	private String mRecoveryPath;
	
	private UpdateHttpClient mUpdateHttpClient = null;
	private ProgressDialog myDialog = null;
	private Thread cThread,sThread,lThread,tThread;
	private String DownloadUrl = null;
	private String DownloadName = null;
	private String DownloadSize = null;
	private static String DownloadTime = null;
	private String flash_path = null;
	private long FileSizeInB = 0;
	private long CurrentSize = 0;
	private String MD5 = null;
	private static String downloadfilename="null";
	private static String downloadpath="null";
	private boolean flag = true;
	private boolean downloading_flag = true;
	private int Progress = 0;
	private TextView mSizeText  = null;
	private ProgressBar mProgressBar = null;
	private TextView mProgressText = null;
	private PowerManager.WakeLock mScreenLock = null;
	
	
	
	protected static final int GUI_STOP_CONNECT_CONNECT_SERVER_FAIL = 0x108;
	protected static final int GUI_STOP_CONNECT_CONNECT_SERVER_SUCCES = GUI_STOP_CONNECT_CONNECT_SERVER_FAIL+0x1;
	protected static final int GUI_STOP_CONNECT_DOWNLOAD_START = GUI_STOP_CONNECT_CONNECT_SERVER_SUCCES+0x1;
	protected static final int GUI_STOP_CONNECT_DOWNLOADING = GUI_STOP_CONNECT_DOWNLOAD_START+0x1;
	protected static final int GUI_STOP_CONNECT_DOWNLOAD_FAIL = GUI_STOP_CONNECT_DOWNLOADING+0x1;
	protected static final int GUI_STOP_CONNECT_DOWNLOAD_END = GUI_STOP_CONNECT_DOWNLOAD_FAIL+0x1;
	protected static final int GUI_STOP_REBOOT = GUI_STOP_CONNECT_DOWNLOAD_END+0x1;
	
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        mFileUtils = new FileUtils();
        mUpdateHttpClient = new UpdateHttpClient();
        
        mTextBottom = (TextView) findViewById(R.id.text_bottom);
        mListCenter = (ListView) findViewById(R.id.list_center);
        mListCenter.setAdapter(newListAdapter(LIST_MAIN));
        
        mListCenter.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String, Object> item = (Map<String, Object>)arg0.getItemAtPosition(arg2);
				
				if (mListType == LIST_MAIN) {
				if(DEBUG) Log.d(TAG,"onItemClick mListType == LIST_MAIN!");
					String item_type = (String) item.get("item_type");					
					if (item_type.equals("local"))
						mListCenter.setAdapter(newListAdapter(LIST_LOCAL));
					// FIXME: TODO OTA upgrade	
					else if (item_type.equals("net"))
					{	
						ConnectServer();// mListCenter.setAdapter(newListAdapter(LIST_NET));		 				
					}
					
				} else if (mListType == LIST_LOCAL) {
					if (item.get("item_icon").equals(R.drawable.item_icon_back)) {
						mListCenter.setAdapter(newListAdapter(LIST_MAIN));
					} else {
						mRecoveryPath = (String) item.get("item_recovery_path");
						if(DEBUG) Log.d(TAG,"mRecoveryPath="+mRecoveryPath);					
						mListCenter.setAdapter(newListAdapter(LIST_CONFIRM));
					}
				} else if (mListType == LIST_NET) {
					if(DEBUG) Log.d(TAG,"onItemClick mListType == LIST_NET!");	
					if (item.get("item_icon").equals(R.drawable.item_icon_back)) {
						mListCenter.setAdapter(newListAdapter(LIST_MAIN));
					} else {
						//mRecoveryPath = (String) item.get("item_recovery_path");
						if(DEBUG) Log.d(TAG,"onItemClick net update CONFIRM!");	
						CheckFreeSpace();
	                    
					}				
				} else if (mListType == LIST_CONFIRM) {
					if (item.get("item_icon").equals(R.drawable.item_icon_back)) {
						mListCenter.setAdapter(newListAdapter(LIST_LOCAL));
					} else {
						if(DEBUG) Log.d(TAG,"mRecoveryPath="+mRecoveryPath);					
						RebootUtils.rebootInstallPackage(UpgradeActivity.this, new File(mRecoveryPath));
					}
				}
			}        	
        });
    }
    
    private SimpleAdapter newListAdapter(int list_type) {
    	if (list_type == LIST_MAIN) {
			return new SimpleAdapter(UpgradeActivity.this, 
					getListData(list_type), 
					R.layout.list_item, 
					new String[]{
						"item_icon",
						"item_name",
						"item_icon2"
					}, 
					new int[]{
						R.id.item_icon,
						R.id.item_name,
						R.id.item_icon2
					});    	
    	} else {
    		return new SimpleAdapter(UpgradeActivity.this, 
    				getListData(list_type), 
    				R.layout.list_item2, 
    				new String[]{
    					"item_icon",
    					"item_name",
    					"item_path",
    					"item_date",
    					"item_icon2"
    				}, 
    				new int[]{
    					R.id.item_icon,
    					R.id.item_name,
    					R.id.item_path,
    					R.id.item_date,
    					R.id.item_icon2
    				});     		
    	}
    }
    
    private List<Map<String, Object>> getListData(int list_type) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;
    	if (list_type == LIST_MAIN) {
    		if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_upgrade)) {
		    	map = new HashMap<String, Object>();
		    	map.put("item_type", "local");
		    	map.put("item_icon", R.drawable.item_icon_def);
		    	map.put("item_name", getText(R.string.upgrade_local));
		    	map.put("item_icon2", R.drawable.item_icon2_def);    	
		    	list.add(map);
    		}
	    	
    		if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_net_upgrade)) {
		    	if (NetworkUtils.isNetworkUp(UpgradeActivity.this)) {
			    	map = new HashMap<String, Object>();
			    	map.put("item_type", "net");
			    	map.put("item_icon", R.drawable.item_icon_def);
			    	map.put("item_name", getText(R.string.upgrade_net));
			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
			    	list.add(map);    	
		    	}
    		}
    		mTextBottom.setText(getText(R.string.info_page_main));
    		
    	} else if (list_type == LIST_NET) {
	    	map = new HashMap<String, Object>();
	    	try{
	    		map.put("item_icon", R.drawable.item_icon_def);
	    		map.put("item_name", downloadfilename);
	    		map.put("item_path", downloadpath);
	    		map.put("item_date", DownloadTime);
	    		map.put("item_icon2", R.drawable.item_icon2_def);
	    		list.add(map);
	    	}
	    	catch(Exception e)
	    	{
	    		if(DEBUG) Log.d(TAG,"map.put error!");	
	    	}
	    	list.addAll(mFileUtils.getListDataBack(UpgradeActivity.this, getString(R.string.back_back)));    
	    	mTextBottom.setText(getText(R.string.info_page_net));
	    	
    	} else if (list_type == LIST_LOCAL){    
    		if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_check_sdcard)) {    			
    	
    			list.addAll(mFileUtils.getListDataSd(UpgradeActivity.this));
    		}
			if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_check_flash)) {
					if(!((Environment.getInternalStorageDirectory()+"/").equals(FileUtils.SD_PARH)&&
						ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_check_sdcard))){	
						list.addAll(mFileUtils.getListDataNand(UpgradeActivity.this));
	    			}
			}
	    	
	    	if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_check_usb)) {
	    		list.addAll(mFileUtils.getListDataUsb(UpgradeActivity.this));
	    	}	
	    	
	    	if (ConfigUtils.getBooleanConfig(UpgradeActivity.this, R.bool.config_local_check_sata)) {
	    		list.addAll(mFileUtils.getListDataSata(UpgradeActivity.this));   		
	    	}	 
	    	
	    	if (list.isEmpty()) {
	    		list.addAll(mFileUtils.getListDataBack(UpgradeActivity.this, getString(R.string.back_no_package)));
	    		mTextBottom.setText(getText(R.string.info_page_local_null));
	    	} else {
	    		list.addAll(mFileUtils.getListDataBack(UpgradeActivity.this, getString(R.string.back_back)));
	    		mTextBottom.setText(getText(R.string.info_page_local));
	    	}
	    	
    	} else if (list_type == LIST_CONFIRM){
    		list.addAll(mFileUtils.getListDataNext(UpgradeActivity.this, getString(R.string.next_install_package)));
    		list.addAll(mFileUtils.getListDataBack(UpgradeActivity.this, getString(R.string.back_back)));
    		mTextBottom.setText(getText(R.string.info_page_confirm));
    		
    	}
    	
    	mListType = list_type; 	    	
    	return list;
    }  

    private void ConnectServer() {
	    final CharSequence strDialogTitle = getString(R.string.str_dialog_title);
	    final CharSequence strDialogBody = getString(R.string.str_dialog_body);
	    if(DEBUG) Log.d(TAG,"ConnectServer!");	  
	    myDialog = ProgressDialog.show(UpgradeActivity.this, strDialogTitle, strDialogBody, true);
	    cThread = new Thread(new Runnable() {
	    	public void run() {
	    		Message m = null;
	    		
	    		if(mUpdateHttpClient.connectServerByURL() == false) {
	    			/*connect server fail*/
	    			if(DEBUG) Log.d(TAG,"Connect server fail!");
	    			m = new Message();
	    			m.what = UpgradeActivity.GUI_STOP_CONNECT_CONNECT_SERVER_FAIL;
	    			UpgradeActivity.this.myMessageHandler.sendMessage(m);
					return ;
	    		}
	    		List<String> names = new ArrayList<String>();
	    		List<String> values = null;
	    		String url = new String(mUpdateHttpClient.URL+"GetSWUpgradeInfo");
	    		
	    		if(DEBUG) Log.d(TAG,"ConnectServer:"+url);
	    		//http://10.28.8.140:8080/PanHub.RDS.Lib.svc/GetSWUpgradeInfo
	    		names.clear();
	    		names.add(PullParserXml.DOWNLOADURL);
	    		names.add(PullParserXml.DOWNLOADNAME);
	    		names.add(PullParserXml.DOWNLOADMD5);
	    		names.add(PullParserXml.DOWNLOADSIZEINB);
	    		names.add(PullParserXml.DOWNLOADFILETIME);
	    		
	    		values = mUpdateHttpClient.getStringFromServer(url,names);
	    		if(DEBUG) Log.d(TAG,"download url:"+values);
	    		if(values == null) {
	    			if(DEBUG) Log.d(TAG,"Can not get download url!");
	    			m = new Message();
	    			m.what = UpgradeActivity.GUI_STOP_CONNECT_CONNECT_SERVER_FAIL;
	    			UpgradeActivity.this.myMessageHandler.sendMessage(m);
	    			return ;
	    		}
	    		DownloadName = values.get(1);
	    		DownloadUrl = mUpdateHttpClient.URL+"Download/"+DownloadName;//values.get(0);
	    		MD5 = values.get(2);
	    		DownloadSize = values.get(3);
	    		DownloadTime = values.get(4);
	    		
	    		if((DownloadUrl == null) || (DownloadName == null) || (MD5 == null) || (DownloadSize == null)) {
	    			if(DEBUG) Log.d(TAG,"Can not get download url, or get name, or get md5, or get size!");
	    			m = new Message();
	    			m.what = UpgradeActivity.GUI_STOP_CONNECT_CONNECT_SERVER_FAIL;
	    			UpgradeActivity.this.myMessageHandler.sendMessage(m);
					return ;
	    		}
				if(DownloadUrl.contains(DownloadName) == false) {
				    int index = DownloadUrl.lastIndexOf("/")+1;
				    if(DEBUG) Log.d(TAG, Integer.toString(index));
				    DownloadName = DownloadUrl.substring(index);
				    if(DownloadName == null)
					    return ;
				    if(DEBUG) Log.d(TAG, DownloadName);
				}
	    		FileSizeInB = Integer.parseInt(DownloadSize);
	    		downloadfilename =DownloadName;
	    		downloadpath =DownloadUrl;
				//connect success.
				if(DEBUG) Log.d(TAG,"connect success!");
				m = new Message();
    			m.what = UpgradeActivity.GUI_STOP_CONNECT_CONNECT_SERVER_SUCCES;
    			UpgradeActivity.this.myMessageHandler.sendMessage(m);
    			if(DEBUG) Log.d(TAG,"************cThread close");
				return ;
	    	}
	    });
	    cThread.start();
	}
	Handler myMessageHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			    case GUI_STOP_CONNECT_CONNECT_SERVER_FAIL:
			    	myDialog.dismiss();
					//new AlertDialog.Builder(UpgradeActivity.this).setMessage(getString(R.string.str_dialog_fail)).create().show();
			    	Toast.makeText(UpgradeActivity.this, getString(R.string.str_dialog_fail), Toast.LENGTH_SHORT).show(); 
			    	break;
			    case GUI_STOP_CONNECT_CONNECT_SERVER_SUCCES:
				    myDialog.dismiss();
				    mListCenter.setAdapter(newListAdapter(LIST_NET));
			    	break;
				case GUI_STOP_CONNECT_DOWNLOAD_START:
					Progress = 0;
					mProgressBar.setProgress(Progress);
					break;
				case GUI_STOP_CONNECT_DOWNLOADING:
					ProgressValue pv = (ProgressValue)msg.obj;
					int pro = pv.getProgress();
					long size = pv.getSize();
					mSizeText.setText(new String(Long.toString(size)+"/"+DownloadSize+"B"));
					if(Progress < pro && pro < 100) {
						Progress = pro;
						mProgressBar.setProgress(Progress);
						mProgressText.setText(new String(Integer.toString(Progress)+"%"));
					}
					else if(pro >= 100) {
						flag = false;
					}
					break;
				case GUI_STOP_CONNECT_DOWNLOAD_FAIL:
					Log.i(TAG, "Downloading fail or download file is error!");
					try{
						TextView download_status = (TextView)findViewById(R.id.download_status);
						download_status.setText(getString(R.string.download_fail_note));
					}
					catch(Exception e)
					{
						Log.w(TAG, "Display downloading fail string!");
					}
					if(lThread != null) {
						flag = false;
						lThread = null;
					}
					if(sThread != null) {
						sThread = null;
					}
					if(tThread != null) {
						flag = false;
						tThread = null;
					}
					//openScreenOffTimeout();
			    	Toast.makeText(UpgradeActivity.this, getString(R.string.download_fail_note), Toast.LENGTH_SHORT).show(); 
					break;
				case GUI_STOP_CONNECT_DOWNLOAD_END:
					Log.i(TAG, "Dowmload end! Next will check download file md5.");
					if(lThread != null) {
						flag = false;
						lThread = null;
					}
					if(tThread != null) {
						flag = false;
						tThread = null;
					}
					mSizeText.setText(new String(DownloadSize+"/"+DownloadSize+"KB"));
					mProgressText.setText("100%");
					break;
				case GUI_STOP_REBOOT:
					if(sThread != null) {
						sThread = null;
					}
					//openScreenOffTimeout();
	                try {
	                    RebootUtils.rebootInstallPackage(UpgradeActivity.this,new File(mRecoveryPath+FileUtils.FW_NAME));
	                } catch (Exception e) {
	                    Log.e(TAG, e.getMessage().toString());
	                }  
					break;
			} 
	      super.handleMessage(msg); 
	    }
	  };
	private void CheckFreeSpace() {
		long free_size = 0;
		flash_path ="null";
		/*there has sd or usb card insert,and it can write and read*/
		File dir = new File("/mnt");
    	String regex = ".+\\.[zZ][iI][pP]";
    	String regex_usb = "sd[a-z]([0-9])*";
    	String regex_usb2 = "sd[a-z]([0-9])*";
    	if (dir.exists() && dir.isDirectory()) {
    		File[] files = dir.listFiles(new MyFilenameFilter(regex_usb));		
    		if (files != null && files.length > 0) {
    			for (File file : files) {
    				if (file.exists() && file.isDirectory()) {
		    			flash_path=file.getAbsolutePath();  
		    			mRecoveryPath=ConfigUtils.getStringConfig(UpgradeActivity.this, R.string.config_recovery_usb_path);
		    			mRecoveryPath=mRecoveryPath+"/"; 
    					File[] files2 = file.listFiles(new MyFilenameFilter(regex_usb2));	
    					if (files2 != null && files2.length > 0) {
    						for (File file2 : files2) {
    							if (file2.exists() && file2.isDirectory()) 
    							{
    				    			flash_path=file2.getAbsolutePath();  
    				    			mRecoveryPath=ConfigUtils.getStringConfig(UpgradeActivity.this, R.string.config_recovery_usb_path);
    				    			mRecoveryPath=mRecoveryPath+"/"; 								
    							}
    						}
    					}
    				}
    			}
    		}
    	}
		if(DEBUG) Log.d(TAG,"************************flash path:"+ flash_path); // /mnt/sda/sda or /mnt/sda/sda1
		
		if(flash_path!="null")
		{
			flash_path=flash_path+"/";
		}
		else if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
		{
			flash_path=FileUtils.SD_PARH;
			mRecoveryPath=ConfigUtils.getStringConfig(UpgradeActivity.this, R.string.config_recovery_sdcard_path);
			mRecoveryPath=mRecoveryPath+"/";
		}
		else {
			flash_path=FileUtils.INTERNAL_MEMORY_PATH;
			mRecoveryPath=ConfigUtils.getStringConfig(UpgradeActivity.this, R.string.config_recovery_flash_path);
			mRecoveryPath=mRecoveryPath+"/";
		}
		if(DEBUG) Log.d(TAG,"************************flash path:"+ flash_path);
		//			free_size = mFileUtils.getFreeSpaceInKB(FileUtils.SD_PARH);
		try{
				free_size = mFileUtils.getFreeSpaceInB(flash_path);
			}
		catch(Exception e){
			if(DEBUG) Log.d(TAG,"get free_size error.");
			}	
		if(free_size >= FileSizeInB) {
			mFileUtils.remove(flash_path, FileUtils.FW_NAME, FileUtils.FW_BACKUP_NAME);
			Downloading();
		}
		else {
			// no freesize for download file.
			Toast.makeText(UpgradeActivity.this, getString(R.string.str_no_freesize), Toast.LENGTH_SHORT).show(); 
		}
	}
private class MyFilenameFilter implements FilenameFilter {
		private Pattern p;
		
		public MyFilenameFilter(String regex) {
			p = Pattern.compile(regex);
		}

		public boolean accept(File file, String name) {
			return p.matcher(name).matches();
		}
		
}

private void Downloading() {
		setContentView(R.layout.downloading);
		LinearLayout layout = (LinearLayout)findViewById(R.id.downloading);
		
		Button exit = (Button)layout.findViewById(R.id.exit);
		exit.requestFocus();
        exit.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v) {
        		if(lThread != null) {
        			flag = false;
        			lThread = null;
        		}
        		if(tThread != null) {
					flag = false;
					tThread = null;
				}
				mFileUtils.delete(flash_path, FileUtils.FW_NAME);
        		UpgradeActivity.this.finish();
        		android.os.Process.killProcess(android.os.Process.myPid());
        	}
        });
		// just net status

        mProgressBar = (ProgressBar)layout.findViewById(R.id.progress_bar);
        mProgressText = (TextView)layout.findViewById(R.id.progress);
        mProgressText.setText(new String("0%"));
        
        mSizeText = (TextView)layout.findViewById(R.id.size);
        mSizeText.setText(new String("0/"+DownloadSize+"KB"));
        
        Resources resources = getBaseContext().getResources();
        String text1 = resources.getString(R.string.downloading_text1);
        String text2 = resources.getString(R.string.downloading_text2);
        		
        TextView source_path = (TextView)layout.findViewById(R.id.source_path);
        source_path.setText(new String(text1 + "/FW Name/" + DownloadName));
        
        TextView target_path = (TextView)layout.findViewById(R.id.target_path);
        
//        closeScreenOffTimeout();
		target_path.setText(new String(text2+flash_path+FileUtils.FW_NAME));
		sThread = new Thread(new Runnable() {
				public void run() {
					Message m = null;
					//downloading
					m = new Message();
					m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOAD_START;
					UpgradeActivity.this.myMessageHandler.sendMessage(m);
					lThread = new Thread(new Runnable() {
						public void run() {
							while(flag) {
								try{
									Thread.sleep(2000);
									File file = new File(flash_path+FileUtils.FW_NAME);
									if(file.exists()) 
									{
										long file_size = file.length();
										int pro = (int)(file_size*100/FileSizeInB);
										if(DEBUG) Log.d(TAG,"*************lThread:"+pro+NetworkUtils.isNetworkUp(UpgradeActivity.this));
										Message m = new Message();
										m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOADING;
										m.obj = new ProgressValue(pro, file_size);
										UpgradeActivity.this.myMessageHandler.sendMessage(m);
										// just net status

									}
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
							if(DEBUG) Log.d(TAG,"************lThread close");
						}
					});
					lThread.start();
					
					//timer just net status
					tThread = new Thread(new Runnable() {
						public void run() {
							while(flag) {
								try{
									Thread.sleep(60000);
									// just net status
									if (!NetworkUtils.isNetworkUp(UpgradeActivity.this))
									{
										//mFileUtils.delete(flash_path, FileUtils.FW_NAME);
										Message m = null;
										m = new Message();
										m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOAD_FAIL;
										UpgradeActivity.this.myMessageHandler.sendMessage(m);
										if(DEBUG) Log.d(TAG,"***************no net");
										break;
									}
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
							if(DEBUG) Log.d(TAG,"************tThread close");
						}
					});
					tThread.start();
					
					if(DEBUG) Log.d(TAG,"************flash_path,url,downloadurl"+flash_path+FileUtils.FW_NAME+DownloadUrl);
					int tag = -1;
					while(flag&&downloading_flag){
						try
						{
							tag = mUpdateHttpClient.downFile(flash_path, FileUtils.FW_NAME, DownloadUrl ,CurrentSize ,FileSizeInB);
						}
						catch(Exception e)
		    			{
		    				if(DEBUG) Log.d(TAG,"mUpdateHttpClient.downFile:"+e);
		    				tag = -1;	
		    			}
						if(tag < 0) {
							mFileUtils.delete(flash_path, FileUtils.FW_NAME);
							m = new Message();
							m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOAD_FAIL;
							UpgradeActivity.this.myMessageHandler.sendMessage(m);
							if(DEBUG) Log.d(TAG,"tag:"+tag);
						}
						else 
						{
							if(DEBUG) Log.d(TAG,"tag:"+tag);
							
							//just filesize
							File file1 = new File(flash_path+FileUtils.FW_NAME);
							if(file1.exists()) 
							{
								CurrentSize = file1.length();
								if(CurrentSize < FileSizeInB)
									continue;
								else
									downloading_flag = false;
							}
							else{
								downloading_flag = false;
							}
							
							
							// just md5
							MD5 mMD5 = new MD5();
							String md5_str = mMD5.md5sum(flash_path+FileUtils.FW_NAME);
							if(md5_str.equalsIgnoreCase(MD5))
							{
								m = new Message();
								m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOAD_END;
								UpgradeActivity.this.myMessageHandler.sendMessage(m);
								m = new Message();
								m.what = UpgradeActivity.GUI_STOP_REBOOT;
								UpgradeActivity.this.myMessageHandler.sendMessage(m);
							}
							else
							{
								mFileUtils.delete(flash_path, FileUtils.FW_NAME);
								m = new Message();
								m.what = UpgradeActivity.GUI_STOP_CONNECT_DOWNLOAD_FAIL;
								UpgradeActivity.this.myMessageHandler.sendMessage(m);
							}
						}
					}
					if(DEBUG) Log.d(TAG,"************sThread close");
					
			}// sThread run
		});
	    sThread.start();
	}//downloading

public class ProgressValue {
		  private int progress;
		  private long size;
		  
		  public ProgressValue(int pro, long size) {
			  this.progress = pro;
			  this.size = size;
		  }
		  
		  public int getProgress() {
			  return this.progress;
		  }
		  
		  public long getSize() {
			  return this.size;
		  }
	  }
    private void closeScreenOffTimeout()
    {
    	if(mScreenLock.isHeld() == false)
    		mScreenLock.acquire();
    }
    
    private void openScreenOffTimeout()
    {
    	if(mScreenLock.isHeld() == true)
    		mScreenLock.release();
    }
    
}