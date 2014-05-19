package com.bingoogol.frogcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bingoogol.frogcare.ui.MainActivity;
import com.bingoogol.frogcare.util.Logger;

/**
 * 主要用于通过拨打指定的号码开启某个activity,
 * 在手机处于被盗状态时，盗窃者的所有外拨电话都会拨打到安全号码
 * 
 * @author bingoogol@sina.com
 * 
 */
public class OutCallReceiver extends BroadcastReceiver {
	private static String TAG = "OutCallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 手机防盗
		String number = getResultData();
		Logger.i(TAG, number);
		if ("8888".equals(number)) {
			Intent lostFindIntent = new Intent(context, MainActivity.class);
			// activity是运行在在自己的任务栈中的，广播接收者或者服务里面没有任务栈，必须显示的指定flag,指定要激活的activity在自己的任务栈里面运行
			lostFindIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostFindIntent);
			// 终止掉这个电话
			// 外拨电话的广播显示的指明了广播接收者，不能通过 abortBroadcast()种植广播
			setResultData(null);
		}
	}
}
