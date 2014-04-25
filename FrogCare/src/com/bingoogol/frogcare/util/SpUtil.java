package com.bingoogol.frogcare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具类，使用前必须先初始化，最好在Application的onCreate方法里初始化
 * 
 * @author bingoogol@sina.com 2014-4-25
 */
public class SpUtil {

	private static SharedPreferences mSharedPreferences;

	private SpUtil() {
	}

	public static void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
	}

	public static void putString(String key, String value) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}

	public static void putInt(String key, int value) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);
	}
}
