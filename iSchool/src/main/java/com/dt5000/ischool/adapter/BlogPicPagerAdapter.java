package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.widget.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 博客图片浏览适配器
 * 
 * @author 周锋
 * @date 2016年2月2日 下午7:05:22
 * @ClassInfo com.dt5000.ischool.adapter.BlogPicPagerAdapter
 * @Description
 */
public class BlogPicPagerAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<String> list;
	private ImageLoader imageLoader;

	public BlogPicPagerAdapter(Context ctx, List<String> datas) {
		this.context = ctx;
		this.list = datas;
		this.inflater = LayoutInflater.from(context);
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
		String picInfo = list.get(position);

		View view = inflater.inflate(R.layout.view_viewpager_item_blog_pic,
				null);
		PhotoView photoView = (PhotoView) view
				.findViewById(R.id.img_photo_view);
		imageLoader.displayImage(picInfo, photoView);

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
