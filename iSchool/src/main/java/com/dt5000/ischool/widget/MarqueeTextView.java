package com.dt5000.ischool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 在无需焦点的情况下无限播放跑马灯效果的TextView
 * 
 * @author 周锋
 * @date 2016年1月6日 下午3:25:24
 * @ClassInfo com.dt5000.ischool.widget.MarqueeTextView
 * @Description
 */
public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context) {
		super(context);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
	
}
