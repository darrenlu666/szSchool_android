package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.DiaryPic;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.widget.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 日记图片附件浏览列表适配器
 * 
 * @author 周锋
 * @date 2016年2月19日 下午4:04:01
 * @ClassInfo com.dt5000.ischool.adapter.DiaryPicPagerListAdapter
 * @Description
 */
public class DiaryPicPagerListAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<DiaryPic> list;
	private ImageLoader imageLoader;

	public DiaryPicPagerListAdapter(Context ctx, List<DiaryPic> datas) {
		this.context = ctx;
		this.list = datas;
		inflater = LayoutInflater.from(context);
		imageLoader = ImageLoaderUtil.createSimple(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@SuppressLint("InflateParams")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		DiaryPic diaryPic = list.get(position);
		String picUrl = diaryPic.getImageUrl();

		View view = inflater.inflate(R.layout.view_viewpager_item_diary_pic,
				null);
		PhotoView img_photo_view = (PhotoView) view
				.findViewById(R.id.img_photo_view);

		imageLoader.displayImage(picUrl, img_photo_view);

		container.addView(view);

		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
