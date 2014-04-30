package com.bingoogol.frogcare.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.bingoogol.frogcare.domain.AppInfo;

/**
 * Created by bingoogol@sina.com on 14-3-30.
 */
public class AppInfoProvider {

	/**
	 * 获取手机里面所有的安装的应用程序信息
	 * 
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		for (PackageInfo packInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);
			appInfo.setAppIcon(appIcon);

			int flags = packInfo.applicationInfo.flags;

			int uid = packInfo.applicationInfo.uid;
			appInfo.setUid(uid);

			if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				appInfo.setUserApp(false);// 系统应用
			} else {
				appInfo.setUserApp(true);// 用户应用
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				appInfo.setInRom(false);
			} else {
				appInfo.setInRom(true);
			}

			String appName = packInfo.applicationInfo.loadLabel(pm).toString();
			appInfo.setAppName(appName);
			String packname = packInfo.packageName;
			appInfo.setPackname(packname);
			String version = packInfo.versionName;
			appInfo.setVersion(version);
			appinfos.add(appInfo);
		}
		return appinfos;
	}
}
