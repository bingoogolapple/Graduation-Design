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
		public static final String DIR_FEEDBACK = DIR_ROOT + File.separator + "feedback";
		public static final String DIR_THEFT = DIR_ROOT + File.separator + "theft";
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
		public static final String SAFE_PHONE_NUMBER = "safephonenumber";
		public static final String ALARM_COMMAND = "alarmcommand";
		public static final String REMOTE_THEFT_COMMAND = "remotetheftcommand";
		public static final String BLACKLIST = "blacklist";
		public static final String ATTRIBUTION = "attribution";
		public static final String LAST_LOCATION = "lastlocation";
		public static final String ATTRIBUTION_LAST_X = "attribution_last_x";
		public static final String ATTRIBUTION_LAST_Y = "attribution_last_y";
		public static final String PHONE_THEFT = "phonetheft";
		public static final String REMOTE_SCREEN_LOCK_PWD = "remoteScreenLockPwd";
	}

	public static final class dbname {
		public static final String ADDRESS = "address.db";
		public static final String COMMONNUM = "commonnum.db";
		public static final String ANTIVIRUS = "antivirus.db";

	}
}