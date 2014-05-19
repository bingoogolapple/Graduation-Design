package com.bingoogol.frogcare.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bingoogol.frogcare.R;

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
		Toast toast = new Toast(context);
		View view = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
		TextView tv_custom_toast_msg = (TextView) view.findViewById(R.id.tv_toast_msg);
		tv_custom_toast_msg.setText(text);
		toast.setView(view);
		if (text.length() > 10) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
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