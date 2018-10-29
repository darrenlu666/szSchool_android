package com.dt5000.ischool.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.MainTabActivity;
import com.dt5000.ischool.activity.WelcomeActivity;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.thread.SyncClass;
import com.dt5000.ischool.thread.SyncGroup;
import com.dt5000.ischool.thread.SyncPerson;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MessagePushType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author 周锋
 * @date 2015年9月7日 下午1:59:49
 * @ClassInfo com.dt5000.ischool.service.NoticeService
 * @Description
 */
public class NoticeService extends IntentService {

    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;
    private User user;
    private Gson gson;

    public NoticeService() {
        super("com.dt5000.ischool.service.NoticeService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        user = User.getUser(getApplicationContext());
        gson = new Gson();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (user == null) {
                MLog.i("接收到通知，但用户未登录，不作处理");
                return;
            }
            MLog.i("用户已登录，继续执行...");

            // 如果是学生端需添加VIP判断，不是VIP则不显示推送通知
            // if (!isStudentAndVIP()) {
            // return;
            // }

            // 解析通知
            String message = intent.getStringExtra("message");
            Map<String, String> retMap = gson.fromJson(message,
                    new TypeToken<Map<String, String>>() {
                    }.getType());

            // 判断通知类型
            String msgType = retMap.get("msgType").toString();
            if (String.valueOf(MessagePushType.Personal).equals(msgType)) {
                MLog.i("接收到的是个人消息...");
                String personalMsgStr = retMap.get("personal");
                if (!CheckUtil.stringIsBlank(personalMsgStr)) {
                    // 个人消息通知逻辑
                    messageNotify(msgType, personalMsgStr);
                }
            } else if (String.valueOf(MessagePushType.Clazz).equals(msgType)) {
                MLog.i("接收到的是班级消息...");
                String clazzMsgStr = retMap.get("clazz");
                if (!CheckUtil.stringIsBlank(clazzMsgStr)) {
                    // 班级消息通知逻辑
                    messageNotify(msgType, clazzMsgStr);
                }
            } else if (String.valueOf(MessagePushType.Group).equals(msgType)) {
                MLog.i("接收到的是群组消息...");
                String groupMsgStr = retMap.get("userGroup");
                if (!CheckUtil.stringIsBlank(groupMsgStr)) {
                    // 群组消息通知逻辑
                    messageNotify(msgType, groupMsgStr);
                }
            } else if (String.valueOf(MessagePushType.Score).equals(msgType)) {
                MLog.i("接收到的是成绩...");
                String scoreStr = retMap.get("score");
                if (!CheckUtil.stringIsBlank(scoreStr)) {
                    // 成绩通知逻辑
                    scoreNotify(scoreStr);
                }
            } else if (String.valueOf(MessagePushType.Homework).equals(msgType)) {
                MLog.i("接收到的是作业...");
                String homeworkStr = retMap.get("homework");
                if (!CheckUtil.stringIsBlank(homeworkStr)) {
                    // 作业通知逻辑
                    homeworkNotify(homeworkStr);
                }
            } else if (String.valueOf(MessagePushType.Vacate).equals(msgType)) {
                MLog.i("接收到的是请假...");
                String vacateStr = retMap.get("homework");
                if (!CheckUtil.stringIsBlank(vacateStr)) {
                    // 请假通知逻辑
                    vacateNotify(vacateStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作业通知逻辑
     *
     * @param homeworkStr
     */
    private void homeworkNotify(String homeworkStr) {
        MLog.i("作业内容-->" + homeworkStr);

        Map<String, String> personalMsgMap = gson.fromJson(homeworkStr,
                new TypeToken<Map<String, String>>() {
                }.getType());
        String sendName = personalMsgMap.get("sendName");
        String content = personalMsgMap.get("content");

        // 只有学生端会收到作业推送
        if (User.isStudentRole(user.getRole())) {
            // 判断程序是否已经启动
            boolean MainTabExist = sharedPreferences.getBoolean("MainTabExist",
                    false);
            Intent intent = new Intent();
            if (MainTabExist) {
                MLog.i(" 主页面已经创建好...");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(this, MainTabActivity.class);
            } else {
                MLog.i("主页面还未创建好，已经关闭了程序");
                intent.setClass(this, WelcomeActivity.class);
            }

            // 设置通知栏
            PendingIntent intentP = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE);
            builder.setTicker(getResources().getString(R.string.app_name));
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentTitle(sendName);
            builder.setContentText(content);
            builder.setContentIntent(intentP);
            builder.setWhen(System.currentTimeMillis());
            Notification noti = builder.build();

            // 弹出消息通知栏
            notificationManager.notify(0, noti);
        }
    }

    /**
     * 请假考勤通知逻辑
     *
     * @param vacateStr
     */
    private void vacateNotify(String vacateStr) {
        MLog.i("请假考勤内容-->" + vacateStr);

        // 将存在新考勤请假的标识存入本地
        sharedPreferences.edit().putBoolean("newVacate", true).commit();

        Map<String, String> personalMsgMap = gson.fromJson(vacateStr,
                new TypeToken<Map<String, String>>() {
                }.getType());
        String sendName = personalMsgMap.get("sendName");
        String content = personalMsgMap.get("content");

        // 判断程序是否已经启动
        boolean MainTabExist = sharedPreferences.getBoolean("MainTabExist",
                false);
        Intent intent = new Intent();
        if (MainTabExist) {
            MLog.i(" 主页面已经创建好...");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(this, MainTabActivity.class);
        } else {
            MLog.i("主页面还未创建好，已经关闭了程序");
            intent.setClass(this, WelcomeActivity.class);
        }

        // 设置通知栏
        PendingIntent intentP = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        builder.setTicker(getResources().getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(sendName);
        builder.setContentText(content);
        builder.setContentIntent(intentP);
        builder.setWhen(System.currentTimeMillis());
        Notification noti = builder.build();

        // 判断弹出通知还是发送广播
        String currentActivity = sharedPreferences.getString("currentActivity",
                "");
        if (currentActivity.equals("Mine")) {
            MLog.i("当前正处于我页面，发送广播通知页面刷新...");
            sendBroadcast(new Intent("com.dt5000.ischool.action.vacate"));
        } else {
            MLog.i("当前不在我界面，弹出消息通知栏...");
            notificationManager.notify(1, noti);
        }
    }

    /**
     * 成绩通知逻辑
     *
     * @param scoreStr
     */
    private void scoreNotify(String scoreStr) {
        MLog.i("成绩内容-->" + scoreStr);

        Map<String, String> personalMsgMap = gson.fromJson(scoreStr,
                new TypeToken<Map<String, String>>() {
                }.getType());
        String sendName = personalMsgMap.get("sendName");
        String content = personalMsgMap.get("content");

        // 只有学生端会收到成绩推送
        if (User.isStudentRole(user.getRole())) {
            // 判断程序是否已经启动
            boolean MainTabExist = sharedPreferences.getBoolean("MainTabExist", false);
            Intent intent = new Intent();
            if (MainTabExist) {
                MLog.i(" 主页面已经创建好...");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(this, MainTabActivity.class);
            } else {
                MLog.i("主页面还未创建好，已经关闭了程序");
                intent.setClass(this, WelcomeActivity.class);
            }

            // 设置通知栏
            PendingIntent intentP = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE);
            builder.setTicker(getResources().getString(R.string.app_name));
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentTitle(sendName);
            builder.setContentText(content);
            builder.setContentIntent(intentP);
            builder.setWhen(System.currentTimeMillis());
            Notification noti = builder.build();

            // 弹出消息通知栏
            notificationManager.notify(0, noti);
        }
    }

    /**
     * 个人消息或班级消息通知逻辑
     *
     * @param msgType
     * @param messageStr
     */
    private void messageNotify(String msgType, String messageStr) {
        MLog.i("消息内容-->" + messageStr);

        // 先把消息同步到本地，然后再进行通知
        if (msgType.equals(String.valueOf(MessagePushType.Personal))) {// 同步个人消息
            SyncPerson.SyncPersonMsg(this, user);
        }
        //TODO
        else if (msgType.equals(String.valueOf(MessagePushType.Group))) {// 同步群组消息
            SyncGroup.SyncGroupMsg(this, user);
        } else {// 同步班级消息
            SyncClass.SyncClassMsg(this, user);
        }

        // 解析消息
        Map<String, String> personalMsgMap = gson.fromJson(messageStr,
                new TypeToken<Map<String, String>>() {
                }.getType());
        String sendId = personalMsgMap.get("sendId");
        String sendName = personalMsgMap.get("sendName");
        String content = personalMsgMap.get("content");
        if (CheckUtil.stringIsBlank(content)) {
            content = "[图片]";
        }

        // 自己发送的消息
        if (sendId.equals(user.getUserId())) {
            MLog.i("自己发送的消息，自己接收到推送，不做处理");
            return;
        }

        // 免打扰判断（在免打扰状态下通知静音）
        boolean notification_silence = false;// 标识是否静音（无声音、无震动）
        boolean no_disturb = sharedPreferences.getBoolean("no_disturb", false);
        if (no_disturb) {// 开启了免打扰
            // 获取免打扰时间段
            int no_disturb_time1 = sharedPreferences.getInt("no_disturb_time1", 23);
            int no_disturb_time2 = sharedPreferences.getInt("no_disturb_time2", 8);

            // 判断是否需要静音
            if (inTimePeriod(no_disturb_time1, no_disturb_time2)) {
                notification_silence = true;
            }
        }

        // 传递参数设置，此处传递的参数留作备用，如果点击通知栏直接跳到对话页面的时候会用到这些参数
        Intent intent = new Intent();
        intent.putExtra("msgType", msgType);
        intent.putExtra("sendId", sendId);
        intent.putExtra("sendName", sendName);

        // 判断程序是否已经启动
        boolean MainTabExist = sharedPreferences.getBoolean("MainTabExist", false);
        if (MainTabExist) {
            MLog.i(" 主页面已经创建好...");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(this, MainTabActivity.class);
        } else {
            MLog.i("主页面还未创建好，已经关闭了程序");
            intent.setClass(this, WelcomeActivity.class);
        }

        // 通知栏参数设置
        PendingIntent intentP = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        builder.setAutoCancel(true);
        builder.setTicker(getResources().getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(sendName);
        builder.setContentText(content);
        builder.setContentIntent(intentP);
        builder.setWhen(System.currentTimeMillis());
        boolean isMsg = sharedPreferences.getBoolean("isMsg", true);
        boolean isVoice = sharedPreferences.getBoolean("isVoice", true);
        boolean isVibrator = sharedPreferences.getBoolean("isVibrator", true);
        if (!notification_silence) {// 非静音状态下设置通知的声音和震动
            if (isVoice && isVibrator) {
                builder.setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_VIBRATE);
            }
            if (isVoice && !isVibrator) {
                builder.setDefaults(Notification.DEFAULT_SOUND);
            }
            if (!isVoice && isVibrator) {
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        }
        Notification noti = builder.build();

        // 判断弹出通知还是发送广播
        String currentActivity = sharedPreferences.getString("currentActivity", "");
        if (currentActivity.equals("Home") || currentActivity.equals("Inclass")
                || currentActivity.equals("PersonMsgList")
                || currentActivity.equals("PersonMsgTalk")
                || currentActivity.equals("GroupMsgList")
                || currentActivity.equals("GroupMsgTalk")
                || currentActivity.equals("ClassMsgTalk")
                || currentActivity.equals("ClassMsgList")) {
            MLog.i("当前正处于首页或者课内或者个人消息对话或者班级消息对话或者个人消息列表或者班级消息列表页面，发送广播通知页面刷新...");
            sendBroadcast(new Intent("com.dt5000.ischool.action.message"));
        } else {
            MLog.i("当前不在首页或者课内或者个人消息对话或者班级消息对话或者个人消息列表或者班级消息列表界面，弹出消息通知栏...");
            if (isMsg) {
                notificationManager.notify(0, noti);
            }
        }
    }

    /**
     * 判断当前时间是否在某时间段内，例如08:00-14:00为(>=08:00&&<=13:59)
     *
     * @param hour1 开始时间（0-24h）
     * @param hour2 结束时间（0-24h）
     * @return
     */
    private boolean inTimePeriod(int hour1, int hour2) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);// 当前小时

        if (hour1 < hour2) {// 在同一天中
            if (currentHour >= hour1 && currentHour < hour2) {
                return true;
            }
        } else if (hour1 > hour2) {// 不在同一天，跨24点
            if (currentHour >= hour1 || currentHour < hour2) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断学生端和VIP状态
     *
     * @return
     */
    @SuppressWarnings("unused")
    private boolean isStudentAndVIP() {
        try {
            MLog.i("接收到推送进行学生端和VIP状态判断...");

            if (user.isTeacher()) {// 教师端不需要判断VIP
                MLog.i("当前为教师端，无推送限制，继续执行...");
                return true;
            }

            MLog.i("当前为学生端，继续判断VIP状态...");
            // 如果是学生端则继续判断VIP状态，判断是否开启VIP，以及VIP状态信息
            SharedPreferences sp = getSharedPreferences("vip_info",
                    MODE_PRIVATE);
            String startVip = sp.getString("startVip", "0");
            String startDate = sp.getString("startDate", "2014-10-01 00:00:00");
            Date startVipDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.CHINESE).parse(startDate);
            if ("1".equals(startVip)) {// 已经开启VIP收费，需再次判断开启VIP收费的时间
                MLog.i("该学校已经开启VIP收费");
                Date nowDate = new Date();
                if (nowDate.getTime() > startVipDate.getTime()) {// 当前时间已经超过开始收费的时间，则再判断VIP状态
                    MLog.i("开启VIP收费时间已到");
                    String vipStatus = sp.getString("vipStatus", "1");
                    if ("1".equals(vipStatus)) {// 是VIP
                        MLog.i("当前用户已经是VIP，可以弹出推送通知...");
                        return true;
                    } else {// 不是VIP
                        MLog.i("当前用户不是VIP，不弹出推送通知...");
                        return false;
                    }
                } else {// 开启VIP收费时间还未到
                    MLog.i("开启VIP收费时间还未到，无限制，可以弹出推送通知...");
                    return true;
                }
            } else {// 还未开启VIP收费，默认为VIP无限制
                MLog.i("当前未开启VIP收费，无限制，可以弹出推送通知...");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
