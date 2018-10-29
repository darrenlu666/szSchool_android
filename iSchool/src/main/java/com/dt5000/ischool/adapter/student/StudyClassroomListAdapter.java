package com.dt5000.ischool.adapter.student;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.PlayVideoActivity;
import com.dt5000.ischool.activity.student.StudyPracticeActivity;
import com.dt5000.ischool.entity.ClassroomVideo;

/**
 * 自主学习同步课堂列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月22日 下午1:28:46
 * @ClassInfo com.dt5000.ischool.adapter.student.StudyClassroomAdapter
 * @Description
 */
public class StudyClassroomListAdapter extends BaseAdapter {

	private List<ClassroomVideo> list;
	private LayoutInflater inflater;
	private Context context;

	public StudyClassroomListAdapter(Context ctx, List<ClassroomVideo> list) {
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
					R.layout.view_list_item_study_classroom, null);
			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.lLayout_to_video = (LinearLayout) convertView
					.findViewById(R.id.lLayout_to_video);
			holder.lLayout_to_practise = (LinearLayout) convertView
					.findViewById(R.id.lLayout_to_practise);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ClassroomVideo video = list.get(position);

		holder.txt_name.setText(video.getVideoName());
		holder.lLayout_to_video
				.setOnClickListener(new clickVideoListener(video));
		holder.lLayout_to_practise
				.setOnClickListener(new clickPractiseListener(video));

		return convertView;
	}

	class clickVideoListener implements OnClickListener {
		private ClassroomVideo video;

		public clickVideoListener(ClassroomVideo video) {
			this.video = video;
		}

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(context, PlayVideoActivity.class);
			intent.putExtra("videoUrl", video.getVideoUrl());
			intent.putExtra("videoId", video.getVideoId());
			intent.putExtra("videoName", video.getVideoName());
			context.startActivity(intent);
		}
	}

	class clickPractiseListener implements OnClickListener {
		private ClassroomVideo video;

		public clickPractiseListener(ClassroomVideo video) {
			this.video = video;
		}

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(context, StudyPracticeActivity.class);
			intent.putExtra("videoId", video.getVideoId());
			intent.putExtra("videoName", video.getVideoName());
			context.startActivity(intent);
		}
	}

	static class ViewHolder {
		TextView txt_name;
		LinearLayout lLayout_to_video;
		LinearLayout lLayout_to_practise;
	}

}