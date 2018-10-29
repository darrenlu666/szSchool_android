package com.dt5000.ischool.adapter;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ChildrenCollect;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 育儿经收藏列表适配器
 * 
 * @author 周锋
 * @date 2016年2月2日 上午11:28:22
 * @ClassInfo com.dt5000.ischool.adapter.ChildrenListAdapter
 * @Description
 */
public class ChildrenCollectListAdapter extends BaseAdapter {

	private Context context;
	private List<ChildrenCollect> list;
	private LayoutInflater inflater;

	public ChildrenCollectListAdapter(Context ctx, List<ChildrenCollect> data) {
		this.context = ctx;
		this.list = data;
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
					R.layout.view_list_item_children_collect, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ChildrenCollect childrenCollect = list.get(position);

		viewHolder.txt_name.setText(childrenCollect.getTitle());

		Date date = TimeUtil.parseFullTime(childrenCollect.getCreateAt());
		viewHolder.txt_time.setText(TimeUtil.yearMonthDayHourMinuteFormat(date
				.getTime()));

		return convertView;
	}

	static final class ViewHolder {
		private TextView txt_name;
		private TextView txt_time;
	}

}
