package com.dt5000.ischool.adapter.teacher;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.Vacate;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 考勤请假单列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年10月12日 上午11:35:28
 * @ClassInfo com.dt5000.ischool.adapter.teacher.AttendanceVacateListAdapter
 * @Description
 */
public class AttendanceVacateListAdapter extends BaseAdapter {

	private List<Vacate> list;
	private LayoutInflater myInflater;
	private Context context;

	public AttendanceVacateListAdapter(Context ctx, List<Vacate> list) {
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
			convertView = myInflater.inflate(
					R.layout.view_list_item_attendance_vacate_teacher, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_status = (TextView) convertView
					.findViewById(R.id.txt_status);
			viewHolder.txt_start_time = (TextView) convertView
					.findViewById(R.id.txt_start_time);
			viewHolder.txt_end_time = (TextView) convertView
					.findViewById(R.id.txt_end_time);
			viewHolder.txt_reason = (TextView) convertView
					.findViewById(R.id.txt_reason);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Vacate vacate = list.get(position);

		viewHolder.txt_time.setText(vacate.getSubmitTime());
		viewHolder.txt_name.setText(vacate.getRealName());
		viewHolder.txt_start_time.setText("开始时间："
				+ TimeUtil.yearMonthDayHourMinuteFormat(TimeUtil.parseFullTime(
						vacate.getStartTime()).getTime()));
		viewHolder.txt_end_time.setText("结束时间："
				+ TimeUtil.yearMonthDayHourMinuteFormat(TimeUtil.parseFullTime(
						vacate.getEndTime()).getTime()));
		viewHolder.txt_reason.setText("请假原因：" + vacate.getReason());

		String isAgree = vacate.getIsAgree();
		if ("0".equals(isAgree)) {
			viewHolder.txt_status.setText("未审核");
			viewHolder.txt_status.setTextColor(Color.parseColor("#929292"));
		} else if ("1".equals(isAgree)) {
			viewHolder.txt_status.setText("已确认");
			viewHolder.txt_status.setTextColor(Color.parseColor("#f19149"));
		} else if ("2".equals(isAgree)) {
			viewHolder.txt_status.setText("已驳回");
			viewHolder.txt_status.setTextColor(Color.parseColor("#ff0000"));
		}

		return convertView;
	}

	static final class ViewHolder {
		TextView txt_name;
		TextView txt_time;
		TextView txt_status;
		TextView txt_start_time;
		TextView txt_end_time;
		TextView txt_reason;
	}

}
