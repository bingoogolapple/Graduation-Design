package com.bingoogol.frogcare.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.bingoogol.frogcare.R;
import com.bingoogol.frogcare.service.PhoneLocationService;
import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;

/**
 * 用户手机防盗模块的短信广播接收者
 * 
 * @author bingoogol@sina.com
 * 
 */
public class TheftReceiver extends BroadcastReceiver {

	private static final String TAG = "TheftReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (SpUtil.getBoolean(Constants.spkey.PHONE_THEFT, false)) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				if (sender.length() == 14) {
					sender = sender.substring(3);
				}
				String body = smsMessage.getMessageBody();
				Logger.i(TAG, "开启手机防盗功能时 短信到来 发送者：" + sender + " 短信内容：" + body);
				String resetCommand = SpUtil.getString(Constants.spkey.REMOTE_THEFT_COMMAND, "") + ":" + SpUtil.getString(Constants.spkey.APPLOCK_PWD, "");
				// 如果是安全手机发来的短信，则检查短信内容是否是手机防盗命令
				if (sender.equals(SpUtil.getString(Constants.spkey.SAFE_PHONE_NUMBER, ""))) {
					if (SpUtil.getString(Constants.spkey.ALARM_COMMAND, "").equals(body)) {
						abortBroadcast();
						playAlarm(context);
						lockScreenAndResetScreenPwd(context);
					} else if ("location".equals(body)) {
						Logger.i(TAG, "返回手机位置");
						Intent phoneLocationService = new Intent(context, PhoneLocationService.class);
						context.startService(phoneLocationService);
						abortBroadcast();
						String lastLocation = SpUtil.getString(Constants.spkey.LAST_LOCATION, "");
						if (!TextUtils.isEmpty(lastLocation)) {
							SmsManager sm = SmsManager.getDefault();
							sm.sendTextMessage(sender, null, lastLocation, null, null);
						}
					} else if ("wipedata".equals(body)) {
						abortBroadcast();
						Logger.i(TAG, "清除数据");
						// DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
						// if (dpm.isAdminActive(new ComponentName(context, MyAdminReceiver.class))) {
						// dpm.wipeData(0);
						// }
					}
				} else if (resetCommand.equals(body)) {
					abortBroadcast();
					Logger.i(TAG, "修改安全手机号码");
					SpUtil.putString(Constants.spkey.SAFE_PHONE_NUMBER, sender);
				}
			}
		}
	}

	/**
	 * 播放报警音乐
	 * 
	 * @param context
	 */
	private void playAlarm(Context context) {
		final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);

		Logger.i(TAG, "播放报警音乐");
		MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
		player.setLooping(false);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
			}
		});
	}

	/**
	 * 重置手机锁屏密码，并锁定屏幕
	 * 
	 * @param context
	 */
	private void lockScreenAndResetScreenPwd(Context context) {
		Logger.i(TAG, "远程锁屏");
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		if (dpm.isAdminActive(new ComponentName(context, MyAdminReceiver.class))) {
			dpm.resetPassword(SpUtil.getString(Constants.spkey.REMOTE_SCREEN_LOCK_PWD, "123456"), 0);
			dpm.lockNow();
		}
	}
}