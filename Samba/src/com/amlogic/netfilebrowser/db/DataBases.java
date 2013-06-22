package com.amlogic.netfilebrowser.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amlogic.netfilebrowser.NetDeviceBrowser;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBases {
	private static final String TAG = "DataBases";
	private DBOpenHelper openHelper;

	public DataBases(Context context) {
		openHelper = new DBOpenHelper(context);
	}

	public Map<String, Object> getData(String name){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "
						+DBOpenHelper.USER+", "
						+DBOpenHelper.PASSWORD+", "
						+DBOpenHelper.IS_SELECTED+" from "
						+DBOpenHelper.DEVICE_TABLE+" where "
						+DBOpenHelper.NAME+"=?", new String[]{name});
		Map<String, Object> data = new HashMap<String, Object>();
		Log.d(TAG, "get data by:"+name);
		if(cursor.moveToFirst()) {
			int usercol = cursor.getColumnIndex(DBOpenHelper.USER);
			int passwordcol = cursor.getColumnIndex(DBOpenHelper.PASSWORD);
			int selcol = cursor.getColumnIndex(DBOpenHelper.IS_SELECTED);
			do{

				String user = cursor.getString(usercol);
				String password = cursor.getString(passwordcol);
				Integer is_sel = cursor.getInt(selcol);
				Log.d(TAG, "getData,user:"+user
						+", password:"+password
						+", mark:"+Integer.toString(is_sel));
				data.put(DBOpenHelper.NAME, name);
				data.put(DBOpenHelper.USER, user);
				data.put(DBOpenHelper.PASSWORD, password);
				data.put(DBOpenHelper.IS_SELECTED, is_sel);
			}while(cursor.moveToNext());
		}
		else {
			Log.d(TAG, "no data(name is "+name+") on db!");
		}
		cursor.close();
		db.close();
		return data;
	}

	public Map<String, Object> getUserPassword(String name){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "
						+DBOpenHelper.USER+", "
						+DBOpenHelper.PASSWORD+" from "
						+DBOpenHelper.DEVICE_TABLE+" where "
						+DBOpenHelper.NAME+"=?", new String[]{name});
		Map<String, Object> data = new HashMap<String, Object>();
		Log.d(TAG, "getUserPassword, name:"+name);
		if(cursor.moveToFirst()) {
			int usercol = cursor.getColumnIndex(DBOpenHelper.USER);
			int passwordcol = cursor.getColumnIndex(DBOpenHelper.PASSWORD);
			do{
				String user = cursor.getString(usercol);
				String password = cursor.getString(passwordcol);
				Log.d(TAG, "getUserPassword, user:"+user
						+", password:"+password);
				data.put(DBOpenHelper.NAME, name);
				data.put(DBOpenHelper.USER, user);
				data.put(DBOpenHelper.PASSWORD, password);
			}while(cursor.moveToNext());
		}
		else {
			Log.d(TAG, "no data(name is "+name+") on db!");
		}
		cursor.close();
		db.close();
		return data;
	}
	
	public int getMark(String name){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "
						+DBOpenHelper.IS_SELECTED+" from "
						+DBOpenHelper.DEVICE_TABLE+" where "
						+DBOpenHelper.NAME+"=?", new String[]{name});
		int mark = NetDeviceBrowser.UNMARK;
		if(cursor.moveToFirst()) {
			int selcol = cursor.getColumnIndex(DBOpenHelper.IS_SELECTED);
			do{
				Log.d(TAG, "getMark, name:"+name+",mark:"+Integer.toString(cursor.getInt(selcol)));
				mark = cursor.getInt(selcol);
			}while(cursor.moveToNext());
		}
		else {
			Log.d(TAG, "no data(name is "+name+") on db!");
		}
		cursor.close();
		db.close();
		return mark;
	}
	
	public ArrayList<String> getMarkDevice(Integer mark) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "
						+DBOpenHelper.NAME+", "
						+DBOpenHelper.IS_SELECTED+" from "
						+DBOpenHelper.DEVICE_TABLE, null);
		ArrayList<String> data = new ArrayList<String>();
		
		if(cursor.moveToFirst()) {
			int namecol = cursor.getColumnIndex(DBOpenHelper.NAME);
			int selcol = cursor.getColumnIndex(DBOpenHelper.IS_SELECTED);
			do{
				Log.d(TAG, "getMarkDevice, name:"+cursor.getString(namecol)+",mark:"+Integer.toString(cursor.getInt(selcol)));
				if(cursor.getInt(selcol) == mark)
					data.add(cursor.getString(namecol));
			}while(cursor.moveToNext());
		}
		else {
			Log.d(TAG, "no data on db!");
		}
		cursor.close();
		db.close();
		return data;
	}
	
	public void save(Map<String, Object> map){
		String name = null;
		String user = null;
		String password = null;
		Integer is_sel = NetDeviceBrowser.UNMARK;
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(Map.Entry<String, Object> entry : map.entrySet()){
				Log.d(TAG, "save, key:"+entry.getKey()+",value:"+entry.getValue());
				if(entry.getKey().equals(DBOpenHelper.NAME)){
					name = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.USER)){
					user = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.PASSWORD)){
					password = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.IS_SELECTED)){
					is_sel = (Integer)entry.getValue();
				}
			}
			Log.d(TAG, "save, name:"+name
					+", user:"+user
					+", password:"+password
					+", is_sel:"+Integer.toString(is_sel));
			if(name != null) {
				db.execSQL("insert into "
					+DBOpenHelper.DEVICE_TABLE+"("
					+DBOpenHelper.NAME+", "
					+DBOpenHelper.USER+", "
					+DBOpenHelper.PASSWORD+", "
					+DBOpenHelper.IS_SELECTED+") values(?,?,?,?)",
					new Object[]{name, user, password, is_sel});
			}
			db.setTransactionSuccessful();
		}
		catch (SQLException e) {
            Log.e(TAG, "save, error:"+e.toString());
		} 
		finally{
			db.endTransaction();
		}
		db.close();
	}

	public void update(Map<String, Object> map){
		String name = null;
		String user = null;
		String password = null;
		Integer is_sel = NetDeviceBrowser.UNMARK;
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(Map.Entry<String, Object> entry : map.entrySet()){
				Log.d(TAG, "update, key:"+entry.getKey()+",value:"+entry.getValue());
				if(entry.getKey().equals(DBOpenHelper.NAME)){
					name = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.USER)){
					user = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.PASSWORD)){
					password = (String)entry.getValue();
				}
				if(entry.getKey().equals(DBOpenHelper.IS_SELECTED)){
					is_sel = (Integer)entry.getValue();
				}
			}
			Log.d(TAG, "update, name:"+name
					+", user:"+user
					+", password:"+password
					+", is_sel:"+Integer.toString(is_sel));
			if(name != null) {
				if((user!= null) && (password != null)) {
					db.execSQL("update "
						+DBOpenHelper.DEVICE_TABLE+" set "
						+DBOpenHelper.USER+"=?, "
						+DBOpenHelper.PASSWORD+"=?, "
						+DBOpenHelper.IS_SELECTED+"=? where "
						+DBOpenHelper.NAME+"=?",
						new Object[]{user, password, is_sel, name});
				}
				else if(user != null) {
					db.execSQL("update "
							+DBOpenHelper.DEVICE_TABLE+" set "
							+DBOpenHelper.USER+"=?, "
							+DBOpenHelper.IS_SELECTED+"=? where "
							+DBOpenHelper.NAME+"=?",
							new Object[]{user, is_sel, name});
				}
				else {
					db.execSQL("update "
							+DBOpenHelper.DEVICE_TABLE+" set "
							+DBOpenHelper.IS_SELECTED+"=? where "
							+DBOpenHelper.NAME+"=?",
							new Object[]{is_sel, name});
				}
			}
			db.setTransactionSuccessful();
		}
		catch (SQLException e) {
            Log.e(TAG, "update, error:"+e.toString());
		} 
		finally{
			db.endTransaction();
		}
		db.close();
	}

	public void delete(String name){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from "
				+DBOpenHelper.DEVICE_TABLE+" where "
				+DBOpenHelper.NAME+"=?", new Object[]{name});
		db.close();
	}
	
	public void deleteAll() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("DELETE FROM "
				+DBOpenHelper.DEVICE_TABLE);
		db.close();
	}
}
