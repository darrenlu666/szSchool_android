package com.dt5000.ischool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 个人消息列表适配器
 *
 * @author 周锋
 * @date 2016年1月21日 上午9:28:57
 * @ClassInfo com.dt5000.ischool.adapter.MsgListAdapter
 * @Description
 */
public class MsgListAdapter extends BaseAdapter {

    private Context context;
    private List<PersonMessage> list;
    private LayoutInflater inflater;
    private String userId;
    private ImageLoader imageLoader;

    public MsgListAdapter(Context ctx, List<PersonMessage> data, String userId) {
        this.context = ctx;
        this.list = data;
        this.userId = userId;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_list_item_msg_, null);
            viewHolder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
            viewHolder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
            viewHolder.txt_count = (TextView) convertView.findViewById(R.id.txt_count);
            viewHolder.img_head = (ImageView) convertView.findViewById(R.id.img_head);
//            viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
//            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PersonMessage message = list.get(position);
//        //delete
//        viewHolder.btnDelete.setOnClickListener(new DeleteOnClickListener(position, true));
//        //liearLayout
//        viewHolder.linearLayout.setOnClickListener(new DeleteOnClickListener(position, false, convertView));

        // 新消息数目
        int count = message.getNewMsgCount();
        if (count > 0) {
            viewHolder.txt_count.setVisibility(View.VISIBLE);
            if (count < 100) {
                viewHolder.txt_count.setText(String.valueOf(count));
            } else {
                viewHolder.txt_count.setText("...");
            }
        } else {
            viewHolder.txt_count.setVisibility(View.GONE);
        }

        // 总是显示对方名字
        String name;
        if (userId.equalsIgnoreCase(message.getSenderId())) {// 如果该条消息是我发送的，那么对方为接收者
            name = message.getReceiverName();
        } else {// 如果该条消息不是我发送的，那么对方为发送者
            name = message.getSenderName();
        }
        viewHolder.txt_name.setText(name);

        // 日期
        viewHolder.txt_time.setText(TimeUtil.messageFormat(message.getSendDatetime()));

        // 内容如果为空说明发送的是图片
        String content = message.getContent();
        if (CheckUtil.stringIsBlank(content)) {
            viewHolder.txt_content.setText("[图片]");
        } else {
            viewHolder.txt_content.setText(content);
        }

        // 头像
        imageLoader.displayImage(message.getProfileUrl(), viewHolder.img_head);

        return convertView;
    }

    static class ViewHolder {
        TextView txt_count;
        TextView txt_name;
        TextView txt_time;
        TextView txt_content;
        ImageView img_head;
//        Button btnDelete;
//        LinearLayout linearLayout;
    }

//    //点击事件
//    public class DeleteOnClickListener implements View.OnClickListener {
//        int position;
//        boolean isDelete;
//        View convertView;
//
//        public DeleteOnClickListener(int position, boolean isDelete) {
//            this.position = position;
//            this.isDelete = isDelete;
//        }
//
//        public DeleteOnClickListener(int position, boolean isDelete, View convertView) {
//            this.position = position;
//            this.isDelete = isDelete;
//            this.convertView = convertView;
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (onItemOrDeleteClickListener != null) {
//                if (isDelete) {
//                    onItemOrDeleteClickListener.onDelete(position);
//                } else {
//                    onItemOrDeleteClickListener.onItemClick(convertView, position);
//                }
//            }
//        }
//    }
//
//    public OnItemOrDeleteClickListener onItemOrDeleteClickListener;
//
//    public void setOnItemOrDeleteClickListener(OnItemOrDeleteClickListener listener) {
//        onItemOrDeleteClickListener = listener;
//    }
//
//    //interface
//    public interface OnItemOrDeleteClickListener {
//        void onDelete(int position);
//
//        void onItemClick(View view, int position);
//    }
}
