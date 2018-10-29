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
import com.dt5000.ischool.entity.SubjectItem;

/**
 * 发布作业选择科目列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午2:29:01
 * @ClassInfo 
 *            com.dt5000.ischool.adapter.teacher.HomeworkAddChooseSubjectListAdapter
 * @Description
 */
public class HomeworkAddChooseSubjectListAdapter extends BaseAdapter {

	private List<SubjectItem> list;
	private LayoutInflater myInflater;
	private Context context;

	public HomeworkAddChooseSubjectListAdapter(Context ctx,
			List<SubjectItem> subjectList) {
		this.list = subjectList;
		this.context = ctx;
		this.myInflater = LayoutInflater.from(context);
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = myInflater.inflate(
					R.layout.view_list_item_homework_add_choose_subject, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txt_name.setText(list.get(position).getSubjectName());

		return convertView;
	}

	static class ViewHolder {
		private TextView txt_name;
	}

}
