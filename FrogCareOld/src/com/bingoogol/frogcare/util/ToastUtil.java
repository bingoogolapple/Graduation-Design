package com.bingoogol.frogcare.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐丝工具类
 * 
 * @author bingoogol@sina.com 2014-2-18
 */
public class ToastUtil {

	private ToastUtil() {
	}

	/**
	 * 根据文本打印吐丝
	 * 
	 * @param context
	 *            应用程序上下文
	 * @param text
	 *            要显示的文本
	 */
	public static void makeText(Context context, CharSequence text) {
		if (text.length() > 10) {
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 根据资源id打印吐丝
	 * 
	 * @param context
	 *            应用程序上下文
	 * @param resId
	 *            资源id
	 */
	public static void makeText(Context context, int resId) {
		makeText(context, context.getResources().getString(resId));
	}
}