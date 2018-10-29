package com.dt5000.ischool.adapter.teacher;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
import com.dt5000.ischool.entity.green_entity.MultipleItem;
import com.dt5000.ischool.entity.green_entity.TabEntity;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.widget.SwipeMenuView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weimy on 2017/12/27.
 */

public class MsgListAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {
    private String userId = null;
    private ImageLoader imageLoader;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MsgListAdapter(List<MultipleItem> data, String userId, Context context) {
        super(data);
        addItemType(MultipleItem.Sigle, R.layout.view_list_item_msg);
        addItemType(MultipleItem.Group, R.layout.group_send_item);
        this.userId = userId;
        imageLoader = ImageLoaderUtil.createSimple(context);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MultipleItem item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.Sigle:
                PersonMessage message = item.getPersonMessage();
                //消息数
                if (message != null) {
                    int count = message.getNewMsgCount();
                    if (count > 0) {
                        helper.setVisible(R.id.txt_count, true);
                        helper.setText(R.id.txt_count, count < 100 ? String.valueOf(count) : "...");
                    } else {
                        helper.setGone(R.id.txt_count, false);
                    }
                    //名称
                    helper.setText(R.id.txt_name, userId.equalsIgnoreCase(message.getSenderId()) ?
                            message.getReceiverName() : message.getSenderName());
                    //日期
                    helper.setText(R.id.txt_time, TimeUtil.messageFormat(message.getSendDatetime()));
                    //内容
                    helper.setText(R.id.txt_content, CheckUtil.stringIsBlank(message.getContent()) ?
                            "[图片]" : message.getContent());
                    // 头像
                    imageLoader.displayImage(message.getProfileUrl(), (ImageView) helper.getView(R.id.img_head));
                    ((SwipeMenuView) helper.getView(R.id.swipeMenuView)).setIos(false);
                    final LinearLayout linearLayout = helper.getView(R.id.linearLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onClick(linearLayout, helper.getAdapterPosition() - 1);
                            }
                        }
                    });
                    Button btnDelete = helper.getView(R.id.btnDelete);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mOnSwipeListener) {
                                mOnSwipeListener.onDel(item, helper.getAdapterPosition());
                            }
                        }
                    });
                }
                break;
            case MultipleItem.Group:
                GroupSendMessage groupSendMessage = item.getGroupSendMessage();
                if (groupSendMessage != null) {
                    helper.setText(R.id.txtFromTeacher, "来自" + groupSendMessage.getSenderName() + "老师");
                    helper.setText(R.id.txtTime, groupSendMessage.getSendDatetime());
                    helper.setText(R.id.txtContent, TextUtils.isEmpty(groupSendMessage.getContent()) ?
                            "[图片]" : groupSendMessage.getContent());

                    final CommonTabLayout commonTabLayout = helper.getView(R.id.tabLayout);
                    String[] tabs = new String[]{"已读(" + groupSendMessage.getReadNum() + "人)",
                            "未读(" + (Integer.valueOf(groupSendMessage.getReceiveNum()) - Integer.valueOf(groupSendMessage.getReadNum())) + "人)"};
                    ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
                    for (int i = 0; i < tabs.length; i++) {
                        mTabEntities.add(new TabEntity(tabs[i]));
                    }
                    commonTabLayout.setTabData(mTabEntities);


                    Button btnDelete = helper.getView(R.id.btnDelete_Group);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mOnSwipeListener) {
                                mOnSwipeListener.onDel(item, helper.getAdapterPosition());
                            }
                        }
                    });

                    final LinearLayout linearLayout = helper.getView(R.id.lineraLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onClick(linearLayout, helper.getAdapterPosition());
                            }
                        }
                    });
                }
                break;
        }
    }


    private OnSwipeListener mOnSwipeListener;

    //侧滑接口
    public interface OnSwipeListener {
        void onDel(MultipleItem multipleItem, int pos);

    }

    public void setOnDelListener(OnSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }

    //因侧滑原因，改变点击事件
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
