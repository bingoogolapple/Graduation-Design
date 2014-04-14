package com.bingoogol.frogcare.util;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value = Scope.UNIQUE)
public interface ISharedPreferences {
	@DefaultBoolean(false)
	boolean autoUpdate();

	@DefaultString("")
	String appLockPwd();
}