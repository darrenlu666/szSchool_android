package com.dt5000.ischool.activity.media.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 间距
 * Created by eachann on 2016/2/17.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int halfSpace;

    public SpacesItemDecoration(int space) {
        this.halfSpace = space / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

       /* if (parent.getPaddingLeft() != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
            parent.setClipToPadding(false);
        }
*/
        outRect.top = halfSpace;
        outRect.bottom = halfSpace;
        outRect.right = halfSpace;
        outRect.left = halfSpace;
        /*//由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % 4 == 0) {
            outRect.left = 0;

        }else {
            outRect.left = halfSpace;
        }*/
    }
}
