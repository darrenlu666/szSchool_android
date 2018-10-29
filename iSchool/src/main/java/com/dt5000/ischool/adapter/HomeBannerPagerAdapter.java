package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.Banner;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 首页轮播列表适配器
 * 
 * @author 周锋
 * @date 2016年2月25日 上午11:19:44
 * @ClassInfo com.dt5000.ischool.adapter.HomeBannerPagerAdapter
 * @Description
 */
public class HomeBannerPagerAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Banner> list;
	private ImageLoader imageLoader;

	public HomeBannerPagerAdapter(Context ctx, List<Banner> list) {
		this.context = ctx;
		this.list = list;
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
		Banner banner = list.get(position);

		View view = inflater.inflate(R.layout.view_viewpager_item_home_banner, null);
		ImageView img = (ImageView) view.findViewById(R.id.img);
		imageLoader.displayImage(banner.getImageUrl(), img);

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
