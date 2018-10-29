package com.dt5000.ischool.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.BlogAddChoosePicGridAdapter;
import com.dt5000.ischool.entity.BlogAddChoosePic;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;

/**
 * 新增博客时选择手机本地图片页面
 * 
 * @author 周锋
 * @date 2016年2月2日 下午7:40:38
 * @ClassInfo com.dt5000.ischool.activity.BlogAddChoosePicActivity
 * @Description
 */
public class BlogAddChoosePicActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private GridView grid_album;

	private BlogAddChoosePicGridAdapter blogAddChoosePicGridAdapter;
	private List<BlogAddChoosePic> picList;// 手机里所有图片列表
	private List<BlogAddChoosePic> currentPicList;// 当前需要加载的图片列表
	private int lastVisibleItem = 0;// 当前GridView最后一个可见项的索引
	private int currentIndex = 0;// 标识当前加载到第几张图片
	private boolean hasMorePic = true;// 标识是否有更多图片供加载

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_add_choose_pic);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择图片");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("确定");
		grid_album = (GridView) findViewById(R.id.grid_album);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BlogAddChoosePicActivity.this.finish();
			}
		});

		// 点击确定，将选择后的图片数据返回给上一页
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (picList != null && picList.size() > 0
						&& blogAddChoosePicGridAdapter != null) {
					List<BlogAddChoosePic> choosedPicList = blogAddChoosePicGridAdapter
							.getChoosedPicList();

					// 将未选择的图片从列表中移除
					List<String> newPicList = new ArrayList<String>();
					for (int i = 0; i < choosedPicList.size(); i++) {
						BlogAddChoosePic pic = choosedPicList.get(i);
						boolean isChoosed = pic.isChoose();
						if (isChoosed) {
							newPicList.add(pic.getPicPath());
						}
					}

					// 将数据返回给上一页
					if (newPicList == null || newPicList.size() <= 0) {
						DialogAlert
								.show(BlogAddChoosePicActivity.this, "请选择图片");
					} else if (newPicList.size() > 9) {
						DialogAlert.show(BlogAddChoosePicActivity.this,
								"最多上传9张图片");
					} else {
						MLog.i("选择后的图片数量：" + newPicList.size());
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("picList",
								(Serializable) newPicList);
						intent.putExtras(bundle);
						BlogAddChoosePicActivity.this.setResult(RESULT_OK,
								intent);
						BlogAddChoosePicActivity.this.finish();
					}
				}
			}
		});

		// 相册活动到底部加载更多
		grid_album.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE
						&& lastVisibleItem == (blogAddChoosePicGridAdapter
								.getCount() - 1)) {

					if (hasMorePic) {
						// 再次加载40张图片
						loadPage();

						// 更新适配器
						blogAddChoosePicGridAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
			}
		});
	}

	private void init() {
		currentPicList = new ArrayList<BlogAddChoosePic>();

		picList = getLocalPicList();

		if (picList != null && picList.size() > 0) {
			loadPage();

			blogAddChoosePicGridAdapter = new BlogAddChoosePicGridAdapter(
					BlogAddChoosePicActivity.this, currentPicList);
			grid_album.setAdapter(blogAddChoosePicGridAdapter);
		}
	}

	/**
	 * 获取手机本地所有图片的地址列表
	 * 
	 * @return
	 */
	private List<BlogAddChoosePic> getLocalPicList() {
		List<BlogAddChoosePic> list = new ArrayList<BlogAddChoosePic>();

		ContentResolver contentResolver = getContentResolver();
		String[] projection = new String[] { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DATE_MODIFIED };
		Cursor cursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		int cursorCount = cursor.getCount();
		MLog.i("查询到手机本地图片数量：" + cursorCount);
		for (int i = 0; i < cursorCount; i++) {
			String picPath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			long lastModTime = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
			MLog.i("图片地址：" + picPath);
			MLog.i("图片最后修改时间：" + lastModTime);
			list.add(new BlogAddChoosePic(picPath, lastModTime, false));
			cursor.moveToNext();
		}
		cursor.close();

		// 按图片的最后修改时间进行从大到小排序
		Collections.sort(list, new PicListCompare());
		MLog.i("排序后-------------------------------------");
		for (int i = 0; i < list.size(); i++) {
			BlogAddChoosePic pic = list.get(i);
			MLog.i("图片地址：" + pic.getPicPath());
			MLog.i("图片最后修改时间：" + pic.getLastModTime());
		}

		return list;
	}

	/**
	 * 每次加载40张图
	 */
	private void loadPage() {
		// 从上次记录的下标开始
		for (int i = currentIndex; i < picList.size(); i++) {
			hasMorePic = false;
			currentPicList.add(picList.get(i));

			if ((i + 1) % 40 == 0) {
				currentIndex = i + 1;// 记录当前加载到第几张图片
				hasMorePic = true;
				break;
			}
		}
	}

	private class PicListCompare implements Comparator<BlogAddChoosePic> {
		@Override
		public int compare(BlogAddChoosePic lhs, BlogAddChoosePic rhs) {
			if (lhs.getLastModTime() > rhs.getLastModTime()) {
				return -1;
			} else if (lhs.getLastModTime() < rhs.getLastModTime()) {
				return 1;
			}
			return 0;
		}
	}

}
