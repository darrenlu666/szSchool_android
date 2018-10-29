package com.dt5000.ischool.adapter.teacher;

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
import com.dt5000.ischool.entity.AttendancePicture;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 考勤照片列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年10月12日 上午11:01:25
 * @ClassInfo 
 *            com.dt5000.ischool.adapter.teacher.AttendancePictureGridListAdapter
 * @Description
 */
public class AttendancePictureGridListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<AttendancePicture> list;
	private ImageLoader imageLoader;

	public AttendancePictureGridListAdapter(Context ctx,
			List<AttendancePicture> datas) {
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
					R.layout.view_grid_list_item_attendance_picture, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.img.setImageResource(R.color.graylit9);
		}

		AttendancePicture attendancePicture = list.get(position);

		imageLoader.displayImage(attendancePicture.getPhotoUrl(), holder.img);
		holder.txt_time.setText(attendancePicture.getTime());

		return convertView;
	}

	static class ViewHolder {
		ImageView img;
		TextView txt_time;
	}

}
