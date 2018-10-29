package com.dt5000.ischool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 宽高比2:1
 * 
 * @author 周锋
 * @date 2016年1月7日 下午2:38:57
 * @ClassInfo com.dt5000.ischool.widget.TwoOneRelativeLayout
 * @Description
 */
public class TwoOneRelativeLayout extends RelativeLayout {

	public TwoOneRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public TwoOneRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TwoOneRelativeLayout(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, widthMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		// int childHeightSize = getMeasuredHeight();
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize / 2, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
