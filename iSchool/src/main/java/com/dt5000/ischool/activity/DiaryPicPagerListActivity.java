package com.dt5000.ischool.activity;

import java.io.File;
import java.util.List;

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
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.DiaryPicPagerListAdapter;
import com.dt5000.ischool.entity.DiaryPic;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 日记图片附件浏览页面
 * 
 * @author 周锋
 * @date 2016年2月19日 下午3:59:57
 * @ClassInfo com.dt5000.ischool.activity.DiaryPicPagerListActivity
 * @Description
 */
public class DiaryPicPagerListActivity extends Activity {

	private ViewPager viewpager_album;
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_download_pic;
	private TextView txt_page_num;

	private List<DiaryPic> attachList;
	private DiaryPicPagerListAdapter diaryPicPagerListAdapter;
	private String curPicUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_pic_pager_list);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_page_num = (TextView) findViewById(R.id.txt_page_num);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_download_pic = (LinearLayout) findViewById(R.id.lLayout_download_pic);
		viewpager_album = (ViewPager) findViewById(R.id.viewpager_album);
		viewpager_album.setPageMargin(30);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DiaryPicPagerListActivity.this.finish();
			}
		});

		// 点击下载图片
		lLayout_download_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DiaryPicPagerListActivity.this)
						.setTitle("是否保存到本地")
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										loadImage(curPicUrl);
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 页面切换监听
		viewpager_album.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				txt_page_num.setText((position + 1) + "/" + attachList.size());
				DiaryPic diaryPic = attachList.get(position);
				// 当前图片地址
				curPicUrl = diaryPic.getImageUrl();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		attachList = (List<DiaryPic>) getIntent().getExtras().getSerializable(
				"attachList");

		// 设置适配器
		diaryPicPagerListAdapter = new DiaryPicPagerListAdapter(
				DiaryPicPagerListActivity.this, attachList);
		viewpager_album.setAdapter(diaryPicPagerListAdapter);
		txt_page_num.setText("1/" + attachList.size());

		// 当前图片地址
		curPicUrl = attachList.get(0).getImageUrl();
	}

	private void loadImage(String imgUrl) {
		ImageLoaderUtil.createSimple(this).loadImage(imgUrl,
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
						MToast.show(DiaryPicPagerListActivity.this, "保存失败",
								MToast.SHORT);
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
			String dataUrl = MediaStore.Images.Media.insertImage(
					getContentResolver(), bitmap, "", "");
			Message message = handler.obtainMessage();
			if (CheckUtil.stringIsBlank(dataUrl)) {
				message.what = FlagCode.FAIL;
			} else {
				message.what = FlagCode.SUCCESS;
				message.obj = dataUrl;
			}
			handler.sendMessage(message);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FlagCode.SUCCESS:
				String dataUrl = (String) msg.obj;

				String realFilePath = PictureUtil.getPath(
						DiaryPicPagerListActivity.this, Uri.parse(dataUrl));
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

				MToast.show(DiaryPicPagerListActivity.this, "已保存至系统相册",
						MToast.SHORT);
				break;
			case FlagCode.FAIL:
				MToast.show(DiaryPicPagerListActivity.this, "保存失败",
						MToast.SHORT);
				break;
			}
		};
	};

}
