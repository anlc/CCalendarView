package com.n.ccalendarlib;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.n.ccalendarlib.factory.DateFactory;
import com.n.ccalendarlib.util.DateFormatUtil;
import com.n.ccalendarlib.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/12/18.
 */

public class CalendarTestView extends FrameLayout {

    private WeekViewPager viewPager;
    private Calendar minDate;

    public CalendarTestView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarTestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        minDate = DateFactory.create();

        viewPager = new WeekViewPager(context, minDate, null);
        addView(viewPager);
        addView(new CalendarSubTitleView(context, attrs));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View subTitleView = getChildAt(1);
        View viewPager = getChildAt(0);

        int subTitleHeight = subTitleView.getMeasuredHeight();
        int viewPagerHeight = this.viewPager.getItem().getMeasuredHeight() + subTitleHeight;
        subTitleView.layout(left, top, right, subTitleHeight);
        viewPager.layout(left, subTitleHeight, right, viewPagerHeight);
    }
}
