package com.bingoogol.frogcare.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bingoogol.frogcare.util.Constants;

public class AddressDao {
	/**
	 * 查询手机号码归属地信息
	 * 
	 * @param context
	 * @param number
	 * @return
	 */
	public static String getAddress(Context context, String number) {
		// 正则表达式过滤手机号码
		// ^1[3458]\d{9}$
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getPath() + File.separator + Constants.dbname.ADDRESS, null, SQLiteDatabase.OPEN_READONLY);
		if (number.matches("^1[3458]\\d{9}$")) {
			// 手机号码
			Cursor cursor = db.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			// 其他号码 110 119 999 120 83559967 02012345678
			// 020 0201
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;

			default:
				if (number.length() >= 10 && number.startsWith("0")) {
					String prefix3 = number.substring(1, 3);
					String prefix4 = number.substring(1, 4);
					Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[] { prefix3 });
					if (cursor.moveToNext()) {

						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
					cursor = db.rawQuery("select location from data2 where area = ?", new String[] { prefix4 });
					if (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
				}
				break;
			}
		}
		db.close();
		return address;
	}

}