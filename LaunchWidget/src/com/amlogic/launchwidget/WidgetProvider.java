/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlogic.launchwidget;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;
/**
 * The widget's AppWidgetProvider.
 */
public class WidgetProvider extends AppWidgetProvider {
	private static final String TAG = "WidgetProvider";
	public static String ACTION_ADD="com.amlogic.widget.add";
	public static String ACTION_DELETE ="com.amlogic.widget.delete" ;
    public static String ACTION_ACTION_OPEN = "com.amlogic.widget.open";
    public static String ACTION_PACKAGE_REFRESH = "com.amlogic.widget.refresh";
    public static String ACTION_PACKAGE_SETTING = "com.amlogic.widget.setting";
    public static int MinimumCount = 1;
    public static String xmlConfigFile = "config.xml";
    private static HandlerThread sWorkerThread; 
    private static Handler sWorkerQueue; 
    private static WidgetDataProviderObserver sDataObserver;
    private InputStream inputStream;    
    public WidgetProvider() {
        // Start the worker thread
        sWorkerThread = new HandlerThread("WeatherWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }     
      
    @Override 
    public void onEnabled(Context context) {  
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, WidgetProvider.class);
            sDataObserver = new WidgetDataProviderObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(WidgetBaseColumns.Columns.CONTENT_URI, true, sDataObserver);
            
            Cursor Cursor = context.getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, null, null,null, null);
            if(Cursor.getCount() < MinimumCount){
	            Log.i(TAG, "insert default data");
	       	 	AssetManager assetManager = context.getAssets();
				try {
					inputStream = assetManager.open(xmlConfigFile);
				} catch (IOException e) {
					e.printStackTrace();
				} 
				 WidgetUtils widgetUtils = new WidgetUtils();
				 widgetUtils.SaxXMl(context, inputStream);
	        }
        }
    }
    
    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();
        if("com.amlogic.widget.finishactivity".equals(action)){
            loadThumbnail(ctx);
            final AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
            final ComponentName cn = new ComponentName(ctx, WidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list);
        }else if (action.equals(ACTION_PACKAGE_REFRESH)) {
            final Context context = ctx;
            sWorkerQueue.removeMessages(0);
            sWorkerQueue.post(new Runnable() {
                @Override  
                public void run() {
                    final ContentResolver r = context.getContentResolver();
                    final Cursor c = r.query(WidgetBaseColumns.Columns.CONTENT_URI, null, null, null, 
                            null);
                    final int count = c.getCount();
                    final int maxDegrees = 96;
                    r.unregisterContentObserver(sDataObserver);
                    for (int i = 0; i < count; ++i) {
                        final Uri uri = ContentUris.withAppendedId(WidgetBaseColumns.Columns.CONTENT_URI, i);
                        final ContentValues values = new ContentValues();
                        values.put("created", new Random().nextInt(maxDegrees));
                        r.update(uri, values, null, null);
                    }
                    r.registerContentObserver(WidgetBaseColumns.Columns.CONTENT_URI, true, sDataObserver);

                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    final ComponentName cn = new ComponentName(context, WidgetProvider.class);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list);
                }
            });
        }else if (action.equals(ACTION_ACTION_OPEN)) {
            final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            final String packageName = intent.getStringExtra(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT);
            final String className = intent.getStringExtra(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME);
            if((packageName != null && packageName != "")&& (className != null && className != "")){
                Log.d("packageName = ", "packageName"+packageName+"className"+className);
                Intent intent2 = new Intent().setComponent(new ComponentName(packageName, className));
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                ctx.startActivity(intent2);
            }
            
        }else if(action.equals(ACTION_DELETE)){
            Log.i(TAG, "---------------------------APP_DELETE--------------------------");
	       	 String appClassName = intent.getStringExtra(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME);
	       	 int delResult = new WidgetUtils().delete(WidgetBaseColumns.Columns.CONTENT_URI, ctx, appClassName);
//	       	 if(delResult >0){
	       		Log.i(TAG, "---------------------------delResult--------------------------"+delResult);
	       		ctx.sendBroadcast(new Intent().setAction(ACTION_PACKAGE_REFRESH));
//	       	 }
        }else if(action.equals(ACTION_ADD)){
        	String appPackageName = intent.getStringExtra(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT);
        	String appClassName = intent.getStringExtra(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME);
        	 ContentValues initialValues = new ContentValues();
        	 initialValues.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, appPackageName);
        	 initialValues.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, appClassName);
        	 Uri uri = new WidgetUtils().insert(WidgetBaseColumns.Columns.CONTENT_URI, ctx, initialValues);
        	 Log.i(TAG, uri.toString()+"---------------------------APP_ADD--------------------------appPackageName ="+appPackageName+"appClassName="+appClassName);
//        	 if(uri != null){
        		 ctx.sendBroadcast(new Intent().setAction(ACTION_PACKAGE_REFRESH));
