package com.bingoogol.frogcare.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

public class ConfirmDialog extends Dialog implements OnClickListener {
	private TextView tv_dialog_confirm_title;
	private TextView tv_dialog_confirm_msg;
	private Button btn_dialog_confirm_left;
	private Button btn_dialog_comfirm_right;
	private BtnCallback mBtnCallback;
	private int mTitleResId;
	private String mMsg;
	private int mLeftResId;
	private int mRightResId;

	public ConfirmDialog(Context context, int titleResId, String msg, int leftResId, int rightResId) {
		super(context, R.style.Theme_Dialog);
		mTitleResId = titleResId;
		mMsg = msg;
		mLeftResId = leftResId;
		mRightResId = rightResId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);
		tv_dialog_confirm_title = (TextView) findViewById(R.id.tv_dialog_confirm_title);
		tv_dialog_confirm_msg = (TextView) findViewById(R.id.tv_dialog_confirm_msg);
		btn_dialog_confirm_left = (Button) findViewById(R.id.btn_dialog_confirm_left);
		btn_dialog_comfirm_right = (Button) findViewById(R.id.btn_dialog_comfirm_right);
		btn_dialog_confirm_left.setOnClickListener(this);
		btn_dialog_comfirm_right.setOnClickListener(this);

		tv_dialog_confirm_title.setText(mTitleResId);
		tv_dialog_confirm_msg.setText(mMsg);
		btn_dialog_confirm_left.setText(mLeftResId);
		btn_dialog_comfirm_right.setText(mRightResId);

		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	public void setBtnCallback(BtnCallback btnCallback) {
		mBtnCallback = btnCallback;
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.btn_dialog_confirm_left:
			mBtnCallback.onClickLeft();
			break;
		case R.id.btn_dialog_comfirm_right:
			mBtnCallback.onClickRight();
			break;
		}
	}

}
