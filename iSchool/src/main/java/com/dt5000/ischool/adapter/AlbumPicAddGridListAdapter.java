package com.dt5000.ischool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AlbumUploadChoosePic;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * 班级相册上传图片时选择本地图片的列表适配器
 *
 * @author 周锋
 * @date 2016年2月3日 下午3:14:24
 * @ClassInfo com.dt5000.ischool.adapter.AlbumPicAddGridListAdapter
 * @Description
 */
public class AlbumPicAddGridListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<AlbumUploadChoosePic> list;
    private ImageLoader imageLoader;

    public AlbumPicAddGridListAdapter(Context ctx,
                                      List<AlbumUploadChoosePic> datas) {
        this.context = ctx;
        this.list = datas;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtil.createSimple(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.view_grid_list_item_album_pic_add, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.checkBox_choose = (CheckBox) convertView
                    .findViewById(R.id.checkBox_choose);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlbumUploadChoosePic albumUploadChoosePic = list.get(position);

//        imageLoader.displayImage("file://" + albumUploadChoosePic.getPicPath(), holder.img);

        Glide.with(context).load(new File(albumUploadChoosePic.getPicPath()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);

        if (albumUploadChoosePic.isChoose()) {
            holder.checkBox_choose.setBackgroundResource(R.drawable.pic_choose);
        } else {
            holder.checkBox_choose.setBackgroundResource(R.drawable.a_transbg);
        }

        holder.checkBox_choose
                .setOnCheckedChangeListener(new PicOnCheckedChangeListener(
                        position, holder.checkBox_choose));

        return convertView;
    }

    /**
     * 点击选择或者取消选择图片的监听
     */
    class PicOnCheckedChangeListener implements OnCheckedChangeListener {
        private int position;
        private CheckBox checkBox;

        public PicOnCheckedChangeListener() {
        }

        public PicOnCheckedChangeListener(int position, CheckBox checkBox) {
            this.position = position;
            this.checkBox = checkBox;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                checkBox.setBackgroundResource(R.drawable.pic_choose);
            } else {
                checkBox.setBackgroundResource(R.drawable.a_transbg);
            }

            // 将列表中该条记录的图片选中属性改为isChecked
            list.get(position).setChoose(isChecked);

            for (int i = 0; i < list.size(); i++) {
                MLog.i("选择状态：" + i + list.get(i).isChoose());
            }
        }
    }

    /**
     * 返回图片选择过后的列表
     */
    public List<AlbumUploadChoosePic> getChoosedPicList() {
        return list;
    }

    static class ViewHolder {
        ImageView img;
        CheckBox checkBox_choose;
    }

}
