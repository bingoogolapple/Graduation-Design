package com.bingoogol.frogcare.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.bingoogol.frogcare.R;

public class PDialog extends Dialog {
	private ProgressWheel pw_dialog_progressbar;

	public PDialog(Context context) {
		super(context, R.style.Theme_Dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress);
		pw_dialog_progressbar = (ProgressWheel) findViewById(R.id.pw_dialog_progressbar);
		pw_dialog_progressbar.resetCount();
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	public void setProgress(int max, int progress,String text) {
		pw_dialog_progressbar.setProgress(360 * progress / max);
		pw_dialog_progressbar.setText(text);
	}
}
