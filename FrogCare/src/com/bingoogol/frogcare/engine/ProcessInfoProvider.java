package com.bingoogol.frogcare.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bingoogol.frogcare.domain.ProcessInfo;

public class ProcessInfoProvider {
	/**
	 * 获取正在运行的进程信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<ProcessInfo> getProcessInfos(Context context) {
		List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			ProcessInfo processInfo = new ProcessInfo();
			String packname = info.processName;
			processInfo.setPackname(packname);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					processInfo.setUserProcess(false);
				} else {
					processInfo.setUserProcess(true);
				}
				processInfo.setAppName(applicationInfo.loadLabel(pm).toString());
				processInfo.setIcon(applicationInfo.loadIcon(pm));
			} catch (PackageManager.NameNotFoundException e) {
				// 当前应用程序不是标准的apk
				processInfo.setAppName(packname);
				processInfo.setIcon(context.getResources().getDrawable(android.R.drawable.star_on));
			}
			long memsize = am.getProcessMemoryInfo(new int[] { info.pid })[0].getTotalPrivateDirty() * 1024;
			processInfo.setMemsize(memsize);
			processInfos.add(processInfo);
		}

		return processInfos;
	}
}
