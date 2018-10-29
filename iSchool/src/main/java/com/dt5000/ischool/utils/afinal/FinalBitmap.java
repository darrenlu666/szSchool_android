package com.dt5000.ischool.utils.afinal;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.dt5000.ischool.utils.afinal.bitmap.core.BitmapCache;
import com.dt5000.ischool.utils.afinal.bitmap.core.BitmapDisplayConfig;
import com.dt5000.ischool.utils.afinal.bitmap.core.BitmapProcess;
import com.dt5000.ischool.utils.afinal.bitmap.display.Displayer;
import com.dt5000.ischool.utils.afinal.bitmap.display.SimpleDisplayer;
import com.dt5000.ischool.utils.afinal.bitmap.download.Downloader;
import com.dt5000.ischool.utils.afinal.bitmap.download.SimpleDownloader;
import com.dt5000.ischool.utils.afinal.core.AsyncTask;
import com.dt5000.ischool.utils.afinal.utils.Utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FinalBitmap {
	private FinalBitmapConfig mConfig;
	private BitmapCache mImageCache;
	private BitmapProcess mBitmapProcess;
	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	private Context mContext;
	private boolean mInit = false ;
	private ExecutorService bitmapLoadAndDisplayExecutor;

	private static FinalBitmap mFinalBitmap;
	
	////////////////////////// config method start////////////////////////////////////
	private FinalBitmap(Context context) {
		mContext = context;
		mConfig = new FinalBitmapConfig(context);
		configDiskCachePath(Utils.getDiskCacheDir(context, "afinalCache").getAbsolutePath());//���û���·��
		configDisplayer(new SimpleDisplayer());//������ʾ��
		configDownlader(new SimpleDownloader());//����������
	}
	
	/**
	 * ����finalbitmap
	 * @param ctx
	 * @return
	 */
	public static synchronized FinalBitmap create(Context ctx){
		if(mFinalBitmap == null){
			mFinalBitmap = new FinalBitmap(ctx.getApplicationContext());
		}
		return mFinalBitmap;
	}
	
	
	
	/**
	 * ����ͼƬ���ڼ��ص�ʱ����ʾ��ͼƬ
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(bitmap);
		return this;
	}

	/**
	 * ����ͼƬ���ڼ��ص�ʱ����ʾ��ͼƬ
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(BitmapFactory.decodeResource(mContext.getResources(), resId));
		return this;
	}
	
	/**
	 * ����ͼƬ����ʧ��ʱ����ʾ��ͼƬ
	 * @param bitmap
	 */
	public FinalBitmap configLoadfailImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(bitmap);
		return this;
	}
	
	/**
	 * ����ͼƬ����ʧ��ʱ����ʾ��ͼƬ
	 * @param resId
	 */
	public FinalBitmap configLoadfailImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(mContext.getResources(), resId));
		return this;
	}
	
	
	/**
	 * ����Ĭ��ͼƬ��С�ĸ߶�
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxHeight(int bitmapHeight){
		mConfig.defaultDisplayConfig.setBitmapHeight(bitmapHeight);
		return this;
	}
	
	/**
	 * ����Ĭ��ͼƬ��С�Ŀ��
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxWidth(int bitmapWidth){
		mConfig.defaultDisplayConfig.setBitmapWidth(bitmapWidth);
		return this;
	}
	
	/**
	 * ����������������ͨ��ftp��������Э��ȥ�����ȡͼƬ��ʱ�������������
	 * @param downlader
	 * @return
	 */
	public FinalBitmap configDownlader(Downloader downlader){
		mConfig.downloader = downlader;
		return this;
	}
	
	/**
	 * ������ʾ������������ʾ�Ĺ������ʾ������
	 * @param displayer
	 * @return
	 */
	public FinalBitmap configDisplayer(Displayer displayer){
		mConfig.displayer = displayer;
		return this;
	}
	
	
	/**
	 * ���ô��̻���·��
	 * @param strPath
	 * @return
	 */
	public FinalBitmap configDiskCachePath(String strPath){
		if(!TextUtils.isEmpty(strPath)){
			mConfig.cachePath = strPath;
		}
		return this;
	}

	/**
	 * �����ڴ滺���С ����2MB������Ч
	 * @param size �����С
	 */
	public FinalBitmap configMemoryCacheSize(int size){
		mConfig.memCacheSize = size;
		return this;
	}
	
	/**
	 * ����Ӧ�������APK���ڴ�İٷֱȣ����ȼ�����configMemoryCacheSize
	 * @param percent �ٷֱȣ�ֵ�ķ�Χ���� 0.05 �� 0.8֮��
	 */
	public FinalBitmap configMemoryCachePercent(float percent){
		mConfig.memCacheSizePercent = percent;
		return this;
	}
	
	/**
	 * ���ô��̻����С 5MB ������Ч
	 * @param size
	 */
	public FinalBitmap configDiskCacheSize(int size){
		mConfig.diskCacheSize = size;
		return this;
	} 
	
	/**
	 * ���ü���ͼƬ���̲߳�������
	 * @param size
	 */
	public FinalBitmap configBitmapLoadThreadSize(int size){
		if(size >= 1)
			mConfig.poolSize = size;
		return this;
	}
	
	/**
	 * �����Ƿ���������ͼƬ��Դ
	 * @param recycleImmediately
	 * @return
	 */
	public FinalBitmap configRecycleImmediately(boolean recycleImmediately){
		mConfig.recycleImmediately = recycleImmediately;
		return this;
	} 
	
	/**
	 * ��ʼ��finalBitmap
	 * @return
	 */
	private FinalBitmap init(){
		
		if( ! mInit ){
			
			BitmapCache.ImageCacheParams imageCacheParams = new BitmapCache.ImageCacheParams(mConfig.cachePath);
			if(mConfig.memCacheSizePercent>0.05 && mConfig.memCacheSizePercent<0.8){
				imageCacheParams.setMemCacheSizePercent(mContext, mConfig.memCacheSizePercent);
			}else{
				if(mConfig.memCacheSize > 1024 * 1024 * 2){
					imageCacheParams.setMemCacheSize(mConfig.memCacheSize);	
				}else{
					//����Ĭ�ϵ��ڴ滺���С
					imageCacheParams.setMemCacheSizePercent(mContext, 0.3f);
				}
			}
			if(mConfig.diskCacheSize > 1024 * 1024 * 5)
				imageCacheParams.setDiskCacheSize(mConfig.diskCacheSize);
			
			imageCacheParams.setRecycleImmediately(mConfig.recycleImmediately);
			//init Cache
			mImageCache = new BitmapCache(imageCacheParams);
			
			//init Executors
			bitmapLoadAndDisplayExecutor = Executors.newFixedThreadPool(mConfig.poolSize,new ThreadFactory() {
				@Override
	            public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					// �����̵߳����ȼ������߳��Ⱥ�˳��ִ�У�����Խ�ߣ�����cpuִ�е�ʱ��Խ�ࣩ
					t.setPriority(Thread.NORM_PRIORITY - 1);
					return t;
				}
			});
			
			//init BitmapProcess
			mBitmapProcess = new BitmapProcess(mConfig.downloader,mImageCache);
			
			mInit = true ;
		}
		
		return this;
	}
	
	////////////////////////// config method end////////////////////////////////////
	
	public void display(View imageView,String uri){
		doDisplay(imageView,uri,null);
	}

	

	public void display(View imageView,String uri,int imageWidth,int imageHeight){
		BitmapDisplayConfig displayConfig = configMap.get(imageWidth+"_"+imageHeight);
		if(displayConfig==null){
			displayConfig = getDisplayConfig();
			displayConfig.setBitmapHeight(imageHeight);
			displayConfig.setBitmapWidth(imageWidth);
			configMap.put(imageWidth+"_"+imageHeight, displayConfig);
		}
		
		doDisplay(imageView,uri,displayConfig);
	}
	
	public void display(View imageView,String uri,Bitmap loadingBitmap){
		BitmapDisplayConfig displayConfig = configMap.get(String.valueOf(loadingBitmap));
		if(displayConfig==null){
			displayConfig = getDisplayConfig();
			displayConfig.setLoadingBitmap(loadingBitmap);
			configMap.put(String.valueOf(loadingBitmap), displayConfig);
		}
		
		doDisplay(imageView,uri,displayConfig);
	}
	
	
	public void display(View imageView,String uri,Bitmap loadingBitmap,Bitmap laodfailBitmap){
		BitmapDisplayConfig displayConfig = configMap.get(String.valueOf(loadingBitmap)+"_"+String.valueOf(laodfailBitmap));
		if(displayConfig==null){
			displayConfig = getDisplayConfig();
			displayConfig.setLoadingBitmap(loadingBitmap);
			displayConfig.setLoadfailBitmap(laodfailBitmap);
			configMap.put(String.valueOf(loadingBitmap)+"_"+String.valueOf(laodfailBitmap), displayConfig);
		}
		
		doDisplay(imageView,uri,displayConfig);
	}
	
	public void display(View imageView,String uri,int imageWidth,int imageHeight,Bitmap loadingBitmap,Bitmap laodfailBitmap){
		BitmapDisplayConfig displayConfig = configMap.get(imageWidth+"_"+imageHeight+"_"+String.valueOf(loadingBitmap)+"_"+String.valueOf(laodfailBitmap));
		if(displayConfig==null){
			displayConfig = getDisplayConfig();
			displayConfig.setBitmapHeight(imageHeight);
			displayConfig.setBitmapWidth(imageWidth);
			displayConfig.setLoadingBitmap(loadingBitmap);
			displayConfig.setLoadfailBitmap(laodfailBitmap);
			configMap.put(imageWidth+"_"+imageHeight+"_"+String.valueOf(loadingBitmap)+"_"+String.valueOf(laodfailBitmap), displayConfig);
		}
		
		doDisplay(imageView,uri,displayConfig);
	}
	
	
	public void display(View imageView,String uri,BitmapDisplayConfig config){
		doDisplay(imageView,uri,config);
	}
	
	
	@SuppressWarnings("deprecation")
	private void doDisplay(View imageView, String uri, BitmapDisplayConfig displayConfig) {
		if(!mInit ){
			init();
		}
		
		if (TextUtils.isEmpty(uri) || imageView == null) {
			return;
		}
		
		if(displayConfig == null)
			displayConfig = mConfig.defaultDisplayConfig;
	
		Bitmap bitmap = null;
	
		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemoryCache(uri);
		}
	
		if (bitmap != null) {
			if(imageView instanceof ImageView){
				((ImageView)imageView).setImageBitmap(bitmap);
			}else{
				imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			}
			
			
		}else if (checkImageTask(uri, imageView)) {
			final BitmapLoadAndDisplayTask task = new BitmapLoadAndDisplayTask(imageView, displayConfig );
			//����Ĭ��ͼƬ
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), displayConfig.getLoadingBitmap(), task);
	       
			if(imageView instanceof ImageView){
				((ImageView)imageView).setImageDrawable(asyncDrawable);
			}else{
				imageView.setBackgroundDrawable(asyncDrawable);
			}
	        
	        task.executeOnExecutor(bitmapLoadAndDisplayExecutor, uri);
	    }
	}
	
	private HashMap<String, BitmapDisplayConfig> configMap = new HashMap<String, BitmapDisplayConfig>();
	
	
	private BitmapDisplayConfig getDisplayConfig(){
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setAnimation(mConfig.defaultDisplayConfig.getAnimation());
		config.setAnimationType(mConfig.defaultDisplayConfig.getAnimationType());
		config.setBitmapHeight(mConfig.defaultDisplayConfig.getBitmapHeight());
		config.setBitmapWidth(mConfig.defaultDisplayConfig.getBitmapWidth());
		config.setLoadfailBitmap(mConfig.defaultDisplayConfig.getLoadfailBitmap());
		config.setLoadingBitmap(mConfig.defaultDisplayConfig.getLoadingBitmap());
		return config;
	}
	

	private void clearCacheInternalInBackgroud() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
	}
	
	
	
	private void clearDiskCacheInBackgroud(){
		if (mImageCache != null) {
			mImageCache.clearDiskCache();
		}
	}
	
	
	private void clearCacheInBackgroud(String key){
		if (mImageCache != null) {
			mImageCache.clearCache(key);
		}
	}
	
	private void clearDiskCacheInBackgroud(String key){
		if (mImageCache != null) {
			mImageCache.clearDiskCache(key);
		}
	}
	

    /**
     * ִ�й�˷�����,FinalBitmap�Ļ����Ѿ�ʧЧ,����ͨ��FinalBitmap.create()��ȡ�µ�ʵ��
     * @author fantouch
     */
	private void closeCacheInternalInBackgroud() {
		if (mImageCache != null) {
			mImageCache.close();
			mImageCache = null;
            mFinalBitmap = null;
		}
	}

	/**
	 * �������bitmap
	 * @param data
	 * @return
	 */
	private Bitmap processBitmap(String uri,BitmapDisplayConfig config) {
		if (mBitmapProcess != null) {
			return mBitmapProcess.getBitmap(uri,config);
		}
		return null;
	}
	
	/**
	 * �ӻ��棨�ڴ滺��ʹ��̻��棩��ֱ�ӻ�ȡbitmap��ע��������io��������ò�Ҫ����ui�߳�ִ��
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromCache(String key){
		Bitmap bitmap  = getBitmapFromMemoryCache(key);
		if(bitmap == null)
			bitmap = getBitmapFromDiskCache(key);
		
		return bitmap;
	}
	
	/**
	 * ���ڴ滺���л�ȡbitmap
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemoryCache(String key){
		return mImageCache.getBitmapFromMemoryCache(key);
	}
	
	/**
	 * �Ӵ��̻����л�ȡbitmap����ע��������io��������ò�Ҫ����ui�߳�ִ��
	 * @param key 
	 * @return
	 */
	public Bitmap getBitmapFromDiskCache(String key){
		return getBitmapFromDiskCache(key,null);
	}
	
	public Bitmap getBitmapFromDiskCache(String key,BitmapDisplayConfig config){
		return mBitmapProcess.getFromDisk(key, config);
	}
	
	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}
	
	/**
     * activity onResume��ʱ���������������ü���ͼƬ�̼߳���
     */
    public void onResume(){
    	setExitTasksEarly(false);
    }
    
    /**
     * activity onPause��ʱ�����������������߳���ͣ
     */
    public void onPause() {
        setExitTasksEarly(true);
    }
    
    /**
     * activity onDestroy��ʱ���������������ͷŻ���
     * ִ�й�˷�����,FinalBitmap�Ļ����Ѿ�ʧЧ,����ͨ��FinalBitmap.create()��ȡ�µ�ʵ��
     * 
     * @author fantouch
     */
    public void onDestroy() {
        closeCache();
    }

	/**
	 * ������л��棨���̺��ڴ棩
	 */
	public void clearCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR);
	}
	
	/**
	 * ���key���ָ�����ڴ滺��
	 * @param key
	 */
	public void clearCache(String key) {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_KEY,key);
	}
	
	/**
	 * ����
	 */
	public void clearMemoryCache() {
		if(mImageCache!=null)
			mImageCache.clearMemoryCache();
	}
	
	/**
	 * ���key���ָ�����ڴ滺��
	 * @param key
	 */
	public void clearMemoryCache(String key) {
		if(mImageCache!=null)
			mImageCache.clearMemoryCache(key);
	}
	
	
	/**
	 * �����̻���
	 */
	public void clearDiskCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_DISK);
	}
	
	/**
	 * ���key���ָ�����ڴ滺��
	 * @param key
	 */
	public void clearDiskCache(String key) {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_KEY_IN_DISK,key);
	}
	

    /**
     * �رջ���
     * ִ�й�˷�����,FinalBitmap�Ļ����Ѿ�ʧЧ,����ͨ��FinalBitmap.create()��ȡ�µ�ʵ��
     * @author fantouch
     */
	public void closeCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLOSE);
	}

	/**
	 * �˳����ڼ��ص��̣߳������˳���ʱ����ôʷ���
	 * @param exitTasksEarly
	 */
	public void exitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		if(exitTasksEarly)
			pauseWork(false);//����ͣ���߳̽���
	}

	/**
	 * ��ͣ���ڼ��ص��̣߳�����listview����gridview���ڻ�����ʱ�����ôʷ���
	 * @param pauseWork trueֹͣ��ͣ�̣߳�false�����߳�
	 */
	public void pauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	    
	private static BitmapLoadAndDisplayTask getBitmapTaskFromImageView(View imageView) {
		if (imageView != null) {
			Drawable drawable = null;
			if(imageView instanceof ImageView){
				drawable = ((ImageView)imageView).getDrawable();
			}else{
				drawable = imageView.getBackground();
			}
			
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}


	    /**
	     * ��� imageView���Ƿ��Ѿ����߳�������
	     * @param data
	     * @param imageView
	     * @return true û�� false ���߳���������
	     */
	public static boolean checkImageTask(Object data, View imageView) {
			final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

			if (bitmapWorkerTask != null) {
				final Object bitmapData = bitmapWorkerTask.data;
				if (bitmapData == null || !bitmapData.equals(data)) {
					bitmapWorkerTask.cancel(true);
				} else {
					// ͬһ���߳��Ѿ���ִ��
					return false;
				}
			}
			return true;
		}
		
		
		private static class AsyncDrawable extends BitmapDrawable {
			private final WeakReference<BitmapLoadAndDisplayTask> bitmapWorkerTaskReference;

			public AsyncDrawable(Resources res, Bitmap bitmap,BitmapLoadAndDisplayTask bitmapWorkerTask) {
				super(res, bitmap);
				bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
						bitmapWorkerTask);
			}

			public BitmapLoadAndDisplayTask getBitmapWorkerTask() {
				return bitmapWorkerTaskReference.get();
			}
		}


	private class CacheExecutecTask extends AsyncTask<Object, Void, Void> {
		public static final int MESSAGE_CLEAR = 1;
		public static final int MESSAGE_CLOSE = 2;
		public static final int MESSAGE_CLEAR_DISK = 3;
		public static final int MESSAGE_CLEAR_KEY = 4;
		public static final int MESSAGE_CLEAR_KEY_IN_DISK = 5;
		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternalInBackgroud();
				break;
			case MESSAGE_CLOSE:
				closeCacheInternalInBackgroud();
				break;
			case MESSAGE_CLEAR_DISK:
				clearDiskCacheInBackgroud();
				break;
			case MESSAGE_CLEAR_KEY:
				clearCacheInBackgroud(String.valueOf(params[1]));
				break;
			case MESSAGE_CLEAR_KEY_IN_DISK:
				clearDiskCacheInBackgroud(String.valueOf(params[1]));
				break;
			}
			return null;
		}
	}
	
	/**
	 * bitmap������ʾ���߳�
	 * @author michael yang
	 */
	private class BitmapLoadAndDisplayTask extends AsyncTask<Object, Void, Bitmap> {
		private Object data;
		private final WeakReference<View> imageViewReference;
		private final BitmapDisplayConfig displayConfig;

		public BitmapLoadAndDisplayTask(View imageView,BitmapDisplayConfig config) {
			imageViewReference = new WeakReference<View>(imageView);
			displayConfig = config;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			if (bitmap == null && !isCancelled()&& getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = processBitmap(dataString,displayConfig);
			}
			
			if(bitmap!=null){
				try {
					mImageCache.addToMemoryCache(dataString, bitmap);
				} catch (Exception e) {
					//
				}
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
			}

			// �ж��̺߳͵�ǰ��imageview�Ƿ���ƥ��
			final View imageView = getAttachedImageView();
			if (bitmap != null && imageView != null) {
				mConfig.displayer.loadCompletedisplay(imageView,bitmap,displayConfig);			
			}else if(bitmap == null && imageView!=null ){
				mConfig.displayer.loadFailDisplay(imageView, displayConfig.getLoadfailBitmap());
			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll(); 
			}
		}

		/**
		 * ��ȡ�߳�ƥ���imageView,��ֹ��������������
		 * @return
		 */
		private View getAttachedImageView() {
			final View imageView = imageViewReference.get();
			final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}
	}
	
	
	/**
	 * @title ������Ϣ
	 * @description FinalBitmap��������Ϣ
	 * @company ̽�������繤����(www.tsz.net)
	 * @author michael Young (www.YangFuhai.com)
	 * @version 1.0
	 * @created 2012-10-28
	 */
	private class FinalBitmapConfig {
		public String cachePath;
		 public Displayer displayer;
		 public Downloader downloader;
		 public BitmapDisplayConfig defaultDisplayConfig;
		 public float memCacheSizePercent;//����ٷֱȣ�androidϵͳ�����ÿ��apk�ڴ�Ĵ�С
		 public int memCacheSize;//�ڴ滺��ٷֱ�
		 public int diskCacheSize;//���̰ٷֱ�
		 public int poolSize = 3;//Ĭ�ϵ��̳߳��̲߳�������
		 public boolean recycleImmediately = true;//�Ƿ����������ڴ�
		
		 public FinalBitmapConfig(Context context) {
				defaultDisplayConfig = new BitmapDisplayConfig();
				
				defaultDisplayConfig.setAnimation(null);
				defaultDisplayConfig.setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);
				
				//����ͼƬ����ʾ���ߴ磨Ϊ��Ļ�Ĵ�С,Ĭ��Ϊ��Ļ��ȵ�1/2��
				DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
				int defaultWidth = (int)Math.floor(displayMetrics.widthPixels/2);
				defaultDisplayConfig.setBitmapHeight(defaultWidth);
				defaultDisplayConfig.setBitmapWidth(defaultWidth);
				
		}
	}
	
}
