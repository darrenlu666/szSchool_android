package com.dt5000.ischool.adapter.teacher;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AttendanceInfo;
import com.dt5000.ischool.entity.AttendancePage;

/**
 * 考勤滑动页面列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年10月11日 下午3:16:30
 * @ClassInfo com.dt5000.ischool.adapter.teacher.AttendancePagerListAdapter
 * @Description
 */
public class AttendancePagerListAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<AttendancePage> list;

	public AttendancePagerListAdapter(Context ctx, List<AttendancePage> datas) {
		this.context = ctx;
		this.list = datas;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@SuppressLint("InflateParams")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		AttendancePage attendancePage = list.get(position);
		List<AttendanceInfo> attendanceInfoList = attendancePage.getList();

		View view = inflater.inflate(R.layout.view_viewpager_item_attendance, null);
		ListView listview_attendance = (ListView) view.findViewById(R.id.listview_attendance);

		AttendanceListAdapter attendanceListAdapter = new AttendanceListAdapter(context, attendanceInfoList);
		listview_attendance.setAdapter(attendanceListAdapter);

		container.addView(view);

		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
