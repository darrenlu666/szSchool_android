package com.dt5000.ischool.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.student.VipPayChooseActivity;
import com.dt5000.ischool.entity.AppInfo;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 主页面
 *
 * @author 周锋
 * @date 2016年1月7日 上午10:37:17
 * @ClassInfo com.dt5000.ischool.activity.MainTabActivity
 * @Description
 */
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements OnCheckedChangeListener {

    private TabHost tabHost;
    private RadioGroup radio_group_main;

    private static final String TAB_HOME = "tab_home";
    private static final String TAB_INCLASS = "tab_inclass";
    private static final String TAB_OUTCLASS = "tab_outclass";
    private static final String TAB_MINE = "tab_mine";

    private Intent homeIntent;
    private Intent inclassIntent;
    private Intent outclassIntent;
    private Intent mineIntent;

    /**
     * 用于标识是否在本次使用当中已经进行过VIP剩余天数判断
     */
    private boolean hasJudgeVipRemainDays = false;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_main_tab);

        initView();
        initListener();
        init();
        checkUpdate();
    }

    private void initView() {
        radio_group_main = (RadioGroup) findViewById(R.id.radio_group_main);
    }

    private void initListener() {
        radio_group_main.setOnCheckedChangeListener(this);
    }

    private void init() {
        // 初始化跳转的intent
        homeIntent = new Intent(this, HomeActivity.class);
        inclassIntent = new Intent(this, InclassActivity.class);
        outclassIntent = new Intent(this, OutclassActivity.class);
        mineIntent = new Intent(this, MineActivity.class);

        // 设置标签
        setTabs();

        // 标记主页面已经创建好
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("MainTabExist", true).commit();

        // 清除通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        user = User.getUser(this);
    }

    /**
     * 设置各个标签数据
     */
    private void setTabs() {
        tabHost = getTabHost();

        tabHost.addTab(buildTabSpec(TAB_HOME, R.string.home,
                R.drawable.home_normal, homeIntent));
        tabHost.addTab(buildTabSpec(TAB_INCLASS, R.string.inclass,
                R.drawable.inclass_normal, inclassIntent));
        tabHost.addTab(buildTabSpec(TAB_OUTCLASS, R.string.outclass,
                R.drawable.outclass_normal, outclassIntent));
        tabHost.addTab(buildTabSpec(TAB_MINE, R.string.mine,
                R.drawable.mine_normal, mineIntent));

        // 默认显示首页
        tabHost.setCurrentTabByTag(TAB_HOME);
    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
                                         final Intent content) {
        return tabHost
                .newTabSpec(tag)
                .setIndicator(getString(resLabel), getResources().getDrawable(resIcon))
                .setContent(content);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateVipInfo();
    }

    ;

    /**
     * 每次激活主页面都去发请求更新VIP状态信息
     */
    private void updateVipInfo() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_VIP_STATUS);
        map.put("userId", user.getUserId());
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, MainTabActivity.this, user.getUserId());

        // 发送请求
        new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String t) {
                        if (!CheckUtil.stringIsBlank(t)) {
                            MLog.i("更新VIP信息返回结果：" + t);
                            try {
                                // 将VIP信息写入配置文件
                                JSONObject obj = new JSONObject(t);
                                SharedPreferences sp = getSharedPreferences(
                                        MCon.SHARED_PREFERENCES_VIP_INFO,
                                        Context.MODE_PRIVATE);
                                Editor edit = sp.edit();
                                edit.putString("vipStatus", obj.optString("vipStatus"));
                                edit.putInt("remainDays", obj.optInt("remainDays"));
                                edit.putString("endDate", obj.optString("endDate"));
                                edit.putString("startVip", obj.optString("startVip"));
                                edit.putString("startDate", obj.optString("startDate"));
                                edit.commit();

                                MLog.i("是否已经进行过vip剩余天数的判断：" + hasJudgeVipRemainDays);
                                if (!hasJudgeVipRemainDays) {
                                    judgeVIP();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 判断VIP剩余天数
     */
    private void judgeVIP() {
        try {
            MLog.i("开始进行vip剩余天数判断...");
            hasJudgeVipRemainDays = true;

            // 判断是否开启VIP，以及VIP状态信息
            SharedPreferences sp = getSharedPreferences(
                    MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
            String startVip = sp.getString("startVip", "0");
            String startDate = sp.getString("startDate", "2014-10-01 00:00:00");//
            Date startVipDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).parse(startDate);
            if ("1".equals(startVip)) { // 已经开启VIP收费，需再次判断开启VIP收费的时间
                MLog.i("该学校已经开启VIP收费");
                Date nowDate = new Date();
                if (nowDate.getTime() > startVipDate.getTime()) {// 当前时间已经超过开始收费的时间，则再判断VIP状态
                    MLog.i("开启VIP收费时间已到");
                    String vipStatus = sp.getString("vipStatus", "1");
                    MLog.i("当前学生的VIP状态vipStatus：" + vipStatus);
                    if ("1".equals(vipStatus)) {// 是VIP，需再次判断VIP剩余天数
                        int remainDays = sp.getInt("remainDays", 30);
                        MLog.i("当前学生VIP的remainDays：" + remainDays);
                        if (remainDays <= 15 && remainDays >= 0) {
                            new AlertDialog.Builder(MainTabActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您的VIP还剩余" + remainDays + "天，是否继续使用VIP？")
                                    .setPositiveButton("续费",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 跳转到购买VIP页面
                                                    startActivity(new Intent(MainTabActivity.this, VipPayChooseActivity.class));
                                                }
                                            }).setNegativeButton("取消", null)
                                    .show();
                        }
                    }
                }
            }
        } catch (Exception e) {
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
                    final AppInfo appInfo = (AppInfo) GsonUtil.jsonToBean(result, AppInfo.class);
                    if (appInfo != null) {
                        int currentVersionCode = getPackageManager()
                                .getPackageInfo(MainTabActivity.this.getPackageName(), 0).versionCode;

                        if (appInfo.getVerCode() > currentVersionCode) {
                            Builder builder = new AlertDialog.Builder(MainTabActivity.this);
                            builder.setTitle("更新提示");
                            builder.setMessage("发现新版本" + appInfo.getVerName());
                            builder.setPositiveButton("升级",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(MainTabActivity.this, UpdateActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("appInfo", appInfo);
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_btn_home:// 首页
                tabHost.setCurrentTabByTag(TAB_HOME);
                break;
            case R.id.radio_btn_inclass:// 消息界面
                tabHost.setCurrentTabByTag(TAB_INCLASS);
                break;
            case R.id.radio_btn_outclass:// 自主学习界面
                tabHost.setCurrentTabByTag(TAB_OUTCLASS);
                break;
            case R.id.radio_btn_mine:// 我的界面
                tabHost.setCurrentTabByTag(TAB_MINE);
                break;
        }
    }

//    /**
//     * 用于监听返回键的时间计时
//     */
//    private long lastBackClickTime = 0;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 点击返回，判断时间间隔
//            if ((System.currentTimeMillis() - lastBackClickTime) > 2000) {
//                MToast.show(MainTabActivity.this, "再按一次退出", MToast.SHORT);
//                lastBackClickTime = System.currentTimeMillis();
//            } else {
//                // 退出程序
//                super.onKeyDown(keyCode, event);
//            }
//            return true;
//        }
//
//        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
//            am.adjustStreamVolume (AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//            int i = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
//            Toast.makeText(this, "当前音量值： " + i, Toast.LENGTH_SHORT).show();
//        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
//            am.adjustStreamVolume (AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE , AudioManager.FLAG_SHOW_UI);
//            int i = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
//            Toast.makeText(this, "当前音量值： " + i, Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.i("MainTabActivity.onDestroy");

        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        // 标记主页面已经销毁
        sharedPreferences.edit().putBoolean("MainTabExist", false).commit();
    }

}
