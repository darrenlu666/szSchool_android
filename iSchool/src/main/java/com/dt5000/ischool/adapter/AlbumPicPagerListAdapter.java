package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AlbumImageItem;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.widget.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 班级相册图片浏览列表适配器
 * 
 * @author 周锋
 * @date 2016年2月3日 下午2:50:32
 * @ClassInfo com.dt5000.ischool.adapter.AlbumPicPagerListAdapter
 * @Description
 */
public class AlbumPicPagerListAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<AlbumImageItem> list;
	private ImageLoader imageLoader;

	public AlbumPicPagerListAdapter(Context ctx, List<AlbumImageItem> datas) {
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
		AlbumImageItem picInfo = list.get(position);
		String picUrl = picInfo.getQiniuUrlLarge();
		MLog.i("当前图片地址：" + picUrl);

		// 新旧版图片地址不一样，老版后台返回图片最后的数字串，新版直接返回完整图片地址
		// String picUrl = "";
		// if (CheckUtil.stringIsBlank(picInfo.getQiniuUrl())) {
		// picUrl = UrlProtocol.ALBUM_IMAGE + picInfo.getImageUrl();
		// } else {
		// picUrl = picInfo.getQiniuUrlLarge();
		// }

		View view = inflater.inflate(R.layout.view_viewpager_item_album_pic,
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
