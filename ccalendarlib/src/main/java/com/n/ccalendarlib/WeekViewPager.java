package com.n.ccalendarlib;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.n.ccalendarlib.util.DateFormatUtil;
import com.n.ccalendarlib.util.DateUtil;
import com.n.ccalendarlib.util.Logger;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class WeekViewPager extends BaseViewPager {

    public WeekView getItem() {
        return (WeekView) arrayDateView.get(getCurrentItem());
    }

    public WeekViewPager(Context context, Calendar minDate, final OnCalendarEventListener onCalendarEventListener) {
        super(context, minDate, onCalendarEventListener);

        setAdapter(new WeekAdapter());
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onCalendarEventListener.onWeekPageChange(position, getItem().getDate());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setSelected(Calendar date) {
        Logger.e("--getItem-->" + DateFormatUtil.format(getItem().getDate()));
        Logger.e("--setCurrentItem-->" + DateUtil.getIntervalWeek(getItem().getDate(), date));
        setCurrentItem(DateUtil.getIntervalWeek(getItem().getDate(), date), false);
    }

    class WeekAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return DateUtil.getIntervalWeek(minDate, maxDate);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WeekView dateView = generatedItem(position);
            container.addView(dateView);
            arrayDateView.put(position, dateView);
            return dateView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            WeekView dateView = (WeekView) object;
            container.removeView(dateView);
            arrayDateView.remove(position);
        }
    }

    private WeekView generatedItem(int position) {
        Calendar calendar = DateUtil.addDay(minDate, position * 7);
        WeekView dateView = new WeekView(getContext());
        dateView.setDate(calendar);
        dateView.setOnCalendarItemClickListener(onCalendarEventListener);
        return dateView;
    }
}
