package com.bingoogol.frogcare.util;

import android.os.Environment;

public class StorageUtil {
	private StorageUtil() {
	}

	/**
	 * 判断外存储是否可写
	 * 
	 * @return
	 */
	public static boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
}
