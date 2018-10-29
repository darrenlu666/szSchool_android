package com.dt5000.ischool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.dt5000.ischool.service.NoticeService;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MLog;

import cn.jpush.android.api.JPushInterface;

/**
 * 接收极光推送的广播
 * 
 * @author 周锋
 * @date 2015年9月7日 下午2:07:31
 * @ClassInfo com.dt5000.ischool.receiver.MyJPushReceiver
 * @Description
 */
public class MyJPushReceiver extends BroadcastReceiver {

	private SharedPreferences sharedPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

			Bundle bundle = intent.getExtras();
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

				String JPushRegistrationId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				MLog.i("MyJPushReceiver接收到绑定的JPushRegistrationId : " + JPushRegistrationId);

				if (!CheckUtil.stringIsBlank(JPushRegistrationId)) {// 将JPushRegistrationId写入配置文件
					Editor editor = sharedPreferences.edit();
					editor.putString("JPushRegistrationId", JPushRegistrationId);
					editor.commit();
				}

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
					.getAction())) {

				String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
				MLog.i("MyJPushReceiver接收到自定义消息：" + message);

				// 测试推送：手机上直接显示推送内容
				// MainToast.show(context, "极光推送\n\n" + message,
				// MainToast.LONG);

				if (!CheckUtil.stringIsBlank(message)) {
					MLog.i("开启NoticeService...");
					Intent noticeIntent = new Intent(context, NoticeService.class);
					noticeIntent.putExtra("message", message);
					context.startService(noticeIntent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
