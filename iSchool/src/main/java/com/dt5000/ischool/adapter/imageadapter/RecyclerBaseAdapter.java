package com.dt5000.ischool.adapter.imageadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Created by ldh on 2015/12/18.
 */
public abstract class RecyclerBaseAdapter<M> extends RecyclerView.Adapter<RecyclerViewBaseHolder> {
    private Context mContext;
    private int mResId;
    private List<M> mList;
    private onItemClickListener mItemClickListener;


    public void setOnItemClickListener(onItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }

    public RecyclerBaseAdapter(Context context, int resId, List<M> list){
        mContext = context;
        mResId = resId;
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewBaseHolder(LayoutInflater.from(mContext).inflate(mResId,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewBaseHolder holder, final int position) {
        if(mItemClickListener != null){
            holder.getmConvertView().setClickable(true);
            holder.getmConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.itemClick(position);
                }
            });
        }
        onBindView(holder,position);
    }

    public abstract void onBindView(RecyclerViewBaseHolder holder, int position);


    @Override
    public int getItemCount() {
        return mList == null? 0:mList.size();
    }

    public void setData(List<M> list){
        mList = list;
        notifyDataSetChanged();
    }

    public interface onItemClickListener{
        void itemClick(int position);
    }
}
