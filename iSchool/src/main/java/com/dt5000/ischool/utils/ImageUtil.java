package com.dt5000.ischool.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;

/**
 * 图片处理辅助类
 * 
 * @author 周锋
 * @date 2016年4月21日 上午10:50:48
 * @ClassInfo com.dt5000.ischool.utils.ImageUtil
 * @Description
 */
public class ImageUtil {

	/**
	 * 从图片文件读取Bitmap并缩放到指定尺寸，使用ThumbnailUtils来缩放
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @param width
	 *            指定宽度
	 * @param height
	 *            指定高度
	 * @return
	 */
	public static Bitmap decodeBitmapToFixSize(String filePath, int width,
			int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

		int realHeight = options.outHeight;
		int realWidth = options.outWidth;
		int scaleAccordWidth = realWidth / width;
		int scaleAccordHeight = realHeight / height;
		int scale = 1;
		if (scaleAccordWidth < scaleAccordHeight) {
			scale = scaleAccordWidth;
		} else {
			scale = scaleAccordHeight;
		}
		if (scale <= 0) {
			scale = 1;
		}

		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);

		return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	}

	/**
	 * 根据当前时间命名图片文件
	 * 
	 * @return
	 */
	public static String getPhotoFileNameWithCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss", Locale.CHINA);
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 将Bitmap缩放到指定尺寸，比使用inSampleSize（采样率）来缩放占内存，但是尺寸可以精确
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 将Bitmap图像保存到系统相册
	 * 
	 * @param bmp
	 * @return
	 */
	public static File saveBitmapToDCIM(Bitmap bmp) {
		File dcimDir = new File(Environment.getExternalStorageDirectory(),
				"/DCIM/Camera/");
		if (!dcimDir.exists()) {
			dcimDir.mkdir();
		}

		File savefile = new File(dcimDir, getPhotoFileNameWithCurrentTime());

		try {
			FileOutputStream fos = new FileOutputStream(savefile);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return savefile;
	}

	/**
	 * 从本地资源图片中读取Bitmap
	 * 
	 * @param resId
	 * @param context
	 * @return
	 */
	public Bitmap decodeBitmapFromResource(int resId, Context context) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 从图片文件读取Bitmap并缩放到限定尺寸，使用ThumbnailUtils来缩放，尺寸精确效率一般
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @param maxSize
	 *            最大长度或宽度
	 * @return
	 */
	public static Bitmap decodeBitmapWithThumbnailUtils(String filePath,
			int maxSize) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 设置只解码图片的边距，不会占用内存
		opt.inJustDecodeBounds = true;

		// 解码图片文件
		BitmapFactory.decodeFile(filePath, opt);

		// 获得图片的实际高和宽
		int realWidth = opt.outWidth;
		int realHeight = opt.outHeight;
		MLog.i("实际高度：" + realHeight);
		MLog.i("实际宽度：" + realWidth);

		// 判断是否需要缩放
		int fixWidth = realWidth;
		int fixHeight = realHeight;
		if (realHeight > maxSize || realWidth > maxSize) {
			if (realHeight >= realWidth) {
				fixHeight = maxSize;
				fixWidth = (int) (fixHeight * (realWidth * 1.0 / realHeight));
			} else {
				fixWidth = maxSize;
				fixHeight = (int) (fixWidth * (realHeight * 1.0 / realWidth));
			}
		}
		MLog.i("期望高度：" + fixHeight);
		MLog.i("期望宽度：" + fixWidth);

		// 读取资源图片
		opt.inJustDecodeBounds = false;
		Bitmap sourceBitmap = BitmapFactory.decodeFile(filePath);

		return ThumbnailUtils.extractThumbnail(sourceBitmap, fixWidth,
				fixHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	}

	/**
	 * 从图片文件读取Bitmap并缩放到限定尺寸，使用inSampleSize（采样率）来缩放，效率高但是尺寸无法精确
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @param maxSize
	 *            最大长度或宽度
	 * @return
	 */
	public static Bitmap decodeBitmapWithInSampleSize(String filePath,
			int maxSize) {
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);

			BitmapFactory.Options opt = new BitmapFactory.Options();
			// 设置只解码图片的边距，不会占用内存
			opt.inJustDecodeBounds = true;

			// 解码图片文件
			BitmapFactory.decodeFile(filePath, opt);

			// 获得图片的实际高和宽
			int realWidth = opt.outWidth;
			int realHeight = opt.outHeight;

			// 判断是否需要缩放
			if (realHeight > maxSize || realWidth > maxSize) {
				// 计算缩放比，1表示原比例，2表示原来的四分之一，以此类推
				int scaleAccordWidth = realWidth / maxSize;
				int scaleAccordHeight = realHeight / maxSize;
				opt.inSampleSize = scaleAccordHeight > scaleAccordWidth ? scaleAccordHeight
						: scaleAccordWidth;
			}

			// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
			opt.inPreferredConfig = Bitmap.Config.RGB_565;

			// 标志复原
			opt.inJustDecodeBounds = false;

			return BitmapFactory.decodeStream(bs, null, opt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将Bitmap转成字节数组，代码来源：微信分享SDK
	 * 
	 * @param bmp
	 * @param needRecycle
	 * @return
	 */
	public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 70, output);
		
		byte[] result = output.toByteArray();
		
		if (needRecycle) {
			bmp.recycle();
		}
		
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
