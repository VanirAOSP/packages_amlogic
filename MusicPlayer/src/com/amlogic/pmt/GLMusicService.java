package com.amlogic.pmt;

import android.app.Service;
import android.media.MediaPlayer;
import android.util.Log;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import java.util.List;
import java.io.IOException;

import java.lang.ref.WeakReference;
import android.media.AudioManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class GLMusicService extends Service {
	
	public MediaPlayer mediaPlayer;  
	private static final String TAG = "GLMusicService";
	protected DataProvider dataProvider=null;
	//public final IBinder binder = new MyBinder(); 
	private final IBinder binder = new ServiceStub(this);
	private boolean isMusicLayoutExist=false;
	private String currentMusicPath="none";
	private int mServiceStartId = -1;
	
	private boolean mIsInitialized = false;
	private AudioManager mAudioManager;
    public static final String SERVICECMD = "com.android.pmt.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

	public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";

	/*public class MyBinder extends Binder{
		public GLMusicService getService(){
			return GLMusicService.this;

		}
	}*/
	
	static class ServiceStub extends IGLMusicService.Stub {
		WeakReference<GLMusicService> mService;
        
        ServiceStub(GLMusicService service) {
            mService = new WeakReference<GLMusicService>(service);
        }
		
        public boolean isPlaying() 
	{
            if( (mService != null) && (mService.get() != null) )
                return mService.get().isPlaying();
            else
                return false;
        }
        public void stop() {
            if( (mService != null) && (mService.get() != null) )
                mService.get().stop();
        }
        public void pause() {
            if( (mService != null) && (mService.get() != null) )
                mService.get().pause();
        }
        public void play() {
            if( (mService != null) && (mService.get() != null) )
                mService.get().play();
        }
        public void prev() {
            if( (mService != null) && (mService.get() != null) )
                mService.get().prev(true);
        }
        public void next() {
            if( (mService != null) && (mService.get() != null) )
                mService.get().next(true);
        }

		public void setSwitchMode(int repeatmode) {
            if( (mService != null) && (mService.get() != null) )
                mService.get().setSwitchMode(repeatmode);
        }
		public int getCurrentPosition(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getCurrentPosition();
            else
                return 0;
		}
		public void reset(){
            if( (mService != null) && (mService.get() != null) )
                mService.get().reset();
		}
		public void setDataSource(String path){
            if( (mService != null) && (mService.get() != null) )
                mService.get().setDataSource(path);
		}
		public void prepare(){
            if( (mService != null) && (mService.get() != null) )
                mService.get().prepare();
		}
		public void start(){
            if( (mService != null) && (mService.get() != null) )
                mService.get().start();
		}
		public int getDuration(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getDuration();
            else
                return 0;
		}
		public void seekTo(int msec){
            if( (mService != null) && (mService.get() != null) )
                mService.get().seekTo(msec);
		}
		public boolean isPlayer(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().isPlayer();
            else
                return false;
		}
		public void setIsMusicLayoutExist(boolean b){
            if( (mService != null) && (mService.get() != null) )
                mService.get().setIsMusicLayoutExist(b);
		}
		public boolean getIsMusicLayoutExist(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getIsMusicLayoutExist();
            else
                return false;
		}
		public void setFirstFileName(String name){
            if( (mService != null) && (mService.get() != null) )
                mService.get().setFirstFileName(name);
		}
		public void setFileList(List<String> list){
            if( (mService != null) && (mService.get() != null) )
                mService.get().setFileList(list);
		}

		public void createDataProvider(String location){
            if( (mService != null) && (mService.get() != null) )
                mService.get().createDataProvider(location);
		}
	    public void setCurrentMusicPath(String path){
            if( (mService != null) && (mService.get() != null) )
                mService.get().setCurrentMusicPath(path);
		}
	    public String getNextFile(){
           if( (mService != null) && (mService.get() != null) )
                return mService.get().getNextFile();
            else
                return "";
		}
	    public String getPreFile(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getPreFile();
            else
                return "";
		}			
	    public String getFirstFile(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getFirstFile();
            else
                return "";
		}
	    public String getCurrentMusicPath(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getCurrentMusicPath();
            else
                return "";
		}
	    public String getCurFilePath(){
            if( (mService != null) && (mService.get() != null) )
                return mService.get().getCurFilePath();
            else
                return "";
		}
	}
		
	@Override
	public IBinder onBind(Intent intent){
	return binder; 
	}  
	    
    @Override
    public boolean onUnbind(Intent intent) {
        // Take a snapshot of the current playlist
		if(!isPlaying())
			{
			stopSelf(mServiceStartId);
			}
        return true;
    }
    
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;

        if (intent != null) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
			
			if(dataProvider==null)
			{
			return START_STICKY;
			}

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                if (position() < 2000) {
                    prev(true);
                } else {
                    seekTo(0);
                    play();
                }
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();

                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();

            } else if (CMDSTOP.equals(cmd)) {
                pause();
                seekTo(0);
            }
        }
        
        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
       /* mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);*/
        return START_STICKY;
    }

	@Override
    public void onCreate() {
    super.onCreate();
		
	if (mediaPlayer == null) {  
	   mediaPlayer = new MediaPlayer(); 
	   } 
	mediaPlayer.setOnCompletionListener(compleListener);
	mediaPlayer.setOnErrorListener(Errorlistener);

    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
            MediaButtonReceiver.class.getName()));
    IntentFilter commandFilter = new IntentFilter();
    commandFilter.addAction(SERVICECMD);
    commandFilter.addAction(TOGGLEPAUSE_ACTION);
    commandFilter.addAction(PAUSE_ACTION);
    commandFilter.addAction(NEXT_ACTION);
    commandFilter.addAction(PREVIOUS_ACTION);
    registerReceiver(mIntentReceiver, commandFilter);
		
    }
	
	@Override 
	public void onDestroy(){
	Log.v(TAG, "onDestroy"); 
	super.onDestroy();
	unregisterReceiver(mIntentReceiver);
	if(mediaPlayer != null){  
		mediaPlayer.stop();	
		mediaPlayer.release();  
		}  
	} 

	public long position() {
        if (mIsInitialized) {
            return mediaPlayer.getCurrentPosition();
        }
        return -1;
    }
		
	public void setCurrentMusicPath(String path)
	{
		currentMusicPath=path;
	}
	public String getCurrentMusicPath()
	{
		return currentMusicPath;
	}
	public void createDataProvider(String location)
		{
		if(dataProvider==null)
			{
			dataProvider = new DataProvider(location);
			}
		}

	public String getCurFilePath()
		{
			if(dataProvider == null)
			{
				return null;
			}
			
			return dataProvider.getCurFilePath();
		}

	public void setSwitchMode(int mode)
		{
		dataProvider.setSwitchMode(mode);
		}
	public MediaPlayer getMediaPlayer()
		{
		return mediaPlayer;
		}
	OnCompletionListener compleListener = new OnCompletionListener() {

		// Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			if(isMusicLayoutExist)
				sendBroadcast2Layout("OnCompletion");
			else
				PlayNext();
		}
	};
	OnErrorListener Errorlistener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			if(isMusicLayoutExist)
					sendBroadcast2Layout("OnError");
			else
				{
				try {
					Log.v("Player", "Errorlistener");
					PlayNext();
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			return false;
		}
	};
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
		{
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        String cmd = intent.getStringExtra("command");
		        if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
		            next(true);
		        } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
		            prev(true);
		        } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
		            if (isPlaying()) {
		            	  sendBroadcast2Layout("player_PAUSE");
		                pause();
		            } else {
		            	  sendBroadcast2Layout("player_NORMAL_PLAY");
		                play();
		            }
		        }else if (CMDSTOP.equals(cmd) && PAUSE_ACTION.equals(action)){
		            sendBroadcast2Layout("player_PAUSE");
		        	stop();
				stopSelf(mServiceStartId);
		        }
		        else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
		            sendBroadcast2Layout("player_PAUSE");
		            pause();
		        } else if (CMDSTOP.equals(cmd)) {
		            sendBroadcast2Layout("player_PAUSE");
		            pause();
					seekTo(0);
		        } 
		    }
		};
	
	public String getNextFile() {
		if(dataProvider!=null)
			{
			String filename = dataProvider.getNextFile();
			return filename;	
			}
		else
			return "null";
	}
	
	public String getPreFile() {
		String filename = dataProvider.getPreFile();
		return filename;
	}
	public String getFirstFile(){
		String filename =dataProvider.getFirstFile();
		return filename;
	}
	public void next(boolean b)
		{
		if(b)
			PlayNext();
		}
	
	public void prev(boolean b)
		{
		if(b)
			PlayPre();
		}
	public boolean isPlaying()
		{
		boolean isPlaying = false;
		try
		{
			isPlaying = mediaPlayer.isPlaying();
		}
		catch(Exception e)
		{
			isPlaying = false;
		}
		
		return isPlaying;
		}

	public void PlayNext() {
		
		String filename =getNextFile();
		if(filename=="null")
			stop();
		else
			{
			mediaPlayer.reset();
			playSong(getCurFilePath());
			}
	}
	
	public void PlayPre() {
		
		String filename =getPreFile();
		mediaPlayer.reset();
		playSong(getCurFilePath());
	}
	public void playSong(String locaton) {

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();
		}

		try {
			mediaPlayer.setDataSource(locaton);
			setCurrentMusicPath(locaton);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("Player", "error");
			// PlayNext();
			return;
		}
		mediaPlayer.start();
	}

	public void setFileList(List<String> filename)
	{	
		dataProvider.setFilelist(filename);
	}
	
	public void setFirstFileName(String name)
	{	
		dataProvider.setfirstname(name);
	}
	
	public void setIsMusicLayoutExist(boolean isexist)
		{
		isMusicLayoutExist=isexist;
		}
	
	public boolean getIsMusicLayoutExist()
		{
		return isMusicLayoutExist;
		}
	
	private void sendBroadcast2Layout(String action)
		{
		Intent intent = new Intent("state_chage");
		intent.putExtra("PlayListener", action);
		sendBroadcast(intent);
		}

	public void play() {  
		if (!mediaPlayer.isPlaying()) {	
			mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this.getPackageName(),
						   MediaButtonReceiver.class.getName()));
			mediaPlayer.start();  
			}  
	}  

	public void pause() {  
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {  
			mediaPlayer.pause();  
			}  
	}  
	
	public void stop() {
		if (mediaPlayer != null)
			{
			mediaPlayer.reset();	
			mediaPlayer.stop();
			setCurrentMusicPath("");
			mIsInitialized = false;
			dataProvider=null;
		}  
	}
	public int getCurrentPosition(){
		return mediaPlayer.getCurrentPosition();
	}
	public void reset(){
	try
	{
		mediaPlayer.reset();	
	}
                catch(Exception e)
                {
                        new Exception("exception happens!");
                }

	}
	public void setDataSource(String path){
        try {
			mediaPlayer.reset();
        	mediaPlayer.setDataSource(path);
			mIsInitialized = true;
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        }
	catch(Exception e)
	{
		mIsInitialized = false;
		new Exception("excepation happens!");
		return;
	}
	}
	
	public void prepare(){
        try {
        	mediaPlayer.prepare();
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            return;
        }
	catch(Exception e)
	{
		new Exception("exception happens!");
		
		return;
	}
	}
	public void start()
	{
		try
		{
			mediaPlayer.start();
		}
		catch(Exception e)
		{
			new Exception("exception happens!");	
		}
	}
	public int getDuration(){
	try
	{
		return mediaPlayer.getDuration();
	}
	                catch(Exception e)
                {
                        new Exception("exception happens!");
			return 0;
                }

	}
	public void seekTo(int msec){
	try
	{
		mediaPlayer.seekTo(msec);
	}
	                catch(Exception e)
                {
                        new Exception("exception happens!");
                }

	}
	public boolean isPlayer(){
		if(mediaPlayer!=null)
			return true;
		else 
			return false;
	}
}
