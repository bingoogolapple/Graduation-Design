package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.SettingView;
import com.bingoogol.frogcare.util.ISharedPreferences_;

@EActivity(R.layout.activity_setting)
public class SettingActivity extends Activity {

	private static final String TAG = "SettingActivity";
	@App
	protected FrogCareApplication mApp;
	@Pref
	protected ISharedPreferences_ mSp;

	@ViewById
	protected SettingView sv_setting_autoupdate;
	@ViewById
	protected SettingView sv_setting_applock;

	@AfterViews
	public void afterViews() {
		sv_setting_autoupdate.setChecked(mSp.autoUpdate().get());
		sv_setting_applock.setChecked(mSp.appLock().get());
	}

	@Click
	public void sv_setting_autoupdate() {
		if (sv_setting_autoupdate.isChecked()) {
			sv_setting_autoupdate.setChecked(false);
			mSp.autoUpdate().put(false);
		} else {
			sv_setting_autoupdate.setChecked(true);
			mSp.autoUpdate().put(true);
		}
	}

	public void sv_setting_applock() {
		if (sv_setting_applock.isChecked()) {
			sv_setting_applock.setChecked(false);
			mSp.appLock().put(false);
			// TODO
		} else {
			sv_setting_applock.setChecked(true);
			mSp.appLock().put(true);
			// TODO
		}
	}

}