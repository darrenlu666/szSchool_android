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
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 帮助页面
 * 
 * @author 周锋
 * @date 2016年2月1日 上午9:17:03
 * @ClassInfo com.dt5000.ischool.activity.HelpActivity
 * @Description
 */
public class HelpActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private ImageView img_qr_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		initView();
		initListener();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("帮助");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		img_qr_code = (ImageView) findViewById(R.id.img_qr_code);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HelpActivity.this.finish();
			}
		});

		// 点击长按保存二维码
		img_qr_code.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(HelpActivity.this)
						.setMessage("保存二维码？")
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										saveQrCode();
									}
								}).setNegativeButton("取消", null).show();

				return true;
			}
		});
	}

	private void saveQrCode() {
		String qrCodeDrawablePath = "drawable://" + R.drawable.a_swipe;

		ImageLoaderUtil.createSimple(this).loadImage(qrCodeDrawablePath,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						new Thread(new DownLoadPicRunnable(loadedImage))
								.start();
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						MToast.show(HelpActivity.this, "保存失败", MToast.SHORT);
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

				String realFilePath = PictureUtil.getPath(HelpActivity.this,
						Uri.fromFile(file));
				MLog.i("保存的图片真实路径：" + realFilePath);

				Uri uri = Uri.fromFile(new File(realFilePath));
				MLog.i("保存的图片uri： " + uri);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					MLog.i("当前手机版本4.4及以上，使用ACTION_MEDIA_SCANNER_SCAN_FILE");
					Intent scanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
					sendBroadcast(scanIntent);
				} else {
					MLog.i("当前手机版本4.4以下，使用ACTION_MEDIA_MOUNTED");
					Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
					sendBroadcast(intent);
				}

				MToast.show(HelpActivity.this, "已保存至系统相册", MToast.SHORT);
				break;
			case FlagCode.FAIL:
				MToast.show(HelpActivity.this, "保存失败", MToast.SHORT);
				break;
			}
		};
	};

}
