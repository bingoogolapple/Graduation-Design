package com.bingoogol.frogcare.ui;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingoogol.frogcare.FrogCareApplication;
import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.PromptDialog;
import com.bingoogol.frogcare.util.ISharedPreferences_;
import com.bingoogol.frogcare.util.Logger;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	@App
	protected FrogCareApplication mApp;
	@Pref
	ISharedPreferences_ mSp;
	private long[] mClickMenuHits = new long[3];

	@ViewById
	protected GridView gv_main_function;

	private static ImageView iv_item_main_function_icon;
	private static TextView tv_item_main_function_name;

	@StringArrayRes
	protected String[] functionNames;
	private static int[] mFunctionIcons = new int[] { R.drawable.security, R.drawable.communicate, R.drawable.software, R.drawable.process, R.drawable.traffic, R.drawable.antivirus, R.drawable.optimize, R.drawable.tool, R.drawable.setting };

	@AfterInject
	public void afterInject() {
		mApp.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.removeActivity(this);
	}

	@Override
	public void onBackPressed() {
		mApp.exitWithDoubleClick();
	}

	@AfterViews
	public void afterViews() {
		gv_main_function.setAdapter(new FunctionAdapter());
		gv_main_function.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					// 手机防盗
					// 检查用户是否设置过密码

					break;
				case 1:
					// 通讯卫士
					break;
				case 2:
					// 软件管理
					break;
				case 3:
					// 进程管理
					break;
				case 4:
					// 流量统计
					break;
				case 5:
					// 手机杀毒
					break;
				case 6:
					// 系统优化
					break;
				case 7:
					// 高级工具
					intent = new Intent(mApp, ToolActivity_.class);
					startActivity(intent);
					break;
				case 8:
					// 设置中心
					intent = new Intent(mApp, SettingActivity_.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_MENU == keyCode) {
			System.arraycopy(mClickMenuHits, 1, mClickMenuHits, 0, mClickMenuHits.length - 1);
			mClickMenuHits[mClickMenuHits.length - 1] = System.currentTimeMillis();
			if (mClickMenuHits[mClickMenuHits.length - 1] - mClickMenuHits[0] <= 1500) {
				changeAppLockPassword();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void changeAppLockPassword() {
		final PromptDialog pd = new PromptDialog(MainActivity.this, R.string.change_applockpwd, R.string.please_input_applockpwd, R.string.update, R.string.cancel, true);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickOk() {
				String appLockPwd = pd.getContent();
				if (appLockPwd.length() == 0) {
					pd.shake();
				} else {
					Logger.d(TAG, "修改程序锁密码成功");
					pd.dismiss();
					mSp.appLockPwd().put(appLockPwd);
				}
			}

			@Override
			public void onClickCancel() {
			}
		});
		pd.show();
	}

	private class FunctionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return functionNames.length;
		}

		@Override
		public Object getItem(int position) {
			return functionNames[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(mApp, R.layout.item_main_function, null);
			// 会调用多次，使用静态的变量引用，减少内存中申请的引用的个数
			iv_item_main_function_icon = (ImageView) convertView.findViewById(R.id.iv_item_main_function_icon);
			tv_item_main_function_name = (TextView) convertView.findViewById(R.id.tv_item_main_function_name);
			iv_item_main_function_icon.setBackgroundResource(mFunctionIcons[position]);
			tv_item_main_function_name.setText(functionNames[position]);
			return convertView;
		}

	}
}
