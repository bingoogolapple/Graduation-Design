package com.bingoogol.frogcare.ui.fragment;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.AdvanceToolActivity;
import com.bingoogol.frogcare.util.Logger;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AdvanceToolFragment extends Fragment implements OnClickListener {
	private static final String TAG = "AdvanceToolFragment";
	private AdvanceToolCallback mAdvanceToolCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mAdvanceToolCallback = (AdvanceToolCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement AdvanceToolCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_advance_tool, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().findViewById(R.id.btn_attribution).setOnClickListener(this);
		getView().findViewById(R.id.btn_common_number).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_attribution:
			Logger.i(TAG, "手机号码归属地查询");
			mAdvanceToolCallback.changeFragment(AdvanceToolActivity.ATTRIBUTION);
			break;
		case R.id.btn_common_number:
			Logger.i(TAG, "常用号码查询");
			mAdvanceToolCallback.changeFragment(AdvanceToolActivity.COMMON_NUMBER);
			break;
		}
	}
}
