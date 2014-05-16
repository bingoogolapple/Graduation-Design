package com.bingoogol.frogcare.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.AntiVirusDao;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.ConfirmDialog;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.Md5Util;

public class VirusActivity extends BaseActivity {
	private static final String TAG = "VirusActivity";
	private ImageView iv_scan;
	private TextView tv_scan_info;
	private ProgressBar pb_scan;
	private LinearLayout ll_container;
	private AsyncTask<Void, ScanInfo, Void> task;
	private List<ScanInfo> virusInfos;
	private Animation ta;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		tv_scan_info = (TextView) findViewById(R.id.tv_scan_info);
		pb_scan = (ProgressBar) findViewById(R.id.pb_scan);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		ta = AnimationUtils.loadAnimation(mApp, R.anim.scan_virus);
		startScan();
	}

	private void startScan() {
		task = new AsyncTask<Void, ScanInfo, Void>() {
			/**
			 * 任务执行之前调用 运行在主线程
			 */
			@Override
			protected void onPreExecute() {
				// 开启扫描动画
				iv_scan.startAnimation(ta);
				tv_scan_info.setText(R.string.init_ing_virus);
				virusInfos = new ArrayList<ScanInfo>();
			}

			/**
			 * 任务执行后调用 运行在主线程
			 */
			@Override
			protected void onPostExecute(Void result) {
				tv_scan_info.setText(R.string.scan_end);
				ta.cancel();
				iv_scan.clearAnimation();
				if (virusInfos.size() > 0) {
					// 发现病毒 显示对话框
					Logger.i(TAG, "发现病毒");
					final ConfirmDialog confirmDialog = new ConfirmDialog(VirusActivity.this, R.string.kill_virus_tip, getString(R.string.virus_number_tips) + virusInfos.size() + getString(R.string.is_kill_virus), R.string.cancel, R.string.kill);
					confirmDialog.setBtnCallback(new BtnCallback() {
						@Override
						public void onClickRight() {
							for (ScanInfo info : virusInfos) {
								String packname = info.packName;
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_DELETE);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:" + packname));
								startActivity(intent);
							}
						}

						@Override
						public void onClickLeft() {
						}
					});
					confirmDialog.show();
				}
			}

			/**
			 * 执行的进度 ui线程
			 */
			@Override
			protected void onProgressUpdate(ScanInfo... values) {
				ScanInfo scanInfo = values[0];
				tv_scan_info.setText(scanInfo.appName);
				TextView tv = new TextView(mApp);
				if (scanInfo.isVirus) {
					tv.setTextColor(Color.RED);
					tv.setText(getString(R.string.discover_virus) + scanInfo.appName);
					virusInfos.add(scanInfo);
				} else {
					tv.setTextColor(Color.WHITE);
					tv.setText(getString(R.string.scan_safety) + scanInfo.appName);
				}
				ll_container.addView(tv, 0);
			}

			/**
			 * 在后台执行任务 运行在子线程
			 */
			@Override
			protected Void doInBackground(Void... params) {
				List<PackageInfo> packinfos = getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
				pb_scan.setMax(packinfos.size());
				int current = 0;
				for (PackageInfo info : packinfos) {
					String md5 = Md5Util.encode(info.signatures[0].toCharsString());
					String result = AntiVirusDao.findVirus(mApp, md5);
					ScanInfo scanInfo = new ScanInfo();
					if (TextUtils.isEmpty(result)) {
						// 安全
						scanInfo.isVirus = false;
					} else {
						// 病毒
						scanInfo.isVirus = true;
					}
					scanInfo.appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
					scanInfo.packName = info.packageName;
					publishProgress(scanInfo);
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					current++;
					pb_scan.setProgress(current);
				}
				return null;
			}
		}.execute();

	}

	@Override
	public void onClick(View v) {
	}

	@Override
	protected void onDestroy() {
		task.cancel(true);
		super.onDestroy();
	}

	private class ScanInfo {
		String appName;
		String packName;
		boolean isVirus;
	}

}
