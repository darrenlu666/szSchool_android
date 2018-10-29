package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.ClassMsgTalkListActivity;
import com.dt5000.ischool.activity.GroupMsgListActivity;
import com.dt5000.ischool.activity.MsgTalkListActivity;
import com.dt5000.ischool.adapter.MsgListAdapter;
import com.dt5000.ischool.db.ClassMessageDBManager;
import com.dt5000.ischool.db.GroupMessageDBManager;
import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.entity.AppInfo;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.GroupMessage;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.thread.SyncClass;
import com.dt5000.ischool.thread.SyncGroup;
import com.dt5000.ischool.thread.SyncPerson;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人消息列表页面：学生端
 *
 * @author 周锋
 * @date 2016年1月18日 下午4:26:13
 * @ClassInfo com.dt5000.ischool.activity.student.MsgListActivity
 * @Description
 */
public class MsgListActivity extends Activity {

    private TextView txt_title;
    private TextView txt_topbar_btn;
    private ListView listview_msg;
    private LinearLayout lLayout_back;
    private LinearLayout lLayout_loading;

    // 班级消息HeadView
    private View classMsgHeadView;
    private TextView classMsgHeadView_txt_name;
    private TextView classMsgHeadView_txt_time;
    private TextView classMsgHeadView_txt_content;
    private TextView classMsgHeadView_txt_count;
    private LinearLayout lLayout_class;
    //群组消息
    private LinearLayout lLayout_group;
    private TextView groupMsgHeadView_txt_content;
    private TextView groupMsgHeadView_txt_count;

