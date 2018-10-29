package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.student.HomeworkListActivity;
import com.dt5000.ischool.activity.student.ScoreActivity;
import com.dt5000.ischool.activity.student.StudySubjectListActivity;
import com.dt5000.ischool.activity.teacher.DiaryClassListActivity;
import com.dt5000.ischool.activity.teacher.GroupWorkListActivity;
import com.dt5000.ischool.activity.teacher.HomeworkClassListActivity;
import com.dt5000.ischool.activity.teacher.ScoreChooseListActivity;
import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.entity.Banner;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
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
 * 课内页面
 *
 * @author 周锋
 * @date 2016年1月7日 下午1:31:19
 * @ClassInfo com.dt5000.ischool.activity.InclassActivity
 * @Description
 */
public class InclassActivity extends Activity {

    private TextView txt_count;
    private TextView txt_homework_count;
    private ImageView img_banner;
    private RelativeLayout rLayout_to_homework;
    private TextView txt_homework;
    private RelativeLayout rLayout_to_score;
    private RelativeLayout rLayout_to_msg;
    private RelativeLayout rLayout_to_lesson;
    private RelativeLayout rLayout_to_study;
    private RelativeLayout rLayout_to_group;
    private RelativeLayout rLayout_to_contact;
    private RelativeLayout rLayout_to_diary;
    private TextView txt_diary;

    private User user;
    private List<Banner> bannerList;
    private FinalHttp finalHttp;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagCode.SUCCESS:// 查询到本地未读消息总数后显示到页面上
                    int count = msg.arg1;
                    if (count > 0) {
                        txt_count.setVisibility(View.VISIBLE);
                        if (count < 100) {
                            txt_count.setText(String.valueOf(count));
                        } else {
                            txt_count.setText("99");
                        }
                    } else {
                        txt_count.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.activityList.add(this);
        setContentView(R.layout.activity_inclass);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        // 初始化View
        img_banner = (ImageView) findViewById(R.id.img_banner);
        txt_count = (TextView) findViewById(R.id.txt_count);
        txt_homework_count = (TextView) findViewById(R.id.txt_homework_count);
        rLayout_to_homework = (RelativeLayout) findViewById(R.id.rLayout_to_homework);
        txt_homework = (TextView) findViewById(R.id.txt_homework);
        rLayout_to_score = (RelativeLayout) findViewById(R.id.rLayout_to_score);
        rLayout_to_msg = (RelativeLayout) findViewById(R.id.rLayout_to_msg);
        rLayout_to_lesson = (RelativeLayout) findViewById(R.id.rLayout_to_lesson);
        rLayout_to_study = (RelativeLayout) findViewById(R.id.rLayout_to_study);
        rLayout_to_group = (RelativeLayout) findViewById(R.id.rLayout_to_group);
        rLayout_to_contact = (RelativeLayout) findViewById(R.id.rLayout_to_contact);
        rLayout_to_diary = (RelativeLayout) findViewById(R.id.rLayout_to_diary);
        txt_diary = (TextView) findViewById(R.id.txt_diary);
    }

