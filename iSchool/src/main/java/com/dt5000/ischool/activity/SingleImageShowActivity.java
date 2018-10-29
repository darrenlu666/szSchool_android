package com.dt5000.ischool.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.dt5000.ischool.widget.photoview.PhotoView;
import com.dt5000.ischool.widget.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 单图片显示页面
 * 
 * @author 周锋
 * @date 2016年1月14日 下午4:04:44
 * @ClassInfo com.dt5000.ischool.activity.SingleImageShowActivity
 * @Description
 */
public class SingleImageShowActivity extends Activity {

	private LinearLayout lLayout_back;
	private LinearLayout lLayout_download_pic;
	private PhotoView img;

	private ImageLoader imageLoader;
	private String imgUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_image_show);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_download_pic = (LinearLayout) findViewById(R.id.lLayout_download_pic);
		img = (PhotoView) findViewById(R.id.img);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SingleImageShowActivity.this.finish();
			}
		});

		// 点击保存图片
		lLayout_download_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SingleImageShowActivity.this)
						.setTitle("是否保存到图片？")
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										loadImage(imgUrl);
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 点击图片退出
		img.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				SingleImageShowActivity.this.finish();
			}
		});
	}

	private void init() {
		imgUrl = getIntent().getExtras().getString("url");
		MLog.i("图片地址：" + imgUrl);

		imageLoader = ImageLoaderUtil.createSimple(this);

		imageLoader.displayImage(imgUrl, img);
	}

	private void loadImage(String imgUrl) {
		imageLoader.loadImage(imgUrl, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				new Thread(new DownLoadPicRunnable(loadedImage)).start();
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				MToast.show(SingleImageShowActivity.this, "保存失败", MToast.SHORT);
			}
		});
	}

	private class DownLoadPicRunnable implements Runnable {
		private Bitmap bitmap;

		public DownLoadPicRunnable(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			File file = ImageUtil.saveBitmapToDCIM(bitmap);
			Message message = handler.obtainMessage();
			if (file.exists() && file.canRead() && file.length() > 0) {
				message.what = FlagCode.SUCCESS;
				message.obj = file;
			} else {
				message.what = FlagCode.FAIL;
			}
			handler.sendMessage(message);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FlagCode.SUCCESS:
				File file = (File) msg.obj;

				String realFilePath = PictureUtil.getPath(
						SingleImageShowActivity.this, Uri.fromFile(file));
				MLog.i("保存的图片真实路径：" + realFilePath);

				Uri uri = Uri.fromFile(new File(realFilePath));
				MLog.i("保存的图片uri： " + uri);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					MLog.i("当前手机版本4.4及以上...");
					Intent scanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
					sendBroadcast(scanIntent);
				} else {
					MLog.i("当前手机版本4.4以下...");
					Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
					sendBroadcast(intent);
				}

				MToast.show(SingleImageShowActivity.this, "已保存至系统相册",
						MToast.SHORT);
				break;
			case FlagCode.FAIL:
				MToast.show(SingleImageShowActivity.this, "保存失败", MToast.SHORT);
				break;
			}
		};
	};

}
