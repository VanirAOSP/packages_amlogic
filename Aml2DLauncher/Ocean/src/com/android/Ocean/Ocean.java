package com.android.Ocean;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Ocean extends Activity {
	/** Called when the activity is first created. */
	private ImageView mImageBubble;
	private ImageView mImageAndroid;
	private ImageView mImageStb;
	private ImageView mImageTv;
	private ImageView mImageMovie;
	private ImageView mImagePicture;
	private ImageView mImageNetbrowser;
	private ImageView mImageSetting;
	private ImageView mImageMusic;

	private ImageView mImageFish1;
	private ImageView mImageFish2;
	private ImageView mImageFish3;
	private ImageView mImageFish4;
	private ImageView mImageFish5;

	private AnimationDrawable mAnimBubble;
	private AnimationDrawable mAnimAndroidTouch;
	private AnimationDrawable mAnimStbTouch;
	private AnimationDrawable mAnimTvTouch;
	private AnimationDrawable mAnimMovieTouch;
	private AnimationDrawable mAnimPictureTouch;
	private AnimationDrawable mAnimNetbrowserTouch;
	private AnimationDrawable mAnimSettingTouch;
	private AnimationDrawable mAnimMusicTouch;
	
	private int mFish1Width = 60, mFish1Height = 33, mFish1X = 700,
			mFish1Y = 450;
	private int mFish2Width = 60, mFish2Height = 29, mFish2X = 250,
			mFish2Y = 250;
	private int mFish3Width = 60, mFish3Height = 31, mFish3X = 0,
			mFish3Y = 350;
	private int mFish4Width = 45, mFish4Height = 20, mFish4X = 100,
			mFish4Y = 70;
	private int mFish5Width = 50, mFish5Height = 33, mFish5X = 700,
			mFish5Y = 150;
	
	private int mAndroidAlpha;
	private int mStbAlpha;
	private int mTvAlpha;
	private int mMovieAlpha;
	private int mSettingAlpha;
	private int mNetbrowserAlpha;
	private int mPictureAlpha;
	private int mMusicAlpha;
	
	private static final float mIconScale = 1.6f;
	private static final float mBubbleScale = 1.6f;
	private static final int mBubbleStandardWidth = 115;
	private static final int mBubbleStandardHeight = 600;
	private static final int mIconStandardWidth = 200;
	private static final int mIconStandardHeight = 266;
	private static final int mIconMinWidth = 74, mIconMinHeight = 98;
	private static final int mIconMaxWidth = 120, mIconMaxHeight = 160;
	private static final int mBubbleMinWidth = 42, mBubbleMinHeight = 92;
	private static final int mBubbleMaxWidth = 222, mBubbleMaxHeight = 480;
	private static final int mMinAlpha = 83, mMaxAlpha = 255;
	
	private int intScreenX, intScreenY;
	private int mBubbleWidth,mBubbleHeight;
	
	private int mAndroidWidth,mAndroidHeight;
	private int mAndroidPresentX, mAndroidPresentY;
	private int mAndroidNormalX, mAndroidNormalY;
	
	private int mStbWidth,mStbHeight;
	private int mStbPresentX, mStbPresentY;
	private int mStbNormalX, mStbNormalY;
	
	private int mTvWidth,mTvHeight;
	private int mTvPresentX, mTvPresentY;
	private int mTvNormalX, mTvNormalY;
	
	private int mMovieWidth,mMovieHeight;
	private int mMoviePresentX, mMoviePresentY;
	private int mMovieNormalX, mMovieNormalY;
	
	private int mSettingWidth,mSettingHeight;
	private int mSettingPresentX, mSettingPresentY;
	private int mSettingNormalX, mSettingNormalY;
	
	private int mNetbrowserWidth,mNetbrowserHeight;
	private int mNetbrowserPresentX, mNetbrowserPresentY;
	private int mNetbrowserNormalX, mNetbrowserNormalY;
	
	private int mPictureWidth,mPictureHeight;
	private int mPicturePresentX, mPicturePresentY;
	private int mPictureNormalX, mPictureNormalY;
	
	private int mMusicWidth,mMusicHeight;
	private int mMusicPresentX, mMusicPresentY;
	private int mMusicNormalX, mMusicNormalY;
	
	private boolean mAndroidFocused = false;
	private boolean mStbFocused = false;
	private boolean mTvFocused = false;
	private boolean mMovieFocused = false;
	private boolean mSettingFocused = false;
	private boolean mNetbrowserFocused = false;
	private boolean mPictureFocused = false;
	private boolean mMusicFocused = false;
	
//	private boolean mAndroidMove = false;
//	private boolean mStbMove = false;
//	private boolean mTvMove = false;
//	private boolean mMovieMove = false;
//	private boolean mSettingMove = false;
//	private boolean mNetbrowserMove = false;
//	private boolean mPictureMove = false;
//	private boolean mMusicMove = false;
	
	private static final int UPWARD = 1;
	private static final int DOWNWARD = 0;

	private static final int START = 1;
	private static final int STOP = 0;
	
	private int mAndroidDirection = UPWARD;
	private int mStbDirection = UPWARD;
	private int mTvDirection = UPWARD;
	private int mMovieDirection = UPWARD;
	private int mSettingDirection = UPWARD;
	private int mNetbrowserDirection = UPWARD;
	private int mPictureDirection = UPWARD;
	private int mMusicDirection = UPWARD;
	
//	private int mAppState = STOP;
	
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		mAppState = START;
		
		mImageFish1 = (ImageView) findViewById(R.id.myImageViewFish1);
		mImageFish1.setImageResource(R.drawable.fish1);

		mImageFish2 = (ImageView) findViewById(R.id.myImageViewFish2);
		mImageFish2.setImageResource(R.drawable.fish2);

		mImageFish3 = (ImageView) findViewById(R.id.myImageViewFish3);
		mImageFish3.setImageResource(R.drawable.fish3);

		mImageFish4 = (ImageView) findViewById(R.id.myImageViewFish4);
		mImageFish4.setImageResource(R.drawable.fish4);

		mImageFish5 = (ImageView) findViewById(R.id.myImageViewFish5);
		mImageFish5.setImageResource(R.drawable.fish5);

		mHandler.post(Background);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		intScreenX = dm.widthPixels;
		intScreenY = dm.heightPixels;

		mImageBubble = (ImageView) findViewById(R.id.myImageViewBubble);		
		mAnimBubble = (AnimationDrawable) getResources().getDrawable(
				R.anim.bubble);
		
		mImageAndroid = (ImageView) findViewById(R.id.myImageViewAndroid);
		mImageAndroid.setImageResource(R.drawable.android_normal);
		mAnimAndroidTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.android_touch);

		mImageStb = (ImageView) findViewById(R.id.myImageViewStb);
		mImageStb.setImageResource(R.drawable.stb_normal);
		mAnimStbTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.stb_touch);

		mImageTv = (ImageView) findViewById(R.id.myImageViewTv);
		mImageTv.setImageResource(R.drawable.tv_normal);
		mAnimTvTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.tv_touch);

		mImageMovie = (ImageView) findViewById(R.id.myImageViewMovie);
		mImageMovie.setImageResource(R.drawable.movie_normal);
		mAnimMovieTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.movie_touch);

		mImageSetting = (ImageView) findViewById(R.id.myImageViewSetting);
		mImageSetting.setImageResource(R.drawable.setting_normal);
		mAnimSettingTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.setting_touch);

		mImageNetbrowser = (ImageView) findViewById(R.id.myImageViewNetbrowser);
		mImageNetbrowser.setImageResource(R.drawable.netbrowser_normal);
		mAnimNetbrowserTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.netbrowser_touch);

		mImagePicture = (ImageView) findViewById(R.id.myImageViewPicture);
		mImagePicture.setImageResource(R.drawable.picture_normal);
		mAnimPictureTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.picture_touch);

		mImageMusic = (ImageView) findViewById(R.id.myImageViewMusic);
		mImageMusic.setImageResource(R.drawable.music_normal);
		mAnimMusicTouch = (AnimationDrawable) getResources().getDrawable(
				R.anim.music_touch);

		RestoreIcon();

		mHandler.post(AndroidNormalRun);
		mHandler.post(StbNormalRun);
		mHandler.post(TvNormalRun);
		mHandler.post(MovieNormalRun);
		mHandler.post(SettingNormalRun);
		mHandler.post(NetbrowserNormalRun);
		mHandler.post(PictureNormalRun);
		mHandler.post(MusicNormalRun);
		

	}

	private Runnable AndroidNormalRun = new Runnable() {
		public void run() {
			
			mImageAndroid.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mAndroidWidth, mAndroidHeight, mAndroidNormalX
							- mAndroidWidth / 2, mAndroidNormalY
							- mAndroidHeight/2));

			if (mAndroidDirection == UPWARD)
				mAndroidNormalY++;
			if (mAndroidDirection == DOWNWARD)
				mAndroidNormalY--;

			if (mAndroidNormalY - mAndroidPresentY >= 1)
				mAndroidDirection = DOWNWARD;
			if (mAndroidPresentY - mAndroidNormalY >= 9)
				mAndroidDirection = UPWARD;

