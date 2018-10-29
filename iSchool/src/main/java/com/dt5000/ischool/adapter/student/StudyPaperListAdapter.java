package com.dt5000.ischool.adapter.student;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyPaper;

/**
 * 自主学习自测评估试卷列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月25日 下午3:53:18
 * @ClassInfo com.dt5000.ischool.adapter.student.StudyPaperListAdapter
 * @Description
 */
public class StudyPaperListAdapter extends BaseAdapter {

	private Context context;
	private List<StudyPaper> list;
	private LayoutInflater myInflater;

	public StudyPaperListAdapter(Context ctx, List<StudyPaper> list) {
		this.context = ctx;
		this.list = list;
		myInflater = LayoutInflater.from(context);
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
			convertView = myInflater.inflate(
					R.layout.view_list_item_study_paper, null);
			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		StudyPaper studyPaper = list.get(position);
		holder.txt_name.setText(studyPaper.getName());

		return convertView;
	}

	static class ViewHolder {
		TextView txt_name;
	}

}
