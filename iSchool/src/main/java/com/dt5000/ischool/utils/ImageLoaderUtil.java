package com.dt5000.ischool.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 创建ImageLoader对象的辅助类
 * 
 * @author 周锋
 * @date 2015年9月8日 下午3:58:33
 * @ClassInfo com.dt5000.ischool.utils.ImageLoaderUtil
 * @Description
 */
public class ImageLoaderUtil {

	private static ImageLoader imageLoader;

	/**
	 * 单例模式创建ImageLoader对象；<br>
	 * 磁盘缓存路径为默认的appCacheDir+"/uil-images"；<br>
	 * 开启内存缓存，没有自定义容量，大小为APP的1/8可用内存；<br>
	 * 开启磁盘缓存，自定义容量和缓存数量，大小为50M，最大缓存文件数量为100；<br>
	 * 
	 * @param context
	 * @return
	 */
	public static ImageLoader createSimple(Context context) {
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.diskCacheSize(50 * 1024 * 1024)
					.diskCacheFileCount(100)
					.defaultDisplayImageOptions(
							getCommonDisplayImageOptions(context)).build();

			imageLoader.init(config);
		}
		return imageLoader;
	}

	public static DisplayImageOptions getCommonDisplayImageOptions(
			Context context) {
		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true).build();
	}

	/**
	 * 加载的Bitmap含有圆角
	 * 
	 * @param context
	 * @param cornerRadiusPixels
	 *            圆角半径，360表示圆形
	 * @return
	 */
	public static DisplayImageOptions getRoundedBitmapDisplayImageOptions(
			Context context, int cornerRadiusPixels) {
		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels))
				.build();
	}

}
