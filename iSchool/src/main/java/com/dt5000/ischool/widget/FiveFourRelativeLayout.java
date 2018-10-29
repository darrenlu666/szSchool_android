package com.dt5000.ischool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 宽高比5:4
 * @author 周锋
 * @date 2014年11月10日 上午10:16:45 
 * @ClassInfo com.dt5000.ischool.widget.ThreeTwoRelativeLayout
 * @Description
 */
public class FiveFourRelativeLayout extends RelativeLayout {

	public FiveFourRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public FiveFourRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FiveFourRelativeLayout(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, widthMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		// int childHeightSize = getMeasuredHeight();
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize,
				MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize * 4 / 5,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
