package com.dt5000.ischool.activity.media.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {
    public final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String DEF_FORMAT = "yyyy-MM-dd HH:mm";
    public final static String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public final static String MONTH_DAY = "MM-dd";
    public final static String HH_MM = "HH:mm";

    public static String getNow(String format) {
        if (null == format || "".equals(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(new Date());
        return date;
    }

    public static boolean isToday(long time) {
        if (getNow(YEAR_MONTH_DAY).equals(getDateStr(time, YEAR_MONTH_DAY))) {
            return true;
        }
        return false;
    }

    /**
     * @param _date
     * @param format
     * @return
     */
    public static long stringToLong(String _date, String format) {
        Date date = stringToDate(_date, format);
        return date.getTime();
    }

    public static Date stringToDate(String _date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String format) {
        if (null == format || "".equals(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(date);
    }

    public static Date intToDate(long lDate) {
        Date date = new Date(lDate);
        return date;
    }

    public static String getDateStr(long times, String format) {
        if (times == 0)
            return "";
        Date date = intToDate(times);
        return dateToString(date, format);
    }

    public static Date getDate(int year, int month, int weekInMonth,
                               int dayInWeek) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month - 1);
        date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
        date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
        return date.getTime();
    }

    public static Date getDate(int month, int weekInMonth, int dayInWeek) {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        date.clear();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month - 1);
        date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
        date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
        return date.getTime();
    }

    public static String getDate(int month, int weekInMonth, int dayInWeek,
                                 String format) {
        Date date = getDate(month, weekInMonth, dayInWeek);
        return getDateStr(date.getTime(), format);
    }

    public final static long ONE_MINUTE = 1000 * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;



    public static String getDuration(long persisTime) {
        if (persisTime <= 0)
            return "";
        long minutes = persisTime/(60*1000);
        long day = minutes / (24 * 60);
        long hour = minutes % (24 * 60) / 60;
        long minute = minutes % 60;
        StringBuilder buf = new StringBuilder();
        buf.append((day == 0 ? "" : day + "天 "));
        buf.append((hour == 0 ? "" : hour + "小时 "));
        buf.append((minute == 0 ? "" : minute + "分 "));
        return buf.toString();
    }

    public static String stringToDate(String lo) {
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        return sd.format(date);
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 10000L;
    }
    /**
     * format time
     *
     * @param time Media duration
     * @return HH:mm:ss
     */
    public static String formatMediaTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }

    /**
     * 判断当前日期是星期几
     * @param  pTime     设置的需要判断的时间  //格式如2012-09-08
     * @return dayForWeek 判断结果 星期天
     * @Exception 发生异常
     */
    public static String getWeek(String pTime) {

        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }

        return Week;
    }

    /**
     * 得到本周周一
     *
     * @return yyyy-MM-dd
     */
    public static String getMondayOfThisWeek(String format) {
        SimpleDateFormat df2 = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        return df2.format(c.getTime());
    }

    /**
     * 得到本周周日
     *
     * @return yyyy-MM-dd
     */
    public static String getSundayOfThisWeek(String format) {
        SimpleDateFormat df2 = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 7);
        return df2.format(c.getTime());
    }



    public  static String [] WEEK = {
        "星期一","星期二","星期三","星期四","星期五","星期六","星期日"
    };
}
