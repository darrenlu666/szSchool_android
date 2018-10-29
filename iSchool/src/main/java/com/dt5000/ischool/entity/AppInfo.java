package com.dt5000.ischool.entity;

import java.io.Serializable;

import android.os.Build;

/**
 * 软件更新信息实体类
 * 
 * @author 周锋
 * @date 2016年2月1日 下午4:50:19
 * @ClassInfo com.dt5000.ischool.entity.AppInfo
 * @Description
 */
public class AppInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String appName;
	private String apkName;
	private String verName;
	private String updateUrl;
	private boolean forced;
	private int verCode;

	public AppInfo() {
	}

	public AppInfo(String appName, String apkName, String verName,
			String updateUrl, boolean forced, int verCode) {
		this.appName = appName;
		this.apkName = apkName;
		this.verName = verName;
		this.updateUrl = updateUrl;
		this.forced = forced;
		this.verCode = verCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getVerName() {
		return verName;
	}

	public void setVerName(String verName) {
		this.verName = verName;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	/**
	 * MODE_MULTI_PROCESS这个值是一个标志， 在Android 2.3及以前，这个标志位都是默认开启的，
	 * 允许多个进程访问同一个SharedPrecferences对象。
	 * 而以后的Android版本，必须通过明确的将MODE_MULTI_PROCESS这个值传递给mode参数， 才能开启多进程访问。
	 * 所以：我们在获得SharedPreferences的时候，需要判断一下SDK的版本号：
	 */
	public static int getSPMODE() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) ? 4 : 1;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

}
