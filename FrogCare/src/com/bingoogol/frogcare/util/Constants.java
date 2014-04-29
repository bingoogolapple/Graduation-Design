package com.bingoogol.frogcare.util;

import java.io.File;

/**
 * 系统常量工具类
 * 
 * @author bingoogol@sina.com 2014-4-25
 */
public final class Constants {

	public static final String APP_NAME = "FrogCare";

	public static final class what {

	}

	public static final class extra {
		public static final String PACKAGENAME = "packagename";
	}

	public static final class file {
		public static final String NEW_APK_NAME = "NewFrogCare.apk";
		public static final String DIR_ROOT = APP_NAME;
		public static final String DIR_DOWNLOAD = DIR_ROOT + File.separator + "download";
		public static final String DIR_FEEDBACK = DIR_ROOT + File.separator + "feedback";;
	}

	public static final class config {
		public static final String UPGRADE_URL = "http://bingoshare.u.qiniudn.com/FrogCare/upgrade.json";
	}

	public static final class mime {
		public static final String APK = "application/vnd.android.package-archive";
	}

	public static final class net {
		public static final int CONNECT_TIMEOUT = 5000;
		public static final int READ_TIMEOUT = 5000;
	}

	public static final class spkey {
		public static final String AUTO_UPGRADE = "autoupgrade";
		public static final String APPLOCK_PWD = "applockpwd";
		public static final String APPLOCK = "applock";
	}
}