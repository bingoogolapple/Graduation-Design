package com.bingoogol.frogcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.service.BlacklistInterceptService;
import com.bingoogol.frogcare.service.AttributionService;
import com.bingoogol.frogcare.service.ApplockService;
import com.bingoogol.frogcare.ui.view.SettingView;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.ServiceStatusUtils;
import com.bingoogol.frogcare.util.SpUtil;

public class SettingActivity extends BaseActivity {
	private static final String TAG = "SettingActivity";
	private SettingView sv_setting_autoupdate;
	private SettingView sv_setting_applock;
	private SettingView sv_setting_attribution;
	private SettingView sv_setting_blacklist;
	private SettingView sv_setting_phone_theft;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting);
		sv_setting_autoupdate = (SettingView) findViewById(R.id.sv_setting_autoupdate);
		sv_setting_applock = (SettingView) findViewById(R.id.sv_setting_applock);
		sv_setting_attribution = (SettingView) findViewById(R.id.sv_setting_attribution);
		sv_setting_blacklist = (SettingView) findViewById(R.id.sv_setting_blacklist);
		sv_setting_phone_theft = (SettingView) findViewById(R.id.sv_setting_phone_theft);
	}

	@Override
	protected void setListener() {
		sv_setting_autoupdate.setOnClickListener(this);
		sv_setting_applock.setOnClickListener(this);
		sv_setting_attribution.setOnClickListener(this);
		sv_setting_blacklist.setOnClickListener(this);
		sv_setting_phone_theft.setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		sv_setting_autoupdate.setChecked(SpUtil.getBoolean(Constants.spkey.AUTO_UPGRADE, false));
		sv_setting_phone_theft.setChecked(SpUtil.getBoolean(Constants.spkey.PHONE_THEFT, false));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ServiceStatusUtils.isServiceRunning(mApp, ApplockService.class.getName())) {
			SpUtil.putBoolean(Constants.spkey.APPLOCK, true);
			sv_setting_applock.setChecked(true);
			Logger.i(TAG, "开启程序锁");
		} else {
			SpUtil.putBoolean(Constants.spkey.APPLOCK, false);
			sv_setting_applock.setChecked(false);
		}
		if (ServiceStatusUtils.isServiceRunning(mApp, AttributionService.class.getName())) {
			sv_setting_attribution.setChecked(true);
		} else {
			sv_setting_attribution.setChecked(false);
		}
		if (ServiceStatusUtils.isServiceRunning(mApp, BlacklistInterceptService.class.getName())) {
			sv_setting_blacklist.setChecked(true);
		} else {
			sv_setting_blacklist.setChecked(false);
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
				stopService(new Intent(this, ApplockService.class));
			} else {
				sv_setting_applock.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.APPLOCK, true);
				startService(new Intent(this, ApplockService.class));
			}
			break;
		case R.id.sv_setting_attribution:
			if (sv_setting_attribution.isChecked()) {
				sv_setting_attribution.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.ATTRIBUTION, false);
				stopService(new Intent(this, AttributionService.class));
			} else {
				sv_setting_attribution.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.ATTRIBUTION, true);
				startService(new Intent(this, AttributionService.class));
			}
			break;
		case R.id.sv_setting_blacklist:
			if (sv_setting_blacklist.isChecked()) {
				sv_setting_blacklist.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.BLACKLIST, false);
				stopService(new Intent(this, BlacklistInterceptService.class));
			} else {
				sv_setting_blacklist.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.BLACKLIST, true);
				startService(new Intent(this, BlacklistInterceptService.class));
			}
			break;
		case R.id.sv_setting_phone_theft:
			if (sv_setting_phone_theft.isChecked()) {
				sv_setting_phone_theft.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.PHONE_THEFT, false);
			} else {
				sv_setting_phone_theft.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.PHONE_THEFT, true);
			}
			break;
		}
	}

}
