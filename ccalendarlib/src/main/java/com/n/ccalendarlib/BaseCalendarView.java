package com.n.ccalendarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.n.ccalendarlib.factory.ConfigPaint;
import com.n.ccalendarlib.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BaseCalendarView extends View {

    protected int maxTable = 42;

    protected int width;
    protected int height;
    protected float singleWidth;
    protected float singleHeight;

    protected float paddingLeft;
    protected float paddingRight;
    protected float paddingTop;
    protected float paddingBottom;

    protected boolean isShowLunar = true;
    protected PointF point;
    protected ConfigPaint configPaint;//画笔
    protected int touchSlop;
    protected int selectPosition;
    private float downY;
    private float downX;

    private List<Calendar> scheduleDate = new ArrayList<>();

    public BaseCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CCalendarView);

        singleWidth = array.getDimension(R.styleable.CCalendarView_c_single_width, dp(42));
        singleHeight = array.getDimension(R.styleable.CCalendarView_c_single_height, dp(42));
        paddingLeft = array.getDimension(R.styleable.CCalendarView_c_padding_left, dp(16));
        paddingRight = array.getDimension(R.styleable.CCalendarView_c_padding_right, dp(16));
        paddingTop = array.getDimension(R.styleable.CCalendarView_c_padding_top, dp(0));
        paddingBottom = array.getDimension(R.styleable.CCalendarView_c_padding_bottom, dp(0));

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
    }

    public void setScheduleDate(List<Calendar> scheduleDate) {
        this.scheduleDate = scheduleDate;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        float tempWidth = (width - paddingLeft - paddingRight) / 7;
        singleWidth = Math.max(tempWidth, singleWidth);
    }

    public void setPoint(int i) {
        point.set(i % 7 * singleWidth, i / 7 * singleHeight);
    }


    // 画选中的背景
    public void drawSelectBg(Canvas canvas, int i) {
        if (i != selectPosition) {
            return;
        }
        float radius = singleWidth / 2.4f;
        float left = point.x + singleWidth / 2 + paddingLeft;
        float top = point.y + singleHeight / 2 + paddingTop + dp(2);
        canvas.drawCircle(left, top, radius, configPaint.selectBgPaint);
    }

    // 画日期
    public void drawText(Canvas canvas, String day, int i, Paint paint) {
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

    // 画底部小圆点
    protected void drawDot(Canvas canvas, Calendar calendar, int i) {
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

    protected abstract Calendar calcDate(int index);
    protected abstract void setSelectPosition(int index);

    public void selectItem(float x, float y) {
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

    public Paint getPaint(int i, Calendar calendar) {
        if (i == selectPosition) {
            return configPaint.selectTextPaint;
        } else if (DateUtil.isToday(calendar)) {
            return configPaint.todayPaint;
        }
        return configPaint.textPaint;
    }

    public float dp(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getContext().getResources().getDisplayMetrics());
    }

    public float sp(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getContext().getResources().getDisplayMetrics());
    }

    // 距中心的偏移
    public float offsetCenterX(Paint paint, String text) {
        float textWidth = textWidth(paint, text);
        return paddingLeft + singleWidth / 2 - textWidth / 2;
    }

    // 距中心的偏移
    public float offsetCenterY(Paint paint, String text) {
        float textHeight = textHeight(paint, text);
        return paddingTop + textHeight / 2 + singleHeight / 2;
    }

    protected float textHeight(Paint paint, String message) {
        return textBounds(paint, message).height();
    }

    protected float textWidth(Paint paint, String message) {
        return paint.measureText(message, 0, message.length());
    }

    private Rect textBounds(Paint paint, String message) {
        Rect rect = new Rect();
        paint.getTextBounds(message, 0, message.length(), rect);
        return rect;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    protected OnCalendarEventListener onCalendarItemClickListener;

    public void setOnCalendarItemClickListener(OnCalendarEventListener onCalendarItemClickListener) {
        this.onCalendarItemClickListener = onCalendarItemClickListener;
    }
}
