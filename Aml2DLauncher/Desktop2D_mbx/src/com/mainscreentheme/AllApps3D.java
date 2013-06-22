package com.mainscreentheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

public class AllApps3D extends Activity {

	private GridView mGrid;
	private ArrayList<ApplicationInfo> mApplications;
	private ApplicationsAdapter mAppAdapter;
	private AppReceiver mAppReceiver;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
		setContentView(R.layout.home); 
		loadApplications();
	    bindApplications();
	    mGrid.setOnItemClickListener(new ApplicationLauncher());
	    
	     mAppReceiver = new AppReceiver();
	     // Register intent receivers
	     IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
	     filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	     filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	     filter.addDataScheme("package");
	     registerReceiver(mAppReceiver, filter);
	     

	}
	@Override
	public void onResume() {
		super.onResume();
	}
	protected void onDestroy(){
	     unregisterReceiver(mAppReceiver);
	     super.onDestroy();
	}

    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
            startActivity(app.intent);
	}
    }

	private void loadApplications() {
//        if (mApplications != null) {
//            return;
//        }

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
                
                Log.i("AllApps3D", ""+application.title.toString());
                
                mApplications.add(application);
            }
        }
    }
	
    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
    	mAppAdapter = new ApplicationsAdapter(this, mApplications);
        if (mGrid == null) {
            mGrid = (GridView) findViewById(R.id.all_apps);
        }
        Log.i("TAG", ""+mApplications.size());
        mGrid.setAdapter(mAppAdapter);
        mGrid.setSelection(0);

    }
    
    public class AppReceiver extends BroadcastReceiver{

    
    	
    	private static final String TAG = "AppReceiver";
        
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		Log.i("AppReceiver", "onReceive");
           
                final String action = intent.getAction();

                if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                        || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                        || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                	
                	Log.i("AppReceiver", "Intent");
                	
                    final String packageName = intent.getData().getSchemeSpecificPart();
                    final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

                    if (packageName == null || packageName.length() == 0) {
                        // they sent us a bad intent
                        return;
                    }

                    if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                    	Log.i("AppReceiver", "ACTION_PACKAGE_CHANGED");

                		loadApplications();
                	    bindApplications();

                    } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    	Log.i("AppReceiver", "ACTION_PACKAGE_REMOVED");

                		loadApplications();
                	    bindApplications();

                        // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                        // later, we will update the package at this time
                    } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    	Log.i("AppReceiver", "ACTION_PACKAGE_ADDED");

                		loadApplications();
                	    bindApplications();

                    }
                    


               
            }

    	}	
}
}
