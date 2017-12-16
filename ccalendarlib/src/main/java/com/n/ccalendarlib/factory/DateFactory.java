package com.n.ccalendarlib.factory;

import com.n.ccalendarlib.util.DateFormatUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by n on 2017/12/8.
 */

public class DateFactory {

    public static Calendar create() {
        return Calendar.getInstance();
    }

    public static Calendar create(Date date) {
        Calendar calendar = create();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar create(String date, String pattern) {
        Date temp = DateFormatUtil.parse(date, pattern);
        return create(temp);
    }
}
