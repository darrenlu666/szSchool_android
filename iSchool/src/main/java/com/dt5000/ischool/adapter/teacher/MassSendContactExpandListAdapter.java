package com.dt5000.ischool.adapter.teacher;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.FriendItem;
import com.dt5000.ischool.entity.GroupItem;
import com.dt5000.ischool.utils.MLog;

/**
 * 群发消息联系人列表适配器：教师端
 * 
 * @author 周锋
 * @date 2016年1月29日 上午9:24:09
 * @ClassInfo 
 *            com.dt5000.ischool.adapter.teacher.MassSendContactExpandListAdapter
 * @Description
 */
public class MassSendContactExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<ContactItem> data;
	private LayoutInflater inflater;
	private List<Boolean> groupCheckStatus;// 储存group级别checkbox状态
	private SparseArray<List<Boolean>> chlidCheckStatus;// 储存child级别checkbox状态，关联groupCheckStatus

	public MassSendContactExpandListAdapter(Context ctx,
			List<ContactItem> listData, boolean massChecked) {
		this.context = ctx;
		this.data = listData;
		this.inflater = LayoutInflater.from(context);

		groupCheckStatus = new ArrayList<Boolean>();
		chlidCheckStatus = new SparseArray<List<Boolean>>();
		for (int i = 0; i < data.size(); i++) {
			groupCheckStatus.add(massChecked);// group级别checkbox状态初始化
			List<FriendItem> fList = data.get(i).getFriendList();
			List<Boolean> list = new ArrayList<Boolean>();
			for (int j = 0; j < fList.size(); j++) {
				list.add(massChecked);// child级别checkbox状态初始化
			}
			chlidCheckStatus.put(i, list);// 关联child和group
		}
		helpLog();
	}

	public List<Boolean> getGroupCheckStatus() {
		return groupCheckStatus;
	}

	public SparseArray<List<Boolean>> getChlidCheckStatus() {
		return chlidCheckStatus;
	}

	private void helpLog() {
		MLog.i("groupCheckStatus:");
		for (int i = 0; i < groupCheckStatus.size(); i++) {
			MLog.i(i + "  " + groupCheckStatus.get(i));
		}
		MLog.i("-------------------");
		MLog.i("chlidCheckStatus:");
		for (int i = 0; i < groupCheckStatus.size(); i++) {
			MLog.i("group" + i);
			List<Boolean> list = chlidCheckStatus.get(i);
			for (int j = 0; j < list.size(); j++) {
				MLog.i("child" + j + "  " + list.get(j));
			}
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ContactItem cont = data.get(groupPosition);
		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type) || "3".equals(type)) {
			return cont.getFriendList().get(childPosition);
		} else if ("2".equals(type)) {
			return cont.getGroupList().get(childPosition);
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
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.view_expand_list_item_child_mass_send_contact,
					null);
		}

		TextView txt_name = (TextView) convertView.findViewById(R.id.txt_name);
		CheckBox checkBox_child = (CheckBox) convertView
				.findViewById(R.id.checkBox_child);
		RelativeLayout rLayout_child = (RelativeLayout) convertView
				.findViewById(R.id.rLayout_child);

		ContactItem cont = data.get(groupPosition);

		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type) || "3".equals(type)) {
			FriendItem friendItem = cont.getFriendList().get(childPosition);
			txt_name.setText(friendItem.getFriendName());
		} else if ("2".equals(type)) {
			GroupItem groupItem = cont.getGroupList().get(childPosition);
			txt_name.setText(groupItem.getGroupName());
		}

		// 获取选中状态
		List<Boolean> list = chlidCheckStatus.get(groupPosition);
		Boolean isChildChecked = list.get(childPosition);
		checkBox_child.setChecked(isChildChecked);

		// 设置child级别checkbox选中监听
		rLayout_child.setOnClickListener(new ChildOnClickListener(
				checkBox_child, groupPosition, childPosition));

		return convertView;
	}

	class ChildOnClickListener implements OnClickListener {
		private CheckBox checkBox;
		private int groupPosition;
		private int childPosition;

		public ChildOnClickListener(CheckBox checkBox, int groupPosition,
				int childPosition) {
			this.checkBox = checkBox;
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
		}

		@Override
		public void onClick(View v) {
			checkBox.toggle();
			if (checkBox.isChecked()) {
				List<Boolean> list = chlidCheckStatus.get(groupPosition);
				list.set(childPosition, true);
			} else {
				List<Boolean> list = chlidCheckStatus.get(groupPosition);
				list.set(childPosition, false);
			}

			// 判断选择多少个子项
			int chlidCancelAll = 0;
			List<Boolean> list = chlidCheckStatus.get(groupPosition);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i)) {
					chlidCancelAll++;
				}
			}

			if (chlidCancelAll == 0) {// 没有选择子项，那么组项也取消选中
				groupCheckStatus.set(groupPosition, false);
			} else if (chlidCancelAll == list.size()) {// 选中了全部子项，那么组项也选中
				groupCheckStatus.set(groupPosition, true);
			} else {// 选择了部分子项，那么组项取消选中
				groupCheckStatus.set(groupPosition, false);
			}

			helpLog();

			// 刷新
			notifyDataSetChanged();
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ContactItem cont = data.get(groupPosition);
		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type) || "3".equals(type)) {
			return cont.getFriendList().size();
		} else if ("2".equals(type)) {
			return cont.getGroupList().size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data == null ? null : data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.view_expand_list_item_group_mass_send_contact,
					null);
		}

		RelativeLayout rLayout_check_group = (RelativeLayout) convertView
				.findViewById(R.id.rLayout_check_group);
		TextView txtType = (TextView) convertView.findViewById(R.id.txtType);
		ImageView img_elist_arrow = (ImageView) convertView
				.findViewById(R.id.img_elist_arrow);
		CheckBox checkBox_group = (CheckBox) convertView
				.findViewById(R.id.checkBox_group);

		ContactItem cont = data.get(groupPosition);

		txtType.setText(cont.getContactName());

		// 箭头
		if (isExpanded) {
			img_elist_arrow.setImageResource(R.drawable.a_arrow_1);
		} else {
			img_elist_arrow.setImageResource(R.drawable.a_arrow_2);
		}

		// 获取选中状态
		Boolean isGroupChecked = groupCheckStatus.get(groupPosition);
		checkBox_group.setChecked(isGroupChecked);

		// 设置group级别checkbox选中监听
		rLayout_check_group.setOnClickListener(new GroupOnClickListener(
				checkBox_group, groupPosition));

		return convertView;
	}

	class GroupOnClickListener implements OnClickListener {
		private CheckBox checkBox;
		private int groupPosition;

		public GroupOnClickListener(CheckBox checkBox, int groupPosition) {
			this.checkBox = checkBox;
			this.groupPosition = groupPosition;
		}

		@Override
		public void onClick(View v) {
			checkBox.toggle();
			if (checkBox.isChecked()) {
				// 设置组项选中
				groupCheckStatus.set(groupPosition, true);
				// 组项下所有子项全部选中
				List<Boolean> list = chlidCheckStatus.get(groupPosition);
				for (int i = 0; i < list.size(); i++) {
					list.set(i, true);
				}
			} else {
				// 设置组项取消选中
				groupCheckStatus.set(groupPosition, false);
				// 组项下所有子项全部取消选中
				List<Boolean> list = chlidCheckStatus.get(groupPosition);
				for (int i = 0; i < list.size(); i++) {
					list.set(i, false);
				}
			}

			helpLog();

			// 刷新
			notifyDataSetChanged();
		}
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

}
