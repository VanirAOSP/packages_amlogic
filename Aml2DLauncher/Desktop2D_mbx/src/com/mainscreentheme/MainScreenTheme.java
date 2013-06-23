package com.mainscreentheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainScreenTheme extends Activity implements OnFocusChangeListener,
		OnTouchListener ,OnClickListener{

	private static final String TAG = "MainScreenTheme";

	private static boolean has_stb = false;
	private static boolean has_stb_pre = false;
	
	public static final String MODE_PATH = "/sys/class/display/mode";
	
	private ImageView mImageTV, mImageTvView,mImageNetBrowser, mImageSTB, mImageSetting,
			mImageAndroid, mImagePicture, mImageDVB, mImageMusic,
			mImageMovie;

	//private TextView mTextWeb,mTextFilebrowser,mTextSettings,mTextApplications,
	//        mTextPicture,mTextMusic,mTextMovie;
	
	//private ImageView mNetBrowserString,mSTBString,mSettingString,mAndroidString,
	//        mPictureString,mMusicString,mMovieString;
	
	private AnimationDrawable mAnimNetBrowser, mAnimSTB, mAnimSetting,
			mAnimAndroid, mAnimPicture, mAnimDVB, mAnimMusic, mAnimMovie;

	private int mTvAlpha = 0;
	
	long time_old = 0;
	
	private Locale mLocale;
	
	private int m1080scale = SystemProperties.getInt("ro.platform.has.1080scale", 0);
		
	private Handler mHandler = new Handler();
	private int saved_id;

	/** Called when the activity is first created. */
    public String getCurMode() {
    	String modeStr;
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(MODE_PATH), 32);
    		try {
    			modeStr = reader.readLine();  
    		} finally {
    			reader.close();
    		}    		
    		return (modeStr == null)? "null" : modeStr;   	

    	} catch (IOException e) { 
    		Log.e("CommWebSite", "IO Exception when read: ");
    		return "null";
    	}    	
    }
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			PackageManager pm = this.getPackageManager();
			PackageInfo info = null;

			info = pm.getPackageInfo("com.amlogic.DVBPlayer", 0);
			if(info != null)
				has_stb = true;
		}catch(android.content.pm.PackageManager.NameNotFoundException e){
			has_stb = false;
		}
		has_stb_pre = has_stb;
		if(m1080scale == 2){
			if(has_stb)
				setContentView(R.layout.main_720p_dvb);
			else
				setContentView(R.layout.main_720p);
		}
		else{
			if(getCurMode().equals("480p")){
				if(has_stb)
					setContentView(R.layout.main_480p_dvb);
				else
					setContentView(R.layout.main_480p);
			}
		    else if(getCurMode().equals("480i")){
		    	if(has_stb)
					setContentView(R.layout.main_480i_dvb);
				else
					setContentView(R.layout.main_480i);
		    }
		    else if(getCurMode().equals("576p")){
		    	if(has_stb)
		    		setContentView(R.layout.main_576p_dvb);
		    	else
		    		setContentView(R.layout.main_576p);
		    }
		    else if(getCurMode().equals("576i")){
		    	if(has_stb)
					setContentView(R.layout.main_576i_dvb);
				else
					setContentView(R.layout.main_576i);
		    }
		    else if(getCurMode().equals("720p")){
				 if(has_stb)
					setContentView(R.layout.main_720p_dvb);
				 else
					setContentView(R.layout.main_720p);
		    }
		    else if(getCurMode().equals("1080p")){
		    	if(m1080scale == 1){
				 	if(has_stb)
						setContentView(R.layout.main_720p_dvb);
					else
						setContentView(R.layout.main_720p);
		    	}
		    	else{
				 	if(has_stb)
						setContentView(R.layout.main_1080p_dvb);
					else
						setContentView(R.layout.main_1080p);
		    	}
		    }
		    else{
		    	if(m1080scale == 1){
				 	if(has_stb)
						setContentView(R.layout.main_720p_dvb);
					else
						setContentView(R.layout.main_720p);
		    	}
		    	else{
					if(has_stb)
						setContentView(R.layout.main_1080i_dvb);
					else
						setContentView(R.layout.main_1080i);
		    	}
		    }
		}
		mImageTV = (ImageView) findViewById(R.id.ImageViewTV);
		mImageTvView = (ImageView) findViewById(R.id.ImageViewTvView);
		mImageNetBrowser = (ImageView) findViewById(R.id.ImageViewNetBrowser);
		mImageDVB = (ImageView) findViewById(R.id.ImageViewDVB);
		mImageSetting = (ImageView) findViewById(R.id.ImageViewSetting);
		mImageAndroid = (ImageView) findViewById(R.id.ImageViewAndroid);
		mImagePicture = (ImageView) findViewById(R.id.ImageViewPicture);
		mImageSTB = (ImageView) findViewById(R.id.ImageViewSTB);
		mImageMusic = (ImageView) findViewById(R.id.ImageViewMusic);
		mImageMovie = (ImageView) findViewById(R.id.ImageViewMovie);

		//mNetBrowserString = (ImageView) findViewById(R.id.ImageNetBrowserString);
		//mSTBString = (ImageView) findViewById(R.id.ImageSTBString);
		//mSettingString = (ImageView) findViewById(R.id.ImageSettingString);
		//mAndroidString = (ImageView) findViewById(R.id.ImageAndroidString);
		//mPictureString = (ImageView) findViewById(R.id.ImagePictureString);
		//mMusicString = (ImageView) findViewById(R.id.ImageMusicString);
		//mMovieString = (ImageView) findViewById(R.id.ImageMovieString);

		//mTextWeb = (TextView)findViewById(R.id.NetBrowserString);
		//mTextFilebrowser = (TextView)findViewById(R.id.STBString);

