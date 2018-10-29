package com.dt5000.ischool.utils.afinal.bitmap.core;

import android.graphics.Bitmap;

public interface IMemoryCache {

	public void put(String key, Bitmap bitmap);

	public Bitmap get(String key);

	public void evictAll();

	public void remove(String key);

}
