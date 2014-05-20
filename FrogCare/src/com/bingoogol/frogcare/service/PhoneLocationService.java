package com.bingoogol.frogcare.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.bingoogol.frogcare.util.Constants;
import com.bingoogol.frogcare.util.Logger;
import com.bingoogol.frogcare.util.SpUtil;

/**
 * 手机定位服务，主要是在找回手机时使用
 * 
 * @author bingoogol@sina.com
 * 
 */
public class PhoneLocationService extends Service {
	private static final String TAG = "PhoneLocationService";
	private LocationManager mLocationManager;
	private PhoneLocationListener mPhoneLocationListener;

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 查询条件
		Criteria criteria = new Criteria();
		// 精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 关心海拔
		criteria.setAltitudeRequired(true);
		// 运行产生开销
		criteria.setCostAllowed(true);
		// 允许最大电量请求
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		String provider = mLocationManager.getBestProvider(criteria, true);
		Logger.i(TAG, "最好的提供者:" + provider);
		mPhoneLocationListener = new PhoneLocationListener();
		mLocationManager.requestLocationUpdates(provider, 0, 0, mPhoneLocationListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		mLocationManager.removeUpdates(mPhoneLocationListener);
		mPhoneLocationListener = null;
		super.onDestroy();
	}

	/**
	 * 手机位置监听器
	 * 
	 * @author bingoogol@sina.com
	 * 
	 */
	private class PhoneLocationListener implements LocationListener {

		// 当位置变化时调用的方法
		@Override
		public void onLocationChanged(Location location) {
			String longitude = "Longitude:" + location.getLongitude();
			String latitude = "Latitude:" + location.getLatitude();
			String accuacy = "Accuracy:" + location.getAccuracy();
			SpUtil.putString(Constants.spkey.LAST_LOCATION, latitude + longitude + accuacy);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}
}
