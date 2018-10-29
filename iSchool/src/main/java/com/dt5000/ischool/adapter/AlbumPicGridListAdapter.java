package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AlbumImageItem;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 班级相册图片列表适配器
 * 
 * @author 周锋
 * @date 2016年2月3日 下午2:45:38
 * @ClassInfo com.dt5000.ischool.adapter.AlbumPicGridListAdapter
 * @Description
 */
public class AlbumPicGridListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<AlbumImageItem> list;
	private ImageLoader imageLoader;

	public AlbumPicGridListAdapter(Context ctx, List<AlbumImageItem> datas) {
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.view_grid_list_item_album_pic, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.img.setImageResource(R.color.graylit9);
		}

		AlbumImageItem albumImageItem = list.get(position);
		
		imageLoader.displayImage(albumImageItem.getQiniuUrl(), holder.img);

		// 新旧版图片地址不一样，老版后台返回图片最后的数字串，新版直接返回完整图片地址
		// if (CheckUtil.stringIsBlank(albumImageItem.getQiniuUrl())) {
		// imageLoader.displayImage(UrlProtocol.ALBUM_IMAGE_SMALL
		// + albumImageItem.getImageUrl(), holder.img);
		// } else {
		// imageLoader.displayImage(albumImageItem.getQiniuUrl(), holder.img);
		// }

		return convertView;
	}

	static class ViewHolder {
		ImageView img;
	}

}
