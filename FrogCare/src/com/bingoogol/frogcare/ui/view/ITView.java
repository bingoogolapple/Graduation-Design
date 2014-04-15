package com.bingoogol.frogcare.ui.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

@EViewGroup(R.layout.view_it)
public class ITView extends RelativeLayout {

	@ViewById
	protected ImageView iv_it_icon;
	@ViewById
	protected TextView tv_it_text;

	private CharSequence mText;
	private Drawable mDrawable;

	public ITView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ITView);
		mText = a.getText(R.styleable.ITView_text);
		mDrawable = a.getDrawable(R.styleable.ITView_src);
		a.recycle();
	}

	@AfterViews
	public void afterViews() {
		tv_it_text.setText(mText);
		iv_it_icon.setImageDrawable(mDrawable);
	}
}