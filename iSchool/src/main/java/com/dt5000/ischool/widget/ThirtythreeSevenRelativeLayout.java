package com.dt5000.ischool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 宽高比33:7
 * 
 * @author 周锋
 * @date 2016年11月2日 上午10:57:51
 * @ClassInfo com.dt5000.ischool.widget.ThirtythreeSevenRelativeLayout
 * @Description
 */
public class ThirtythreeSevenRelativeLayout extends RelativeLayout {

	public ThirtythreeSevenRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ThirtythreeSevenRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ThirtythreeSevenRelativeLayout(Context context) {
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
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				childWidthSize * 7 / 33, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
