package com.n.ccalendarlib;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.n.ccalendarlib.util.DateFormatUtil;
import com.n.ccalendarlib.util.DateUtil;
import com.n.ccalendarlib.util.Logger;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class MonthViewPager extends BaseViewPager {

    public MonthViewPager(Context context, Calendar minDate, final OnCalendarEventListener onCalendarEventListener) {
        super(context, minDate, onCalendarEventListener);

        setAdapter(new MonthAdapter());
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getItem(position).setSelectPositionOnToday();
                } else {
                    getItem(position).setSelectPositionOnFirst();
                }
                if (onCalendarEventListener != null) {
                    onCalendarEventListener.onMonthChanged(position, DateUtil.getDayInFirst(getItemDate(position)));
                    onCalendarEventListener.onDayClick(getItem().getSelectPosition(), getItem().getDate());
                    onCalendarEventListener.onMonthPageChange(position, getItem().getDate());
                }
//                setScheduleDate();
//                onDayClick(getItem().getSelectPosition(), getItem().getDate());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public Calendar getItemDate(int position) {
        Calendar temp = data.get(position);
        if (temp != null) {
            return temp;
        }
        Calendar calendar = DateUtil.addMonth(minDate, position);
        data.put(position, calendar);
        return calendar;
    }

    public MonthView getItem() {
        return getItem(getCurrentItem());
    }

    public MonthView getItem(int position) {
        return (MonthView) arrayDateView.get(position);
    }

    public void setSelectDate(Calendar selectDate) {
        setCurrentItem(DateUtil.getIntervalMonth(getItem().getDate(), selectDate), false);
    }

    class MonthAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return DateUtil.getIntervalMonth(minDate, maxDate);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MonthView dateView = generatedItem(position);
            container.addView(dateView);
            if (position != 0) {
                dateView.setSelectPositionOnFirst();
            } else {
                dateView.setSelectPositionOnToday();
            }
            arrayDateView.put(position, dateView);
            return dateView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            MonthView dateView = (MonthView) object;
            container.removeView(dateView);
            arrayDateView.remove(position);
        }
    }

    private MonthView generatedItem(int position) {
        Calendar calendar = DateUtil.addMonth(minDate, position);
        MonthView dateView = new MonthView(getContext());
        dateView.setDate(calendar);
        dateView.setOnCalendarItemClickListener(onCalendarEventListener);
        return dateView;
    }
}
