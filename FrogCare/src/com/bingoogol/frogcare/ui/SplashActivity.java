package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bingoogol.frogcare.R;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {

	@AfterViews
	public void afterViews() {
		Animation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(1000);
		findViewById(R.id.ll_splash_root).startAnimation(animation);
	}
}
