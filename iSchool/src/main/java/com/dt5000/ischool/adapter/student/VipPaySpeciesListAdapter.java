package com.dt5000.ischool.adapter.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.VipPaySpecies;

import java.util.List;

/**
 * VIP套餐列表适配器：学生端
 * 
 * @author 周锋
 * @date 2016年1月29日 下午7:46:40
 * @ClassInfo com.dt5000.ischool.adapter.student.VipPaySpeciesListAdapter
 * @Description
 */
public class VipPaySpeciesListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<VipPaySpecies> list;

	public VipPaySpeciesListAdapter(Context ctx, List<VipPaySpecies> data) {
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
			convertView = inflater.inflate(R.layout.view_list_item_vip_pay_species, null);
			holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		VipPaySpecies species = list.get(position);
		
		holder.txt_name.setText(species.getGoodsName());

		return convertView;
	}

	static class ViewHolder {
		TextView txt_name;
	}

}
