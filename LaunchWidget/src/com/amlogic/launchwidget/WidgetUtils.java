package com.amlogic.launchwidget;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;



public class WidgetUtils {
	
	private static final String TAG = "WidgetUtils";
	private Cursor mCursor;
	private FileInputStream fileInputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private StringBuffer xmlContent = new StringBuffer();
    private Uri mUri =  Uri.parse("content://com.google.provider.NotePad/notes");;

    private String pacakgeName[] = null ;
    private String className[] = null ;
    
    private static final String[] PROJECTION = new String[] {
        WidgetBaseColumns.Columns._ID, // 0
        WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, // 1
    };
    
	public  Uri insert(Uri uri,  Context context,ContentValues initialValues) {
		// TODO Auto-generated method stub
//		int id = 0;
//		mCursor = ( (Activity) context).managedQuery(mUri, PROJECTION, null,null, null);
//		mCursor.moveToFirst();
////		Log.d(TAG,"=============id============="+mCursor.getCount());
////		if(mCursor.getCount() < 1){
////			id = 0;
////		}else{
////			id = mCursor.getInt(mCursor.getColumnIndex("_id"));
////		}
////		initialValues.put("modified", id+1);
		return context.getContentResolver().insert(uri, initialValues);
	}
   
	public final void update(String text, String title, Context context) {

		mCursor = ((Activity) context).managedQuery(mUri, PROJECTION, null,null, null);
		ContentValues values = new ContentValues();
		values.put(WidgetBaseColumns.Columns.COLUMN_MODIFY,
				System.currentTimeMillis());
		if (true) {
			if (title == null) {
				int length = text.length();
				title = text.substring(0, Math.min(30, length));
				if (length > 30) {
					int lastSpace = title.lastIndexOf(' ');
					if (lastSpace > 0) {
						title = title.substring(0, lastSpace);
					}
				}
			}
			values.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, title);
		} else if (title != null) {
			values.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, title);
		}
		values.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, text);
		int i = context.getContentResolver().update(mUri, values, null, null);
	}

	
	public final void mvtop( String packageName, Context context) {

	}
	
	public final int delete(Uri uri, Context context,String className) {
		String where = WidgetBaseColumns.Columns.COLUMN_CLASS_NAME+" = '"+className+"'";
		return context.getContentResolver().delete(uri, where, null);
	}
	
	public  List getInstalledApps(Context mContext) {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();  
		List<ApplicationInfo> packages = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
		List<String> appInfo = new ArrayList<String>();
		for (int i = packages.size()-1; i >= 0; i--) {
			for (int j = 0; j < appInfo.size(); j++) {
				if (packages.get(i).packageName.equals(appInfo.get(j))) {
					packages.remove(i);
				}
			}
		}
		for(int i=0;i<packages.size();i++) { 
			ApplicationInfo packageInfo = packages.get(i);  
			Log.i(TAG, "---------------------------packageName--------------------------"+packageInfo.packageName);
			AppInfo tmpInfo = new AppInfo();   
			tmpInfo.packageName = packageInfo.packageName;    
			tmpInfo.appIcon = packageInfo.loadIcon(mContext.getPackageManager());
			appList.add(tmpInfo);        
		} 
		return appList; 
	}
	
	 private static ArrayList<ApplicationInfo> mApplications;
	 
	 
	  /**
     * Loads the list of installed applications in mApplications.
     */
	 private void loadApplications(Context context) {
	       if ( mApplications != null) {
	           return;
	       }

	       PackageManager manager = context.getPackageManager();
	       Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	       mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

	       final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
	       Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

	       if (apps != null) {
	           final int count = apps.size();

	           if (mApplications == null) {
	               mApplications = new ArrayList<ApplicationInfo>(count);
	           }
	           mApplications.clear();
	           for (int i = 0; i < count; i++) {
	               ApplicationInfo application = new ApplicationInfo();
	               ResolveInfo info = apps.get(i);
	               application.packageName = info.activityInfo.packageName;
	               application.className = info.activityInfo.name;
	               mApplications.add(application);
	           }
	       }
	   }
    
    
	
	
	 //get system install package infomations;
//	public List<Map<String,Object>>  getLauncherApps(Context context) {
//		
// 
//		 HashMap<Object, CharSequence> mLabelCache  = new HashMap<Object, CharSequence>();
//		 
//		List<ResolveInfo> packages =  context.getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);
//		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>(packages.size());
//		for (int i = 0; i < packages.size() - 1; i++) {
//		    for (int j = packages.size() - 1; j > i; j--) {
//		     if ( packages.get(j).activityInfo.packageName.equals(packages.get(i).activityInfo.packageName)) {
//		    	 packages.remove(j);
//		     }
//		    }
//		   }
//		for (int i = 0; i < packages.size(); i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			ResolveInfo resolveInfo = packages.get(i);
//			map.put("packagename", resolveInfo.activityInfo.packageName);
//			map.put("classname", resolveInfo.activityInfo.name);
////			SELECT _id, packagename FROM tb_widget WHERE (packagename ='com.android.settings') ORDER BY modified DESC;
//			list.add(map);
//			 Log.i("", "-------------------------packageName------------------------"+resolveInfo.activityInfo.packageName);
//			 Log.i("", "-------------------------className------------------------"+resolveInfo.activityInfo.name);
//		}
//		return list;
//	}
		
	
	// get system install package infomations;
