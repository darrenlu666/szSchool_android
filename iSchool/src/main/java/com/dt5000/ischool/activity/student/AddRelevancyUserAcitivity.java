package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.thread.LoginThread;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.HelpUtils;
import com.dt5000.ischool.widget.dialog.LoginDialog;

import org.apache.http.NameValuePair;

import java.util.List;

public class AddRelevancyUserAcitivity extends Activity {
    private LinearLayout lLayout_back;
    private EditText edtName, edtUser, edtPassword;
    private Button btnAdd;

    private ProgressDialog progressDialog;
    private LoginDialog dialog;

    private String password = null;

    private User user;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 关闭加载进度条
            progressDialog.dismiss();
            switch (msg.what) {
                case FlagCode.SUCCESS:
                    User user1 = (User) msg.obj;
                    HelpUtils.save(AddRelevancyUserAcitivity.this, password, user1);
                    startActivity(new Intent(AddRelevancyUserAcitivity.this, SwtichUserAcitivity.class));
                    finish();
                    break;
                case FlagCode.FAIL:
                    dialog.setContent("你输入的信息有误!").show();
                    break;
                case FlagCode.EXCEPTION:
                    dialog.setContent("你输入的信息有误!").show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_add_relevancy_user_acitivity);

        initView();
        initLitener();
        init();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        edtName = (EditText) findViewById(R.id.edtName);
        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnAdd = (Button) findViewById(R.id.btnAdd);
    }

    private void initLitener() {
        lLayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtUser.getText().toString();
                password = edtPassword.getText().toString();
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)) {
                    Toast.makeText(AddRelevancyUserAcitivity.this, "请输入用户名或者密码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<NameValuePair> httpParams = HelpUtils.loginMessage(AddRelevancyUserAcitivity.this, "0", name, password);
                progressDialog.show();
                new Thread(new LoginThread(AddRelevancyUserAcitivity.this, httpParams, handler)).start();
            }
        });
    }


    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("登陆中...");

        //提示
        dialog = new LoginDialog(this);

        user = User.getUser(this);
    }
}
