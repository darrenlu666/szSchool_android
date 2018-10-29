package com.dt5000.ischool.thread;

import android.content.Context;

import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
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
 * 同步个人消息方法，在线程中执行
 *
 * @author 周锋
 * @date 2016年1月19日 上午11:51:43
 * @ClassInfo com.dt5000.ischool.thread.SyncPerson
 * @Description
 */
public class SyncPerson {

    /**
     * 查询出本地数据库中个人消息id的最大值，然后向服务器获取大于该id的所有消息(即最新消息)，再插入本地数据库
     *
     * @param context
     * @param user
     */
    public synchronized static void SyncPersonMsg(Context context, User user) {
        try {
            // 获取本地数据库中个人消息的最大id
            PersonMessageDBManager dbManager = new PersonMessageDBManager(context);
            int maxId = dbManager.queryMaxPersonMsgId(user.getUserId());
            MLog.i("本地个人消息maxId: " + String.valueOf(maxId));

            // 拼接参数
            Map<String, String> map = new HashMap<String, String>();
            map.put("operationType", UrlProtocol.OPERATION_TYPE_SYNC_PERSION_MSG);
            map.put("maxId", String.valueOf(maxId));
            map.put("role", String.valueOf(user.getRole()));
            List<NameValuePair> httpParams = UrlBulider.getHttpParams(map, context, user.getUserId());
            String httpURL = UrlBulider.getHttpURL(map, context, user.getUserId());
            MLog.i("同步个人消息地址： " + httpURL);

            // 发送请求
            String response = HttpClientUtil.doPost(UrlProtocol.MAIN_HOST, httpParams);
            //TODO
            MLog.i("同步返回的json数据为： " + response.toString());
            // 解析
            JSONObject obj = new JSONObject(response);
            String result = obj.optString("pList");
            GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = builder.create();
            List<PersonMessage> data = gson.fromJson(result,
                    new TypeToken<List<PersonMessage>>() {
                    }.getType());


            // 将数据插入数据库
            if (data != null && data.size() > 0) {
                dbManager.addPersonMsgList(data);
                //更新发送者的信息
                dbManager.updatePerson(data, context);
            }

            if (User.isTeacherRole(user.getRole())) {
                String groupSendResult = obj.optString("massMessageList");
                List<GroupSendMessage> groupSends = gson.fromJson(groupSendResult, new TypeToken<List<GroupSendMessage>>() {
                }.getType());
                if (groupSends != null && groupSends.size() > 0) {
                    DaoHelper.addGroupSend(groupSends);
                }
            }


            // 关闭数据库
            if (null != dbManager) {
                dbManager.closeDB();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
