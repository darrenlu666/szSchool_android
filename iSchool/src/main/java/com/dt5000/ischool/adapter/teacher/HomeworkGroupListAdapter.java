package com.dt5000.ischool.adapter.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.GroupItem;

import java.util.List;

/**
 * 
 * @author 周锋
 * @date 2016年1月14日 上午10:20:42
 * @ClassInfo com.dt5000.ischool.adapter.teacher.HomeworkGroupListAdapter
 * @Description
 */
public class HomeworkGroupListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<GroupItem> list;

	public HomeworkGroupListAdapter(Context ctx, List<GroupItem> datas) {
		this.context = ctx;
		this.list = datas;
		inflater = LayoutInflater.from(context);
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
			convertView = inflater.inflate(
					R.layout.view_list_item_homework_class, null);
			holder = new ViewHolder();
			holder.txt_group_name = (TextView) convertView
					.findViewById(R.id.txt_class_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GroupItem classItem = list.get(position);
		holder.txt_group_name.setText(classItem.getGroupName());

		return convertView;
	}

	static class ViewHolder {
		TextView txt_group_name;
	}

}
