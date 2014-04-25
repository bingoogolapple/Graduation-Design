package com.bingoogol.frogcare.ui;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.util.ConnectivityUtil;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;
import com.bingoogol.frogcare.util.StorageUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SplashActivity extends BaseActivity {
	private static final String TAG = "SplashActivity";
	// 新版本apk文件路径
	private String mApkUrl;
	// 新版本名称
	private String mVersionName;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_splash);
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		((TextView) findViewById(R.id.tv_splash_versionName)).setText(mApp.getCurrentVersionName());
		checkVersion();
	}

	private void checkVersion() {
		if (SpUtil.getBoolean(Constants.spkey.AUTO_UPGRADE, false) && ConnectivityUtil.isWifiConnected(mApp) && StorageUtil.isExternalStorageWritable()) {
			new AsyncHttpClient().get(Constants.config.UPGRADE_URL, new JsonHttpResponseHandler("") {
				@Override
				public void onSuccess(JSONObject jsonObject) {
					try {
						if (mApp.getCurrentVersionCode() < jsonObject.getInt("versionCode")) {
							Logger.i(TAG, "显示升级对话框");
							mApkUrl = jsonObject.getString("apkUrl");
							mVersionName = jsonObject.getString("versionName");
							showUpgradDialog();
						} else {
							// 没有新版本，不用升级，进入主界面
							loadMainActivity();
						}
					} catch (JSONException e) {
						// 解析升级信息失败，进入主界面
						Logger.e(TAG, "解析升级信息异常");
						loadMainActivity();
					}
				}
			});
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					loadMainActivity();
				}
			}, 1500);
		}
	}

	/**
	 * 显示升级对话框
	 * 
	 * @param versionName
	 */
	public void showUpgradDialog() {

	}

	/**
	 * 加载应用程序主界面
	 */
	private void loadMainActivity() {
		startActivity(new Intent(mApp, MainActivity.class));
		finish();
	}

	/**
	 * 安装应用
	 * 
	 * @param apkFile
	 *            apk文件
	 */
	private void install(File apkFile) {
		startActivity(mApp.getInstallApkIntent(apkFile));
		// 销毁当前应用
		finish();
	}
}