//        	 }
        }else if(action.equals(ACTION_PACKAGE_SETTING)){
        	Intent setingIntent = new Intent();
        	setingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	setingIntent.setClassName("com.amlogic.launchwidget", "com.amlogic.launchwidget.WidgetListActivity");
			ctx.startActivity(setingIntent);
        }else if("com.amlogic.widget.addapp".equals(action)){
        	Log.i("", "------------------com.amlogic.widget.addapp-----------------------------------");
       	 	AssetManager assetManager = ctx.getAssets();
			 try {
				inputStream = assetManager.open(xmlConfigFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			 WidgetUtils widgetUtils = new WidgetUtils();
			 widgetUtils.SaxXMl(ctx, inputStream);
			 
       }
        
//        if("com.amlogic.widget.add_app".equals(action)){
//        	
//        	AssetManager assetManager =ctx.getAssets();
//      		try {
//				 inputStream = assetManager.open("config.xml");
//				 WidgetUtils widgetUtils = new WidgetUtils();
//				 widgetUtils.SaxXMl(inputStream);
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        	
//          WidgetUtils widgetUtils = new WidgetUtils();
//			try {
//			inputStream =  (FileInputStream) context.getResources().getAssets().open("config.xml");
//			  Log.i(TAG, "----inputStream------"+inputStream.toString());	
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//			widgetUtils.SaxXMl(inputStream);
//        	
//          Log.i(TAG, "-------------------------add_app------------------------" );
//          ContentValues initialValues = new ContentValues();
//     	  initialValues.put(WidgetBaseColumns.Widget.COLUMN_NAME_TITLE, "com.gsoft.appinstall");
//     	  initialValues.put(WidgetBaseColumns.Widget.COLUMN_NAME_NOTE, "main");
//     	  new WidgetUtils().insert(WidgetBaseColumns.Widget.CONTENT_URI, ctx, initialValues);
//     	  initialValues = new ContentValues();
//          initialValues.put(WidgetProvider.EXTRA_PACKAGE_NAME, "com.android.settings");
//          initialValues.put(WidgetProvider.EXTRA_CLASS_NAME, "Settings");
//          new WidgetUtils().insert(WidgetBaseColumns.Widget.CONTENT_URI, ctx, initialValues);
//          initialValues = new ContentValues();
//          initialValues.put(WidgetProvider.EXTRA_PACKAGE_NAME, "com.android.music");
//          initialValues.put(WidgetProvider.EXTRA_CLASS_NAME, "MusicBrowserActivity");
//          new WidgetUtils().insert(WidgetBaseColumns.Widget.CONTENT_URI, ctx, initialValues);
//          initialValues = new ContentValues();
//          initialValues.put(WidgetProvider.EXTRA_PACKAGE_NAME, "com.android.camera");
//          initialValues.put(WidgetProvider.EXTRA_CLASS_NAME, "Camera");
//          new WidgetUtils().insert(WidgetBaseColumns.Widget.CONTENT_URI, ctx, initialValues);
//          ctx.sendBroadcast(new Intent().setAction(ACTION_PACKAGE_REFRESH));	
//          
//        }
        
        super.onReceive(ctx, intent);
    }
            
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            final Intent intent = new Intent(context, WidgetRemoteViews.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.weather_list, intent);
            rv.setEmptyView(R.id.weather_list, R.id.empty_view);

            final Intent onClickIntent = new Intent(context, WidgetProvider.class);
            onClickIntent.setAction(ACTION_ACTION_OPEN);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);
            
            // Bind the click intent for the refresh button on the widget
            final Intent refreshIntent = new Intent(context, WidgetProvider.class);
            refreshIntent.setAction(WidgetProvider.ACTION_PACKAGE_SETTING);
            final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.add_more, refreshPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


//return a snapshot of the current list of recent apps
public void loadThumbnail(Context context) {
    final ActivityManager am = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);

    final List<ActivityManager.RecentTaskInfo> recentTasks =
            am.getRecentTasks(2, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

    int numTasks = recentTasks.size();
    if(numTasks ==2){
        RecentTaskInfo task = recentTasks.get(1);
		if(task!=null && task.baseIntent!=null && task.baseIntent.getComponent()!=null){
        String className = task.baseIntent.getComponent().getClassName();
        Cursor cursor = context.getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, new String[]{WidgetBaseColumns.Columns._ID, WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON} , " classname = ?",new String[] {className}, null); 
	    if(cursor.moveToFirst()){
            if(cursor.getBlob(1).length==1){
                ActivityManager.TaskThumbnails thumbs = am.getTaskThumbnails(task.persistentId);
                if (thumbs != null && thumbs.mainThumbnail != null) {
                    int rowId = cursor.getInt(0);
                    ContentValues cv = new ContentValues();
                    writeBitmap(cv, thumbs.mainThumbnail );
                    int id = context.getContentResolver().update(WidgetBaseColumns.Columns.CONTENT_URI, cv, "_id = "+ rowId, null);
               		 Log.w("WidgetProvider", "id ="+id);
				} else {
                return;
                }
            
            }
        }
        cursor.close();
        }
		}
    }

    public byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("WidgetProvider", "Could not write icon");
            return null;
        }
    }

    public void writeBitmap(ContentValues values, Bitmap bitmap) {
        byte[] data = flattenBitmap(bitmap);
        values.put(WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON, data);
    }

}




    
 


class WidgetDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;
    WidgetDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {
        // cursor for the new data.
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
    }
}



