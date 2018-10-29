package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ContactSearchItem;
import com.dt5000.ischool.utils.CheckUtil;

/**
 * 通讯录搜索列表适配器
 * 
 * @author 周锋
 * @date 2017年3月17日 下午2:11:52
 * @ClassInfo com.dt5000.ischool.adapter.ContactSearchListAdapter
 * @Description
 */
public class ContactSearchListAdapter extends BaseAdapter {

	private Context context;
	private List<ContactSearchItem> list;
	private LayoutInflater inflater;
	private String filterTxt;

	public ContactSearchListAdapter(Context ctx, List<ContactSearchItem> data,
			String filterTxt) {
		this.context = ctx;
		this.list = data;
		this.filterTxt = filterTxt;
		inflater = LayoutInflater.from(context);
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
			convertView = inflater.inflate(
					R.layout.view_list_item_contact_search, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_phone = (TextView) convertView
					.findViewById(R.id.txt_phone);
			viewHolder.lLayout_phone = (LinearLayout) convertView
					.findViewById(R.id.lLayout_phone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ContactSearchItem contactSearchItem = list.get(position);

		// 名称，将搜索的文字颜色高亮
		String name = contactSearchItem.getName();
		int start = name.indexOf(filterTxt);
		int end = start + filterTxt.length();
		SpannableStringBuilder builder = new SpannableStringBuilder(name);
		ForegroundColorSpan foreColor = new ForegroundColorSpan(
				Color.parseColor("#84ccc9"));
		builder.setSpan(foreColor, start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		viewHolder.txt_name.setText(builder);

		// 判断是否显示电话号码
		String type = contactSearchItem.getType();
		if ("0".equals(type)) {// 联系人
			String phone = contactSearchItem.getPhone();
			if (CheckUtil.stringIsBlank(phone)) {
				viewHolder.txt_phone.setVisibility(View.GONE);
				viewHolder.lLayout_phone.setVisibility(View.GONE);
			} else {
				viewHolder.lLayout_phone.setVisibility(View.VISIBLE);
				viewHolder.txt_phone.setVisibility(View.VISIBLE);
				viewHolder.txt_phone.setText(phone);

				// 点击拨打电话
				viewHolder.lLayout_phone
						.setOnClickListener(new OnClickPhoneListener(phone));
			}
		} else {
			// 组中不需要手机号
			viewHolder.txt_phone.setVisibility(View.GONE);
			viewHolder.lLayout_phone.setVisibility(View.GONE);
		}

		return convertView;
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

	static class ViewHolder {
		TextView txt_name;
		TextView txt_phone;
		LinearLayout lLayout_phone;
	}

}
