package com.dt5000.ischool.activity.teacher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.GroupMsgListActivity;
import com.dt5000.ischool.activity.MsgTalkListActivity;
import com.dt5000.ischool.adapter.teacher.MsgListAdapter;
import com.dt5000.ischool.db.ClassMessageDBManager;
import com.dt5000.ischool.db.GroupMessageDBManager;
import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.AppInfo;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.GroupMessage;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
import com.dt5000.ischool.entity.green_entity.MultipleItem;
import com.dt5000.ischool.thread.SyncClass;
import com.dt5000.ischool.thread.SyncGroup;
import com.dt5000.ischool.thread.SyncPerson;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 个人消息列表页面：教师端
 *
 * @author 周锋
 * @date 2016年1月20日 下午7:50:59
 * @ClassInfo com.dt5000.ischool.activity.teacher.MsgListActivity
 * @Description
 */
public class MsgListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MsgListAdapter.OnSwipeListener, MsgListAdapter.OnItemClickListener {
    //toolbar
    private TextView txt_title;
    private LinearLayout lLayout_back;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MsgListAdapter msgListAdapter = null;
    //合并消息
    List<MultipleItem> multipleItems = null;

    //消息HeadView
    private View classMsgHeadView;
    //班级消息
    private LinearLayout lLayout_class;
    private TextView classMsgHeadView_txt_name;
    private TextView classMsgHeadView_txt_time;
    private TextView classMsgHeadView_txt_content;
    private TextView classMsgHeadView_txt_count;
    //群组消息
    private LinearLayout lLayout_group;
    private TextView groupMsgHeadView_txt_content;
    private TextView groupMsgHeadView_txt_count;
    //群消息
    private LinearLayout lLayout_groupSend;
    private TextView txt_grouplist_content;

    private User user;
    private boolean showClass = false;// 标识是否显示头部班级消息
    private MyHandler handler = new MyHandler(this);

