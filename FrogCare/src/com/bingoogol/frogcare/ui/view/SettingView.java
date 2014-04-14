package com.bingoogol.frogcare.ui.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

	private String mContentOn;
	private String mContentOff;
	private String mTitle;

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
		mTitle = a.getString(R.styleable.SettingView_title);
		mContentOn = a.getString(R.styleable.SettingView_content_on);
		mContentOff = a.getString(R.styleable.SettingView_content_off);
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

	public void setOnclickedListener(OnCheckedChangeListener listener) {
		cb_view_setting_status.setOnCheckedChangeListener(listener);
	}
}
