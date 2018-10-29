package com.dt5000.ischool.thread;

import android.content.Context;
import android.os.Handler;

import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.FlagCode;

/**
 * 同步班级消息线程
 * 
 * @author 周锋
 * @date 2016年1月19日 上午11:51:04
 * @ClassInfo com.dt5000.ischool.thread.SyncClassMessageThread
 * @Description
 */
public class SyncClassMessageThread extends Thread {

	private Handler handle;
	private Context context;
	private User user;

	public SyncClassMessageThread(Handler handle, Context context, User user) {
		this.context = context;
		this.handle = handle;
		this.user = user;
	}

	@Override
	public void run() {
		SyncClass.SyncClassMsg(context, user);
		handle.sendEmptyMessage(FlagCode.SPECIAL);
	}

}