//	public List<ResourceHolder>  getLauncherApps(Context context) {
//		ResourceHolder resourceHolder;
//		List<ResolveInfo> packages =  context.getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);
//		List<ResourceHolder> list = new ArrayList<ResourceHolder>(packages.size());
//		
//		for (int i = 0; i < packages.size() - 1; i++) {
//			for (int j = packages.size() - 1; j > i; j--) {
//				if (packages.get(j).activityInfo.packageName.equals(packages
//						.get(i).activityInfo.packageName)) {
//					packages.remove(j);
//				}
//			}
//		}
//		
//		for (int i = 0; i < packages.size(); i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			ResolveInfo resolveInfo = packages.get(i);
//			resourceHolder = new ResourceHolder();
//			resourceHolder.setPackageName(resolveInfo.activityInfo.packageName);
//			resourceHolder.setClassName(resolveInfo.activityInfo.name);
//			list.add(resourceHolder);
//			
////			SELECT _id, packagename FROM tb_widget WHERE (packagename ='com.android.settings') ORDER BY modified DESC;
//			 Log.i("", "-------------------------packageName------------------------"+resourceHolder.getPackageName());
//			 Log.i("", "-------------------------className------------------------"+resourceHolder.getClassName());
//		}
//		return list;
//	}
	
	
	// get system install package infomations;
	public List<String> getinstallApps(Context context) {
		List<String> list = new ArrayList<String>();
		List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
		Iterator<ApplicationInfo> itemInfo = packages.iterator();
		while (itemInfo.hasNext()) {
			ApplicationInfo app = (ApplicationInfo) itemInfo.next();
			 if  ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0)   
	             continue;  
			list.add( app.packageName);
		}
		return list;
	}
	
	
	public boolean SaxXMl(Context context , InputStream fileInputStream) {
		Log.i(TAG, "---------------------------SaxXMl--------------------------");
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			SaxHandler saxHandler = new SaxHandler();
			reader.setContentHandler(saxHandler);

			inputStreamReader = new InputStreamReader(fileInputStream);
			bufferedReader = new BufferedReader(inputStreamReader);

			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				xmlContent.append(readLine);
			}
			if (xmlContent != null)
				reader.parse(new InputSource(new StringReader(xmlContent.toString())));
			
			loadApplications(context);
			List<String> pacakgeList = new ArrayList<String>();
			List<String> classList = new ArrayList<String>();
			if(readLine != ""){
				
				if(pacakgeName.length == className.length){
				for (int i = 0; i < pacakgeName.length ; i++) {
					for (int j = 0 ;  j < mApplications.size() ; j++) {
						if ((pacakgeName[i]
								.equals(mApplications.get(j).packageName) && className[i].equals(mApplications.get(j).className))) {
							pacakgeList.add(pacakgeName[i]);
							classList.add(className[i]);
						}
					}
				}
			}  
				if(pacakgeList.size() == classList.size()){
					for(int i  = 0 ;i < pacakgeName.length;i++){
						  ContentValues initialValues = new ContentValues();
				          initialValues.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, pacakgeList.get(i));
				          initialValues.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, classList.get(i));
				          insert(WidgetBaseColumns.Columns.CONTENT_URI, context, initialValues);
						Log.d("", "=======pacakgeName=========="+pacakgeName[i]);
						Log.d("", "=======className=========="+className[i]);
						
					}
				
			} 
				
			}
			
			
			pacakgeName = null ;
		    className = null ;
			 
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		  return false;
	}

	 class SaxHandler extends DefaultHandler {
		 
			
		 private Context mContext;
		 public SaxHandler(Context context){
			 this.mContext = context;
		 }
		 public SaxHandler(){
		 }
		private static final String TAG = "SaxHandler";
		String hisname, address, money, sex, status;
		String mLocalName, keyVisible, keyValue, visibleValue;
		boolean sdStorage = false;
		String value;

		public void startDocument() throws SAXException {
			Log.i(TAG," sax start ");
		}

		public void endDocument() throws SAXException {
			Log.i(TAG," sax end ");
		}

		public void startElement(String namespaceURI, String localName,
				String qName, Attributes attr) throws SAXException {

			mLocalName = localName;

		}

		public void endElement(String namespaceURI, String localName, String qName)
				throws SAXException {

			mLocalName = "";

		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {

		    
			if (mLocalName.equals("package_config")) {
				String string = new String(ch, start,length).trim();
				if(string.trim() != ""){
					   pacakgeName = string.split(",");
					 for(int i = 0 ; i<pacakgeName.length;i++){
						 pacakgeName[i] = pacakgeName[i].trim();
						 Log.d("", "==============================pacakgeName=========="+pacakgeName[i]);
					 }
				}
			}
			
			if (mLocalName.equals("class_config")) {
				String string = new String(ch, start,length).trim();
				if(string.trim() != ""){
					 className = string.split(",");
					 for(int i = 0 ; i<className.length;i++){
						 className[i] = className[i].trim();
						 Log.d("", "==============================className=========="+className[i]);
					 }
				}
			}
		}

		
	 }
		
		
	
	
	public class AppInfo {
		public String appName=""; 
		public String packageName=""; 
		public String className=""; 
		public String versionName=""; 
		public Drawable appIcon=null; 
		
		public  String  getClassName(Context context,ApplicationInfo applicationInfo){
			
			PackageInfo pi = null;
			try {
				pi = context.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

			String className = "";
			ResolveInfo ri = apps.iterator().next();
			if (ri != null ) {
			className = ri.activityInfo.name;
			}
			return className;
		}
	}
}
