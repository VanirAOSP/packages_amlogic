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

import java.io.InputStream;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class WidgetRemoteViews extends RemoteViewsService {
	
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	public static final String TAG = "WidgetRemoteViews";
    private Context mContext; 
    private Cursor mCursor;
    private int mAppWidgetId;   

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {

    }

    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider
        String packageName = "";
        String className = "";
        String appName = "";
        byte[] previewIcon = null;
        int temp = 0;
        if (mCursor.moveToPosition(position)) {
        	packageName = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT));
        	className = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME));
        	appName = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_APP_NAME));
        	previewIcon = mCursor.getBlob(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON));
        	  Log.i(TAG, "--packageName------className-----------------"+packageName+"  "+className + (previewIcon.length));
        	
        }

        Log.i(TAG, "-----------------------getViewAt--------------------------"+position);
        
        Bitmap bitmap  = null;
        PackageInfo packageInfo = null;
			try {
				ImageButton view ;
				packageInfo = mContext.getPackageManager().getPackageInfo(packageName , 0);
//				appName = (String) packageInfo.applicationInfo.loadLabel(mContext.getPackageManager());
//				Drawable drawable =  packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
				Drawable drawable =  mContext.getPackageManager().getActivityIcon(new ComponentName(packageName, className));
				bitmap = (((BitmapDrawable) drawable).getBitmap()); 
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		 
        final int itemId =  R.layout.widget_item;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        
        rv.setImageViewBitmap(R.id.widget_image, bitmap);
        if(appName.isEmpty()){
        	appName = (String) packageInfo.applicationInfo.loadLabel(mContext.getPackageManager());
        }
        rv.setTextViewText(R.id.widget_item,  appName);
        if(previewIcon!=null && previewIcon.length>1){
        	rv.setImageViewBitmap(R.id.widget_preview, BitmapFactory.decodeByteArray(previewIcon,0,previewIcon.length));
		}else{
			Resources rec = mContext.getResources();
			InputStream in = rec.openRawResource(R.drawable.bg_default_preview);
			rv.setImageViewBitmap(R.id.widget_preview,  BitmapFactory.decodeStream(in));
		}
        
 
        // Set the click intent so that we can handle it and show a toast message
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();  
        extras.putString(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, packageName);
        extras.putString(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, className);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_action, fillInIntent);

        return rv;  
    }
    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, null, null,
                null, WidgetBaseColumns.Columns.DEFAULT_SORT_ORDER);
        
        Log.i(TAG, "-----------------------getCount--------------------------"+mCursor.getCount());
        
    }
}
