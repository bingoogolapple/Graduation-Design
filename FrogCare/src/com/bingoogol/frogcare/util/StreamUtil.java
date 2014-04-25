package com.bingoogol.frogcare.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据流操作工具类
 * 
 * @author bingoogol@sina.com 2014-4-23
 */
public class StreamUtil {
	private static final String TAG = "StreamUtil";

	private StreamUtil() {
	}

	/**
	 * 读取输入流中的字符串
	 * 
	 * @param is
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		return new String(baos.toByteArray(), "UTF-8");
	}

	/**
	 * 关闭流
	 * 
	 * @param stream
	 *            要关闭的数据流
	 * @param errMsg
	 *            关闭流出错时对应的错误信息
	 */
	public static void close(Closeable stream, String errMsg) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Logger.e(TAG, errMsg);
			}
		}
	}
}