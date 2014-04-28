package com.bingoogol.frogcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private GridView gv_main_function;
	private String[] functionNames;
	private static TextView tv_item_main_content;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		gv_main_function = (GridView) findViewById(R.id.gv_main_function);

	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		functionNames = getResources().getStringArray(R.array.functionNames);
		gv_main_function.setAdapter(new FunctionAdapter());
		gv_main_function.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					// 手机防盗
					// 检查用户是否设置过密码

					break;
				case 1:
					// 通讯卫士
					break;
				case 2:
					// 软件管理
					break;
				case 3:
					// 进程管理
					break;
				case 4:
					// 流量统计
					break;
				case 5:
					// 手机杀毒
					break;
				case 6:
					// 系统优化
					break;
				case 7:
					// 高级工具
					// intent = new Intent(mApp, ToolActivity_.class);
					// startActivity(intent);
					break;
				case 8:
					// 设置中心
					intent = new Intent(mApp, SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		mApp.exitWithDoubleClick();
	}

	private class FunctionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return functionNames.length;
		}

		@Override
		public Object getItem(int position) {
			return functionNames[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(mApp, R.layout.item_main, null);
			// 会调用多次，使用静态的变量引用，减少内存中申请的引用的个数
			tv_item_main_content = (TextView) convertView.findViewById(R.id.tv_item_main_content);
			tv_item_main_content.setText(functionNames[position]);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {

	}
}