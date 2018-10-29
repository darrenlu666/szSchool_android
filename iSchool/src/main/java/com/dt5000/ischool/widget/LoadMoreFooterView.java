package com.dt5000.ischool.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * 可以添加在ListView底部的FootView，功能是点击加载更多数据
 * 
 * @author 周锋
 * @date 2016年1月8日 下午2:16:08
 * @ClassInfo com.dt5000.ischool.widget.LoadMoreFooterView
 * @Description
 */
public class LoadMoreFooterView {

	private View view;
	private TextView txt_footview_more;
	private LinearLayout lLayout_footview_loading;
	private OnClickLoadMoreListener clickLoadMoreListener;

	public interface OnClickLoadMoreListener {
		public void onClickLoadMore();
	}

	@SuppressLint("InflateParams")
	public LoadMoreFooterView(Context context, OnClickLoadMoreListener listener) {
		this.clickLoadMoreListener = listener;

		view = LayoutInflater.from(context).inflate(
				R.layout.view_list_footview_clickmore, null);
		txt_footview_more = (TextView) view
				.findViewById(R.id.txt_footview_more);
		lLayout_footview_loading = (LinearLayout) view
				.findViewById(R.id.lLayout_footview_loading);
		txt_footview_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (clickLoadMoreListener != null) {
					loadMore();
					clickLoadMoreListener.onClickLoadMore();
				}
			}
		});
	}

	public View create() {
		return view;
	}

	public void loadMore() {
		lLayout_footview_loading.setVisibility(View.VISIBLE);
		txt_footview_more.setVisibility(View.GONE);
	}

	public void loadComplete() {
		lLayout_footview_loading.setVisibility(View.GONE);
		txt_footview_more.setVisibility(View.VISIBLE);
		txt_footview_more.setText("更多...");
		txt_footview_more.setEnabled(true);
	}

	public void noMore() {
		lLayout_footview_loading.setVisibility(View.GONE);
		txt_footview_more.setVisibility(View.VISIBLE);
		txt_footview_more.setText("加载完毕");
		txt_footview_more.setEnabled(false);
	}

}
