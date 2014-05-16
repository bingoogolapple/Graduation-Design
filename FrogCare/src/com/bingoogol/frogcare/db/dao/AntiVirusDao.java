package com.bingoogol.frogcare.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bingoogol.frogcare.util.Constants;

public class AntiVirusDao {

	/**
	 * 查询一条md5信息是否是病毒
	 * 
	 * @param context
	 * @param md5
	 * @return null代表安全 result病毒的描述信息
	 */
	public static String findVirus(Context context, String md5) {
		String reuslt = null;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getPath() + File.separator + Constants.dbname.ANTIVIRUS, null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select desc from datable where md5=?";
		Cursor cursor = db.rawQuery(sql, new String[] { md5 });
		if (cursor.moveToNext()) {
			reuslt = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return reuslt;
	}
}
