package com.dt5000.ischool.activity.media.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.bean.MMVideoBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * @author eachann
 */
public class MMVideoGridAdapter extends MMBaseRecyclerViewAdapter<MMVideoBean> {
    public MMVideoGridAdapter(List<MMVideoBean> list, Context context) {
        super(list, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.multimedia_item_video_grid, parent, false);
        return new NormalRecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ((NormalRecyclerViewHolder) viewHolder).tvTime.setVisibility(View.VISIBLE);
        if (position == 0) {
            ((NormalRecyclerViewHolder) viewHolder).ivThumbsNails.setImageURI(Uri.parse("res://com.codyy.erpsportal/" + R.drawable.ic_exam_camera));
            ((NormalRecyclerViewHolder) viewHolder).tvTime.setVisibility(View.GONE);
        } else {
            final NormalRecyclerViewHolder albumHolder = (NormalRecyclerViewHolder) viewHolder;
            setController(list.get(position).getThumb(), albumHolder);
            ((NormalRecyclerViewHolder) viewHolder).tvTime.setVisibility(View.VISIBLE);
            ((NormalRecyclerViewHolder) viewHolder).tvTime.setText(list.get(position).getTime());
        }
    }





    private void setController(String path, NormalRecyclerViewHolder albumHolder) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + path));
        imageRequestBuilder.setResizeOptions(new ResizeOptions(
                albumHolder.ivThumbsNails.getLayoutParams().width,
                albumHolder.ivThumbsNails.getLayoutParams().height));
        ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(albumHolder.ivThumbsNails.getController())
                .setAutoPlayAnimations(true)
                .build();
        albumHolder.ivThumbsNails.setController(draweeController);
    }

    private static class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ivThumbsNails;
        TextView tvTime;

        private NormalRecyclerViewHolder(View view) {
            super(view);
            ivThumbsNails = (SimpleDraweeView) view.findViewById(R.id.iv_video_thumbnails);
            tvTime = (TextView) view.findViewById(R.id.tv_video_time);
        }
    }
}
