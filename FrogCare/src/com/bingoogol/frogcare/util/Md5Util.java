package com.bingoogol.frogcare.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author bingoogol@sina.com 14-2-12.
 */
public class Md5Util {
	private Md5Util() {
	}

	public static String encode(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(text.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				// 标准md5加密
				int i = b & 0xff;
				// 修改最后一个值来加盐值
				// int i = b & 0xfe;
				String str = Integer.toHexString(i);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// can't reach
			return "";
		}

	}
}
