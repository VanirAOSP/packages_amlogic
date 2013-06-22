package com.amlogic.netfilebrowser.smbmnt;

import java.util.ArrayList;
import android.util.Log;

public class SmbClientMnt {
	public static final String TAG = "SmbClientMnt";
	private ArrayList<String> sharepaths = new ArrayList<String>();

	//-1 input username and psw
	public native int SmbMount(String MntArg);

	public native int SmbUnMount(String MountPoint);

	public native int SmbRefresh();

	public native String SmbGetShareList(int fd); 

	public native int SmbGetStatus(int fd); 

	public native int SmbGetNum(); 

	public void FreshList() {
		sharepaths.clear();
		SmbRefresh();
	}
	
	public int GetStatus(int type) {
		return 0;
	}
	
	public ArrayList<String> GetListNum() {
		String item = "";
		int total = SmbGetNum(); 
		Log.d(TAG,"Get ListNum:"+Integer.toString(total));
		if(total > 0) {
			sharepaths.clear();
			for(int i = 0; i < total;i++) {
				item =  SmbGetShareList(i);
				Log.d(TAG,"Got Item"+item);
				sharepaths.add(item);
			}
		}
		return sharepaths;
	}
	
	public String GetShareDir(int num) {
		if(num > sharepaths.size()) {
			return null;
		}
		else {
			return  sharepaths.get(num);
		}	 		 
	}
	
	static {
        System.loadLibrary("smbmnt");
    }
}