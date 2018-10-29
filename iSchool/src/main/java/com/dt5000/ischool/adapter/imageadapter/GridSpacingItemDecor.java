package com.dt5000.ischool.adapter.imageadapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 *
 * Created by gujiajia on 2018/3/1.
 */

public class GridSpacingItemDecor extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int horizontalSpacing;
    private int verticalSpacing;
    private boolean includeHorizontalEdge;
    private boolean includeVerticalEdge;

    private GridSpacingItemDecor(int spanCount, int horizontalSpacing, int verticalSpacing,
                                 boolean includeHorizontalEdge, boolean includeVerticalEdge) {
        this.spanCount = spanCount;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.includeHorizontalEdge = includeHorizontalEdge;
        this.includeVerticalEdge = includeVerticalEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeHorizontalEdge) {
            outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * horizontalSpacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
        } else {
            outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
        }

        if (includeVerticalEdge) {
            if (position < spanCount) { // top edge
                outRect.top = verticalSpacing;
            }
            outRect.bottom = verticalSpacing; // item bottom
        } else {
            if (position >= spanCount) {
                outRect.top = verticalSpacing; // item top
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int spanCount = 1;
        private int horizontalSpacing = 8;
        private int verticalSpacing = 8;
        private boolean includeHorizontalEdge;
        private boolean includeVerticalEdge;

        public Builder spanCount(int spanCount) {
            this.spanCount = spanCount;
            return this;
        }

        public Builder horizontalSpacing(int horizontalSpacing) {
            this.horizontalSpacing = horizontalSpacing;
            return this;
        }

        public Builder horizontalSpaceDp(Context context, int dp) {
            this.horizontalSpacing = dp2px(context, dp);
            return this;
        }

        public Builder verticalSpacing(int verticalSpacing) {
            this.verticalSpacing = verticalSpacing;
            return this;
        }

        public Builder verticalSpaceDp(Context context, int dp) {
            this.verticalSpacing = dp2px(context, dp);
            return this;
        }

        public Builder includeHorizontalEdge(boolean includeHorizontalEdge) {
            this.includeHorizontalEdge = includeHorizontalEdge;
            return this;
        }

        public Builder includeVerticalEdge(boolean includeVerticalEdge) {
            this.includeVerticalEdge = includeVerticalEdge;
            return this;
        }

        public GridSpacingItemDecor build() {
            return new GridSpacingItemDecor(spanCount,horizontalSpacing,verticalSpacing,
                includeHorizontalEdge, includeVerticalEdge);
        }
    }

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + .5f);
    }
}