    private void initListener() {
        // 点击跳转到公告详情页
        img_banner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bannerList != null && bannerList.size() >= 1) {
                    Banner banner = bannerList.get(0);
                    Intent intent = new Intent(InclassActivity.this,
                            BannerDetailActivity.class);
                    intent.putExtra("linkUrl", banner.getLinkUrl());
                    startActivity(intent);
                }
            }
        });

        // 点击跳转到我的作业页面
        rLayout_to_homework.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(InclassActivity.this,
                            HomeworkClassListActivity.class));
                } else {
                    startActivity(new Intent(InclassActivity.this,
                            HomeworkListActivity.class));
                }
            }
        });

        rLayout_to_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(InclassActivity.this,
                            GroupWorkListActivity.class));
                }
            }
        });

        // 点击跳转到我的成绩页面
        rLayout_to_score.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(InclassActivity.this, ScoreChooseListActivity.class));
                } else {
                    startActivity(new Intent(InclassActivity.this, ScoreActivity.class));
                }
            }
        });

        // 点击跳转到消息盒子页面
        rLayout_to_msg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(
                            InclassActivity.this,
                            com.dt5000.ischool.activity.teacher.MsgListActivity.class));
                } else {
                    startActivity(new Intent(
                            InclassActivity.this,
                            com.dt5000.ischool.activity.student.MsgListActivity.class));
                }
            }
        });

        // 点击跳转到我的日记页面
        rLayout_to_diary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isTeacherRole(user.getRole())) {
                    startActivity(new Intent(InclassActivity.this,
                            DiaryClassListActivity.class));
                } else {
                    startActivity(new Intent(InclassActivity.this,
                            DiaryListActivity.class));
                }
            }
        });

        // 点击跳转到课程表页面
        rLayout_to_lesson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断角色类型
                if (User.isTeacherRole(user.getRole())) {
                    // 教师端课程表暂时不开放，跳转到一个备用页面
                    startActivity(new Intent(InclassActivity.this,
                            LessonTableNoUseActivity.class));
                } else {
                    startActivity(new Intent(InclassActivity.this,
                            LessonTableActivity.class));
                }
            }
        });

        // 点击跳转到自主学习页面
        rLayout_to_study.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InclassActivity.this,
                        StudySubjectListActivity.class));
            }
        });

        // 点击跳转到通讯录页面
        rLayout_to_contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InclassActivity.this, ContactListActivity.class));
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        finalHttp = new FinalHttp();

        // 判断学校类型
        if (User.isKindergarten(user.getSchoolCode())) {
            // 幼儿园没有成绩查询模块、日记模块、自主学习模块
            rLayout_to_score.setVisibility(View.INVISIBLE);
            rLayout_to_diary.setVisibility(View.INVISIBLE);
            rLayout_to_study.setVisibility(View.INVISIBLE);
            rLayout_to_group.setVisibility(View.INVISIBLE);
        }

        // 判断角色类型
        if (User.isTeacherRole(user.getRole())) {
            txt_homework.setText("班级作业");
            txt_diary.setText("班级日记");
            rLayout_to_group.setVisibility(View.VISIBLE);
            // 教师端没有自主学习模块
            rLayout_to_study.setVisibility(View.INVISIBLE);
        } else {
            txt_homework.setText("我的作业");
            txt_diary.setText("我的日记");
            rLayout_to_group.setVisibility(View.INVISIBLE);
        }

        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);
    }

    private void getData() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_BANNER);
        map.put("schoolId", String.valueOf(user.getSchoolbaseinfoId()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
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
                            if (data != null && data.size() >= 1) {
                                bannerList = data;

                                Banner banner = bannerList.get(0);
                                ImageLoaderUtil.createSimple(
                                        InclassActivity.this).displayImage(
                                        banner.getImageUrl(), img_banner);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 动态广播接收消息推送
    private BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 开启线程去查本地数据库中未读消息总数
            new queryMessageCountThread().start();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "Inclass").commit();

        // 开启线程去查本地数据库中未读消息总数
        new queryMessageCountThread().start();

        // 学生端获取动态更新数据
        if (User.isStudentRole(user.getRole())) {
            getDynamics();
        }
    }

    // 获取动态信息，包括作业、博客等
    private void getDynamics() {
        // 获取本地保存过的最大作业、博客等id
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        String homeworkMaxId = sharedPreferences
                .getString("homeworkMaxId", "0");
        String blogMaxId = sharedPreferences.getString("blogMaxId", "0");

        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_DYNAMICS);
        map.put("cid", user.getClassinfoId());
        map.put("homeworkMaxId", homeworkMaxId);
        map.put("blogMaxId", blogMaxId);
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());
        String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
        MLog.i("动态更新地址： " + httpURL);

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject obj = new JSONObject(t);
                            int homeworkNewCount = obj
                                    .optInt("homeworkNewCount");

                            if (homeworkNewCount > 0) {
                                txt_homework_count.setVisibility(View.VISIBLE);
                                if (homeworkNewCount < 100) {
                                    txt_homework_count.setText(String
                                            .valueOf(homeworkNewCount));
                                } else {
                                    txt_homework_count.setText("99");
                                }
                            } else {
                                txt_homework_count.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
    }


    class queryMessageCountThread extends Thread {
        @Override
        public void run() {
            // 可能会出现在同步消息之前去数据库查询消息，导致结果为空或者为之前的旧数据，所以先睡眠几秒再进行同步
            SystemClock.sleep(1000);

            PersonMessageDBManager personMessageDBManager = null;
            Message message = new Message();
            try {
                personMessageDBManager = new PersonMessageDBManager(InclassActivity.this);

                // 查询未读班级消息数
                int unReadClassMessageCount = personMessageDBManager
                        .queryUnreadClzMsg(user.getUserId());
                MLog.i("未读班级消息数：" + unReadClassMessageCount);

                // 查询未读群组消息数
                int unReadGroupMessageCount = personMessageDBManager.queryUnreadGroupMsg(user.getUserId());
                MLog.i("未读群组消息数：" + unReadGroupMessageCount);

                // 查询未读个人消息数
                List<PersonMessage> personMessageList = personMessageDBManager
                        .queryMsg(user.getUserId(), null);
                int unReadPersonMessageCount = 0;
                for (int i = 0; i < personMessageList.size(); i++) {
                    unReadPersonMessageCount += personMessageList.get(i)
                            .getNewMsgCount();
                }
                MLog.i("未读个人消息数：" + unReadPersonMessageCount);
                MLog.i("未读消息总数："
                        + (unReadPersonMessageCount + unReadClassMessageCount));

                message.what = FlagCode.SUCCESS;
                // 判断是否该学校是否屏蔽班级消息
                if (user.getClassMessage() == 0) {
                    MLog.i("未屏蔽班级消息，消息总数相加");
                    message.arg1 = unReadPersonMessageCount
                            + unReadClassMessageCount + unReadGroupMessageCount;
                } else {
                    MLog.i("已屏蔽班级消息，消息总数只用个人未读数");
                    message.arg1 = unReadPersonMessageCount;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handler.sendMessage(message);
                if (null != personMessageDBManager) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.i("InclassActivity.onDestroy");
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
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
                MToast.show(InclassActivity.this, "再按一次退出", MToast.SHORT);
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
