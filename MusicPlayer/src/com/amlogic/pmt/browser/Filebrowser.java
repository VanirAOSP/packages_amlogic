package com.amlogic.pmt.browser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import com.amlogic.pmt.GLGridLayout;
import com.amlogic.pmt.MiscUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import com.amlogic.pmt.Resolution;
import android.graphics.Matrix;

public class Filebrowser implements GridBrowser, OnFolderBrowserListener, OnUnFolderBrowserListener{
	public static boolean IfFolder = true;
	private String Filetype;
	
	private Context mContext						   = null;
	private GLGridLayout gridlayout 				   = null;
	
	Bitmap titleBitmap        						   = null;
	Bitmap infoTitleBitmap   						   = null;
	Bitmap selectedBitmap     						   = null;
	Bitmap unselectBitmap    						   = null;
	Bitmap folderBitmap            					   = null;
	Bitmap iconBitmap                                  = null;
	Bitmap preiconBitmap                               = null;
	Bitmap prefolderBitmap                             = null;
	Bitmap itmBgBitmap                                 = null;
	private FolderBrowser Folderbrowser 			   = null;
	private UnFolderBrowser UnFolderbrowser 		   = null;
	ArrayList<FilebrowserItemData> UnFolderBrowserlist = null;
	List<FilebrowserItemData> folderBrowserList 	   = null;
	
	public  Filebrowser(Context cnt, GLGridLayout gridl){
		mContext = cnt;
		gridlayout = gridl;
	}
	
	//@Override
	public int getCount() {
		if(IfFolder){
			if(folderBrowserList == null)
				return 0;
			return folderBrowserList.size();
		}else{
			if(UnFolderBrowserlist == null)
				return 0;
			return UnFolderBrowserlist.size();
		}
	}

	public int getItemResId(int idx){
		if(IfFolder){
			if(folderBrowserList == null || idx >= folderBrowserList.size())
				return -1;
			return folderBrowserList.get(idx).GetUnfocusID();
		}else{
			if(UnFolderBrowserlist == null || idx >= UnFolderBrowserlist.size())
				return -1;
			return UnFolderBrowserlist.get(idx).GetUnfocusID();
		}
	}
	
/*	//@Override
	public Bitmap getItemBitmap(int idx) {
//		if(idx < 0)
			return null;
		
		int resId = getItemResId(idx);
		String filename = getItemName(idx);
		
		if(resId < 0 || filename == null)
			return null;
		
		Bitmap devBmp = null;
		if(Filetype.equals("Audio")){
			devBmp = Bitmap.createBitmap(548, 100, Bitmap.Config.ARGB_8888);
			MiscUtil.drawBitmapIcon(devBmp, unselectBitmap, 0, 0);
			
			Bitmap icnbmp = null;
			if(resId == R.drawable.icon_browse_folder){
				icnbmp = folderBitmap;
			}else if(resId == R.drawable.icon_pre_music){
				icnbmp = iconBitmap;
			}else{
				icnbmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
			}
			MiscUtil.drawBitmapIcon(devBmp, icnbmp, 30, 32);

			MiscUtil.drawBitmapText(devBmp, filename, 80, 60, 26, Color.WHITE, Align.LEFT,
					new Rect(80, 0, 520, 100));
		}
		else{
			devBmp = Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888);
			MiscUtil.drawBitmapIcon(devBmp, unselectBitmap, 2, 15);
			
			Bitmap icnbmp = null;
			if(resId == R.drawable.icon_browse_folder){
				icnbmp = folderBitmap;
			}else if(resId == R.drawable.icon_browse_picture){
				String pathname = getItemAbsoluteName(idx);
				icnbmp = MiscUtil.getThumbImage(pathname, 140, 196);
			}
			if(icnbmp == null){
				icnbmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
			}
			if(icnbmp != null){
				MiscUtil.drawBitmapIcon(devBmp, icnbmp, 30, 26+15);
			}

			MiscUtil.drawBitmapText(devBmp, filename, 28, 266, 26, Color.WHITE, Align.LEFT,
					new Rect(0, 230, 170, 300));
		}
		return devBmp;
	}
*/	
	public Bitmap getTitleBitmap() {
		return titleBitmap;
	}

	public Bitmap getInfoTitleBitmap() {
		return infoTitleBitmap;
	}
	
