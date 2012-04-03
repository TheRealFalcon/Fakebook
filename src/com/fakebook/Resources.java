package com.fakebook;

import java.io.File;

import android.content.SharedPreferences;
import android.os.Environment;

public class Resources {
	private static final String[] VALID_EXTENSIONS = {".png", ".jpg", ".bmp", ".txt"};
	private static final String APPLICATION_NAME = "fakebook";
	public static String getApplicationDirectory(SharedPreferences settings)
	{
		//Hack for a few devices
		File externalSd = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd");
		String applicationDirectory = null;
		if (Environment.isExternalStorageRemovable()) {
			if (Environment.getExternalStorageState().equals("mounted")) {
				applicationDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATION_NAME + "/";
			}
		}		
		else if (externalSd.isDirectory()) {
			applicationDirectory = externalSd.getAbsolutePath() + "/" + APPLICATION_NAME + "/";
		}
		
		if (applicationDirectory != null) {
			settings.edit().putString("ApplicationDirectory", applicationDirectory).commit();
		}
		return applicationDirectory;
	}
	
	public static String[] getValidExtensions()
	{
		return VALID_EXTENSIONS;
	}
}
