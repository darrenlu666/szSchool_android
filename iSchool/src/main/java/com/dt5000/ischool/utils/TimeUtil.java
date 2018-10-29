package com.dt5000.ischool.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化辅助类
 * 
 * @author 周锋
 * @date 2016年3月28日 上午10:16:32
 * @ClassInfo com.dt5000.ischool.utils.TimeUtil
 * @Description
 */
public class TimeUtil {

	private static final SimpleDateFormat FORMAT_FULL_TIME = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	public static final SimpleDateFormat FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINA);
	private static final SimpleDateFormat FORMAT_YEAR_MONTH_DAY = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINA);
	private static final SimpleDateFormat FORMAT_YEAR_MONTH_DAY_CHINESE = new SimpleDateFormat(
			"yy年MM月dd日", Locale.CHINA);
	private static final SimpleDateFormat FORMAT_MONTH_DAY_CHINESE = new SimpleDateFormat(
			"MM月dd日", Locale.CHINA);
	private static final SimpleDateFormat FORMAT_HOUR_MINUTE_SECOND = new SimpleDateFormat(
			"HH:mm:ss", Locale.CHINA);
	private static final SimpleDateFormat FORMAT_HOUR_MINUTE = new SimpleDateFormat(
			"HH:mm", Locale.CHINA);

	/**
	 * 将时间戳Long解析成字符串，格式为（yyyy-MM-dd）
	 * 
	 * @param time
	 * @return
	 */
	public static String yearMonthDayFormat(Long time) {
		String result = "";
		if (time != null) {
			Timestamp date = new Timestamp(time);
			result = FORMAT_YEAR_MONTH_DAY.format(date);
		}
		return result;
	}

	/**
	 * 将时间戳Long解析成字符串，格式为（yyyy-MM-dd HH:mm）
	 * 
	 * @param time
	 * @return
	 */
	public static String yearMonthDayHourMinuteFormat(Long time) {
		String result = "";
		if (time != null) {
			Timestamp date = new Timestamp(time);
			result = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE.format(date);
		}
		return result;
	}

	/**
	 * 将时间戳Long解析成字符串，格式为（yy年MM月dd日或者MM月dd日）
	 * 
	 * @param time
	 * @return
	 */
	public static String yearMonthDayChineseFormat(Long time) {
		String result = "";
		if (time != null) {
			Timestamp date = new Timestamp(time);
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			int currentYear = calendar.get(Calendar.YEAR);
			calendar.setTime(date);
			int pastYear = calendar.get(Calendar.YEAR);
			if (currentYear == pastYear) {
				result = FORMAT_MONTH_DAY_CHINESE.format(date);
			} else {
				result = FORMAT_YEAR_MONTH_DAY_CHINESE.format(date);
			}
		}

		return result;
	}

	/**
	 * 将时间戳Long解析成字符串，格式为（HH:mm）
	 * 
	 * @param time
	 * @return
	 */
	public static String hourMinuteFormat(Long time) {
		String result = "";
		if (time != null) {
			Timestamp date = new Timestamp(time);
			result = FORMAT_HOUR_MINUTE.format(date);
		}
		return result;
	}

	/**
	 * 将时间戳Long解析成字符串，格式为（HH:mm:ss）
	 * 
	 * @param time
	 * @return
	 */
	public static String hourMinuteSecondFormat(Long time) {
		String result = "";
		if (time != null) {
			Timestamp date = new Timestamp(time);
			result = FORMAT_HOUR_MINUTE_SECOND.format(date);
		}
		return result;
	}

	/**
	 * 将Date解析成字符串，格式为（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param date
	 * @return
	 */
	public static String fullTimeFormat(Date date) {
		return FORMAT_FULL_TIME.format(date);
	}

	/**
	 * 将格式为（yyyy-MM-dd HH:mm:ss）的字符串解析成Date
	 * 
	 * @param date
	 * @return
	 */
	public static Date parseFullTime(String time) {
		Date date = null;
		try {
			date = FORMAT_FULL_TIME.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			date = new Date();
		}

		return date;
	}

	/**
	 * 解析Date
	 * 
	 * @param format
	 * @param time
	 * @return
	 */
	public static Date parseTime(DateFormat format, String time) {
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			date = new Date();
		}

		return date;
	}

	/**
	 * 将Date解析成字符串，格式为聊天界面的时间格式
	 * 
	 * @param date
	 * @return
	 */
	public static String messageFormat(Date date) {
		String result = "";
		Calendar cal = Calendar.getInstance(Locale.CHINESE);

		// 获取当前相关时间数据
		cal.setTime(new Date());
		int curYear = cal.get(Calendar.YEAR);// 年
		int curMonth = cal.get(Calendar.MONTH);// 月
		int curDay = cal.get(Calendar.DAY_OF_MONTH);// 日

		// 获取消息的时间数据
		cal.setTime(date);
		int msgYear = cal.get(Calendar.YEAR);// 年
		int msgMonth = cal.get(Calendar.MONTH);// 月
		int msgDay = cal.get(Calendar.DAY_OF_MONTH);// 日
		int msgHour24 = cal.get(Calendar.HOUR_OF_DAY);// 时x24
		int msgMin = cal.get(Calendar.MINUTE);// 分
		int msgAP = cal.get(Calendar.AM_PM);// 上下午标识

		if (msgYear < curYear) {// 今年之前的
			SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm",
					Locale.CHINESE);
			result = df.format(date);
		} else {// 今年的
			if (msgMonth < curMonth) {// 当月之前的
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm",
						Locale.CHINESE);
				result = df.format(date);
			} else {// 本月的
				if (msgDay < curDay) {// 当天之前的
					int gap = curDay - msgDay;
					if (gap == 1) {// 昨天的
						SimpleDateFormat df = new SimpleDateFormat("HH:mm",
								Locale.CHINESE);
						result = "昨天 " + df.format(date);
					} else if (gap == 2) {// 前天的
						SimpleDateFormat df = new SimpleDateFormat("HH:mm",
								Locale.CHINESE);
						result = "前天 " + df.format(date);
					} else {// 两天之前的
						SimpleDateFormat df = new SimpleDateFormat(
								"MM-dd HH:mm", Locale.CHINESE);
						result = df.format(date);
					}
				} else {// 当天的
					if (msgAP == 0) {// 上午
						String strHour = msgHour24 + "";
						String strMin = msgMin + "";
						if (msgHour24 < 10) {
							strHour = "0" + strHour;
						}
						if (msgMin < 10) {
							strMin = "0" + strMin;
						}
						result = strHour + ":" + strMin;// 24小时制
					} else {// 下午
						String strHour = msgHour24 + "";
						String strMin = msgMin + "";
						if (msgHour24 < 10) {
							if (msgHour24 == 0) {
								strHour = "12";
							} else {
								strHour = "0" + strHour;
							}
						}
						if (msgMin < 10) {
							strMin = "0" + strMin;
						}
						result = strHour + ":" + strMin;// 24小时制
					}
				}
			}
		}
		// 时间归位
		cal.setTime(new Date());
		return result;
	}

}
