package com.dt5000.ischool.activity.teacher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.MsgListAdapter;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
import com.dt5000.ischool.entity.green_entity.MultipleItem;
import com.dt5000.ischool.thread.SyncClass;
import com.dt5000.ischool.thread.SyncGroup;
import com.dt5000.ischool.thread.SyncPerson;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.widget.dialog.nicedialog.BaseNiceDialog;
import com.dt5000.ischool.widget.dialog.nicedialog.NiceDialog;
import com.dt5000.ischool.widget.dialog.nicedialog.ViewConvertListener;
import com.dt5000.ischool.widget.dialog.nicedialog.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Group_Send_List_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, MsgListAdapter.OnSwipeListener, MsgListAdapter.OnItemClickListener {
    private String tag = Group_Send_List_Activity.class.getSimpleName();
    //toolbar
    private TextView txt_title;
    private LinearLayout lLayout_back;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private MsgListAdapter msgListAdapter = null;
    //合并消息
    List<MultipleItem> multipleItems = null;

    private User user;

    private Subscription subscription = null;
    private boolean isFirst = true;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__send__list_);

        initView();
        init();
        initListener();

        onRefresh();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("群发消息");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
        lLayout_back.setOnClickListener(this);
    }

    private void init() {
        user = User.getUser(this);
        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);

        msgListAdapter = new MsgListAdapter(null, user.getUserId(), this);
        msgListAdapter.setOnDelListener(this);
        msgListAdapter.setOnItemClickListener(this);
    }

    // 动态广播接收消息推送
    private BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(false);
        if (isFirst) {
            isFirst = false;
            return;
        }

        isRefresh = false;
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(3)
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isRefresh) {
                            isRefresh = true;
                            // 同步个人消息
                            SyncPerson.SyncPersonMsg(Group_Send_List_Activity.this, user);
                            // 同步班级消息
                            SyncClass.SyncClassMsg(Group_Send_List_Activity.this, user);
                            // 同步群组消息
                            SyncGroup.SyncGroupMsg(Group_Send_List_Activity.this, user);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        onResume();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lLayout_back:
                finish();
                break;
        }
    }

    @Override
    public void onDel(MultipleItem multipleItem, int pos) {
        if (multipleItem == null) {
            return;
        }
        switch (multipleItem.getItemType()) {
            case MultipleItem.Group:
                final GroupSendMessage groupSendMessage = multipleItem.getGroupSendMessage();
                SharedPreferences sp = getSharedPreferences("group_pref", Context.MODE_PRIVATE);
                String itemShowTag = groupSendMessage.getSenderId() + "--" + groupSendMessage.getSendDatetime();
                // 标识该会话条目不显示在列表中
                sp.edit().putBoolean(itemShowTag, false).commit();
                //清空数据库
                DaoHelper.deleteGroup(groupSendMessage);
                //adapter更新
                if (msgListAdapter != null) {
                    msgListAdapter.remove(pos);
                }
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {
        if (msgListAdapter == null) {
            return;
        }

        final GroupSendMessage groupSendMessage = msgListAdapter.getData().get(position).getGroupSendMessage();
        if (groupSendMessage != null) {
            NiceDialog.init()
                    .setLayoutId(R.layout.ad_layout)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.txtContent, "来自" + groupSendMessage.getSenderName() + "老师:\n\n\t\t" + groupSendMessage.getContent());
                            holder.setOnClickListener(R.id.close, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .setWidth(210)
                    .setOutCancel(false)
                    .setAnimStyle(R.style.EnterExitAnimation)
                    .show(getSupportFragmentManager());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "PersonMsgList").commit();
        subscription = Observable.from(DaoHelper.getGroupSendList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<GroupSendMessage, Boolean>() {
                    @Override
                    public Boolean call(GroupSendMessage groupSendMessage) {
                        SharedPreferences sp = getSharedPreferences("group_pref", Context.MODE_PRIVATE);
                        String itemShowTag = groupSendMessage.getSenderId() + "--" + groupSendMessage.getSendDatetime();
                        boolean show = sp.getBoolean(itemShowTag, true);
                        return show;
                    }
                })
                .toList()
                .subscribe(new Subscriber<List<GroupSendMessage>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(tag, "onCompleted");
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "onError");
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onNext(List<GroupSendMessage> groupSendMessageList) {
                        Log.i(tag, "onNext");
                        multipleItems = merge(groupSendMessageList);
                        if (msgListAdapter != null) {
                            msgListAdapter.setNewData(multipleItems);
                            recyclerView.setAdapter(msgListAdapter);
                        }
                    }
                });
    }


    //将个人消息，群发消息整理为一个集合
    public List<MultipleItem> merge(List<GroupSendMessage> messageList) {
        List<MultipleItem> multipleItems = new ArrayList<>();
        if (messageList != null && messageList.size() > 0) {
            for (GroupSendMessage groupSendMessage : messageList) {
                multipleItems.add(new MultipleItem(MultipleItem.Group, groupSendMessage));
            }
        }

        return multipleItems;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
    }
}
