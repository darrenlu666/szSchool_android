package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.thread.LoginThread;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.HelpUtils;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.NetWorkUtils;
import com.dt5000.ischool.widget.dialog.LoginDialog;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * 登录页面
 *
 * @author 周锋
 * @date 2016年1月13日 上午10:47:36
 * @ClassInfo com.dt5000.ischool.activity.LoginActivity
 * @Description
 */
public class LoginActivity extends Activity {

    private EditText edit_username;
    private EditText edit_password;
    private Button btn_login;
    private ImageView img_clear_username;
    private ImageView img_clear_password;

    private ProgressDialog progressDialog;
    private LoginDialog dialog;
    private String username;
    private String password;

    /**
     * 登录时用到的角色区分，0表示学生，1表示教师，登录时首先用0，如果登录失败再尝试用1
     */
    private String loginRole = "0";

    /**
     * 登录是区分角色的，如果用户名和密码都正确但是登录失败，就是角色不对，此时应该再次变更角色登录<br>
     * 该标识就是记录是否已经尝试进行过第二次登录，如果第二次登录也失败就不再继续
     */
    private boolean hasSecondLogin = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagCode.SUCCESS:// 登录成功
                    HelpUtils.save(LoginActivity.this, password,null);

                    // 关闭加载进度条
                    progressDialog.dismiss();

                    // 跳转到主页面
                    startActivity(new Intent(LoginActivity.this, MainTabActivity.class));

                    // 关闭本页面
                    LoginActivity.this.finish();
                    break;
                case FlagCode.FAIL:// 登录失败
                    if (!hasSecondLogin) {
                        // 进行第二次登录，变更角色
                        loginRole = "1";
                        login();
                        hasSecondLogin = true;
                    } else {
                        progressDialog.dismiss();
                        dialog.setContent("用户名或密码错误!").show();
                    }
                    break;
                case FlagCode.EXCEPTION:// 登录异常
                    progressDialog.dismiss();
                    dialog.setContent("网络连接失败!").show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
        init();
    }

    public void initView() {
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        img_clear_username = (ImageView) findViewById(R.id.img_clear_username);
        img_clear_password = (ImageView) findViewById(R.id.img_clear_password);
    }

    private void initListener() {
        // 点击登录
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // 点击清除用户名
        img_clear_username.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_username.setText("");
            }
        });

        // 点击清除密码
        img_clear_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_password.setText("");
            }
        });

        // 监听用户名输入
        edit_username.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    img_clear_username.setVisibility(View.VISIBLE);
                } else {
                    img_clear_username.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 监听密码输入
        edit_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    img_clear_password.setVisibility(View.VISIBLE);
                } else {
                    img_clear_password.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("登录中...");

        //提示
        dialog = new LoginDialog(this);
    }

    /**
     * 登录
     */
    private void login() {
        username = edit_username.getText().toString().trim();
        password = edit_password.getText().toString().trim();

        if (CheckUtil.stringIsBlank(username)) {
            MToast.show(LoginActivity.this, "请输入用户名", MToast.SHORT);
            return;
        }

        if (CheckUtil.stringIsBlank(password)) {
            MToast.show(LoginActivity.this, "请输入密码", MToast.SHORT);
            return;
        }

        //判断网络状态
        if (!NetWorkUtils.isNetworkConnected(this)) {
            Message message = Message.obtain();
            message.what = FlagCode.EXCEPTION;
            handler.sendMessage(message);
        }

        // 封装参数
        List<NameValuePair> httpParams = HelpUtils.loginMessage(this, loginRole, username, password);

        // 显示进度条
        progressDialog.show();

        // 登录判断标识置为初始状态
        hasSecondLogin = false;
        loginRole = "0";

        // 发送请求
        hideSoftInput(getCurrentFocus().getWindowToken());
        new Thread(new LoginThread(this, httpParams, handler)).start();
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}