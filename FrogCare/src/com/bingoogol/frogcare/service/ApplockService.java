package com.bingoogol.frogcare.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.bingoogol.frogcare.db.dao.AppLockDao;
import com.bingoogol.frogcare.ui.ApplockAuthActivity;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;

/**
 * 程序锁服务
 * 
 * @author bingoogol@sina.com
 * 
 */
public class ApplockService extends Service {
	private static final String TAG = "ApplockService";
	private AppLockDao mAppLockDao;
	private boolean mIsWatching;
	private List<String> mTempStopProtectPackageNames;
	private List<String> mProtectPackageNames;

	private ApplockObserver mApplockObserver;
	private ScreenLockReceiver mScreenLockReceiver;
	private ScreenUnLockReceiver mScreenUnLockReceiver;
	private Intent mAuthIntent;
	private ActivityManager mActivityManager;

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTempStopProtectPackageNames = new ArrayList<String>();
		mAppLockDao = new AppLockDao(this);
		mProtectPackageNames = mAppLockDao.findAll();

		mAuthIntent = new Intent(this, ApplockAuthActivity.class);
		// 如果不加这个标记，并且之前该应用的activity任务栈里还有activity，那么在用户完成验证后还会回到该应用当中
		mAuthIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		mScreenLockReceiver = new ScreenLockReceiver();
		registerReceiver(mScreenLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		mScreenUnLockReceiver = new ScreenUnLockReceiver();
		registerReceiver(mScreenUnLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

		mApplockObserver = new ApplockObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.uri, true, mApplockObserver);

		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		startWatchDog();
	}

	@Override
	public void onDestroy() {
		mIsWatching = false;
		unregisterReceiver(mScreenLockReceiver);
		mScreenLockReceiver = null;
		unregisterReceiver(mScreenUnLockReceiver);
		mScreenUnLockReceiver = null;
		getContentResolver().unregisterContentObserver(mApplockObserver);
		mApplockObserver = null;
		super.onDestroy();
	}

	protected void startWatchDog() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mIsWatching = true;
				while (mIsWatching) {
					// 获取最近创建的任务栈
					RunningTaskInfo runningTaskInfo = mActivityManager.getRunningTasks(2).get(0);
					String packageName = runningTaskInfo.topActivity.getPackageName();
					if (mProtectPackageNames.contains(packageName)) {
						// 如果当前应用程序没有处于临时停止保护状态
						if (!mTempStopProtectPackageNames.contains(packageName)) {
							mAuthIntent.putExtra(Constants.extra.PACKAGENAME, packageName);
							startActivity(mAuthIntent);
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private class ScreenLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.i(TAG, "屏幕锁屏");
			mIsWatching = false;
			mTempStopProtectPackageNames.clear();
		}

	}

	private class ScreenUnLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.i(TAG, "屏幕解锁");
			if (!mIsWatching) {
				startWatchDog();
			}
		}

	}

	private class MyBinder extends Binder implements WatchDogCallback {

		@Override
		public void addTempStopProtectPackageName(String packageName) {
			Logger.i(TAG, "停止保护" + packageName);
			mTempStopProtectPackageNames.add(packageName);
		}
	}

	private class ApplockObserver extends ContentObserver {

		public ApplockObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Logger.i(TAG, "加锁应用发生变化");
			mProtectPackageNames = mAppLockDao.findAll();
		}

	}

	public interface WatchDogCallback {
		public void addTempStopProtectPackageName(String packageName);
	}
}