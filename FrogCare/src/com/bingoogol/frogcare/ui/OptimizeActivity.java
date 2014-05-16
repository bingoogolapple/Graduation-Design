package com.bingoogol.frogcare.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.domain.CacheInfo;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.Logger;

public class OptimizeActivity extends BaseActivity {
	private static final String TAG = "OptimizeActivity";
	private ListView lv_cache;
	private List<CacheInfo> mCacheInfos;
	private CLPDialog mClpDialog;
	private CacheAdapter mCacheAdapter;
	private Map<String, CacheInfo> maps;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_optimize);
		lv_cache = (ListView) findViewById(R.id.lv_cache);
	}

	@Override
	protected void setListener() {
		lv_cache.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.applications.InstalledAppDetailsTop"));
				intent.setData(Uri.parse("package:" + mCacheInfos.get(position).getPackname()));
				startActivity(intent);
			}
		});
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mClpDialog = new CLPDialog(this);
		maps = new HashMap<String, CacheInfo>();
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
				List<PackageInfo> packageinfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				for (PackageInfo info : packageinfos) {
					String appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
					String packname = info.packageName;
					CacheInfo cacheinfo = new CacheInfo();
					cacheinfo.setAppName(appName);
					cacheinfo.setPackname(packname);
					maps.put(packname, cacheinfo);
					setAppSize(packname);
				}
				try {
					// 有延迟，先睡一下
					Thread.sleep(4500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Set<Entry<String, CacheInfo>> sets = maps.entrySet();
				mCacheInfos = new ArrayList<CacheInfo>();
				for (Entry<String, CacheInfo> entry : sets) {
					mCacheInfos.add(entry.getValue());
				}
				Logger.i(TAG, "获取数据完成" + mCacheInfos.size());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mClpDialog.dismiss();
				if (mCacheAdapter == null) {
					mCacheAdapter = new CacheAdapter();
				}
				lv_cache.setAdapter(mCacheAdapter);
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 根据包名获取应用程序的体积信息 注意: 这个方法是一个异步的方法 程序的体积要花一定时间才能获取到.
	 * 
	 * @param packname
	 */
	private void setAppSize(final String packname) {
		try {
			Method method = PackageManager.class.getMethod("getPackageSizeInfo", new Class[] { String.class, IPackageStatsObserver.class });
			method.invoke(getPackageManager(), new Object[] { packname, new IPackageStatsObserver.Stub() {

				public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
					// 注意这个操作是一个异步的操作
					long codesize = pStats.codeSize;
					long datasize = pStats.dataSize;
					Logger.i(TAG, packname);
					CacheInfo info = maps.get(packname);
					info.setDataSize(Formatter.formatFileSize(mApp, datasize));
					info.setCodeSize(Formatter.formatFileSize(mApp, codesize));
					maps.put(packname, info);
				}
			} });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCacheInfos.size();
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
			View view = null;
			ViewHolder holder = null;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(mApp, R.layout.item_optimize, null);
				holder = new ViewHolder();
				holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_code_size = (TextView) view.findViewById(R.id.tv_code_size);
				holder.tv_data_size = (TextView) view.findViewById(R.id.tv_data_size);
				// 把holder对象跟view对象关联在一起.
				view.setTag(holder);
			}
			final CacheInfo cacheInfo = mCacheInfos.get(position);
			holder.tv_app_name.setText(cacheInfo.getAppName());
			holder.tv_code_size.setText(cacheInfo.getCodeSize());
			holder.tv_data_size.setText(cacheInfo.getDataSize());
			return view;
		}
	}

	private static class ViewHolder {
		TextView tv_app_name;
		TextView tv_code_size;
		TextView tv_data_size;
	}

}
