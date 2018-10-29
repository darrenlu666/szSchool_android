package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AlbumItem;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 班级相册列表适配器
 * 
 * @author 周锋
 * @date 2016年2月3日 上午10:38:01
 * @ClassInfo com.dt5000.ischool.adapter.AlbumListAdapter
 * @Description
 */
public class AlbumListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<AlbumItem> list;
	private ImageLoader imageLoader;

	public AlbumListAdapter(Context ctx, List<AlbumItem> datas) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.view_list_item_album, null);
			holder = new ViewHolder();
			holder.txtAlbumName = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.imgCoverPic = (ImageView) convertView
					.findViewById(R.id.img_cover);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AlbumItem albumItem = list.get(position);

		holder.txtAlbumName.setText(albumItem.getAlbumName() + "("
				+ albumItem.getSize() + ")");

		imageLoader.displayImage(albumItem.getQiniuCoverUrl(),
				holder.imgCoverPic);

		return convertView;
	}

	static class ViewHolder {
		TextView txtAlbumName;
		ImageView imgCoverPic;
	}

}
