package com.amlogic.netfilebrowser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	//private static final String TAG = "DBOpenHelper";
	private static final String DBNAME = "DateBase.db";
	private static final int VERSION = 1;
	public static final String DEVICE_TABLE = "device_table";	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String IS_SELECTED = "is_selected";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+DEVICE_TABLE+" ("
				+ID+" integer primary key autoincrement, "
				+NAME+" TEXT, "+USER+" TEXT, "+PASSWORD+" TEXT, "+IS_SELECTED+" INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+DEVICE_TABLE);
		onCreate(db);
	}	
}
