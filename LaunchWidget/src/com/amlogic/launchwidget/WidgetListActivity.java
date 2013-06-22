/*
 * Copyright (C) 2007 The Android Open Source Project
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class WidgetListActivity extends ListActivity {

	private static final String TAG = "WidgetSettingList";
	List<Map<String,Object>>  mList = new ArrayList<Map<String,Object>>();
	private static ArrayList<ApplicationInfo> mApplications;
	private ApplicationsAdapter applicationsAdapter;
//	List<ResourceHolder> appList = new ArrayList<ResourceHolder>();
//	List<Map<String,Object>> appList = new ArrayList<Map<String,Object>>();
	private Cursor mCursor;
	private static final String[] PROJECTION = new String[] {
			WidgetBaseColumns.Columns._ID,  
			WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT,  
			WidgetBaseColumns.Columns.COLUMN_CLASS_NAME,
	};
	private static final int COLUMN_INDEX_TITLE = 1;
	private static final Intent String = null;    
  
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		Intent intent = getIntent();
		if (intent.getData() == null) {  
			intent.setData(WidgetBaseColumns.Columns.CONTENT_URI);
		}
		getListView().setOnCreateContextMenuListener(WidgetListActivity.this);
		
		loadApplications();
        applicationsAdapter = new ApplicationsAdapter(this, mApplications);
    	setListAdapter(applicationsAdapter);
	}  
	
	@Override
	protected void onResume() {
		super.onResume();
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.widget_options_menu, menu);
		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
				new ComponentName(this, WidgetListActivity.class), null, intent,
				0, null);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			return;
		}
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.widget_context_menu, menu);
		menu.setHeaderTitle(getString(R.string.operation_title));
		Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), Integer.toString((int) info.id)));
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, WidgetListActivity.class), null, intent, 0, null);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		String packageName ;
		String className ;
		int id ;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
		String where = WidgetBaseColumns.Columns.COLUMN_CLASS_NAME+" = '"+(String) mApplications.get((int) (info.id)).className+"'";
		Log.d(TAG, "-------------------className-------------------------"+(String) mApplications.get((int) (info.id)).className);
		switch (item.getItemId()) {
		case R.id.context_widget_add:
			 if(getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, PROJECTION, where, null, null).getCount() < 1){
				ContentValues contentValues = new ContentValues();
				contentValues.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, (String) mApplications.get((int) (info.id)).packageName);
				contentValues.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, (String) mApplications.get((int) (info.id)).className);
				contentValues.put(WidgetBaseColumns.Columns.COLUMN_APP_NAME, (String) mApplications.get((int) (info.id)).title);
				new WidgetUtils().insert(WidgetBaseColumns.Columns.CONTENT_URI, this, contentValues);
				applicationsAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH)); 
			 }
			 return true;
//		case R.id.context_favorite:
//			Log.d("",mApplications.get((int) (info.id)).intent.toString());
////			Log.d(TAG, "-------------------context_favorite-------------------------");
//			
//			sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH));
//			return true;
//			
//		case R.id.context_mv_up:
//			 Log.d(TAG, "-------------------context_mv_up-------------------------");
//			 mCursor = getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, null, null, null, null);
//			 mCursor.moveToFirst();
//			 id = mCursor.getInt(mCursor.getColumnIndex("_id"));
//			 packageName = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT));
//			 className = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME));
//        	 Log.d(TAG, "-------------------cursor.getColumnCount()-------------------------"+id+"---"+packageName+className);
//        	 sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH));
//			return true;
//			
//		case R.id.context_mv_down:
//			Log.d(TAG, "-------------------context_mv_down-------------------------");
//			mCursor = getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, null, null, null, null);
//			mCursor.moveToLast();
//			packageName = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT));
//			className = mCursor.getString(mCursor.getColumnIndex(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME));
//			Log.d(TAG, "-------------------cursor.getColumnCount()-------------------------"+packageName+className);
//			sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH));
//			return true;
			
		case R.id.context_widget_delete:
			if(getContentResolver().query(WidgetBaseColumns.Columns.CONTENT_URI, PROJECTION, where, null, null).getCount() > 0){
				Log.d(TAG, "-------------------delete from widget-------------------------");
				new WidgetUtils().delete(WidgetBaseColumns.Columns.CONTENT_URI, this, (String) mApplications.get((int) (info.id)).className);
				applicationsAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH));
			 }
			return true; 
			
		case R.id.context_exit:
			Log.d(TAG, "-------------------exit------------------------");
			sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_PACKAGE_REFRESH));
			return true;
			
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i(TAG, "hasFocus = "+hasFocus);
//		if(!hasFocus)
		super.onWindowFocusChanged(hasFocus);
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		String action = getIntent().getAction();
		final ApplicationInfo info = mApplications.get(position);
		Intent intent =  info.intent;
        startActivity(intent);
        this.finish();
	}
  
	 
   @Override
	protected void onDestroy() {
	   if( mCursor != null){
		   mCursor.close();
		   mCursor = null;
	   }
	   super.onDestroy();
	}


   
   private void loadApplications() {
       if ( mApplications != null) {
           return;
       }

       PackageManager manager = getPackageManager();
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
               application.title = info.loadLabel(manager);
               application.setActivity(new ComponentName(
                       info.activityInfo.applicationInfo.packageName,
                       info.activityInfo.name),
                       Intent.FLAG_ACTIVITY_NEW_TASK
                       | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
               application.icon = info.activityInfo.loadIcon(manager);
               application.packageName = info.activityInfo.packageName;
               application.className = info.activityInfo.name;
               mApplications.add(application);
           }
       }
   }
   
   private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
       private Rect mOldBounds = new Rect();
       Context mContext;
       public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
           super(context, 0, apps);
           this.mContext = context;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           final ApplicationInfo info = mApplications.get(position);
           if (convertView == null) {
               final LayoutInflater inflater = getLayoutInflater();
               convertView = inflater.inflate(R.layout.widget_list, parent, false);
           }
           Drawable icon = info.icon;
           if (!info.filtered) {
               //final Resources resources = getContext().getResources();
               int width = (int) mContext.getResources().getDimension(R.dimen.widget_icon_width);
               int height = (int) mContext.getResources().getDimension(R.dimen.widget_icon_height);

               final int iconWidth = icon.getIntrinsicWidth();
               final int iconHeight = icon.getIntrinsicHeight();

               if (icon instanceof PaintDrawable) {
                   PaintDrawable painter = (PaintDrawable) icon;
                   painter.setIntrinsicWidth(width);
                   painter.setIntrinsicHeight(height);
               }

               if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
                   final float ratio = (float) iconWidth / iconHeight;

                   if (iconWidth > iconHeight) {
                       height = (int) (width / ratio);
                   } else if (iconHeight > iconWidth) {
                       width = (int) (height * ratio);
                   }

                   final Bitmap.Config c =
                           icon.getOpacity() != PixelFormat.OPAQUE ?
                               Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                   final Bitmap thumb = Bitmap.createBitmap(width, height, c);
                   final Canvas canvas = new Canvas(thumb);
                   canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
                   mOldBounds.set(icon.getBounds());
                   icon.setBounds(0, 0, width, height);
                   icon.draw(canvas);
                   icon.setBounds(mOldBounds);
                   icon = info.icon = new BitmapDrawable(thumb);
                   info.filtered = true;
               }
           }

           final TextView textView = (TextView) convertView.findViewById(R.id.widget_title);
           final ImageView appView = (ImageView) convertView.findViewById(R.id.widget_icon);
           final ImageView shotcutIcon = (ImageView) convertView.findViewById(R.id.widget_shotcut);
           appView.setImageDrawable(icon);
//         textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
           textView.setText(info.title);
           mCursor = ((Activity) mContext).managedQuery(WidgetBaseColumns.Columns.CONTENT_URI, new String[] {
					WidgetBaseColumns.Columns._ID,  
					WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT,  
					}, WidgetBaseColumns.Columns.COLUMN_CLASS_NAME+" ="+"'"+info.className+"'",null, null);
			if(mCursor.getCount() >= 1 ){
				shotcutIcon.setImageResource(R.drawable.shotcut);
			}else{
				 
				shotcutIcon.setImageResource(R.drawable.bg_default_preview);
			}
           return convertView;
       }
   }

   
   class ApplicationInfo {
	    CharSequence title;
	    Intent intent;
	    String packageName;
	    String className;
	    Drawable icon;
	    boolean filtered;

	    final void setActivity(ComponentName className, int launchFlags) {
	        intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setComponent(className);
	        intent.setFlags(launchFlags);
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (!(o instanceof ApplicationInfo)) {
	            return false;
	        }

	        ApplicationInfo that = (ApplicationInfo) o;
	        return title.equals(that.title) &&
	                intent.getComponent().getClassName().equals(
	                        that.intent.getComponent().getClassName());
	    }

	    @Override
	    public int hashCode() {
	        int result;
	        result = (title != null ? title.hashCode() : 0);
	        final String name = intent.getComponent().getClassName();
	        result = 31 * result + (name != null ? name.hashCode() : 0);
	        return result;
	    }
	}
}
   
 
