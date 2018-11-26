package com.dt5000.ischool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.PlayVideoActivity;
import com.dt5000.ischool.activity.PlayVideoActivity2;
import com.dt5000.ischool.activity.PlayVideoActivity3;
import com.dt5000.ischool.activity.SingleImageShowActivity;
import com.dt5000.ischool.activity.media.activity.VideoPlayerActivity;
import com.dt5000.ischool.activity.media.activity.VideoViewActivity;
import com.dt5000.ischool.db.MsgLocalPathDBManager;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.EmojiUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.VideoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import okhttp3.internal.framed.FrameReader;

import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static com.dt5000.ischool.net.UrlProtocol.VIDEO_HOST;

/**
 * 班级消息对话列表适配器
 *
 * @author 周锋
 * @date 2016年1月21日 上午11:41:45
 * @ClassInfo com.dt5000.ischool.adapter.ClassMsgTalkListAdapter
 * @Description
 */
public class ClassMsgTalkListAdapter extends BaseAdapter {

    private Context context;
    private List<ClassMessage> list;
    private LayoutInflater myInflater;
    private ImageLoader imageLoader;
    private User user;
    private MsgLocalPathDBManager mMsgLocalPathDBManager;

    public ClassMsgTalkListAdapter(Context context, List<ClassMessage> list, User user) {
        this.context = context;
        this.list = list;
        this.user = user;
        myInflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtil.createSimple(context);
        mMsgLocalPathDBManager = new MsgLocalPathDBManager(context);
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = myInflater.inflate(
                    R.layout.view_list_item_class_msg_talk, null);
            viewHolder.txt_name_left = (TextView) convertView
                    .findViewById(R.id.txt_name_left);
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
            viewHolder.img_video_play_left = (ImageView) convertView.findViewById(R.id.img_video_play_left);
            viewHolder.img_video_play_right = (ImageView) convertView.findViewById(R.id.img_video_play_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ClassMessage message = list.get(position);

        // 时间
        Date date = message.getSendDate();
        if (position == 0) {
            viewHolder.txt_time.setVisibility(View.VISIBLE);
            viewHolder.txt_time.setText(TimeUtil.messageFormat(date));
        } else {
            // 获取上一条消息的日期
            Date pre_data = list.get(position - 1).getSendDate();
            // 假如本条消息和上一条消息时间差小于10分钟，则不显示时间文本
            if ((date.getTime() - pre_data.getTime()) >= 10 * 60 * 1000) {
                viewHolder.txt_time.setVisibility(View.VISIBLE);
                viewHolder.txt_time.setText(TimeUtil.messageFormat(date));
            } else {
                viewHolder.txt_time.setVisibility(View.GONE);
            }
        }

        if (user.getUserId().equalsIgnoreCase(message.getSenderID())) {// 自己发送的信息
            viewHolder.lLayout_left.setVisibility(View.GONE);
            viewHolder.rLayout_right.setVisibility(View.VISIBLE);

            // 图片
            final String imageUrl = message.getPicUrl();
            if (!CheckUtil.stringIsBlank(imageUrl)) {
                if (ImageUtil.isImage(imageUrl)) {
                    viewHolder.img_pic_right.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_right.setVisibility(View.GONE);
                    viewHolder.img_video_play_right.setVisibility(View.GONE);
                    String smallImg = UrlProtocol.SMALL_IMAGE + imageUrl;
                    String middleImg = UrlProtocol.MIDDLE_IMAGE + imageUrl;
                    String largeImg = UrlProtocol.LARGE_IMAGE + imageUrl;
                    viewHolder.img_pic_right.setTag(smallImg);
                    MLog.i("小图：" + smallImg);
                    MLog.i("中图：" + middleImg);
                    MLog.i("大图：" + largeImg);
                    imageLoader.loadImage(middleImg,
                            new MySimpleImageLoadingListener(
                                    viewHolder.img_pic_right, smallImg));
                    viewHolder.img_pic_right
                            .setOnClickListener(new ImgClickListener(largeImg));
                } else if (VideoUtil.isVideo(imageUrl)) {
                    final String localPath = mMsgLocalPathDBManager.getPath(message.getClassMessageID());
                    viewHolder.img_pic_right.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_right.setVisibility(View.GONE);

                    viewHolder.img_video_play_right.setVisibility(View.VISIBLE);
                    viewHolder.img_pic_right.setTag(imageUrl);
                    new ThumbLoadTask(viewHolder.img_pic_right, imageUrl).execute();
                    viewHolder.img_pic_right.setOnClickListener(null);
                    viewHolder.img_video_play_right.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PlayVideoActivity3.class);
                            if (localPath != null) {
                                intent.putExtra("videoLocalUrl", localPath);
                            }
                            intent.putExtra("videoUrl", VIDEO_HOST + imageUrl);
                            intent.putExtra("isSelf", true);
                            intent.putExtra("videoName", imageUrl);
                            context.startActivity(intent);
                        }
                    });
                }
            } else {
                viewHolder.img_video_play_right.setVisibility(View.GONE);
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

            // 头像
            imageLoader.displayImage(user.getProfileUrl(),
                    viewHolder.img_head_right);
        } else {// 别人发送的信息
            viewHolder.rLayout_right.setVisibility(View.GONE);
            viewHolder.lLayout_left.setVisibility(View.VISIBLE);

            // 图片
            final String imageUrl = message.getPicUrl();
            if (!CheckUtil.stringIsBlank(imageUrl)) {
                if (ImageUtil.isImage(imageUrl)) {
                    viewHolder.img_pic_left.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_left.setVisibility(View.GONE);
                    viewHolder.img_video_play_left.setVisibility(View.GONE);
                    String smallImg = UrlProtocol.SMALL_IMAGE + imageUrl;
                    String middleImg = UrlProtocol.MIDDLE_IMAGE + imageUrl;
                    String largeImg = UrlProtocol.LARGE_IMAGE + imageUrl;
                    viewHolder.img_pic_left.setTag(smallImg);
                    MLog.i("小图：" + smallImg);
                    MLog.i("中图：" + middleImg);
                    MLog.i("大图：" + largeImg);
                    imageLoader.loadImage(middleImg,
                            new MySimpleImageLoadingListener(
                                    viewHolder.img_pic_left, smallImg));
                    viewHolder.img_pic_left
                            .setOnClickListener(new ImgClickListener(largeImg));
                } else if (VideoUtil.isVideo(imageUrl)) {
                    viewHolder.img_pic_left.setVisibility(View.VISIBLE);
                    viewHolder.img_emoji_left.setVisibility(View.GONE);

                    viewHolder.img_video_play_left.setVisibility(View.VISIBLE);
                    viewHolder.img_pic_left.setTag(imageUrl);
                    new ThumbLoadTask(viewHolder.img_pic_left, imageUrl).execute();
                    viewHolder.img_pic_left.setOnClickListener(null);
                    viewHolder.img_video_play_left.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PlayVideoActivity3.class);
                            intent.putExtra("videoUrl", VIDEO_HOST + imageUrl);
                            intent.putExtra("videoName", imageUrl);
                            context.startActivity(intent);
                        }
                    });
                }

            } else {
                viewHolder.img_pic_left.setVisibility(View.GONE);
                viewHolder.img_video_play_left.setVisibility(View.GONE);
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

            // 名字
            viewHolder.txt_name_left.setText(message.getSenderName());

            // 头像
            String headPic = message.getStuPhoto();
            imageLoader.displayImage(headPic, viewHolder.img_head_left);
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
            int width = loadedImage.getWidth() / 2;
            int height = loadedImage.getHeight() / 2;
            Bitmap bm = Bitmap.createScaledBitmap(loadedImage, width, height, true);
            if (smallPic.equals(imageView.getTag()))
                imageView.setImageBitmap(bm);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            imageLoader.displayImage(smallPic, imageView);
        }
    }

    private static class ThumbLoadTask extends AsyncTask<String, String, Bitmap> {
        private WeakReference<ImageView> mImageView;
        private String mUrl;

        ThumbLoadTask(ImageView imgView, String url) {
            mImageView = new WeakReference<>(imgView);
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            if (mImageView == null || mImageView.get() == null) return null;
            Bitmap bm = ImageUtil.getBitmapFromDCIM(mImageView.get().getContext(), mUrl);
            if (bm == null)
                bm = ImageUtil.getVideoThumb(mImageView.get().getContext(), VIDEO_HOST + mUrl, mUrl);
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView != null && mImageView.get() != null)
                if (bitmap == null) {
                    mImageView.get().setImageResource(R.drawable.pic_default);
                } else if (mImageView.get().getTag().equals(mUrl)) {
                    mImageView.get().setImageBitmap(bitmap);
                }
        }
    }

    static class ViewHolder {
        TextView txt_name_left;
        TextView txt_time;
        LinearLayout lLayout_left;
        RelativeLayout rLayout_right;
        TextView txt_content_left;
        TextView txt_content_right;
        ImageView img_pic_left;
        ImageView img_pic_right;
        ImageView img_emoji_left;
        ImageView img_emoji_right;
        ImageView img_head_left;
        ImageView img_head_right;
        ImageView img_video_play_left;
        ImageView img_video_play_right;
    }

}