//			if (!mAndroidMove&&(mAppState==START))
				mHandler.postDelayed(AndroidNormalRun, 200);
		}
	};
	
	private Runnable StbNormalRun = new Runnable() {
		public void run() {
			
			mImageStb.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mStbWidth, mStbHeight, mStbNormalX
							- mStbWidth / 2, mStbNormalY
							- mStbHeight/2));

			if (mStbDirection == UPWARD)
				mStbNormalY++;
			if (mStbDirection == DOWNWARD)
				mStbNormalY--;

			if (mStbNormalY - mStbPresentY >= 9)
				mStbDirection = DOWNWARD;
			if (mStbPresentY - mStbNormalY >= 1)
				mStbDirection = UPWARD;
			
//			if (!mStbMove&&(mAppState==START))
				mHandler.postDelayed(StbNormalRun, 200);
		}
	};
	
	private Runnable TvNormalRun = new Runnable() {
		public void run() {
			mImageTv.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mTvWidth, mTvHeight, mTvNormalX
							- mTvWidth / 2, mTvNormalY
							- mTvHeight/2));

			if (mTvDirection == UPWARD)
				mTvNormalY++;
			if (mTvDirection == DOWNWARD)
				mTvNormalY--;

			if (mTvNormalY - mTvPresentY >= 2)
				mTvDirection = DOWNWARD;
			if (mTvPresentY - mTvNormalY >= 8)
				mTvDirection = UPWARD;
			
//			if (!mTvMove&&(mAppState==START))
				mHandler.postDelayed(TvNormalRun, 200);
			
		}
	};
	
	private Runnable MovieNormalRun = new Runnable() {
		public void run() {
			mImageMovie.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mMovieWidth, mMovieHeight, mMovieNormalX
							- mMovieWidth / 2, mMovieNormalY
							- mMovieHeight/2));

			if (mMovieDirection == UPWARD)
				mMovieNormalY++;
			if (mMovieDirection == DOWNWARD)
				mMovieNormalY--;

			if (mMovieNormalY - mMoviePresentY >= 8)
				mMovieDirection = DOWNWARD;
			if (mMoviePresentY - mMovieNormalY >= 2)
				mMovieDirection = UPWARD;
			
//			if (!mMovieMove&&(mAppState==START))
				mHandler.postDelayed(MovieNormalRun, 200);
			
		}
	};
	
	private Runnable SettingNormalRun = new Runnable() {
		public void run() {
			mImageSetting.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mSettingWidth, mSettingHeight, mSettingNormalX
							- mSettingWidth / 2, mSettingNormalY
							- mSettingHeight/2));

			if (mSettingDirection == UPWARD)
				mSettingNormalY++;
			if (mSettingDirection == DOWNWARD)
				mSettingNormalY--;

			if (mSettingNormalY - mSettingPresentY >= 3)
				mSettingDirection = DOWNWARD;
			if (mSettingPresentY - mSettingNormalY >= 7)
				mSettingDirection = UPWARD;
			
//			if (!mSettingMove&&(mAppState==START))
				mHandler.postDelayed(SettingNormalRun, 200);
			
		}
	};
	
	private Runnable NetbrowserNormalRun = new Runnable() {
		public void run() {
			mImageNetbrowser.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mNetbrowserWidth, mNetbrowserHeight, mNetbrowserNormalX
							- mNetbrowserWidth / 2, mNetbrowserNormalY
							- mNetbrowserHeight/2));

			if (mNetbrowserDirection == UPWARD)
				mNetbrowserNormalY++;
			if (mNetbrowserDirection == DOWNWARD)
				mNetbrowserNormalY--;

			if (mNetbrowserNormalY - mNetbrowserPresentY >= 7)
				mNetbrowserDirection = DOWNWARD;
			if (mNetbrowserPresentY - mNetbrowserNormalY >= 3)
				mNetbrowserDirection = UPWARD;

//			if (!mNetbrowserMove&&(mAppState==START))
				mHandler.postDelayed(NetbrowserNormalRun, 200);
		}
	};
	
	private Runnable PictureNormalRun = new Runnable() {
		public void run() {
			mImagePicture.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mPictureWidth, mPictureHeight, mPictureNormalX
							- mPictureWidth / 2, mPictureNormalY
							- mPictureHeight/2));

			if (mPictureDirection == UPWARD)
				mPictureNormalY++;
			if (mPictureDirection == DOWNWARD)
				mPictureNormalY--;

			if (mPictureNormalY - mPicturePresentY >= 4)
				mPictureDirection = DOWNWARD;
			if (mPicturePresentY - mPictureNormalY >= 6)
				mPictureDirection = UPWARD;
			
//			if (!mPictureMove&&(mAppState==START))
				mHandler.postDelayed(PictureNormalRun, 200);
			
		}
	};
	
	private Runnable MusicNormalRun = new Runnable() {
		public void run() {
			mImageMusic.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mMusicWidth, mMusicHeight, mMusicNormalX
							- mMusicWidth / 2, mMusicNormalY
							- mMusicHeight/2));

			if (mMusicDirection == UPWARD)
				mMusicNormalY++;
			if (mMusicDirection == DOWNWARD)
				mMusicNormalY--;

			if (mMusicNormalY - mMusicPresentY >= 6)
				mMusicDirection = DOWNWARD;
			if (mMusicPresentY - mMusicNormalY >= 4)
				mMusicDirection = UPWARD;
			
