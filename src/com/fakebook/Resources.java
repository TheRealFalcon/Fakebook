package com.fakebook;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;



public class Resources {
	private static final String[] VALID_EXTENSIONS = {".png", ".jpg", ".bmp", ".txt"};
	private static final String APPLICATION_NAME = "fakebook";
	SQLiteDatabase db;
	
	private class DictionaryOpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_NAME = "fakebook";
		private static final String TABLE_CREATE =
				"CREATE TABLE settings (" +
						"key TEXT, " +
						"value TEXT);";
		
		DictionaryOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE);
			ContentValues insertValues = new ContentValues();
			insertValues.put("key", "applicationDirectory");
			db.insert("settings", "value", insertValues);
			System.out.println("Here");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}    
		
	}
	
	
	
	public Resources(Context context) {
		DictionaryOpenHelper helper = new DictionaryOpenHelper(context);
		db = helper.getWritableDatabase();
		if (getApplicationDirectory() == null) {
			setApplicationDirectory(getDefaultDirectory());
		}
	}
	
	private String getDefaultDirectory() {
		String applicationDirectory = null;
		if (Environment.isExternalStorageRemovable()) {
			if (Environment.getExternalStorageState().equals("mounted")) {
				applicationDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATION_NAME + "/";
			}
		}
		
		else {
			//Hack for a few devices
			applicationDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd" + "/" + APPLICATION_NAME + "/";
		}
		File appFile = new File(applicationDirectory);
		if (appFile.isDirectory()) {
			return applicationDirectory;
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public String getApplicationDirectory() {
		Cursor cursor = db.query("settings", new String[] {"value"}, "key='applicationDirectory'", null, null, null, null);
		cursor.moveToFirst();
		//		try {
			return cursor.getString(0);
//		} catch (Exception e) {
//			return null;
//		}
	}
	
	public boolean setApplicationDirectory(String dir) {
		if (!dir.endsWith("/")) {
			dir = dir + "/";
		}
		File dirFile = new File(dir);
		if (!dirFile.isDirectory()) {
			return false;
		}
		ContentValues updateValues = new ContentValues();
		updateValues.put("value", dir);
		db.update("settings", updateValues, "key='applicationDirectory'", null);
		return true;
	}

	public static String[] getValidExtensions()
	{
		return VALID_EXTENSIONS;
	}
}
