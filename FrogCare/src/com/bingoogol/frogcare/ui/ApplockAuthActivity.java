package com.bingoogol.frogcare.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.service.ApplockService;
import com.bingoogol.frogcare.service.ApplockService.WatchDogCallback;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;
import com.bingoogol.frogcare.util.ToastUtil;

public class ApplockAuthActivity extends BaseActivity {
	private static final String TAG = "ApplockAuthActivity";
	private ImageView iv_applock_auth_icon;
	private TextView tv_applock_auth_name;
	private EditText et_applock_auth_pwd;
	private MyConn mMyConn;
	private WatchDogCallback mWatchDogCallback;
	private String mPackageName;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_applock_auth);
		iv_applock_auth_icon = (ImageView) findViewById(R.id.iv_applock_auth_icon);
		tv_applock_auth_name = (TextView) findViewById(R.id.tv_applock_auth_name);
		et_applock_auth_pwd = (EditText) findViewById(R.id.et_applock_auth_pwd);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.btn_applock_auth_ok).setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mPackageName = getIntent().getStringExtra(Constants.extra.PACKAGENAME);
		try {
			ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(mPackageName, 0);
			tv_applock_auth_name.setText(applicationInfo.loadLabel(getPackageManager()));
			iv_applock_auth_icon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
		} catch (Exception e) {
			Logger.e(TAG, "获取应用信息失败" + e.getMessage());
		}
		mMyConn = new MyConn();
		bindService(new Intent(mApp, ApplockService.class), mMyConn, BIND_AUTO_CREATE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_applock_auth_ok) {
			String password = et_applock_auth_pwd.getText().toString().trim();
			if (SpUtil.getString(Constants.spkey.APPLOCK_PWD, "").equals(password)) {
				finish();
				mWatchDogCallback.addTempStopProtectPackageName(mPackageName);
			} else {
				ToastUtil.makeText(mApp, "密码不正确");
			}
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