//		if(SystemProperties.getBoolean("mbx.dvb.enable", false)){
//			mTextFilebrowser.setText(R.string.stb);
//		}

		//mTextSettings = (TextView)findViewById(R.id.SettingString);
		//mTextApplications = (TextView)findViewById(R.id.AndroidString);
		//mTextPicture = (TextView)findViewById(R.id.PictureString);
		//mTextMusic = (TextView)findViewById(R.id.MusicString);
		//mTextMovie = (TextView)findViewById(R.id.MovieString);

		mAnimSTB = (AnimationDrawable) mImageSTB.getDrawable();
		mAnimNetBrowser = (AnimationDrawable) mImageNetBrowser.getDrawable();
		mAnimDVB = (AnimationDrawable) mImageDVB.getDrawable();
		mAnimSetting = (AnimationDrawable) mImageSetting.getDrawable();
		mAnimAndroid = (AnimationDrawable) mImageAndroid.getDrawable();
		mAnimPicture = (AnimationDrawable) mImagePicture.getDrawable();
		mAnimMusic = (AnimationDrawable) mImageMusic.getDrawable();
		mAnimMovie = (AnimationDrawable) mImageMovie.getDrawable();

		mImageDVB.setOnFocusChangeListener(this);
		mImageNetBrowser.setOnFocusChangeListener(this);
		mImageSTB.setOnFocusChangeListener(this);
		mImageSetting.setOnFocusChangeListener(this);
		mImageAndroid.setOnFocusChangeListener(this);
		mImagePicture.setOnFocusChangeListener(this);
		mImageMusic.setOnFocusChangeListener(this);
		mImageMovie.setOnFocusChangeListener(this);

		mImageDVB.setOnTouchListener(this);
		mImageNetBrowser.setOnTouchListener(this);
		mImageSTB.setOnTouchListener(this);
		mImageSetting.setOnTouchListener(this);
		mImageAndroid.setOnTouchListener(this);
		mImagePicture.setOnTouchListener(this);
		mImageMusic.setOnTouchListener(this);
		mImageMovie.setOnTouchListener(this);

		mImageDVB.setOnClickListener(this);
		mImageSTB.setOnClickListener(this);
		mImageAndroid.setOnClickListener(this);
		mImageMusic.setOnClickListener(this);
		mImageMovie.setOnClickListener(this);
		mImageNetBrowser.setOnClickListener(this);
		mImagePicture.setOnClickListener(this);
		mImageSetting.setOnClickListener(this);

		mImageTV.setImageResource(R.drawable.tv00);
		mImageNetBrowser.setImageResource(R.drawable.netbrowser00);
		mImageSTB.setImageResource(R.drawable.stb00);
		mImageSetting.setImageResource(R.drawable.setting00);
		mImageAndroid.setImageResource(R.drawable.android00);
		mImagePicture.setImageResource(R.drawable.picture00);
		mImageDVB.setImageResource(R.drawable.dvb00);
		mImageMusic.setImageResource(R.drawable.music00);
		mImageMovie.setImageResource(R.drawable.movie00);

		//mNetBrowserString.setImageResource(R.drawable.blank_short);
		//mSTBString.setImageResource(R.drawable.blank_large);
		//mSettingString.setImageResource(R.drawable.blank_large);
		//mAndroidString.setImageResource(R.drawable.blank_large);
		//mPictureString.setImageResource(R.drawable.blank_large);
		//mMusicString.setImageResource(R.drawable.blank_short);
		//mMovieString.setImageResource(R.drawable.blank_short);

        	boolean mVfdDisplay = SystemProperties.getBoolean("hw.vfd", false); 
        	if(mVfdDisplay ){
        		String[] cmdtest ={ "/system/bin/sh", "-c", "echo"+" "+"0:00:00"+" "+"> /sys/devices/platform/m1-vfd.0/led" };
            	do_exec(cmdtest);
        	}
	}
	int keyCodeResend;

	public boolean onKeyUp(int keyCode, KeyEvent event)
    {
    	Log.i(TAG, String.format("keyCode of onKeyUp is %d", keyCode));
		if((keyCode== KeyEvent.KEYCODE_DPAD_UP) ||(keyCode== KeyEvent.KEYCODE_DPAD_DOWN)){
			keyCodeResend = keyCode;

			new Thread(new Runnable() {
	               public void run() {
				    	IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
				    	long now = SystemClock.uptimeMillis();
				    	if((now - time_old) > 500){
					    	time_old = now;
			            	mHandler.removeCallbacks(TvRun);
			            	mHandler.removeCallbacks(StbRun);
			            	mHandler.removeCallbacks(NetbrowserRun);
			            	mHandler.removeCallbacks(DvbRun);
			            	mHandler.removeCallbacks(SettingRun);
			            	mHandler.removeCallbacks(AndroidRun);
			            	mHandler.removeCallbacks(PictureRun);
			            	mHandler.removeCallbacks(MusicRun);
			            	mHandler.removeCallbacks(MovieRun);
			        		mAnimNetBrowser.stop();
			        		mAnimDVB.stop();
			        		mAnimSetting.stop();
			        		mAnimAndroid.stop();
			        		mAnimPicture.stop();
			        		mAnimSTB.stop();
			        		mAnimMusic.stop();
			        		mAnimMovie.stop();
				            KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCodeResend==KeyEvent.KEYCODE_DPAD_UP?KeyEvent.KEYCODE_DPAD_RIGHT:KeyEvent.KEYCODE_DPAD_LEFT, 0);
				            KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCodeResend==KeyEvent.KEYCODE_DPAD_UP?KeyEvent.KEYCODE_DPAD_RIGHT:KeyEvent.KEYCODE_DPAD_LEFT, 0);
				            try{
						        Log.i(TAG, String.format("keyCode of onKeyUp_Thread is %d", keyCodeResend));
								try {
									Log.i(TAG, "down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keycode, 0);");
										wm.injectKeyEvent(down, false);
								}catch (RemoteException e) {
						            Log.i(TAG, "DeadOjbectException");
						        }
								try {
									Log.i(TAG, "up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keycode, 0);");
										wm.injectKeyEvent(up, false);
								}catch (RemoteException e) {
						            Log.i(TAG, "DeadOjbectException");
						        }
				            }
				            catch(SecurityException e){
				            	Log.i(TAG, "SecurityException");
				            }
				    	}
	               }
	
	          }).start();
			return true;
		}
		return super.onKeyUp(keyCode, event);
    };

	public boolean onKeyDown(int keyCode, KeyEvent event)
    {
		if((keyCode== KeyEvent.KEYCODE_DPAD_UP) ||(keyCode== KeyEvent.KEYCODE_DPAD_DOWN)){
			return true;
		}
		if(keyCode== KeyEvent.KEYCODE_BACK){
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
    };

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause ");
	}
	
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		Log.d(TAG, "onResume ");
//	}
//	protected void onDestroy() {
		// TODO Auto-generated method stub
