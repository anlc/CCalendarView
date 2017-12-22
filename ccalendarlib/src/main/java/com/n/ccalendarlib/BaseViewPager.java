package com.n.ccalendarlib;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;

import com.n.ccalendarlib.factory.DateFactory;
import com.n.ccalendarlib.util.DateFormatUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class BaseViewPager extends ViewPager {

    protected SparseArrayCompat<Calendar> data;
    protected OnCalendarEventListener onCalendarEventListener;
    protected SparseArrayCompat<BaseCalendarView> arrayDateView;
    protected Calendar minDate;
    protected Calendar maxDate;

    public BaseViewPager(Context context, Calendar minDate, OnCalendarEventListener onCalendarEventListener) {
        super(context);
        data = new SparseArrayCompat<>();
        arrayDateView = new SparseArrayCompat<>();
        this.minDate = minDate;
        this.onCalendarEventListener = onCalendarEventListener;
        maxDate = DateFactory.create(DateFormatUtil.parse("2099-12-12"));
    }
}
