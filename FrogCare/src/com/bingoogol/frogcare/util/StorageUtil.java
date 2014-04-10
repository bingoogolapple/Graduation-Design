package com.bingoogol.frogcare.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

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

	public static File getDownloadDir() {
		File downloadDir = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.file.DIR_DOWNLOAD);
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		return downloadDir;
	}
	
	/**
	 * 获取可用sd空间大小
	 * 
	 * @param context
	 *            应用程序上下文
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getAvailableSize(Context context) {
		StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		long blocks = statfs.getAvailableBlocks();
		long size = statfs.getBlockSize();
		return blocks * size;
	}

	/**
	 * sd卡空间是否够用
	 * 
	 * @param context
	 *            应用程序上下文
	 * @param contentLength
	 *            文件大小
	 * @return
	 */
	public static boolean isSaveable(Context context, int contentLength) {
		long avaliable = getAvailableSize(context);
		return avaliable > contentLength ? true : false;
	}
}
