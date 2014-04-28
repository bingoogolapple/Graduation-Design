package com.bingoogol.frogcare.ui.view;

import com.bingoogol.frogcare.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

public class PDialog extends Dialog {
	private ProgressBar pb_dialog_progress;

	public PDialog(Context context) {
		super(context, R.style.Theme_Dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress);
		pb_dialog_progress = (ProgressBar) findViewById(R.id.pb_dialog_progress);
		pb_dialog_progress.setProgress(0);

		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	public void setProgress(int progress) {
		pb_dialog_progress.setProgress(progress);
	}

	public void setMax(int max) {
		pb_dialog_progress.setMax(max);
	}
}
