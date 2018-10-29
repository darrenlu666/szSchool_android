package com.dt5000.ischool.utils;

import android.app.AlertDialog;
import android.content.Context;

/**
 * 对话框辅助类
 * 
 * @author 周锋
 * @date 2016年1月13日 下午2:31:41
 * @ClassInfo com.dt5000.ischool.utils.DialogAlert
 * @Description
 */
public class DialogAlert {

	/**
	 * 显示简易对话框，只包含信息和确定按钮
	 * 
	 * @param context
	 * @param msg
	 *            提示信息
	 */
	public static void show(Context context, String msg) {
		new AlertDialog.Builder(context).setMessage(msg)
				.setPositiveButton("确定", null).show();
	}

}
