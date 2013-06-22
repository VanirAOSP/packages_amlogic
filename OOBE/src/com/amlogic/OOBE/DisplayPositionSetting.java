package com.amlogic.OOBE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.SystemProperties;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.content.ComponentName;
import com.amlogic.OOBE.SelectFlag;

public class DisplayPositionSetting extends Activity{
	
	private static final String TAG="DisplayPositionSetting";
	private String curOutputmode= "";
	private String valScreenratio= "";
	private String sel_Outputmode="";
	private static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";
	private static final String STR_SCREEN_RATIO = "screenratio";
	private final static String sel_dispaly_mode = "oobeflag";
	private Button moutputmodeBtn;
	private Button mscreenratioBtn;
	private Button mbackBtn;
	private Button mnextBtn;

	private int sel_index;
	private int index_outputmode=0;
	private String []outputmode_array;
	private static final int GET_USER_OPERATION=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_position);
		
		moutputmodeBtn = (Button)findViewById(R.id.btn_display_position);

		mbackBtn = (Button)findViewById(R.id.btn_display_position_back);
		mnextBtn = (Button)findViewById(R.id.btn_display_position_next);
		
		moutputmodeBtn.setOnClickListener(new moutputmodeBtnListener());
		//mscreenratioBtn.setOnClickListener(new mscreenratioBtnListener());
		mbackBtn.setOnClickListener(new mbackBtnListener());
		mnextBtn.setOnClickListener(new mnextBtnListener());

	}


			class moutputmodeBtnListener implements OnClickListener{
			
				//@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				Intent intent = new Intent();
					intent.setClass(DisplayPositionSetting.this,PositionSetting.class);
				startActivity(intent);
				DisplayPositionSetting.this.finish();
			}
		}
			
		
	class mbackBtnListener implements OnClickListener{

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ConfigOOBE config = new ConfigOOBE(DisplayPositionSetting.this);
			String str = config.getPrevActivityName("DisplayPositionSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(DisplayPositionSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(DisplayPositionSetting.this,OOBE.class);
			}
			startActivity(intent);
			DisplayPositionSetting.this.finish();
		}
		
	}
	
	class mnextBtnListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			GotoNextActivity();
		}
		
	}

	private int GetIndexOfArray(String inputString,String []stringArray)
		{
		int index=0;
		for (String candidate : stringArray) 
			{
			if(inputString.equals(candidate))
				{
				break;
				}
			index++;
			}
		return index;
		}

	private void GotoNextActivity()
		{
			ConfigOOBE config = new ConfigOOBE(DisplayPositionSetting.this);
			String str = config.getNextActivityName("DisplayPositionSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(DisplayPositionSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(DisplayPositionSetting.this,Finish.class);
			}
			startActivity(intent);
			DisplayPositionSetting.this.finish();
		
		}
    @Override
	protected  void onActivityResult(int requestCode,int resultCode,Intent data)
		{
		super.onActivityResult(requestCode,resultCode,data);
		switch(requestCode)
			{
			case (GET_USER_OPERATION):
				if(resultCode==Activity.RESULT_OK)
					{
					if(SystemProperties.getInt("ro.platform.has.1080scale", 0) == 2){
						SystemProperties.set(STR_OUTPUT_MODE,moutputmodeBtn.getText().toString());
					}
					SelectFlag.setSelect(moutputmodeBtn.getText().toString());
					sel_Outputmode=SelectFlag.getSelect();
					sel_index=GetIndexOfArray(SelectFlag.getSelect(),outputmode_array);	
					//GotoNextActivity();
					}
				else if(resultCode==Activity.RESULT_CANCELED)
					{
					//GotoNextActivity();
					/*moutputmodeBtn.setText(outputmode_array[index_outputmode]);
					sel_index=index_outputmode; //reset as current mode because user canceled selected*/
					moutputmodeBtn.setText(outputmode_array[sel_index]);
					}
				
			}
		}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	

	

}
