package com.n.ccalendarlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
 * Created by n on 2017/12/10.
 */

public class CCalendarView extends FrameLayout {

    private OnCalendarEventListener onCalendarEventListener;
    private SparseArrayCompat<Calendar> data;
    private SparseArrayCompat<CalendarDateView> arrayDateView;
    private FrameLayout titleViewLayout;
    private TextView titleView;
    private ViewPager viewPager;
    private FrameLayout bottomView;
    private Calendar minDate;
    private Calendar maxDate;

    public CCalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        data = new SparseArrayCompat<>();
        arrayDateView = new SparseArrayCompat<>();
        minDate = DateFactory.create();
        maxDate = DateFactory.create(DateFormatUtil.parse("2099-12-12"));

        titleViewLayout = new FrameLayout(context);
        addView(titleViewLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(new CalendarSubTitleView(context, attrs));
        viewPager = new ViewPager(context);
        addView(viewPager);
        bottomView = new FrameLayout(context);
        addView(bottomView);
        addAdapter();
    }

    public void setOnCalendarEventListener(OnCalendarEventListener onCalendarEventListener) {
        this.onCalendarEventListener = onCalendarEventListener;
    }

    //设置对应日期下的点
    public void setScheduleDate(List<String> scheduleDate) {
        List<Calendar> calendars = new ArrayList<>();
        for (String date : scheduleDate) {
            calendars.add(DateFactory.create(date, DateFormatUtil.PATTERN.YYYY_MM_DD));
        }
        CalendarDateView dateView = arrayDateView.get(viewPager.getCurrentItem());
        dateView.setScheduleDate(calendars);
    }

    public void setTitleView(View view, int titleIdRes) {
        titleViewLayout.addView(view);
        View titleView = view.findViewById(titleIdRes);
        if (titleView instanceof TextView) {
            this.titleView = (TextView) titleView;
            this.titleView.setText(DateFormatUtil.format(minDate));
        } else {
            throw new RuntimeException("titleView is not TextView");
        }
    }

    public void setTitleView(int viewRes, int titleIdRes) {
        View view = LayoutInflater.from(getContext()).inflate(viewRes, null);
        setTitleView(view, titleIdRes);
    }

    private void setTitle(String date) {
        if (titleView != null) {
            titleView.setText(date);
        }
    }

    public void setBottomView(View bottomView) {
        this.bottomView.addView(bottomView);
    }

    private void addAdapter() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return DateUtil.getIntervalMonth(minDate, maxDate);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                CalendarDateView dateView = (CalendarDateView) object;
                container.removeView(dateView);
                arrayDateView.remove(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                CalendarDateView dateView = generatedItem(position);
                container.addView(dateView);
                if (position != 0) {
                    dateView.setSelectPositionOnFirst();
                } else {
                    dateView.setSelectPositionOnToday();
                }
                arrayDateView.put(position, dateView);
                return dateView;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    arrayDateView.get(position).setSelectPositionOnToday();
                } else {
                    arrayDateView.get(position).setSelectPositionOnFirst();
                }
                if (onCalendarEventListener != null) {
                    onCalendarEventListener.onMonthChanged(position, DateUtil.getDayInFirst(getItem(position)));
                    onCalendarEventListener.onDayClick(position, arrayDateView.get(position).getDate());
                }
                setTitle(DateFormatUtil.format(arrayDateView.get(position).getDate()));
                arrayDateView.get(position).measure(0,0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private Calendar getItem(int position) {
        Calendar temp = data.get(position);
        if (temp != null) {
            return temp;
        }
        Calendar calendar = DateUtil.addMonth(minDate, position);
        data.put(position, calendar);
        return calendar;
    }

    private CalendarDateView generatedItem(int position) {
        Calendar calendar = DateUtil.addMonth(minDate, position);
        CalendarDateView dateView = new CalendarDateView(getContext());
        dateView.setDate(calendar);
        dateView.setOnCalendarItemClickListener(new OnCalendarEventListener() {
            @Override
            public void onMonthChanged(int position, Calendar date) {
                setTitle(DateFormatUtil.format(date));
                if (onCalendarEventListener != null) {
                    onCalendarEventListener.onMonthChanged(position, DateUtil.getDayInFirst(getItem(position)));
                }
            }

            @Override
            public void onDayClick(int position, Calendar date) {
                setTitle(DateFormatUtil.format(date));
                if (onCalendarEventListener != null) {
                    onCalendarEventListener.onDayClick(position, arrayDateView.get(position).getDate());
                }
            }
        });
        return dateView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View titleLayout = getChildAt(0);
        View subTitleView = getChildAt(1);
        View viewPager = getChildAt(2);
        View bottomView = getChildAt(3);

        int titleLayoutHeight = titleLayout.getMeasuredHeight();
        int subTitleHeight = subTitleView.getMeasuredHeight();
        int viewPagerHeight = viewPager.getMeasuredHeight();
        int bottomViewHeight = bottomView.getMeasuredHeight();
        titleLayout.layout(left, top, right, titleLayoutHeight);
        subTitleView.layout(left, titleLayoutHeight, right, titleLayoutHeight + subTitleHeight);
        viewPager.layout(left, subTitleView.getBottom(), right, viewPagerHeight);
        bottomView.layout(left, viewPager.getBottom(), right, bottomViewHeight);
    }

    private int touchSlop;
    private float downY;
    private float downX;
    private float mLastY;

    private boolean isAnimating = false;

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (isAnimating) {
            return true;
        }
        return super.onInterceptHoverEvent(event);
    }
}
