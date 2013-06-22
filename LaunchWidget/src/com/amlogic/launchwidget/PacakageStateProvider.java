package com.amlogic.launchwidget;

import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PacakageStateProvider extends BroadcastReceiver {

	InputStream inputStream;
	private static final String TAG = "PacakageStateProvider";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub 
		final String action = intent.getAction();
		if(Intent.ACTION_PACKAGE_CHANGED.equals(action)|| Intent.ACTION_PACKAGE_REMOVED.equals(action)){
        	 final String packageName = intent.getData().getSchemeSpecificPart();
        	 if (!(packageName == null || packageName.length() == 0)) {
        		 int delResult = new WidgetUtils().delete(WidgetBaseColumns.Columns.CONTENT_URI, context, packageName);
        	 }
        	 context.sendBroadcast(new Intent().setAction(WidgetProvider.ACTION_ACTION_OPEN));
        }
	}
  
}
