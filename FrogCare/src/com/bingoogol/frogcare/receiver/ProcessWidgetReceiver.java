package com.bingoogol.frogcare.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.bingoogol.frogcare.service.ProcessWidgetService;

/**
 * 进程管理桌面组件广播接收者
 * 
 * @author bingoogol@sina.com
 * 
 */
public class ProcessWidgetReceiver extends AppWidgetProvider {
	// 每个方法中单独new或者用静态成员变量
	private static Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 注意: 广播接受者活的时间非常短的 只要onreceive方法执行完毕了.
		// 广播接受者就会被系统回收.
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context) {
		intent = new Intent(context, ProcessWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// Intent intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		// Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
	}
}