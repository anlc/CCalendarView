package com.n.ccalendarlib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by n on 2017/12/8.
 */

public class DateFormatUtil {

    public interface PATTERN {
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        String HH_MM = "HH:mm";
    }

    public static String format(Calendar calendar, String pattern) {
        return format(calendar.getTime(), pattern);
    }

    public static String format(Calendar calendar) {
        return format(calendar, PATTERN.YYYY_MM_DD);
    }

    public static String format(Date date) {
        return format(date, PATTERN.YYYY_MM_DD);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date parse(String date) {
        return parse(date, PATTERN.YYYY_MM_DD);
    }

    public static Date parse(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
