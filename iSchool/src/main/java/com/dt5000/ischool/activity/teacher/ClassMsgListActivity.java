package com.dt5000.ischool.activity.teacher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.ClassMsgTalkListActivity;
import com.dt5000.ischool.adapter.teacher.ClassMsgListAdapter;
import com.dt5000.ischool.db.ClassMessageDBManager;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级消息列表页面：教师端
 *
 * @author 周锋
 * @date 2016年1月21日 上午10:21:34
 * @ClassInfo com.dt5000.ischool.activity.teacher.ClassMsgListActivity
 * @Description
 */
public class ClassMsgListActivity extends Activity {

    private TextView txt_title;
    private LinearLayout lLayout_back;
    private LinearLayout lLayout_loading;
    private ListView listview_msg;

    private User user;
    private ClassMsgListAdapter classMessageAdpter;
    private List<ClassMessage> classMessageList = new ArrayList<ClassMessage>();
    private MyHandler handler = new MyHandler(this);

    // 动态广播接收消息推送
    private BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_msg_list);

        initView();
        initListener();
        init();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("班级消息");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
        listview_msg = (ListView) findViewById(R.id.listview_msg);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassMsgListActivity.this.finish();
            }
        });

        // 点击进入班级消息界面
        listview_msg.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView txt_count = (TextView) view.findViewById(R.id.txt_count);
                txt_count.setVisibility(View.GONE);

                ClassMessage classMessage = classMessageList.get(position);
                Intent intent = new Intent(ClassMsgListActivity.this, ClassMsgTalkListActivity.class);
                intent.putExtra("classInfoID", classMessage.getClassinfoID());
                intent.putExtra("className", classMessage.getClazzName());
                startActivity(intent);
            }
        });

        // 长按删除聊天
        listview_msg.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                final ClassMessage cm = classMessageList.get(position);

                new AlertDialog.Builder(ClassMsgListActivity.this)
                        .setMessage("删除该聊天？")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sp = getSharedPreferences("class_msg_pref", Context.MODE_PRIVATE);

                                        // 在pref配置文件中以“userId--classId”形式来唯一确定一条会话
                                        String itemShowTag = user.getUserId() + "--" + cm.getClassinfoID();
                                        // 标识该会话条目不显示在列表中
                                        sp.edit().putBoolean(itemShowTag, false).commit();
                                        // 更新该会话的状态，标记为已读
                                        new updateClassMsgStatusThread(cm.getClassinfoID()).start();
                                    }
                                }).setNegativeButton("取消", null).show();

                return true;
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);
    }

    /**
     * 发送请求获取教师所带班级列表，再根据班级列表去查询本地数据库中对应的班级消息
     */
    class queryMsgThread extends Thread {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            Message handlerMessage = new Message();
            List<ClassMessage> messageList = null;
            ClassMessageDBManager classMessageDBManager = null;
            try {
                // 配置参数
                Map<String, String> map = new HashMap<String, String>();
                map.put("operationType", UrlProtocol.OPERATION_TYPE_TEACHER_CLASS);
                List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, ClassMsgListActivity.this, user.getUserId());

                // 发送请求
                String response = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
                Log.i("queryMsgThread", response.toString());
                // 解析
                JSONObject obj = new JSONObject(response);
                String result = obj.optString("classList");
                List<ClassItem> data = (List<ClassItem>) GsonUtil.jsonToList(result,
                        new TypeToken<List<ClassItem>>() {
                        }.getType());

                // 查询数据库
                if (data != null && data.size() > 0) {
                    classMessageDBManager = new ClassMessageDBManager(ClassMsgListActivity.this);
                    messageList = classMessageDBManager.queryClazzInfo(ClassMsgListActivity.this, data, user.getUserId());
                    handlerMessage.what = FlagCode.CODE_0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handlerMessage.obj = messageList;
                handler.sendMessage(handlerMessage);
                if (classMessageDBManager != null) {
                    classMessageDBManager.closeDB();
                }
            }
        }
    }

    class updateClassMsgStatusThread extends Thread {
        private String classId;

        public updateClassMsgStatusThread(String cid) {
            this.classId = cid;
        }

        @Override
        public void run() {
            ClassMessageDBManager messageManager = null;
            try {
                messageManager = new ClassMessageDBManager(ClassMsgListActivity.this);
                messageManager.updateMsgStatus(user.getUserId(), classId);
                handler.sendEmptyMessage(FlagCode.CODE_1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != messageManager)
                    messageManager.closeDB();
            }
        }
    }

    static class MyHandler extends Handler {
        WeakReference<ClassMsgListActivity> referActivity;

        MyHandler(ClassMsgListActivity activity) {
            referActivity = new WeakReference<ClassMsgListActivity>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ClassMsgListActivity classMsgListActivity = referActivity.get();
            switch (msg.what) {
                case FlagCode.CODE_0: {
                    List<ClassMessage> data = (List<ClassMessage>) msg.obj;

                    // 清空旧数据
                    classMsgListActivity.classMessageList.clear();

                    // 用新集合来过滤需要去除的数据
                    List<ClassMessage> sortMsgs = new ArrayList<ClassMessage>();
                    // 检测是否为删除过的会话条目，以及是否为有新消息的会话条目
                    SharedPreferences sp = classMsgListActivity.getSharedPreferences("class_msg_pref", MODE_PRIVATE);
                    for (ClassMessage cm : data) {
                        // 在pref配置文件中以“userId--classId”形式来唯一确定一条会话
                        String itemShowTag = classMsgListActivity.user.getUserId() + "--" + cm.getClassinfoID();

                        int newCount = cm.getNewClzMsgCount();
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

                    classMsgListActivity.classMessageList.addAll(sortMsgs);

                    // 通知适配器更新数据
                    if (classMsgListActivity.classMessageAdpter == null) {
                        classMsgListActivity.classMessageAdpter = new ClassMsgListAdapter(classMsgListActivity, classMsgListActivity.classMessageList);
                        classMsgListActivity.listview_msg.setAdapter(classMsgListActivity.classMessageAdpter);
                    } else {
                        classMsgListActivity.classMessageAdpter.notifyDataSetChanged();
                    }
                    classMsgListActivity.lLayout_loading.setVisibility(View.GONE);
                    break;
                }
                case FlagCode.CODE_1:// 删除一条会话后更新数据库信息状态成功后刷新页面
                    classMsgListActivity.onResume();
                    break;
            }
        }
    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getUser(this);
        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "ClassMsgList").commit();

        // 激活页面时查询本地数据库中班级消息
        if (user.getClassMessage() == 0) {
            lLayout_loading.setVisibility(View.VISIBLE);
            new queryMsgThread().start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
    }

}
