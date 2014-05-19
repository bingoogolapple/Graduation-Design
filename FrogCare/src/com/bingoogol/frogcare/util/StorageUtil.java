package com.bingoogol.frogcare.util;

import java.io.File;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

public class StorageUtil {
	private static final String TAG = "StorageUtil";

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

	/**
	 * 获取当前app下载文件存储目录
	 * 
	 * @return
	 */
	public static File getDownloadDir() {
		File downloadDir = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.file.DIR_DOWNLOAD);
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		return downloadDir;
	}

	/**
	 * 获取手机重启后拍摄照片的存储目录
	 * 
	 * @return
	 */
	public static File getTheftDir() {
		File downloadDir = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.file.DIR_THEFT);
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		return downloadDir;
	}

	/**
	 * 获取当前app错误日志文件存储目录
	 * 
	 * @return
	 */
	public static File getFeedbackDir() {
		File feedbackDir = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.file.DIR_FEEDBACK);
		if (!feedbackDir.exists()) {
			feedbackDir.mkdirs();
		}
		return feedbackDir;
	}

	/**
	 * 根据Uri获取文件的真实路径
	 * 
	 * @param uri
	 * @param context
	 * @return
	 */
	public static String getRealPathByUri(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				Logger.e(TAG, e.getMessage());
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
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

	/**
	 * 获取可用内存空间
	 * 
	 * @param context
	 * @return long byte 单位 大小
	 */
	@SuppressWarnings("static-access")
	public static long getAvailMemSize(Context context) {
		MemoryInfo memoryInfo = new MemoryInfo();
		((ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}
}
