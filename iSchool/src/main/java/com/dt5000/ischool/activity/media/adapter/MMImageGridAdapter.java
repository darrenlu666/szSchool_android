package com.dt5000.ischool.activity.media.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.bean.MMImageBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;


/**
 * @author eachann
 */
public class MMImageGridAdapter extends MMBaseRecyclerViewAdapter<MMImageBean> {
    private int mPosition = -1;
    private int mImageWidth;

    public MMImageGridAdapter(List<MMImageBean> list, Context context) {
        super(list, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.multimedia_item_image_grid, parent, false);
        return new NormalRecyclerViewHolder(view);
    }

    public void setPosition(int position) {
        notifyItemChanged(mPosition);
        this.mPosition = position;
        notifyItemChanged(mPosition);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        try {
            if (list.get(position).getPath() == null) {
                ((NormalRecyclerViewHolder) viewHolder).layerView.setVisibility(View.GONE);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setBackgroundColor(Color.GRAY);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setImageResource(R.drawable.ic_exam_camera);
            } else {
                ((NormalRecyclerViewHolder) viewHolder).layerView.setVisibility(View.VISIBLE);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setController(getDraweeController(viewHolder, list.get(position)));
                if (list.get(position).isSelected()) {
                    ((NormalRecyclerViewHolder) viewHolder).imgQueueMultiSelected.setImageResource(R.drawable.ic_exam_select_p);
                    ((NormalRecyclerViewHolder) viewHolder).layerView.setBackgroundResource(R.color.image_selected_color);
                } else {
                    ((NormalRecyclerViewHolder) viewHolder).imgQueueMultiSelected.setImageResource(R.drawable.ic_exam_select_n);
                    ((NormalRecyclerViewHolder) viewHolder).layerView.setBackgroundResource(R.color.transparent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DraweeController getDraweeController(RecyclerView.ViewHolder viewHolder, MMImageBean imageBean) {
        ImageRequest thumbnailsRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse("file://" + imageBean.getThumbnails()))
                .setResizeOptions(new ResizeOptions(mImageWidth, mImageWidth))
                .build();
        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder();
        if (!imageBean.getPath().equals(imageBean.getThumbnails())) {
            ImageRequest[] imageRequests = new ImageRequest[2];
            imageRequests[0] = thumbnailsRequest;
            imageRequests[1] = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse("file://" + imageBean.getPath()))
                    .setResizeOptions(new ResizeOptions(mImageWidth, mImageWidth))
                    .build();
            controllerBuilder.setFirstAvailableImageRequests( imageRequests);
        } else {
            controllerBuilder.setImageRequest(thumbnailsRequest);
        }

        return controllerBuilder
                .setOldController(((NormalRecyclerViewHolder) viewHolder).imgQueue.getController())
                .setAutoPlayAnimations(true)
                .build();
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

    public static class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imgQueue;
        ImageView imgQueueMultiSelected;
        View layerView;

        public NormalRecyclerViewHolder(View view) {
            super(view);
            imgQueue = (SimpleDraweeView) view.findViewById(R.id.imgQueue);
            imgQueueMultiSelected = (ImageView) view.findViewById(R.id.cb_select_tag);
            layerView = view.findViewById(R.id.v_selected_frame);
        }
    }
}
