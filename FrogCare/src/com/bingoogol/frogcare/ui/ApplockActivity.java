package com.bingoogol.frogcare.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.AppLockDao;
import com.bingoogol.frogcare.domain.AppInfo;
import com.bingoogol.frogcare.engine.AppInfoProvider;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.Logger;

public class ApplockActivity extends BaseActivity {
	private enum Tab {
		LOCKED, UNLOCK
	}

	private static final String TAG = "ApplockActivity";
	private TextView tv_unlock_tab;
	private TextView tv_locked_tab;
	private LinearLayout ll_unlock;
	private LinearLayout ll_locked;
	private TextView tv_unlock_count;
	private TextView tv_locked_count;
	private ListView lv_unlock;
	private ListView lv_locked;

	private AppLockAdapter mUnlockAdapter;
	private AppLockAdapter mLockedAdapter;

	private Tab mTab = Tab.UNLOCK;

	/**
	 * 所有的未加锁的应用程序集合
	 */
	private List<AppInfo> mUnlockAppInfos;

	/**
	 * 所有的已加锁的应用程序集合
	 */
	private List<AppInfo> mLockedAppInfos;

	private AppLockDao mAppLockDao;

	private CLPDialog mClpDialog;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_applock);
		tv_unlock_tab = (TextView) findViewById(R.id.tv_unlock_tab);
		tv_locked_tab = (TextView) findViewById(R.id.tv_locked_tab);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
		tv_locked_count = (TextView) findViewById(R.id.tv_locked_count);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_locked = (ListView) findViewById(R.id.lv_locked);
	}

	@Override
	protected void setListener() {
		tv_locked_tab.setOnClickListener(this);
		tv_unlock_tab.setOnClickListener(this);
		lv_unlock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Animation an = AnimationUtils.loadAnimation(mApp, R.anim.appitem_remove_unlock);
				view.startAnimation(an);
				removeUnlock(position);
			}
		});
		lv_locked.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Animation an = AnimationUtils.loadAnimation(mApp, R.anim.appitem_remove_locked);
				view.startAnimation(an);
				removeLocked(position);
			}
		});
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mClpDialog = new CLPDialog(this);
		mAppLockDao = new AppLockDao(mApp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_unlock_tab:
			changeToUnlock();
			break;
		case R.id.tv_locked_tab:
			changeToLocked();
			break;
		}
	}

	private void changeToUnlock() {
		tv_unlock_tab.setBackgroundResource(R.color.bg_tab_pressed);
		tv_locked_tab.setBackgroundResource(R.color.bg_tab);
		ll_unlock.setVisibility(View.VISIBLE);
		ll_locked.setVisibility(View.GONE);
		mTab = Tab.UNLOCK;
	}

	private void changeToLocked() {
		tv_locked_tab.setBackgroundResource(R.color.bg_tab_pressed);
		tv_unlock_tab.setBackgroundResource(R.color.bg_tab);
		ll_locked.setVisibility(View.VISIBLE);
		ll_unlock.setVisibility(View.GONE);
		mTab = Tab.LOCKED;
	}

	@Override
	protected void onStart() {
		super.onStart();
		fillData();
		if (mTab == Tab.LOCKED) {
			changeToLocked();
		} else {
			changeToUnlock();
		}
	}

	private void removeUnlock(final int position) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				AppInfo appInfo = mUnlockAppInfos.get(position);
				mUnlockAppInfos.remove(appInfo);
				mLockedAppInfos.add(appInfo);
				// 添加到已经加锁的数据库
				mAppLockDao.add(appInfo.getPackname());
				// 通知数据适配器更新,刚才的这个view对象已经不再了.比的条目复用这个view对象
				mLockedAdapter.notifyDataSetChanged();
				mUnlockAdapter.notifyDataSetChanged();
			}
		}, 300);
	}

	private void removeLocked(final int position) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				AppInfo appInfo = mLockedAppInfos.get(position);
				mLockedAppInfos.remove(appInfo);
				mUnlockAppInfos.add(appInfo);
				// 从已经加锁的数据库删除
				mAppLockDao.delete(appInfo.getPackname());
				// 通知数据适配器更新
				mLockedAdapter.notifyDataSetChanged();
				mUnlockAdapter.notifyDataSetChanged();
			}
		}, 300);
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				mClpDialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				List<AppInfo> allAppinfos = AppInfoProvider.getAppInfos(ApplockActivity.this);
				// 初始化未加锁的程序集合
				mUnlockAppInfos = new ArrayList<AppInfo>();
				// 初始化已加锁程序集合
				mLockedAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : allAppinfos) {
					if (mAppLockDao.find(appInfo.getPackname())) {
						mLockedAppInfos.add(appInfo);
					} else {
						mUnlockAppInfos.add(appInfo);
					}
				}
				Logger.i(TAG, "获取数据完成");
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				mClpDialog.dismiss();
				if (mUnlockAdapter == null) {
					mUnlockAdapter = new AppLockAdapter(true);
				}
				lv_unlock.setAdapter(mUnlockAdapter);
				if (mLockedAdapter == null) {
					mLockedAdapter = new AppLockAdapter(false);
				}
				lv_locked.setAdapter(mLockedAdapter);
			}
		}.execute();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("mTab", mTab);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mTab = (Tab) savedInstanceState.getSerializable("mTab");
	}

	private class AppLockAdapter extends BaseAdapter {
		private boolean unlockFlag;

		/**
		 * 构造方法
		 * 
		 * @param unlockFlag
		 *            true 未加锁的数据适配器 false 已加锁的数据适配器
		 */
		public AppLockAdapter(boolean unlockFlag) {
			this.unlockFlag = unlockFlag;
		}

		@Override
		public int getCount() {
			if (unlockFlag) {// 未加锁应用
				tv_unlock_count.setText(getString(R.string.unlock_count) + mUnlockAppInfos.size());
				return mUnlockAppInfos.size();

			} else {// 已加锁应用
				tv_locked_count.setText(getString(R.string.locked_count) + mLockedAppInfos.size());
				return mLockedAppInfos.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			ViewHolder holder;
			View view;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(mApp, R.layout.item_applock, null);
				holder = new ViewHolder();
				holder.iv_item_applock_icon = (ImageView) view.findViewById(R.id.iv_item_applock_icon);
				holder.tv_item_applock_name = (TextView) view.findViewById(R.id.tv_item_applock_name);
				holder.iv_item_applock_status = (ImageView) view.findViewById(R.id.iv_item_applock_status);
				view.setTag(holder);
			}
			if (unlockFlag) {// 未加锁应用
				appInfo = mUnlockAppInfos.get(position);
				holder.iv_item_applock_status.setImageResource(R.drawable.locked);
			} else {// 已加锁应用
				appInfo = mLockedAppInfos.get(position);
				holder.iv_item_applock_status.setImageResource(R.drawable.unlock);
			}
			holder.iv_item_applock_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_item_applock_name.setText(appInfo.getAppName());
			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv_item_applock_icon;
		TextView tv_item_applock_name;
		ImageView iv_item_applock_status;
	}
}
