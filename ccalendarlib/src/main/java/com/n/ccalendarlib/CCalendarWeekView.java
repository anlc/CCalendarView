package com.n.ccalendarlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.util.Calendar;

/**
 * Created by n on 2017/12/15.
 */

public class CCalendarWeekView  extends FrameLayout implements OnCalendarEventListener {

    private CalendarSubTitleView subTitleView;
    private CalendarDateView dateView;
    private FrameLayout bottomView;

    private int touchSlop;
    private float downY;
    private float downX;
    private float mLastY;

    private boolean isAnimating = false;
    private boolean isSlideVer = true;

    public CCalendarWeekView(Context context) {
        this(context, null);
    }

    public CCalendarWeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCalendarWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dateView = new CalendarDateView(context);
        addView(dateView);
        subTitleView = new CalendarSubTitleView(context);
        addView(subTitleView);
        bottomView = new FrameLayout(context);
        bottomView.setBackgroundColor(Color.GRAY);
        addView(bottomView);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        dateView.setOnCalendarItemClickListener(this);
    }

    public void setBottomView(View view) {
        bottomView.addView(view);
    }

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
//                float moveY = downY - event.getY();
//                Log.e("tag", "--moveY-->" + moveY + "--getTranslationY-->" + bottomView.getTranslationY() + "--->" + (bottomView.getTranslationY() + moveY));
//                if (moveY > 0 && bottomView.getTranslationY() + moveY <= 0) {
//                    translationY(-moveY);
//                    return true;
//                }
//                if (moveY < 0 && bottomView.getTranslationY() + moveY >= 0) {
//                    translationY(0);
//                    return true;
//                }
//                translationY(bottomView.getTranslationY() + moveY);
//                break;
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

    private void translationY(float y) {
        bottomView.setTranslationY(y);
        translationDateView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getSingleHeight(), MeasureSpec.EXACTLY);
        dateView.measure(widthMeasureSpec, heightSpec);
        bottomView.measure(widthMeasureSpec, heightSpec);
    }

    private int swipeHeight;
    private int translationDirection = 0;

    private void translationDateView() {
        float percent = bottomView.getTranslationY() * 1.0f / swipeHeight;
        dateView.setTranslationY(translationDirection * percent);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        swipeHeight = dateView.getMeasuredHeight() - getSingleHeight();
    }

    private int getSingleHeight() {
        return (int) dateView.getSingleHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View dateView = getChildAt(0);
        View subTitleView = getChildAt(1);
        View bottomView = getChildAt(2);

        int dateHeight = dateView.getMeasuredHeight();
        int subTitleHeight = subTitleView.getMeasuredHeight();
        int bottomHeight = bottomView.getMeasuredHeight();

        subTitleView.layout(left, top, right, subTitleHeight);
        dateView.layout(left, subTitleView.getBottom(), right, subTitleView.getBottom() + dateHeight);
        bottomView.layout(left, dateView.getBottom() , right, dateView.getBottom() + bottomHeight);
    }

    private void animateShow() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomView, "translationY", bottomView.getTranslationY(), 0f);
        animator.setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / swipeHeight;
                dateView.setTranslationY(translationDirection * percent);
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
                dateView.setTranslationY(translationDirection * percent);
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

    }

    @Override
    public void onDayClick(int position, Calendar date) {
        int line = position / 7;
        translationDirection = line * getSingleHeight();
    }
}
