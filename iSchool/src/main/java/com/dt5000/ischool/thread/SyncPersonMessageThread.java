package com.dt5000.ischool.thread;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.FlagCode;

/**
 * 同步个人消息线程
 * 
 * @author 周锋
 * @date 2016年1月19日 下午1:45:00
 * @ClassInfo com.dt5000.ischool.thread.SyncPersonMessageThread
 * @Description
 */
public class SyncPersonMessageThread extends Thread {

	private Handler handler;
	private Context context;
	private User user;

	public SyncPersonMessageThread(Handler handler, Context context, User user) {
		this.handler = handler;
		this.context = context;
		this.user = user;
	}

	@Override
	public void run() {
		//睡眠1秒再去服务器拿数据，如果直接拿数据可能出现服务器还未存表完成的情况
		SystemClock.sleep(1000);
		
		SyncPerson.SyncPersonMsg(context, user);
		handler.sendEmptyMessage(FlagCode.SPECIAL);
	}

}
