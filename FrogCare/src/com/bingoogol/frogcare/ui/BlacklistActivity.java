package com.bingoogol.frogcare.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.BlacklistDao;
import com.bingoogol.frogcare.domain.BlacklistInfo;
import com.bingoogol.frogcare.ui.view.CLPDialog;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.ToastUtil;

public class BlacklistActivity extends BaseActivity {
	private static final String TAG = "BlacklistActivity";

	private ListView lv_blacklist;
	private BlacklistDao mBlacklistDao;
	private List<BlacklistInfo> mBlacklistInfos;
	private CLPDialog mClpDialog;
	/**
	 * 一共有多少页
	 */
	private int mTotalPageNumber = 0;
	/**
	 * 初始页码
	 */
	private int mPagenumber = 0;
	/**
	 * 每一次最多获取的数据
	 */
	private int maxSize = 20;

	private BlacklistAdapter mBlacklistAdapter;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_blacklist);
		lv_blacklist = (ListView) findViewById(R.id.lv_blacklist);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.btn_add).setOnClickListener(this);
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		mClpDialog = new CLPDialog(this);
		mBlacklistDao = new BlacklistDao(mApp);
		fillData();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_add) {
			BlacklistInfo blacklistInfo = new BlacklistInfo("123", 1);
			if (mBlacklistDao.add(blacklistInfo) > 0) {
				mBlacklistInfos.add(blacklistInfo);
				mBlacklistAdapter.notifyDataSetChanged();
			} else {
				ToastUtil.makeText(mApp, "添加黑名单号码失败");
			}
		}
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				mClpDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				/**
				 * 检查集合是否为空 ,如果不为空 新的数据添加到集合的里面.
				 */
				if (mBlacklistInfos == null) {
					mBlacklistInfos = mBlacklistDao.findBlacklistByPage(mPagenumber, maxSize);
				} else {
					mBlacklistInfos.addAll(mBlacklistDao.findBlacklistByPage(mPagenumber, maxSize));
				}
				Logger.i(TAG, "获取数据完成");
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mClpDialog.dismiss();
				if (mBlacklistAdapter == null) {
					mBlacklistAdapter = new BlacklistAdapter();
				}
				lv_blacklist.setAdapter(mBlacklistAdapter);
			}
		}.execute();
	}

	private class BlacklistAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mBlacklistInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			// 减少 布局文件转化成view对象的次数 减少了对象的创建
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// Log.i(TAG, "创建新的view对象:" + position);
				view = View.inflate(getApplicationContext(), R.layout.item_blacklist, null);
				// 2减少孩子控件的查找次数
				holder = new ViewHolder();// 创建一个容器 容器 存放孩子id的引用.
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.ib_delete = (ImageButton) view.findViewById(R.id.ib_delete);
				// 把holder对象跟view对象关联在一起.
				view.setTag(holder);
			}
			final BlacklistInfo blacklistInfo = mBlacklistInfos.get(position);
			holder.tv_number.setText(getString(R.string.number_tips) + blacklistInfo.getNumber());
			if (blacklistInfo.getMode() == 1) {
				holder.tv_mode.setText(R.string.all_intercept);
			} else if (blacklistInfo.getMode() == 2) {
				holder.tv_mode.setText(R.string.phone_intercept);
			} else if (blacklistInfo.getMode() == 3) {
				holder.tv_mode.setText(R.string.message_message);
			}
			holder.ib_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mBlacklistDao.delete(blacklistInfo.getNumber());
					mBlacklistInfos.remove(blacklistInfo);
					mBlacklistAdapter.notifyDataSetChanged();
				}
			});
			return view;
		}
	}

	private static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageButton ib_delete;
	}
}
