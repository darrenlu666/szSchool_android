package com.dt5000.ischool.adapter.student;

import java.util.Date;
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
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.student.AttendancePictureActivity;
import com.dt5000.ischool.entity.Attendance;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 考勤列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年10月9日 上午10:46:31
 * @ClassInfo com.dt5000.ischool.adapter.student.AttendanceListAdapter
 * @Description
 */
public class AttendanceListAdapter extends BaseAdapter {

	private List<Attendance> list;
	private LayoutInflater myInflater;
	private Context context;
	private User user;

	public AttendanceListAdapter(Context ctx, List<Attendance> list) {
		this.context = ctx;
		this.list = list;
		user = User.getUser(context);
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
			convertView = myInflater.inflate(
					R.layout.view_list_item_attendance, null);
			viewHolder.txt_date = (TextView) convertView
					.findViewById(R.id.txt_date);
			viewHolder.txt_in = (TextView) convertView
					.findViewById(R.id.txt_in);
			viewHolder.txt_out = (TextView) convertView
					.findViewById(R.id.txt_out);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Attendance attendance = list.get(position);

		Date date = TimeUtil.parseFullTime(attendance.getTransactionTime());
		viewHolder.txt_date
				.setText(TimeUtil.yearMonthDayFormat(date.getTime()));

		viewHolder.txt_in.setText(user.getRealName() + "在 "
				+ TimeUtil.hourMinuteFormat(date.getTime()) + " 打卡");
		viewHolder.txt_in.setOnClickListener(new TxtClickListener(attendance));

		if (position == 0) {
			viewHolder.txt_date.setVisibility(View.VISIBLE);
		} else {
			Attendance lastAttendance = list.get(position - 1);
			Date lastDate = TimeUtil.parseFullTime(lastAttendance
					.getTransactionTime());
			if (TimeUtil.yearMonthDayFormat(lastDate.getTime()).equals(
					TimeUtil.yearMonthDayFormat(date.getTime()))) {
				viewHolder.txt_date.setVisibility(View.GONE);
			} else {
				viewHolder.txt_date.setVisibility(View.VISIBLE);
			}
		}

		return convertView;
	}

	private class TxtClickListener implements OnClickListener {
		private Attendance attendance;

		public TxtClickListener(Attendance attendance) {
			this.attendance = attendance;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, AttendancePictureActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("attendance", attendance);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}

	static final class ViewHolder {
		TextView txt_date;
		TextView txt_in;
		TextView txt_out;
	}

}
