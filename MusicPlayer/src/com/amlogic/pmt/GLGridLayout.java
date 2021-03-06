package com.amlogic.pmt;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.amlogic.pmt.browser.Devicebrowser;
import com.amlogic.pmt.browser.Filebrowser;
import com.amlogic.pmt.browser.GridBrowser;
import java.io.FileOutputStream;

public class GLGridLayout extends GLBaseLayout {

	private int loadingResID = R.raw.loading_msg_en;
	private int loadingTexID = -1;
	static int[] loadingTexRect = new int[]{(1920/2)-180, (1080/2)-50, 0, 360, 100};
	private boolean loadingMsgShown = false;
	private PMTBreatheInOutAnimation breathe = null;
	protected final static int PAGE_UP = 0;
	protected final static int PAGE_DOWN = 1;

	private Waiting mWaitDlg = null;
	private int bgResId = R.drawable.bg;
	private int bgTexID = -1;
	private boolean ldfromMisicLayout= false;
	boolean isSlotsVisable = true;
	private Handler handlerPlay = null;
	private Handler handlerUpdateInfo = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(focusIndex == msg.arg1)
					gridUpdateSelectInfos();
				break;
			default:
				break;
			}
		}
	};
	
	private Handler handlerEnterBack = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				gridEnterBack();
				break;
			default:
				break;
			}
		}
	};
	private Handler handlerDelayShowDev = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
//				FBReader.Instance.ShowDevDisplay();
				break;
			default:
				break;
			}
		}
	};
	
	 public boolean onGenericMotionEvent(MotionEvent event){
	 		int step = (int)event.getAxisValue(MotionEvent.AXIS_VSCROLL);
      int rows = gridGetRows();
      int cols = gridGetCols();
      int count = gridGetItemCount();
      if (step > 1) {
      		if(focusIndex>0){
      				stopAnims();
      				int oldSlt = focusIndex-firstIndex;
      				if(focusIndex - 1 < firstIndex){
      						firstIndex -= cols*rows;
                  focusIndex --;
                  PageUpdateSlots(oldSlt);
              }else{
              		focusIndex --;
              		gridMoveSelected(oldSlt, focusIndex-firstIndex);
              		postUpdateInfoMessage();
              }
          } 
          return true;
      } else {
      		if(step < -1){
      				if(focusIndex>=0 && focusIndex+1 < count){
      						stopAnims();
      						int oldSlt = focusIndex-firstIndex;
      						if(focusIndex + 1 >= firstIndex + rows*cols){
      								firstIndex += cols*rows;
      								focusIndex ++;
      								PageUpdateSlots(oldSlt);
      					  }else{
      					  		focusIndex ++;
      					  		gridMoveSelected(oldSlt, focusIndex-firstIndex);
      					  		postUpdateInfoMessage();
      					  }
      				}
            }
            return true;
        }
     }
	
//	private Handler handlerUpdatePictureInfo = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1:
//				requestThumbImage();
//				break;
//			default:
//				break;
//			}
//		}
//	};
	
	private GLPose disappearAnimPose  = null;
	private ArrayList<DisplaySlot> disappearAnimSlots = null;
	private String focusFileName = null;
	
	private int switchState = SWITCH_IDLE;	
	final static int SWITCH_IDLE = 0;
	
	final static int SWITCH_ENTER_PREPARE 			= 1;
	final static int SWITCH_ENTER_LOADING_TEXTURES 	= 2;
	final static int SWITCH_ENTER_ANIMATING			= 3;
	
	final static int SWITCH_BACK_PREPARE			= 101;
	final static int SWITCH_BACK_LOADING_TEXTURES 	= 102;
	final static int SWITCH_BACK_ANIMATING			= 103;
	
	final static int SWITCH_PAGE					= 201;
	
	static int[] bgTexRect = new int[]{0, 0, 40, 1920, 1080};
	
	float item_center[][];	
	int item_w=0;
	int item_h=0;
	
	final static int IDX_TITLE 		=	0;				//slot for title.
	final static int IDX_INFO_TITLE	=	IDX_TITLE+1;	//slot for information.
	final static int IDX_INFO 		=	IDX_INFO_TITLE+1;	//slot for information.
	final static int IDX_AD 		=	IDX_INFO+1;		//slot for advertisement
	final static int IDX_SCROLL_BK	=	IDX_AD+1;		//slot for scroll background
	final static int IDX_SCROLL_BLOCK=	IDX_SCROLL_BK+1;		//slot for scroll block
	final static int IDX_COUNT		=	IDX_SCROLL_BLOCK+1;		//slot for current/count
	final static int IDX_PROMPT		=	IDX_COUNT+1;		//slot for current/count
	
	static int IDX_SELECTED=IDX_PROMPT+1;		//slot for selected item.
	
	final static int IDX_ITEM0 		=	IDX_SELECTED+1;		//ROWS*COLS slots , for display item.
	
	final static float GL_0ES_X = 1920/2; //960
	final static float GL_0ES_Y = 1080/2; //540
	final static float VERTEX = 130;	//
	final static float FOCUS_SCALE 	= 1.1f;
	final static int ANIM_TIMING 	= 500;
	final static float SCALE_MIN = (float) (1.473/960); 	// 0.001534375f;
//	final static float SELECTED_Z	= 0.001f;
	

//
//((ITEM_W/2+ITEM_X)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-ITEM_Y-(ITEM_H/2))*SCALE_MIN, 0,
//0,0,0,0,
//ITEM_W/VERTEX,ITEM_H/VERTEX,1,
	final static float X_STEP = 450;
	final static float InitPositions[][] = new float[][]{
		{// IDX_TITLE
			((150/2+111)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-36-(38/2))*SCALE_MIN, 0,
			0,0,0,0,
			150/VERTEX,38/VERTEX,1,
		},
		{// IDX_INFO_TITLE
			((200/2+1400)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-93-(36/2))*SCALE_MIN, 0,
			0,0,0,0,
			200/VERTEX,36/VERTEX,1,
		},
		{// IDX_INFO
			((350/2+1420+X_STEP)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-150-(440/2))*SCALE_MIN, 0,
			0,0,0,0,
			350/VERTEX,440/VERTEX,1,
		},
		{// IDX_AD
			((385/2+1398+X_STEP)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-715-(250/2))*SCALE_MIN, 0,
			0,0,0,0,
			385/VERTEX,250/VERTEX,1,
		},
		{// IDX_SCROLL_BK
			((33/2+1385+X_STEP)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-167-(812/2))*SCALE_MIN, 0,
			0,0,0,0,
			10/VERTEX,812/VERTEX,1,
		},
		{// IDX_SCROLL_BLOCK
			((33/2+1385+X_STEP)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-167-(512/2))*SCALE_MIN, 0,
			0,0,0,0,
			10/VERTEX,512/VERTEX,1,
		},
		{// IDX_COUNT
			((300/2+1468)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-56-(20/2))*SCALE_MIN, 0,
			0,0,0,0,
			300/VERTEX,24/VERTEX,1,
		},
		{// IDX_PROMPT
			((702/2+666)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-378-(480/2))*SCALE_MIN, 0,
			0,0,0,0,
			702/VERTEX,480/VERTEX,1,
		},
		{// IDX_SELECTED
			((702/2+366)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-378-(480/2))*SCALE_MIN, 0,
			0,0,0,0,
			702/VERTEX,480/VERTEX,1,
		},
	};
	
	
	
