package com.bingoogol.frogcare.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.db.dao.AddressDao;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;

/**
 * 来电归属地显示服务
 * 
 * @author bingoogol@sina.com
 * 
 */
public class AttributionService extends Service {
	private static final String TAG = "AttributionService";
	private TelephonyManager mTelephonyManager;
	private AttributionPhoneListener mAttributionPhoneListener;
	private OutgoingCallReceiver mOutgoingCallReceiver;
	private WindowManager mWindowManager;
	/**
	 * 全局的显示在界面上的归属地组件
	 */
	private View mView;
	private LayoutParams mLayoutParams;

	@Override
	public void onCreate() {
		super.onCreate();
		mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		// 采用代码的方式注册广播接收者
		mOutgoingCallReceiver = new OutgoingCallReceiver();
		registerReceiver(mOutgoingCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
		// 注册一个监听器监听电话的状态
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mAttributionPhoneListener = new AttributionPhoneListener();
		mTelephonyManager.listen(mAttributionPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTelephonyManager.listen(mAttributionPhoneListener, PhoneStateListener.LISTEN_NONE);
		mAttributionPhoneListener = null;
		unregisterReceiver(mOutgoingCallReceiver);
		mOutgoingCallReceiver = null;
	}

	/**
	 * 外拨电话广播接收者,在开启归属地显示服务时注册该广播接收者，关闭归属地显示服务时取消注册该广播接收者
	 * 
	 * @author bingoogol@sina.com
	 * 
	 */
	private class OutgoingCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.i(TAG, "监听到外拨电话");
			showAddress(AddressDao.getAddress(getApplicationContext(), getResultData()));
		}
	}

	/**
	 * 归属地电话监听器
	 * 
	 * @author bingoogol@sina.com
	 * 
	 */
	private class AttributionPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 电话到来，显示归属地组件
				showAddress(AddressDao.getAddress(getApplicationContext(), incomingNumber));
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// 空闲状态 电话挂断，移除归属地组件
				if (mView != null) {
					mWindowManager.removeView(mView);
					mView = null;
				}
				break;
			}
		}
	}

	private void showAddress(String address) {
		mView = View.inflate(this, R.layout.view_attribution, null);
		mView.setOnTouchListener(new View.OnTouchListener() {
			int startX = 0;
			int startY = 0;

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					mLayoutParams.x += dx;
					mLayoutParams.y += dy;
					if (mLayoutParams.x < 0) {
						mLayoutParams.x = 0;
					}
					if (mLayoutParams.y < 0) {
						mLayoutParams.y = 0;
					}
					if (mLayoutParams.x > mWindowManager.getDefaultDisplay().getWidth()) {
						mLayoutParams.x = mWindowManager.getDefaultDisplay().getWidth();
					}
					if (mLayoutParams.y > mWindowManager.getDefaultDisplay().getHeight()) {
						mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight();
					}
					mWindowManager.updateViewLayout(mView, mLayoutParams);
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(Constants.spkey.ATTRIBUTION_LAST_X, mLayoutParams.x);
					SpUtil.putInt(Constants.spkey.ATTRIBUTION_LAST_Y, mLayoutParams.y);
					break;
				}
				return true;
			}
		});

		TextView tv_attribution = (TextView) mView.findViewById(R.id.tv_attribution);
		tv_attribution.setText(address);
		mLayoutParams = new LayoutParams();
		mLayoutParams.gravity = Gravity.LEFT + Gravity.TOP;
		mLayoutParams.x = SpUtil.getInt(Constants.spkey.ATTRIBUTION_LAST_X, 300);
		mLayoutParams.y = SpUtil.getInt(Constants.spkey.ATTRIBUTION_LAST_Y, 300);
		mLayoutParams.height = LayoutParams.WRAP_CONTENT;
		mLayoutParams.width = LayoutParams.WRAP_CONTENT;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_KEEP_SCREEN_ON;
		mLayoutParams.format = PixelFormat.TRANSLUCENT;
		mLayoutParams.type = LayoutParams.TYPE_PRIORITY_PHONE;
		mWindowManager.addView(mView, mLayoutParams);
	}
}