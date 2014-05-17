package com.bingoogol.frogcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.service.WatchDogService;
import com.bingoogol.frogcare.ui.view.SettingView;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.ServiceStatusUtils;
import com.bingoogol.frogcare.util.SpUtil;

public class SettingActivity extends BaseActivity {
	private static final String TAG = "SettingActivity";
	private SettingView sv_setting_autoupdate;
	private SettingView sv_setting_applock;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting);
		sv_setting_autoupdate = (SettingView) findViewById(R.id.sv_setting_autoupdate);
		sv_setting_applock = (SettingView) findViewById(R.id.sv_setting_applock);
	}

	@Override
	protected void setListener() {
		sv_setting_autoupdate.setOnClickListener(this);
		sv_setting_applock.setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		sv_setting_autoupdate.setChecked(SpUtil.getBoolean(Constants.spkey.AUTO_UPGRADE, false));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ServiceStatusUtils.isServiceRunning(mApp, WatchDogService.class.getName())) {
			SpUtil.putBoolean(Constants.spkey.APPLOCK, true);
			sv_setting_applock.setChecked(true);
			Logger.i(TAG, "开启程序锁");
		} else {
			SpUtil.putBoolean(Constants.spkey.APPLOCK, false);
			sv_setting_applock.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sv_setting_autoupdate:
			if (sv_setting_autoupdate.isChecked()) {
				sv_setting_autoupdate.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.AUTO_UPGRADE, false);
			} else {
				sv_setting_autoupdate.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.AUTO_UPGRADE, true);
			}
			break;
		case R.id.sv_setting_applock:
			if (sv_setting_applock.isChecked()) {
				sv_setting_applock.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.APPLOCK, false);
				stopService(new Intent(this, WatchDogService.class));
			} else {
				sv_setting_applock.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.APPLOCK, true);
				startService(new Intent(this, WatchDogService.class));
			}
			break;
		}
	}

}