//		super.onDestroy();
//		Log.d(TAG, "onDestroy ");
//		System.exit(0);
//	}
	private Runnable TvRun = new Runnable() {
		public void run() {
//			Log.d(TAG, "TvRun "+mTvAlpha);
			mImageTvView.setAlpha(mTvAlpha);
			mTvAlpha = mTvAlpha + 5;
			
			if(mTvAlpha<=255)
				mHandler.postDelayed(TvRun, 50);
		}
	};
	
	private Runnable StbRun = new Runnable() {
		public void run() {
			mImageSTB.setImageDrawable(mAnimSTB);
			mAnimSTB.start();
		}
	};
	
	private Runnable NetbrowserRun = new Runnable() {
		public void run() {
			mImageNetBrowser.setImageDrawable(mAnimNetBrowser);
			mAnimNetBrowser.start();
		}
	};
	
	private Runnable DvbRun = new Runnable() {
		public void run() {
			mImageDVB.setImageDrawable(mAnimDVB);
			mAnimDVB.start();
		}
	};
	
	private Runnable SettingRun = new Runnable() {
		public void run() {
			mImageSetting.setImageDrawable(mAnimSetting);
			mAnimSetting.start();
		}
	};
	
	private Runnable AndroidRun = new Runnable() {
		public void run() {
			mImageAndroid.setImageDrawable(mAnimAndroid);
			mAnimAndroid.start();
		}
	};
	
	private Runnable PictureRun = new Runnable() {
		public void run() {
			mImagePicture.setImageDrawable(mAnimPicture);
			mAnimPicture.start();
		}
	};
	
	private Runnable MusicRun = new Runnable() {
		public void run() {
			mImageMusic.setImageDrawable(mAnimMusic);
			mAnimMusic.start();
		}
	};
	
	private Runnable MovieRun = new Runnable() {
		public void run() {
			mImageMovie.setImageDrawable(mAnimMovie);
			mAnimMovie.start();
		}
	};
	
	
	public void onFocusChange(View v, boolean hasFocus) {
//		Log.d(TAG, "onFocusChange");
    	mHandler.removeCallbacks(TvRun);
    	mHandler.removeCallbacks(StbRun);
    	mHandler.removeCallbacks(NetbrowserRun);
    	mHandler.removeCallbacks(DvbRun);
    	mHandler.removeCallbacks(SettingRun);
    	mHandler.removeCallbacks(AndroidRun);
    	mHandler.removeCallbacks(PictureRun);
    	mHandler.removeCallbacks(MusicRun);
    	mHandler.removeCallbacks(MovieRun);
		mAnimNetBrowser.stop();
		mAnimDVB.stop();
		mAnimSetting.stop();
		mAnimAndroid.stop();
		mAnimPicture.stop();
		mAnimSTB.stop();
		mAnimMusic.stop();
		mAnimMovie.stop();
		
		mHandler.removeCallbacks(TvRun);
		mTvAlpha = 0;
		mImageTvView.setAlpha(mTvAlpha);
		
		mImageTV.setImageResource(R.drawable.tv00);
		mImageNetBrowser.setImageResource(R.drawable.netbrowser00);
		mImageDVB.setImageResource(R.drawable.dvb00);
		mImageSetting.setImageResource(R.drawable.setting00);
		mImageAndroid.setImageResource(R.drawable.android00);
		mImagePicture.setImageResource(R.drawable.picture00);
		mImageSTB.setImageResource(R.drawable.stb00);
		mImageMusic.setImageResource(R.drawable.music00);
		mImageMovie.setImageResource(R.drawable.movie00);

		mLocale = getResources().getConfiguration().locale;
		
		if ((v.getId() == R.id.ImageViewSTB) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--TV");
			mHandler.postDelayed(StbRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
//			if(SystemProperties.getBoolean("mbx.dvb.enable", false))
//			 mImageTvView.setImageResource(R.drawable.tv_stb);
//			else{
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   	mImageTvView.setImageResource(R.drawable.tv_filebrowser_zh);
				else
			   	mImageTvView.setImageResource(R.drawable.tv_filebrowser);
//			}				
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewNetBrowser) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--NetBrowser");
			mHandler.postDelayed(NetbrowserRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_netbrowser_zh);
			else 
			mImageTvView.setImageResource(R.drawable.tv_netbrowser);
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewDVB) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--STB");
			Log.d(TAG, "DVB ID="+v.getId()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			mHandler.postDelayed(DvbRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_dvb_zh);
			else 
			mImageTvView.setImageResource(R.drawable.tv_dvb);
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewSetting) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--Setting");
			mHandler.postDelayed(SettingRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_setting_zh);
			else
			mImageTvView.setImageResource(R.drawable.tv_setting);
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewAndroid) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--Android");
			mHandler.postDelayed(AndroidRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_android_zh);
			else
			mImageTvView.setImageResource(R.drawable.tv_android);
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewPicture) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--Picture");
			mHandler.postDelayed(PictureRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_picture_zh);
			else
			mImageTvView.setImageResource(R.drawable.tv_picture);
			mHandler.postDelayed(TvRun, 50);
		}

		if ((v.getId() == R.id.ImageViewMusic) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--Music");
			mHandler.postDelayed(MusicRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_music_zh);
			else
			mImageTvView.setImageResource(R.drawable.tv_music);
			mHandler.postDelayed(TvRun, 50);
		}
		if ((v.getId() == R.id.ImageViewMovie) && (hasFocus)) {
			//Log.d(TAG, "onFocusChange--Movie");
			mHandler.postDelayed(MovieRun, 50);
			mImageTvView.setAlpha(mTvAlpha);
			if(mLocale.getLanguage().equalsIgnoreCase("zh"))
			   mImageTvView.setImageResource(R.drawable.tv_movie_zh);
			else
			mImageTvView.setImageResource(R.drawable.tv_movie);
			mHandler.postDelayed(TvRun, 50);
		}

	}

	public void onResume(){
        	boolean mVfdDisplay = SystemProperties.getBoolean("hw.vfd", false); 
        	if(mVfdDisplay ){
        		String[] cmdtest ={ "/system/bin/sh", "-c", "echo"+" "+"0:00:00"+" "+"> /sys/devices/platform/m1-vfd.0/led" };
            	do_exec(cmdtest);
       		}				
        	try{
    			PackageManager pm = this.getPackageManager();
    			PackageInfo info = null;

    			info = pm.getPackageInfo("com.amlogic.DVBPlayer", 0);
    			if(info != null)
    				has_stb = true;
    		}catch(android.content.pm.PackageManager.NameNotFoundException e){
    			has_stb = false;
    		}
    		if(has_stb_pre != has_stb){
		if(m1080scale == 2){
			if(has_stb)
				setContentView(R.layout.main_720p_dvb);
			else
				setContentView(R.layout.main_720p);
		}
		else{
			if(getCurMode().equals("480p")){
				if(has_stb)
					setContentView(R.layout.main_480p_dvb);
				else
					setContentView(R.layout.main_480p);
			}
		    else if(getCurMode().equals("480i")){
		    	if(has_stb)
					setContentView(R.layout.main_480i_dvb);
				else
					setContentView(R.layout.main_480i);
		    }
		    else if(getCurMode().equals("576p")){
		    	if(has_stb)
		    		setContentView(R.layout.main_576p_dvb);
		    	else
		    		setContentView(R.layout.main_576p);
		    }
		    else if(getCurMode().equals("576i")){
		    	if(has_stb)
					setContentView(R.layout.main_576i_dvb);
				else
					setContentView(R.layout.main_576i);
		    }
		    else if(getCurMode().equals("720p")){
				 if(has_stb)
					setContentView(R.layout.main_720p_dvb);
				 else
					setContentView(R.layout.main_720p);
		    }
		    else if(getCurMode().equals("1080p")){
		    	if(m1080scale == 1){
				 	if(has_stb)
						setContentView(R.layout.main_720p_dvb);
					else
						setContentView(R.layout.main_720p);
		    	}
		    	else{
				 	if(has_stb)
						setContentView(R.layout.main_1080p_dvb);
					else
						setContentView(R.layout.main_1080p);
		    	}
		    }
		    else{
		    	if(m1080scale == 1){
				 	if(has_stb)
						setContentView(R.layout.main_720p_dvb);
					else
						setContentView(R.layout.main_720p);
		    	}
		    	else{
					if(has_stb)
						setContentView(R.layout.main_1080i_dvb);
					else
						setContentView(R.layout.main_1080i);
		    	}
		    }
		}
    	mImageTV = (ImageView) findViewById(R.id.ImageViewTV);
		mImageTvView = (ImageView) findViewById(R.id.ImageViewTvView);
		mImageNetBrowser = (ImageView) findViewById(R.id.ImageViewNetBrowser);
		mImageDVB = (ImageView) findViewById(R.id.ImageViewDVB);
		mImageSetting = (ImageView) findViewById(R.id.ImageViewSetting);
		mImageAndroid = (ImageView) findViewById(R.id.ImageViewAndroid);
		mImagePicture = (ImageView) findViewById(R.id.ImageViewPicture);
		mImageSTB = (ImageView) findViewById(R.id.ImageViewSTB);
		mImageMusic = (ImageView) findViewById(R.id.ImageViewMusic);
		mImageMovie = (ImageView) findViewById(R.id.ImageViewMovie);
		
		mAnimSTB = (AnimationDrawable) mImageSTB.getDrawable();
		mAnimNetBrowser = (AnimationDrawable) mImageNetBrowser.getDrawable();
		mAnimDVB = (AnimationDrawable) mImageDVB.getDrawable();
		mAnimSetting = (AnimationDrawable) mImageSetting.getDrawable();
		mAnimAndroid = (AnimationDrawable) mImageAndroid.getDrawable();
		mAnimPicture = (AnimationDrawable) mImagePicture.getDrawable();
		mAnimMusic = (AnimationDrawable) mImageMusic.getDrawable();
		mAnimMovie = (AnimationDrawable) mImageMovie.getDrawable();

		mImageDVB.setOnFocusChangeListener(this);
		mImageNetBrowser.setOnFocusChangeListener(this);
		mImageSTB.setOnFocusChangeListener(this);
		mImageSetting.setOnFocusChangeListener(this);
		mImageAndroid.setOnFocusChangeListener(this);
		mImagePicture.setOnFocusChangeListener(this);
		mImageMusic.setOnFocusChangeListener(this);
		mImageMovie.setOnFocusChangeListener(this);

		mImageDVB.setOnTouchListener(this);
		mImageNetBrowser.setOnTouchListener(this);
		mImageSTB.setOnTouchListener(this);
		mImageSetting.setOnTouchListener(this);
		mImageAndroid.setOnTouchListener(this);
		mImagePicture.setOnTouchListener(this);
		mImageMusic.setOnTouchListener(this);
		mImageMovie.setOnTouchListener(this);
	
		mImageDVB.setOnClickListener(this);
		mImageSTB.setOnClickListener(this);
		mImageAndroid.setOnClickListener(this);
		mImageMusic.setOnClickListener(this);
		mImageMovie.setOnClickListener(this);
		mImageNetBrowser.setOnClickListener(this);
		mImagePicture.setOnClickListener(this);
		mImageSetting.setOnClickListener(this);
		
		mImageTV.setImageResource(R.drawable.tv00);
		mImageNetBrowser.setImageResource(R.drawable.netbrowser00);
		mImageSTB.setImageResource(R.drawable.stb00);
		mImageSetting.setImageResource(R.drawable.setting00);
		mImageAndroid.setImageResource(R.drawable.android00);
		mImagePicture.setImageResource(R.drawable.picture00);
		mImageDVB.setImageResource(R.drawable.dvb00);
		mImageMusic.setImageResource(R.drawable.music00);
		mImageMovie.setImageResource(R.drawable.movie00);

        }
		if (saved_id == R.id.ImageViewAndroid)
		{
			mImageAndroid.requestFocusFromTouch();
			mImageAndroid.requestFocus();
		}
		else if (saved_id == R.id.ImageViewMusic)
		{
			mImageMusic.requestFocusFromTouch();
			mImageMusic.requestFocus();
		}
		else if (saved_id == R.id.ImageViewMovie)
		{
			mImageMovie.requestFocusFromTouch();
			mImageMovie.requestFocus();
		}
		else if (saved_id == R.id.ImageViewNetBrowser)
		{
			mImageNetBrowser.requestFocusFromTouch();
			mImageNetBrowser.requestFocus();
		}
		else if (saved_id == R.id.ImageViewPicture)
		{
			mImagePicture.requestFocusFromTouch();
			mImagePicture.requestFocus();
		}
		else if (saved_id == R.id.ImageViewSetting)
		{
			mImageSetting.requestFocusFromTouch();
			mImageSetting.requestFocus();
		}
		else if (saved_id == R.id.ImageViewDVB)
		{
			mImageDVB.requestFocusFromTouch();
			mImageDVB.requestFocus();
		}
		else if (saved_id == R.id.ImageViewSTB)
		{
			mImageSTB.requestFocusFromTouch();
			mImageSTB.requestFocus();
		}
		else{
			mImageAndroid.requestFocusFromTouch();
			mImageAndroid.requestFocus();
		}
		
        super.onResume();
	}	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		mLocale = getResources().getConfiguration().locale;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			if ((v.getId() == R.id.ImageViewSTB)) {
//				Log.d(TAG, "onTouch--STB");
				mHandler.postDelayed(StbRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				/*if(SystemProperties.getBoolean("mbx.dvb.enable", false))
					mImageTvView.setImageResource(R.drawable.tv_stb);
				else*/{
				   if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				        mImageTvView.setImageResource(R.drawable.tv_filebrowser_zh);
				else
				   mImageTvView.setImageResource(R.drawable.tv_filebrowser);
                                } 
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewNetBrowser)) {
//				Log.d(TAG, "onTouch--NetBrowser");
				mHandler.postDelayed(NetbrowserRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_netbrowser_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_netbrowser);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewDVB)) {
//				Log.d(TAG, "onTouch--DVB");
				mHandler.postDelayed(DvbRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				mImageTvView.setImageResource(R.drawable.tv_dvb);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewSetting)) {
//				Log.d(TAG, "onTouch--Setting");
				mHandler.postDelayed(SettingRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_setting_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_setting);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewAndroid)) {
//				Log.d(TAG, "onTouch--Android");
				mHandler.postDelayed(AndroidRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_android_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_android);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewPicture)) {
//				Log.d(TAG, "onTouch--Picture");
				mHandler.postDelayed(PictureRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_picture_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_picture);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewMusic)) {
//				Log.d(TAG, "onTouch--Music");
				mHandler.postDelayed(MusicRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_music_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_music);
				mHandler.postDelayed(TvRun, 50);
			}
			if ((v.getId() == R.id.ImageViewMovie)) {
//				Log.d(TAG, "onTouch--Movie");
				mHandler.postDelayed(MovieRun, 50);
				mImageTvView.setAlpha(mTvAlpha);
				if(mLocale.getLanguage().equalsIgnoreCase("zh"))
				   mImageTvView.setImageResource(R.drawable.tv_movie_zh);
				else
				mImageTvView.setImageResource(R.drawable.tv_movie);
				mHandler.postDelayed(TvRun, 50);
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			
			mAnimNetBrowser.stop();
			mAnimDVB.stop();
			mAnimSetting.stop();
			mAnimAndroid.stop();
			mAnimPicture.stop();
			mAnimSTB.stop();
			mAnimMusic.stop();
			mAnimMovie.stop();
			
			mHandler.removeCallbacks(TvRun);
			mTvAlpha = 0;
			mImageTvView.setAlpha(mTvAlpha);
			
			mImageTV.setImageResource(R.drawable.tv00);
			mImageNetBrowser.setImageResource(R.drawable.netbrowser00);
			mImageDVB.setImageResource(R.drawable.dvb00);
			mImageSetting.setImageResource(R.drawable.setting00);
			mImageAndroid.setImageResource(R.drawable.android00);
			mImagePicture.setImageResource(R.drawable.picture00);
			mImageSTB.setImageResource(R.drawable.stb00);
			mImageMusic.setImageResource(R.drawable.music00);
			mImageMovie.setImageResource(R.drawable.movie00);
		}
		return false;
	}

       private String do_exec(String[] cmd) {   
          String s = "\n";         
          try {   
              Process p = Runtime.getRuntime().exec(cmd);   
              BufferedReader in = new BufferedReader(   
                                new InputStreamReader(p.getInputStream()));   
              String line = null;   
              while ((line = in.readLine()) != null) {   
                  s += line + "\n";                  
             }   
          } catch (IOException e) {             
              e.printStackTrace();   
          }   
          return cmd.toString();        
      }   
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onClick");
		if ((v.getId() == R.id.ImageViewAndroid))
		{
		Intent intent = new Intent();
		//intent .setComponent(new ComponentName("com.android.launcher", "com.android.launcher2.Launcher"));
		intent.setClass(MainScreenTheme.this, AllApps3D.class);
		startActivity(intent);
		}
		if ((v.getId() == R.id.ImageViewMusic))
		{
		Intent intent = new Intent();
		try{
			intent .setComponent(new ComponentName("org.geometerplus.zlibrary.ui.android", "org.geometerplus.android.fbreader.FBReader"));
			startActivity(intent);
		}catch(Exception e){
			intent .setComponent(new ComponentName("com.android.music", "com.android.music.MusicBrowserActivity"));
			startActivity(intent);
		}
		}
		if ((v.getId() == R.id.ImageViewMovie))
		{
		stopMediaPlayer();
		Intent intent = new Intent();
		intent .setComponent(new ComponentName("com.farcore.videoplayer", "com.farcore.videoplayer.FileList"));
		startActivity(intent);
		}
		if ((v.getId() == R.id.ImageViewNetBrowser))
		{
		Intent intent = new Intent();
		intent .setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
		startActivity(intent);
		}
		if ((v.getId() == R.id.ImageViewPicture))
		{
		Intent intent = new Intent();
		try{
			intent .setComponent(new ComponentName("com.amlogic.PicturePlayer", "org.geometerplus.android.fbreader.FBReader"));
			startActivity(intent);
		}catch(Exception e){
			intent .setComponent(new ComponentName("com.cooliris.media", "com.cooliris.media.Gallery"));
			startActivity(intent);
		}
		}
		if ((v.getId() == R.id.ImageViewSetting))
		{
		Intent intent = new Intent();
		intent .setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
		startActivity(intent);
		}
		if ((v.getId() == R.id.ImageViewDVB))
		{
		if(has_stb){
			Intent intent = new Intent();
	                intent .setComponent(new ComponentName("com.amlogic.DVBPlayer", "com.amlogic.DVBPlayer.DialogNoChannel"));
			startActivity(intent);
		}
		else{
			Log.d(TAG,"NO DVBPlayer");
		}
		}
		if ((v.getId() == R.id.ImageViewSTB))
		{
		Intent intent = new Intent();
        intent .setComponent(new ComponentName("com.fb.FileBrower", "com.fb.FileBrower.FileBrower"));
		startActivity(intent);
		}
		saved_id = v.getId();
	}

	public void stopMediaPlayer()//stop the backgroun music player
    {
		Intent intent = new Intent();
    	intent.setAction("com.android.music.musicservicecommand.pause");
    	intent.putExtra("command", "stop");
    	this.sendBroadcast(intent);
    }
}
