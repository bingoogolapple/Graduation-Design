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
	private Button btn_dialog_confirm_ok;
	private Button btn_dialog_comfirm_cancel;
	private BtnCallback mBtnCallback;
	private int mTitleResId;
	private String mMsg;
	private int mOkResId;
	private int mCancelResId;

	public ConfirmDialog(Context context, int titleResId, String msgResId, int okResId, int cancelResId) {
		super(context, R.style.DialogTheme);
		mTitleResId = titleResId;
		mMsg = msgResId;
		mOkResId = okResId;
		mCancelResId = cancelResId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);
		tv_dialog_confirm_title = (TextView) findViewById(R.id.tv_dialog_confirm_title);
		tv_dialog_confirm_msg = (TextView) findViewById(R.id.tv_dialog_confirm_msg);
		btn_dialog_confirm_ok = (Button) findViewById(R.id.btn_dialog_confirm_ok);
		btn_dialog_comfirm_cancel = (Button) findViewById(R.id.btn_dialog_comfirm_cancel);
		btn_dialog_confirm_ok.setOnClickListener(this);
		btn_dialog_comfirm_cancel.setOnClickListener(this);

		tv_dialog_confirm_title.setText(mTitleResId);
		tv_dialog_confirm_msg.setText(mMsg);
		btn_dialog_confirm_ok.setText(mOkResId);
		btn_dialog_comfirm_cancel.setText(mCancelResId);

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
		case R.id.btn_dialog_confirm_ok:
			mBtnCallback.onClickOk();
			break;
		case R.id.btn_dialog_comfirm_cancel:
			mBtnCallback.onClickCancel();
			break;
		}
	}

}
