package com.bingoogol.frogcare.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.ConfirmDialog;
import com.bingoogol.frogcare.ui.view.ProgressDialog;
import com.bingoogol.frogcare.util.ConnectivityUtil;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.ISharedPreferences_;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.StorageUtil;
import com.bingoogol.frogcare.util.StreamUtil;
import com.bingoogol.frogcare.util.ToastUtil;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	@App
	protected FrogCareApplication mApp;
	@Pref
	protected ISharedPreferences_ mSp;

	@HttpsClient
	protected HttpClient mHttpClient;

	private String mApkurl;

	private ProgressDialog mProgressDialog;

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
		// 检查版本更新
		checkVersion();

		Animation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(500);
		findViewById(R.id.ll_splash_root).startAnimation(animation);
	}

	@Background
	public void checkVersion() {
		// 如果配置了自动更新，并且已连接wifi，SD卡可写状态，则检查新版本，否则延迟1500毫秒加载主界面
		if (mSp.autoUpdate().get() && ConnectivityUtil.isWifiConnected(mApp) && StorageUtil.isExternalStorageWritable()) {
			try {
				HttpResponse response = mHttpClient.execute(new HttpGet(Constants.config.UPGRADE_URL));
				String upgradeJson = StreamUtil.inputStream2String(response.getEntity().getContent());
				JSONObject jsonObject = new JSONObject(upgradeJson);
				if (getCurrentVersionCode() < jsonObject.getInt("versionCode")) {
					mApkurl = jsonObject.getString("apkurl");
					showUpgradDialog(jsonObject.getString("versionName"));
				} else {
					loadMainUiDelay();
				}
			} catch (Exception e) {
				loadMainUiDelay();
				Logger.e(TAG, "自动升级出错：" + e.getMessage());
			}

		} else {
			loadMainUiDelay();
		}
	}

	@UiThread
	public void showUpgradDialog(String versionName) {
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.current_version_tips) + getCurrentVersionName() + "\n");
		sb.append(getString(R.string.new_version_tips) + versionName + "\n\n");
		sb.append(getString(R.string.whether_upgrade_tips));
		final ConfirmDialog confirmDialog = new ConfirmDialog(SplashActivity.this, R.string.find_new_version, sb.toString(), R.string.upgrade_now, R.string.upgrade_later);
		confirmDialog.setBtnCallback(new BtnCallback() {
			@Override
			public void onClickOk() {
				mProgressDialog = new ProgressDialog(SplashActivity.this);
				mProgressDialog.show();
				upgrade();
			}

			@Override
			public void onClickCancel() {
				loadMainUi();
			}
		});
		confirmDialog.show();
	}

	@Background
	public void upgrade() {
		FileOutputStream fos = null;
		boolean isSuccess = false;
		File apkFile = new File(StorageUtil.getDownloadDir(), Constants.file.NEW_APK_NAME);
		apkFile.deleteOnExit();
		try {
			fos = new FileOutputStream(apkFile);
			HttpResponse response = mHttpClient.execute(new HttpGet(mApkurl));
			// 进度条比较特殊，可以在子线程里更新数据
			int contentLength = (int) response.getEntity().getContentLength();
			if (StorageUtil.isSaveable(mApp, contentLength)) {
				mProgressDialog.setMax(contentLength);
				InputStream is = response.getEntity().getContent();
				byte[] buffer = new byte[1024];
				int current = 0;
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					current += len;
					mProgressDialog.setProgress(current);
				}
				isSuccess = true;
			} else {
				showToast(R.string.sdcard_not_enough);
			}
		} catch (Exception e) {
			Logger.e(TAG, "下载apk文件出错：" + e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Logger.e(TAG, "下载apk时关闭文件输出流出错:" + e.getMessage());
				}
			}
			// 不管成功还是失败，都得关闭进度对话框
			closeProgressDialog();
			if (isSuccess) {
				install(apkFile);
			} else {
				loadMainUi();
			}
		}
	}

	@UiThread
	public void closeProgressDialog() {
		mProgressDialog.dismiss();
	}

	@UiThread
	public void showToast(int resId) {
		ToastUtil.makeText(mApp, resId);
	}

	public String getCurrentVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			// 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
			return null;
		}
	}

	public int getCurrentVersionCode() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (Exception e) {
			// 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
			return 0;
		}
	}

	@UiThread(delay = 1000)
	public void loadMainUiDelay() {
		loadMainUi();
	}

	@UiThread
	public void loadMainUi() {
		startActivity(new Intent(this, MainActivity_.class));
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		finish();
	}

	/**
	 * 安装应用
	 * 
	 * @param apkFile
	 *            apk文件
	 */
	private void install(File apkFile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

}
