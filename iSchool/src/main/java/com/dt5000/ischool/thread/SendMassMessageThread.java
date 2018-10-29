package com.dt5000.ischool.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 群发消息线程
 *
 * @author 周锋
 * @date 2016年1月29日 上午10:47:17
 * @ClassInfo com.dt5000.ischool.thread.SendMassMessageThread
 * @Description
 */
public class SendMassMessageThread implements Runnable {

    private Handler handler;
    private Context context;
    private User user;
    private Map<String, String> paramMap;

    public SendMassMessageThread() {
    }

    public SendMassMessageThread(Handler handler, Context context, User user,
                                 Map<String, String> paramMap) {
        this.handler = handler;
        this.context = context;
        this.user = user;
        this.paramMap = paramMap;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            List<NameValuePair> httpParams = UrlBulider.getHttpParams(paramMap, context, user.getUserId());
            Log.i("SendMassMessageThread", "群发消息链接---" + UrlBulider.getHttpURL(paramMap, context, user.getUserId()));

            String response = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
            MLog.i("群发消息返回结果：" + response);

            JSONObject obj = new JSONObject(response);
            String result = obj.optString("result");
            if ("success".equals(result)) {
                message.what = FlagCode.SUCCESS;
            } else {
                message.what = FlagCode.FAIL;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = FlagCode.FAIL;
        }
        handler.sendMessage(message);
    }

}
