package com.bingoogol.frogcare.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.bingoogol.frogcare.ui.fragment.AdvanceToolCallback;
import com.bingoogol.frogcare.ui.fragment.AdvanceToolFragment;
import com.bingoogol.frogcare.ui.fragment.AttributionFragment;
import com.bingoogol.frogcare.ui.fragment.CommonNumberFragment;

public class AdvanceToolActivity extends BaseActivity implements AdvanceToolCallback {
	private static final String TAG = "AdvanceToolActivity";
	public static final int ADVANCE_TOOL = 1;
	public static final int COMMON_NUMBER = 2;
	public static final int ATTRIBUTION = 3;
	private AdvanceToolFragment mAdvanceToolFragment;
	private AttributionFragment mAttributionFragment;
	private CommonNumberFragment mCommonNumberFragment;
	private int mToolType = ADVANCE_TOOL;

	@Override
	protected void initView() {
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			mAdvanceToolFragment = new AdvanceToolFragment();
			mAttributionFragment = new AttributionFragment();
			mCommonNumberFragment = new CommonNumberFragment();
		} else {
			mToolType = savedInstanceState.getInt("mToolType");
		}
		changeFragment(mToolType);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mToolType", mToolType);
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void changeFragment(int toolType) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		switch (toolType) {
		case ADVANCE_TOOL:
			ft.replace(android.R.id.content, mAdvanceToolFragment);
			break;
		case COMMON_NUMBER:
			ft.replace(android.R.id.content, mCommonNumberFragment);
			break;
		case ATTRIBUTION:
			ft.replace(android.R.id.content, mAttributionFragment);
			break;
		}
		ft.commit();
		mToolType = toolType;
	}
}
