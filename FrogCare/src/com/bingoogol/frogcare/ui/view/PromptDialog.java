package com.bingoogol.frogcare.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

public class PromptDialog extends Dialog implements OnClickListener {
	private TextView tv_dialog_prompt_title;
	private EditText et_dialog_prompt_content;
	private Button btn_dialog_prompt_right;
	private Button btn_dialog_prompt_left;
	private BtnCallback mBtnCallback;
	private int mTitleResId;
	private int mHintResId;
	private int mRightResId;
	private int mLeftResId;
	private boolean mIsPwdType;

	public PromptDialog(Context context, int titleResId, int hintResId, int rightResId, int leftResId, boolean isPwdType) {
		super(context, R.style.Theme_Dialog);
		mTitleResId = titleResId;
		mHintResId = hintResId;
		mRightResId = rightResId;
		mLeftResId = leftResId;
		mIsPwdType = isPwdType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_prompt);
		tv_dialog_prompt_title = (TextView) findViewById(R.id.tv_dialog_prompt_title);
		et_dialog_prompt_content = (EditText) findViewById(R.id.et_dialog_prompt_content);
		btn_dialog_prompt_right = (Button) findViewById(R.id.btn_dialog_prompt_right);
		btn_dialog_prompt_left = (Button) findViewById(R.id.btn_dialog_prompt_left);
		btn_dialog_prompt_right.setOnClickListener(this);
		btn_dialog_prompt_left.setOnClickListener(this);

		tv_dialog_prompt_title.setText(mTitleResId);
		et_dialog_prompt_content.setHint(mHintResId);
		if (mIsPwdType) {
			et_dialog_prompt_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		btn_dialog_prompt_right.setText(mRightResId);
		btn_dialog_prompt_left.setText(mLeftResId);

		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	public void setBtnCallback(BtnCallback btnCallback) {
		mBtnCallback = btnCallback;
	}

	public String getContent() {
		return et_dialog_prompt_content.getText().toString().trim();
	}

	public void shake() {
		Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
		et_dialog_prompt_content.startAnimation(shakeAnimation);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_prompt_right:
			mBtnCallback.onClickRight();
			break;
		case R.id.btn_dialog_prompt_left:
			this.dismiss();
			mBtnCallback.onClickLeft();
			break;
		}
	}

}
