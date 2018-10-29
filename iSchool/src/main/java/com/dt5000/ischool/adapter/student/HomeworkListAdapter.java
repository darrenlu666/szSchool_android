package com.dt5000.ischool.adapter.student;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.student.HomeworkSubjectListActivity;
import com.dt5000.ischool.entity.Homework;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 作业列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月8日 下午2:37:30
 * @ClassInfo com.dt5000.ischool.adapter.student.HomeworkListAdapter
 * @Description
 */
public class HomeworkListAdapter extends BaseAdapter {

	private List<Homework> list;
	private LayoutInflater myInflater;
	private Context context;

	public HomeworkListAdapter(Context ctx, List<Homework> list) {
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
			convertView = myInflater.inflate(R.layout.view_list_item_homework,
					null);
			viewHolder.lLayout_up = (LinearLayout) convertView
					.findViewById(R.id.lLayout_up);
			viewHolder.imgSubject = (ImageView) convertView
					.findViewById(R.id.imgSubject);
			viewHolder.txtSubject = (TextView) convertView
					.findViewById(R.id.txtsubject);
			viewHolder.txtCreateBy = (TextView) convertView
					.findViewById(R.id.txtcreateby);
			viewHolder.txtCreateTime = (TextView) convertView
					.findViewById(R.id.txtcreateTime);
			viewHolder.txtWorkName = (TextView) convertView
					.findViewById(R.id.txtworkname);
			viewHolder.txtContent = (TextView) convertView
					.findViewById(R.id.txtcontent);
			viewHolder.txt_top_time = (TextView) convertView
					.findViewById(R.id.txt_top_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Homework homework = list.get(position);
		final Integer subjectId = homework.getSubjectId();

		viewHolder.imgSubject.setImageResource(homework.getSubjectPicId());
		viewHolder.txtSubject.setText(homework.getSubjectName());
		viewHolder.txtCreateBy.setText(homework.getCreateBy());
		viewHolder.txtWorkName.setText(homework.getName());
		viewHolder.txtContent.setText(homework.getSubContent());

		// 获取当条作业发布日期的long数值
		Long thisTimeLong = homework.getCreateTime();
		// 获取当条作业的格式化时间年月日和时分
		String thisYMD = TimeUtil.yearMonthDayChineseFormat(thisTimeLong);
		String thisHM = TimeUtil.hourMinuteFormat(thisTimeLong);
		// 设置时分显示
		viewHolder.txtCreateTime.setText(thisHM);
		// 设置年月日显示且第一条显示头部时间
		viewHolder.txt_top_time.setText(thisYMD);

		if (position == 0) {
			viewHolder.lLayout_up.setVisibility(View.VISIBLE);
		}

		// 从第二条起需进行显示判断
		if (position >= 1) {
			Homework lastIhomework = list.get(position - 1);
			// 获取上一条作业发布日期的long数值
			Long lastTimeLong = lastIhomework.getCreateTime();
			// 获取上一条作业的格式化时间年月日
			String lastYMD = TimeUtil.yearMonthDayChineseFormat(lastTimeLong);
			// 判断是否一样
			if (lastYMD.equals(thisYMD)) {
				viewHolder.lLayout_up.setVisibility(View.GONE);
			} else {
				viewHolder.lLayout_up.setVisibility(View.VISIBLE);
			}
		}

		// 设置图标点击监听,跳转到当前科目的所有作业列表界面
		viewHolder.imgSubject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						HomeworkSubjectListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("subjectId", subjectId);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	static final class ViewHolder {
		private LinearLayout lLayout_up;
		private ImageView imgSubject;
		private TextView txt_top_time;
		private TextView txtSubject;
		private TextView txtCreateBy;
		private TextView txtCreateTime;
		private TextView txtWorkName;
		private TextView txtContent;
	}

}
