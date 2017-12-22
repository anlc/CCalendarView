package com.n.ccalendarlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.n.ccalendarlib.factory.DateFactory;
import com.n.ccalendarlib.util.DateUtil;

import java.util.Calendar;

/**
 * Created by n on 2017/11/23.
 */

public class MonthView extends BaseCalendarView {

    private Calendar currentMonthCalendar;
    private int monthFirstIndex; //当前月的第一天是周几

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        maxTable = 42;
        setDate(DateFactory.create());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getTotalHeight());
    }

    private int getTotalHeight() {
        return (int) (maxTable / 7 * singleHeight + paddingTop + paddingBottom + dp(8));
    }

    public void setDate(Calendar date) {
        currentMonthCalendar = date;
        monthFirstIndex = DateUtil.getFirstDayOfWeek(date) - 1;
        selectPosition = monthFirstIndex;
//        maxTable = getNextMonthStartIndex() <= 35 ? 35 : 42;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawMonth(canvas);
    }

    private void drawMonth(Canvas canvas) {
        Calendar tempDate = DateUtil.addDay(DateUtil.getDayInFirst(currentMonthCalendar), -monthFirstIndex - 1);
        for (int i = 0; i < maxTable; i++) {
            setPoint(i);
            DateUtil.addDayWithSelf(tempDate, 1);
            String day = DateUtil.getDayOfMonth(tempDate) + "";
            Paint paint = i < monthFirstIndex || i > getNextMonthStartIndex() ? configPaint.otherMonthTextPaint : getPaint(i, tempDate);
            drawSelectBg(canvas, i);
            drawText(canvas, day, i, paint);
            drawDot(canvas, tempDate, i);
        }
    }

    private int getNextMonthStartIndex() {
        return monthFirstIndex + DateUtil.getMaxDayOfMonth(currentMonthCalendar) - 1;
    }

    public void setSelectPositionOnFirst() {
        selectPosition = DateUtil.getFirstDayOfWeek(currentMonthCalendar) - 1;
    }

    public void setSelectPositionOnToday() {
        selectPosition = DateUtil.getDayOfMonth(currentMonthCalendar) + monthFirstIndex - 1;
    }

    public void setSelectPosition(int selectPosition) {
        if (selectPosition < monthFirstIndex || selectPosition > getNextMonthStartIndex()) {
            return;
        }
        this.selectPosition = selectPosition;
        Calendar selectDate = calcDate(selectPosition);
        if (onCalendarItemClickListener != null) {
            onCalendarItemClickListener.onDayClick(selectPosition, selectDate);
        }
    }

    protected Calendar calcDate(int index) {
        Calendar temp = DateUtil.getDayInZero(currentMonthCalendar);
        return DateUtil.addDay(temp, index - monthFirstIndex + 1);
    }

    public Calendar getDate() {
        Calendar calendar = DateUtil.getDayInFirst(currentMonthCalendar);
        return DateUtil.addDay(calendar, selectPosition - monthFirstIndex);
    }

    public float getSingleHeight() {
        return singleHeight;
    }
}