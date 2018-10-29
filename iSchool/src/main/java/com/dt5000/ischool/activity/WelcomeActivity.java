package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.thread.LoginThread;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.HelpUtils;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * 欢迎页面
 *
 * @author 周锋
 * @date 2016年1月18日 下午3:26:51
 * @ClassInfo com.dt5000.ischool.activity.WelcomeActivity
 * @Description
 */
public class WelcomeActivity extends Activity {

    private SharedPreferences sharedPreferences;

    private LinearLayout lLayout_login_progress;
    private int loadingTime = 2;// 计时模拟加载时间
    private Timer timer = new Timer();

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            loadingTime--;
            handler.sendEmptyMessage(FlagCode.SPECIAL);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagCode.SPECIAL:
                    if (loadingTime <= 0) {
                        timer.cancel();
                        login();
                    }
                    break;
                case FlagCode.SUCCESS:// 登录成功
                    sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
                    String password = sharedPreferences.getString("password", "");
                    HelpUtils.save(WelcomeActivity.this, password, null);
                    // 跳转到主页面
                    startActivity(new Intent(WelcomeActivity.this, MainTabActivity.class));

                    // 关闭本页面
                    WelcomeActivity.this.finish();
                    break;
                case FlagCode.FAIL:// 登录失败
                    loginFail();
                    break;
                case FlagCode.EXCEPTION:// 登录异常
                    loginException();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        initView();
        init();
    }

    private void initView() {
        lLayout_login_progress = (LinearLayout) findViewById(R.id.lLayout_login_progress);
    }

    private void init() {
        // 开始计时
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 登录
     */
    private void login() {
        // 判断之前是否登录过
        User user = User.getUser(this);
        if (user == null) {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            WelcomeActivity.this.finish();
        } else {
            sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);

            // 封装参数
            Map<String, String> map = new HashMap<String, String>();
            map.put("operationType", UrlProtocol.OPERATION_TYPE_LOGIN);
            map.put("userName", sharedPreferences.getString("username", ""));
            map.put("pwd", sharedPreferences.getString("password", ""));
            map.put("role", sharedPreferences.getString("role", "0"));
            List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, this, "");
            String httpURL = UrlBulider.getHttpURL(map, this, "");
            MLog.i("登录地址：" + httpURL);

            // 发送请求
            lLayout_login_progress.setVisibility(View.VISIBLE);
            new Thread(new LoginThread(this, httpParams, handler)).start();
        }
    }

    /**
     * 登录失败后做相应处理
     */
    private void loginFail() {
        // 周锋修改 20160519 如果登录的时候出现网络问题，不再清除本地用户信息，而是提示用户网络连接异常
        // 清空所有配置文件中的信息
        // getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO,
        // Context.MODE_PRIVATE).edit().clear().commit();// 用户信息配置文件
        // getSharedPreferences(MCon.SHARED_PREFERENCES_VIP_INFO,
        // Context.MODE_PRIVATE).edit().clear().commit();// VIP信息配置文件

        // 跳转到登录页面
        // startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));

        new AlertDialog.Builder(this).setMessage("目前无法登录,请重新登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO,
                                Context.MODE_PRIVATE).edit().clear().commit();// 用户信息配置文件
                        getSharedPreferences(MCon.SHARED_PREFERENCES_VIP_INFO,
                                Context.MODE_PRIVATE).edit().clear().commit();// VIP信息配置文件

                        //跳转到登录页面
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void loginException() {
        new AlertDialog.Builder(this).setMessage("目前无法登录，请检查网络连接是否正常")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WelcomeActivity.this.finish();
                    }
                }).setCancelable(false).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 拦截返回键
        }
        return true;
    }

}
