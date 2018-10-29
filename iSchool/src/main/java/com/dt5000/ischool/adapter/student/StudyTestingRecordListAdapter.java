package com.dt5000.ischool.adapter.student;

import java.sql.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyTestRecord;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 自主学习自测评估做题记录列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月27日 上午11:50:13
 * @ClassInfo com.dt5000.ischool.adapter.student.StudyTestingRecordListAdapter
 * @Description
 */
public class StudyTestingRecordListAdapter extends BaseAdapter {

	private List<StudyTestRecord> list;
	private Context context;
	private LayoutInflater inflater;

	public StudyTestingRecordListAdapter(Context ctx, List<StudyTestRecord> list) {
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.view_list_item_study_testing_record, null);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		StudyTestRecord testRecord = list.get(position);

		viewHolder.txt_name.setText(testRecord.getName());

		viewHolder.txt_time.setText(TimeUtil.messageFormat(new Date(testRecord
				.getCreateTime())));

		return convertView;
	}

	private static class ViewHolder {
		private TextView txt_time;
		private TextView txt_name;
	}

}
