package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.content.Intent;

import com.bingoogol.frogcare.R;

@EActivity(R.layout.activity_tool)
public class ToolActivity extends Activity {

	private static final String TAG = "ToolActivity";

	@Click
	public void itv_tool_applock() {
		startActivity(new Intent(this, AppLockActivity_.class));
	}
}