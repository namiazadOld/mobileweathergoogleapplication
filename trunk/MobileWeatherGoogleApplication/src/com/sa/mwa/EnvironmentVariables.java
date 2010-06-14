package com.sa.mwa;

import android.content.Context;
import android.content.SharedPreferences;

public class EnvironmentVariables {

	public static final String SETTINGS_NAME = "mwa_settings";
	public static final int DEFAULT_REFRESH_RATE = 1;
	public static final String DEFAULT_DEVICE_NAME = "MY_DEVICE";
	
	public static final String DEVICE_NAME_KEY = "device_name";
	public static final String REFRESH_RATE_KEY = "refresh_rate";
	
	public static void initalize(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (!settings.contains(DEVICE_NAME_KEY))
			editor.putString(DEVICE_NAME_KEY, DEFAULT_DEVICE_NAME);
		if (!settings.contains(REFRESH_RATE_KEY))
			editor.putInt(REFRESH_RATE_KEY, DEFAULT_REFRESH_RATE);
		editor.commit();
	}
	
	public static void settings(Context context, String deviceName, int refreshRate)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DEVICE_NAME_KEY, deviceName);
		editor.putInt(REFRESH_RATE_KEY, refreshRate);
		editor.commit();
	}
	
	public static String getDeviceName(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getString(DEVICE_NAME_KEY, DEFAULT_DEVICE_NAME);
	}
	
	public static int getRefreshRate(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(EnvironmentVariables.SETTINGS_NAME, 0);
		int i = settings.getInt(REFRESH_RATE_KEY, DEFAULT_REFRESH_RATE);
		return i;
	}
}
