package com.example.Upgrade;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.os.SystemProperties;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

import com.example.Upgrade.Download.ByteBuffer;

import android.os.StatFs;
import android.util.Log;

public class FileUtils {	
	private final static String TAG = "Upgrade.FileUtils";
	public final static String SD_PARH = "/mnt/sdcard/";
	public final static String INTERNAL_MEMORY_PATH = "/mnt/flash/";
	public final static String FW_NAME = "update.zip";
	public final static String FW_BACKUP_NAME = "update_backup.zip";
	public final static String SAVE_DIR_NAME = "OLD FIRMWARE";
	private static final int BUFFER_SIZE = 16*1024; 
	private File saveFile;
		

	public List<Map<String, Object>> getListDataSd(Context context) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;
    	String recovery_path = 
    		ConfigUtils.getStringConfig(context, R.string.config_recovery_sdcard_path);
    	if(Environment.getExternalStorage2State().equals(Environment.MEDIA_MOUNTED)){
			String recovery_path_external = 
    		ConfigUtils.getStringConfig(context, R.string.config_recovery_sdcard_external);
			File dir;
			dir = new File(Environment.getExternalStorage2Directory().getPath());
			String regex = ".+\\.[zZ][iI][pP]";
			   if (dir.exists() && dir.isDirectory()) {
    			File[] files = dir.listFiles(new MyFilenameFilter(regex));
    			if (files != null && files.length > 0) {
    				for (File file : files) {
    			    	map = new HashMap<String, Object>();
    			    	map.put("item_icon", R.drawable.item_icon_def);
    			    	map.put("item_name", file.getName());    			    	
    			    	map.put("item_path", file.getAbsolutePath());
    			    	map.put("item_recovery_path", recovery_path_external+ "/" + file.getName());
    			    	
    	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    	        			.format(new Date(file.lastModified()));    			    	
    			    	map.put("item_date", date);
    			    	
    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
    			    	list.add(map);    					
    				}
    			}
    		}

		}
    	else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		File dir;
			if(SystemProperties.getBoolean("virtualsd.enable", false)) {
    			dir = new File(Environment.getExternalStorage2Directory().getPath());
    			Log.d(TAG, "virtualsd.enable is true!!!! ");
    		}
    		else {
    			dir = new File(Environment.getExternalStorageDirectory().getPath());
    			Log.d(TAG, "virtualsd.enable is false!!!! ");
    		}
    		
    		String regex = ".+\\.[zZ][iI][pP]";
    		
