package com.n.ccalendarlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
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
import com.n.ccalendarlib.util.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by n on 2017/12/10.
 */

public class CCalendarView extends FrameLayout implements OnCalendarEventListener {

    private OnCalendarEventListener onCalendarEventListener;
    private SparseArrayCompat<Calendar> data;
    private SparseArrayCompat<CalendarDateView> arrayDateView;
    private FrameLayout titleViewLayout;
    private TextView titleView;
    private ViewPager viewPager;
    private FrameLayout bottomView;
    private Calendar minDate;
    private Calendar maxDate;
    private List<Calendar> scheduleCalendars;

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

        viewPager = new ViewPager(context);
        addView(viewPager);
        titleViewLayout = new FrameLayout(context);
        addView(titleViewLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(new CalendarSubTitleView(context, attrs));
        bottomView = new FrameLayout(context){
            @Override
            public boolean onInterceptHoverEvent(MotionEvent event) {
                return CCalendarView.this.onInterceptHoverEvent(event);
            }
        };
        bottomView.setBackgroundColor(Color.WHITE);
        addView(bottomView);
        addAdapter();
    }

    public void setOnCalendarEventListener(OnCalendarEventListener onCalendarEventListener) {
        this.onCalendarEventListener = onCalendarEventListener;
    }

    //设置对应日期下的点
    public void setScheduleDate(List<String> scheduleDate) {
        scheduleCalendars= new ArrayList<>();
        for (String date : scheduleDate) {
            scheduleCalendars.add(DateFactory.create(date, DateFormatUtil.PATTERN.YYYY_MM_DD));
        }
        setScheduleDate();
    }

    public void setScheduleDate() {
        CalendarDateView dateView = arrayDateView.get(viewPager.getCurrentItem());
        dateView.setScheduleDate(scheduleCalendars);
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
                }
//                arrayDateView.get(position).measure(0, 0);
                setScheduleDate();
                onDayClick(getItem().getSelectPosition(), getItem().getDate());
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
        dateView.setOnCalendarItemClickListener(this);
        return dateView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View titleLayout = getChildAt(1);
        View subTitleView = getChildAt(2);
        View viewPager = getChildAt(0);
        View bottomView = getChildAt(3);

        int titleLayoutHeight = titleLayout.getMeasuredHeight();
        int subTitleHeight = subTitleView.getMeasuredHeight() + titleLayoutHeight;
        int viewPagerHeight = getItem().getMeasuredHeight() + subTitleHeight;
        titleLayout.layout(left, top, right, titleLayoutHeight);
        subTitleView.layout(left, titleLayoutHeight, right, subTitleHeight);
        viewPager.layout(left, subTitleHeight, right, viewPagerHeight);
        bottomView.layout(left, viewPagerHeight, right, getMeasuredHeight());
    }

    private int touchSlop;
    private float downY;
    private float downX;
    private float mLastY;

    private boolean isAnimating = false;
    private int swipeHeight;
    private int translationDirection = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isAnimating) {
            return true;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = downY = ev.getY();
                downX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = downY - ev.getY();
                float moveX = downX - ev.getX();
                if (Math.abs(moveY) > touchSlop || Math.abs(moveX) > touchSlop) {
                    if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                        //上下滑动
                        float dy = ev.getY() - mLastY;
                        if (Math.abs(dy) > touchSlop) {
                            if ((dy > 0 && bottomView.getTranslationY() <= 0)
                                    || (dy < 0 && bottomView.getTranslationY() >= -swipeHeight)) {
                                mLastY = ev.getY();
                                return true;
                            }
                        }
                    } else {
                        //左右滑动
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = downY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = event.getY() - mLastY;
                if (dy > 0 && bottomView.getTranslationY() + dy >= 0) {
                    bottomView.setTranslationY(0);
                    translationDateView();
                    return true;
                }
                if (dy < 0 && bottomView.getTranslationY() + dy <= -swipeHeight) {
                    bottomView.setTranslationY(-swipeHeight);
                    translationDateView();
                    return true;
                }
                bottomView.setTranslationY(bottomView.getTranslationY() + dy);
                translationDateView();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (bottomView.getTranslationY() == 0 ||
                        bottomView.getTranslationY() == swipeHeight) {
                    break;
                }
                if (event.getY() - downY > 0) {
                    animateShow();
                } else {
                    animateHide();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        swipeHeight = getItem().getMeasuredHeight() - getSingleHeight();
    }

    private int getSingleHeight() {
        return (int) getItem().getSingleHeight();
    }

    private CalendarDateView getItem(){
        return arrayDateView.get(viewPager.getCurrentItem());
    }

    private void translationDateView() {
        float percent = bottomView.getTranslationY() * 1.0f / swipeHeight;
        viewPager.setTranslationY(translationDirection * percent);
    }

    private void animateShow() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomView, "translationY", bottomView.getTranslationY(), 0f);
        animator.setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / swipeHeight;
                viewPager.setTranslationY(translationDirection * percent);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        animator.start();
    }

    private void animateHide() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomView, "translationY", bottomView.getTranslationY(), -swipeHeight);
        animator.setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / swipeHeight;
                viewPager.setTranslationY(translationDirection * percent);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        animator.start();
    }

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
        translationDirection = position / 7 * getSingleHeight();
    }
}
