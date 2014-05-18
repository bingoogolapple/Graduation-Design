package com.bingoogol.frogcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.ui.view.BtnCallback;
import com.bingoogol.frogcare.ui.view.PromptDialog;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;
import com.bingoogol.frogcare.util.ToastUtil;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private GridView gv_main_function;
	private String[] functionNames;
	private static TextView tv_item_main_content;
	private long[] mClickMenuHits = new long[3];

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		gv_main_function = (GridView) findViewById(R.id.gv_main_function);

	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void afterViews(Bundle savedInstanceState) {
		functionNames = getResources().getStringArray(R.array.functionNames);
		gv_main_function.setAdapter(new FunctionAdapter());
		gv_main_function.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					// 手机防盗
					// 检查用户是否设置过密码
					openActivity(TheftActivity.class);
					break;
				case 1:
					// 通讯卫士
					openActivity(BlacklistActivity.class);
					break;
				case 2:
					// 软件管理
					openActivity(SoftwareManageActivity.class);
					break;
				case 3:
					// 进程管理
					openActivity(ProcessManageActivity.class);
					break;
				case 4:
					// 隐私保护
					openActivity(ApplockActivity.class);
					break;
				case 5:
					// 手机杀毒
					openActivity(VirusActivity.class);
					break;
				case 6:
					// 系统优化
					openActivity(OptimizeActivity.class);
					break;
				case 7:
					// 高级工具
					openActivity(AdvanceToolActivity.class);
					break;
				case 8:
					// 设置中心
					openActivity(SettingActivity.class);
					break;
				}
			}
		});
		auth();
	}

	private void auth() {
		if (TextUtils.isEmpty(SpUtil.getString(Constants.spkey.APPLOCK_PWD, ""))) {
			showChangeAppLockPasswordDialog();
		} else {
			showAuthAppLockPasswordDialog();
		}
	}

	@Override
	public void onBackPressed() {
		mApp.exitWithDoubleClick();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (KeyEvent.KEYCODE_MENU == keyCode) {
			System.arraycopy(mClickMenuHits, 1, mClickMenuHits, 0, mClickMenuHits.length - 1);
			mClickMenuHits[mClickMenuHits.length - 1] = System.currentTimeMillis();
			if (mClickMenuHits[mClickMenuHits.length - 1] - mClickMenuHits[0] <= 1500) {
				if (TextUtils.isEmpty(SpUtil.getString(Constants.spkey.APPLOCK_PWD, ""))) {
					showChangeAppLockPasswordDialog();
				} else {
					showEnterOldAppLockPasswordDialog();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showAuthAppLockPasswordDialog() {
		final PromptDialog pd = new PromptDialog(MainActivity.this, R.string.auth_applockpwd, R.string.please_input_applockpwd, R.string.ok, R.string.cancel, true);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String appLockPwd = pd.getContent();
				if (appLockPwd.equals(SpUtil.getString(Constants.spkey.APPLOCK_PWD, ""))) {
					pd.dismiss();
				} else {
					ToastUtil.makeText(mApp, R.string.pwd_error_tips);
				}
			}

			@Override
			public void onClickLeft() {
				mApp.exit();
			}
		});
		pd.show();
	}

	private void openActivity(Class<?> cls) {
		startActivity(new Intent(mApp, cls));
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	private void showEnterOldAppLockPasswordDialog() {
		final PromptDialog pd = new PromptDialog(MainActivity.this, R.string.auth_applockpwd, R.string.please_input_oldapplockpwd, R.string.ok, R.string.cancel, true);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String appLockPwd = pd.getContent();
				if (appLockPwd.equals(SpUtil.getString(Constants.spkey.APPLOCK_PWD, ""))) {
					pd.dismiss();
					showChangeAppLockPasswordDialog();
				} else {
					ToastUtil.makeText(mApp, R.string.pwd_error_tips);
				}
			}

			@Override
			public void onClickLeft() {
			}
		});
		pd.show();
	}

	private void showChangeAppLockPasswordDialog() {
		final PromptDialog pd = new PromptDialog(MainActivity.this, R.string.change_applockpwd, R.string.please_input_newapplockpwd, R.string.update, R.string.cancel, true);
		pd.setBtnCallback(new BtnCallback() {

			@Override
			public void onClickRight() {
				String appLockPwd = pd.getContent();
				if (appLockPwd.length() == 0) {
					pd.shake();
					ToastUtil.makeText(mApp, R.string.pwd_not_allow_null_tips);
				} else {
					Logger.d(TAG, "修改程序锁密码成功");
					pd.dismiss();
					SpUtil.putString(Constants.spkey.APPLOCK_PWD, appLockPwd);
				}
			}

			@Override
			public void onClickLeft() {
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
			convertView = View.inflate(mApp, R.layout.item_main, null);
			// 会调用多次，使用静态的变量引用，减少内存中申请的引用的个数
			tv_item_main_content = (TextView) convertView.findViewById(R.id.tv_item_main_content);
			tv_item_main_content.setText(functionNames[position]);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {

	}

	protected enum OpreateType {
		APP_LOCK, SETTING_CENTER, THEFT
	}
}