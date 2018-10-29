package com.dt5000.ischool.adapter;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.Diary;
import com.dt5000.ischool.entity.DiaryDoc;
import com.dt5000.ischool.entity.DiaryPic;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 日记列表适配器
 * 
 * @author 周锋
 * @date 2016年2月4日 上午10:36:14
 * @ClassInfo com.dt5000.ischool.adapter.DiaryListAdapter
 * @Description
 */
public class DiaryListAdapter extends BaseAdapter {

	private Context context;
	private List<Diary> list;
	private LayoutInflater inflater;

	public DiaryListAdapter(Context ctx, List<Diary> data) {
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
			convertView = inflater.inflate(R.layout.view_list_item_diary, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.img_level = (ImageView) convertView
					.findViewById(R.id.img_level);
			viewHolder.img_pic = (ImageView) convertView
					.findViewById(R.id.img_pic);
			viewHolder.img_doc = (ImageView) convertView
					.findViewById(R.id.img_doc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Diary diary = list.get(position);

		// 日记标题
		viewHolder.txt_name.setText(diary.getTitle());

		// 日记内容
		viewHolder.txt_content.setText(diary.getContent());

		// 日记作者和时间
		Date date = TimeUtil.parseFullTime(diary.getCreateAt());
		viewHolder.txt_time.setText(diary.getAuthor() + " "
				+ TimeUtil.yearMonthDayHourMinuteFormat(date.getTime()));

		// 判断评价等级
		int level = diary.getLevelRank();
		if (level < 1) {
			viewHolder.img_level.setVisibility(View.GONE);
		} else {
			viewHolder.img_level.setVisibility(View.VISIBLE);
			if (level == 4) {
				viewHolder.img_level
						.setBackgroundResource(R.drawable.a_level_jiayou);
			} else if (level == 2) {
				viewHolder.img_level
						.setBackgroundResource(R.drawable.a_level_liang);
			} else if (level == 1) {
				viewHolder.img_level
						.setBackgroundResource(R.drawable.a_level_you);
			} else if (level == 3) {
				viewHolder.img_level
						.setBackgroundResource(R.drawable.a_level_yue);
			}
		}

		// 判断是否有图片附件
		List<DiaryPic> attachList = diary.getAttachList();
		if (attachList != null && attachList.size() > 0) {
			viewHolder.img_pic.setVisibility(View.VISIBLE);
		} else {
			viewHolder.img_pic.setVisibility(View.GONE);
		}

		// 判断是否有文档附件
		List<DiaryDoc> docList = diary.getDocList();
		if (docList != null && docList.size() > 0) {
			viewHolder.img_doc.setVisibility(View.VISIBLE);
		} else {
			viewHolder.img_doc.setVisibility(View.GONE);
		}

		return convertView;
	}

	static final class ViewHolder {
		private TextView txt_name;
		private TextView txt_time;
		private TextView txt_content;
		private ImageView img_level;
		private ImageView img_pic;
		private ImageView img_doc;
	}

}
