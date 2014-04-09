package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.util.ConnectivityUtil;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.ISharedPreferences_;
import com.bingoogol.frogcare.util.StorageUtil;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {

	@App
	FrogCareApplication mApp;
	@Pref
	ISharedPreferences_ mSp;

	@HttpsClient
	HttpClient mHttpClient;

	@ViewById(R.id.tv_splash_version)
	TextView tv_splash_version;
	private String mVersion;

	@AfterInject
	public void afterInject() {
		// 在onCreate之前执行
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
		// 在onCreate之后，onStart之前执行
		// 设置当前版本号
		mVersion = getCurrentVersion();
		tv_splash_version.setText(getString(R.string.version_tips) + mVersion);
		// 检查版本更新
		checkVersion();

		Animation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(1000);
		findViewById(R.id.ll_splash_root).startAnimation(animation);
	}

	@Background
	public void checkVersion() {
		// 如果配置了自动更新，并且已连接wifi，SD卡可写状态，则检查新版本，否则延迟1500毫秒加载主界面
		if (mSp.autoUpdate().get() && ConnectivityUtil.isWifiConnected(mApp) && StorageUtil.isExternalStorageWritable()) {
			try {
//				HttpGet httpGet = new HttpGet(Constants.config.UPGRADE_URL);
//				HttpResponse response = mHttpClient.execute(httpGet);
			} catch (Exception e) {
				
			}

		} else {
			loadMainUiDelay();
		}
	}

	public String getCurrentVersion() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			// 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
			return null;
		}
	}

	@UiThread(delay = 1500)
	public void loadMainUiDelay() {
		startActivity(new Intent(this, MainActivity_.class));
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		finish();
	}

}
