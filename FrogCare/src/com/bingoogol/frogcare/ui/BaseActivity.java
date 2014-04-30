package com.bingoogol.frogcare.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.bingoogol.frogcare.App;
import com.bingoogol.frogcare.R;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
	protected App mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mApp = (App) getApplicationContext();
		mApp.addActivity(this);
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		afterViews(savedInstanceState);
	}

	/**
	 * 初始化布局以及View控件
	 */
	protected abstract void initView();

	/**
	 * 给View控件添加事件监听器
	 */
	protected abstract void setListener();

	/**
	 * 处理业务逻辑，状态恢复等操作
	 * 
	 * @param savedInstanceState
	 */
	protected abstract void afterViews(Bundle savedInstanceState);

	@Override
	protected void onDestroy() {
		mApp.removeActivity(this);
		super.onDestroy();
	}

	public void onBack(View v) {
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		finish();
	}
}