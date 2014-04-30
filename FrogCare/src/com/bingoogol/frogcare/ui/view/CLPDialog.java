package com.bingoogol.frogcare.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bingoogol.frogcare.R;

public class CLPDialog extends Dialog {
	private AnimationDrawable mAd;
	private ImageView iv_clpd_progress;

	public CLPDialog(Context context) {
		super(context, R.style.Theme_Dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_clpd);
		iv_clpd_progress = (ImageView) findViewById(R.id.iv_clpd_progress);
		mAd = (AnimationDrawable) iv_clpd_progress.getDrawable();
		setCancelable(false);
	}

	@Override
	public void show() {
		super.show();
		mAd.start();
	}
}