//////////////////////////////////////////////////////////
	public final static int MODE_DEVICE		=	0;
	public final static int MODE_FILE		=	1;
	public final static int MODE_MUSIC		=	2;
	
	/////////// DEVICE MODE //////////////////
	final static int DM_ROWS		=	2;
	final static int DM_COLS		= 	3;
	
	final static float DM_W = 300;
	final static float DM_H = 390;
	final static float DM_X_S1 = 300;  //259
	final static float DM_X_S2 = DM_X_S1+339;
	final static float DM_X_S3 = DM_X_S2+339;
	final static float DM_Y_S = 198;
	
	final static float DM_SCALE_X 	= DM_W/VERTEX;
	final static float DM_SCALE_Y 	= DM_H/VERTEX;
	
	final static float DM_POS_X0 	= ((DM_W/2+DM_X_S1)-GL_0ES_X)*SCALE_MIN;
	final static float DM_POS_X1 	= ((DM_W/2+DM_X_S2)-GL_0ES_X)*SCALE_MIN;
	final static float DM_POS_X2 	= ((DM_W/2+DM_X_S3)-GL_0ES_X)*SCALE_MIN;
	
	final static float DM_POS_Y0 	= (GL_0ES_Y-DM_Y_S-(DM_H/2))*SCALE_MIN;
	final static float DM_POS_Y1 	= DM_POS_Y0-(DM_H*SCALE_MIN)+(2*SCALE_MIN);

	final static float DMGridItemPositions[][] = new float[][]{
		{//icon
			0f, -0.02f, 0,	//position
			0.9f, 1.2f, 1,	//scale
		},
		{//name
			0f, -0.066f, 0,	//position
			0.7f, 0.12f, 1,		//scale
		},
	};
	
	final static float DMSlotPositions[][] = new float[][]{
		{// 0-0
			DM_POS_X0, DM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
		{// 0-1
			DM_POS_X1, DM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
		{// 0-2
			DM_POS_X2, DM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
		{// 1-0
			DM_POS_X0, DM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
		{// 1-1
			DM_POS_X1, DM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
		{// 1-2
			DM_POS_X2, DM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			DM_SCALE_X,DM_SCALE_Y,1,	//Scale
		},
	};
	
	////////// FILE MODE /////////////////////
	
	final static int FM_ROWS		=	3;
	final static int FM_COLS		=	6;
	
	final static int DISTANCE_X = 290;
	final static int DISTANCE_Y = 40;
	
	final static float FM_W = 263;//195;
	final static float FM_H = 274;//278;
	final static float FM_X_S1 = 100;
	final static float FM_X_S2 = FM_X_S1+DISTANCE_X;
	final static float FM_X_S3 = FM_X_S2+DISTANCE_X;
	final static float FM_X_S4 = FM_X_S3+DISTANCE_X;
	final static float FM_X_S5 = FM_X_S4+DISTANCE_X;
	final static float FM_X_S6 = FM_X_S5+DISTANCE_X;
	final static float FM_Y_S = 125;
	
	final static float FM_SCALE_X 	= FM_W/VERTEX;
	final static float FM_SCALE_Y 	= FM_H/VERTEX;
	
	final static float FM_POS_X0 	= ((FM_W/2+FM_X_S1)-GL_0ES_X)*SCALE_MIN;
	final static float FM_POS_X1 	= ((FM_W/2+FM_X_S2)-GL_0ES_X)*SCALE_MIN;
	final static float FM_POS_X2 	= ((FM_W/2+FM_X_S3)-GL_0ES_X)*SCALE_MIN;
	final static float FM_POS_X3 	= ((FM_W/2+FM_X_S4)-GL_0ES_X)*SCALE_MIN;
	final static float FM_POS_X4 	= ((FM_W/2+FM_X_S5)-GL_0ES_X)*SCALE_MIN;
	final static float FM_POS_X5 	= ((FM_W/2+FM_X_S6)-GL_0ES_X)*SCALE_MIN;
	
	final static float FM_POS_Y0 	= (GL_0ES_Y-FM_Y_S-(FM_H/2))*SCALE_MIN;
	final static float FM_POS_Y1 	= FM_POS_Y0-(FM_H*SCALE_MIN)+(2*SCALE_MIN)-DISTANCE_Y*SCALE_MIN;
	final static float FM_POS_Y2 	= FM_POS_Y1-(FM_H*SCALE_MIN)+(2*SCALE_MIN)*2-DISTANCE_Y*SCALE_MIN;
	
	final static float FMGridItemPositions[][] = new float[][]{
		{//icon
			0f, 0.024f, 0,	//position
			//142f/FM_W, 198f/FM_H, 1,	//scale
			206f/FM_W, 146f/FM_H, 1,	//scale
		},
		{//name
			0, -0.07f, 0,	//position
			130f/FM_W, 28f/FM_H, 1,	//scale
		},
	};
	
	final static float FMSlotPositions[][] = new float[][]{
		{// 0-0
			FM_POS_X0, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 0-1
			FM_POS_X1, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 0-2
			FM_POS_X2, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 0-3
			FM_POS_X3, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 0-4
			FM_POS_X4, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 0-5
			FM_POS_X5, FM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-0
			FM_POS_X0, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-1
			FM_POS_X1, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-2
			FM_POS_X2, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-3
			FM_POS_X3, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-4
			FM_POS_X4, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 1-5
			FM_POS_X5, FM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-0
			FM_POS_X0, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-1
			FM_POS_X1, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-2
			FM_POS_X2, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-3
			FM_POS_X3, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-4
			FM_POS_X4, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
		{// 2-5
			FM_POS_X5, FM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			FM_SCALE_X,FM_SCALE_Y,1,	//Scale
		},
	};	
	
	////////// MUSIC MODE /////////////////////
	
	final static int MM_ROWS		=	7;
	final static int MM_COLS		=	2;
	
	final static float MM_W = 556;
	final static float MM_H = 108;
	final static float MM_X_S1 = 306;//136
	final static float MM_X_S2 = 876;//706
	final static float MM_Y_S = 186;
	
	final static float MM_SCALE_X 	= MM_W/VERTEX;  // 556/130
	final static float MM_SCALE_Y 	= MM_H/VERTEX;  //108/130
	
	final static float MM_POS_X0 	= ((MM_W/2+MM_X_S1)-GL_0ES_X)*SCALE_MIN;
	final static float MM_POS_X1 	= ((MM_W/2+MM_X_S2)-GL_0ES_X)*SCALE_MIN;
		
	final static float MM_POS_Y0 	= (GL_0ES_Y-MM_Y_S-(MM_H/2))*SCALE_MIN;
	final static float MM_POS_Y1 	= MM_POS_Y0-(MM_H*SCALE_MIN);
	final static float MM_POS_Y2 	= MM_POS_Y1-(MM_H*SCALE_MIN);
	final static float MM_POS_Y3 	= MM_POS_Y2-(MM_H*SCALE_MIN);
	final static float MM_POS_Y4 	= MM_POS_Y3-(MM_H*SCALE_MIN);
	final static float MM_POS_Y5 	= MM_POS_Y4-(MM_H*SCALE_MIN);
	final static float MM_POS_Y6 	= MM_POS_Y5-(MM_H*SCALE_MIN);
	
	final static float MMGridItemPositions[][] = new float[][]{
		{//icon
			-0.080f, -0.008f, 0,	//position
			38f/MM_W, 40f/MM_H, 1,		//scale
		},
		{//name
			0.01f, 0.02f, 0,		//position
			420f/MM_W, 60f/MM_H, 1,			//scale
		},
	};
	
	final static float MMSlotPositions[][] = new float[][]{
		{// 0-0
			MM_POS_X0, MM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 0-1
			MM_POS_X1, MM_POS_Y0, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 1-0
			MM_POS_X0, MM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 1-1
			MM_POS_X1, MM_POS_Y1, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 2-0
			MM_POS_X0, MM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 2-1
			MM_POS_X1, MM_POS_Y2, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 3-0
			MM_POS_X0, MM_POS_Y3, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 3-1
			MM_POS_X1, MM_POS_Y3, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 4-0
			MM_POS_X0, MM_POS_Y4, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 4-1
			MM_POS_X1, MM_POS_Y4, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 5-0
			MM_POS_X0, MM_POS_Y5, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 5-1
			MM_POS_X1, MM_POS_Y5, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 6-0
			MM_POS_X0, MM_POS_Y6, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
		{// 6-1
			MM_POS_X1, MM_POS_Y6, 0,		//Position
			0,0,0,0,		//Rotation
			MM_SCALE_X,MM_SCALE_Y,1,	//Scale
		},
	};
	private static final String TAG = "GLGridLayout";	
	
/////////////////////////////////////////////////////////


	private String fileType;
	private int curMode = -1;
	private int focusIndex = -1;
	private int firstIndex = 0;
	private boolean adEnable = false;
	private GridBrowser browser = null;
	private boolean doneInitPrompt = false;
	
	GLGridLayout(Context context, String name, boolean showLoading){
		super(context, name, "/sdcard");
		String language = SystemProperties.get("persist.sys.language","en");
		String country = SystemProperties.get("persist.sys.country","US");

		if(language.equals("zh") && country.equals("CN")){
			loadingResID = R.raw.loading_msg_chs;
		}
		else if(language.equals("zh") && country.equals("TW")){
			loadingResID = R.raw.loading_msg_cht;
		}
		else{
			loadingResID = R.raw.loading_msg_en;
		}

		loadingTexRect = new int[]{(int)((1920/2-180)*Resolution.getScaleX()), (int)((1080/2-50)*Resolution.getScaleY()), 0, (int)(360*Resolution.getScaleX()), (int)(100*Resolution.getScaleY())};
		bgTexRect = new int[]{0, 0, 40, (int)(1920*Resolution.getScaleX()), (int)(1080*Resolution.getScaleY())};
		
		loadingMsgShown = !showLoading;
		
		initPosition();
		
		getItem(IDX_SCROLL_BK).SetBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scroll_vert_long_bg));
		getItem(IDX_SCROLL_BLOCK).SetBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scroll_vert_short_light));
		
		String mode =this.myConProvider.getMyParam("ListMode");
		if(!mode.equals(""))
		{
			if(mode.equals("FILE"))
				Filebrowser.IfFolder  = false;
			else
			if(mode.equals("FOLDER"))
				Filebrowser.IfFolder  = true;
		}
		
		adEnable = true;
//		handlerDelayShowDev.sendEmptyMessageDelayed(1, 3000);
	}
	
	private void initPrompt() {
//		Bitmap bkbmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_info_content);
//		Bitmap devBmp = bkbmp.copy(Bitmap.Config.ARGB_8888, true);
//		MiscUtil.drawBitmapText(devBmp, mContext.getResources().getString(R.string.no_files), 
//				350, 90, 32, Color.WHITE, Align.CENTER, null);
//		MiscUtil.drawBitmapText(devBmp, mContext.getResources().getString(R.string.press_key_return), 
//				350, 280, 32, Color.WHITE, Align.CENTER, null);
		
		getItem(IDX_PROMPT).setVisible(false);
		getItem(IDX_PROMPT).SetBitmap(browser.getWarningBitmap());
	}

	private void initPosition(){
		for(int idx = 0; idx < IDX_ITEM0; idx++ ){
			DisplaySlot slt = new DisplaySlot("", ""); 
			slt.setPosition(InitPositions[idx][0], InitPositions[idx][1], InitPositions[idx][2]);
			slt.setRotation(InitPositions[idx][3], InitPositions[idx][4], InitPositions[idx][5], InitPositions[idx][6]);
			slt.setScale(InitPositions[idx][7], InitPositions[idx][8], InitPositions[idx][9]);
			addItem(slt);
		}
	}
	
	private void ClearItemSlots(){
		for(int i=getSize()-1; i>=IDX_ITEM0; i--){
			removeItem(i);
		}
	}
	
	private int gridGetRows(){
		if(curMode == MODE_DEVICE){
			return DM_ROWS;
		}else if(curMode == MODE_FILE){
			return FM_ROWS;
		}else if(curMode == MODE_MUSIC){
			return MM_ROWS;
		}
		return 0;
	}
	
	private int gridGetCols(){
		if(curMode == MODE_DEVICE){
			return DM_COLS;
		}else if(curMode == MODE_FILE){
			return FM_COLS;
		}else if(curMode == MODE_MUSIC){
			return MM_COLS;
		}
		return 0;
	}
	
	private float[][] gridGetGridItemPositions(){
		if(curMode == MODE_DEVICE){
			return DMGridItemPositions;
		}
		if(curMode == MODE_FILE){
			return FMGridItemPositions;
		}
		if(curMode == MODE_MUSIC){
			return MMGridItemPositions;
		}
		return null;
	}
	
	private float[] gridGetSlotPositions(int idx){
		float positions[][] = null;
		if(curMode == MODE_DEVICE){
			positions = DMSlotPositions;
		}else if(curMode == MODE_FILE){
			positions = FMSlotPositions;
		}else if(curMode == MODE_MUSIC){
			positions = MMSlotPositions;
		}else
			return null;
		return positions[idx];
	}
	
	private void gridResetSelected(){
		focusIndex = -1;
		firstIndex = 0;
	}
	
	public int gridGetSlotCount(){
		return gridGetRows() * gridGetCols();
	}

	public int gridGetFocusIndex(){
		return focusIndex;
	}
	
	public String gridGetPlayFileName(){
		if(focusIndex >= 0 && browser!=null ){
			return browser.getItemAbsoluteName(focusIndex); 
		}
		return null;
	}
	
	public ArrayList<String> gridGetPlayList(){
		if(focusIndex >= 0 && browser!=null ){
			return browser.getPlayList(); 
		}
		return null;
	}

	private int gridGetItemCount() {
		if(browser == null)
			return 0;
		
		return browser.getCount();
	}
	
	private void gridUpdateSlots(){
		if(browser == null)
			return;
		
		synchronized(slots){
			for(int i=0; i<gridGetSlotCount(); i++){
				int sltIdx = IDX_ITEM0 + i;
				if(sltIdx < slots.size()){
					GridSlot slt = (GridSlot) slots.get(sltIdx);
					
					int itm = firstIndex + i;
					if(itm < gridGetItemCount()){
						Bitmap bmp;
						bmp = browser.getItemIconBitmap(itm);
						slt.SetIconBitmap(bmp);
						bmp = browser.getItemNameBitmap(itm);
						slt.SetNameBitmap(bmp);
						bmp = browser.getItemScrollNameBitmap(itm);
						slt.SetNameScrollBitmap(bmp);
						slt.SetNameScroll(itm == focusIndex);
						slt.setVisible(true);
					}else{
						slt.SetIconBitmap(null);
						slt.SetNameBitmap(null);
						slt.SetNameScroll(false);
						slt.setVisible(false);
					}
				}
			}
		}
	}
	
	private boolean gridUpdateFirstIndex(){
		int oldfrist = firstIndex;
		int count  = gridGetSlotCount();
		firstIndex = focusIndex / count * count;
		if(firstIndex < 0 || firstIndex >= browser.getCount())
			firstIndex = 0;	
		return firstIndex == oldfrist;
	}
	
	private void gridUpdateSelected(){
		
		if(focusIndex < 0 || focusIndex >= browser.getCount()){
			if(browser.getCount() > 0){
				focusIndex = 0;
			}else{
				focusIndex = -1;
			}
		}
		
		gridUpdateFirstIndex();
		
		DisplaySlot slt = getItem(IDX_SELECTED);
		if(focusIndex >= 0){
			int i = focusIndex - firstIndex;
			float pos[] = gridGetSlotPositions(i);
			slt.setPosition(pos[0], pos[1], pos[2]/*SELECTED_Z*/);
			slt.setScale(pos[7], pos[8], pos[9]);
//			slt.setVisible(true);
//			if(breathe != null){
//				breathe.start();
//			}
		}
		else{
			slt.setVisible(false);
//			slt.setScale(SCALE_MIN, SCALE_MIN, SCALE_MIN);
			if(breathe != null){
				breathe.stop();
			}
		}
	}
	private void gridShowSelected(){
		
		DisplaySlot slt = getItem(IDX_SELECTED);
		Log.d(TAG,"gridShowSelected focusIndex:"+focusIndex);
		if(focusIndex >= 0){
			slt.setVisible(true);
		}
	}
	private void gridHideSelected(){
		if(breathe != null){
			breathe.stop();
		}
		DisplaySlot slt = getItem(IDX_SELECTED);
		slt.setVisible(false);
	}
	
	public void setMode(int mode){
		if(curMode == mode || mode < MODE_DEVICE || mode > MODE_MUSIC)
			return;
		
		curMode = mode;
		ClearItemSlots();

		int rows = gridGetRows();
		int cols = gridGetCols();
		
		float[][] itmPos = gridGetGridItemPositions();
//		IDX_SELECTED = getSize();
//		//add selected slot
//		DisplaySlot slt1 = new DisplaySlot("selected", "");
//		slt1.setVisible(false);
//		addItem(slt1);
		Log.i(TAG,"-------------------rows"+rows);
		setItemPos();
		for(int i=0; i<rows; i++){
			for(int j=0; j<cols; j++){
				float pos[] = gridGetSlotPositions(i*cols+j);
				GridSlot slt = new GridSlot();
				slt.setPosition(pos[0], pos[1], pos[2]);
				Log.i(TAG,"-------------------x "+pos[0]);
				Log.i(TAG,"-------------------y "+pos[1]);
				Log.i(TAG,"-------------------z "+pos[2]);
				slt.setRotation(pos[3], pos[4], pos[5], pos[6]);
				slt.setScale(pos[7], pos[8], pos[9]);
				
				slt.SetBkBitmap(browser.getItemBkBitmap());
				slt.SetIconPosition(itmPos[0][0], itmPos[0][1], itmPos[0][2]);
				slt.SetIconScale(itmPos[0][3], itmPos[0][4], itmPos[0][5]);
				slt.SetNamePosition(itmPos[1][0], itmPos[1][1], itmPos[1][2]);
				slt.SetNameScale(itmPos[1][3], itmPos[1][4], itmPos[1][5]);
				slt.SetNameWidthPixel(browser.getNameWidthPixel());

				slt.setVisible(false);
				addItem(slt);
			}
		}


		//breathe = new PMTBreatheInOutAnimation(slt, 2000, 0.1f);
	}
	
	Vector<PMTAnimation> animlist = new Vector<PMTAnimation>();
	private void stopAnims(){
//		PMTAnimator.getInstance().reqStop();
		for(PMTAnimation a : animlist){
			a.stop();
		}
		animlist.clear();
		
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	private void gridMoveSelected(int oldIdx, int newIdx){
		DisplaySlot slt = getItem(IDX_SELECTED);
		float oldPos[] = gridGetSlotPositions(oldIdx);
		float newPos[] = gridGetSlotPositions(newIdx);
		
		if(breathe != null){
			breathe.stop();
		}
		
		PMTTransformAnimation am0 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), ANIM_TIMING*2/3, 0);
		am0.setTranslation(oldPos[0], oldPos[1], oldPos[2]/*SELECTED_Z*/,
				newPos[0], newPos[1], newPos[2]/*SELECTED_Z*/);
		
//		PMTTransformAnimation am00 = new PMTTransformAnimation(slt, new AccelerateInterpolator(), ANIM_TIMING/3/2, 0);
//		am00.setScale(newPos[7], newPos[8], newPos[9], newPos[7]*FOCUS_SCALE, newPos[8]*FOCUS_SCALE, newPos[9]);
//		am0.setNextAnim(am00);
//		
//		PMTTransformAnimation am000 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), ANIM_TIMING/3/2, 0);
//		am000.setScale(newPos[7]*FOCUS_SCALE, newPos[8]*FOCUS_SCALE, newPos[9], newPos[7], newPos[8], newPos[9]);
//		am00.setNextAnim(am000);
		animlist.add(am0);
		PMTAnimator.getInstance().startAnimations(animlist);
		
		//set name scroll
		GridSlot oldSlot = (GridSlot) getItem(IDX_ITEM0 + oldIdx);
		oldSlot.SetNameScroll(false);
		GridSlot newSlot = (GridSlot) getItem(IDX_ITEM0 + newIdx);
		newSlot.SetNameScroll(true);
	}
	/*
	private void gridPageDown(){
		
		synchronized(slots){
			
			int rows = gridGetRows();
			int cols = gridGetCols();
			int slotscount = rows * cols;
			
			// next page line. 
			for(int i=0; i<cols; i++){
				int sltIdx = IDX_ITEM0 + slotscount + i;
				DisplaySlot slt = slots.get(sltIdx);
				
				int itm = firstIndex + slotscount + i;
				if(itm < gridGetItemCount()){
					Bitmap bmp = browser.getItemBitmap(itm);
					slt.SetBitmap(bmp);
					slt.setVisible(true);
				}else{
					slt.SetBitmap(null);
					slt.setVisible(false);
				}
			}
			
			for(int i=0; i<cols*(rows+1); i++){
				int sltIdx = IDX_ITEM0 + i;
				DisplaySlot slt = slots.get(sltIdx);
				
				float posOrg[] = gridGetSlotPositions(i);
				float posDst[] = gridGetSlotPositions(i - cols);
				
				PMTTransformAnimation am0 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), ANIM_TIMING, 0);
				am0.setScale(posOrg[7], posOrg[8], posOrg[9], posDst[7], posDst[8], posDst[9]);
				am0.setTranslation(posOrg[0], posOrg[1], posOrg[2], posDst[0], posDst[1], posDst[2]);
				am0.setRotation(0, 360, 1, 0, 0);
				
				if(i < cols){//top row: disappear
					PMTTransformAnimation am00 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), 0, 0);
					am00.setScale( posDst[7], posDst[8], posDst[9], SCALE_MIN, SCALE_MIN, SCALE_MIN);
					am0.setNextAnim(am00);
					
					slots.add(IDX_ITEM0+cols*(rows+1)+i, slt);
				}
				
				animlist.add(am0);
			}
			
			for(int i=0; i<cols; i++)
				slots.remove(IDX_ITEM0);

		}
		PMTAnimator.getInstance().startAnimations(animlist);
		
	}
	
	private void gridPageUp(){
		
		synchronized(slots){
			
			int rows = gridGetRows();
			int cols = gridGetCols();
			int slotscount = rows * cols;
			
			// prev page line. 
			for(int i=0; i<cols; i++){
				int sltIdx = IDX_ITEM0 + slotscount + i;
				DisplaySlot slt = slots.get(sltIdx);
				
				int itm = firstIndex - cols + i;
				if(itm>=0 && itm < gridGetItemCount()){
					Bitmap bmp = browser.getItemBitmap(itm);
					slt.SetBitmap(bmp);
					slt.setVisible(true);
				}else{
					slt.SetBitmap(null);
					slt.setVisible(false);
				}
				
				slots.remove(sltIdx);
				slots.add(IDX_ITEM0+i, slt);
			}
			
			for(int i=0; i<cols*(rows+1); i++){
				int sltIdx = IDX_ITEM0 + i;
				DisplaySlot slt = slots.get(sltIdx);
				
				float posOrg[] = gridGetSlotPositions(i - cols);
				float posDst[] = gridGetSlotPositions(i);
				
				PMTTransformAnimation am0 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), ANIM_TIMING, 0);
				am0.setScale(posOrg[7], posOrg[8], posOrg[9], posDst[7], posDst[8], posDst[9]);
				am0.setTranslation(posOrg[0], posOrg[1], posOrg[2], posDst[0], posDst[1], posDst[2]);
				am0.setRotation(360, 0, 1, 0, 0);
				
				if(i >= slotscount){//bottom row: disappear
					PMTTransformAnimation am00 = new PMTTransformAnimation(slt, new DecelerateInterpolator(), 0, 0);
					am00.setScale( posDst[7], posDst[8], posDst[9], SCALE_MIN, SCALE_MIN, SCALE_MIN);
					am0.setNextAnim(am00);
				}
				
				animlist.add(am0);
			}

		}
		PMTAnimator.getInstance().startAnimations(animlist);
		
	}	
	*/
	
	//Override
	public void setSlotsLayout(){
		
	}

	//Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		synchronized(this){
			if(switchState != SWITCH_IDLE)
				return true;
		}
    	Log.i("GLGridLayout", "onKeyDown(" + keyCode+ ")");

		int rows = gridGetRows();
		int cols = gridGetCols();
		int count = gridGetItemCount();
		
		switch(keyCode){
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
			onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT,event);
			return true;
			
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
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,event);
			return true;
		
		case KeyEvent.KEYCODE_DPAD_UP:
			if(focusIndex / cols > 0){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;
				if(focusIndex < firstIndex + cols){
					//gridPageUp();
					focusIndex -= cols;
					firstIndex -= cols*rows;
//					gridUpdateSlots();
//					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					PageUpdateSlots(oldSlt);
				}else{
					focusIndex -= cols;
					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					postUpdateInfoMessage();
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(focusIndex >=0 && focusIndex / cols < (count-1) / cols){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;
				if(focusIndex >= firstIndex+cols*(rows-1)){
//					gridPageDown();
					firstIndex += cols*rows;
					focusIndex += cols;
					if(focusIndex >= count){
						focusIndex = count - 1;
					}
//					gridUpdateSlots();
//					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					PageUpdateSlots(oldSlt);
				}else{
					focusIndex += cols;
					if(focusIndex >= count)
						focusIndex = count - 1;
					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					postUpdateInfoMessage();
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(focusIndex>0){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;
				if(focusIndex - 1 < firstIndex){
//					gridPageUp();
					firstIndex -= cols*rows;
					focusIndex --;
//					gridUpdateSlots();
//					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					PageUpdateSlots(oldSlt);
				}else{
					focusIndex --;
					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					postUpdateInfoMessage();
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Log.d(TAG,focusIndex+","+firstIndex);
			if(focusIndex>=0 && focusIndex+1 < count){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;
				if(focusIndex + 1 >= firstIndex + rows*cols){
//					gridPageDown();
					firstIndex += cols*rows;
					focusIndex ++;
//					gridUpdateSlots();
//					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					PageUpdateSlots(oldSlt);
				}else{
					focusIndex ++;
					gridMoveSelected(oldSlt, focusIndex-firstIndex);
					postUpdateInfoMessage();
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			Log.i("GLGridLayout", "--> KEYCODE_ENTER");
			if(focusIndex>=0 && focusIndex < gridGetItemCount()){
				stopAnims();
				browser.cancelThumbImage();
				browser.cancelInfoImage();
				gridHideSelected();
				if(curMode == MODE_DEVICE){
					gridPrepareEnterBack(SWITCH_ENTER_PREPARE);
					String devName = browser.getItemAbsoluteName(focusIndex);
					gridResetSelected();
					enterFileBrowser(devName, null);
				}else if(curMode == MODE_FILE || curMode == MODE_MUSIC){
					if(browser.IFIsFolder(focusIndex)){
						focusFileName = null;
						gridPrepareEnterBack(SWITCH_ENTER_PREPARE);
						showWaitingAnimation();
						browser.EnterDir(focusIndex);						
						gridResetSelected();
//						gridUpdateItems();
//						LoadEnterBackTextures();
					}else{
						//gridHideSelected();
						if(handlerPlay != null){
							Message msg = new Message();
							msg.what = RelevanceOp.getPlayHandlerWhat(fileType);
							Log.i("GLGridLayout", "handlerPlay.sendMessage(msg)");
//							handlerDelayShowDev.removeMessages(1);
//							FBReader.Instance.hideDevDisplay();
							handlerPlay.sendMessage(msg);
							
						}
					}
				}
			}
			Log.i("GLGridLayout", "<-- KEYCODE_ENTER");
			return true;
		case KeyEvent.KEYCODE_BACK:
			Log.i("GLGridLayout", "--> KEYCODE_BACK");
			gridHideSelected();
			ShowPrompt(curMode,false);
			stopAnims();
			browser.cancelThumbImage();
			browser.cancelInfoImage();
			if(curMode == MODE_DEVICE){
				if(ldfromMisicLayout)
					{
					setFileType("Audio");
					Message msg = new Message();
					msg.what = RelevanceOp.getPlayHandlerWhat("Audio_Show");
					Log.i("GLGridLayout", "handlerPlay.sendMessage(msg)");
					handlerPlay.sendMessage(msg);
					return true;
					}		
			}else if(curMode == MODE_FILE || curMode == MODE_MUSIC){
				focusFileName = browser.getCurDirPath();
//				if(focusIndex >= 0)
//					aName = browser.getItemAbsoluteName(focusIndex);
				gridPrepareEnterBack(SWITCH_BACK_PREPARE);
				showWaitingAnimation();
				if(browser.upOneLevel()){
					gridResetSelected();
//					if(aName != null){
//						focusIndex = browser.findItem(aName);
//						gridUpdateFirstIndex();
//					}
//					gridUpdateItems();
//					LoadEnterBackTextures();
				}else{
					hideWaitingAnimation();
					gridResetSelected();
					enterDeviceBrowser();
				}
				Log.i("GLGridLayout", "KEYCODE_BACK <- : TRUE");
				return true;
			}
			Log.i("GLGridLayout", "KEYCODE_BACK <- : FALSE");
			break;
		}

		return false;
	}

	public boolean pageScroll(int direction )
		{
		int rows = gridGetRows();
		int cols = gridGetCols();
		int count = gridGetItemCount();
		Log.e(TAG,"XXXXXXXXXXXXX--"+rows+cols+count+direction);
		switch(direction){
		case PAGE_UP:
			if(focusIndex / (cols*rows) > 0){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;
				
				focusIndex -= cols*rows;
				firstIndex -= cols*rows;
				PageUpdateSlots(oldSlt);

			}
			return true;
		case PAGE_DOWN:
			int mItemNumPerPage = cols*rows;
			if(focusIndex >=0 && focusIndex / mItemNumPerPage < (count-1) / mItemNumPerPage){
				stopAnims();
				int oldSlt = focusIndex-firstIndex;

				focusIndex += cols*rows;
				firstIndex += cols*rows;
				if(focusIndex >= count){
						focusIndex = count - 1;
					}
				PageUpdateSlots(oldSlt);
			}
			return true;
		}
		return false;
		}
	public boolean onTouch(MotionEvent event){
		
		return false;
	}

	//Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}
	private void setItemPos()
		{
		int cols=0;
		int rows=0;
		if(curMode == MODE_DEVICE)
			{
			cols=DM_COLS;
			rows=DM_ROWS;
			}
		else if(curMode == MODE_FILE || curMode == MODE_MUSIC)
			{
			cols=MM_COLS;
			rows=MM_ROWS;
			}
		item_center=new float[cols*rows][4];
		int [] origin_pos=new int[]{Resolution.getWidth()/2,Resolution.getHeight()/2};
		for(int i=0; i<rows; i++){
			for(int j=0; j<cols; j++){
				float [] pos = gridGetSlotPositions(i*cols+j);
				item_center[i*cols+j][0]=origin_pos[0]+pos[0]*Resolution.getWidth()*0.34f;
				item_center[i*cols+j][1]=origin_pos[1]-pos[1]*Resolution.getHeight()*0.6f;
				}
			}
		float []pos=gridGetSlotPositions(0);
		item_w=(int) (0.2f*Resolution.getWidth()*pos[7]*0.319f);
		item_h=(int) (0.2f*Resolution.getHeight()*pos[8]*0.4063f);
		}

	
	private int GetSelIndex(int x ,int y)
		{
		int cols=0;
		int rows=0;
		if(curMode == MODE_DEVICE)
			{
			cols=DM_COLS;
			rows=DM_ROWS;
			}
		else if(curMode == MODE_FILE || curMode == MODE_MUSIC)
			{
			cols=MM_COLS;
			rows=MM_ROWS;
			}
		
		for(int i=0; i<cols; i++)
			{
			int left= (int)(item_center[i][0]-item_w/2);
			int right= (int)(item_center[i][0]+item_w/2);
			if(x > left&& x < right)
				{
				for(int j=0; j<rows; j++)
					{
					int top =(int)(item_center[i+j*cols][1]-item_h/2);
					int buttom =(int)(item_center[i+j*cols][1]+item_h/2);
					if(y > top && y < buttom)
						{
						return i+j*cols;
						}
					}
				}
			}
		return -1;
		}
	 public void MouseClick(View v ,int x, int y)
	 	{
	 	Log.i("GLGridLayout", "-------------------> MouseClick");
		int sel_index=GetSelIndex(x,y);
		int total_index=firstIndex+sel_index;
		if(sel_index>=0)
			{
			if(total_index>=0 && total_index<gridGetItemCount())
				{
				focusIndex=total_index;
				if(focusIndex>=0 && focusIndex < gridGetItemCount())
					{
					stopAnims();
					browser.cancelThumbImage();
					browser.cancelInfoImage();
					gridHideSelected();
					if(curMode == MODE_DEVICE)
						{
						gridPrepareEnterBack(SWITCH_ENTER_PREPARE);
						String devName = browser.getItemAbsoluteName(focusIndex);
						gridResetSelected();
						enterFileBrowser(devName, null);
						}
					else if(curMode == MODE_FILE || curMode == MODE_MUSIC)
						{
						if(browser.IFIsFolder(focusIndex))
							{
							focusFileName = null;
							gridPrepareEnterBack(SWITCH_ENTER_PREPARE);
							showWaitingAnimation();
							browser.EnterDir(focusIndex);						
							gridResetSelected();
		//						gridUpdateItems();
		//						LoadEnterBackTextures();
							}
						else{
							//gridHideSelected();
							if(handlerPlay != null)
								{
								Message msg = new Message();
								msg.what = RelevanceOp.getPlayHandlerWhat(fileType);
								Log.i("GLGridLayout", "handlerPlay.sendMessage(msg)");
		//							handlerDelayShowDev.removeMessages(1);
		//							FBReader.Instance.hideDevDisplay();
								handlerPlay.sendMessage(msg);
								}
							}
						}
					}
				}
			}
		Log.i("GLGridLayout", "<-- onClick");
	 	}

	private void showLoadingMsg(GL10 gl) {
		if (loadingTexID < 0 && loadingResID >= 0)
			loadingTexID = TextureManager.loadTextureOESETC1(gl, mContext.getResources(), loadingResID);
		if (loadingTexID >= 0) {
			int err;
			gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			gl.glActiveTexture( GL10.GL_TEXTURE0 );
			gl.glEnable( GL10.GL_TEXTURE_2D );
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, loadingTexID);
			if ((err = gl.glGetError()) !=  0) Log.v("GLGridLayout", "bindtex err=" + err);
			((GL11Ext) gl).glDrawTexfOES(loadingTexRect[0], loadingTexRect[1], loadingTexRect[2], loadingTexRect[3], loadingTexRect[4]);
			if ((err = gl.glGetError()) !=  0) Log.v("GLGridLayout", "drawtex err=" + err);

			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

	//Override
	public synchronized void drawFrame(GL10 gl) {
        gl.glDisable(GL11.GL_DEPTH_TEST);
		setCamera(gl);
		
		if (bgTexID < 0 && bgResId >= 0) {
			/* If bg not loaded, show a loading message, then load the bg and slot textures */
			if (loadingMsgShown == false) {
				showLoadingMsg(gl);
				loadingMsgShown = true;
				gl.glEnable(GL11.GL_DEPTH_TEST);
				return;
			}
			else {
				/* loading msg has been shown, now load textures ..in GLThread */
//				Log.i("GLGridLayout", "--> loadTextureOES( bgResId )");
//				bgTexID = TextureManager.loadTextureOES(gl, mContext.getResources(), bgResId);
				Bitmap btp = BitmapFactory.decodeResource(mContext.getResources(), bgResId);
				bgTexID = TextureManager.loadTextureOES(gl, btp, false);
				Log.e("GLGridLayout","-----------------bgResId"+bgResId);
				FileOutputStream fos = null; 
				try { 
				fos = new FileOutputStream( "/sdcard/btp"+ ".jpg" ); 
				if ( fos != null ) 
						{ 
								btp.compress(Bitmap.CompressFormat.JPEG, 100, fos ); 
								fos.close(); 
						} 
				} catch( Exception e ) 
						{ 
						Log.e("SearchDrawable","------------------create file fail");
						} 
						
//				Log.i("GLGridLayout", "<-- loadTextureOES( bgResId ) " + bgTexID);
			}
		}

		if(bgTexID>0){
			gl.glClientActiveTexture(GL10.GL_TEXTURE0);
	        gl.glActiveTexture( GL10.GL_TEXTURE0 );
	        gl.glEnable( GL10.GL_TEXTURE_2D );
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, bgTexID);
            ((GL11Ext) gl).glDrawTexfOES(bgTexRect[0], bgTexRect[1], bgTexRect[2], bgTexRect[3], bgTexRect[4]);

            gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			
		}
		
//		synchronized(this){
			if(switchState == SWITCH_ENTER_LOADING_TEXTURES ||
				switchState == SWITCH_BACK_LOADING_TEXTURES){
				Log.i("GLGridLayout", "drawFrame() switchState: " + switchState);
				synchronized(slots){
					for(DisplaySlot slot : slots){
						if(slot.isVisible()){
							slot.LoadTextures(gl);
						}
					}
				}
				if(switchState == SWITCH_ENTER_LOADING_TEXTURES){
					switchState = SWITCH_ENTER_ANIMATING;
				}else{
					switchState = SWITCH_BACK_ANIMATING;
				}
				
				Message msg = new Message();
				msg.what = 1;
				Log.i("GLGridLayout", "handlerEnterBack.sendMessageDelayed(msg, 100) switchState: " + switchState);
				handlerEnterBack.sendMessageDelayed(msg, 100);
			}
			
			
			if(disappearAnimSlots != null && disappearAnimSlots.size()>0){ 
				gl.glPushMatrix();
				
				if(disappearAnimPose != null)
					disappearAnimPose.glPoseDraw(gl);
				for(DisplaySlot slot : disappearAnimSlots){
					slot.drawSlot(gl);
				}
		
				gl.glPopMatrix();
			}
//		}		
		
		if(isSlotsVisable){
			//Log.e("GLGridLayout","---------------------drawFrame");
			drawSlot(gl);
			drawEffects(gl);
		}
		
        gl.glEnable(GL11.GL_DEPTH_TEST);

	}
	
	public void ldFromMusic(boolean bl)
		{
		ldfromMisicLayout=bl;
		}
	
	//Override
	public void startAutoPlay()
	{
	}
	//Override
	public void stopAutoPlay()
	{

	}
	//Override
	public void onstop()
	{
	
		delLayoutTextures();
	}

	//Override
	public synchronized void delLayoutTextures() {
		super.delLayoutTextures();
		if(bgTexID >=0 ){
			TextureManager.delTexture(bgTexID);
			bgTexID = -1;
		}
		if(loadingTexID>=0){
			TextureManager.delTexture(loadingTexID);
			loadingTexID = -1;
		}
	}

	public synchronized void setBgResId(int id){
		if(bgTexID >= 0){
			TextureManager.delTexture(bgTexID);
			bgTexID = -1;
		}
		
		bgResId = id;
	}
	
	public void UpdateTitle() {
		Bitmap bitmap = null;
		if(browser != null){
			bitmap = browser.getTitleBitmap();
		}
		getItem(IDX_TITLE).SetBitmap(bitmap);
	}
	
	public void UpdateInfoTitle() {
		Bitmap bitmap = null;
		if(browser != null){
			bitmap = browser.getInfoTitleBitmap();
		}
		getItem(IDX_INFO_TITLE).SetBitmap(bitmap);
		getItem(IDX_INFO_TITLE).setVisible(false);
	}
	
	private void postUpdateInfoMessage(){
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = focusIndex;
		handlerUpdateInfo.sendMessageDelayed(msg, 500);
	}
//	private void postUpdataPictureMassage()
//	{
//		Message msg = new Message();
//		msg.what = 1;
//		handlerUpdatePictureInfo.sendMessageDelayed(msg, 100);
//	}
	public void UpdateInfo() {
		if(browser != null && focusIndex >= 0){
			browser.requestInfoImage(focusIndex);
		}else{
			getItem(IDX_INFO).SetBitmap(null);
		}
	}
	public void setInfoBitmap(Bitmap bitmap) {
		getItem(IDX_INFO).SetBitmap(bitmap, false);
		getItem(IDX_INFO).setVisible(false);
	}

	public void UpdateSelectedBitmap() {
		Bitmap bitmap = null;
		if(browser != null){
			bitmap = browser.getSelectedBitmap();
		}
		getItem(IDX_SELECTED).SetBitmap(bitmap);
	}	
	
	public void setFileType(String filetype){
		fileType = new String(filetype);
	}
	
	public void gridUpdateSelectInfos(){
		UpdateInfo();
		UpdateScroll();
		UpdateCount();		
		if(breathe != null){
			breathe.start();
		}
	}
	
	public void gridUpdateItems(){
		gridUpdateSelected();
		gridUpdateSlots();
//		gridUpdateSelectInfos();
	}
	
	public void focusItemByAName(String aName){
		if(aName != null){
			int newFocus = browser.findItem(aName);
			if(newFocus >= 0 && focusIndex != newFocus){
				focusIndex = newFocus;
				if(gridUpdateFirstIndex()){
					gridUpdateItems();
				}
				gridUpdateSelectInfos();
			}
		}
	}

	public void ShowPrompt(int curmode,boolean show) {
		if(show)
		{
			if(curmode == MODE_DEVICE)
			{
				if(!getItem(IDX_PROMPT).getName().equals("MODE_DEVICE"))
				{
					getItem(IDX_PROMPT).setVisible(false);
					getItem(IDX_PROMPT).setName("MODE_DEVICE");
					getItem(IDX_PROMPT).SetBitmap(browser.getWarningBitmap());
					getItem(IDX_PROMPT).setVisible(true);
				}
			}else
			{
				if(!getItem(IDX_PROMPT).getName().equals("MODE_UNDEVICE"))
				{
					getItem(IDX_PROMPT).setVisible(false);
					getItem(IDX_PROMPT).setName("MODE_UNDEVICE");
					getItem(IDX_PROMPT).SetBitmap(browser.getWarningBitmap());
					getItem(IDX_PROMPT).setVisible(true);
				}
			}
				
		}
		getItem(IDX_PROMPT).setVisible(show);
	}

	private void UpdateCount() {
		Bitmap devBmp = null;
		int count = gridGetItemCount();
		if(count > 0 && focusIndex >= 0){
			devBmp = Bitmap.createBitmap(280, 32, Bitmap.Config.ARGB_8888);
			MiscUtil.drawBitmapText(devBmp, (focusIndex+1) + "/" + count, 
					140, 32, 32, Color.WHITE, Align.CENTER, null);
		}
		getItem(IDX_COUNT).SetBitmap(devBmp);
	}

	private void UpdateScroll() {
		DisplaySlot sltBk = getItem(IDX_SCROLL_BK);
		DisplaySlot sltBlock = getItem(IDX_SCROLL_BLOCK);
		int count = gridGetItemCount();
		int rows = gridGetRows();
		int cols = gridGetCols();
		
		if(count <= rows*cols){
			sltBk.setVisible(false);
			sltBlock.setVisible(false);
		}else{
			int currow = focusIndex / cols;
			int totalrows = (count + cols - 1) / cols;
			float totalscal = InitPositions[IDX_SCROLL_BK][8];
			float scal = totalscal * rows / totalrows;
			float orgpos = InitPositions[IDX_SCROLL_BK][1];
			float pos = ((totalscal-scal)/2 - currow*(totalscal-scal)/(totalrows-1)) * 0.2f + orgpos;
			sltBlock.setScale(InitPositions[IDX_SCROLL_BLOCK][7], scal, InitPositions[IDX_SCROLL_BLOCK][9]);
			sltBlock.setPosition(InitPositions[IDX_SCROLL_BLOCK][0], pos, InitPositions[IDX_SCROLL_BLOCK][2]);
			sltBk.setVisible(true);
			sltBlock.setVisible(true);
		}
	}

	public void enterDeviceBrowser(){
		Devicebrowser brw = new Devicebrowser(this.mContext, this);
		brw.SetFileType(fileType);
		browser = brw;
		
		setMode(GLGridLayout.MODE_DEVICE);
				
		UpdateTitle();
		UpdateInfoTitle();
		UpdateSelectedBitmap();
		
		brw.getDevice();	//callback~ BrowseFinish()
		
	}
	
	public void enterFileBrowser(String devName, String backFName){

		if (!Filebrowser.IfFolder) {
			 String realDevice = getDevice(devName);
			 if(realDevice != null)
				 devName = realDevice;
		}
		Filebrowser brw = new Filebrowser(this.mContext, this);
		brw.SetFileType(devName, fileType);
		browser = brw;
		focusFileName = backFName;
		
		if(fileType.equals("Audio"))
			setMode(GLGridLayout.MODE_MUSIC);
		else
			setMode(GLGridLayout.MODE_FILE);
		
		UpdateTitle();
		UpdateInfoTitle();
		UpdateSelectedBitmap();
		this.gridHideSelected();
		brw.StartFilebrowser();	//callback~ BrowseFinish()
		showWaitingAnimation();
	}
	
	private int pageOldSlot = 0;
	private void PageUpdateSlots(int oldSlt) {
		switchState = SWITCH_PAGE;
		pageOldSlot = oldSlt;
//		new Thread("BrowseFinish") {
//			public void run() {
				synchronized(GLGridLayout.this){
					browser.cancelThumbImage();
					browser.cancelInfoImage();
					gridUpdateSlots();
					gridMoveSelected(pageOldSlot, focusIndex-firstIndex);
//					postUpdataPictureMassage();
					requestThumbImage();
					switchState = SWITCH_IDLE;
					postUpdateInfoMessage();
				};
//			}
//		}.start();
	}
	
//	public void playFinish(){
//		Log.d(TAG,"playFinish");
//		gridUpdateSelected();
//		this.gridShowSelected();
//		hideWaitingAnimation();
////		new Thread("BrowseFinish") {
////			public void run() {
//				processBrowseFinish();
////			}
////		}.start();
//	}
	public void browseFinish(){
		Log.d(TAG,"browseFinish");
		hideWaitingAnimation();
//		new Thread("BrowseFinish") {
//			public void run() {
				processBrowseFinish();
//			}
//		}.start();
	}
	public void processBrowseFinish(){
		if(focusFileName != null){
			focusIndex = browser.findItem(focusFileName);
			focusFileName = null;
			gridUpdateFirstIndex();
		}
		
		gridUpdateItems();
		Log.d(TAG,"processBrowseFinish.....switchState"+switchState);
		if(switchState == SWITCH_IDLE)
		{
			ShowPrompt(curMode,curMode == MODE_DEVICE && gridGetItemCount() <= 0);
//			postUpdataPictureMassage();
			requestThumbImage();
			postUpdateInfoMessage();
			this.gridShowSelected();
		}else
			LoadEnterBackTextures();
	}
	
	private PMTTransformAnimation animEnterSelected = null;
	
	private void gridPrepareEnterBack(int state){
		Log.i("GLGridLayout", "--> gridPrepareEnterBack() state: " + state);
		synchronized(this){ synchronized(slots){
			switchState = state;
			disappearAnimPose  = new GLPose(this);
			disappearAnimSlots = new ArrayList<DisplaySlot>();
			for(int idx = 0; idx < IDX_ITEM0; idx++ ){
				DisplaySlot slt = slots.get(idx);
				disappearAnimSlots.add(slt);
			}
			float[][] itmPos = gridGetGridItemPositions();
			for(int idx = IDX_ITEM0; idx < slots.size()-1; idx++ ){
				GridSlot oSlt = (GridSlot)slots.get(idx);
				GridSlot slt = new GridSlot();
				slt.setPosition(oSlt.getPosition());
				slt.setRotation(oSlt.getRotate());
				slt.setScale(oSlt.getScale());
				
				slt.SetBkBitmap(browser.getItemBkBitmap());
				slt.SetIconPosition(itmPos[0][0], itmPos[0][1], itmPos[0][2]);
				slt.SetIconScale(itmPos[0][3], itmPos[0][4], itmPos[0][5]);
				slt.SetNamePosition(itmPos[1][0], itmPos[1][1], itmPos[1][2]);
				slt.SetNameScale(itmPos[1][3], itmPos[1][4], itmPos[1][5]);
				slt.SetNameWidthPixel(browser.getNameWidthPixel());
				slt.setVisible(false);
				
				slots.set(idx, slt);
				disappearAnimSlots.add(oSlt);
			}
			setSlotsVisable(false);
		}}
		Log.i("GLGridLayout", "<-- gridPrepareEnterBack() state: " + state);
	}
	
	private void LoadEnterBackTextures(){
		synchronized(this){
			if(switchState == SWITCH_ENTER_PREPARE){
				switchState = SWITCH_ENTER_LOADING_TEXTURES;
			}
			if(switchState == SWITCH_BACK_PREPARE){
				switchState = SWITCH_BACK_LOADING_TEXTURES;
			}
		}
	}
	
	private void gridEnterBack(){
		Log.i("GLGridLayout", "--> gridEnterBack() switchState: " + switchState);
		synchronized(this){ synchronized(slots){
				
			PMTTransformAnimation am0 = null;
			if(disappearAnimSlots != null){
				am0 = new PMTTransformAnimation(disappearAnimPose, new AccelerateInterpolator(), ANIM_TIMING/2, 0);
				if(switchState == SWITCH_ENTER_ANIMATING)	//enter
					am0.setScale(1,1,1,2,2,1);
				else
					am0.setScale(1,1,1,0.2f,0.2f,1);
		    	am0.addListener(new PMTAnimationListener(){
					public void onAnimationEvent(int eventType, PMTAnimation animation) {
						//Override
						if(eventType==EVENT_FINISHED){
							Log.i("GLGridLayout", "am0.EVENT_FINISHED");
							synchronized(GLGridLayout.this){
								if(disappearAnimSlots != null){
									for(int idx = IDX_ITEM0; idx < disappearAnimSlots.size(); idx++ ){
										GridSlot slt = (GridSlot)disappearAnimSlots.get(idx);
										slt.delSlotTextures();
									}
									disappearAnimSlots = null;
								}
								disappearAnimPose = null;
								if(animEnterSelected != null)
									animEnterSelected.start();
								setSlotsVisable(true);
								
							}
						}
					}});
			}

	    	animEnterSelected = new PMTTransformAnimation(this, new DecelerateInterpolator(), ANIM_TIMING/2, 0);
	    	if(switchState == SWITCH_ENTER_ANIMATING)	//enter
	    		animEnterSelected.setScale(0.2f,0.2f,1,1,1,1);
	    	else
	    		animEnterSelected.setScale(2,2,1,1,1,1);
	    	
	    	animEnterSelected.addListener(new PMTAnimationListener(){
				public void onAnimationEvent(int eventType, PMTAnimation animation) {
					//Override
					if(eventType==EVENT_FINISHED){
						gridUpdateSelected();
						gridShowSelected();
						Log.i("GLGridLayout", "animEnterSelected.EVENT_FINISHED");
						synchronized(GLGridLayout.this){
							animEnterSelected = null;
							switchState = SWITCH_IDLE;
//							postUpdataPictureMassage();
							requestThumbImage();
							postUpdateInfoMessage();
							ShowPrompt(curMode,curMode != MODE_DEVICE && gridGetItemCount() <= 0);
						}
					}
				}});

	    	
	    	if(am0 != null){
	    		animlist.add(am0);
				PMTAnimator.getInstance().startAnimations(animlist);
	    	}
	    	else
	    		animEnterSelected.start();
		}}
		Log.i("GLGridLayout", "<-- gridEnterBack() switchState: " + switchState);
	}
	
	protected void setSlotsVisable(boolean b) {
		isSlotsVisable = b;
	}
	
	public void setPlayHandler(Handler handler){
		handlerPlay = handler;
	}
	public void UnmountDevice(String deviceName){
		if(curMode == MODE_DEVICE){
			String aName = null;
			if( focusIndex >= 0){
				aName = browser.getItemAbsoluteName(focusIndex);
			}
			
			if(browser.UnmountDevice(deviceName)){
				if(aName != null){
					focusIndex = browser.findItem(aName);
					gridUpdateFirstIndex();
				}
				stopAnims();
				gridUpdateItems();
				gridUpdateSelectInfos();
			}
			
			ShowPrompt(curMode,curMode == MODE_DEVICE && gridGetItemCount() <= 0);
		}else if (browser.getCurDirPath().startsWith(deviceName)){
			gridHideSelected();
			gridResetSelected();
			enterDeviceBrowser();
		}
	}

	
	public void MountDevice(String deviceName) {
		if(curMode == MODE_DEVICE){
			if(browser.MountDevice(deviceName)){
				stopAnims();
				gridUpdateItems();
				gridUpdateSelectInfos();
			}
		}
	}

	private void UpdateAd(final Bitmap bitmap) {
		if (browser != null) {
			Log.i("GLGridLayout", "UpdateAd() ");
			DisplaySlot sltAd = getItem(IDX_AD);
			sltAd.setVisible(false);
			sltAd.SetBitmap(bitmap.copy(bitmap.getConfig(), true), false);
		}
	}

	public void getADBitmap(final Bitmap bitmap) {
		if (adEnable)
			if (bitmap != null)
				UpdateAd(bitmap);
	}

	private void requestThumbImage() {
		int showPicTotal = 0;
		if (gridGetItemCount() - firstIndex > 0)
			if (gridGetItemCount() - firstIndex > FM_COLS*FM_ROWS)
				showPicTotal = FM_COLS*FM_ROWS;
			else
				showPicTotal = gridGetItemCount() - firstIndex;
		else
			return;
		browser.requestThumbImage(firstIndex, showPicTotal);
	}

	public void updataThumbImage(int pos, Bitmap bmp) {
		synchronized (slots) {
			int sltIdx = IDX_ITEM0 + pos - firstIndex;
			if (sltIdx >= IDX_ITEM0 && sltIdx < IDX_ITEM0 + gridGetSlotCount()) {
				GridSlot slt = (GridSlot) slots.get(sltIdx);

				int itm = pos;
				if (itm < gridGetItemCount()) {
					slt.SetIconBitmap(bmp, false);
					// slt.setVisible(true);
					// }else{
					// slt.SetIconBitmap(null);
					// slt.setVisible(false);
				}
			}
		}
	}

	private String getDevice(String filename) {
		File[] filesOne = new File("/mnt").listFiles();
		if (filesOne != null) {
			for (File file : filesOne) {
				if (file.getPath().startsWith("/mnt/sd")) {
					File[] fileTwo = new File(file.getPath()).listFiles();
					if (fileTwo != null) {
						for (File tempfile : fileTwo) {
							if (filename.contains(tempfile.getPath()))
								return tempfile.getPath();
						}
					}
				}
			}
		}
		return null;

	}
	
	/*private void showWaitingAnimation(){	
		Waiting wait=(Waiting)((Activity) mContext).findViewById(R.id.getting);	
		if(wait.getVisibility()  != View.VISIBLE)
		{
			wait.requestFocus();
			wait.setVisibility(View.VISIBLE);
			String str_msg = mContext.getString(R.string.buffing_string) ;
			wait.setInfoString( str_msg  , Waiting.BUFFRING_BUTTON );
			
		}
		
	}
	
	private void hideWaitingAnimation(){
//		infoDia.setVisibility(View.INVISIBLE);
		View wait=(Waiting)((Activity) mContext).findViewById(R.id.getting);	
		if(wait.getVisibility()  != View.INVISIBLE)
		{
			wait.setVisibility(View.INVISIBLE);
		}
	
	}*/

	private void showWaitingAnimation(){	
		if (mWaitDlg == null)
			mWaitDlg = new Waiting(mContext, null);

		String str_msg = mContext.getString(R.string.buffing_string) ;
		mWaitDlg.setInfoString( str_msg  , Waiting.BUFFRING_BUTTON );
		Log.d(TAG, ">>>>>>>>>>>>>call wait dlg show");
		mWaitDlg.show();
	}
	
	private void hideWaitingAnimation(){
		if (mWaitDlg != null) 
			mWaitDlg.hide();
	}

}
