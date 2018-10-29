package com.dt5000.ischool.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author 周锋
 * @date 2016年2月4日 上午9:48:49
 * @ClassInfo com.dt5000.ischool.utils.MToast
 * @Description
 */
public class MToast {

	private static Toast toast;
	/** Show the view or text notification for a short period of time */
	public static final int SHORT = 0;
	/** Show the view or text notification for a long period of time */
	public static final int LONG = 1;

	/**
	 * 显示Toast,使用唯一静态Toast对象
	 * 
	 * @param ctx
	 * @param msg
	 *            要显示的内容
	 * @param type
	 *            显示时间类型(SHORT-短、LONG-长)
	 */
	public static void show(Context ctx, String msg, int type) {
		if (toast == null) {// 不存在新建
			// 设置内容和时间
			if (type == SHORT) {// 短
				toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
			} else {// 长
				toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
			}
		} else {// 已存在
			// 设置内容
			toast.setText(msg);
			// 设置时间
			if (type == SHORT) {// 短
				toast.setDuration(Toast.LENGTH_SHORT);
			} else {// 长
				toast.setDuration(Toast.LENGTH_LONG);
			}
		}
		// 显示Toast
		toast.show();
	}

}
