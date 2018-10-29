package com.dt5000.ischool.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.dt5000.ischool.entity.GroupMessageSend;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送群组消息线程
 */
public class SendGroupMessageThread implements Runnable {

    private Handler handler;
    private String filePath;
    private Context context;
    private User user;
    private GroupMessageSend groupMessageSend;
    private String sendSMS;

    public SendGroupMessageThread(Handler handler, String filePath,
                                  Context context, User user, GroupMessageSend groupMessageSend,
                                  String sendSMS) {
        this.handler = handler;
        this.filePath = filePath;
        this.context = context;
        this.user = user;
        this.groupMessageSend = groupMessageSend;
        this.sendSMS = sendSMS;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            if (!CheckUtil.stringIsBlank(filePath)) {
                // 将发送图片保存为msg_pic_send.jpg
                File msg_pic_send = new File(FileUtil.getCameraDir(), "msg_pic_send.jpg");
                msg_pic_send.createNewFile();

                FileOutputStream fos = new FileOutputStream(msg_pic_send);
                Bitmap decodeBitmap = ImageUtil.decodeBitmapWithThumbnailUtils(filePath, 1600);
                // 压缩图片质量至80%
                decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();

                filePath = msg_pic_send.getAbsolutePath();
                MLog.i("发送的图片路径：" + filePath);
            }

            // 将参数转成Json
            Gson gson = new GsonBuilder().create();
            String jsonMessage = gson.toJson(groupMessageSend);

            // 封装参数
            Map<String, String> map = new HashMap<String, String>();
            map.put("operationType", UrlProtocol.OPERATION_TYPE_SEND_GROUP_MSG);
            map.put("jsonMessage", jsonMessage);
            map.put("sendMsg", sendSMS);
            if (!CheckUtil.stringIsBlank(filePath)) {
                String subffix = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
                map.put("subffix", subffix);
            }
            String httpURL = UrlBulider.getHttpURL(map, context, user.getUserId());
            MLog.i("发送群消息Url：" + httpURL);
            // 发送请求
            String response = HttpClientUtil.doPostWithSingleFile(httpURL, filePath);
            MLog.i("发送群消息返回结果：" + response);

            // 解析
            JSONObject obj = new JSONObject(response);
            String memo = obj.optString("memo");
            if ("".equals(memo)) {
                message.what = FlagCode.SUCCESS;
            } else {
                message.what = FlagCode.FAIL;
                MLog.i("发送群消息解析失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = FlagCode.FAIL;
            MLog.i("发送群消息出现异常");
        }

        handler.sendMessage(message);
    }

}
