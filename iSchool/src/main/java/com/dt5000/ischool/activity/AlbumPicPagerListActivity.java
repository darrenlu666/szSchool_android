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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.AlbumPicPagerListAdapter;
import com.dt5000.ischool.entity.AlbumImageItem;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 班级相册图片浏览页面
 * 
 * @author 周锋
 * @date 2016年2月3日 下午2:29:55
 * @ClassInfo com.dt5000.ischool.activity.AlbumPicPagerListActivity
 * @Description
 */
public class AlbumPicPagerListActivity extends Activity {

	private ViewPager viewpager_album;
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_download_pic;
	private TextView txt_page_num;

	private List<AlbumImageItem> albumImageList;
	private AlbumPicPagerListAdapter albumPicPagerListAdapter;
	private String curPicUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_pic_pager_list);

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
				AlbumPicPagerListActivity.this.finish();
			}
		});

		// 点击下载图片
		lLayout_download_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(AlbumPicPagerListActivity.this)
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
				txt_page_num.setText((position + 1) + "/"
						+ albumImageList.size());
				AlbumImageItem albumImageItem = albumImageList.get(position);
				curPicUrl = albumImageItem.getQiniuUrlLarge();

				// 当前图片地址，新旧版图片地址不一样，老版后台返回图片最后的数字串，新版直接返回完整图片地址
				// if (CheckUtil.stringIsBlank(albumImageItem.getQiniuUrl())) {
				// curPicUrl = UrlProtocol.ALBUM_IMAGE
				// + albumImageItem.getImageUrl();
				// } else {
				// curPicUrl = albumImageItem.getQiniuUrlLarge();
				// }
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
		Bundle extras = getIntent().getExtras();
		int index = extras.getInt("index");
		albumImageList = (List<AlbumImageItem>) extras
				.getSerializable("albumImageList");

		// 设置适配器
		albumPicPagerListAdapter = new AlbumPicPagerListAdapter(
				AlbumPicPagerListActivity.this, albumImageList);
		viewpager_album.setAdapter(albumPicPagerListAdapter);
		viewpager_album.setCurrentItem(index);
		txt_page_num.setText((index + 1) + "/" + albumImageList.size());
		curPicUrl = albumImageList.get(index).getQiniuUrlLarge();

		// 当前图片地址，新旧版图片地址不一样，老版后台返回图片最后的数字串，新版直接返回完整图片地址
		// if (CheckUtil.stringIsBlank(albumImageList.get(index).getQiniuUrl()))
		// {
		// curPicUrl = UrlProtocol.ALBUM_IMAGE
		// + albumImageList.get(index).getImageUrl();
		// } else {
		// curPicUrl = albumImageList.get(index).getQiniuUrlLarge();
		// }
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
						MToast.show(AlbumPicPagerListActivity.this, "保存失败",
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
						AlbumPicPagerListActivity.this, Uri.fromFile(file));
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

				MToast.show(AlbumPicPagerListActivity.this, "已保存至系统相册",
						MToast.SHORT);
				break;
			case FlagCode.FAIL:
				MToast.show(AlbumPicPagerListActivity.this, "保存失败",
						MToast.SHORT);
				break;
			}
		};
	};

}
