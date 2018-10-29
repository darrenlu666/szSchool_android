package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.LoginActivity;
import com.dt5000.ischool.activity.MainTabActivity;
import com.dt5000.ischool.adapter.student.SwitchUserAdapter;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.UserInfo;
import com.dt5000.ischool.eventbus.ChangeUser;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.thread.LoginThread;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.HelpUtils;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.dt5000.ischool.widget.dialog.LoginDialog;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class SwtichUserAcitivity extends Activity implements SwitchUserAdapter.onSwipeMenuViewClickListener {
    private String tag = SwtichUserAcitivity.class.getSimpleName();
    private LinearLayout lLayout_back;
    private TextView txt_title;
    private RecyclerView recyclerView;
    private LinearLayout addRelevancyUserLayout;

    private SwitchUserAdapter adapter;
    private User user;

    private ProgressDialog progressDialog;
    private LoginDialog dialog;
    private UserInfo userInfo;

    private boolean isRuning = true;
    private final int logut = 0x123;
    private final int changUser = 0x345;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 关闭加载进度条
            progressDialog.dismiss();
            switch (msg.what) {
                case FlagCode.SUCCESS:// 登录成功
                    if (userInfo != null) {
                        HelpUtils.save(SwtichUserAcitivity.this, userInfo.getPwd(),null);
                    }
                    // 跳转到主页面
                    startActivity(new Intent(SwtichUserAcitivity.this, MainTabActivity.class));
                    // 关闭本页面
                    finish();
                    break;
                case FlagCode.FAIL:
                    dialog.setContent("切换失败!").show();
                    break;
                case FlagCode.EXCEPTION:
                    dialog.setContent("网络连接失败!").show();
                    break;
                case logut:
                    String result = (String) msg.obj;
                    MLog.i("登出返回结果：" + result);
                    if (!CheckUtil.stringIsBlank(result)) {
                        HelpUtils.cleanMessage(true, SwtichUserAcitivity.this);
                        DaoHelper.deleteUserInfo(userInfo);
                        startActivity(new Intent(SwtichUserAcitivity.this, LoginActivity.class));
                    }
                    break;
                case changUser:
                    String result1 = (String) msg.obj;
                    MLog.i("登出返回结果：" + result1);
                    if (!CheckUtil.stringIsBlank(result1)) {
                        HelpUtils.cleanMessage(false, SwtichUserAcitivity.this);
                        switchUser();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_swtich_user_acitivity);
        //注解eventbus
        EventBus.getDefault().register(this);
        initView();
        init();
        initListener();
        getUserInfo();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("切换帐号");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        addRelevancyUserLayout = (LinearLayout) findViewById(R.id.addRelevancyUserLayout);
    }

    private void initListener() {
        lLayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addRelevancyUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SwtichUserAcitivity.this, AddRelevancyUserAcitivity.class));
                finish();
            }
        });
    }

    private void init() {
        user = User.getUser(this);
        adapter = new SwitchUserAdapter(this);
        adapter.setOnSwipeMenuViewClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("切换中...");

        //提示
        dialog = new LoginDialog(this);
    }

    private void getUserInfo() {
        List<UserInfo> userInfos = MainApplication.getDaoSession().getUserInfoDao().queryBuilder().list();
        if (userInfos != null && userInfos.size() > 0) {
            adapter.setUserInfos(userInfos);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onEventChangeUser(final ChangeUser changeUser) {
        Log.i("onEventChangeUser", "onEventChangeUser");
        isRuning = true;
        // 封装参数
        userInfo = changeUser.getInfo();
        new MyThread(changUser).start();
    }

    public void switchUser() {
        progressDialog.show();
        Log.i("switchUser", "switchUser---" + userInfo.getUserName() + userInfo.getPwd() + userInfo.getRole());
        List<NameValuePair> httpParams = HelpUtils.loginMessage(SwtichUserAcitivity.this, "0", userInfo.getUserName(), userInfo.getPwd());
        new Thread(new LoginThread(SwtichUserAcitivity.this, httpParams, handler)).start();
    }

    @Override
    public void onswipeClick(int position, UserInfo userInfo) {
        this.userInfo = userInfo;
        isRuning = true;
        if (userInfo.getUserId().equals(user.getUserId())) {
            new MyThread(logut).start();
        } else {
            DaoHelper.deleteUserInfo(userInfo);
        }
    }


    public class MyThread extends Thread {
        private int what;

        public MyThread(int what) {
            this.what = what;
        }

        @Override
        public void run() {
            super.run();
            try {
                while (isRuning) {
                    final List<NameValuePair> httpParams = HelpUtils.logoutMessage(user, SwtichUserAcitivity.this);
                    String result = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
                    Message message = Message.obtain();
                    message.what = what;
                    message.obj = result;
                    handler.sendMessage(message);
                    isRuning = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        finish();
    }
}