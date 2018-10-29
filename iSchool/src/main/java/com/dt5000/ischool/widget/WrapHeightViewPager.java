package com.dt5000.ischool.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自适应高度的ViewPager
 * 
 * @author 周锋
 * @date 2014年11月10日 下午1:30:10
 * @ClassInfo com.dt5000.ischool.widget.WrapHeightViewPager
 * @Description
 */
public class WrapHeightViewPager extends ViewPager {

	public WrapHeightViewPager(Context context) {
		super(context);
	}

	public WrapHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h > height)
				height = h;
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
