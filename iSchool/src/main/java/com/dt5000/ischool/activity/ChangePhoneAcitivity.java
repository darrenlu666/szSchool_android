package com.dt5000.ischool.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.RetrofitService;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.widget.dialog.ChangePhoneDialog;
import com.google.gson.Gson;

public class ChangePhoneAcitivity extends Activity {
    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txtOldPhone;
    private EditText input_phone;
    private Button btnConfim;

    private User user = null;
    private String newPhone = null;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case 0:
                    SharedPreferences sp = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
                    user.setPhone(newPhone);
                    sp.edit().putString("userJson", new Gson().toJson(user)).commit();
                    onResume();
                    finish();
                    break;
                case -1:
                    Toast.makeText(ChangePhoneAcitivity.this, "手机号修改失败!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_acitivity);
        initView();
        initLitener();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("手机号修改中...");
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.change_phone));
        txtOldPhone = (TextView) findViewById(R.id.txtOldPhone);
        input_phone = (EditText) findViewById(R.id.input_phone);
        btnConfim = (Button) findViewById(R.id.btnConfim);
    }

    private void initLitener() {
        lLayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = input_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(ChangePhoneAcitivity.this, "手机号不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ChangePhoneDialog phoneDialog = new ChangePhoneDialog(ChangePhoneAcitivity.this);
                phoneDialog.setConfirmPhone(phone).setOnSelectListener(new ChangePhoneDialog.OnSelectListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean isYes) {
                        dialog.dismiss();
                        if (isYes && user != null) {
                            newPhone = phone;
                            progressDialog.show();
                            RetrofitService.changeUserPhone(ChangePhoneAcitivity.this, user, phone, handler);
                        }
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = User.getUser(this);
        init();
    }

    private void init() {
        txtOldPhone.setText("系统中手机号码:" + user.getPhone());
    }
}
