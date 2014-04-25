package com.bingoogol.frogcare.util;

import java.io.File;

/**
 * 系统常量工具类
 * 
 * @author bingoogol@sina.com 2014-4-25
 */
public final class Constants {
	public static final class what {

	}

	public static final class extra {
		public static final String PACKAGENAME = "packagename";
	}

	public static final class file {
		public static final String DIR_ROOT = "FrogCare";
		public static final String DIR_DOWNLOAD = DIR_ROOT + File.separator + "download";
		public static final String NEW_APK_NAME = "NewFrogCare.apk";
	}

	public static final class config {
		public static final String UPGRADE_URL = "http://bingoshare.u.qiniudn.com/FrogCare/upgrade.json";
	}
}