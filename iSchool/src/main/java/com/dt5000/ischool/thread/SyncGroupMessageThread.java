package com.dt5000.ischool.thread;

import android.content.Context;
import android.os.Handler;

import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.FlagCode;

/**
 * 同步群组消息线程
 *
 */
public class SyncGroupMessageThread extends Thread {

	private Handler handle;
	private Context context;
	private User user;

	public SyncGroupMessageThread(Handler handle, Context context, User user) {
		this.context = context;
		this.handle = handle;
		this.user = user;
	}

	@Override
	public void run() {
		SyncGroup.SyncGroupMsg(context, user);
		handle.sendEmptyMessage(FlagCode.SPECIAL);
	}

}
