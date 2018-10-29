package com.dt5000.ischool.adapter;

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
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.SingleImageShowActivity;
import com.dt5000.ischool.entity.HomeworkComment;
import com.dt5000.ischool.entity.HomeworkCommentPic;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 作业详情页面评论列表适配器
 * 
 * @author 周锋
 * @date 2016年1月13日 下午4:44:59
 * @ClassInfo com.dt5000.ischool.adapter.HomeworkCommentListAdapter
 * @Description
 */
public class HomeworkCommentListAdapter extends BaseAdapter {

	private Context context;
	private List<HomeworkComment> list;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public HomeworkCommentListAdapter(Context ctx, List<HomeworkComment> data) {
		this.context = ctx;
		this.list = data;
		inflater = LayoutInflater.from(context);
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
			convertView = inflater.inflate(
					R.layout.view_list_item_homework_comment, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.img_pic = (ImageView) convertView
					.findViewById(R.id.img_pic);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		HomeworkComment comment = list.get(position);

		// 姓名
		String name = comment.getUserName();
		if (User.isTeacherRole(comment.getUserRole())) {
			name += "(老师)";
		} else {
			name += "(家长)";
		}
		viewHolder.txt_name.setText(name);

		// 内容
		String content = comment.getContent();
		if (CheckUtil.stringIsBlank(content)) {
			viewHolder.txt_content.setVisibility(View.GONE);
		} else {
			viewHolder.txt_content.setVisibility(View.VISIBLE);
			viewHolder.txt_content.setText(content);
		}

		// 图片
		HomeworkCommentPic image = comment.getImage();
		if (image != null) {
			viewHolder.img_pic.setVisibility(View.VISIBLE);

			imageLoader.displayImage(image.getSmallUrl(), viewHolder.img_pic);
			viewHolder.img_pic.setOnClickListener(new ImgClickListener(image
					.getUrl()));
		} else {
			viewHolder.img_pic.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ImgClickListener implements OnClickListener {
		private String url;

		public ImgClickListener(String imgUrl) {
			this.url = imgUrl;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, SingleImageShowActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}

	static final class ViewHolder {
		private TextView txt_name;
		private TextView txt_content;
		private ImageView img_pic;
	}

}
