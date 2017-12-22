package com.n.ccalendarlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.n.ccalendarlib.factory.DateFactory;
import com.n.ccalendarlib.util.DateUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class WeekView extends BaseCalendarView {

    private Calendar currentMonthCalendar;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        maxTable = 7;
        setDate(DateFactory.create());
    }

    public void setDate(Calendar date) {
        currentMonthCalendar = date;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWeek(canvas);
    }

    private void drawWeek(Canvas canvas) {
        Calendar tempDate = DateUtil.addDay(currentMonthCalendar, -1);
        for (int i = 0; i < maxTable; i++) {
            setPoint(i);
            DateUtil.addDayWithSelf(tempDate, 1);
            String day = DateUtil.getDayOfMonth(tempDate) + "";
            Paint paint = getPaint(i, tempDate);
            drawSelectBg(canvas, i);
            drawText(canvas, day, i, paint);
            drawDot(canvas, tempDate, i);
        }
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        Calendar selectDate = calcDate(selectPosition);
        if (onCalendarItemClickListener != null) {
            onCalendarItemClickListener.onDayClick(selectPosition, selectDate);
        }
    }

    @Override
    protected Calendar calcDate(int index) {
        Calendar temp = DateFactory.create(currentMonthCalendar.getTime());
        return DateUtil.addDay(temp, index);
    }

    public Calendar getDate() {
        Calendar calendar = DateFactory.create(currentMonthCalendar.getTime());
        return DateUtil.addDay(calendar, selectPosition);
    }
}
