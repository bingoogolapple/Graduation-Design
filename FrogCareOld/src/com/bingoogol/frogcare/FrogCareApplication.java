package com.bingoogol.frogcare;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EApplication;

import android.app.Activity;
import android.app.Application;

import com.bingoogol.frogcare.util.ToastUtil;

@EApplication
public class FrogCareApplication extends Application {
	private List<Activity> mActivities = new ArrayList<Activity>();
	private long lastTime;

	public void addActivity(Activity activity) {
		mActivities.add(activity);
	}

	public void removeActivity(Activity activity) {
		mActivities.remove(activity);
	}

	public void exitWithDoubleClick() {
		if (System.currentTimeMillis() - lastTime <= 1500) {
			exit();
		} else {
			lastTime = System.currentTimeMillis();
			ToastUtil.makeText(this, R.string.exit_tips);
		}
	}

	public void exit() {
		for (Activity activity : mActivities) {
			activity.finish();
		}
		System.exit(0);
	}
}
