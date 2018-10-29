package com.dt5000.ischool.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AppInfo;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.HelpUtils;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * 设置页面
 *
 * @author 周锋
 * @date 2016年1月30日 下午4:39:34
 * @ClassInfo com.dt5000.ischool.activity.SettingActivity
 * @Description
 */
public class SettingActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_notice;
    private TextView txt_password;
    private TextView txt_disturb;
    private TextView txt_update;
    private TextView txt_about;
    private TextView txt_help;
    private TextView txt_logout;
    private TextView txt_version_code;
    private TextView txt_changePhone;

    private User user;
    private final int logut = 0x123;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case logut:
                    String result = (String) msg.obj;
                    MLog.i("登出返回结果：" + result);
                    if (!CheckUtil.stringIsBlank(result)) {
                        HelpUtils.cleanMessage(true, SettingActivity.this);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_setting);
        initView();
        initListener();
        init();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("设置");
        txt_notice = (TextView) findViewById(R.id.txt_notice);
        txt_password = (TextView) findViewById(R.id.txt_password);
        txt_disturb = (TextView) findViewById(R.id.txt_disturb);
        txt_update = (TextView) findViewById(R.id.txt_update);
        txt_about = (TextView) findViewById(R.id.txt_about);
        txt_help = (TextView) findViewById(R.id.txt_help);
        txt_logout = (TextView) findViewById(R.id.txt_logout);
        txt_version_code = (TextView) findViewById(R.id.txt_version_code);
        txt_changePhone = (TextView) findViewById(R.id.txt_changePhone);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        // 点击进入消息提醒页面
        txt_notice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,
                        NoticeSetActivity.class));
            }
        });

        // 点击进入修改密码页面
        txt_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,
                        ModPasswordActivity.class));
            }
        });

        // 点击进入免打扰设置页面
        txt_disturb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,
                        DisturbSetActivity.class));
            }
        });

        // 点击检查升级
        txt_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });

        // 点击进入关于我们
        txt_about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,
                        AboutActivity.class));
            }
        });

        // 点击进入帮助页面
        txt_help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
            }
        });

        // 点击退出程序
        txt_logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setMessage("退出当前帐号？")
                        .setPositiveButton("退出",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        final List<NameValuePair> httpParams = HelpUtils.logoutMessage(user, SettingActivity.this);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String result = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
                                                    Message message = Message.obtain();
                                                    message.what = logut;
                                                    message.obj = result;
                                                    handler.sendMessage(message);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                }).setNegativeButton("取消", null).show();
            }
        });

        txt_changePhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ChangePhoneAcitivity.class));
            }
        });
    }

    private void init() {
        user = User.getUser(this);
        try {
            // 设置版本号
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version_code.setText(packageInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate() {
        new FinalHttp().get(UrlProtocol.APP_UPDATE, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String t) {
                MLog.i("软件更新返回结果：" + t);
                String result = t.replace("\\", "");
                try {
                    final AppInfo appInfo = (AppInfo) GsonUtil.jsonToBean(
                            result, AppInfo.class);
                    if (appInfo != null) {
                        int currentVersionCode = getPackageManager()
                                .getPackageInfo(
                                        SettingActivity.this.getPackageName(),
                                        0).versionCode;

                        if (appInfo.getVerCode() > currentVersionCode) {
                            Builder builder = new AlertDialog.Builder(
                                    SettingActivity.this);
                            builder.setTitle("更新提示");
                            builder.setMessage("发现新版本" + appInfo.getVerName());
                            builder.setPositiveButton("升级",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Intent intent = new Intent(
                                                    SettingActivity.this,
                                                    UpdateActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("appInfo",
                                                    appInfo);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                            if (!appInfo.isForced()) {
                                // 假如不是强制升级，则添加"取消"按钮
                                builder.setNegativeButton("取消", null);
                            } else {
                                // 假如强制升级，则对话框无法点击返回键消除
                                builder.setCancelable(false);
                            }
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        } else {
                            DialogAlert.show(SettingActivity.this, "当前已是最新版");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
