package com.dt5000.ischool.thread;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.dt5000.ischool.activity.student.AddRelevancyUserAcitivity;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * 登录线程
 *
 * @author 周锋
 * @date 2016年1月18日 下午1:55:22
 * @ClassInfo com.dt5000.ischool.thread.LoginThread
 * @Description
 */
public class LoginThread implements Runnable {

    private Handler handler;
    private Context context;
    private List<NameValuePair> httpParams;

    public LoginThread(Context ctx, List<NameValuePair> httpParams,
                       Handler handler) {
        this.context = ctx;
        this.httpParams = httpParams;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            String result = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
            MLog.i("登录返回结果：" + result);

            if (!CheckUtil.stringIsBlank(result)) {
                JSONObject object = new JSONObject(result);
                String userItem = object.optString("userItem");
                User user = (User) GsonUtil.jsonToBean(userItem, User.class);

                if (user != null) {
                    if (!(context instanceof AddRelevancyUserAcitivity)) {
                        // 将用户信息写入配置文件
                        SharedPreferences sharedPreferences_user = context
                                .getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
                        sharedPreferences_user.edit()
                                .putString("userJson", userItem)
                                .putString("headUrl", user.getProfileUrl())
                                .putString("userId", user.getUserId())
                                .putString("realName", user.getRealName())
                                .commit();

                        // 将开启VIP收费的时间、该学校是否已经开启VIP、用户是否是VIP等信息写入配置文件
                        // 此处注意，startDate和startVip在最外层的JSON串中，而vipStatus在内部userItem的JSON串中
                        JSONObject userItemObj = new JSONObject(userItem);
                        SharedPreferences sharedPreferences_vip = context.
                                getSharedPreferences(MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
                        Editor edit = sharedPreferences_vip.edit();
                        edit.putString("startDate", object.optString("startDate"));
                        edit.putString("startVip", object.optString("startVip"));
                        edit.putString("vipStatus", userItemObj.optString("vipStatus"));
                        edit.commit();

                        // 从服务器获取最新的个人和班级消息
                        SyncPerson.SyncPersonMsg(context, user);// 个人消息
                        SyncClass.SyncClassMsg(context, user);// 班级消息
                        SyncGroup.SyncGroupMsg(context, user);//群组消息
                        message.what = FlagCode.SUCCESS;
                    } else {
                        message.what = FlagCode.SUCCESS;
                        message.obj = user;
                    }
                } else {
                    message.what = FlagCode.FAIL;
                }
            } else {
                message.what = FlagCode.EXCEPTION;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = FlagCode.EXCEPTION;
        }

        handler.sendMessage(message);
    }

}
