package com.bingoogol.frogcare.ui.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

@EViewGroup(R.layout.view_setting)
public class SettingView extends RelativeLayout {

	@ViewById
	protected TextView tv_view_setting_title;
	@ViewById
	protected TextView tv_view_setting_content;
	@ViewById
	protected CheckBox cb_view_setting_status;

	private CharSequence mContentOn;
	private CharSequence mContentOff;
	private CharSequence mTitle;

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SettingView_title:
				mTitle = a.getText(attr);
				break;
			case R.styleable.SettingView_content_on:
				mContentOn = a.getText(attr);
				break;
			case R.styleable.SettingView_content_off:
				mContentOff = a.getText(attr);
				break;
			}
		}
		a.recycle();
	}

	@AfterViews
	public void afterViews() {
		tv_view_setting_title.setText(mTitle);
		if (isChecked()) {
			tv_view_setting_content.setText(mContentOn);
		} else {
			tv_view_setting_content.setText(mContentOff);
		}
	}

	public boolean isChecked() {
		return cb_view_setting_status.isChecked();
	}

	public void setChecked(boolean checked) {
		cb_view_setting_status.setChecked(checked);
		if (checked) {
			tv_view_setting_content.setTextColor(Color.BLACK);
			tv_view_setting_content.setText(mContentOn);
		} else {
			tv_view_setting_content.setText(mContentOff);
			tv_view_setting_content.setTextColor(Color.RED);
		}
	}
}