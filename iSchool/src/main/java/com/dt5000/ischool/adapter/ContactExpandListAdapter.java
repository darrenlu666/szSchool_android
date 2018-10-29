package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.FriendItem;
import com.dt5000.ischool.entity.GroupItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.CheckUtil;

/**
 * 通讯录列表适配器
 * 
 * @author 周锋
 * @date 2016年1月28日 下午7:14:13
 * @ClassInfo com.dt5000.ischool.adapter.ContactExpandListAdapter
 * @Description
 */
public class ContactExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<ContactItem> list;
	private LayoutInflater inflater;

	public ContactExpandListAdapter(Context ctx, List<ContactItem> list) {
		this.context = ctx;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ContactItem cont = list.get(groupPosition);
		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type)) {
			return cont.getFriendList().get(childPosition);
		} else if ("2".equals(type) || "4".equals(type)) {
			return cont.getGroupList().get(childPosition);
		} else {
			return null;
		}	// TODO: 2017/5/3  群发教师以及学生返回
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(
				R.layout.view_expand_list_item_child_contact, null);
		TextView txt_name = (TextView) convertView.findViewById(R.id.txt_name);
		TextView txt_phone = (TextView) convertView
				.findViewById(R.id.txt_phone);
		LinearLayout lLayout_phone = (LinearLayout) convertView
				.findViewById(R.id.lLayout_phone);

		ContactItem cont = list.get(groupPosition);

		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type)) {// 联系人
			FriendItem friendItem = cont.getFriendList().get(childPosition);

			// 姓名
			txt_name.setText(friendItem.getFriendName());

			// 判断角色
			if (User.isTeacherRole(Integer.parseInt(friendItem.getFriendRole()))) {// 教师
				String friendPhone = friendItem.getFriendPhone();
				if (CheckUtil.stringIsBlank(friendPhone)) {
					txt_phone.setVisibility(View.GONE);
					lLayout_phone.setVisibility(View.GONE);
				} else {
					txt_phone.setText(friendPhone);
					lLayout_phone.setVisibility(View.VISIBLE);

					// 点击拨打电话
					lLayout_phone.setOnClickListener(new OnClickPhoneListener(
							friendPhone));
				}
			} else {// 学生
				String friendPhone = friendItem.getFriendPhone();
				if (CheckUtil.stringIsBlank(friendPhone)) {
					txt_phone.setVisibility(View.GONE);
					lLayout_phone.setVisibility(View.GONE);
				} else {
					txt_phone.setText(friendPhone);
					lLayout_phone.setVisibility(View.VISIBLE);

					// 点击拨打电话
					lLayout_phone.setOnClickListener(new OnClickPhoneListener(
							friendPhone));
				}
			}
		} else if ("2".equals(type) || "4".equals(type)) {// 联系人组
			GroupItem groupItem = cont.getGroupList().get(childPosition);

			// 组名
			txt_name.setText(groupItem.getGroupName());

			// 组中不需要手机号
			txt_phone.setVisibility(View.GONE);
			lLayout_phone.setVisibility(View.GONE);
		}
		// TODO: 2017/5/3  群发教师以及学生
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ContactItem cont = list.get(groupPosition);
		String type = cont.getType();
		if ("0".equals(type) || "1".equals(type)) {
			return cont.getFriendList().size();
		} else if ("2".equals(type) || "4".equals(type)) {
			return cont.getGroupList().size();
		} else {
			return 0;
		}
		// TODO: 2017/5/3  群发教师以及学生返回Count
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
					R.layout.view_expand_list_item_group_contact, null);
			groupViewHolder.txtType = (TextView) convertView
					.findViewById(R.id.txtType);
			groupViewHolder.img_elist_arrow = (ImageView) convertView
					.findViewById(R.id.img_elist_arrow);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}

		ContactItem cont = list.get(groupPosition);

		groupViewHolder.txtType.setText(cont.getContactName());

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

	private class OnClickPhoneListener implements OnClickListener {
		private String phone;

		public OnClickPhoneListener(String phone) {
			this.phone = phone;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(context)
					.setMessage(phone)
					.setPositiveButton("呼叫",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											"android.intent.action.CALL", Uri
													.parse("tel:" + phone));
									context.startActivity(intent);
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	private class GroupViewHolder {
		TextView txtType;
		ImageView img_elist_arrow;
	}

	private class ChildViewHolder {
		TextView txt_name;
		TextView txt_phone;
		LinearLayout lLayout_phone;
	}

}
