package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.BlogComment;

/**
 * 博客评论列表适配器
 * 
 * @author 周锋
 * @date 2016年2月2日 下午6:39:32
 * @ClassInfo com.dt5000.ischool.adapter.BlogCommentListAdapter
 * @Description
 */
public class BlogCommentListAdapter extends BaseAdapter {

	private Context context;
	private List<BlogComment> list;
	private LayoutInflater inflater;

	public BlogCommentListAdapter(Context ctx, List<BlogComment> data) {
		this.context = ctx;
		this.list = data;
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.view_list_item_blog_comment, null);
			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BlogComment blogComment = list.get(position);

		holder.txt_name.setText(blogComment.getCreateName() + ":");
		holder.txt_content.setText(blogComment.getContent());

		return convertView;
	}

	static class ViewHolder {
		TextView txt_name;
		TextView txt_content;
	}

}
