package com.bingoogol.frogcare.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.domain.ProcessInfo;
import com.bingoogol.frogcare.engine.ProcessInfoProvider;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.StorageUtil;
import com.bingoogol.frogcare.util.ToastUtil;

public class ProcessManageActivity extends BaseActivity {
	private static final String TAG = "ProcessManageActivity";

	private TextView tv_process_storage;
	private ListView lv_process;
	private TextView tv_status;

	private CLPDialog mClpDialog;

	private ProcessAdapter mProcessAdapter;

	private long mTotalMem;
	private long mAvailMem;
	private long mRunningProcessCount;

	private List<ProcessInfo> mUserProcessInfos;

	private List<ProcessInfo> mSystemProcessInfos;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_process_manage);
		tv_process_storage = (TextView) findViewById(R.id.tv_process_storage);
		lv_process = (ListView) findViewById(R.id.lv_process);
		tv_status = (TextView) findViewById(R.id.tv_status);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.btn_select_all).setOnClickListener(this);
		findViewById(R.id.btn_select_inverse).setOnClickListener(this);
		findViewById(R.id.btn_onekey_clean).setOnClickListener(this);

		lv_process.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mUserProcessInfos != null && mSystemProcessInfos != null) {
					if (firstVisibleItem >= (mUserProcessInfos.size() + 1)) {
						tv_status.setText(getString(R.string.system_process_tips) + mSystemProcessInfos.size());
					} else {
						tv_status.setText(getString(R.string.user_process_tips) + mUserProcessInfos.size());
					}
				}
			}
		});
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击的条目所对应的数据.
				ProcessInfo processInfo = (ProcessInfo) lv_process.getItemAtPosition(position);
				if (processInfo.getPackname().equals(getPackageName())) {
					return;
				}
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_process);
				if (processInfo.isChecked()) {
					processInfo.setChecked(false);
					cb.setChecked(false);
				} else {
					processInfo.setChecked(true);
					cb.setChecked(true);
				}
			}
		});
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mClpDialog = new CLPDialog(this);
		mTotalMem = getTotalMemSize();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_all:
			selectAll();
			break;
		case R.id.btn_select_inverse:
			inverseSelect();
			break;
		case R.id.btn_onekey_clean:
			onekeyClean();
			break;
		}
	}

	private void selectAll() {
		for (ProcessInfo info : mUserProcessInfos) {
			if (info.getPackname().equals(getPackageName())) {
				info.setChecked(false);
			} else {
				info.setChecked(true);
			}
		}
		for (ProcessInfo info : mSystemProcessInfos) {
			info.setChecked(true);
		}
		mProcessAdapter.notifyDataSetChanged();
	}

	private void inverseSelect() {
		for (ProcessInfo info : mUserProcessInfos) {
			if (info.getPackname().equals(getPackageName())) {
				info.setChecked(false);
			} else {
				info.setChecked(!info.isChecked());
			}
		}
		for (ProcessInfo info : mSystemProcessInfos) {
			info.setChecked(!info.isChecked());
		}
		mProcessAdapter.notifyDataSetChanged();
	}

	/**
	 * 杀死选中的进程
	 */
	public void onekeyClean() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int killcount = 0;
		long savedMem = 0;
		List<ProcessInfo> killedInfos = new ArrayList<ProcessInfo>();
		for (ProcessInfo info : mUserProcessInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				killcount++;
				savedMem += info.getMemsize();
				killedInfos.add(info);
			}
		}
		for (ProcessInfo info : mSystemProcessInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				killcount++;
				savedMem += info.getMemsize();
				killedInfos.add(info);
			}
		}
		ToastUtil.makeText(mApp, getString(R.string.kill_process_tips) + killcount + getString(R.string.release_storage_tips) + Formatter.formatFileSize(mApp, savedMem));
		mRunningProcessCount -= killcount;
		mAvailMem += savedMem;
		// fillData(); 影响用户体验 得到的真实的数据
		tv_process_storage.setText(getString(R.string.running_process_tips) + mRunningProcessCount + getString(R.string.available_total_tips) + Formatter.formatFileSize(this, mAvailMem) + "/" + Formatter.formatFileSize(this, mTotalMem));
		for (ProcessInfo info : killedInfos) {
			if (info.isUserProcess()) {
				mUserProcessInfos.remove(info);
			} else {
				mSystemProcessInfos.remove(info);
			}
		}
		// 通知界面更新. 自己修改了 集合里面的数据 不会让listview回到开头
		mProcessAdapter.notifyDataSetChanged();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mAvailMem = StorageUtil.getAvailMemSize(mApp);
		mRunningProcessCount = getRunningProcessCount();
		tv_process_storage.setText(getString(R.string.running_process_tips) + mRunningProcessCount + getString(R.string.available_total_tips) + Formatter.formatFileSize(this, mAvailMem) + "/" + Formatter.formatFileSize(this, mTotalMem));
		fillData();
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				mClpDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<ProcessInfo> processInfos = ProcessInfoProvider.getProcessInfos(mApp);
				mUserProcessInfos = new ArrayList<ProcessInfo>();
				mSystemProcessInfos = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : processInfos) {
					if (info.isUserProcess()) {
						mUserProcessInfos.add(info);
					} else {
						mSystemProcessInfos.add(info);
					}
				}
				Logger.i(TAG, "获取数据完成");
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mClpDialog.dismiss();
				if (mProcessAdapter == null) {
					mProcessAdapter = new ProcessAdapter();
				}
				lv_process.setAdapter(mProcessAdapter);
			}
		}.execute();
	}

	/**
	 * 获取正在运行的进程的个数
	 * 
	 * @return
	 */
	private int getRunningProcessCount() {
		// PackageManager 程序管理器 管理静态的信息.
		// ActivityManager 进程管理器 任务管理器
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}

	/**
	 * 获取总的内存信息
	 * 
	 * @return long byte 单位 大小
	 */
	private long getTotalMemSize() {
		// ActivityManager am = (ActivityManager)
		// getSystemService(ACTIVITY_SERVICE);
		// MemoryInfo outInfo = new MemoryInfo();
		// am.getMemoryInfo(outInfo);
		// return outInfo.totalMem;
		try {
			FileInputStream fis = new FileInputStream("/proc/meminfo");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String result = br.readLine();
			br.close();
			fis.close();
			// MemTotal: 513000 kB
			StringBuffer sb = new StringBuffer();
			for (char c : result.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	private class ProcessAdapter extends BaseAdapter {

		/**
		 * 禁用掉 两个textview的点击事件.
		 */
		@Override
		public boolean isEnabled(int position) {
			if (position == 0) {
				// 第0个位置的条目. 显示一个textview
				return false;
			} else if (position == (mUserProcessInfos.size() + 1)) {
				// 第2个textview 显示
				return false;
			}
			return true;
		}

		/**
		 * 返回listview里面有多少个条目,多出来两个条目 一个显示用户进程的个数 一个显示系统进程的个数
		 */
		@Override
		public int getCount() {
			return mUserProcessInfos.size() + mSystemProcessInfos.size() + 2;
		}

		/**
		 * 返回某个位置绑定的数据
		 */
		@Override
		public Object getItem(int position) {
			ProcessInfo processInfo = null;
			if (position == 0) {
				return null;
			} else if (position == (mUserProcessInfos.size() + 1)) {
				return null;
			} else if (position <= mUserProcessInfos.size()) {
				// 用户进程
				int newposition = position - 1;
				// 最上面有一个textview 把空间占据一个
				processInfo = mUserProcessInfos.get(newposition);
			} else {
				// 剩下来只可能是系统进程
				// 分别减去两个textview
				int newposition = position - mUserProcessInfos.size() - 2;
				processInfo = mSystemProcessInfos.get(newposition);
			}
			return processInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProcessInfo processInfo = null;
			View view = null;
			ViewHolder holder;
			if (position == 0) {// 第0个位置的条目. 显示一个textview
				TextView tv = (TextView) View.inflate(mApp, R.layout.view_list_title, null);
				tv.setText(getString(R.string.user_process_tips) + mUserProcessInfos.size());
				return tv;
			} else if (position == (mUserProcessInfos.size() + 1)) {
				// 第2个textview 显示
				// 有多少个系统进程
				TextView tv = (TextView) View.inflate(mApp, R.layout.view_list_title, null);
				tv.setText(getString(R.string.system_software_tips) + mSystemProcessInfos.size());
				return tv;
			} else if (position <= mUserProcessInfos.size()) {
				// 用户进程
				// 最上面有一个textview 把空间占据一个
				int newposition = position - 1;
				processInfo = mUserProcessInfos.get(newposition);
			} else {
				// 剩下来只可能是系统进程
				// 分别减去两个textview和用户集合的个数
				int newposition = position - mUserProcessInfos.size() - 2;
				processInfo = mSystemProcessInfos.get(newposition);
			}

			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(mApp, R.layout.item_process, null);
				holder = new ViewHolder();
				holder.iv_process_icon = (ImageView) view.findViewById(R.id.iv_process_icon);
				holder.tv_process_name = (TextView) view.findViewById(R.id.tv_process_name);
				holder.tv_storage = (TextView) view.findViewById(R.id.tv_storage);
				holder.cb_process = (CheckBox) view.findViewById(R.id.cb_process);
				view.setTag(holder);
			}

			if (processInfo.getPackname().equals(getPackageName())) {
				holder.cb_process.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_process.setVisibility(View.VISIBLE);
			}

			holder.iv_process_icon.setImageDrawable(processInfo.getIcon());
			holder.tv_process_name.setText(processInfo.getAppName());
			holder.tv_storage.setText(getString(R.string.occupy_storage_tips) + Formatter.formatFileSize(mApp, processInfo.getMemsize()));
			holder.cb_process.setChecked(processInfo.isChecked());
			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv_process_icon;
		TextView tv_process_name;
		TextView tv_storage;
		CheckBox cb_process;
	}

}
