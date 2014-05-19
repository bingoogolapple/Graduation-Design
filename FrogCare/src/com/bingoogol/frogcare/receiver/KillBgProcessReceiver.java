package com.bingoogol.frogcare.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bingoogol.frogcare.util.Logger;

public class KillBgProcessReceiver extends BroadcastReceiver {
	private static final String TAG = "KillBgProcessReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG, "我是自定义的清理进程的广播事件");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			am.killBackgroundProcesses(info.processName);
		}
	}

}
