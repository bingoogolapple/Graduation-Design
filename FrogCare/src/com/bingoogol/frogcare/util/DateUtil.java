package com.bingoogol.frogcare.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private DateUtil() {
	}

	public static String Date2String(Date date) {
		return mSdf.format(date);
	}
}