    private Subscription subscription = null;
    private boolean isRefresh = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);

        initView();
        init();
        initListener();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("消息盒子");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //群组,班级消息HeadView
        classMsgHeadView = getLayoutInflater().inflate(R.layout.view_list_item_head_msg, null);

        lLayout_class = (LinearLayout) classMsgHeadView.findViewById(R.id.lLayout_class);
        classMsgHeadView_txt_name = (TextView) classMsgHeadView.findViewById(R.id.txt_name);
        classMsgHeadView_txt_time = (TextView) classMsgHeadView.findViewById(R.id.txt_time);
        classMsgHeadView_txt_content = (TextView) classMsgHeadView.findViewById(R.id.txt_content);
        classMsgHeadView_txt_count = (TextView) classMsgHeadView.findViewById(R.id.txt_count);

        lLayout_group = (LinearLayout) classMsgHeadView.findViewById(R.id.lLayout_group);
        groupMsgHeadView_txt_content = (TextView) classMsgHeadView.findViewById(R.id.txt_group_content);
        groupMsgHeadView_txt_count = (TextView) classMsgHeadView.findViewById(R.id.txt_group_count);

        lLayout_groupSend = (LinearLayout) classMsgHeadView.findViewById(R.id.lLayout_groupSend);
        lLayout_groupSend.setVisibility(View.VISIBLE);
        txt_grouplist_content = (TextView) classMsgHeadView.findViewById(R.id.txt_grouplist_content);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "PersonMsgList").commit();

        // 激活页面时查询本地数据库中个人消息
        new queryMsgThread().start();

        if (showClass) {
            // 激活页面时查询本地数据库中最新一条班级消息
            new queryLatestClassMsgThread().start();
        }

        new queryLatestGroupMsgThread().start();

        swipeRefreshLayout.setEnabled(true);
        //设置群发送信息
        List<GroupSendMessage> groupSendMessageList = DaoHelper.getGroupSendList();
        if (groupSendMessageList != null) {
            if (groupSendMessageList.size() > 0) {
                txt_grouplist_content.setText(groupSendMessageList.get(0).getContent());
            }
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = false;
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(3)
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isRefresh) {
                            isRefresh = true;
                            // 同步个人消息
                            SyncPerson.SyncPersonMsg(MsgListActivity.this, user);
                            if (showClass) {
                                // 同步班级消息
                                SyncClass.SyncClassMsg(MsgListActivity.this, user);
                                // 同步群组消息
                                SyncGroup.SyncGroupMsg(MsgListActivity.this, user);
                            }
                        }
                    }
                })
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        handler.sendEmptyMessageDelayed(FlagCode.CODE_3, 1000);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
    }

    /**
     * 查询群组消息的最新一条数据
     */
    class queryLatestGroupMsgThread extends Thread {
        @Override
        public void run() {
            List<GroupMessage> messageList = null;
            GroupMessageDBManager groupMessageDBManager = null;
            PersonMessageDBManager personMessageDBManager = null;
            Message message = new Message();
            try {
                personMessageDBManager = new PersonMessageDBManager(MsgListActivity.this);
                groupMessageDBManager = new GroupMessageDBManager(MsgListActivity.this);
                // 查询未读的班级消息数目
                int clzUnreadMsgCount = personMessageDBManager.queryUnreadGroupMsg(user.getUserId());
                // 查询最新的一条班级消息
                messageList = groupMessageDBManager.queryTop(1, user.getUserId(), user.getClassinfoId());
                message.what = FlagCode.CODE_4;
                message.arg1 = clzUnreadMsgCount;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                message.obj = messageList;
                handler.sendMessage(message);
                if (null != groupMessageDBManager) {
                    groupMessageDBManager.closeDB();
                }
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    /**
     * 查询个人对话消息
     */
    class queryMsgThread extends Thread {
        @Override
        public void run() {
            Message message = new Message();
            //个人消息
            List<PersonMessage> messageList = null;
            PersonMessageDBManager personMessageDBManager = null;
            try {
                //查询个人消息
                personMessageDBManager = new PersonMessageDBManager(MsgListActivity.this);
                messageList = personMessageDBManager.queryMsg(user.getUserId(), null);
                multipleItems = merge(messageList);
                message.what = FlagCode.CODE_0;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                message.obj = multipleItems;
                handler.sendMessage(message);
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    //将个人消息，群发消息整理为一个集合
    public List<MultipleItem> merge(List<PersonMessage> messageList) {
        List<MultipleItem> multipleItems = new ArrayList<>();
        if (messageList != null && messageList.size() > 0) {
            for (PersonMessage personMessage : messageList) {
                multipleItems.add(new MultipleItem(MultipleItem.Sigle, personMessage));
            }
        }

        return multipleItems;
    }

    /**
     * 查询班级消息的最新一条数据
     */
    class queryLatestClassMsgThread extends Thread {
        @Override
        public void run() {
            List<ClassMessage> messageList = null;
            ClassMessageDBManager classMessageDBManager = null;
            PersonMessageDBManager personMessageDBManager = null;
            Message message = new Message();
            try {
                personMessageDBManager = new PersonMessageDBManager(MsgListActivity.this);
                classMessageDBManager = new ClassMessageDBManager(MsgListActivity.this);
                // 查询未读的班级消息数目
                int clzUnreadMsgCount = personMessageDBManager.queryUnreadClzMsg(user.getUserId());
                // 查询最新的一条班级消息
                messageList = classMessageDBManager.queryTop(1, user.getUserId(), user.getClassinfoId());
                message.what = FlagCode.CODE_1;
                message.arg1 = clzUnreadMsgCount;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                message.obj = messageList;
                handler.sendMessage(message);
                if (null != classMessageDBManager) {
                    classMessageDBManager.closeDB();
                }
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    class updateMsgStatusThread extends Thread {
        private String friendId;

        public updateMsgStatusThread(String fi) {
            this.friendId = fi;
        }

        @Override
        public void run() {
            PersonMessageDBManager personMessageDBManager = null;
            try {
                personMessageDBManager = new PersonMessageDBManager(MsgListActivity.this);
                personMessageDBManager.updateMsgStatus(user.getUserId(), friendId);
                handler.sendEmptyMessage(FlagCode.CODE_2);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgListActivity.this.finish();
            }
        });

        // 点击进入班级消息界面
        lLayout_class.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgListActivity.this, ClassMsgListActivity.class));
            }
        });

        // 点击进入群组消息界面
        lLayout_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgListActivity.this, GroupMsgListActivity.class));
            }
        });

        lLayout_groupSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgListActivity.this, Group_Send_List_Activity.class));
            }
        });

    }


    @Override
    public void onClick(View view, int position) {
        if (msgListAdapter == null) {
            return;
        }
        MultipleItem multipleItem = msgListAdapter.getData().get(position);
        if (multipleItem == null) {
            return;
        }
        switch (multipleItem.getItemType()) {
            case MultipleItem.Sigle:
                PersonMessage personMessage = multipleItem.getPersonMessage();
                //清空头标
                view.findViewById(R.id.txt_count).setVisibility(View.GONE);
                Intent intent = new Intent(MsgListActivity.this, MsgTalkListActivity.class);
                String receiverId = personMessage.getReceiverId();

                String friendId = user.getUserId().equals(receiverId) ?
                        personMessage.getSenderId() : receiverId;
                String friendName = user.getUserId().equals(receiverId) ?
                        personMessage.getSenderName() : personMessage.getReceiverName();

                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDel(MultipleItem multipleItem, int pos) {
        if (multipleItem == null) {
            return;
        }
        switch (multipleItem.getItemType()) {
            case MultipleItem.Sigle:
                final PersonMessage personMessage = multipleItem.getPersonMessage();
                SharedPreferences sp = getSharedPreferences("msg_pref", Context.MODE_PRIVATE);

                String itemShowTag = user.getUserId().equals(personMessage.getSenderId()) ?
                        personMessage.getSenderId() + "--" + personMessage.getReceiverId() :
                        personMessage.getReceiverId() + "--" + personMessage.getSenderId();
                String friendId = user.getUserId().equals(personMessage.getSenderId()) ?
                        personMessage.getReceiverId() : personMessage.getSenderId();

                // 标识该会话条目不显示在列表中
                sp.edit().putBoolean(itemShowTag, false).commit();
                // 更新该会话的状态，标记为已读
                new updateMsgStatusThread(friendId).start();
                break;
        }
    }

    private void init() {
        user = User.getUser(this);
        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);

        // 判断是否需要显示班级消息
        MLog.i("判断是否需要显示班级消息：classMessage=" + user.getClassMessage());
        if (user.getClassMessage() == 0) {
            lLayout_class.setVisibility(View.VISIBLE);
            showClass = true;
        } else {
            lLayout_class.setVisibility(View.GONE);
            showClass = false;
        }

        msgListAdapter = new MsgListAdapter(null, user.getUserId(), this);
        msgListAdapter.addHeaderView(classMsgHeadView);
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

    static class MyHandler extends Handler {
        WeakReference<MsgListActivity> referActivity;

        MyHandler(MsgListActivity activity) {
            referActivity = new WeakReference<MsgListActivity>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MsgListActivity msgListActivity = referActivity.get();
            switch (msg.what) {
                case FlagCode.CODE_0:
                    List<MultipleItem> multipleItems = (List<MultipleItem>) msg.obj;
                    //新集合
                    List<MultipleItem> sortMsgs = new ArrayList<MultipleItem>();

                    // 检测是否为删除过的会话条目，以及是否为有新消息的会话条目
                    SharedPreferences sp = msgListActivity.getSharedPreferences("msg_pref", AppInfo.getSPMODE());
                    //移除被删除的item
                    for (MultipleItem multipleItem : multipleItems) {
                        if (multipleItem.getItemType() == MultipleItem.Sigle) {
                            PersonMessage personMessage = multipleItem.getPersonMessage();
                            String itemShowTag = msgListActivity.user.getUserId().equals(personMessage.getSenderId()) ?
                                    personMessage.getSenderId() + "--" + personMessage.getReceiverId() :
                                    personMessage.getReceiverId() + "--" + personMessage.getSenderId();

                            int newCount = personMessage.getNewMsgCount();
                            if (newCount > 0) {// 只要该会话有新消息就标识该会话条目显示在列表中
                                sp.edit().putBoolean(itemShowTag, true).commit();
                                sortMsgs.add(multipleItem);
                            } else {// 如果该会话为旧数据，则检测是否删除过该会话条目
                                boolean show = sp.getBoolean(itemShowTag, true);
                                if (show) {
                                    sortMsgs.add(multipleItem);
                                }
                            }
                        }
                    }

                    if (msgListActivity.msgListAdapter != null) {
                        msgListActivity.msgListAdapter.setNewData(sortMsgs);
                        msgListActivity.recyclerView.setAdapter(msgListActivity.msgListAdapter);
                    }
                    break;
                case FlagCode.CODE_1:// 查询到最新的一条班级消息
                    msgListActivity.classMsgHeadView_txt_name.setText("班级消息");
                    msgListActivity.classMsgHeadView_txt_time.setText("");
                    // 消息数目
                    int clazzMsgCount = msg.arg1;
                    if (clazzMsgCount == 0) {
                        msgListActivity.classMsgHeadView_txt_count.setVisibility(View.GONE);
                        msgListActivity.classMsgHeadView_txt_content.setText("无新消息");
                    } else {
                        msgListActivity.classMsgHeadView_txt_count.setVisibility(View.VISIBLE);
                        msgListActivity.classMsgHeadView_txt_content.setText(clazzMsgCount + "条未读");
                        if (clazzMsgCount < 100) {
                            msgListActivity.classMsgHeadView_txt_count.setText(String.valueOf(clazzMsgCount));
                        } else {
                            msgListActivity.classMsgHeadView_txt_count.setText("...");
                        }
                    }
                    break;
                case FlagCode.CODE_4:// 查询到最新的一条群组消息
                    // 消息数目
                    int groupMsgCount = msg.arg1;
                    if (groupMsgCount == 0) {
                        msgListActivity.groupMsgHeadView_txt_count.setVisibility(View.GONE);
                        msgListActivity.groupMsgHeadView_txt_content.setText("无新消息");
                    } else {
                        msgListActivity.groupMsgHeadView_txt_count.setVisibility(View.VISIBLE);
                        msgListActivity.groupMsgHeadView_txt_content.setText(groupMsgCount + "条未读");
                        if (groupMsgCount < 100) {
                            msgListActivity.groupMsgHeadView_txt_count.setText(String.valueOf(groupMsgCount));
                        } else {
                            msgListActivity.groupMsgHeadView_txt_count.setText("...");
                        }
                    }
                    break;
                case FlagCode.CODE_2:// 删除一条会话后更新数据库信息状态成功后刷新页面
                    msgListActivity.onResume();
                    break;
                case FlagCode.CODE_3:// 点击刷新同步个人和班级消息成功后刷新页面
                    msgListActivity.swipeRefreshLayout.setEnabled(false);
                    msgListActivity.swipeRefreshLayout.setRefreshing(false);
                    msgListActivity.onResume();
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
    }

}