//			if (!mMusicMove&&(mAppState==START))
				mHandler.postDelayed(MusicNormalRun, 200);
			
		}
	};

	private Runnable Background = new Runnable() {
		public void run() {
//			 Log.d("Fish", "running");
			mImageFish1.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mFish1Width, mFish1Height, mFish1X, mFish1Y));
			mImageFish2.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mFish2Width, mFish2Height, mFish2X, mFish2Y));
			mImageFish3.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mFish3Width, mFish3Height, mFish3X, mFish3Y));
			mImageFish4.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mFish4Width, mFish4Height, mFish4X, mFish4Y));
			mImageFish5.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mFish5Width, mFish5Height, mFish5X, mFish5Y));
			mFish1X--;
			mFish2X++;
			mFish3X++;
			mFish4X--;
			mFish5X++;
			if (mFish1X <= -100)
				mFish1X = 800;
			if (mFish2X >= 800)
				mFish2X = -100;
			if (mFish3X >= 800)
				mFish3X = -100;
			if (mFish4X <= -100)
				mFish4X = 800;
			if (mFish5X >= 800)
				mFish5X = -100;
//			if(mAppState==START)
				mHandler.postDelayed(Background, 100);
		}
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
//		Log.i("jay", "onStop");
		super.onStop();
//		mAppState = STOP;
		mHandler.removeCallbacks(Background);
		mHandler.removeCallbacks(AndroidNormalRun);
		mHandler.removeCallbacks(StbNormalRun);
		mHandler.removeCallbacks(TvNormalRun);
		mHandler.removeCallbacks(MovieNormalRun);
		mHandler.removeCallbacks(SettingNormalRun);
		mHandler.removeCallbacks(NetbrowserNormalRun);
		mHandler.removeCallbacks(PictureNormalRun);
		mHandler.removeCallbacks(MusicNormalRun);
		
		System.exit(0);
//		android.os.Process.killProcess(android.os.Process.myPid());
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();

		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
//				Log.i("jay", "ACTION_DOWN");
				
				androidSetFocus(x,y);
				stbSetFocus(x,y);
				tvSetFocus(x,y);
				movieSetFocus(x,y);
				settingSetFocus(x,y);
				netbrowserSetFocus(x,y);
				pictureSetFocus(x,y);
				musicSetFocus(x,y);
				
				break;
			case MotionEvent.ACTION_MOVE:
//				Log.i("jay", "ACTION_MOVE");
				
				if(isStbFocused())
				{
				   mHandler.removeCallbacks(StbNormalRun);
				   stbMove(x, y);
				}
				if(isTvFocused())
				{
				   mHandler.removeCallbacks(TvNormalRun);
				   tvMove(x, y);
				}
				if(isMovieFocused())
				{
				   mHandler.removeCallbacks(MovieNormalRun);
				   movieMove(x, y);
				}
				if(isAndroidFocused())
				{
				   mHandler.removeCallbacks(AndroidNormalRun);
				   androidMove(x, y);
				}
				if(isSettingFocused())
				{
				   mHandler.removeCallbacks(SettingNormalRun);
				   settingMove(x, y);
				}
				if(isNetbrowserFocused())
				{
				   mHandler.removeCallbacks(NetbrowserNormalRun);
				   netbrowserMove(x, y);
				}
				if(isPictureFocused())
				{
				   mHandler.removeCallbacks(PictureNormalRun);
				   pictureMove(x, y);
				}
				if(isMusicFocused())
				{
				   mHandler.removeCallbacks(MusicNormalRun);
				   musicMove(x, y);
				}
				
				break;
			case MotionEvent.ACTION_UP:
