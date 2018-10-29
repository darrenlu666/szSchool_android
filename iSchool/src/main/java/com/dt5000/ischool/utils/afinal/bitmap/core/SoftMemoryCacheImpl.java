package com.dt5000.ischool.utils.afinal.bitmap.core;

import java.lang.ref.SoftReference;

import com.dt5000.ischool.utils.afinal.utils.Utils;

import android.graphics.Bitmap;

public class SoftMemoryCacheImpl implements IMemoryCache {

	private final LruMemoryCache<String, SoftReference<Bitmap>> mMemoryCache;

	public SoftMemoryCacheImpl(int size) {
		mMemoryCache = new LruMemoryCache<String, SoftReference<Bitmap>>(size) {
			@Override
			protected int sizeOf(String key, SoftReference<Bitmap> sBitmap) {
				final Bitmap bitmap = sBitmap == null ? null : sBitmap.get();
				if (bitmap == null)
					return 1;
				return Utils.getBitmapSize(bitmap);
			}
		};
	}

	@Override
	public void put(String key, Bitmap bitmap) {
		mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap));
	}

	@Override
	public Bitmap get(String key) {
		SoftReference<Bitmap> memBitmap = mMemoryCache.get(key);
		if (memBitmap != null) {
			return memBitmap.get();
		}
		return null;
	}

	@Override
	public void evictAll() {
		mMemoryCache.evictAll();
	}

	@Override
	public void remove(String key) {
		mMemoryCache.remove(key);
	}

}
