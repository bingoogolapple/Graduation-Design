package com.bingoogol.frogcare.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bingoogol.frogcare.db.DBOpenHelper;
import com.bingoogol.frogcare.domain.BlacklistInfo;

public class BlacklistDao {
	private DBOpenHelper helper;

	/**
	 * 构造方法初始化helper对象
	 * 
	 * @param context
	 */
	public BlacklistDao(Context context) {
		helper = new DBOpenHelper(context);
	}

	/**
	 * 查找黑名单号码
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacklist", null, "number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 查找全部黑名单号码
	 * 
	 * @return
	 */
	public List<BlacklistInfo> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacklist", new String[] { "number", "mode" }, null, null, null, null, null);
		List<BlacklistInfo> infos = new ArrayList<BlacklistInfo>();
		while (cursor.moveToNext()) {
			infos.add(new BlacklistInfo(cursor.getString(0), cursor.getInt(1)));
		}
		cursor.close();
		db.close();
		return infos;
	}

	/**
	 * 分页的查找黑名单号码
	 * 
	 * @param pagenumber
	 *            页码 从第0页开始
	 * @param maxSize
	 *            一页最多少条数据
	 * @return
	 */
	public List<BlacklistInfo> findBlacklistByPage(int pagenumber, int maxSize) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacklist order by _id desc limit ? offset ? ", new String[] { String.valueOf(maxSize), String.valueOf(pagenumber * maxSize) });
		List<BlacklistInfo> infos = new ArrayList<BlacklistInfo>();
		while (cursor.moveToNext()) {
			infos.add(new BlacklistInfo(cursor.getString(0), cursor.getInt(1)));
		}
		cursor.close();
		db.close();
		return infos;
	}

	/**
	 * 获取一共有多少页的内容
	 * 
	 * @param maxSize
	 * @return
	 */
	public int getTotalPageNumber(int maxSize) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacklist ", null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		if (count % maxSize == 0) {
			return count / maxSize;
		} else {
			return count / maxSize + 1;
		}
	}

	/**
	 * 添加一条黑名单号码
	 * 
	 * @param blacklistInfo
	 * 
	 * @return 新添加的条目在数据库的位置
	 */
	public long add(BlacklistInfo blacklistInfo) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", blacklistInfo.getNumber());
		values.put("mode", blacklistInfo.getMode());
		long id = db.insert("blacklist", null, values);
		db.close();
		return id;
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * 
	 * @param number
	 *            黑名单号码
	 * @param newmode
	 *            新的拦截模式
	 * @return 是否更新成功
	 */
	public boolean update(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", newmode);
		int affectraw = db.update("blacklist", values, "number=?", new String[] { number });
		db.close();
		if (affectraw == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除一条黑名单号码
	 * 
	 * @param number
	 * @return
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int affectedrow = db.delete("blacklist", "number=?", new String[] { number });
		db.close();
		if (affectedrow == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 查找黑名单号码的拦截模式
	 * 
	 * @param number
	 * @return 0 代表不是黑名单号码 1 全部拦截 2电话拦截 3短信拦截
	 */
	public int findMode(String number) {
		int result = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacklist", new String[] { "mode" }, "number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}
}
