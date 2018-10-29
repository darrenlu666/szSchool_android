package com.dt5000.ischool.adapter.student;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.UserInfo;
import com.dt5000.ischool.eventbus.ChangeUser;
import com.dt5000.ischool.eventbus.LoginOut;
import com.dt5000.ischool.widget.GlideCircleTransform;
import com.dt5000.ischool.widget.SwipeMenuView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by weimy on 2017/11/28.
 */

public class SwitchUserAdapter extends RecyclerView.Adapter<SwitchUserAdapter.MyViewHolder> {
    private List<UserInfo> userInfos = null;
    private Context context;

    public SwitchUserAdapter(Context context) {
        this.context = context;
    }

    //设置数据
    public void setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
        notifyDataSetChanged();
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_message_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(context)
                .load(userInfos.get(position).getProfileUrl())
                .bitmapTransform(new GlideCircleTransform(context))
                .into(holder.iv_icon);
        holder.txtUserName.setText(userInfos.get(position).getRealName());
        User user = User.getUser(context);

        if (user.getUserId().equals(userInfos.get(position).getUserId())) {
            holder.iv_image.setVisibility(View.VISIBLE);
        } else {
            holder.iv_image.setVisibility(View.GONE);
        }

        holder.linearLayout.setOnClickListener(new MyOnClickListeer(false, position));
        holder.swipeMenuView.setIos(false);
        holder.btnDelete_Group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSwipeMenuViewClickListener != null) {
                    onSwipeMenuViewClickListener.onswipeClick(position, userInfos.get(position));
                    getUserInfos().remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userInfos != null) {
            return userInfos.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView iv_icon;
        private TextView txtUserName;
        private ImageView iv_image;
        private Button btnDelete_Group;
        private SwipeMenuView swipeMenuView;

        public MyViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.lineraLayout);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            btnDelete_Group = (Button) itemView.findViewById(R.id.btnDelete_Group);
            swipeMenuView = (SwipeMenuView) itemView.findViewById(R.id.swipeMenuView);
        }
    }

    private class MyOnClickListeer implements View.OnClickListener {
        private boolean isMySelf;
        private int position;

        public MyOnClickListeer(boolean isMySelf, int position) {
            this.isMySelf = isMySelf;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (isMySelf) {
                notifyItemRemoved(position);
                EventBus.getDefault().post(new LoginOut(userInfos.get(position)));
            } else {
                EventBus.getDefault().post(new ChangeUser(userInfos.get(position)));
            }
        }
    }


    public onSwipeMenuViewClickListener onSwipeMenuViewClickListener;

    //interface
    public interface onSwipeMenuViewClickListener {
        void onswipeClick(int position, UserInfo userInfo);
    }

    public void setOnSwipeMenuViewClickListener(onSwipeMenuViewClickListener listener) {
        this.onSwipeMenuViewClickListener = listener;
    }
}
