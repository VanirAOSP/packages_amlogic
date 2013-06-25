/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.library.ZLibrary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;

import com.amlogic.DebugConfig;
import com.amlogic.pmt.DevStatusDisplay;
import com.amlogic.pmt.RelevanceOp;
import com.amlogic.pmt.RenderView;
import com.amlogic.pmt.Resolution;
import com.amlogic.pmt.Waiting;
import com.amlogic.pmt.menu.MenuOp;
import android.os.SystemProperties;//gyx comment
import com.amlogic.PicturePlayer.R;
import android.net.Uri;

public abstract class ZLAndroidActivity extends Activity {
	protected static final String TAG = "zlactivity";

	public abstract ZLApplication createApplication(String fileName);

	// private static final String REQUESTED_ORIENTATION_KEY =
	// "org.geometerplus.zlibrary.ui.android.library.androidActiviy.RequestedOrientation";
	// private static final String ORIENTATION_CHANGE_COUNTER_KEY =
	// "org.geometerplus.zlibrary.ui.android.library.androidActiviy.ChangeCounter";
	public RenderView mGLRenderView = null;
	private String filetype = null;
	private RelevanceOp relevanceOp = null;
	private MenuOp menuOp = null;
	public AbsoluteLayout layout;
	protected DevStatusDisplay devdisplay;
	private int MouseX = 0;
	private int MouseY = 0;

	private int m = 0;
	private int n = 0;
	private float x = 0;
	private float y = 0;
	private boolean flag = false;
	private float cScale = 0;
	private float lastLength = 0;
	private float currentLength = 0;
	private float moveX = 0;
	private float moveY = 0;
	private int sflag = 0;
	protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;
    protected int mTouchState = TOUCH_STATE_REST;

	protected int mScrollStep = 110;
	private int mDownMotionX = 0;
	private int mDownMotionY = 0;
	protected final static int PAGE_UP = 0;
    protected final static int PAGE_DOWN = 1;

	// public int[] resolution = new int[]/*{1920,1080};//*/{1280,720};
	public static boolean exit_flag = false;

	// Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		// state.putInt(REQUESTED_ORIENTATION_KEY, myOrientation);
		// state.putInt(ORIENTATION_CHANGE_COUNTER_KEY, myChangeCounter);
	}

	public abstract String fileNameForEmptyUri();

	/*
	 * private String fileNameFromUri(Uri uri) { if
	 * (uri.equals(Uri.parse("file:///"))) { return fileNameForEmptyUri(); }
	 * else { return uri.getPath(); } }
	 */

	// Override
	public void onCreate(Bundle state) {
		if (DebugConfig.debug == true)
			Log.v("Txtplayer", "Create");
		super.onCreate(state);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		exit_flag = false;
		int screen_Width = getWindowManager().getDefaultDisplay().getWidth();
		int screen_Height = getWindowManager().getDefaultDisplay()
				.getHeight();
		Resolution.setResolution(screen_Width, screen_Height);
		if (true == DebugConfig.debug)
			Log.d(TAG, "WIDTH " + Resolution.getWidth() + ", HEIGHT"
					+ Resolution.getHeight());

		layout = new AbsoluteLayout(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.main, null);
		layout.addView(view, Resolution.getWidth(), Resolution.getHeight());
		setContentView(layout);
		getLibrary().setActivity(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			filetype = bundle.getString("file_type");
		} else {
			filetype = "Picture";
			// filetype = "Picture";Audio;Text
		}
		mGLRenderView = (RenderView) view.findViewById(R.id.glsurfaceview);
		// gyx add
		SystemProperties.set("vplayer.hideStatusBar.enable","true");
		
		mGLRenderView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (true == DebugConfig.debug)
					Log.d(TAG, "ontouch action:" + event.getAction() + ","
							+ event.getRawX() + "," + event.getRawY());
				MouseX = (int) event.getRawX();
				MouseY = (int) event.getRawY();
				int eventAction = event.getAction();
				int eventCount = event.getPointerCount();
				
				if (mGLRenderView.GetGridLayoutInstance() != null) 
					{
					int mScrollD = 2;

						switch (eventAction & MotionEvent.ACTION_MASK) {
							case MotionEvent.ACTION_DOWN:
								mTouchState = TOUCH_STATE_REST;
								mDownMotionX = MouseX;
								mDownMotionY = MouseY;
								break;
					
							case MotionEvent.ACTION_MOVE:
								mTouchState = TOUCH_STATE_SCROLLING;
								
								break;
					
							case MotionEvent.ACTION_UP:
								if(Math.abs(MouseY-mDownMotionY) > mScrollStep)
									{
									mTouchState = TOUCH_STATE_SCROLLING;
									if(MouseY > mDownMotionY)
										mScrollD = 0;
									else
										mScrollD = 1;
									}
								break;
							}
						if (mTouchState == TOUCH_STATE_SCROLLING) 
							{
							mTouchState = TOUCH_STATE_REST;
							if (mGLRenderView.GetGridLayoutInstance().pageScroll(mScrollD) == true) 
								return true;
							}					 
					}

				if (mGLRenderView.GetPictureLayoutInstance() != null) {
					if (0 == eventAction) {
						sflag = 1;
						m = MouseX;
						n = MouseY;
					} else if (1 == eventAction) {
						flag = false;
					}
					if (2 == eventCount) {
						if (sflag == 1) {
							moveX = event.getX(1) - event.getX(0);
							moveY = event.getY(1) - event.getY(0);
							lastLength = (float) Math.sqrt(moveX * moveX
									+ moveY * moveY);
						}
						moveX = event.getX(1) - event.getX(0);
						moveY = event.getY(1) - event.getY(0);
						currentLength = (float) Math.sqrt(moveX * moveX + moveY
								* moveY);
						cScale = (currentLength - lastLength) / 350;
						mGLRenderView.currentPicLayout.setScale(cScale);
						lastLength = currentLength;
						flag = true;
						sflag = 2;
					} else if (1 == eventCount && flag == false) {
						if (2 == eventAction) {
							x = MouseX - m;
							y = MouseY - n;
							mGLRenderView.currentPicLayout.setPosXY(x / 350,
									-y / 350);
							m = MouseX;
							n = MouseY;
						}
					}
				}
				return false;
			}
		});

		mGLRenderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (true == DebugConfig.debug)
					Log.d(TAG, "onclick");
				if (mGLRenderView.GetGridLayoutInstance() != null) {
					if (true == DebugConfig.debug)
						Log.d(TAG, "---------------call gridlayout click");
					mGLRenderView.GetGridLayoutInstance().MouseClick(v, MouseX,
							MouseY);
				} else if (mGLRenderView.GetPictureLayoutInstance() != null) {
					if (menuOp.getMenuInstance() != null
							&& menuOp.getMenuInstance().getVisibility() == View.VISIBLE) {
						if (true == DebugConfig.debug)
							Log.d(TAG,
									"---------------getMenuInstance is exist");
						if (menuOp.getMenuInstance().getMenuIns() != null) {
							int focus = menuOp.getMenuInstance().getMenuIns()
									.mouseClick(MouseX, MouseY);
							if (focus >= 0) {
								menuOp.getMenuInstance().handleOnClick(focus);
								if (menuOp.getMenuInstance() != null)
									menuOp.getMenuInstance().requestFocus();
								if (relevanceOp.menuStopFlag) {
									menuOp.DestoryMenu();
									relevanceOp.menuStopFlag = false;
								}
							}
						}
					} else {
						showmenu();
					}
				}
			}

		});
		menuOp = new MenuOp(this);
		menuOp.setRenderView(mGLRenderView);

		relevanceOp = new RelevanceOp(this);
		relevanceOp.setRenderView(mGLRenderView);
		relevanceOp.setMenuOpInstance(menuOp);
	}

	// Override
	public void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
        SystemProperties.set("vplayer.hideStatusBar.enable","true");
		if (mGLRenderView.GetMusicLayoutInstance() != null
				|| mGLRenderView.GetPictureLayoutInstance() != null) {
			return;
		}
		getDevice();

		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");
		registerReceiver(broadcastReceiver, intentFilter);
		register_ad_broadcast();
		Intent intent = getIntent();
		String action = intent.getAction();
		if (true == DebugConfig.debug)
			Log.e(TAG, "-------action is" + action);
		if (Intent.ACTION_VIEW.equalsIgnoreCase(action)) {

			Uri uri = intent.getData();
			if (uri != null) {
				relevanceOp.PlayFile(filetype, uri.getPath());
				return;
			}
		}
		relevanceOp.PlayBrowser(filetype);
	}

	// Override
	public void onPause() {
		if (true == DebugConfig.debug)
			Log.v("TxtAndriodplayer", "onPause");
		super.onPause();
		menuOp.DestoryMenu();
        SystemProperties.set("vplayer.hideStatusBar.enable","false");
	}

	private static ZLAndroidLibrary getLibrary() {
		return (ZLAndroidLibrary) ZLibrary.Instance();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (true == DebugConfig.debug)
			Log.d(TAG, "down:" + keyCode);
		if (mGLRenderView.GetPictureLayoutInstance() != null) {
			if ((mGLRenderView.currentPicLayout.status == 0 || mGLRenderView.currentPicLayout.status2 == 0) && keyCode != 23) {
//				if (true == DebugConfig.debug)
					Log.d("ZLAndroid", "==0&onKeyDown return==");
				return false;
			}
		}
		if (!exit_flag)
			return false;
		if (keyCode == 113) {
			if (mGLRenderView.GetMusicLayoutInstance() != null) {
				mGLRenderView.GetMusicLayoutInstance().pauseSong();
			}
			finish();
			return super.onKeyDown(keyCode, event);
		}
		if (PMTSpecialKeyDown(keyCode, event) == true)
			return true;
		if (PMTKeyDown(keyCode, event) == true)
			return true;
		return super.onKeyDown(keyCode, event);
	}
	private boolean PMTKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 0xCF:
		case 0xCE:
		case 0xCD:
		case 0xCC:
		case 0xCB:
		case 0xCA:
		case 0xC9:
		case 0xC8:
		case 0xC7:
		case 0xC6:
		case 0xC5:
		case 0xC4:
		case 0xC3:
		case 0xC2:
		case 0xC1:
			PMTKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, event);
			break;
		case 0xFF:
		case 0xFE:
		case 0xFD:
		case 0xFC:
		case 0xFB:
		case 0xFA:
		case 0xF9:
		case 0xF8:
		case 0xF7:
		case 0xF6:
		case 0xF5:
		case 0xF4:
		case 0xF3:
		case 0xF2:
		case 0xF1:
			PMTKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event);
			break;
		}
		if (menuOp.getMenuInstance() != null
				&& menuOp.getMenuInstance().getVisibility() == View.VISIBLE) {
 			if((keyCode==25)||(keyCode==24))
			{
				return false;
			}
			postClearOsdMessage();
			if (menuOp.getMenuInstance().getVisibility() == View.VISIBLE) {
				if (relevanceOp.menuStopFlag) {
					menuOp.DestoryMenu();
					relevanceOp.menuStopFlag = false;
				} else if (keyCode == KeyEvent.KEYCODE_MENU
						|| keyCode == KeyEvent.KEYCODE_BACK) {
					menuOp.HideMenu();
				}
			}
			if (mGLRenderView.GetMusicLayoutInstance() != null) {
				if (keyCode == 92
						|| keyCode == KeyEvent.KEYCODE_ENTER
						|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					if (mGLRenderView.GetMusicLayoutInstance().audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
						handlerClearOsd.removeMessages(1);
					}
				}
			}
			return true;
		} else {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				if (mGLRenderView.GetGridLayoutInstance() == null) {
					if (menuOp.getMenuInstance() != null
							&& menuOp.getMenuInstance().getVisibility() != View.VISIBLE) {
						String data = menuOp.getMenuInstance().getPlayType();
						if (data.equals("music")) {
							menuOp.getMenuInstance().ShowVolumeBarShortCut();
							postClearOsdMessage();
							return true;
						}
					}
				}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showmenu();
		}
		if (mGLRenderView.GetGridLayoutInstance() != null) {

			if (mGLRenderView.GetGridLayoutInstance().onKeyDown(keyCode, event) == true) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_BACK) {

				if (mGLRenderView.GetMusicLayoutInstance() != null) {
					mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
					mGLRenderView.uninitMusicLayout();
				}
				finish();
				return true;
			}
		}
		if (mGLRenderView.GetTxtLayoutInstance() != null) {
			if (mGLRenderView.GetTxtLayoutInstance().onKeyDown(keyCode, event) == true)
				return true;
		}

		if (mGLRenderView.GetPictureLayoutInstance() != null) {
			if (mGLRenderView.GetPictureLayoutInstance().onKeyDown(keyCode,
					event) == true)
				return true;
		}

		if (mGLRenderView.GetMusicLayoutInstance() != null) {
			if (mGLRenderView.GetMusicLayoutInstance()
					.onKeyDown(keyCode, event) == true)
				return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mGLRenderView.GetPictureLayoutInstance() != null
					&& mGLRenderView.currentPicLayout.getZoom() != mGLRenderView.currentPicLayout.defaultScale) {
				mGLRenderView.currentPicLayout.resetPosXY();
			} else
				relevanceOp.ReturnRelevance();
			return true;
		}

		if (keyCode == 90 || keyCode == 92) {
			if (mGLRenderView.GetMusicLayoutInstance() != null) {
				if (mGLRenderView.GetMusicLayoutInstance().audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
					handlerClearOsd.removeMessages(1);
			}
		}
		return false;
	}

	private boolean PMTSpecialKeyDown(int keyCode, KeyEvent event) {
		if (mGLRenderView.GetGridLayoutInstance() != null)
			return false;
		if (keyCode == KeyEvent.KEYCODE_ENTER
				|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (true == DebugConfig.debug)
				Log.d(TAG, "menuOp.getMenuInstance " + menuOp.getMenuInstance());
			if (menuOp.getMenuInstance() == null) {
				if (true == DebugConfig.debug)
					Log.d(TAG, ">>>showmenu");
				showmenu();
				if (menuOp.getMenuInstance() != null)
					menuOp.getMenuInstance().setVisibility(View.INVISIBLE);
			}
			if (menuOp.getMenuInstance() != null
					&& menuOp.getMenuInstance().getVisibility() == View.INVISIBLE) {
				menuOp.getMenuInstance().handleSpecialKey1(85); // be same as
																// pause/play
																// key
				return true;
			}
		}

		if (keyCode == 94 || keyCode == 95 || keyCode == 96 || keyCode == 100
				|| keyCode == 92 || keyCode == 85 || keyCode == 86
				|| keyCode == 87 || keyCode == 88
				|| keyCode == 114 || keyCode == 115) {
			if (menuOp.getMenuInstance() == null) {
				showmenu();
				// BUG: the following 2 statements will cause the menu cannot be
				// displayed,
				// but it gets the focus and draws normally, the reason is
				// unknown
				if (menuOp.getMenuInstance() != null)
					menuOp.getMenuInstance().setVisibility(View.INVISIBLE);
			}
		}
		switch (keyCode) {
		case 94:// IMAGE_MODE 图像模式
			menuOp.getMenuInstance().SelectFrameShortCut(
					"shortcut_setup_video_picture_mode_");
			return true;
		case 95:// VOICE_MODE 声音模式
			menuOp.getMenuInstance().SelectFrameShortCut(
					"shortcut_setup_audio_sound_mode_");
			return true;
		case 96:// DISP_MODE 显示模式
			menuOp.getMenuInstance().SelectFrameShortCut(
					"shortcut_setup_video_display_mode_");
			return true;
		case 100:// SOURCE 通道选择
			menuOp.getMenuInstance().SelectFrameShortCut(
					"shortcut_common_source_");
			return true;

		case 92:
		case 85:
		case 86: // play_STOP
		//case 90:
		case 87:
		case 88:
			menuOp.getMenuInstance().handleSpecialKey(keyCode);
			if (menuOp.getMenuInstance() != null)
				menuOp.getMenuInstance().requestFocus();
			break;
		case 114:
		case 115:
			menuOp.getMenuInstance().ShowVolumeBarShortCut();
			postClearOsdMessage();
			return true;

		}
		return false;
	}

	public void showmenu() {
		if (mGLRenderView.GetGridLayoutInstance() == null) {
			String type = relevanceOp.getMenuType();
			if (true == DebugConfig.debug)
				Log.d(TAG, ">>>showmenu relevanceOp.getMenuType() "
						+ relevanceOp.getMenuType());
			if ((!(type.equals("")))) {
				if (true == DebugConfig.debug)
					Log.d(TAG, ">>>showmenu 22222");
				menuOp.ShowMenu(type);
				postClearOsdMessage();
				if (type.indexOf("T") != -1) {
					relevanceOp.CallbackName("txt",
							relevanceOp.getCurTextName());
					relevanceOp.CallbackPosScale("txt", "Nothing");
				}
				if (type.indexOf("P") != -1) {
					relevanceOp.CallbackName("picture",
							relevanceOp.getCurPictureName());
					relevanceOp.CallbackPosScale("picture", "Nothing");
				}
				if (type.indexOf("M") != -1) {
					relevanceOp.CallbackName("music",
							relevanceOp.getCurMusicName());
				}
			}
		}
	}

	// Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(ad_receiver);
		unregisterReceiver(broadcastReceiver);
		mGLRenderView.uninitTxtLayout();
		mGLRenderView.uninitMusicLayout();
		mGLRenderView.uninitPictureLayout();
		mGLRenderView.uninitGridLayout();
		mGLRenderView.onStop();
	}

	public void onDestroy() {
		if (true == DebugConfig.debug)
			Log.v("TxtAndriodplayer", "Destroy");
		super.onDestroy();
	}

	private int killProcess() {
		// TODO Auto-generated method stub
		if (true == DebugConfig.debug)
			Log.d("Skyworth3DNetmovieBrowser", "killProcess");
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		return 0;
	}

	private int myChangeCounter;

	private void setAutoRotationMode() {
	}

	// Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);

		switch (getRequestedOrientation()) {
		default:
			break;
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
			if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
				myChangeCounter = 0;
			} else if (myChangeCounter++ > 0) {
				setAutoRotationMode();
			}
			break;
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
			if (config.orientation != Configuration.ORIENTATION_LANDSCAPE) {
				myChangeCounter = 0;
			} else if (myChangeCounter++ > 0) {
				setAutoRotationMode();
			}
			break;
		}
	}

	void rotate() {
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		// @Override
		public void onReceive(Context context, Intent intent) {
			String ReceiveName = intent.getData().toString().substring(7);
			/*
			 * if(ReceiveName.equals("/mnt/sdcard")) return;
			 */
			if (intent.getAction()
					.equals("android.intent.action.MEDIA_MOUNTED")) {
				PostDelayMountMessage(ReceiveName);

			} else if (intent.getAction().equals(
					"android.intent.action.MEDIA_REMOVED")
					|| intent.getAction().equals(
							"android.intent.action.MEDIA_UNMOUNTED")
					|| intent.getAction().equals(
							"android.intent.action.MEDIA_BAD_REMOVAL")) {
				for (int i = 0; i < DeviceList.size(); i++) {
					if (true == DebugConfig.debug)
						Log.d(TAG,
								"ReceiveName " + ReceiveName + ", DeviceList +"
										+ i + " " + DeviceList.get(i));
					if (ReceiveName.equals(DeviceList.get(i)))
						relevanceOp.UnmountDevice(ReceiveName);
				}
			}
		}
	};
	List<String> DeviceList = new ArrayList<String>();

	public void getDevice() {
		File[] files = new File("/mnt").listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getPath().startsWith("/mnt/sd")
						|| file.getPath().equals("/mnt/sata")) {
					if (file != null) {
						DeviceList.add(file.getPath());
					}
				}
			}
		}
	}

	private Handler handlerClearOsd = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				menuOp.HideMenu();
				break;
			}
		}
	};

	public void postClearOsdMessage() {
		handlerClearOsd.removeMessages(1);
		Message mage = handlerClearOsd.obtainMessage(1);
		handlerClearOsd.sendMessageDelayed(mage, 10000);
	}

	private Handler handlerDelayMount = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				Bundle data = msg.getData();
				String ReceiveName = data.getString("name");
				relevanceOp.MountDevice(ReceiveName);
				DeviceList.add(ReceiveName);
				break;
			}
		}
	};

	public void PostDelayMountMessage(String name) {
		Message mage = handlerDelayMount.obtainMessage(1);
		Bundle data = new Bundle();
		data.putString("name", new String(name));
		mage.setData(data);
		handlerDelayMount.sendMessageDelayed(mage, 2000);
	}

	/**************** for skyworth advertise ****************/
	private static final String AD_UPDATE_BROAD = "com.skyworth.adservice.updatead";
	private static final String UPDATE_AD_IMGE = "update_ad_imge";
	private static final String UPDATE_AD_TEXT = "update_ad_text";
	private ADUpdateReceiver ad_receiver = null;

	private void register_ad_broadcast() {
		ad_receiver = new ADUpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AD_UPDATE_BROAD);
		this.registerReceiver(ad_receiver, filter);

	}

	private Bitmap getLoacalBitmap(String url) {

		{
			File BitmapFile = new File(url);
			FileInputStream fis;
			try {
				fis = new FileInputStream(BitmapFile);
				Bitmap btp = BitmapFactory.decodeStream(fis);
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return btp;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}
	}

	class ADUpdateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (mGLRenderView.GetGridLayoutInstance() != null) {
				String action = intent.getAction();
				if (action.equals(AD_UPDATE_BROAD)) {
					Bundle bundle = intent.getExtras();
					if (bundle != null) {
						String ad_imge_url = bundle.getString(UPDATE_AD_IMGE);
						if (null != ad_imge_url) {
							// Log.d( TAG ,"the ad imge update url : " +
							// ad_imge_url );
							Bitmap ad_bitmap = null;
							if (ad_imge_url.startsWith("/"))
								ad_bitmap = getLoacalBitmap(ad_imge_url);
							if (null != ad_bitmap) {
								// Log.d(TAG , "update ad poster : " +
								// ad_imge_url ) ;
								// advert.setImageBitmap(ad_bitmap);
								if (mGLRenderView.GetGridLayoutInstance() != null)
									mGLRenderView.GetGridLayoutInstance()
											.getADBitmap(ad_bitmap);
							}
						}
						String ad_text = bundle.getString(UPDATE_AD_TEXT);
						if (null != ad_text) {
							// Log.d( TAG ,"the ad text update text : " +
							// ad_text );
						}
					}
				}
			}

		}
	}
	/**************** end skyworth advertise ****************/

}
