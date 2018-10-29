package com.dt5000.ischool.widget;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by weimy on 2017/10/13.
 */

public abstract class ClickableSpannable extends ClickableSpan {
    private int textColor;

    public ClickableSpannable(int color) {
        this.textColor = color;
    }

    /**
     * 重写updateDrawState更新文字属性
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(textColor);
        ds.setUnderlineText(false);//不添加下划线
        ds.clearShadowLayer();
    }
}
