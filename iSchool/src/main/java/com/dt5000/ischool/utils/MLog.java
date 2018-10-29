package com.dt5000.ischool.utils;

import com.dt5000.ischool.BuildConfig;

import android.util.Log;

/**
 * Log信息辅助类
 * 
 * @author 周锋
 * @Date 2014-5-30
 * @ClassInfo com.dt5000.ischool.utils.zf-MLog.java
 * @Description
 */
public class MLog {

	/** 通用后台信息过滤Tag */
	private static final String TAG = "COMMON_LOG-->";

	/**
	 * 后台打印提示级别的信息
	 * 
	 * @param msg
	 *            待打印信息
	 */
	public static void i(String msg) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, msg);
		}
	}

	/**
	 * 后台打印调试级别的信息
	 * 
	 * @param msg
	 *            待打印信息
	 */
	public static void d(String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * 后台打印所有级别的信息
	 * 
	 * @param msg
	 *            待打印信息
	 */
	public static void v(String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, msg);
		}
	}

}
