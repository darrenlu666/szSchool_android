package com.dt5000.ischool.adapter.student;

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
import com.dt5000.ischool.entity.SubjectItem;

/**
 * 自主学习科目列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月21日 下午7:20:07
 * @ClassInfo com.dt5000.ischool.adapter.student.StudySubjectListAdapter
 * @Description
 */
public class StudySubjectListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<SubjectItem> list;

	public StudySubjectListAdapter(Context ctx, List<SubjectItem> list) {
		this.context = ctx;
		this.list = list;
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
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.view_list_item_study_subject, null);
			holder.txt_subject_name = (TextView) convertView
					.findViewById(R.id.txt_subject_name);
			holder.img_subject = (ImageView) convertView
					.findViewById(R.id.img_subject);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SubjectItem subjectItem = list.get(position);

		holder.txt_subject_name.setText(subjectItem.getSubjectName());
		holder.img_subject.setBackgroundResource(subjectItem.getSubjectImg());

		return convertView;
	}

	static class ViewHolder {
		ImageView img_subject;
		TextView txt_subject_name;
	}

}
