package com.n.ccalendarlib;

import java.util.Calendar;

/**
 * Created by n on 2017/11/23.
 */

public interface OnCalendarEventListener {

    void onWeekPageChange(int position, Calendar date);

    void onMonthPageChange(int position, Calendar date);

    void onMonthChanged(int position, Calendar date);

    void onDayClick(int position, Calendar date);
}
