package com.dt5000.ischool.adapter.teacher;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ClassItem;

/**
 * 日记班级列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年2月18日 下午2:51:21
 * @ClassInfo com.dt5000.ischool.adapter.teacher.DiaryClassListAdapter
 * @Description
 */
public class DiaryClassListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<ClassItem> list;

	public DiaryClassListAdapter(Context ctx, List<ClassItem> datas) {
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
			convertView = inflater.inflate(R.layout.view_list_item_diary_class,
					null);
			holder = new ViewHolder();
			holder.txt_class_name = (TextView) convertView
					.findViewById(R.id.txt_class_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ClassItem classItem = list.get(position);
		holder.txt_class_name.setText(classItem.getClassName());

		return convertView;
	}

	static class ViewHolder {
		TextView txt_class_name;
	}

}