package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
import android.view.KeyEvent;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.PromptDialog;
import com.bingoogol.frogcare.util.ISharedPreferences_;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	@App
	FrogCareApplication mApp;
	@Pref
	ISharedPreferences_ mSp;
	private long[] mClickMenuHits = new long[3];

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_MENU == keyCode) {
			System.arraycopy(mClickMenuHits, 1, mClickMenuHits, 0, mClickMenuHits.length - 1);
			mClickMenuHits[mClickMenuHits.length - 1] = System.currentTimeMillis();
			if (mClickMenuHits[mClickMenuHits.length - 1] - mClickMenuHits[0] <= 1500) {
				changeAppLockPassword();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void changeAppLockPassword() {
		final PromptDialog pd = new PromptDialog(MainActivity.this, R.string.change_applockpwd, R.string.please_input_applockpwd, R.string.update, R.string.cancel, true);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickOk() {
				String appLockPwd = pd.getContent();
				if (appLockPwd.length() == 0) {
					pd.shake();
				} else {
					pd.dismiss();
					mSp.appLockPwd().put(appLockPwd);
				}
			}

			@Override
			public void onClickCancel() {
			}
		});
		pd.show();
	}
}
