package com.bingoogol.frogcare.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

public class SettingView extends RelativeLayout {

	private TextView tv_view_setting_title;
	private TextView tv_view_setting_content;
	private ImageView iv_view_setting_status;

	private CharSequence mContentOn;
	private CharSequence mContentOff;
	private boolean mIsChecked;

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		initAttrs(context, attrs);
	}

	public SettingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void initView(Context context) {
		View view = View.inflate(context, R.layout.view_setting, this);
		tv_view_setting_title = (TextView) view.findViewById(R.id.tv_view_setting_title);
		tv_view_setting_content = (TextView) view.findViewById(R.id.tv_view_setting_content);
		iv_view_setting_status = (ImageView) view.findViewById(R.id.iv_view_setting_status);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SettingView_title:
				tv_view_setting_title.setText(a.getText(attr));
				break;
			case R.styleable.SettingView_content_on:
				mContentOn = a.getText(attr);
				break;
			case R.styleable.SettingView_content_off:
				mContentOff = a.getText(attr);
				iv_view_setting_status.setImageResource(R.drawable.setting_off);
				tv_view_setting_content.setText(mContentOff);
				mIsChecked = false;
				break;
			}
		}
		a.recycle();
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void setChecked(boolean checked) {
		mIsChecked = checked;
		if (mIsChecked) {
			tv_view_setting_content.setText(mContentOn);
			iv_view_setting_status.setImageResource(R.drawable.setting_on);
		} else {
			tv_view_setting_content.setText(mContentOff);
			iv_view_setting_status.setImageResource(R.drawable.setting_off);
		}
	}
}