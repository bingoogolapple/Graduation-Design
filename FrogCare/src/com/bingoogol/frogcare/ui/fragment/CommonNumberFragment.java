package com.bingoogol.frogcare.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.AdvanceToolActivity;

public class CommonNumberFragment extends Fragment implements OnClickListener {
	private static final String TAG = "CommonNumberFragment";
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
		return inflater.inflate(R.layout.fragment_common_number, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().findViewById(R.id.btn_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_back) {
			mAdvanceToolCallback.changeFragment(AdvanceToolActivity.ADVANCE_TOOL);
		}
	}
}
