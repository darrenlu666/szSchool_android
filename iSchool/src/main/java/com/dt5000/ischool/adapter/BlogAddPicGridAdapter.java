package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 新增博客页面图片列表适配器
 * 
 * @author 周锋
 * @date 2016年2月2日 下午7:36:56
 * @ClassInfo com.dt5000.ischool.adapter.BlogAddPicGridAdapter
 * @Description
 */
public class BlogAddPicGridAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<String> list;
	private ImageLoader imageLoader;

	public BlogAddPicGridAdapter(Context ctx, List<String> datas) {
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
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.view_grid_item_blog_add_pic, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String string = list.get(position);

		if ("icon_add".equals(string)) {
			holder.img.setImageResource(R.drawable.a_pic_more);
		} else {
			imageLoader.loadImage("file://" + string, new ImageSize(100, 100),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							holder.img.setImageBitmap(loadedImage);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}
					});
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView img;
	}

}
