package com.n.ccalendarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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

public class CalendarDateView extends BaseCalendarView {

    private int maxTable = 42;
    private boolean isShowLunar = true;

    private Calendar currentMonthCalendar;
    private int monthFirstIndex; //当前月的第一天是周几
    private ConfigPaint configPaint;//画笔

    private PointF point;
    private int selectPosition;
    private int touchSlop;
    private float downY;
    private float downX;

    private List<Calendar> scheduleDate = new ArrayList<>();

    public CalendarDateView(Context context) {
        this(context, null);
    }

    public CalendarDateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CCalendarView);
        float textSize = array.getDimension(R.styleable.CCalendarView_c_text_size, sp(15));
        int textColor = array.getColor(R.styleable.CCalendarView_c_text_color, -1);
        float lunarSize = array.getDimension(R.styleable.CCalendarView_c_lunar_text_size, sp(9));
        int lunarColor = array.getColor(R.styleable.CCalendarView_c_lunar_text_color, -1);

        array.recycle();

        point = new PointF();
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
        postInvalidate();
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

    private void setPoint(int i) {
        point.set(i % 7 * singleWidth, i / 7 * singleHeight);
    }

    // 画底部小圆点
    private void drawDot(Canvas canvas, Calendar calendar, int i) {
        float left = point.x + paddingLeft;
        float top = point.y + offsetCenterY(configPaint.textPaint, "00");
        for (Calendar item : scheduleDate) {
            if (DateUtil.isEquals(item, calendar)) {
                canvas.drawCircle(left + singleWidth / 2,
                        top + textHeight(configPaint.lunarPaint, "农历") + dp(4),
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
        float radius = singleWidth / 2.4f;
        float left = point.x + singleWidth / 2 + paddingLeft;
        float top = point.y + singleHeight / 2 + paddingTop + dp(2);
        canvas.drawCircle(left, top, radius, configPaint.selectBgPaint);
    }

    // 画日期
    private void drawText(Canvas canvas, String day, int i, Paint paint) {
        float top = point.y + offsetCenterY(configPaint.textPaint, day);
        drawText(canvas, day,
                point.x + offsetCenterX(configPaint.textPaint, day),
                top - textHeight(configPaint.textPaint, day) / 2,
                paint);

        //农历相关处理
        Paint tempPaint = configPaint.lunarPaint;
        if (paint == configPaint.todayPaint) tempPaint = configPaint.todayLunarPaint;
        if (paint == configPaint.selectTextPaint) tempPaint = configPaint.selectLunarPaint;
        if (paint == configPaint.otherMonthTextPaint) tempPaint = configPaint.otherMonthLunarPaint;
        drawLunar(canvas, point.x, top + textHeight(paint, day) / 2, i, tempPaint);
    }

    // 画文字
    private void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x, y, paint);
    }

    // 画农历
    private void drawLunar(Canvas canvas, float x, float y, int index, Paint paint) {
        if (isShowLunar) {
            Calendar calendar = calcDate(index);
            String lunar = LunarCalendar.getLunarText(calendar);
            drawText(canvas, lunar, x + offsetCenterX(configPaint.lunarPaint, lunar), y + dp(2), paint);
        }
    }

    private int getCurrentMonthMaxDay() {
        return DateUtil.getMaxDayOfMonth(currentMonthCalendar);
    }

    private int getNextMonthStartIndex() {
        return monthFirstIndex + DateUtil.getMaxDayOfMonth(currentMonthCalendar) - 1;
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
            if (x > point.x + paddingLeft &&
                    x < point.x + singleWidth + paddingLeft &&
                    y > point.y + paddingTop &&
                    y < point.y + singleHeight + paddingTop) {
                setSelectPosition(i);
                invalidate();
                return;
            }
        }
    }

    public void setSelectPositionOnFirst() {
        selectPosition = DateUtil.getFirstDayOfWeek(currentMonthCalendar) - 1;
//        postInvalidate();
    }

    public void setSelectPositionOnToday() {
        selectPosition = DateUtil.getDayOfMonth(currentMonthCalendar) + monthFirstIndex - 1;
//        postInvalidate();
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

    private Calendar calcDate(int index) {
        Calendar temp = DateUtil.getDayInZero(currentMonthCalendar);
        return DateUtil.addDay(temp, index - monthFirstIndex + 1);
    }

    private Paint getPaint(int i, Calendar calendar) {
        if (i == selectPosition) {
            return configPaint.selectTextPaint;
        } else if (DateUtil.isToday(calendar)) {
            return configPaint.todayPaint;
        }
        return configPaint.textPaint;
    }

    public Calendar getDate() {
        Calendar calendar = DateUtil.getDayInFirst(currentMonthCalendar);
        return DateUtil.addDay(calendar, selectPosition - monthFirstIndex);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    private OnCalendarEventListener onCalendarItemClickListener;

    public void setOnCalendarItemClickListener(OnCalendarEventListener onCalendarItemClickListener) {
        this.onCalendarItemClickListener = onCalendarItemClickListener;
    }

    public float getSingleHeight() {
        return singleHeight;
    }
}