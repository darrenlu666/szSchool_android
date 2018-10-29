package com.dt5000.ischool.adapter.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.utils.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * 班级消息列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年1月21日 上午11:14:06
 * @ClassInfo com.dt5000.ischool.adapter.teacher.ClassMsgListAdapter
 * @Description
 */
public class ClassMsgListAdapter extends BaseAdapter {

	private Context context;
	private List<ClassMessage> list;
	private LayoutInflater myInflater;

	public ClassMsgListAdapter(Context ctx, List<ClassMessage> list) {
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
			convertView = myInflater.inflate(R.layout.view_list_item_class_msg,
					null);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.txt_count = (TextView) convertView
					.findViewById(R.id.txt_count);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ClassMessage classMessage = list.get(position);

		viewHolder.txt_name.setText(classMessage.getClazzName());

		Date sendDate = classMessage.getSendDate();
		if (sendDate != null) {
			viewHolder.txt_time.setText(TimeUtil.messageFormat(sendDate));
		}

		int count = classMessage.getNewClzMsgCount();
		if (count > 0) {
			viewHolder.txt_content.setText(String.valueOf(count) + "条未读");
			viewHolder.txt_count.setVisibility(View.VISIBLE);

			if (count < 100) {
				viewHolder.txt_count.setText(String.valueOf(count));
			} else {
				viewHolder.txt_count.setText("...");
			}
		} else {
			viewHolder.txt_content.setText("无新消息");
			viewHolder.txt_count.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		private TextView txt_name;
		private TextView txt_content;
		private TextView txt_time;
		private TextView txt_count;
	}

}
