package com.dt5000.ischool.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckUtil {

	/**
	 * 判断手机号码格式是否正确
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCellphone(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断邮箱格式是否正确
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断当前是否有可用网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		// 获得手机所有连接管理对象（包括对wi-fi等连接的管理）
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager != null) {
			// 获得网络连接管理的对象
			NetworkInfo info = connManager.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// 判断当前网络是否已连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					info.getTypeName();
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * <strong>判断一个字符序列是否为空</strong>
	 * <ul>
	 * <li>如果字符序列对象为空则返回true</li>
	 * <li>如果字符序列所有字符均为空格则返回true</li>
	 * </ul>
	 * 
	 * @param cs
	 *            需要判断的字符序列对象
	 * @return 结果为空返回true，否则为false
	 */
	public static boolean stringIsBlank(CharSequence cs) {
		if (cs == null) {
			return true;
		}

		int strlen = cs.length();
		if (strlen == 0) {
			return true;
		}

		for (int i = 0; i < strlen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 解码UniCode
	 * 
	 * @param str
	 * @return
	 */
	public static String unescapeUnicode(String str) {
		StringBuffer sb = new StringBuffer();
		Matcher matcher = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(str);
		while (matcher.find()) {
			matcher.appendReplacement(sb,
					(char) Integer.parseInt(matcher.group(1), 16) + "");
		}
		matcher.appendTail(sb);
		return sb.toString().replace("\\", "");
	}

	/**
	 * 检查是否包含emoji表情
	 * 
	 * @param string
	 * @return
	 */
	public static boolean containsEmoji(String content) {
		Pattern p = Pattern
				.compile(
						"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
						Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);

		return m.find();
	}

}
