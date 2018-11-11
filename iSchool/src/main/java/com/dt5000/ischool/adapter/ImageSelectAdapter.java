package com.dt5000.ischool.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ImageMessage;
import com.dt5000.ischool.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weimy on 2017/11/30.
 */

public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.ImageViewHolder> {

    private List<ImageMessage> imageMessages = null;
    private Context context;

    public ImageSelectAdapter(Context context) {
        this.context = context;
        this.imageMessages=new ArrayList<>();
    }

    //设置数据
    public void setImageMessages(List<String> picPaths) {
        imageMessages.clear();
        for (int i = 0; i < picPaths.size(); i++) {
            Bitmap picBitmap = ImageUtil.decodeBitmapToFixSize(picPaths.get(i), 120, 130);
            imageMessages.add(new ImageMessage(picBitmap));
        }
        notifyDataSetChanged();
    }

    //获取数据
    public List<ImageMessage> getImageMessages() {
        if (imageMessages != null) {
            return imageMessages;
        }
        return null;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_select_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap bitmap = imageMessages.get(position).getBitmap();
        if (bitmap != null) {
            holder.thumbnail.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return imageMessages == null ? 0 : imageMessages.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;

        public ImageViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}
