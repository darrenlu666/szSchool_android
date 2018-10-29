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
import com.dt5000.ischool.entity.Score;

/**
 * 成绩列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年1月28日 下午4:34:17
 * @ClassInfo com.dt5000.ischool.adapter.teacher.ScoreListAdapter
 * @Description
 */
public class ScoreListAdapter extends BaseAdapter {

	private List<Score> list;
	private LayoutInflater myInflater;
	private Context context;

	public ScoreListAdapter(Context ctx, List<Score> list) {
		this.context = ctx;
		this.list = list;
		myInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
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
			convertView = myInflater.inflate(R.layout.view_list_item_score,
					null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_score = (TextView) convertView
					.findViewById(R.id.txt_score);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Score score = list.get(position);

		viewHolder.txt_name.setText(score.getExamName());
		viewHolder.txt_time.setText(score.getDate());

		// double[] scores = score.getScores();
		// String[] subjects = score.getSubjects();
		// String scoreStr = "";
		// for (int i = 0; i < scores.length; i++) {
		// if (scores[i] > 0) {
		// scoreStr += subjects[i] + ":" + scores[i] + "    ";
		// }
		// }
		viewHolder.txt_score.setText(score.getSubscore());

		return convertView;
	}

	static final class ViewHolder {
		TextView txt_name;
		TextView txt_time;
		TextView txt_score;
	}

}
