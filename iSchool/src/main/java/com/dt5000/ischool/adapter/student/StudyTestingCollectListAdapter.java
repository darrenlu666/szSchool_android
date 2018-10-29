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
import com.dt5000.ischool.entity.StudyTestCollect;

/**
 * 自主学习自测评估试题收藏列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月21日 上午11:14:06
 * @ClassInfo com.dt5000.ischool.adapter.teacher.ClassMsgListAdapter
 * @Description
 */
public class StudyTestingCollectListAdapter extends BaseAdapter {

	private List<StudyTestCollect> list;
	private Context context;
	private LayoutInflater myInflater;

	public StudyTestingCollectListAdapter(Context ctx,
			List<StudyTestCollect> list) {
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = myInflater.inflate(
					R.layout.view_list_item_study_testing_collect, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_index = (TextView) convertView
					.findViewById(R.id.txt_index);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		StudyTestCollect testCollect = list.get(position);

		viewHolder.txt_name.setText(testCollect.getName());

		int sortIndex = testCollect.getSortIndex();
		if (sortIndex >= 1) {// 存在sortIndex表示错题难题，显示收藏的题号
			viewHolder.txt_index.setVisibility(View.VISIBLE);
			viewHolder.txt_index.setText("第" + sortIndex + "题");
		} else {// 套题不显示题号
			viewHolder.txt_index.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		private TextView txt_name;
		private TextView txt_index;
	}

}
