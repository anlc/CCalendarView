package com.n.ccalendarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.n.ccalendarlib.factory.ConfigPaint;
import com.n.ccalendarlib.factory.DateFactory;
import com.n.ccalendarlib.util.DateFormatUtil;
import com.n.ccalendarlib.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by n on 2017/11/23.
 */

public class DateView extends BaseCalendarView {

    private int maxTable = 42;
    private boolean isShowLunar = true;

    private Calendar lastMonthCalendar;
    private Calendar currentMonthCalendar;
    private int monthFirstIndex; //当前月的第一天是周几
    private ConfigPaint configPaint;//画笔

    private Point point;
    private int selectPosition;
    private int touchSlop;
    private float downY;
    private float downX;

    private List<Calendar> scheduleDate = new ArrayList<>();

    public DateView(Context context) {
        this(context, null);
    }

    public DateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CCalendarView);
        float textSize = array.getDimension(R.styleable.CCalendarView_c_text_size, sp(15));
        int textColor = array.getColor(R.styleable.CCalendarView_c_text_color, -1);
        float lunarSize = array.getDimension(R.styleable.CCalendarView_c_lunar_text_size, sp(9));
        int lunarColor = array.getColor(R.styleable.CCalendarView_c_lunar_text_color, -1);

        array.recycle();

        point = new Point();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        configPaint = new ConfigPaint.Build()
                .textPaint(textSize, textColor)
                .lunarPaint(lunarSize, lunarColor)
                .build();
        setBackgroundColor(Color.WHITE);
        setDate(DateFactory.create());
    }

    public void setScheduleDate(List<Calendar> scheduleDate) {
        this.scheduleDate = scheduleDate;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (6 * singleHeight + paddingTop + paddingBottom + dp(8)));
    }

    public void setDate(Calendar date) {
        lastMonthCalendar = DateUtil.addMonth(date, -1);
        currentMonthCalendar = date;
        monthFirstIndex = DateUtil.getFirstDayOfWeek(date) - 1;
        selectPosition = monthFirstIndex;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawMonthDate(canvas);
    }

    private void drawMonthDate(Canvas canvas) {
        // 上个月
        int lastMonthMaxDay = DateUtil.getMaxDayOfMonth(lastMonthCalendar);
        for (int i = 1; i <= monthFirstIndex; i++) {
            String day = String.valueOf(lastMonthMaxDay - monthFirstIndex + i);
            drawText(canvas, day, i - 1, configPaint.otherMonthTextPaint);
//            drawSelectBg(canvas, i);
        }
        // 当前月
        int currentMonthMaxDay = getCurrentMonthMaxDay();
        Calendar tempDate = DateUtil.getDayInZero(currentMonthCalendar);
        for (int i = 1; i <= currentMonthMaxDay; i++) {
            String day = String.valueOf(i);
//            Paint paint = getPaint(DateUtil.addDayWithSelf(tempDate, 1));
            DateUtil.addDayWithSelf(tempDate, 1);
            Paint paint = getPaint(i + monthFirstIndex);
            drawSelectBg(canvas, i + monthFirstIndex - 1);
            if (DateUtil.isToday(tempDate)) {
                drawTodayBg(canvas, i + monthFirstIndex - 1);
            }
            drawText(canvas, day, i + monthFirstIndex - 1, paint);
            drawDot(canvas, tempDate, i + monthFirstIndex - 1);
        }
        // 下一月
        int nextMonthStartIndex = getNextMonthStartIndex();
//        int lastDay = dateUtil.getMaxDayOfMonth(nextMonthCalendar);
        for (int i = nextMonthStartIndex; i < maxTable; i++) {
            String day = String.valueOf(i - nextMonthStartIndex + 1);
            drawText(canvas, day, i + 1, configPaint.otherMonthTextPaint);
//            drawSelectBg(canvas, i + 1);
        }
    }

    private void setPoint(int i) {
        point.set(i % 7, i / 7);
    }

    private int getCurrentMonthMaxDay() {
        return DateUtil.getMaxDayOfMonth(currentMonthCalendar);
    }

    private int getNextMonthStartIndex() {
        return monthFirstIndex + DateUtil.getMaxDayOfMonth(currentMonthCalendar) - 1;
    }

    // 画底部小圆点
    private void drawDot(Canvas canvas, Calendar calendar, int i) {
        setPoint(i);
        float left = point.x * singleWidth + paddingLeft;
        float top = point.y * singleHeight + paddingTop;
        for (Calendar item : scheduleDate) {
            if (DateUtil.isEquals(item, calendar)) {
                canvas.drawCircle(left + singleWidth / 2,
                        top + singleHeight,
                        dp(2),
                        i == selectPosition ? configPaint.selectDotPaint : configPaint.dotPaint);
                break;
            }
        }
    }

    // 画选中的背景
    private void drawSelectBg(Canvas canvas, int i) {
        if (i != selectPosition) {
            return;
        }
        setPoint(i);
        float left = point.x * singleWidth + paddingLeft;
        float top = point.y * singleHeight + paddingTop;
        canvas.drawCircle(left + singleWidth / 2, top + dp(4) + singleHeight / 2, singleWidth / 2, configPaint.selectBgPaint);
    }

    // 画今天的背景图
    private void drawTodayBg(Canvas canvas, int i) {
        setPoint(i);
        float left = point.x * singleWidth + paddingLeft;
        float top = point.y * singleHeight + paddingTop;
        canvas.drawCircle(left + singleWidth / 2, top + dp(4) + singleHeight / 2, singleWidth / 2, configPaint.selectBgPaint);
    }

    // 画日期
    private void drawText(Canvas canvas, String day, int i, Paint paint) {
        setPoint(i);
        float left = point.x * singleWidth;
        float top = point.y * singleHeight + offsetCenterY(configPaint.textPaint, day);
        drawText(canvas, day,
                left + offsetCenterX(configPaint.textPaint, day),
                top - textHeight(configPaint.textPaint, day) / 2,
                paint);

        //农历相关处理
        Paint tempPaint = configPaint.lunarPaint;
        if (paint == configPaint.selectTextPaint) tempPaint = configPaint.selectLunarPaint;
        if (paint == configPaint.otherMonthTextPaint) tempPaint = configPaint.otherMonthLunarPaint;
        drawLunar(canvas, left, top, i, tempPaint);
    }

    // 画文字
    private void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x, y, paint);
    }

    // 画农历
    private void drawLunar(Canvas canvas, float x, float y, int index, Paint paint) {
        if (isShowLunar) {
//            Calendar calendar = calcSelectDate(index);
            Calendar calendar = calcLunarDate(index);
            String lunar = LunarCalendar.getLunarText(calendar);
            canvas.drawText(lunar,
                    x + offsetCenterX(configPaint.lunarPaint, lunar),
                    y + textHeight(configPaint.lunarPaint, lunar),
                    paint);
        }
    }

    private Calendar calcLunarDate(int index) {
        Calendar calendar;
        if (index < monthFirstIndex) {
            Calendar temp = DateUtil.getDayInEnd(lastMonthCalendar);
            calendar = DateUtil.addDay(temp, index - monthFirstIndex + 1);
        } else {
            Calendar temp = DateUtil.getDayInZero(currentMonthCalendar);
            DateUtil.addDayWithSelf(temp, 1);
            calendar = DateUtil.addDay(temp, index - monthFirstIndex);
        }
        return calendar;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                downX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                float moveY = downY - event.getY();
                float moveX = downX - event.getX();
                if (Math.abs(moveY) < touchSlop && Math.abs(moveX) < touchSlop) {
                    selectItem(event.getX(), event.getY());
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void selectItem(float x, float y) {
        for (int i = 0; i < maxTable; i++) {
            setPoint(i);
            if (x > point.x * singleWidth + paddingLeft &&
                    x < point.x * singleWidth + singleWidth + paddingLeft &&
                    y > point.y * singleHeight + paddingTop &&
                    y < point.y * singleHeight + singleHeight + paddingTop) {
                setSelectPosition(i);
                invalidate();
                return;
            }
        }
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
        Calendar selectDate = calcSelectDate();
        Log.e("tag", "selectDate : " + DateFormatUtil.format(selectDate));
//        if (selectPosition < monthFirstIndex) {
//            if (onCalendarItemClickListener != null) {
//                onCalendarItemClickListener.onLastMonthClick(selectPosition, selectDate);
//            }
//        } else if (selectPosition > getNextMonthStartIndex()) {
//            if (onCalendarItemClickListener != null) {
//                onCalendarItemClickListener.onNextMonthClick(selectPosition, selectDate);
//            }
//        } else {
        if (onCalendarItemClickListener != null) {
            onCalendarItemClickListener.onDayClick(selectPosition, selectDate);
        }
//        }
    }

    private Calendar calcSelectDate() {
        Calendar calendar;
        if (selectPosition < monthFirstIndex) {
            Calendar temp = DateUtil.getDayInEnd(lastMonthCalendar);
            calendar = DateUtil.addDay(temp, selectPosition - monthFirstIndex + 1);
        } else {
            Calendar temp = DateUtil.getDayInZero(currentMonthCalendar);
            DateUtil.addDayWithSelf(temp, 1);
            calendar = DateUtil.addDay(temp, selectPosition - monthFirstIndex);
        }
        return calendar;
    }

    private Paint getPaint(int i) {
//        if (DateUtil.isToday(calendar)) {
        if (i == selectPosition + 1) {
            return configPaint.selectTextPaint;
        }
        return configPaint.textPaint;
    }

    public Calendar getDate() {
        return currentMonthCalendar;
    }

    private OnCalendarEventListener onCalendarItemClickListener;

    public void setOnCalendarItemClickListener(OnCalendarEventListener onCalendarItemClickListener) {
        this.onCalendarItemClickListener = onCalendarItemClickListener;
    }
}