    private User user;
    private List<PersonMessage> messageList = new ArrayList<PersonMessage>();
    private MsgListAdapter msgListAdapter;
    private boolean showClass = false;// 标识是否显示头部班级消息
    private MyHandler handler = new MyHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list_student);

        initView();
        initListener();
        init();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("刷新");
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("消息盒子");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
        listview_msg = (ListView) findViewById(R.id.listview_msg);

        // 班级消息HeadView
        classMsgHeadView = getLayoutInflater().inflate(R.layout.view_list_item_head_msg, null);
        classMsgHeadView_txt_name = (TextView) classMsgHeadView.findViewById(R.id.txt_name);
        classMsgHeadView_txt_time = (TextView) classMsgHeadView.findViewById(R.id.txt_time);
        classMsgHeadView_txt_content = (TextView) classMsgHeadView.findViewById(R.id.txt_content);
        classMsgHeadView_txt_count = (TextView) classMsgHeadView.findViewById(R.id.txt_count);

        lLayout_class = (LinearLayout) classMsgHeadView.findViewById(R.id.lLayout_class);
        lLayout_group = (LinearLayout) classMsgHeadView.findViewById(R.id.lLayout_group);
        groupMsgHeadView_txt_content = (TextView) classMsgHeadView.findViewById(R.id.txt_group_content);
        groupMsgHeadView_txt_count = (TextView) classMsgHeadView.findViewById(R.id.txt_group_count);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
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
            List<PersonMessage> messageList = null;
            PersonMessageDBManager personMessageDBManager = null;
            Message message = new Message();
            try {
                personMessageDBManager = new PersonMessageDBManager(MsgListActivity.this);
                messageList = personMessageDBManager.queryMsg(user.getUserId(), null);
                message.what = FlagCode.CODE_0;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                message.obj = messageList;
                handler.sendMessage(message);
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
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
                personMessageDBManager = new PersonMessageDBManager(
                        MsgListActivity.this);
                personMessageDBManager.updateMsgStatus(user.getUserId(),
                        friendId);
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

        // 点击刷新
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lLayout_loading.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 同步个人消息
                        SyncPerson.SyncPersonMsg(MsgListActivity.this, user);

                        if (showClass) {
                            // 同步班级消息
                            SyncClass.SyncClassMsg(MsgListActivity.this, user);
                        }
                        //TODO 同步群组消息
                        SyncGroup.SyncGroupMsg(MsgListActivity.this, user);

                        handler.sendEmptyMessage(FlagCode.CODE_3);
                    }
                }).start();
            }
        });

        lLayout_class.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MsgListActivity.this, ClassMsgTalkListActivity.class);
                    intent.putExtra("classInfoID", user.getClassinfoId());
                    intent.putExtra("className", user.getGradeName() + user.getClassName());
                    startActivity(intent);

            }
        });

        // 点击进入群组消息界面
        lLayout_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgListActivity.this, GroupMsgListActivity.class));
            }
        });


        // 点击进入个人对话界面
        listview_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt_count = (TextView) view.findViewById(R.id.txt_count);
                txt_count.setVisibility(View.GONE);

                // 如果添加了班级消息HeadView，那么点击时位置需要修正
                position = position - 1;
                PersonMessage message = messageList.get(position);

                Intent intent = new Intent(MsgListActivity.this,
                        MsgTalkListActivity.class);
                String friendId = "";
                String friendName = "";
                String receiverId = message.getReceiverId();
                if (user.getUserId().equals(receiverId)) {// 最新一条信息是别人发给自己的
                    friendName = message.getSenderName();
                    friendId = message.getSenderId();
                } else {// 最新一条信息是自己发给别人的
                    friendName = message.getReceiverName();
                    friendId = receiverId;
                }
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
            }
        });

        // 长按删除聊天
        listview_msg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;
                final PersonMessage cm = messageList.get(position);

                new AlertDialog.Builder(MsgListActivity.this)
                        .setMessage("删除该聊天？")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sp = getSharedPreferences("msg_pref", Context.MODE_PRIVATE);

                                        // 在pref配置文件中以“userId--friendId”形式来唯一确定一条会话
                                        String itemShowTag = "";
                                        String friendId = "";
                                        if (user.getUserId().equals(cm.getSenderId())) {// 该会话中最新一条消息是自己发给别人的
                                            friendId = cm.getReceiverId();
                                            itemShowTag = cm.getSenderId() + "--" + cm.getReceiverId();
                                        } else {// 该会话中最新一条消息是别人发给自己的
                                            friendId = cm.getSenderId();
                                            itemShowTag = cm.getReceiverId() + "--" + cm.getSenderId();
                                        }

                                        // 标识该会话条目不显示在列表中
                                        sp.edit().putBoolean(itemShowTag, false).commit();

                                        // 更新该会话的状态，标记为已读
                                        new updateMsgStatusThread(friendId).start();
                                    }
                                }).setNegativeButton("取消", null).show();

                return true;
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        // 判断是否需要显示班级消息
        MLog.i("判断是否需要显示班级消息：classMessage=" + user.getClassMessage());
        if (user.getClassMessage() == 0) {
            lLayout_class.setVisibility(View.VISIBLE);
            showClass = true;
        } else {
            lLayout_class.setVisibility(View.GONE);
            showClass = false;
        }

        listview_msg.addHeaderView(classMsgHeadView);

        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);
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
                    List<PersonMessage> data = (List<PersonMessage>) msg.obj;

                    // 清空旧数据
                    msgListActivity.messageList.clear();

                    // 用新集合来过滤需要去除的数据
                    List<PersonMessage> sortMsgs = new ArrayList<PersonMessage>();
                    // 检测是否为删除过的会话条目，以及是否为有新消息的会话条目
                    SharedPreferences sp = msgListActivity.getSharedPreferences("msg_pref", AppInfo.getSPMODE());
                    for (PersonMessage cm : data) {
                        // 在pref配置文件中以“userId--friendId”形式来唯一确定一条会话
                        String itemShowTag = "";
                        if (msgListActivity.user.getUserId().equals(
                                cm.getSenderId())) {// 该会话中最新一条消息是自己发给别人的
                            itemShowTag = cm.getSenderId() + "--"
                                    + cm.getReceiverId();
                        } else {// 该会话中最新一条消息是别人发给自己的
                            itemShowTag = cm.getReceiverId() + "--"
                                    + cm.getSenderId();
                        }

                        int newCount = cm.getNewMsgCount();
                        if (newCount > 0) {// 只要该会话有新消息就标识该会话条目显示在列表中
                            sp.edit().putBoolean(itemShowTag, true).commit();
                            sortMsgs.add(cm);
                        } else {// 如果该会话为旧数据，则检测是否删除过该会话条目
                            boolean show = sp.getBoolean(itemShowTag, true);
                            if (!show) {// 假如该会话条目被删除过
                                // NO-OP
                            } else {
                                sortMsgs.add(cm);
                            }
                        }
                    }

                    msgListActivity.messageList.addAll(sortMsgs);

                    // 通知适配器更新数据
                    if (msgListActivity.msgListAdapter == null) {
                        msgListActivity.msgListAdapter = new MsgListAdapter(msgListActivity, msgListActivity.messageList, msgListActivity.user.getUserId());
                        msgListActivity.listview_msg.setAdapter(msgListActivity.msgListAdapter);
                    } else {
                        msgListActivity.msgListAdapter.notifyDataSetChanged();
                    }

                    msgListActivity.lLayout_loading.setVisibility(View.GONE);
                    break;
                case FlagCode.CODE_1:// 查询到最新的一条班级消息
                    List<ClassMessage> clzMsges = (List<ClassMessage>) msg.obj;
                    if (clzMsges != null && clzMsges.size() > 0) {
                        ClassMessage latestClassMsg = clzMsges.get(0);// 获取最新的一条班级消息实体

                        // 班级名称
                        msgListActivity.classMsgHeadView_txt_name.setText(latestClassMsg.getClazzName());

                        // 如果图片不为空则显示图片字样
                        String content = latestClassMsg.getContent();
                        String picUrl = latestClassMsg.getPicUrl();
                        if (!CheckUtil.stringIsBlank(picUrl)) {
                            msgListActivity.classMsgHeadView_txt_content.setText(latestClassMsg.getSenderName() + ": [图片]");
                        } else {
                            msgListActivity.classMsgHeadView_txt_content.setText(latestClassMsg.getSenderName() + ": " + content);
                        }

                        // 时间
                        msgListActivity.classMsgHeadView_txt_time.setText(TimeUtil.messageFormat(latestClassMsg.getSendDate()));
                    }

                    // 消息数目
                    int clazzMsgCount = msg.arg1;
                    if (clazzMsgCount == 0) {
                        msgListActivity.classMsgHeadView_txt_count.setVisibility(View.GONE);
                    } else {
                        msgListActivity.classMsgHeadView_txt_count.setVisibility(View.VISIBLE);
                        if (clazzMsgCount < 100) {
                            msgListActivity.classMsgHeadView_txt_count.setText(String.valueOf(clazzMsgCount));
                        } else {
                            msgListActivity.classMsgHeadView_txt_count.setText("...");
                        }
                    }
                    break;
                case FlagCode.CODE_2:// 删除一条会话后更新数据库信息状态成功后刷新页面
                    msgListActivity.onResume();
                    break;
                case FlagCode.CODE_3:// 点击刷新同步个人和班级消息成功后刷新页面
                    msgListActivity.onResume();
                    msgListActivity.lLayout_loading.setVisibility(View.GONE);
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
            }
        }
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
    }
}
