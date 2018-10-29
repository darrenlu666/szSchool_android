package com.dt5000.ischool.adapter.teacher;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.teacher.AttendancePictureGridListActivity;
import com.dt5000.ischool.entity.AttendanceInfo;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 考勤列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年10月11日 下午3:18:31
 * @ClassInfo com.dt5000.ischool.adapter.teacher.AttendanceListAdapter
 * @Description
 */
public class AttendanceListAdapter extends BaseAdapter {

	private Context context;
	private List<AttendanceInfo> list;
	private LayoutInflater myInflater;
	private ImageLoader imageLoader;

	public AttendanceListAdapter(Context ctx, List<AttendanceInfo> list) {
		this.context = ctx;
		this.list = list;
		myInflater = LayoutInflater.from(context);
		imageLoader = ImageLoaderUtil.createSimple(context);
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
					R.layout.view_list_item_attendance_teacher, null);
			viewHolder.lLayout_root = (LinearLayout) convertView
					.findViewById(R.id.lLayout_root);
			viewHolder.txt_in = (TextView) convertView
					.findViewById(R.id.txt_in);
			viewHolder.txt_out = (TextView) convertView
					.findViewById(R.id.txt_out);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.img_head = (ImageView) convertView
					.findViewById(R.id.img_head);
			viewHolder.img_flag = (ImageView) convertView
					.findViewById(R.id.img_flag);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		AttendanceInfo attendanceInfo = list.get(position);

		// 名字
		viewHolder.txt_name.setText(attendanceInfo.getRealName());

		// 进校时间
		String arriveTime = attendanceInfo.getArriveTime();
		if (!CheckUtil.stringIsBlank(arriveTime)) {
			Date arriveDate = TimeUtil.parseFullTime(arriveTime);
			viewHolder.txt_in.setText(TimeUtil
					.hourMinuteSecondFormat(arriveDate.getTime()));
		} else {
			viewHolder.txt_in.setText("");
		}

		// 离校时间
		String leaveTime = attendanceInfo.getLeaveTime();
		if (!CheckUtil.stringIsBlank(leaveTime)) {
			Date leaveDate = TimeUtil.parseFullTime(leaveTime);
			viewHolder.txt_out.setText(TimeUtil
					.hourMinuteSecondFormat(leaveDate.getTime()));
		} else {
			viewHolder.txt_out.setText("");
		}

		// 请假标识
		if ("0".equals(attendanceInfo.getIfLeave())) {
			viewHolder.img_flag.setVisibility(View.GONE);
		} else {
			viewHolder.img_flag.setVisibility(View.VISIBLE);
		}

		// 头像
		imageLoader.displayImage(attendanceInfo.getHeadImageUrl(),
				viewHolder.img_head);

		viewHolder.lLayout_root.setOnClickListener(new ImgClickListener(
				attendanceInfo));

		return convertView;
	}

	private class ImgClickListener implements OnClickListener {
		private AttendanceInfo attendanceInfo;

		public ImgClickListener(AttendanceInfo info) {
			this.attendanceInfo = info;
		}

		@Override
		public void onClick(View v) {
			if (!"0".equals(attendanceInfo.getKqUserId())) {
				Intent intent = new Intent(context,
						AttendancePictureGridListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("attendanceInfo", attendanceInfo);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		}
	}

	static class ViewHolder {
		LinearLayout lLayout_root;
		TextView txt_in;
		TextView txt_out;
		TextView txt_name;
		ImageView img_head;
		ImageView img_flag;
	}

}
