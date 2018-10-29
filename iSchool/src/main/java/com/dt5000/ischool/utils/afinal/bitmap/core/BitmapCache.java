package com.dt5000.ischool.utils.afinal.bitmap.core;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.dt5000.ischool.utils.afinal.bitmap.core.BytesBufferPool.BytesBuffer;
import com.dt5000.ischool.utils.afinal.bitmap.core.DiskCache.LookupRequest;
import com.dt5000.ischool.utils.afinal.utils.Utils;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

public class BitmapCache {

	// Ĭ�ϵ��ڴ滺���С
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 8; // 8MB

	// Ĭ�ϵĴ��̻����С
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB
	private static final int DEFAULT_DISK_CACHE_COUNT = 1000 * 10; // �����ͼƬ����

	// BitmapCache��һЩĬ������
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;

	private DiskCache mDiskCache;
	private IMemoryCache mMemoryCache;
	private ImageCacheParams mCacheParams;

	public BitmapCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	/**
	 * ��ʼ�� ͼƬ����
	 * 
	 * @param cacheParams
	 */
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;

		// �Ƿ������ڴ滺��
		if (mCacheParams.memoryCacheEnabled) {
			// �Ƿ����������ڴ�
			if (mCacheParams.recycleImmediately)
				mMemoryCache = new SoftMemoryCacheImpl(
						mCacheParams.memCacheSize);
			else
				mMemoryCache = new BaseMemoryCacheImpl(
						mCacheParams.memCacheSize);
		}

		// �Ƿ�����sdcard����
		if (cacheParams.diskCacheEnabled) {
			try {
				String path = mCacheParams.diskCacheDir.getAbsolutePath();
				mDiskCache = new DiskCache(path, mCacheParams.diskCacheCount,
						mCacheParams.diskCacheSize, false);
			} catch (IOException e) {
				// ignore.
			}
		}
	}

	/**
	 * ���ͼƬ���ڴ滺����
	 * 
	 * @param url
	 *            Url ��ַ
	 * @param bitmap
	 *            ͼƬ���
	 */
	public void addToMemoryCache(String url, Bitmap bitmap) {
		if (url == null || bitmap == null) {
			return;
		}
		mMemoryCache.put(url, bitmap);
	}

	/**
	 * ��� ��ݵ�sdcard������
	 * 
	 * @param url
	 *            url��ַ
	 * @param data
	 *            �����Ϣ
	 */
	public void addToDiskCache(String url, byte[] data) {
		if (mDiskCache == null || url == null || data == null) {
			return;
		}
		// Add to disk cache
		byte[] key = Utils.makeKey(url);
		long cacheKey = Utils.crc64Long(key);
		ByteBuffer buffer = ByteBuffer.allocate(key.length + data.length);
		buffer.put(key);
		buffer.put(data);
		synchronized (mDiskCache) {
			try {
				mDiskCache.insert(cacheKey, buffer.array());
			} catch (IOException ex) {
				// ignore.
			}
		}

	}

	/**
	 * ��sdcard�л�ȡ�ڴ滺��
	 * 
	 * @param url
	 *            ͼƬurl��ַ
	 * @param buffer
	 *            ��仺����
	 * @return �Ƿ���ͼƬ
	 */
	public boolean getImageData(String url, BytesBuffer buffer) {
		if (mDiskCache == null)
			return false;

		byte[] key = Utils.makeKey(url);
		long cacheKey = Utils.crc64Long(key);
		try {
			LookupRequest request = new LookupRequest();
			request.key = cacheKey;
			request.buffer = buffer.data;
			synchronized (mDiskCache) {
				if (!mDiskCache.lookup(request))
					return false;
			}
			if (Utils.isSameKey(key, request.buffer)) {
				buffer.data = request.buffer;
				buffer.offset = key.length;
				buffer.length = request.length - buffer.offset;
				return true;
			}
		} catch (IOException ex) {
			// ignore.
		}
		return false;
	}

	/**
	 * ���ڴ滺���л�ȡbitmap.
	 */
	public Bitmap getBitmapFromMemoryCache(String data) {
		if (mMemoryCache != null)
			return mMemoryCache.get(data);
		return null;
	}

	/**
	 * ����ڴ滺���sdcard����
	 */
	public void clearCache() {
		clearMemoryCache();
		clearDiskCache();
	}

	public void clearDiskCache() {
		if (mDiskCache != null)
			mDiskCache.delete();
	}

	public void clearMemoryCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
		}
	}

	public void clearCache(String key) {
		clearMemoryCache(key);
		clearDiskCache(key);
	}

	public void clearDiskCache(String url) {
		addToDiskCache(url, new byte[0]);
	}

	public void clearMemoryCache(String key) {
		if (mMemoryCache != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		if (mDiskCache != null)
			mDiskCache.close();
	}

	/**
	 * Image cache ��������Ϣ.
	 */
	public static class ImageCacheParams {
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public int diskCacheCount = DEFAULT_DISK_CACHE_COUNT;
		public File diskCacheDir;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean recycleImmediately = true;

		public ImageCacheParams(File diskCacheDir) {
			this.diskCacheDir = diskCacheDir;
		}

		public ImageCacheParams(String diskCacheDir) {
			this.diskCacheDir = new File(diskCacheDir);
		}

		/**
		 * ���û����С
		 * 
		 * @param context
		 * @param percent
		 *            �ٷֱȣ�ֵ�ķ�Χ���� 0.05 �� 0.8֮��
		 */
		public void setMemCacheSizePercent(Context context, float percent) {
			if (percent < 0.05f || percent > 0.8f) {
				throw new IllegalArgumentException(
						"setMemCacheSizePercent - percent must be "
								+ "between 0.05 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * getMemoryClass(context) * 1024
					* 1024);
		}

		public void setMemCacheSize(int memCacheSize) {
			this.memCacheSize = memCacheSize;
		}

		public void setDiskCacheSize(int diskCacheSize) {
			this.diskCacheSize = diskCacheSize;
		}

		private static int getMemoryClass(Context context) {
			return ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
		}

		public void setDiskCacheCount(int diskCacheCount) {
			this.diskCacheCount = diskCacheCount;
		}

		public void setRecycleImmediately(boolean recycleImmediately) {
			this.recycleImmediately = recycleImmediately;
		}

	}

}
