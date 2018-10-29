package com.dt5000.ischool.utils.afinal.bitmap.display;

import com.dt5000.ischool.utils.afinal.bitmap.core.BitmapDisplayConfig;

import android.graphics.Bitmap;
import android.view.View;

public interface Displayer {

	/**
	 * ͼƬ������� �ص��ĺ���
	 * 
	 * @param imageView
	 * @param bitmap
	 * @param config
	 */
	public void loadCompletedisplay(View imageView, Bitmap bitmap,
			BitmapDisplayConfig config);

	/**
	 * ͼƬ����ʧ�ܻص��ĺ���
	 * 
	 * @param imageView
	 * @param bitmap
	 */
	public void loadFailDisplay(View imageView, Bitmap bitmap);

}
