package com.amlogic.netfilebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amlogic.netfilebrowser.smbmnt.SmbClientMnt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class NetFileBrowser extends Activity {
	
	private static final String TAG = "NetFileBrowser";
	private String MOUNTPATH = "/mnt/NetShareDirs";
	private SmbClientMnt MntInfo = null;
	private NetBrowserOp mNetBrowserOp = null;
	private String curPath = null;
	private String prevPath = null;
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
	private AlertDialog sort_dialog = null;
	private AlertDialog help_dialog = null;
	private final int DIALOG_SORT = 1;
	private final int DIALOG_HELP = 2;
	
	public void onDestroy() {
		MntInfo.SmbUnMount("umount /mnt/NetShareDirs");
		super.onDestroy();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        MntInfo = new SmbClientMnt();
		mNetBrowserOp = new NetBrowserOp();
		curPath = MOUNTPATH;
		prevPath = MOUNTPATH;
        if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST)) {
        	initListView();
        }
        else {
        	initThumbView();
        }
        
        initTopBar();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	protected Dialog onCreateDialog(int id) {
		switch (id) {
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
    	}
    	return list;
    }
    
	private void initTopBar() {
        btn_home = (Button) findViewById(R.id.btn_home);  
        btn_home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!curPath.equals(MOUNTPATH)) {
					curPath = MOUNTPATH;
					prevPath = MOUNTPATH;
					if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST))
						displayListView();
					else
						displayThumbView();
				}
				else {
		    		Intent intent = new Intent();
		    		intent.setClass(NetFileBrowser.this, NetDeviceBrowser.class);
		    		startActivity(intent);
		    		finish();
				}
			}        	
        });   
        
        btn_browse_scan = (Button) findViewById(R.id.btn_browse_scan); 
        if(mNetBrowserOp.getActType().equals(NetBrowserOp.SCAN)) {
        	btn_browse_scan.setBackgroundResource(R.drawable.btn_scan);
        }
        else {
        	btn_browse_scan.setBackgroundResource(R.drawable.btn_browse);
        }
        btn_browse_scan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mNetBrowserOp.setActType(NetBrowserOp.SCAN);
				Intent intent = new Intent();
				intent.setClass(NetFileBrowser.this, NetDeviceBrowser.class);
				startActivity(intent);
				finish();
			}        	
        });

        btn_sort = (Button) findViewById(R.id.btn_sort);  
        btn_sort.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_SORT);
			}        	
        });
        
        btn_parent = (Button) findViewById(R.id.btn_parent);  
        btn_parent.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!curPath.equals(MOUNTPATH)) {
					File file = new File(curPath);
					String parent_path = file.getParent();
					prevPath = curPath;
					curPath = parent_path;
					if(mNetBrowserOp.getDispMode().equals(NetBrowserOp.LIST))
						displayListView();
					else
						displayThumbView();
				}
				else {
		    		Intent intent = new Intent();
		    		intent.setClass(NetFileBrowser.this, NetDeviceBrowser.class);
		    		startActivity(intent);
		    		finish();
				}
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
        mListView.setAdapter(getFileAdapter(curPath, NetBrowserOp.LIST));
        mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
			
				String file_path = (String) item.get("file_path");
				File file = new File(file_path);
				if(!file.exists()){
					return;
				}

				if (file.isDirectory()) {	
					prevPath = curPath;
					curPath = file_path;
					displayListView();	
				}
				else {	
					openFile(file);
				}				
			}        	
        });
	}

	private void initThumbView() {
        setContentView(R.layout.netbrowser_thumb);
        mPath = (TextView) findViewById(R.id.thumb_path);  
        mGridView = (GridView) findViewById(R.id.mygridview);
        mGridView.setAdapter(getFileAdapter(curPath, NetBrowserOp.THUMB));
        mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
				
				String file_path = (String) item.get("file_path");
				File file = new File(file_path);
				if(!file.exists()){
					return;
				}
				
				if (file.isDirectory()) {	
					prevPath = curPath;
					curPath = file_path;
					displayThumbView();	
				}
				else {	
					openFile(file);				
				}        	
	        }   
        });
	}

    private void updatePathShow(String path) {      	
        if(mPath == null)
        	return;
        mPath.setText(path);   	
    }
    
	private void displayThumbView() {
		mGridView.setAdapter(getFileAdapter(curPath, NetBrowserOp.THUMB));
	}
	
	private void updateThumbView() {
		((BaseAdapter) mGridView.getAdapter()).notifyDataSetChanged();
	}
	
	private void displayListView() {
		mListView.setAdapter(getFileAdapter(curPath, NetBrowserOp.LIST));
	}
	
	private void updateListView() {
		((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
	}
	
    private SimpleAdapter getFileAdapter(String path, String type) {
    	int resid = type.equals(NetBrowserOp.LIST)?R.layout.devicelist_item:R.layout.devicegrid_item;
        return new SimpleAdapter(NetFileBrowser.this,
        		getFileData(path, type),
        		resid,        		
                new String[]{"item_type", "item_name", "item_sel"},        		
                new int[]{R.id.item_type, R.id.item_name, R.id.item_sel,});
    }

    private List<Map<String, Object>> getFileData(String path, String type) {    	
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
    	int resid = type.equals(NetBrowserOp.LIST)?
    			R.drawable.item_type_dir:R.drawable.item_preview_dir;
    	try {
    		File file_path = new File(path); 
        	if (file_path != null && file_path.exists()) { 
        		File[] files = file_path.listFiles();
        		if (files != null) {
            		if (files.length > 0) {
            			Arrays.sort(files, new MyComparator(MyComparator.NAME_ASCEND));
            			for (File file : files) {    					
            	        	Map<String, Object> map = new HashMap<String, Object>();    		        	
            	        	map.put("item_name", file.getName());   
            	        	String file_abs_path = file.getAbsolutePath();
            	        	map.put("file_path", file_abs_path);
            	        	
            	        	if (file.isDirectory()) {
            	        		map.put("item_sel", R.drawable.item_img_unsel);
            	        		map.put("item_type", resid);        	        		
            	        	} 
            	        	else {
            	        		map.put("item_sel", R.drawable.item_img_unsel); 
            	        		map.put("item_type", getFileTypeImg(file.getName(), type));
            	        	}
            	        	if(!file.isHidden()){
            	        		list.add(map);
            	        	}
            			}
            		}            		
        		}
        		updatePathShow(path);
        	}
    	} 
    	catch (Exception e) {
    		Log.e(TAG, "Exception when getDeviceListData(): ", e);
    		return list;
		}
    	/*
    	if (!list.isEmpty()) {    	
    		Collections.sort(list, new Comparator<Map<String, Object>>() {
				
				public int compare(Map<String, Object> object1,
						Map<String, Object> object2) {	
					return ((String) object1.get("item_name")).compareTo((String) object2.get("item_name"));					
				}    			
    		});
    	}
    	*/
    	return list;
 	}  
    
	public boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for(String aEnd : fileEndings) {
			if(checkItsEnd.toLowerCase().endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	private int getFileTypeImg(String name, String type) {
		int resid = type.equals(NetBrowserOp.LIST)?
				R.drawable.item_type_file:R.drawable.item_preview_file;
		
		if(checkEndsWithInStringArray(name, 
				getResources().getStringArray(R.array.Video))) {
			resid = type.equals(NetBrowserOp.LIST)?
					R.drawable.item_type_video:R.drawable.item_preview_video;
		}
		else if(checkEndsWithInStringArray(name, 
				getResources().getStringArray(R.array.Audio))) {
			resid = type.equals(NetBrowserOp.LIST)?
					R.drawable.item_type_music:R.drawable.item_preview_music;
		}
		else if(checkEndsWithInStringArray(name, 
				getResources().getStringArray(R.array.Picture))) {
			resid = type.equals(NetBrowserOp.LIST)?
					R.drawable.item_type_photo:R.drawable.item_preview_photo;
		}
		else if(checkEndsWithInStringArray(name, 
				getResources().getStringArray(R.array.Text))) {
			resid = type.equals(NetBrowserOp.LIST)?
					R.drawable.item_type_text:R.drawable.item_preview_text;
		}
		return resid;
	}

    /** get file type op*/
    public static boolean isVideo(String filename) {     
    	String name = filename.toLowerCase();
        for (String ext : video_extensions) {
            if (name.endsWith(ext))
                return true;
        }
        return false;
    }
    public static boolean isMusic(String filename) {  
    	String name = filename.toLowerCase();
        for (String ext : music_extensions) {
            if (name.endsWith(ext))
                return true;
        }
        return false;
    }
    public  static boolean isPhoto(String filename) {   
    	String name = filename.toLowerCase();
        for (String ext : photo_extensions) {
            if (name.endsWith(ext))
                return true;
        }
        return false;
    } 
    public  static boolean isApk(String filename) {   
    	String name = filename.toLowerCase();        
        if (name.endsWith(".apk"))
            return true;
        return false;
    }     
    /* file type extensions */
    //video from layer
    public static final String[] video_extensions = { ".3gp",
        ".divx",
        ".h264",
        ".avi",
        ".m2ts",
        ".mkv",
        ".mov",
        ".mp2",
        ".mp4",
        ".mpg",
        ".mpeg",
        ".rm",
        ".rmvb",
        ".wmv",
        ".ts",
        ".tp",
        ".dat",
        ".vob",
        ".flv",
        ".vc1",
        ".m4v",
        ".f4v",
        ".asf",
        ".lst",
       /* "" */
    };
    //music
    private static final String[] music_extensions = { ".mp3",
    	".wma",
    	".m4a",
    	".aac",
    	".ape",
    	".ogg",
    	".flac",
    	".alac",
    	".wav",
    	".mid",
    	".xmf",
    	".mka",
    	".pcm",
    	".adpcm"
    };
    //photo
    private static final String[] photo_extensions = { ".jpg",
    	".jpeg",
    	".bmp",
    	".tif",
    	".tiff",
    	".png",
    	".gif",
    	".giff",
    	".jfi",
    	".jpe",
    	".jif",
    	".jfif"
    };		
    public static String CheckMediaType(File file){
        String typeStr="application/*";
        String filename = file.getName();
        
        if (isVideo(filename))
        	typeStr = "video/*";
        else if (isMusic(filename))
        	typeStr = "audio/*";
        else if (isPhoto(filename))
        	typeStr = "image/*";
        else if (isApk(filename))
        	typeStr = "application/vnd.android.package-archive";
        else
        	typeStr = "application/*";
       Log.d(TAG, "CheckMediaType, type:"+typeStr);
       return typeStr;
    }
    
	protected void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = "*/*";        
        type = CheckMediaType(f);
        intent.setDataAndType(Uri.fromFile(f),type);
        startActivity(intent);      		
	}
}
