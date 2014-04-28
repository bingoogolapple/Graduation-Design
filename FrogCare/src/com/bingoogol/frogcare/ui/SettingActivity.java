package com.bingoogol.frogcare.ui;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.SettingView;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.SpUtil;

import android.os.Bundle;
import android.view.View;

public class SettingActivity extends BaseActivity {
	private static final String TAG = "SettingActivity";
	private SettingView sv_setting_autoupdate;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting);
		sv_setting_autoupdate = (SettingView) findViewById(R.id.sv_setting_autoupdate);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.btn_setting_back).setOnClickListener(this);
		sv_setting_autoupdate.setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		sv_setting_autoupdate.setChecked(SpUtil.getBoolean(Constants.spkey.AUTO_UPGRADE, false));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setting_back:
			finish();
			break;
		case R.id.sv_setting_autoupdate:
			if (sv_setting_autoupdate.isChecked()) {
				sv_setting_autoupdate.setChecked(false);
				SpUtil.putBoolean(Constants.spkey.AUTO_UPGRADE, false);
			} else {
				sv_setting_autoupdate.setChecked(true);
				SpUtil.putBoolean(Constants.spkey.AUTO_UPGRADE, true);
			}
			break;
		}
	}

}
