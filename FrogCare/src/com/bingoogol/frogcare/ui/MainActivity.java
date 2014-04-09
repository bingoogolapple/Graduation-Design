package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;

import android.app.Activity;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	@App
	FrogCareApplication mApp;

	@AfterInject
	public void afterInject() {
		mApp.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.removeActivity(this);
	}
	
	@Override
	public void onBackPressed() {
		mApp.exitWithDoubleClick();
	}

	@AfterViews
	public void afterViews() {

	}
}
