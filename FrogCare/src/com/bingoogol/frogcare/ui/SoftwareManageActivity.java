package com.bingoogol.frogcare.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.domain.AppInfo;
import com.bingoogol.frogcare.engine.AppInfoProvider;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.DensityUtil;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.ToastUtil;

public class SoftwareManageActivity extends BaseActivity {
	private static final String TAG = "SoftwareManageActivity";

	private TextView tv_available_storage;
	private ListView lv_software;
	private TextView tv_status;

	private CLPDialog mClpDialog;

	private AppInfo mAppInfo;

	/**
	 * 手机上用户程序的列表
	 */
	private List<AppInfo> mUserAppInfos;
	/**
	 * 手机上系统程序的列表
	 */
	private List<AppInfo> mSystemAppInfos;
	private AppAdapter mAppAdapter;

	private PopupWindow mPopupWindow;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_software_manage);
		tv_available_storage = (TextView) findViewById(R.id.tv_available_storage);
		lv_software = (ListView) findViewById(R.id.lv_software);
		tv_status = (TextView) findViewById(R.id.tv_status);
	}

	@Override
	protected void setListener() {
		lv_software.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (mUserAppInfos != null && mSystemAppInfos != null) {
					if (firstVisibleItem >= (mUserAppInfos.size() + 1)) {
						tv_status.setText(getString(R.string.system_software_tips) + mSystemAppInfos.size());
					} else {
						tv_status.setText(getString(R.string.user_software_tips) + mUserAppInfos.size());
					}
				}
			}
		});
		lv_software.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissPopupWindow();
				// 把点击的条目 赋值给类的成员变量
				mAppInfo = (AppInfo) lv_software.getItemAtPosition(position);
				Logger.i(TAG, "被点击的条目包名:" + mAppInfo.getPackname());
				// popupwindow 类似于对话框 轻量级的activity 重量级的对话框
				View contentView = View.inflate(mApp, R.layout.view_software_popupwindow, null);
				contentView.findViewById(R.id.btn_software_run).setOnClickListener(SoftwareManageActivity.this);
				contentView.findViewById(R.id.btn_software_uninstall).setOnClickListener(SoftwareManageActivity.this);
				contentView.findViewById(R.id.btn_software_share).setOnClickListener(SoftwareManageActivity.this);

				mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				// 如果想让在点击别的地方的时候 关闭掉弹出窗体 一定要记得给mPopupWindow设置一个背景资源
				mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

				int[] location = new int[2];
				view.getLocationInWindow(location);
				mPopupWindow.showAtLocation(parent, Gravity.TOP + Gravity.LEFT, location[0] + DensityUtil.dip2px(mApp, 60), location[1]);

				ScaleAnimation sa = new ScaleAnimation(0.5f, 1.1f, 0.5f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(500);

				contentView.startAnimation(sa);
			}
		});
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mClpDialog = new CLPDialog(this);
	}

	@Override
	public void onClick(View view) {
		dismissPopupWindow();
		switch (view.getId()) {
		case R.id.btn_software_run:
			startApplication();
			break;
		case R.id.btn_software_uninstall:
			uninstallApplication();
			break;
		case R.id.btn_software_share:
			shareApplication();
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		tv_available_storage.setText(getString(R.string.available_sd_tips) + getAvailSD() + "    " + getString(R.string.available_rom_tips) + getAvailROM());
		fillData();
	}

	private void dismissPopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	/**
	 * 获取手机内部存储空间
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getAvailROM() {
		StatFs statfs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
		int blocks = statfs.getAvailableBlocks();
		int size = statfs.getBlockSize();
		long total = blocks * size;
		return Formatter.formatFileSize(this, total);
	}

	/**
	 * 获取手机外部存储空间
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getAvailSD() {
		StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		int blocks = statfs.getAvailableBlocks();
		int size = statfs.getBlockSize();
		long total = blocks * size;
		return Formatter.formatFileSize(this, total);
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				mClpDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<AppInfo> appInfos = AppInfoProvider.getAppInfos(mApp);
				mUserAppInfos = new ArrayList<AppInfo>();
				mSystemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						mUserAppInfos.add(info);
					} else {
						mSystemAppInfos.add(info);
					}
				}
				Logger.i(TAG, "获取数据完成");
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mClpDialog.dismiss();
				if (mAppAdapter == null) {
					mAppAdapter = new AppAdapter();
				}
				lv_software.setAdapter(mAppAdapter);
			}
		}.execute();
	}

	/**
	 * 卸载一个应用程序
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DELETE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + mAppInfo.getPackname()));
		startActivity(intent);
	}

	/**
	 * 开启一个应用,实质上就是开启这个应用的第一个activity
	 */
	private void startApplication() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(mAppInfo.getPackname(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = info.activities;
			if (activityInfos != null && activityInfos.length > 0) {
				ActivityInfo activityInfo = activityInfos[0];
				// 代表的就是当前应用程序入口的activity
				String className = activityInfo.name;
				Intent intent = new Intent();
				intent.setClassName(mAppInfo.getPackname(), className);
				startActivity(intent);
			} else {
				ToastUtil.makeText(this, R.string.can_not_run_app);
			}
		} catch (Exception e) {
			ToastUtil.makeText(this, R.string.can_not_find_app);
		}

	}

	/**
	 * 分享一个应用程序
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_tips) + mAppInfo.getAppName() + getString(R.string.downloadurl_tips) + mAppInfo.getPackname());
		startActivity(intent);
	}

	private class AppAdapter extends BaseAdapter {

		/**
		 * 禁用掉 两个textview的点击事件.
		 */
		@Override
		public boolean isEnabled(int position) {
			if (position == 0) {
				// 第0个位置的条目. 显示一个textview
				return false;
			} else if (position == (mUserAppInfos.size() + 1)) {
				// 第2个textview 显示
				return false;
			}
			return true;
		}

		/**
		 * 返回listview里面有多少个条目,多出来两个条目 一个显示用户程序的个数 一个显示系统程序的个数
		 */
		@Override
		public int getCount() {
			return mUserAppInfos.size() + mSystemAppInfos.size() + 2;
		}

		/**
		 * 返回某个位置绑定的数据
		 */
		@Override
		public Object getItem(int position) {
			AppInfo appInfo = null;
			if (position == 0) {
				return null;
			} else if (position == (mUserAppInfos.size() + 1)) {
				return null;
			} else if (position <= mUserAppInfos.size()) {
				// 用户程序
				int newposition = position - 1;
				// 最上面有一个textview 把空间占据一个
				appInfo = mUserAppInfos.get(newposition);
			} else {
				// 剩下来只可能是系统程序
				// 分别减去两个textview
				int newposition = position - mUserAppInfos.size() - 2;
				appInfo = mSystemAppInfos.get(newposition);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo = null;
			View view = null;
			ViewHolder holder;
			if (position == 0) {// 第0个位置的条目. 显示一个textview
				TextView tv = (TextView) View.inflate(mApp, R.layout.view_list_title, null);
				tv.setText(getString(R.string.user_software_tips) + mUserAppInfos.size());
				return tv;
			} else if (position == (mUserAppInfos.size() + 1)) {
				// 第2个textview 显示
				// 有多少个系统程序
				TextView tv = (TextView) View.inflate(mApp, R.layout.view_list_title, null);
				tv.setText(getString(R.string.system_software_tips) + mSystemAppInfos.size());
				return tv;
			} else if (position <= mUserAppInfos.size()) {
				// 用户程序
				// 最上面有一个textview 把空间占据一个
				int newposition = position - 1;
				appInfo = mUserAppInfos.get(newposition);
			} else {
				// 剩下来只可能是系统程序
				// 分别减去两个textview和用户集合的个数
				int newposition = position - mUserAppInfos.size() - 2;
				appInfo = mSystemAppInfos.get(newposition);
			}

			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(mApp, R.layout.item_software, null);
				holder = new ViewHolder();
				holder.iv_software_icon = (ImageView) view.findViewById(R.id.iv_software_icon);
				holder.tv_software_name = (TextView) view.findViewById(R.id.tv_software_name);
				holder.tv_software_version = (TextView) view.findViewById(R.id.tv_software_version);
				holder.tv_software_location = (TextView) view.findViewById(R.id.tv_software_location);
				view.setTag(holder);
			}

			holder.iv_software_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_software_name.setText(appInfo.getAppName());
			holder.tv_software_version.setText(appInfo.getVersion());
			if (appInfo.isInRom()) {
				holder.tv_software_location.setText(R.string.mobile_storage);
			} else {
				holder.tv_software_location.setText(R.string.external_storage);
			}
			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv_software_icon;
		TextView tv_software_name;
		TextView tv_software_location;
		TextView tv_software_version;
	}

}
