package com.bingoogol.frogcare.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by bingoogol@sina.com on 14-3-30.
 */
public class AppInfo {
	private Drawable appIcon;
	private String appName;
	private String packname;
	private String version;

	/**
	 * 应用程序可以被安装到不同的位置 , 手机内存 外部存储sd卡
	 */
	private boolean inRom;

	private boolean userApp;

	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
}
