package com.dt5000.ischool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.BlogAddChoosePic;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 新增博客时选择手机本地图片的列表适配器
 * 
 * @author 周锋
 * @date 2015年2月5日 下午2:01:00
 * @ClassInfo 
 *            com.dt5000.wuxixt.adapter.student.EventCreateChoosePicGridAdapterStudent
 * @Description
 */
public class BlogAddChoosePicGridAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<BlogAddChoosePic> list;
	private ImageLoader imageLoader;

	public BlogAddChoosePicGridAdapter(Context ctx, List<BlogAddChoosePic> datas) {
		this.context = ctx;
		this.list = datas;
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.view_grid_item_blog_add_choose_pic, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.checkBox_choose = (CheckBox) convertView
					.findViewById(R.id.checkBox_choose);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BlogAddChoosePic blogAddChoosePic = list.get(position);

		imageLoader.displayImage("file://" + blogAddChoosePic.getPicPath(),
				holder.img);

		if (blogAddChoosePic.isChoose()) {
			holder.checkBox_choose.setBackgroundResource(R.drawable.pic_choose);
		} else {
			holder.checkBox_choose.setBackgroundResource(R.drawable.a_transbg);
		}

		holder.checkBox_choose
				.setOnCheckedChangeListener(new PicOnCheckedChangeListener(
						position, holder.checkBox_choose));

		return convertView;
	}

	/**
	 * 点击选择或者取消选择图片的监听
	 */
	class PicOnCheckedChangeListener implements OnCheckedChangeListener {
		private int position;
		private CheckBox checkBox;

		public PicOnCheckedChangeListener() {
		}

		public PicOnCheckedChangeListener(int position, CheckBox checkBox) {
			this.position = position;
			this.checkBox = checkBox;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				checkBox.setBackgroundResource(R.drawable.pic_choose);
			} else {
				checkBox.setBackgroundResource(R.drawable.a_transbg);
			}

			// 将列表中该条记录的图片选中属性改为isChecked
			list.get(position).setChoose(isChecked);

			for (int i = 0; i < list.size(); i++) {
				MLog.i("选择状态：" + i + list.get(i).isChoose());
			}
		}
	}

	/**
	 * 返回图片选择过后的列表
	 * 
	 * @return
	 */
	public List<BlogAddChoosePic> getChoosedPicList() {
		return list;
	}

	static class ViewHolder {
		ImageView img;
		CheckBox checkBox_choose;
	}

}
