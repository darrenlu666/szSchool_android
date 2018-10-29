package com.dt5000.ischool.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.activity.student.AddRelevancyUserAcitivity;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.UserInfo;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by weimy on 2017/11/29.
 */

public class HelpUtils {

    //setting
    public static User saveSetting(Activity activity, String password) {
        // 将登录信息写入配置文件
        SharedPreferences sharedPreferences = activity.getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        final User user = User.getUser(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUserName());// 登录用户名
        editor.putString("password", password);// 登录密码
        editor.putString("role", User.isTeacherRole(user.getRole()) ? "1" : "0");// 登录角色
        editor.commit();

        return user;
    }

    //保存登陆信息
    public static void save(Activity activity, String password, User userAdd) {//userId为当前未切换的帐号
        User user = null;
        if (!(activity instanceof AddRelevancyUserAcitivity)) {
            user = saveSetting(activity, password);
        } else {
            user = userAdd;
        }

        UserInfo userInfo = new UserInfo(user.getUserId(), String.valueOf(user.getRole()),
                user.getUserName(), password, user.getProfileUrl(), user.getRealName(), 0, "");

        if (!User.isTeacherRole(Integer.valueOf(userInfo.getRole()))) {
            DaoHelper.insertAndUpdateUserInfo(userInfo);
        }
    }


    //登陆
    public static List<NameValuePair> loginMessage(Activity activity, String loginRole, String username, String password) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_LOGIN);
        map.put("role", loginRole);
        map.put("userName", username);
        map.put("pwd", password);
        List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, activity, "");

        String httpURL = UrlBulider.getHttpParamss(map, activity, "");
        MLog.i("登录地址：" + httpURL);

        return httpParams;
    }

    //修改手机号
    public static String changePhone(Activity activity, String userId, String role, String newPhone) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_UserEditPhone);
        map.put("userId", userId);
        map.put("newPhone", newPhone);
        map.put("role", role);

        String httpURL = UrlBulider.getHttpParamss(map, activity, userId);
        return httpURL;
    }

    //通知已读
    public static String notityReader(Activity activity, String sendUserId, String receiveUserId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_NotityReader);
        map.put("sendUserId", sendUserId);
        map.put("receiveUserId", receiveUserId);

        String httpURL = UrlBulider.getHttpParamss(map, activity, receiveUserId);
        return httpURL;
    }

    //订单详情
    public static String payOrder(Activity activity, String userId, int pageSize, int pageNum) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_Orders);
        map.put("userId", userId);
        map.put("pageSize", String.valueOf(pageSize));
        map.put("pageNum", String.valueOf(pageNum));

        String httpURL = UrlBulider.getHttpURL(map, activity, userId);
        return httpURL;
    }

    //退出
    public static List<NameValuePair> logoutMessage(User user, Activity activity) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_LOGIN_OFF);
        final List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, activity, user.getUserId());
        return httpParams;
    }

    public static void cleanMessage(boolean isClean, Activity activity) {
        // 清空通用配置文件，记住用户先前的一些常用设置
        SharedPreferences preferences = activity.getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        boolean isMsg = preferences.getBoolean("isMsg", true);
        boolean isVoice = preferences.getBoolean("isVoice", true);
        boolean isVibrator = preferences.getBoolean("isVibrator", true);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putBoolean("isMsg", isMsg);
        editor.putBoolean("isVoice", isVoice);
        editor.putBoolean("isVibrator", isVibrator);
        editor.commit();

        // 清空VIP配置文件
        SharedPreferences vip_preferences = activity.getSharedPreferences(
                MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
        vip_preferences.edit().clear().commit();

        // 清除通知栏
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        // 清空Activity栈
        if (isClean) {
            //清空数据库
            DaoHelper.deleteAll();
            for (Activity ac : MainApplication.activityList) {
                if (ac != null) {
                    ac.finish();
                }
            }
        }
    }


}
