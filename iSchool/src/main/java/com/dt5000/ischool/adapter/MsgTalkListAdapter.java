package com.dt5000.ischool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.SingleImageShowActivity;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.EmojiUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Date;
import java.util.List;

/**
 * 个人消息对话列表适配器
 *
 * @author 周锋
 * @date 2016年1月21日 上午10:43:08
 * @ClassInfo com.dt5000.ischool.adapter.MsgTalkListAdapter
 * @Description
 */
public class MsgTalkListAdapter extends BaseAdapter {

    private Context context;
    private List<PersonMessage> list;
    private LayoutInflater inflater;
    private User user;
    private ImageLoader imageLoader;

    public MsgTalkListAdapter(Context context, List<PersonMessage> list, User user) {
        this.context = context;
        this.list = list;
        this.user = user;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtil.createSimple(context);
    }

    //改变本人头像
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_list_item_msg_talk,
                    null);
            viewHolder.txt_time = (TextView) convertView
                    .findViewById(R.id.txt_time);
            viewHolder.lLayout_left = (LinearLayout) convertView
                    .findViewById(R.id.lLayout_left);
            viewHolder.rLayout_right = (RelativeLayout) convertView
                    .findViewById(R.id.rLayout_right);
            viewHolder.txt_content_left = (TextView) convertView
                    .findViewById(R.id.txt_content_left);
            viewHolder.txt_content_right = (TextView) convertView
                    .findViewById(R.id.txt_content_right);
            viewHolder.img_pic_left = (ImageView) convertView
                    .findViewById(R.id.img_pic_left);
            viewHolder.img_pic_right = (ImageView) convertView
                    .findViewById(R.id.img_pic_right);
            viewHolder.img_emoji_left = (ImageView) convertView
                    .findViewById(R.id.img_emoji_left);
            viewHolder.img_emoji_right = (ImageView) convertView
                    .findViewById(R.id.img_emoji_right);
            viewHolder.img_head_left = (ImageView) convertView
                    .findViewById(R.id.img_head_left);
            viewHolder.img_head_right = (ImageView) convertView
                    .findViewById(R.id.img_head_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PersonMessage message = list.get(position);

        // 时间
        Date date = message.getSendDatetime();
        if (position == 0) {
            viewHolder.txt_time.setVisibility(View.VISIBLE);
            viewHolder.txt_time.setText(TimeUtil.messageFormat(date));
        } else {
            // 获取上一条消息的日期
            Date pre_data = list.get(position - 1).getSendDatetime();
            // 假如本条消息和上一条消息时间差小于10分钟，则不显示时间文本
            if ((date.getTime() - pre_data.getTime()) >= 10 * 60 * 1000) {
                viewHolder.txt_time.setVisibility(View.VISIBLE);
                viewHolder.txt_time.setText(TimeUtil.messageFormat(date));
            } else {
                viewHolder.txt_time.setVisibility(View.GONE);
            }
        }

        if (user.getUserId().equalsIgnoreCase(message.getSenderId())) {// 自己发送的信息
            viewHolder.lLayout_left.setVisibility(View.GONE);
            viewHolder.rLayout_right.setVisibility(View.VISIBLE);

            // 头像
            imageLoader.displayImage(user.getProfileUrl(), viewHolder.img_head_right);
            Log.i("myself", "自己发送消息头像----" + user.getProfileUrl());

            // 图片
            String imageUrl = message.getImageUrl();
            if (!CheckUtil.stringIsBlank(imageUrl)) {
                viewHolder.img_pic_right.setVisibility(View.VISIBLE);
                viewHolder.img_emoji_right.setVisibility(View.GONE);
                String smallImg = UrlProtocol.SMALL_IMAGE + imageUrl;
                String middleImg = UrlProtocol.MIDDLE_IMAGE + imageUrl;
                String largeImg = UrlProtocol.LARGE_IMAGE + imageUrl;
                MLog.i("小图：" + smallImg);
                MLog.i("中图：" + middleImg);
                MLog.i("大图：" + largeImg);
                imageLoader.loadImage(middleImg,
                        new MySimpleImageLoadingListener(
                                viewHolder.img_pic_right, smallImg));
                viewHolder.img_pic_right
                        .setOnClickListener(new ImgClickListener(largeImg));
            } else {
                viewHolder.img_pic_right.setVisibility(View.GONE);
            }

            // 内容
            String content = message.getContent();
            if (!CheckUtil.stringIsBlank(content)) {
                // 判断文本是否为表情
                int emojiResId = EmojiUtil.getEmojiResId(content);
                if (emojiResId == -1) {// 普通文本
                    viewHolder.txt_content_right.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_right.setVisibility(View.GONE);
                    viewHolder.txt_content_right.setText(content);
                } else {// 表情
                    viewHolder.txt_content_right.setVisibility(View.GONE);
                    viewHolder.img_emoji_right.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_right.setImageResource(emojiResId);
                }
            } else {
                viewHolder.txt_content_right.setVisibility(View.GONE);
            }
        } else {// 别人发送的信息
            viewHolder.rLayout_right.setVisibility(View.GONE);
            viewHolder.lLayout_left.setVisibility(View.VISIBLE);

            // 头像
            imageLoader.displayImage(message.getProfileUrl(), viewHolder.img_head_left);
            Log.i("myself", "别人发送信息头像----" + message.getProfileUrl());
            // 图片
            String imageUrl = message.getImageUrl();
            if (!CheckUtil.stringIsBlank(imageUrl)) {
                viewHolder.img_pic_left.setVisibility(View.VISIBLE);
                viewHolder.img_emoji_left.setVisibility(View.GONE);
                String smallImg = UrlProtocol.SMALL_IMAGE + imageUrl;
                String middleImg = UrlProtocol.MIDDLE_IMAGE + imageUrl;
                String largeImg = UrlProtocol.LARGE_IMAGE + imageUrl;
                MLog.i("小图：" + smallImg);
                MLog.i("中图：" + middleImg);
                MLog.i("大图：" + largeImg);
                imageLoader.loadImage(middleImg,
                        new MySimpleImageLoadingListener(
                                viewHolder.img_pic_left, smallImg));
                viewHolder.img_pic_left.setOnClickListener(new ImgClickListener(largeImg));
            } else {
                viewHolder.img_pic_left.setVisibility(View.GONE);
            }

            // 内容
            String content = message.getContent();
            if (!CheckUtil.stringIsBlank(content)) {
                // 判断文本是否为表情
                int emojiResId = EmojiUtil.getEmojiResId(content);
                if (emojiResId == -1) {// 普通文本
                    viewHolder.txt_content_left.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_left.setVisibility(View.GONE);
                    viewHolder.txt_content_left.setText(content);
                } else {// 表情
                    viewHolder.txt_content_left.setVisibility(View.GONE);
                    viewHolder.img_emoji_left.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_left.setImageResource(emojiResId);
                }
            } else {
                viewHolder.txt_content_left.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ImgClickListener implements OnClickListener {
        private String url;

        public ImgClickListener(String imgUrl) {
            this.url = imgUrl;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SingleImageShowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    private class MySimpleImageLoadingListener extends
            SimpleImageLoadingListener {
        private ImageView imageView;
        private String smallPic;

        public MySimpleImageLoadingListener(ImageView imageView, String smallPic) {
            this.imageView = imageView;
            this.smallPic = smallPic;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            imageView.setImageBitmap(null);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            imageView.setImageBitmap(loadedImage);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            imageLoader.displayImage(smallPic, imageView);
        }
    }

    static class ViewHolder {
        private TextView txt_time;
        private LinearLayout lLayout_left;
        private RelativeLayout rLayout_right;
        private TextView txt_content_left;
        private TextView txt_content_right;
        private ImageView img_pic_left;
        private ImageView img_pic_right;
        private ImageView img_emoji_left;
        private ImageView img_emoji_right;
        private ImageView img_head_left;
        private ImageView img_head_right;
    }

}
