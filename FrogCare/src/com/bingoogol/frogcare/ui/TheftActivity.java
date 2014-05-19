package com.bingoogol.frogcare.ui;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.PromptDialog;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;
import com.bingoogol.frogcare.util.ToastUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class TheftActivity extends BaseActivity {
	private static final String TAG = "TheftActivity";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_theft);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.btn_bind_phone).setOnClickListener(this);
		findViewById(R.id.btn_set_alarm_command).setOnClickListener(this);
		findViewById(R.id.btn_set_remote_theft_command).setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bind_phone:
			showBindPhoneDialog();
			break;
		case R.id.btn_set_alarm_command:
			showSetAlarmCommandDialog();
			break;
		case R.id.btn_set_remote_theft_command:
			showRemoteTheftCommand();
			break;
		}
	}

	private void showRemoteTheftCommand() {
		final PromptDialog pd = new PromptDialog(TheftActivity.this, R.string.set_remote_theft_command, R.string.please_input_remote_theft_command, R.string.ok, R.string.cancel, false);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String alarmCommand = pd.getContent();
				if (TextUtils.isEmpty(alarmCommand)) {
					ToastUtil.makeText(mApp, R.string.remote_theft_command_not_allow_null);
					pd.shake();
				} else {
					pd.dismiss();
					SpUtil.putString(Constants.spkey.REMOTE_THEFT_COMMAND, alarmCommand);
				}
			}

			@Override
			public void onClickLeft() {
			}
		});
		pd.show();
	}

	private void showSetAlarmCommandDialog() {
		final PromptDialog pd = new PromptDialog(TheftActivity.this, R.string.set_alarm_command, R.string.please_input_alarm_command, R.string.ok, R.string.cancel, false);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String alarmCommand = pd.getContent();
				if (TextUtils.isEmpty(alarmCommand)) {
					ToastUtil.makeText(mApp, R.string.alarm_command_not_allow_null);
					pd.shake();
				} else {
					pd.dismiss();
					SpUtil.putString(Constants.spkey.ALARM_COMMAND, alarmCommand);
				}
			}

			@Override
			public void onClickLeft() {
			}
		});
		pd.show();
	}

	private void showBindPhoneDialog() {
		final PromptDialog pd = new PromptDialog(TheftActivity.this, R.string.bind_safe_phone_number, R.string.please_input_safe_number, R.string.ok, R.string.cancel, false);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String safePhoneNumber = pd.getContent();
				if (TextUtils.isEmpty(safePhoneNumber)) {
					Logger.i(TAG, safePhoneNumber);
					ToastUtil.makeText(mApp, R.string.safe_number_not_allow_null);
					pd.shake();
				} else {
					pd.dismiss();
					SpUtil.putString(Constants.spkey.SAFE_PHONE_NUMBER, safePhoneNumber);
				}
			}

			@Override
			public void onClickLeft() {
			}
		});
		pd.show();
	}

}
