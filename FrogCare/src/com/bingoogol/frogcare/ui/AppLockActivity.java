package com.bingoogol.frogcare.ui;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
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

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.AppLockDao;
import com.bingoogol.frogcare.domain.AppInfo;
import com.bingoogol.frogcare.engine.AppInfoProvider;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.ISharedPreferences_;

@EActivity(R.layout.activity_applock)
public class AppLockActivity extends Activity {

	private static final String TAG = "AppLockActivity";
	@App
	protected FrogCareApplication mApp;
	@Pref
	protected ISharedPreferences_ mSp;

	@ViewById
	protected TextView tv_unlock_tab;
	@ViewById
	protected TextView tv_locked_tab;
	@ViewById
	protected LinearLayout ll_unlock;
	@ViewById
	protected LinearLayout ll_locked;
	@ViewById
	protected TextView tv_unlock_count;
	@ViewById
	protected TextView tv_locked_count;
	@ViewById
	protected ListView lv_unlock;
	@ViewById
	protected ListView lv_locked;

	private AppLockAdapter mUnlockAdapter;
	private AppLockAdapter mLockedAdapter;

	/**
	 * 所有的未加锁的应用程序集合
	 */
	private List<AppInfo> mUnlockAppInfos;

	/**
	 * 所有的已加锁的应用程序集合
	 */
	private List<AppInfo> mLockedAppInfos;

	@Bean
	protected AppLockDao mAppLockDao;

	private CLPDialog mClpDialog;

	@AfterViews
	public void afterViews() {
		mClpDialog = new CLPDialog(this);
		mClpDialog.setCancelable(false);
		mClpDialog.show();
		fillData();
		lv_unlock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appitem_remove_unlock);
				view.startAnimation(an);
				removeUnlock(position);
			}
		});
		lv_locked.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appitem_remove_locked);
				view.startAnimation(an);
				removeLocked(position);
			}
		});
	}

	@UiThread(delay = 300)
	public void removeUnlock(int position) {
		AppInfo appInfo = mUnlockAppInfos.get(position);
		mUnlockAppInfos.remove(appInfo);
		mLockedAppInfos.add(appInfo);
		// 添加到已经加锁的数据库
		mAppLockDao.add(appInfo.getPackname());
		// 通知数据适配器更新,刚才的这个view对象已经不再了.比的条目复用这个view对象
		mLockedAdapter.notifyDataSetChanged();
		mUnlockAdapter.notifyDataSetChanged();
	}

	@UiThread(delay = 300)
	public void removeLocked(int position) {
		AppInfo appInfo = mLockedAppInfos.get(position);
		mLockedAppInfos.remove(appInfo);
		mUnlockAppInfos.add(appInfo);
		// 从已经加锁的数据库删除
		mAppLockDao.delete(appInfo.getPackname());
		// 通知数据适配器更新
		mLockedAdapter.notifyDataSetChanged();
		mUnlockAdapter.notifyDataSetChanged();
	}

	@Background
	public void fillData() {
		List<AppInfo> allAppinfos = AppInfoProvider.getAppInfos(AppLockActivity.this);
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
		afterFillData();
	}

	@UiThread
	public void afterFillData() {
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

	@Click
	public void tv_unlock_tab() {
		tv_unlock_tab.setBackgroundResource(R.drawable.tab_left_pressed);
		tv_locked_tab.setBackgroundResource(R.drawable.tab_right_default);
		ll_unlock.setVisibility(View.VISIBLE);
		ll_locked.setVisibility(View.GONE);
	}

	@Click
	public void tv_locked_tab() {
		tv_locked_tab.setBackgroundResource(R.drawable.tab_right_pressed);
		tv_unlock_tab.setBackgroundResource(R.drawable.tab_left_default);
		ll_locked.setVisibility(View.VISIBLE);
		ll_unlock.setVisibility(View.GONE);
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
				tv_unlock_count.setText("未加锁应用" + mUnlockAppInfos.size() + "个");
				return mUnlockAppInfos.size();

			} else {// 已加锁应用
				tv_locked_count.setText("已加锁应用" + mLockedAppInfos.size() + "个");
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
				view = View.inflate(getApplicationContext(), R.layout.item_applock, null);
				holder = new ViewHolder();
				holder.iv_item_applock_icon = (ImageView) view.findViewById(R.id.iv_item_applock_icon);
				holder.tv_item_applock_name = (TextView) view.findViewById(R.id.tv_item_applock_name);
				holder.iv_item_applock_status = (ImageView) view.findViewById(R.id.iv_item_applock_status);
				view.setTag(holder);
			}
			if (unlockFlag) {// 未加锁应用
				appInfo = mUnlockAppInfos.get(position);
				holder.iv_item_applock_status.setImageResource(R.drawable.lock);
			} else {// 已加锁应用
				appInfo = mLockedAppInfos.get(position);
				holder.iv_item_applock_status.setImageResource(R.drawable.unlock);
			}

			holder.iv_item_applock_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_item_applock_name.setText(appInfo.getAppName());

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_item_applock_icon;
		TextView tv_item_applock_name;
		ImageView iv_item_applock_status;
	}

}