//				Log.i("jay", "ACTION_UP");
				if(isAndroidFocused())
				   androidNormalState(x,y);
				if(isStbFocused())
				   stbNormalState(x, y);
				if(isTvFocused())
				   tvNormalState(x, y);
				if(isMovieFocused())
				   movieNormalState(x, y);
				if(isSettingFocused())
				   settingNormalState(x, y);
				if(isNetbrowserFocused())
				   netbrowserNormalState(x, y);
				if(isPictureFocused())
				   pictureNormalState(x, y);
				if(isMusicFocused())
				   musicNormalState(x, y);

				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void androidSetFocus(float x, float y)
	{
		if((x >= mAndroidPresentX - mAndroidWidth / 2) && (x <= mAndroidPresentX + mAndroidWidth / 2)
			&& (y >= mAndroidPresentY - mAndroidHeight / 2) && (y <= mAndroidPresentY+ mAndroidHeight / 2))
		{
			mAndroidFocused = true;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void stbSetFocus(float x, float y)
	{
//		Log.i("jay", "stbSetFocus");
		if((x >= mStbPresentX - mStbWidth / 2) && (x <= mStbPresentX + mStbWidth / 2)
			&& (y >= mStbPresentY - mStbHeight / 2) && (y <= mStbPresentY+ mStbHeight / 2))
		{
//			Log.i("jay", "mStbMove = true;");
			mAndroidFocused = false;
			mStbFocused = true;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void tvSetFocus(float x, float y)
	{
		if((x >= mTvPresentX - mTvWidth / 2) && (x <= mTvPresentX + mTvWidth / 2)
			&& (y >= mTvPresentY - mTvHeight / 2) && (y <= mTvPresentY+ mTvHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = true;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void movieSetFocus(float x, float y)
	{
		if((x >= mMoviePresentX - mMovieWidth / 2) && (x <= mMoviePresentX + mMovieWidth / 2)
			&& (y >= mMoviePresentY - mMovieHeight / 2) && (y <= mMoviePresentY+ mMovieHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = true;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void settingSetFocus(float x, float y)
	{
		if((x >= mSettingPresentX - mSettingWidth / 2) && (x <= mSettingPresentX + mSettingWidth / 2)
			&& (y >= mSettingPresentY - mSettingHeight / 2) && (y <= mSettingPresentY+ mSettingHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = true;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void netbrowserSetFocus(float x, float y)
	{
		if((x >= mNetbrowserPresentX - mNetbrowserWidth / 2) && (x <= mNetbrowserPresentX + mNetbrowserWidth / 2)
			&& (y >= mNetbrowserPresentY - mNetbrowserHeight / 2) && (y <= mNetbrowserPresentY+ mNetbrowserHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = true;
			mPictureFocused = false;
			mMusicFocused = false;
			
	}
	}
	private void pictureSetFocus(float x, float y)
	{
		if((x >= mPicturePresentX - mPictureWidth / 2) && (x <= mPicturePresentX + mPictureWidth / 2)
			&& (y >= mPicturePresentY - mPictureHeight / 2) && (y <= mPicturePresentY+ mPictureHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = true;
			mMusicFocused = false;
			
	}
	}
	private void musicSetFocus(float x, float y)
	{
		if((x >= mMusicPresentX - mMusicWidth / 2) && (x <= mMusicPresentX + mMusicWidth / 2)
			&& (y >= mMusicPresentY - mMusicHeight / 2) && (y <= mMusicPresentY+ mMusicHeight / 2))
		{
			mAndroidFocused = false;
			mStbFocused = false;
			mTvFocused = false;
			mMovieFocused = false;
			mSettingFocused = false;
			mNetbrowserFocused = false;
			mPictureFocused = false;
			mMusicFocused = true;
			
	}
	}
	private boolean isAndroidFocused()
	{
		return mAndroidFocused;
	}
	private boolean isStbFocused()
	{
		return mStbFocused;
	}
	private boolean isTvFocused()
	{
		return mTvFocused;
	}
	private boolean isMovieFocused()
	{
		return mMovieFocused;
	}
	private boolean isSettingFocused()
	{
		return mSettingFocused;
	}
	private boolean isNetbrowserFocused()
	{
		return mNetbrowserFocused;
	}
	private boolean isPictureFocused()
	{
		return mPictureFocused;
	}
	private boolean isMusicFocused()
	{
		return mMusicFocused;
	}
	
	private void androidMove(float x, float y)
	{
		if (((x >= mAndroidPresentX - mAndroidWidth / 2) && (x <= mAndroidPresentX
				+ mAndroidWidth / 2))
				&& ((y >= mAndroidPresentY - mAndroidHeight / 2) && (y <= mAndroidPresentY
						+ mAndroidHeight / 2))) {
			if ((x >= mAndroidPresentX - 2) && (x <= mAndroidPresentX + 2)
					&& (y >= mAndroidPresentY - 2)
					&& (y <= mAndroidPresentY + 2)) {

				mImageAndroid.setImageDrawable(mAnimAndroidTouch);
				mAnimAndroidTouch.start();
			} else
				mAnimAndroidTouch.stop();
		
			mAndroidPresentX = (int) x;
			mAndroidPresentY = (int) y;

			mAndroidWidth = (int)(mIconScale*mAndroidPresentY*mIconStandardWidth/intScreenY);
			mAndroidHeight = (int)(mIconScale*mAndroidPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mAndroidPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mAndroidPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mAndroidWidth <=mIconMinWidth)
				mAndroidWidth = mIconMinWidth;
			if(mAndroidWidth >=mIconMaxWidth)
				mAndroidWidth = mIconMaxWidth;

			if(mAndroidHeight <=mIconMinHeight)
				mAndroidHeight = mIconMinHeight;			
			if(mAndroidHeight >=mIconMaxHeight)
				mAndroidHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mAndroidPresentX<=mAndroidWidth/2)
				mAndroidPresentX = mAndroidWidth/2;
			if(mAndroidPresentX>=intScreenX - mAndroidWidth/2)
				mAndroidPresentX = intScreenX - mAndroidWidth/2;
			if(mAndroidPresentY<=mAndroidHeight/2)
				mAndroidPresentY = mAndroidHeight/2;
			if(mAndroidPresentY>=intScreenY - mAndroidHeight/2)
				mAndroidPresentY = intScreenY - mAndroidHeight/2;
			
//			Log.d("Fish", "mAndroidHeight "+mAndroidHeight);
//			Log.d("Fish", "mAndroidPresentY "+mAndroidPresentY);
			
			mImageAndroid.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mAndroidWidth, mAndroidHeight, mAndroidPresentX
							- mAndroidWidth / 2, mAndroidPresentY
							- mAndroidHeight/2));


			
			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mAndroidPresentX
							- mBubbleWidth / 2, mAndroidPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mAndroidMove = true;
			setAndroidAlpha();
		}
	}
	private void stbMove(float x, float y) {
		if (((x >= mStbPresentX - mStbWidth / 2) && (x <= mStbPresentX
				+ mStbWidth / 2))
				&& ((y >= mStbPresentY - mStbHeight / 2) && (y <= mStbPresentY
						+ mStbHeight / 2))) {
			if ((x >= mStbPresentX - 2) && (x <= mStbPresentX + 2)
					&& (y >= mStbPresentY - 2)
					&& (y <= mStbPresentY + 2)) {

				mImageStb.setImageDrawable(mAnimStbTouch);
				mAnimStbTouch.start();
			} else
				mAnimStbTouch.stop();
		
			mStbPresentX = (int) x;
			mStbPresentY = (int) y;

			mStbWidth = (int)(mIconScale*mStbPresentY*mIconStandardWidth/intScreenY);
			mStbHeight = (int)(mIconScale*mStbPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mStbPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mStbPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mStbWidth <=mIconMinWidth)
				mStbWidth = mIconMinWidth;
			if(mStbWidth >=mIconMaxWidth)
				mStbWidth = mIconMaxWidth;

			if(mStbHeight <=mIconMinHeight)
				mStbHeight = mIconMinHeight;			
			if(mStbHeight >=mIconMaxHeight)
				mStbHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mStbPresentX<=mStbWidth/2)
				mStbPresentX = mStbWidth/2;
			if(mStbPresentX>=intScreenX - mStbWidth/2)
				mStbPresentX = intScreenX - mStbWidth/2;
			if(mStbPresentY<=mStbHeight/2)
				mStbPresentY = mStbHeight/2;
			if(mStbPresentY>=intScreenY - mStbHeight/2)
				mStbPresentY = intScreenY - mStbHeight/2;
			
			mImageStb.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mStbWidth, mStbHeight, mStbPresentX
							- mStbWidth / 2, mStbPresentY
							- mStbHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mStbPresentX
							- mBubbleWidth / 2, mStbPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mStbMove = true;
			setStbAlpha();
		}
	}
	private void tvMove(float x, float y) {
		if (((x >= mTvPresentX - mTvWidth / 2) && (x <= mTvPresentX
				+ mTvWidth / 2))
				&& ((y >= mTvPresentY - mTvHeight / 2) && (y <= mTvPresentY
						+ mTvHeight / 2))) {
			if ((x >= mTvPresentX - 2) && (x <= mTvPresentX + 2)
					&& (y >= mTvPresentY - 2)
					&& (y <= mTvPresentY + 2)) {

				mImageTv.setImageDrawable(mAnimTvTouch);
				mAnimTvTouch.start();
			} else
				mAnimTvTouch.stop();
		
			mTvPresentX = (int) x;
			mTvPresentY = (int) y;

			mTvWidth = (int)(mIconScale*mTvPresentY*mIconStandardWidth/intScreenY);
			mTvHeight = (int)(mIconScale*mTvPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mTvPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mTvPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mTvWidth <=mIconMinWidth)
				mTvWidth = mIconMinWidth;
			if(mTvWidth >=mIconMaxWidth)
				mTvWidth = mIconMaxWidth;

			if(mTvHeight <=mIconMinHeight)
				mTvHeight = mIconMinHeight;			
			if(mTvHeight >=mIconMaxHeight)
				mTvHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mTvPresentX<=mTvWidth/2)
				mTvPresentX = mTvWidth/2;
			if(mTvPresentX>=intScreenX - mTvWidth/2)
				mTvPresentX = intScreenX - mTvWidth/2;
			if(mTvPresentY<=mTvHeight/2)
				mTvPresentY = mTvHeight/2;
			if(mTvPresentY>=intScreenY - mTvHeight/2)
				mTvPresentY = intScreenY - mTvHeight/2;
			
			mImageTv.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mTvWidth, mTvHeight, mTvPresentX
							- mTvWidth / 2, mTvPresentY
							- mTvHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mTvPresentX
							- mBubbleWidth / 2, mTvPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mTvMove = true;
			setTvAlpha();
		}
	}
	private void movieMove(float x, float y) {
		if (((x >= mMoviePresentX - mMovieWidth / 2) && (x <= mMoviePresentX
				+ mMovieWidth / 2))
				&& ((y >= mMoviePresentY - mMovieHeight / 2) && (y <= mMoviePresentY
						+ mMovieHeight / 2))) {
			if ((x >= mMoviePresentX - 2) && (x <= mMoviePresentX + 2)
					&& (y >= mMoviePresentY - 2)
					&& (y <= mMoviePresentY + 2)) {

				mImageMovie.setImageDrawable(mAnimMovieTouch);
				mAnimMovieTouch.start();
			} else
				mAnimMovieTouch.stop();
		
			mMoviePresentX = (int) x;
			mMoviePresentY = (int) y;

			mMovieWidth = (int)(mIconScale*mMoviePresentY*mIconStandardWidth/intScreenY);
			mMovieHeight = (int)(mIconScale*mMoviePresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mMoviePresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mMoviePresentY*mBubbleStandardHeight/intScreenY);
				
			if(mMovieWidth <=mIconMinWidth)
				mMovieWidth = mIconMinWidth;
			if(mMovieWidth >=mIconMaxWidth)
				mMovieWidth = mIconMaxWidth;

			if(mMovieHeight <=mIconMinHeight)
				mMovieHeight = mIconMinHeight;			
			if(mMovieHeight >=mIconMaxHeight)
				mMovieHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mMoviePresentX<=mMovieWidth/2)
				mMoviePresentX = mMovieWidth/2;
			if(mMoviePresentX>=intScreenX - mMovieWidth/2)
				mMoviePresentX = intScreenX - mMovieWidth/2;
			if(mMoviePresentY<=mMovieHeight/2)
				mMoviePresentY = mMovieHeight/2;
			if(mMoviePresentY>=intScreenY - mMovieHeight/2)
				mMoviePresentY = intScreenY - mMovieHeight/2;
			
			mImageMovie.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mMovieWidth, mMovieHeight, mMoviePresentX
							- mMovieWidth / 2, mMoviePresentY
							- mMovieHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mMoviePresentX
							- mBubbleWidth / 2, mMoviePresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mMovieMove = true;
			setMovieAlpha();
		}
	}
	private void settingMove(float x, float y) {
		if (((x >= mSettingPresentX - mSettingWidth / 2) && (x <= mSettingPresentX
				+ mSettingWidth / 2))
				&& ((y >= mSettingPresentY - mSettingHeight / 2) && (y <= mSettingPresentY
						+ mSettingHeight / 2))) {
			if ((x >= mSettingPresentX - 2) && (x <= mSettingPresentX + 2)
					&& (y >= mSettingPresentY - 2)
					&& (y <= mSettingPresentY + 2)) {

				mImageSetting.setImageDrawable(mAnimSettingTouch);
				mAnimSettingTouch.start();
			} else
				mAnimSettingTouch.stop();
		
			mSettingPresentX = (int) x;
			mSettingPresentY = (int) y;

			mSettingWidth = (int)(mIconScale*mSettingPresentY*mIconStandardWidth/intScreenY);
			mSettingHeight = (int)(mIconScale*mSettingPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mSettingPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mSettingPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mSettingWidth <=mIconMinWidth)
				mSettingWidth = mIconMinWidth;
			if(mSettingWidth >=mIconMaxWidth)
				mSettingWidth = mIconMaxWidth;

			if(mSettingHeight <=mIconMinHeight)
				mSettingHeight = mIconMinHeight;			
			if(mSettingHeight >=mIconMaxHeight)
				mSettingHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mSettingPresentX<=mSettingWidth/2)
				mSettingPresentX = mSettingWidth/2;
			if(mSettingPresentX>=intScreenX - mSettingWidth/2)
				mSettingPresentX = intScreenX - mSettingWidth/2;
			if(mSettingPresentY<=mSettingHeight/2)
				mSettingPresentY = mSettingHeight/2;
			if(mSettingPresentY>=intScreenY - mSettingHeight/2)
				mSettingPresentY = intScreenY - mSettingHeight/2;
			
			mImageSetting.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mSettingWidth, mSettingHeight, mSettingPresentX
							- mSettingWidth / 2, mSettingPresentY
							- mSettingHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mSettingPresentX
							- mBubbleWidth / 2, mSettingPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mSettingMove = true;
			setSettingAlpha();
		}
	}
	
	private void netbrowserMove(float x, float y) {
		if (((x >= mNetbrowserPresentX - mNetbrowserWidth / 2) && (x <= mNetbrowserPresentX
				+ mNetbrowserWidth / 2))
				&& ((y >= mNetbrowserPresentY - mNetbrowserHeight / 2) && (y <= mNetbrowserPresentY
						+ mNetbrowserHeight / 2))) {
			if ((x >= mNetbrowserPresentX - 2) && (x <= mNetbrowserPresentX + 2)
					&& (y >= mNetbrowserPresentY - 2)
					&& (y <= mNetbrowserPresentY + 2)) {

				mImageNetbrowser.setImageDrawable(mAnimNetbrowserTouch);
				mAnimNetbrowserTouch.start();
			} else
				mAnimNetbrowserTouch.stop();
		
			mNetbrowserPresentX = (int) x;
			mNetbrowserPresentY = (int) y;

			mNetbrowserWidth = (int)(mIconScale*mNetbrowserPresentY*mIconStandardWidth/intScreenY);
			mNetbrowserHeight = (int)(mIconScale*mNetbrowserPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mNetbrowserPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mNetbrowserPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mNetbrowserWidth <=mIconMinWidth)
				mNetbrowserWidth = mIconMinWidth;
			if(mNetbrowserWidth >=mIconMaxWidth)
				mNetbrowserWidth = mIconMaxWidth;

			if(mNetbrowserHeight <=mIconMinHeight)
				mNetbrowserHeight = mIconMinHeight;			
			if(mNetbrowserHeight >=mIconMaxHeight)
				mNetbrowserHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mNetbrowserPresentX<=mNetbrowserWidth/2)
				mNetbrowserPresentX = mNetbrowserWidth/2;
			if(mNetbrowserPresentX>=intScreenX - mNetbrowserWidth/2)
				mNetbrowserPresentX = intScreenX - mNetbrowserWidth/2;
			if(mNetbrowserPresentY<=mNetbrowserHeight/2)
				mNetbrowserPresentY = mNetbrowserHeight/2;
			if(mNetbrowserPresentY>=intScreenY - mNetbrowserHeight/2)
				mNetbrowserPresentY = intScreenY - mNetbrowserHeight/2;
			
			mImageNetbrowser.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mNetbrowserWidth, mNetbrowserHeight, mNetbrowserPresentX
							- mNetbrowserWidth / 2, mNetbrowserPresentY
							- mNetbrowserHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mNetbrowserPresentX
							- mBubbleWidth / 2, mNetbrowserPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mNetbrowserMove = true;
			setNetbrowserAlpha();
		}
	}
	private void pictureMove(float x, float y) {
		if (((x >= mPicturePresentX - mPictureWidth / 2) && (x <= mPicturePresentX
				+ mPictureWidth / 2))
				&& ((y >= mPicturePresentY - mPictureHeight / 2) && (y <= mPicturePresentY
						+ mPictureHeight / 2))) {
			if ((x >= mPicturePresentX - 2) && (x <= mPicturePresentX + 2)
					&& (y >= mPicturePresentY - 2)
					&& (y <= mPicturePresentY + 2)) {

				mImagePicture.setImageDrawable(mAnimPictureTouch);
				mAnimPictureTouch.start();
			} else
				mAnimPictureTouch.stop();
		
			mPicturePresentX = (int) x;
			mPicturePresentY = (int) y;

			mPictureWidth = (int)(mIconScale*mPicturePresentY*mIconStandardWidth/intScreenY);
			mPictureHeight = (int)(mIconScale*mPicturePresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mPicturePresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mPicturePresentY*mBubbleStandardHeight/intScreenY);
				
			if(mPictureWidth <=mIconMinWidth)
				mPictureWidth = mIconMinWidth;
			if(mPictureWidth >=mIconMaxWidth)
				mPictureWidth = mIconMaxWidth;

			if(mPictureHeight <=mIconMinHeight)
				mPictureHeight = mIconMinHeight;			
			if(mPictureHeight >=mIconMaxHeight)
				mPictureHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mPicturePresentX<=mPictureWidth/2)
				mPicturePresentX = mPictureWidth/2;
			if(mPicturePresentX>=intScreenX - mPictureWidth/2)
				mPicturePresentX = intScreenX - mPictureWidth/2;
			if(mPicturePresentY<=mPictureHeight/2)
				mPicturePresentY = mPictureHeight/2;
			if(mPicturePresentY>=intScreenY - mPictureHeight/2)
				mPicturePresentY = intScreenY - mPictureHeight/2;
			
			mImagePicture.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mPictureWidth, mPictureHeight, mPicturePresentX
							- mPictureWidth / 2, mPicturePresentY
							- mPictureHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mPicturePresentX
							- mBubbleWidth / 2, mPicturePresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mPictureMove = true;
			setPictureAlpha();
		}
	}
	private void musicMove(float x, float y) {
		if (((x >= mMusicPresentX - mMusicWidth / 2) && (x <= mMusicPresentX
				+ mMusicWidth / 2))
				&& ((y >= mMusicPresentY - mMusicHeight / 2) && (y <= mMusicPresentY
						+ mMusicHeight / 2))) {
			if ((x >= mMusicPresentX - 2) && (x <= mMusicPresentX + 2)
					&& (y >= mMusicPresentY - 2)
					&& (y <= mMusicPresentY + 2)) {

				mImageMusic.setImageDrawable(mAnimMusicTouch);
				mAnimMusicTouch.start();
			} else
				mAnimMusicTouch.stop();
		
			mMusicPresentX = (int) x;
			mMusicPresentY = (int) y;

			mMusicWidth = (int)(mIconScale*mMusicPresentY*mIconStandardWidth/intScreenY);
			mMusicHeight = (int)(mIconScale*mMusicPresentY*mIconStandardHeight/intScreenY);

			mBubbleWidth = (int)(mBubbleScale*mMusicPresentY*mBubbleStandardWidth/intScreenY);
		    mBubbleHeight = (int)(mBubbleScale*mMusicPresentY*mBubbleStandardHeight/intScreenY);
				
			if(mMusicWidth <=mIconMinWidth)
				mMusicWidth = mIconMinWidth;
			if(mMusicWidth >=mIconMaxWidth)
				mMusicWidth = mIconMaxWidth;

			if(mMusicHeight <=mIconMinHeight)
				mMusicHeight = mIconMinHeight;			
			if(mMusicHeight >=mIconMaxHeight)
				mMusicHeight = mIconMaxHeight;
			
			if(mBubbleWidth <=mBubbleMinWidth)
				mBubbleWidth = mBubbleMinWidth;
			if(mBubbleWidth >=mBubbleMaxWidth)
				mBubbleWidth = mBubbleMaxWidth;

			if(mBubbleHeight <=mBubbleMinHeight)
				mBubbleHeight = mBubbleMinHeight;			
			if(mBubbleHeight >=mBubbleMaxHeight)
				mBubbleHeight = mBubbleMaxHeight;
			
			if(mMusicPresentX<=mMusicWidth/2)
				mMusicPresentX = mMusicWidth/2;
			if(mMusicPresentX>=intScreenX - mMusicWidth/2)
				mMusicPresentX = intScreenX - mMusicWidth/2;
			if(mMusicPresentY<=mMusicHeight/2)
				mMusicPresentY = mMusicHeight/2;
			if(mMusicPresentY>=intScreenY - mMusicHeight/2)
				mMusicPresentY = intScreenY - mMusicHeight/2;
			
			mImageMusic.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mMusicWidth, mMusicHeight, mMusicPresentX
							- mMusicWidth / 2, mMusicPresentY
							- mMusicHeight/2));

			mImageBubble.setLayoutParams(new AbsoluteLayout.LayoutParams(
					mBubbleWidth, mBubbleHeight, mMusicPresentX
							- mBubbleWidth / 2, mMusicPresentY-mBubbleHeight/2));
			
			mImageBubble.setImageDrawable(mAnimBubble);
			mAnimBubble.start();
			
//			mMusicMove = true;
			setMusicAlpha();
		}
	}
	

    private void setAndroidAlpha()
    {
		mAndroidAlpha = 255 * 2*mAndroidPresentY / intScreenY;
		
		if (mAndroidAlpha < mMinAlpha)
			mAndroidAlpha = mMinAlpha;
		if (mAndroidAlpha > mMaxAlpha)
			mAndroidAlpha = mMaxAlpha;

		mImageAndroid.setAlpha(mAndroidAlpha);
		mImageBubble.setAlpha(mAndroidAlpha);
	}

    private void setStbAlpha()
    {
		mStbAlpha = 255 * 2*mStbPresentY / intScreenY;
		
		if (mStbAlpha < mMinAlpha)
			mStbAlpha = mMinAlpha;
		if (mStbAlpha > mMaxAlpha)
			mStbAlpha = mMaxAlpha;

		mImageStb.setAlpha(mStbAlpha);
		mImageBubble.setAlpha(mStbAlpha);
	}
    private void setTvAlpha()
    {
		mTvAlpha = 255 * 2*mTvPresentY / intScreenY;
		
		if (mTvAlpha < mMinAlpha)
			mTvAlpha = mMinAlpha;
		if (mTvAlpha > mMaxAlpha)
			mTvAlpha = mMaxAlpha;

		mImageTv.setAlpha(mTvAlpha);
		mImageBubble.setAlpha(mTvAlpha);
	}
    private void setMovieAlpha()
    {
		mMovieAlpha = 255 * 2*mMoviePresentY / intScreenY;
		
		if (mMovieAlpha < mMinAlpha)
			mMovieAlpha = mMinAlpha;
		if (mMovieAlpha > mMaxAlpha)
			mMovieAlpha = mMaxAlpha;

		mImageMovie.setAlpha(mMovieAlpha);
		mImageBubble.setAlpha(mMovieAlpha);
	}
    private void setSettingAlpha()
    {
    	mSettingAlpha = 255 * 2*mSettingPresentY / intScreenY;
		
		if (mSettingAlpha < mMinAlpha)
			mSettingAlpha = mMinAlpha;
		if (mSettingAlpha > mMaxAlpha)
			mSettingAlpha = mMaxAlpha;

		mImageSetting.setAlpha(mSettingAlpha);
		mImageBubble.setAlpha(mSettingAlpha);
	}
    private void setNetbrowserAlpha()
    {
		mNetbrowserAlpha = 255 * 2*mNetbrowserPresentY / intScreenY;
		
		if (mNetbrowserAlpha < mMinAlpha)
			mNetbrowserAlpha = mMinAlpha;
		if (mNetbrowserAlpha > mMaxAlpha)
			mNetbrowserAlpha = mMaxAlpha;

		mImageNetbrowser.setAlpha(mNetbrowserAlpha);
		mImageBubble.setAlpha(mNetbrowserAlpha);
	}
    private void setPictureAlpha()
    {
		mPictureAlpha = 255 * 2*mPicturePresentY / intScreenY;
		
		if (mPictureAlpha < mMinAlpha)
			mPictureAlpha = mMinAlpha;
		if (mPictureAlpha > mMaxAlpha)
			mPictureAlpha = mMaxAlpha;

		mImagePicture.setAlpha(mPictureAlpha);
		mImageBubble.setAlpha(mPictureAlpha);
	}
    private void setMusicAlpha()
    {
		mMusicAlpha = 255 * 2*mMusicPresentY / intScreenY;
		
		if (mMusicAlpha < mMinAlpha)
			mMusicAlpha = mMinAlpha;
		if (mMusicAlpha > mMaxAlpha)
			mMusicAlpha = mMaxAlpha;

		mImageMusic.setAlpha(mMusicAlpha);
		mImageBubble.setAlpha(mMusicAlpha);
	}
    
	private void androidNormalState(float x, float y) {
		if ((x >= mAndroidPresentX - mAndroidWidth / 2)
				&& (x <= mAndroidPresentX + mAndroidWidth / 2)
				&& (y >= mAndroidPresentY - mAndroidHeight / 2)
				&& (y <= mAndroidPresentY + mAndroidHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mAndroidMove = false;
			mAndroidNormalX = mAndroidPresentX;
			mAndroidNormalY = mAndroidPresentY;
			
			mImageAndroid.setImageResource(R.drawable.android_normal);
			mHandler.post(AndroidNormalRun);
		}
	}

	private void stbNormalState(float x, float y) {
		if ((x >= mStbPresentX - mStbWidth / 2)
				&& (x <= mStbPresentX + mStbWidth / 2)
				&& (y >= mStbPresentY - mStbHeight / 2)
				&& (y <= mStbPresentY + mStbHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mStbMove = false;
			mStbNormalX = mStbPresentX;
			mStbNormalY = mStbPresentY;
			mImageStb.setImageResource(R.drawable.stb_normal);
			mHandler.post(StbNormalRun);
		}
	}
	
	private void tvNormalState(float x, float y) {
		if ((x >= mTvPresentX - mTvWidth / 2)
				&& (x <= mTvPresentX + mTvWidth / 2)
				&& (y >= mTvPresentY - mTvHeight / 2)
				&& (y <= mTvPresentY + mTvHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mTvMove = false;
			mTvNormalX = mTvPresentX;
			mTvNormalY = mTvPresentY;
			mImageTv.setImageResource(R.drawable.tv_normal);
			mHandler.post(TvNormalRun);
		}
	}
	
	private void movieNormalState(float x, float y) {
		if ((x >= mMoviePresentX - mMovieWidth / 2)
				&& (x <= mMoviePresentX + mMovieWidth / 2)
				&& (y >= mMoviePresentY - mMovieHeight / 2)
				&& (y <= mMoviePresentY + mMovieHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mMovieMove = false;
			mMovieNormalX = mMoviePresentX;
			mMovieNormalY = mMoviePresentY;
			mImageMovie.setImageResource(R.drawable.movie_normal);
			mHandler.post(MovieNormalRun);
		}
	}

	private void settingNormalState(float x, float y) {
		if ((x >= mSettingPresentX - mSettingWidth / 2)
				&& (x <= mSettingPresentX + mSettingWidth / 2)
				&& (y >= mSettingPresentY - mSettingHeight / 2)
				&& (y <= mSettingPresentY + mSettingHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mSettingMove = false;
			mSettingNormalX = mSettingPresentX;
			mSettingNormalY = mSettingPresentY;
			mImageSetting.setImageResource(R.drawable.setting_normal);
			mHandler.post(SettingNormalRun);
		}
	}
	
	private void netbrowserNormalState(float x, float y) {
		if ((x >= mNetbrowserPresentX - mNetbrowserWidth / 2)
				&& (x <= mNetbrowserPresentX + mNetbrowserWidth / 2)
				&& (y >= mNetbrowserPresentY - mNetbrowserHeight / 2)
				&& (y <= mNetbrowserPresentY + mNetbrowserHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mNetbrowserMove = false;
			mNetbrowserNormalX = mNetbrowserPresentX;
			mNetbrowserNormalY = mNetbrowserPresentY;
			mImageNetbrowser.setImageResource(R.drawable.netbrowser_normal);
			mHandler.post(NetbrowserNormalRun);
		}
	}

	private void pictureNormalState(float x, float y) {
		if ((x >= mPicturePresentX - mPictureWidth / 2)
				&& (x <= mPicturePresentX + mPictureWidth / 2)
				&& (y >= mPicturePresentY - mPictureHeight / 2)
				&& (y <= mPicturePresentY + mPictureHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mPictureMove = false;
			mPictureNormalX = mPicturePresentX;
			mPictureNormalY = mPicturePresentY;
			mImagePicture.setImageResource(R.drawable.picture_normal);
			mHandler.post(PictureNormalRun);
		}
	}

	private void musicNormalState(float x, float y) {
		if ((x >= mMusicPresentX - mMusicWidth / 2)
				&& (x <= mMusicPresentX + mMusicWidth / 2)
				&& (y >= mMusicPresentY - mMusicHeight / 2)
				&& (y <= mMusicPresentY + mMusicHeight / 2)) {
			
//			mAnimBubble.stop();
//			mImageBubble.setImageResource(R.drawable.bubble_blank);
			
//			mMusicMove = false;
			mMusicNormalX = mMusicPresentX;
			mMusicNormalY = mMusicPresentY;
			mImageMusic.setImageResource(R.drawable.music_normal);
			mHandler.post(MusicNormalRun);
		}
	}
	
	public void androidPositionInit()
	{
		mAndroidPresentX = 522;
		mAndroidPresentY = 176;

		mAndroidNormalX = 522;
		mAndroidNormalY = 176;
	
		mAndroidWidth = (int)(mIconScale*mAndroidPresentY*mIconStandardWidth/intScreenY);
		mAndroidHeight = (int)(mIconScale*mAndroidPresentY*mIconStandardHeight/intScreenY);
		
		if(mAndroidWidth <=mIconMinWidth)
			mAndroidWidth = mIconMinWidth;
		if(mAndroidWidth >=mIconMaxWidth)
			mAndroidWidth = mIconMaxWidth;

		if(mAndroidHeight <=mIconMinHeight)
			mAndroidHeight = mIconMinHeight;			
		if(mAndroidHeight >=mIconMaxHeight)
			mAndroidHeight = mIconMaxHeight;
	}
	public void stbPositionInit()
	{
		mStbPresentX = 691;
		mStbPresentY = 171;

		mStbNormalX = 691;
		mStbNormalY = 171;
	
		mStbWidth = (int)(mIconScale*mStbPresentY*mIconStandardWidth/intScreenY);
		mStbHeight = (int)(mIconScale*mStbPresentY*mIconStandardHeight/intScreenY);

		if(mStbWidth <=mIconMinWidth)
			mStbWidth = mIconMinWidth;
		if(mStbWidth >=mIconMaxWidth)
			mStbWidth = mIconMaxWidth;

		if(mStbHeight <=mIconMinHeight)
			mStbHeight = mIconMinHeight;			
		if(mStbHeight >=mIconMaxHeight)
			mStbHeight = mIconMaxHeight;
	}
	
	public void tvPositionInit()
	{
		mTvPresentX = 615;
		mTvPresentY = 285;

		mTvNormalX = 615;
		mTvNormalY = 285;
		
		mTvWidth = (int)(mIconScale*mTvPresentY*mIconStandardWidth/intScreenY);
		mTvHeight = (int)(mIconScale*mTvPresentY*mIconStandardHeight/intScreenY);
		
		if(mTvWidth <=mIconMinWidth)
			mTvWidth = mIconMinWidth;
		if(mTvWidth >=mIconMaxWidth)
			mTvWidth = mIconMaxWidth;

		if(mTvHeight <=mIconMinHeight)
			mTvHeight = mIconMinHeight;			
		if(mTvHeight >=mIconMaxHeight)
			mTvHeight = mIconMaxHeight;
	}
	
	public void moviePositionInit()
	{
		mMoviePresentX = 40;
		mMoviePresentY = 296;

		mMovieNormalX = 40;
		mMovieNormalY = 296;
		
		mMovieWidth = (int)(mIconScale*mMoviePresentY*mIconStandardWidth/intScreenY);
		mMovieHeight = (int)(mIconScale*mMoviePresentY*mIconStandardHeight/intScreenY);
		
		if(mMovieWidth <=mIconMinWidth)
			mMovieWidth = mIconMinWidth;
		if(mMovieWidth >=mIconMaxWidth)
			mMovieWidth = mIconMaxWidth;

		if(mMovieHeight <=mIconMinHeight)
			mMovieHeight = mIconMinHeight;			
		if(mMovieHeight >=mIconMaxHeight)
			mMovieHeight = mIconMaxHeight;
	}
	public void settingPositionInit()
	{
		mSettingPresentX = 141;
		mSettingPresentY = 154;

		mSettingNormalX = 141;
		mSettingNormalY = 154;
	
		mSettingWidth = (int)(mIconScale*mSettingPresentY*mIconStandardWidth/intScreenY);
		mSettingHeight = (int)(mIconScale*mSettingPresentY*mIconStandardHeight/intScreenY);
		
		if(mSettingWidth <=mIconMinWidth)
			mSettingWidth = mIconMinWidth;
		if(mSettingWidth >=mIconMaxWidth)
			mSettingWidth = mIconMaxWidth;

		if(mSettingHeight <=mIconMinHeight)
			mSettingHeight = mIconMinHeight;			
		if(mSettingHeight >=mIconMaxHeight)
			mSettingHeight = mIconMaxHeight;
	}
	public void netbrowserPositionInit()
	{
		mNetbrowserPresentX = 201;
		mNetbrowserPresentY = 295;

		mNetbrowserNormalX = 201;
		mNetbrowserNormalY = 295;
	
		mNetbrowserWidth = (int)(mIconScale*mNetbrowserPresentY*mIconStandardWidth/intScreenY);
		mNetbrowserHeight = (int)(mIconScale*mNetbrowserPresentY*mIconStandardHeight/intScreenY);
		
		if(mNetbrowserWidth <=mIconMinWidth)
			mNetbrowserWidth = mIconMinWidth;
		if(mNetbrowserWidth >=mIconMaxWidth)
			mNetbrowserWidth = mIconMaxWidth;

		if(mNetbrowserHeight <=mIconMinHeight)
			mNetbrowserHeight = mIconMinHeight;			
		if(mNetbrowserHeight >=mIconMaxHeight)
			mNetbrowserHeight = mIconMaxHeight;
		
		
	}
	public void picturePositionInit()
	{
		mPicturePresentX = 390;
		mPicturePresentY = 262;

		mPictureNormalX = 390;
		mPictureNormalY = 262;

		mPictureWidth = 134;
		mPictureHeight = 150;
		
		mPictureWidth = (int)(mIconScale*mPicturePresentY*mIconStandardWidth/intScreenY);
		mPictureHeight = (int)(mIconScale*mPicturePresentY*mIconStandardHeight/intScreenY);
		
		if(mPictureWidth <=mIconMinWidth)
			mPictureWidth = mIconMinWidth;
		if(mPictureWidth >=mIconMaxWidth)
			mPictureWidth = mIconMaxWidth;

		if(mPictureHeight <=mIconMinHeight)
			mPictureHeight = mIconMinHeight;			
		if(mPictureHeight >=mIconMaxHeight)
			mPictureHeight = mIconMaxHeight;
	}
	public void musicPositionInit()
	{
		mMusicPresentX = 313;
		mMusicPresentY = 189;

		mMusicNormalX = 313;
		mMusicNormalY = 189;

		mMusicWidth = 80;
		mMusicHeight = 106;
		
		mMusicWidth = (int)(mIconScale*mMusicPresentY*mIconStandardWidth/intScreenY);
		mMusicHeight = (int)(mIconScale*mMusicPresentY*mIconStandardHeight/intScreenY);
		
		if(mMusicWidth <=mIconMinWidth)
			mMusicWidth = mIconMinWidth;
		if(mMusicWidth >=mIconMaxWidth)
			mMusicWidth = mIconMaxWidth;

		if(mMusicHeight <=mIconMinHeight)
			mMusicHeight = mIconMinHeight;			
		if(mMusicHeight >=mIconMaxHeight)
			mMusicHeight = mIconMaxHeight;
	}

	public void RestoreIcon() {
		
		androidPositionInit();
		stbPositionInit();
		tvPositionInit();
		moviePositionInit();
		settingPositionInit();
		netbrowserPositionInit();
		picturePositionInit();
		musicPositionInit();
		
		setAndroidAlpha();
		setStbAlpha();
		setTvAlpha();
		setMovieAlpha();
		setSettingAlpha();
		setNetbrowserAlpha();
		setPictureAlpha();
		setMusicAlpha();
	}

}