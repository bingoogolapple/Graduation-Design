package com.bingoogol.frogcare.db.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bingoogol.frogcare.util.Constants;

public class CommonNumberDao {
	private static String getDbFilePath(Context context) {
		return context.getFilesDir().getPath() + File.separator + Constants.dbname.COMMONNUM;
	}

	/**
	 * 返回一共有多少个分组信息
	 * 
	 * @param context
	 * @return
	 */
	public static int getGroupCount(Context context) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select count(*) from classlist", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 返回某个分组里面有多少个孩子
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static int getChildrenCountByPosition(Context context, int groupPosition) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		String sql = "select count(*) from table" + newPosition;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 获取某个位置的分组的名称
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static String getGroupNameByPosition(Context context, int groupPosition) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		String sql = "select name from classlist where idx = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { newPosition + "" });
		cursor.moveToNext();
		String name = cursor.getString(0);
		cursor.close();
		db.close();
		return name;
	}

	/**
	 * 获取所有的分组名称
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getGroupNames(Context context) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select name from classlist";
		Cursor cursor = db.rawQuery(sql, null);
		List<String> groupNames = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			groupNames.add(name);
		}
		cursor.close();
		db.close();
		return groupNames;
	}

	/**
	 * 获取某个位置的分组里面的某个位置的孩子的名称
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static String getChildNameByPosition(Context context, int groupPosition, int childPosition) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		int newChildPositions = childPosition + 1;
		String sql = "select name,number from table" + newPosition + " where _id = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { newChildPositions + "" });
		cursor.moveToNext();
		String name = cursor.getString(0);
		String number = cursor.getString(1);
		cursor.close();
		db.close();
		return name + "\n" + number;

	}

	/**
	 * 获取某个位置的分组里面所有的孩子名称
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static List<String> getChildrenNamesByPosition(Context context, int groupPosition) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(getDbFilePath(context), null, SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		String sql = "select name,number from table" + newPosition;
		Cursor cursor = db.rawQuery(sql, null);
		List<String> childrenNames = new ArrayList<String>();

		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String number = cursor.getString(1);
			childrenNames.add(name + "\n" + number);
		}
		cursor.close();
		db.close();
		return childrenNames;

	}
}
