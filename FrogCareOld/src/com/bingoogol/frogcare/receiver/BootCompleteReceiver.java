package com.bingoogol.frogcare.receiver;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bingoogol.frogcare.service.WatchDogService_;
import com.bingoogol.frogcare.util.ISharedPreferences_;
import com.bingoogol.frogcare.util.Logger;

@EReceiver
public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	@Pref
	protected ISharedPreferences_ mSp;

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG, "手机启动完毕");
		if (mSp.appLock().get()) {
			WatchDogService_.intent(context).start();
		}
		// TODO
	}
}