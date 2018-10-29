package com.dt5000.ischool.adapter.teacher;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.FriendItem;

/**
 * 成绩查询选择学生列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年1月28日 下午1:23:50
 * @ClassInfo com.dt5000.ischool.adapter.teacher.ScoreChooseExpandListAdapter
 * @Description
 */
public class ScoreChooseExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<ContactItem> list;
	private LayoutInflater inflater;

	public ScoreChooseExpandListAdapter(Context ctx, List<ContactItem> list) {
		this.context = ctx;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ContactItem cont = list.get(groupPosition);
		String type = cont.getType();
		if ("1".equals(type)) {
			return cont.getFriendList().get(childPosition);
		} else {
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder childViewHolder = null;
		if (convertView == null) {
			childViewHolder = new ChildViewHolder();
			convertView = inflater.inflate(
					R.layout.view_expand_list_item_child_score_choose, null);
			childViewHolder.txtName = (TextView) convertView
					.findViewById(R.id.txtName);
			convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}

		ContactItem cont = list.get(groupPosition);

		FriendItem friendItem = cont.getFriendList().get(childPosition);
		childViewHolder.txtName.setText(friendItem.getFriendName());

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ContactItem cont = list.get(groupPosition);
		String type = cont.getType();
		if ("1".equals(type)) {
			return cont.getFriendList().size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return list == null ? null : list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupViewHolder = null;
		if (convertView == null) {
			groupViewHolder = new GroupViewHolder();
			convertView = inflater.inflate(
					R.layout.view_expand_list_item_group_score_choose, null);
			groupViewHolder.txtType = (TextView) convertView
					.findViewById(R.id.txtType);
			groupViewHolder.img_elist_arrow = (ImageView) convertView
					.findViewById(R.id.img_elist_arrow);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}

		ContactItem cont = list.get(groupPosition);

		String contactName = cont.getContactName();
		groupViewHolder.txtType.setText(contactName);

		// 箭头
		if (isExpanded) {
			groupViewHolder.img_elist_arrow
					.setImageResource(R.drawable.a_arrow_1);
		} else {
			groupViewHolder.img_elist_arrow
					.setImageResource(R.drawable.a_arrow_2);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// 很重要：实现ChildView点击事件，必须返回true
		return true;
	}

	private class GroupViewHolder {
		TextView txtType;
		ImageView img_elist_arrow;
	}

	private class ChildViewHolder {
		TextView txtName;
	}

}
