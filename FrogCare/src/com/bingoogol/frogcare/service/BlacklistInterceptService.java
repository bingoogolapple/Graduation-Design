package com.bingoogol.frogcare.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.bingoogol.frogcare.db.dao.BlacklistDao;
import com.bingoogol.frogcare.util.Logger;

/**
 * 黑名单拦截服务
 * 
 * @author bingoogol@sina.com
 * 
 */
public class BlacklistInterceptService extends Service {
	private static final String TAG = "BlacklistInterceptService";
	private BlacklistSmsInterceptReceiver mBlacklistSmsInterceptReceiver;
	private BlacklistDao mBlacklistDao;
	private TelephonyManager mTelephonyManager;
	private BlacklistPhoneInterceptListener mBlacklistPhoneInterceptListener;

	@Override
	public void onCreate() {
		super.onCreate();
		mBlacklistDao = new BlacklistDao(this);
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mBlacklistPhoneInterceptListener = new BlacklistPhoneInterceptListener();
		mTelephonyManager.listen(mBlacklistPhoneInterceptListener, PhoneStateListener.LISTEN_CALL_STATE);
		Logger.i(TAG, "注册黑名单电话监听器");
		mBlacklistSmsInterceptReceiver = new BlacklistSmsInterceptReceiver();
		IntentFilter filter = new IntentFilter();
		// 谁先执行这段代码，谁优先级就高。所以应该在开机广播中开启服务
		filter.setPriority(Integer.MAX_VALUE);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(mBlacklistSmsInterceptReceiver, filter);
		Logger.i(TAG, "注册黑名单短信广播接收者");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBlacklistSmsInterceptReceiver);
		mBlacklistSmsInterceptReceiver = null;
		mTelephonyManager.listen(mBlacklistPhoneInterceptListener, PhoneStateListener.LISTEN_NONE);
		mBlacklistPhoneInterceptListener = null;
	}

	/**
	 * 黑名单短信接收器，在服务中通过代码的方式注册，在开启黑名单拦截服务时注册该广播接受者，在关闭黑名单拦截服务时取消注册该广播
	 * 
	 * @author bingoogol
	 * 
	 */
	private class BlacklistSmsInterceptReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				if (sender.length() == 14) {
					sender = sender.substring(3);
				}
				// 检查sender是否在黑名单列表里面 检查拦截模式. 短信拦截 全部拦截
				int mode = mBlacklistDao.findMode(sender);
				Logger.i(TAG, "收到短信 mode:" + mode);
				if (1 == mode || mode == 3) {
					Logger.i(TAG, "拦截黑名单短信\n发送者：" + sender + "\n短信内容" + smsMessage.getMessageBody());
					abortBroadcast();
					// TODO:把短信的内容 和 电话号码 时间 存起来.
				}
			}

		}

	}

	/**
	 * 黑名单电话监听器，在开启黑名单拦截服务时注册该监听器，在关闭黑名单拦截服务时取消该监听器
	 * 
	 * @author bingoogol@sina.com
	 * 
	 */
	private class BlacklistPhoneInterceptListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				int mode = mBlacklistDao.findMode(incomingNumber);
				Logger.i(TAG, "收到来电 mode:" + mode + "  number:" + incomingNumber);
				if (1 == mode || 2 == mode) {
					Logger.i("PhoneListener", "挂断电话");
					endCall();
					// 呼叫记录不是立刻产生的.不能立刻的删除呼叫记录.注册一个内容观察者，观察呼叫记录的变化
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(new Handler(), incomingNumber));
				}
				break;
			}

		}

	}

	/**
	 * 呼叫记录内容观察者
	 * 
	 * @author bingoogol@sina.com
	 * 
	 */
	private class CallLogObserver extends ContentObserver {
		private String mIncomingNumber;

		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.mIncomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			Logger.i("CallLogObserver", "观察到了呼叫记录内容变化...");
			deleteCallLog(mIncomingNumber);
			// 内容观察者已经完成了使命，取消内容观察者的注册
			getContentResolver().unregisterContentObserver(this);
			super.onChange(selfChange);
		}

	}

	/**
	 * 挂断电话. 调用系统隐藏的api 挂断电话
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void endCall() {
		// 1.获取电话服务的代理对象 IBinder
		try {
			Class clazz = getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getMethod("getService", new Class[] { String.class });
			IBinder iBinder = (IBinder) method.invoke(null, new Object[] { TELEPHONY_SERVICE });
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除呼叫记录
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver reslover = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		reslover.delete(uri, "number=?", new String[] { incomingNumber });
	}
}
