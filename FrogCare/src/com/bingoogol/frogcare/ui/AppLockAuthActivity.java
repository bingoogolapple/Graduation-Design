package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.callback.WatchDogCallback;
import com.bingoogol.frogcare.service.WatchDogService_;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.ISharedPreferences_;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.ToastUtil;

@EActivity(R.layout.activity_applock_auth)
public class AppLockAuthActivity extends Activity {
	private static final String TAG = "AppLockAuthActivity";
	@App
	protected FrogCareApplication mApp;
	@Pref
	protected ISharedPreferences_ mSp;
	@ViewById
	protected ImageView iv_applock_auth_icon;
	@ViewById
	protected TextView tv_applock_auth_name;
	@ViewById
	protected EditText et_applock_auth_password;
	private MyConn mMyConn;
	private WatchDogCallback mWatchDogCallback;
	private String mPackageName;

	@AfterViews
	public void afterViews() {
		mPackageName = getIntent().getStringExtra(Constants.extra.PACKAGENAME);
		try {
			ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(mPackageName, 0);
			tv_applock_auth_name.setText(applicationInfo.loadLabel(getPackageManager()));
			iv_applock_auth_icon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
		} catch (Exception e) {
			Logger.e(TAG, "获取应用信息失败" + e.getMessage());
		}
		mMyConn = new MyConn();
		bindService(new Intent(mApp, WatchDogService_.class), mMyConn, BIND_AUTO_CREATE);
	}

	@Click
	public void btn_applock_auth_ok() {
		String password = et_applock_auth_password.getText().toString().trim();
		if (mSp.appLockPwd().get().equals(password)) {
			finish();
			mWatchDogCallback.addTempStopProtectPackageName(mPackageName);
		} else {
			ToastUtil.makeText(mApp, "密码不正确");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mMyConn);
		mMyConn = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 回桌面.
			// <action android:name="android.intent.action.MAIN" />
			// <category android:name="android.intent.category.HOME" />
			// <category android:name="android.intent.category.DEFAULT" />
			// <category android:name="android.intent.category.MONKEY"/>
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addCategory(Intent.CATEGORY_MONKEY);
			startActivity(intent);
			finish();
			// 销毁事件.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mWatchDogCallback = (WatchDogCallback) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	}
}