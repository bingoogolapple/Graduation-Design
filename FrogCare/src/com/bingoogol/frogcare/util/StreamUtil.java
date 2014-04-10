package com.bingoogol.frogcare.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
	private StreamUtil() {
	}

	public static String InputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		return new String(baos.toByteArray(), "UTF-8");
	}
}