package com.dt5000.ischool.adapter.imageadapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by codyy on 2015/12/18.
 */
public class RecyclerViewBaseHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;

    public RecyclerViewBaseHolder(View itemView){
        super(itemView);
        mViews = new SparseArray<>();
        mConvertView = itemView;
    }

    public <T extends View> T getView(@IdRes int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }

        return (T)view;
    }

    public View getmConvertView(){
        return mConvertView;
    }
}
