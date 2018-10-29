package com.dt5000.ischool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 嵌套ListView，用于显示在别的AbsListView中，解决条目显示不全的问题
 * 
 * @author 周锋
 * @date 2016年2月2日 下午4:44:40
 * @ClassInfo com.dt5000.ischool.widget.NestedListView
 * @Description
 */
public class NestedListView extends ListView {

	public NestedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NestedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NestedListView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