    		if (dir.exists() && dir.isDirectory()) {
    			File[] files = dir.listFiles(new MyFilenameFilter(regex));
    			if (files != null && files.length > 0) {
    				for (File file : files) {
    			    	map = new HashMap<String, Object>();
    			    	map.put("item_icon", R.drawable.item_icon_def);
    			    	map.put("item_name", file.getName());    			    	
    			    	map.put("item_path", file.getAbsolutePath());
    			    	map.put("item_recovery_path", recovery_path + "/" + file.getName());
    			    	
    	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    	        			.format(new Date(file.lastModified()));    			    	
    			    	map.put("item_date", date);
    			    	
    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
    			    	list.add(map);    					
    				}
    			}
    		}
    		
    	} 
    	
		return list;
		
	}
	
	public List<Map<String, Object>> getListDataNand(Context context) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;	
    	String recovery_path = 
    		ConfigUtils.getStringConfig(context, R.string.config_recovery_flash_path);    	
    	
    	if (Environment.getInternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		File dir = new File(Environment.getInternalStorageDirectory().getPath());
    		String regex = ".+\\.[zZ][iI][pP]";
    		
    		if (dir.exists() && dir.isDirectory()) {
    			File[] files = dir.listFiles(new MyFilenameFilter(regex));
    			if (files != null && files.length > 0) {
    				for (File file : files) {
    			    	map = new HashMap<String, Object>();
    			    	map.put("item_icon", R.drawable.item_icon_def);
    			    	map.put("item_name", file.getName());
    			    	map.put("item_path", file.getAbsolutePath());
    			    	map.put("item_recovery_path", recovery_path + "/" + file.getName());
    			    	
    	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    	        			.format(new Date(file.lastModified()));    			    	
    			    	map.put("item_date", date);
    			    	
    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
    			    	list.add(map);    					
    				}
    			}
    		}
    		
    	} 
    	
		return list;
		
	}	
	
	public List<Map<String, Object>> getListDataUsb(Context context) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;	
    	String recovery_path = 
    		ConfigUtils.getStringConfig(context, R.string.config_recovery_usb_path);     	
    	
    	File dir = new File("/mnt");
    	String regex = ".+\\.[zZ][iI][pP]";
    	String regex_usb = "sd[a-z]([0-9])*";
    	String regex_usb2 = "sd[a-z]([0-9])*";
    	
    	if (dir.exists() && dir.isDirectory()) {
    		File[] files = dir.listFiles(new MyFilenameFilter(regex_usb));		
    		if (files != null && files.length > 0) {
    			for (File file : files) {
    				if (file.exists() && file.isDirectory()) {
    					File[] files4 = file.listFiles(new MyFilenameFilter(regex));
		    			if (files4 != null && files4.length > 0) {
		    				for (File file4 : files4) {
    			    	map = new HashMap<String, Object>();
    			    	map.put("item_icon", R.drawable.item_icon_def);
    			    	map.put("item_name", file4.getName());
    			    	map.put("item_path", file4.getAbsolutePath());
    			    	map.put("item_recovery_path", recovery_path + "/" + file4.getName());
    			    	
    	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    	        			.format(new Date(file4.lastModified()));    			    	
    			    	map.put("item_date", date);
    			    	
    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
    			    	list.add(map);    	
		    				}
		    			}
    					File[] files2 = file.listFiles(new MyFilenameFilter(regex_usb2));	
    					if (files2 != null && files2.length > 0) {
    						for (File file2 : files2) {
    							if (file2.exists() && file2.isDirectory()) {
    				    			File[] files3 = file2.listFiles(new MyFilenameFilter(regex));
    				    			if (files3 != null && files3.length > 0) {
    				    				for (File file3 : files3) {
    				    			    	map = new HashMap<String, Object>();
    				    			    	map.put("item_icon", R.drawable.item_icon_def);
    				    			    	map.put("item_name", file3.getName());
    				    			    	map.put("item_path", file3.getAbsolutePath());
    				    			    	map.put("item_recovery_path", recovery_path + "/" + file3.getName());
    				    			    	
    				    	        		String date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    				    	        			.format(new Date(file3.lastModified()));    			    	
    				    			    	map.put("item_date", date2);
    				    			    	
    				    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
    				    			    	list.add(map);    					
    				    				}
    				    			}    								
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
		return list;
		
	}	
	
	public List<Map<String, Object>> getListDataSata(Context context) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;	
    	String recovery_path = 
    		ConfigUtils.getStringConfig(context, R.string.config_recovery_sata_path);     	
    	
    	File dir = new File("/mnt/sata");
    	String regex = ".+\\.[zZ][iI][pP]";
    	String regex_sata = "sd[a-z]([0-9])*";
    	
    	if (dir.exists() && dir.isDirectory()) {
    		File[] files = dir.listFiles(new MyFilenameFilter(regex_sata));	
    		if (files != null && files.length > 0) {
    			for (File file : files) {
    				if (file.exists() && file.isDirectory()) {
    					File[] files2 = file.listFiles(new MyFilenameFilter(regex));	
    					if (files2 != null && files2.length > 0) {
    						for (File file2 : files2) {
		    			    	map = new HashMap<String, Object>();
		    			    	map.put("item_icon", R.drawable.item_icon_def);
		    			    	map.put("item_name", file2.getName());
		    			    	map.put("item_path", file2.getAbsolutePath());
		    			    	map.put("item_recovery_path", recovery_path + "/" + file2.getName());
		    			    	
		    	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
		    	        			.format(new Date(file2.lastModified()));    			    	
		    			    	map.put("item_date", date);
		    			    	
		    			    	map.put("item_icon2", R.drawable.item_icon2_def);    	
		    			    	list.add(map);     							
    						}    						
    					}
    				}
    			}
    		}
    	}  
    	
		return list;
		
	}	
	
	
	public List<Map<String, Object>> getListDataBack(Context context, String backStr) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;
    	
    	map = new HashMap<String, Object>();
    	map.put("item_icon", R.drawable.item_icon_back);
    	map.put("item_name", backStr);    			    	
    	map.put("item_path", "");     			    	
    	map.put("item_date", "");    	
    	map.put("item_icon2", R.drawable.item_icon2_null);  
    	
    	list.add(map);      	
    	return list;
	}	
	
	public List<Map<String, Object>> getListDataNext(Context context, String nextStr) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map;
    	
    	map = new HashMap<String, Object>();
    	map.put("item_icon", R.drawable.item_icon_def);
    	map.put("item_name", nextStr);    			    	
    	map.put("item_path", "");     			    	
    	map.put("item_date", "");    	
    	map.put("item_icon2", R.drawable.item_icon2_def);  
    	
    	list.add(map);      	
    	return list;
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
	
	public boolean exists(String path) {
		File file = new File(path);
		return file.exists();
	}
		public int delete(String device, String file) {
		if((device ==  null) || (file == null)) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "delete:device is null, or file is null");
			return -1;
		}
		if(UpgradeActivity.DEBUG) Log.d(TAG, "delete:device is " + device + ",file is " + file);
		String f_path = new String(device + file);

		File f_file = new File(f_path);
		if(f_file.exists()) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "delete:" + f_file.getPath()+ " is exist");
			f_file.delete();
		}
		return 0;
	}
	
	public int remove(String device, String file) {
		if((device ==  null) || (file == null)) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:device is null, or file is null");
			return -1;
		}
		if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:device is " + device + ",file is " + file);
		String dir_path = new String(device + FileUtils.SAVE_DIR_NAME+"/");
		String f_path = new String(device + file);

		File f_file = new File(f_path);
		File dir = new File(dir_path);
		
		if(!dir.exists()) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:" + dir.getPath() + " is not exist");
			dir.mkdir();
		}
		if(f_file.exists()) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:" + f_file.getPath()+ " is exist");
			f_file.renameTo(new File(dir,f_file.getName()));
		}
		return 0;
	}

	public int remove(String device, String file, String rename) {
		if((device ==  null) || (file == null) || (rename == null)) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:device is null, or file is null");
			return -1;
		}
		if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:device is " + device + ",file is " + file);
		String dir_path = new String(device + FileUtils.SAVE_DIR_NAME+"/");
		String f_path = new String(device + file);

		File f_file = new File(f_path);
		File dir = new File(dir_path);
		
		if(!dir.exists()) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:" + dir.getPath() + " is not exist");
			dir.mkdir();
		}
		if(f_file.exists()) {
			if(UpgradeActivity.DEBUG) Log.d(TAG, "remove:" + f_file.getPath()+ " is exist");
			f_file.renameTo(new File(dir,rename));
		}
		return 0;
	}
	
	public File creat(String device, String file) throws IOException {
		if((device ==  null) || (file == null)) {
			return null;
		}
		String f_path = new String(device + file);
		
		File f_file = new File(f_path);
		
		if(!f_file.exists()) {
			f_file.createNewFile();
		}
		return f_file;
	}
	
	public int writeFromInput(String device,String file,InputStream inputStream) {
		if((device ==  null) || (file == null)) {
			return -1;
		}		
		File f_file = null;
		OutputStream output = null;
		byte[] buffer = new byte[BUFFER_SIZE];
		int offset = 0;
		ByteBuffer bbuffer = null;
		try {
			//f_file = creat(device, file);
			//output = new FileOutputStream(f_file, true);
			/*byte buffer[]=new byte[4*1024];
			while((inputStream.read(buffer))!=-1) {
				output.write(buffer);
			}
			*/
			File dir = new File(device);
			saveFile = new File(dir, file);
			RandomAccessFile raf = new RandomAccessFile(this.saveFile, "rw");
			bbuffer = new ByteBuffer(raf, 0);
			int ch = 0;
			while((ch = inputStream.read(buffer, 0, BUFFER_SIZE))!=-1) {
				bbuffer.fillBuffer(buffer, ch);
				//output.write(ch);
			}
			//output.flush();

			bbuffer.write();
			raf.close();
			inputStream.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		/*
		finally {
			if(output != null) {
				try {
					output.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}*/
		return 0;
	}
	
	public long getFreeSpaceInB(String path) {
    	long nSDFreeSize = 0;
    	if (path != null) {
        	StatFs statfs = new StatFs(path);

    		long nBlocSize = statfs.getBlockSize();
    		long nAvailaBlock = statfs.getAvailableBlocks();
    		nSDFreeSize = nAvailaBlock * nBlocSize;
    	}
    	if(UpgradeActivity.DEBUG) Log.d(TAG, "getFreeSpaceInKB:"+Long.toString(nSDFreeSize)+"KB");
		return nSDFreeSize;
    }
}
