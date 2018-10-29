package com.dt5000.ischool.thread;

import android.content.Context;

import com.dt5000.ischool.db.ClassMessageDBManager;
import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步班级消息方法，在线程中执行
 *
 * @author 周锋
 * @date 2016年1月19日 上午11:52:28
 * @ClassInfo com.dt5000.ischool.thread.SyncClass
 * @Description
 */
public class SyncClass {

    /**
     * 查询出本地数据库中班级消息id的最大值，然后向服务器获取大于该id的所有消息(即最新消息)，再插入本地数据库
     *
     * @param context
     * @param user
     */
    public synchronized static void SyncClassMsg(Context context, User user) {
        try {
            // 获取本地数据库中班级消息的最大id
            PersonMessageDBManager personMessageDBManager = new PersonMessageDBManager(context);
            int maxId = personMessageDBManager.queryMaxClassMsgId(user.getUserId());
            MLog.i("本地班级消息maxId: " + String.valueOf(maxId));
            if (personMessageDBManager != null) {
                personMessageDBManager.closeDB();
            }

            // 拼接参数
            Map<String, String> map = new HashMap<String, String>();
            map.put("operationType", UrlProtocol.OPERATION_TYPE_SYNC_CLASS_MSG);
            map.put("maxId", String.valueOf(maxId));
            map.put("role", String.valueOf(user.getRole()));
            List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, context, user.getUserId());

            String httpURL = UrlBulider.getHttpURL(map, context, user.getUserId());
            MLog.i("同步班级消息地址：" + httpURL);

            // 发送请求
            String response = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);

            // 解析
            JSONObject obj = new JSONObject(response);
            String result = obj.optString("cList");
            GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = builder.create();
            List<ClassMessage> data = gson.fromJson(result,
                    new TypeToken<List<ClassMessage>>() {
                    }.getType());

            // 将数据插入数据库
            ClassMessageDBManager classMessageDBManager = new ClassMessageDBManager(context);
            if (null != data && data.size() > 0) {
                classMessageDBManager.addClassMsgList(data);
                classMessageDBManager.updatePerson(data, context);
            }

            // 关闭数据库
            if (null != classMessageDBManager) {
                classMessageDBManager.closeDB();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
