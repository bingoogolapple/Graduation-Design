package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.util.ISharedPreferences_;

@EActivity(R.layout.activity_setting)
public class SettingActivity extends Activity {

	private static final String TAG = "SettingActivity";
	@App
	FrogCareApplication mApp;
	@Pref
	ISharedPreferences_ mSp;

}