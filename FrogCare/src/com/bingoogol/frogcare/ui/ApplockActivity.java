package com.bingoogol.frogcare.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.bingoogol.frogcare.R;

public class ApplockActivity extends BaseActivity {
	private static final String TAG = "ApplockActivity";
	private ViewPager vp_applock;
	private ArrayList<String> titles;
	private ArrayList<View> views;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_applock);
		vp_applock = (ViewPager) findViewById(R.id.vp_applock);
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		titles = new ArrayList<String>();
		titles.add("未加锁应用");
		titles.add("已加锁应用");

		views = new ArrayList<View>();
		TextView v1 = new TextView(this);
		v1.setText("未加锁应用内容");
		v1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TextView v2 = new TextView(this);
		v2.setText("已加锁应用内容");
		v2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		views.add(v1);
		views.add(v2);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titles.get(position);
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}

		};
		vp_applock.setAdapter(mPagerAdapter);

	}

	@Override
	public void onClick(View v) {
	}
}
