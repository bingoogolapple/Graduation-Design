package com.bingoogol.frogcare.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.receiver.ProcessWidgetReceiver;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.StorageUtil;

/**
 * 进程管理桌面组件更新服务
 * 
 * @author bingoogol@sina.com
 * 
 */
public class ProcessWidgetService extends Service {
	private static final String TAG = "ProcessWidgetService";
	private Timer mTimer;
	private TimerTask mTimerTask;
	private ActivityManager mActivityManager;

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i(TAG, "开启进程管理桌面组件更新服务");
		// 开启一个定时器 定期更新widget
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mTimer = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// 更新widget
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
				ComponentName provider = new ComponentName(getApplicationContext(), ProcessWidgetReceiver.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_process);
				views.setTextViewText(R.id.tv_process_count, getString(R.string.running_process_tips) + mActivityManager.getRunningAppProcesses().size());
				views.setTextViewText(R.id.tv_available_memory, getString(R.string.available_memory_tips) + Formatter.formatFileSize(getApplicationContext(), StorageUtil.getAvailMemSize(getApplicationContext())));
				Intent intent = new Intent();
				intent.setAction("com.bingoogol.killallbgprocess");
				// 定义一个延期的广播事件
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				appWidgetManager.updateAppWidget(provider, views);
			}
		};
		// TODO 锁屏的时候停止该服务，节约用电
		mTimer.schedule(mTimerTask, 1000, 1000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "关闭进程管理桌面组件更新服务");
		mTimer.cancel();
		mTimerTask.cancel();
		mTimer = null;
		mTimerTask = null;
	}
}
