package com.dt5000.ischool.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

/**
 * 文件操作辅助类
 * 
 * @author 周锋
 * @date 2016年3月28日 上午10:34:24
 * @ClassInfo com.dt5000.ischool.utils.FileUtil
 * @Description
 */
public class FileUtil {

	/**
	 * 从SDCard读取文件为字节流
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] readFromSD(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取手机相册目录
	 * 
	 * @return
	 */
	public static File getCameraDir() {
		File camera_dir = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera/");
		if (!camera_dir.exists()) {
			camera_dir.mkdirs();
		}

		return camera_dir;
	}

	/**
	 * 获取手机下载目录
	 * 
	 * @return
	 */
	public static File getDownloadDir() {
		File download_dir = new File(Environment.getExternalStorageDirectory()
				+ "/Download/");
		if (!download_dir.exists()) {
			download_dir.mkdirs();
		}

		return download_dir;
	}

	/**
	 * 获取SDcard中项目缓存目录
	 * 
	 * @return
	 */
	public static File getISchoolCacheDir() {
		File ischool_cache_dir = new File(
				Environment.getExternalStorageDirectory() + "/ISchool/cache/");
		if (!ischool_cache_dir.exists()) {
			ischool_cache_dir.mkdirs();
		}

		return ischool_cache_dir;
	}

	public static String getDiskCacheDir(Context context) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return cachePath;
	}

}
