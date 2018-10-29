package com.dt5000.ischool.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.student.AttendanceListActivity;
import com.dt5000.ischool.activity.student.CollectChooseActivity;
import com.dt5000.ischool.activity.student.SwtichUserAcitivity;
import com.dt5000.ischool.activity.student.VipPayChooseActivity;
import com.dt5000.ischool.activity.teacher.AttendanceClassListActivity;
import com.dt5000.ischool.entity.Banner;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我页面
 *
 * @author 周锋
 * @date 2016年1月7日 下午1:34:18
 * @ClassInfo com.dt5000.ischool.activity.MineActivity
 * @Description
 */
public class MineActivity extends Activity {

    private ImageView img_banner;
    private RelativeLayout rLayout_to_collect;
    private RelativeLayout rLayout_to_order;
    private RelativeLayout rLayout_to_personal;
    private RelativeLayout rLayout_to_feedback;
    private RelativeLayout rLayout_to_setting;
    private RelativeLayout rLayout_to_attendance;
    private RelativeLayout rLayout_to_swtich;
    private TextView txt_vacate_count;

    private User user;
    private List<Banner> bannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_mine);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        img_banner = (ImageView) findViewById(R.id.img_banner);
        rLayout_to_collect = (RelativeLayout) findViewById(R.id.rLayout_to_collect);
        rLayout_to_order = (RelativeLayout) findViewById(R.id.rLayout_to_order);
        rLayout_to_personal = (RelativeLayout) findViewById(R.id.rLayout_to_personal);
        rLayout_to_feedback = (RelativeLayout) findViewById(R.id.rLayout_to_feedback);
        rLayout_to_setting = (RelativeLayout) findViewById(R.id.rLayout_to_setting);
        rLayout_to_attendance = (RelativeLayout) findViewById(R.id.rLayout_to_attendance);
        rLayout_to_swtich = (RelativeLayout) findViewById(R.id.rLayout_to_swtich);
        txt_vacate_count = (TextView) findViewById(R.id.txt_vacate_count);
    }

    private void initListener() {
        // 点击跳转到公告详情页
        img_banner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bannerList != null && bannerList.size() >= 3) {
                    Banner banner = bannerList.get(2);
                    Intent intent = new Intent(MineActivity.this, BannerDetailActivity.class);
                    intent.putExtra("linkUrl", banner.getLinkUrl());
                    startActivity(intent);
                }
            }
        });

        // 点击跳转到个人信息页面
        rLayout_to_personal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this, PersonalActivity.class));
            }
        });

        // 点击跳转到意见反馈页面
        rLayout_to_feedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this, FeedbackActivity.class));
            }
        });

        // 点击跳转到系统设置页面
        rLayout_to_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this, SettingActivity.class));
            }
        });

        // 点击跳转到我的收藏页面
        rLayout_to_collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this,
                        CollectChooseActivity.class));
            }
        });

        // 点击跳转到我的订单页面
        rLayout_to_order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this, VipPayChooseActivity.class));
//                SharedPreferences sp = getSharedPreferences(MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
//                String vipStatus = sp.getString("vipStatus", "0");
//                if ("1".equals(vipStatus)) {// 是VIP进入VIP信息界面
//                    startActivity(new Intent(MineActivity.this, VipInfoActivity.class));
//                } else {// 不是VIP进入支付页面
//                    startActivity(new Intent(MineActivity.this, VipPayChooseActivity.class));
//                }
            }
        });

        // 点击跳转到考勤页面
        rLayout_to_attendance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将新考勤请假的标识清空
                SharedPreferences sharedPreferences = getSharedPreferences(
                        MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("newVacate", false).commit();

                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(MineActivity.this,
                            AttendanceClassListActivity.class));
                } else {
                    startActivity(new Intent(MineActivity.this,
                            AttendanceListActivity.class));
                }
            }
        });

        rLayout_to_swtich.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this, SwtichUserAcitivity.class));
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        // 判断角色类型
        if (User.isTeacherRole(user.getRole())) {
            // 教师端没有我的收藏和VIP用户模块
            rLayout_to_collect.setVisibility(View.INVISIBLE);
            rLayout_to_order.setVisibility(View.INVISIBLE);
            rLayout_to_swtich.setVisibility(View.INVISIBLE);
        }

        // 注册广播监听请假考勤推送
        IntentFilter filter = new IntentFilter(
                "com.dt5000.ischool.action.vacate");
        registerReceiver(vacateBroadcastReceiver, filter);
    }

    // 动态广播接收请假考勤推送
    private BroadcastReceiver vacateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNewVacate();
        }
    };

    private void getData() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_BANNER);
        map.put("schoolId", String.valueOf(user.getSchoolbaseinfoId()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());

        // 发送请求
        new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("轮播数据返回结果：" + t);
                        try {
                            // 取出Json串
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("banners");

                            // 解析实体类
                            Type listType = new TypeToken<List<Banner>>() {
                            }.getType();
                            List<Banner> data = (List<Banner>) GsonUtil
                                    .jsonToList(result, listType);

                            // 判断返回的数据
                            if (data != null && data.size() >= 3) {
                                bannerList = data;

                                Banner banner = bannerList.get(2);
                                ImageLoaderUtil.createSimple(MineActivity.this)
                                        .displayImage(banner.getImageUrl(),
                                                img_banner);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 查询本地有没有保存新考勤请假的标识
     */
    private void checkNewVacate() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        boolean newVacate = sharedPreferences.getBoolean("newVacate", false);
        if (newVacate) {
            txt_vacate_count.setVisibility(View.VISIBLE);
        } else {
            txt_vacate_count.setVisibility(View.GONE);
        }

        // 清除考勤请假通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "Mine").commit();

        checkNewVacate();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.i("MineActivity.onDestroy");

        // 注销广播
        unregisterReceiver(vacateBroadcastReceiver);
    }

    /**
     * 用于监听返回键的时间计时
     */
    private long lastBackClickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 点击返回，判断时间间隔
            if ((System.currentTimeMillis() - lastBackClickTime) > 2000) {
                MToast.show(MineActivity.this, "再按一次退出", MToast.SHORT);
                lastBackClickTime = System.currentTimeMillis();
            } else {
                // 退出程序
                super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return false;
    }

}
