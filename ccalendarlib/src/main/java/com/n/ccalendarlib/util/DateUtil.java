package com.n.ccalendarlib.util;

import com.n.ccalendarlib.factory.DateFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * @return 当前年
     */
    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * @return 当前月份
     */
    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    /**
     * @return 当前日
     */
    public static int getDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return 当前是第几周
     */
    public static int getWeekOfMonth(Calendar calendar) {
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * @return 当前日期中的天
     */
    public static int getCurrentDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return 当前月中的第一天是周几
     */
    public static int getFirstDayOfWeek(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int index = temp.get(Calendar.DAY_OF_WEEK);
        if (index == 1) {
            index = 8;
        }
        return index - 1;
    }

    /**
     * @return 当前月中最大的天数
     */
    public static int getMaxDayOfMonth(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        return temp.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static boolean isToday(Calendar calendar) {
        Calendar current = DateFactory.create();
        return isEquals(current, calendar);
    }

    public static Calendar addMonth(Calendar calendar, int amount) {
        Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MONTH, amount);
        return temp;
    }

    public static Calendar addDay(Calendar calendar, int amount) {
        Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.DAY_OF_MONTH, amount);
        return temp;
    }

    public static Calendar addDayWithSelf(Calendar calendar, int amount) {
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar;
    }

    public static Calendar getDayInZero(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 0);
        return temp;
    }

    public static Calendar getDayInFirst(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        return temp;
    }

    public static Calendar getDayInEnd(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, getMaxDayOfMonth(calendar));
        return temp;
    }

    public static int getIntervalMonth(Calendar minDate, Calendar maxDate) {
        int result = maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH);
        int month = (maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    public static boolean isEquals(Calendar calendar1, Calendar calendar2) {
        Calendar current = calendar1;
        int cYear = getYear(current);
        int cMonth = getMonth(current);
        int cDay = getDayOfMonth(current);

        int year = getYear(calendar2);
        int month = getMonth(calendar2);
        int day = getDayOfMonth(calendar2);
        if (cYear != year) {
            return false;
        }
        if (cMonth != month) {
            return false;
        }
        if (cDay != day) {
            return false;
        }
        return true;
    }
}