	public void SetFileType(String DeviceName, String filetype){
		Filetype = filetype;
		if (Filetype.equals("Audio")) {
			titleBitmap = BitmapFactory.decodeResource(mContext.getResources(),	R.drawable.title_music);
			infoTitleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.preview_title_music);
			unselectBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.music_item_unsel);
			selectedBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.music_item_sel);
			folderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pre_folder);
			iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pre_music);
			preiconBitmap = iconBitmap;
			itmBgBitmap = unselectBitmap;
		} else if (Filetype.equals("Picture")) {
			titleBitmap = BitmapFactory.decodeResource(mContext.getResources(),	R.drawable.title_picture);
			infoTitleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.preview_title_picture);
			unselectBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.list_pre_unsel);
			selectedBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.list_pre_sel);
			folderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_browse_folder);
			iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_browse_picture);
			preiconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pre_picture);
			itmBgBitmap = unselectBitmap;
//			itmBgBitmap = Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888);
//			MiscUtil.drawBitmapIcon(itmBgBitmap, unselectBitmap, 2, 15);
		}else/* if (Filetype.equals("Text")) */{
			titleBitmap = BitmapFactory.decodeResource(mContext.getResources(),	R.drawable.title_txt);
			infoTitleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.preview_title_txt);
			unselectBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.list_pre_unsel);
			selectedBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.list_pre_sel);
			folderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_browse_folder);
			iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_browse_txt); //
			preiconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pre_txt);
			itmBgBitmap = unselectBitmap;
//			itmBgBitmap = Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888);
//			MiscUtil.drawBitmapIcon(itmBgBitmap, unselectBitmap, 2, 15);
		}
		prefolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pre_folder);
		if (IfFolder) {
			Folderbrowser = new FolderBrowser(mContext, filetype, DeviceName);//
			Folderbrowser.SetFileType(filetype);
			Folderbrowser.SetOnFolderBrowserListener(this);
		} else {
			UnFolderBrowserlist = new ArrayList<FilebrowserItemData>();
			UnFolderbrowser = new UnFolderBrowser(mContext, DeviceName); //
			UnFolderbrowser.SetFileType(filetype);
			UnFolderbrowser.SetOnUnFolderBrowserListener(this);
		}

	}

	//@Override
	public String getItemAbsoluteName(int idx) {
		if(IfFolder){
			if(folderBrowserList == null || idx >= folderBrowserList.size())
				return null;
			return folderBrowserList.get(idx).getAbsoluteFilePath();
		}else{
			if(UnFolderBrowserlist == null || idx >= UnFolderBrowserlist.size())
				return null;
			return UnFolderBrowserlist.get(idx).getAbsoluteFilePath();
		}
	}
	
	public String getItemName(int idx) {
		if(IfFolder){
			if(folderBrowserList == null || idx >= folderBrowserList.size())
				return null;
			return folderBrowserList.get(idx).getFileName();
		}else{
			if(UnFolderBrowserlist == null || idx >= UnFolderBrowserlist.size())
				return null;
			return UnFolderBrowserlist.get(idx).getFileName();
		}
	}
	
	public void StartFilebrowser() {
		if (IfFolder) {
			Folderbrowser.FirstFileBrowser();
//			folderBrowserList = Folderbrowser.FirstFileBrowser();
//			if(gridlayout != null)
//				gridlayout.BrowseFinish();
		} else {
			UnFolderbrowser.StartLoadData(UnFolderbrowser.skywbrowser);
		}
	}
	
	 public boolean IFIsFolder(int position)
	 {
		if (IfFolder == true)
			return Folderbrowser.IFIsFolder(position);
		else
			return false;
	 }
	 
	 public void EnterDir(int position) {
		if (IfFolder == true)
//			folderBrowserList = Folderbrowser.EnterFocusDir(position);
			Folderbrowser.EnterFocusDir(position);
		else
		{
//			UnFolderbrowser.StartLoadData(UnFolderbrowser.skywbrowser);
		}
	}
	 
	public String getCurDirPath(){
		if (IfFolder == true)
			return Folderbrowser.getCurDirPath();
		else
		{
		}
		return null;
	}

	//@Override
	public boolean upOneLevel() {
		if (IfFolder == true){
			return Folderbrowser.upOneLevel();
//			folderBrowserList = Folderbrowser.upOneLevel();
//			if(folderBrowserList != null)
//				return true;
		}else
		{
//			UnFolderbrowser.StartLoadData(UnFolderbrowser.skywbrowser);
		}
		return false;
	}

	public Bitmap getSelectedBitmap() {
		return selectedBitmap;
	}

	public List<FilebrowserItemData> GetFileInfo(int idx) {
		List<FilebrowserItemData> lstInfo = null;
		String filename = getItemAbsoluteName(idx);
				
		if (IFIsFolder(idx)) {
			if (IfFolder == true) {
				lstInfo = Folderbrowser.GetPreFileList(filename);
				if (lstInfo != null) {
				} else {
					lstInfo = new ArrayList<FilebrowserItemData>();
					lstInfo.add(new FilebrowserItemData(
							mContext.getResources().getString(R.string.tips), 0, 0));// name
					lstInfo.add(new FilebrowserItemData(
							mContext.getResources().getString(R.string.folder_empty), 0, 0));// date
				}
			} else {
/*				UnFolderBrowser browser = new UnFolderBrowser(
						this.getContext(), idx); //
				browser.SetFileType(mfiletype);
				browser.SetOnUnFolderBrowserListener(this);
				Browserlist.clear();
				browser.StartLoadData(UnFolderBrowser.skywprebrowser);
*/
				}
		} else {
			File file = new File(filename);
			String LastModifiedDate = "";
			Date dt = new Date(file.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd");
			LastModifiedDate = sdf.format(dt);
			
			lstInfo = new ArrayList<FilebrowserItemData>();
			lstInfo.add(new FilebrowserItemData(file.getName(), 0, 0));// name
			lstInfo.add(new FilebrowserItemData(LastModifiedDate, 0, 0));// date

			String size = MiscUtil.GetFileSize(file);
			lstInfo.add(new FilebrowserItemData(size, 0, 0));
		}
		
		return lstInfo;
	}

	public void setInfoBitmap(int idx) {
		List<FilebrowserItemData> lstInfo = GetFileInfo(idx);
		if(lstInfo != null && lstInfo.size() > 0)
		{
			Bitmap devBmp = Bitmap.createBitmap(350, 440, Bitmap.Config.ARGB_8888);
			int fontsize = 26;
			Canvas canvas = new Canvas(devBmp);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(fontsize);
			paint.setTextAlign(Align.LEFT);
			paint.setAntiAlias(true);	
			
			Paint paint1 = new Paint();    
			paint1.setColor(Color.rgb(0xa0, 0xb2,0xda));
			paint1.setFakeBoldText(true);
			paint1.setAntiAlias(true);	
			paint1.setTextSize(fontsize);
			paint1.setTextAlign(Align.LEFT);
			for(int i=0; i<lstInfo.size() && i<11; i++){
				String text = lstInfo.get(i).getFileName();
				if(text == null)
					break;
				
				int ResID = lstInfo.get(i).GetfocusID();
				if(ResID != 0)
				{
					text = MiscUtil.breakText(text, fontsize, 280);
					if(ResID == R.drawable.icon_browse_folder)
						canvas.drawBitmap(prefolderBitmap, 0, (1+i)*38 -23, null);
					else
						canvas.drawBitmap(preiconBitmap, 0, (1+i)*38 -23, null);
					canvas.drawText(text, 55, (1+i)*38, paint);
				}
				else
				{
					if(lstInfo.size() == 3)
					{
						String name = null ;
						if(i == 0)
							name = mContext.getString(R.string.filename);
						if(i == 1)
							name = mContext.getString(R.string.filetime);
						if(i == 2)
							name = mContext.getString(R.string.filesize);
						canvas.drawText(name, 0, (1+i)*40, paint1);
						text = MiscUtil.breakText(text, fontsize, 260);
						canvas.drawText(text,90, (1+i)*40, paint);
					}
					else
						canvas.drawText(text, 10, (1+i)*40, paint);
					
				}
			}
			gridlayout.setInfoBitmap(devBmp);
		}else{
			gridlayout.setInfoBitmap(null);
		}
	}

	//@Override
	public ArrayList<String> getPlayList() {
		ArrayList<String> lst = new ArrayList<String>();
		if (IfFolder == true){
			Folderbrowser.GetPlayerList(lst);
		}
		else
		{
			lst.clear();
			for (int i = 0; i < UnFolderBrowserlist.size(); i++) {
				lst.add(UnFolderBrowserlist.get(i).getAbsoluteFilePath());
			}
		}
		
		return lst;
	}

	//@Override
	public boolean MountDevice(String deviceName) {
		// should not be called.
		return false;
	}

	//@Override
	public boolean UnmountDevice(String deviceName) {
		// should not be called.
		return false;
	}

	//@Override
	public int findItem(String AbsoluteName) {
		if (IfFolder == true){
			for(int i=0; i<folderBrowserList.size(); i++){
				if(AbsoluteName.equals(folderBrowserList.get(i).getAbsoluteFilePath()))
					return i;
			}
		}else{
			for(int i=0; i<UnFolderBrowserlist.size(); i++){
				if(AbsoluteName.equals(UnFolderBrowserlist.get(i).getAbsoluteFilePath()))
					return i;
			}
		}
		return -1;
	}
	
	//@Override
	public void OnFolderBrowser(FilebrowserItemData... value) {
//		if(value != null)
//		{
//			for (FilebrowserItemData image : value) {
//				folderBrowserList.add(image);
//			}
//		}
//		else	//no files
//		{
//		}
	}

	//@Override
	public void OnFolderBrowserFinish() {
		folderBrowserList = Folderbrowser.GetAllFileList();
		if(gridlayout != null)
			gridlayout.browseFinish();
	}
	
	//@Override
	public void OnUnFolderBrowser(FilebrowserItemData... FID) {
		if(FID != null)
		{
			for (FilebrowserItemData image : FID) {
				UnFolderBrowserlist.add(image);
			}
		}
		else	//no files
		{
		}
	}

	//@Override
	public void OnUnFolderBrowserFinish() {
		if(gridlayout != null)
			gridlayout.browseFinish();
	}

	public Bitmap getItemBkBitmap() {
		return itmBgBitmap;
	}

	public Bitmap getItemIconBitmap(int idx) {
		if(idx < 0)
			return null;
		
		int resId = getItemResId(idx);
		
		if(resId < 0)
			return null;
		
		Bitmap icnbmp = null;
		if(Filetype.equals("Audio")){
			if(resId == R.drawable.icon_browse_folder){
				icnbmp = folderBitmap;
			}else if(resId == R.drawable.icon_pre_music){
				icnbmp = iconBitmap;
			}else{
				icnbmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
			}
		}
		else
		if(Filetype.equals("Text")){
			if(resId == R.drawable.icon_browse_folder){
				icnbmp = folderBitmap;
			}else {
				icnbmp = iconBitmap;
			}
		}
		else
		{
			if(resId == R.drawable.icon_browse_folder){
				icnbmp = folderBitmap;
			}else if(resId == R.drawable.icon_browse_picture){
//				String pathname = getItemAbsoluteName(idx);
//				icnbmp = MiscUtil.getThumbImage(pathname, 140, 196);
//				if(icnbmp == null){
				icnbmp = iconBitmap;
//				}
			}
		}
		return icnbmp;
	}

	public Bitmap getItemNameBitmap(int idx) {
		if(idx < 0)
			return null;
		
		String filename = getItemName(idx);
		
		if(filename == null)
			return null;
		
		Bitmap devBmp = null;
		if(Filetype.equals("Audio")){
			devBmp = Bitmap.createBitmap(420, 60, Bitmap.Config.ARGB_8888);
			String fname = MiscUtil.breakText(filename, 32, 420);
			MiscUtil.drawBitmapText(devBmp, fname, 0, 50, 32, Color.WHITE, Align.LEFT,	null);
		}
		else{
			devBmp = Bitmap.createBitmap(140, 34, Bitmap.Config.ARGB_8888);
			String fname = MiscUtil.breakText(filename, 26, 140);
			MiscUtil.drawBitmapText(devBmp, fname, 70, 27, 26, Color.WHITE, Align.CENTER, null);
		}
		return devBmp;
	}

	public Bitmap getItemScrollNameBitmap(int idx) {
		if(idx < 0)
			return null;
		
		String filename = getItemName(idx);
		
		if(filename == null)
			return null;
		
		Bitmap devBmp = null;
		if(Filetype.equals("Audio")){
			int fontSize = 32;
			int widthPixel = (int)MiscUtil.getTextWidth(filename, fontSize);
			if(widthPixel > 1180)
				widthPixel = 1180;
			if(widthPixel < 420)
				widthPixel = 420;
			
			devBmp = Bitmap.createBitmap(widthPixel, 60, Bitmap.Config.ARGB_8888);
			MiscUtil.drawBitmapText(devBmp, filename, 0, 50, fontSize, Color.WHITE, Align.LEFT, null);
		}
		else{
			int fontSize = 26;
			int widthPixel = (int)MiscUtil.getTextWidth(filename, fontSize);
			if(widthPixel > 1180)
				widthPixel = 1180;
			if(widthPixel <= 140){
				devBmp = Bitmap.createBitmap(140, 34, Bitmap.Config.ARGB_8888);
				MiscUtil.drawBitmapText(devBmp, filename, 70, 27, fontSize, Color.WHITE, Align.CENTER, null);
			}else{
				devBmp = Bitmap.createBitmap(widthPixel + fontSize, 34, Bitmap.Config.ARGB_8888);
				MiscUtil.drawBitmapText(devBmp, filename, 0, 27, fontSize, Color.WHITE, Align.LEFT, null);
			}
		}
		return devBmp;
	}

	public int getNameWidthPixel() {
		if(Filetype.equals("Audio")){
			return 448;
		}
		return 140;
	}
	
	class mRequestPicThread extends Thread {
		int mpos = 0;
		int mtotal = 0;

		mRequestPicThread(int pos, int total) {
			mpos = pos;
			mtotal = total;
		}

		public void run() {
			synchronized (this) {
				for (int i = mpos; i < mtotal + mpos && i < getCount()
						&& picThreadbutton; i++) {
					int resId = getItemResId(i);
					if (resId == R.drawable.icon_browse_picture) {
						String pathname = getItemAbsoluteName(i);
						Bitmap icnbmp = MiscUtil.getThumbImage(pathname, 140,
								196);
						if (icnbmp != null && gridlayout != null)
							gridlayout.updataThumbImage(i, icnbmp);
					}
				}
			}
		}

	}

	mRequestPicThread requestPic = null;
	private boolean picThreadbutton = false;

	public void requestThumbImage(int pos, int total) {
		if (Filetype.equals("Picture")) {
			if (requestPic == null) {
				requestPic = new mRequestPicThread(pos, total);
				picThreadbutton = true;
				requestPic.start();
			}

		}

	}

	public void cancelThumbImage() {
		if (Filetype.equals("Picture") && requestPic != null) {
			picThreadbutton = false;
			synchronized (requestPic) {
				requestPic = null;
			}
		}
	}

	
	class RequestInfoThread extends Thread {
		int index = -1;
		boolean mStop = false;

		RequestInfoThread(int idx) {
			index = idx;
		}

		public void run() {
			synchronized (this) {
				if (!mStop && index >= 0 && index < getCount()) {
					setInfoBitmap(index);
				}
			}
		}

		public void reqStop() {
			FolderBrowser.reqStopPreList = true;
			mStop = true;
		}
		public void reqStart(){
			FolderBrowser.reqStopPreList = false;
			start();
		}
	}

	RequestInfoThread requestInfo = null;

	public void requestInfoImage(int idx) {
		cancelInfoImage();
		requestInfo = new RequestInfoThread(idx);
		requestInfo.reqStart();
	}

	public void cancelInfoImage() {
		if(requestInfo != null){
			requestInfo.reqStop();
			synchronized (requestInfo) {
				requestInfo = null;
			}
		}
	}

	public Bitmap  getWarningBitmap()
	{		
		Bitmap warnBg=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_info_content);
		int width = warnBg.getWidth();
        int height = warnBg.getHeight();

		int canvas_w = (int)(width*Resolution.getScaleX());
		int canvas_h = (int)(height*Resolution.getScaleY());
		Bitmap devBmp = Bitmap.createBitmap(canvas_w, canvas_h, Bitmap.Config.ARGB_8888);
		int fontsize = (int) (36*Resolution.getScaleX());
		Canvas canvas = new Canvas(devBmp);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(fontsize);
		paint.setTextAlign(Align.LEFT);
		paint.setAntiAlias(true);	

        Matrix matrix = new Matrix();
        matrix.postScale(Resolution.getScaleX(), Resolution.getScaleY());
        Bitmap newBmp = Bitmap.createBitmap(warnBg, 0, 0, width, height,matrix, true);
		canvas.drawBitmap(newBmp, 0, 0, null);
//		canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.button_info_content_sel), 201, 170, null);
		String data = mContext.getResources().getString(R.string.not_folder);
		
		if (Filetype.equals("Audio")) 
			data = data + mContext.getResources().getString(R.string.music_file);
		else
		if (Filetype.equals("Picture"))	
			data = data + mContext.getResources().getString(R.string.pic_file);
		else
			data = data + mContext.getResources().getString(R.string.txt_file);
				
		canvas.drawText(data,  300*Resolution.getScaleX(), 200*Resolution.getScaleY(), paint);	
		return devBmp;
	}
}
