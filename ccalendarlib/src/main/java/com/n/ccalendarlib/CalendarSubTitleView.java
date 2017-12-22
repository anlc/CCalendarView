package com.n.ccalendarlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.n.ccalendarlib.factory.PaintFactory;

import java.util.Calendar;

public class CalendarSubTitleView extends BaseCalendarView {

    private final String[] week = new String[]{"一", "二", "三", "四", "五", "六", "日",};

    private Paint titlePaint;
    private Paint linePaint;
    private Path linePath;

    private float titleHeight;

    public CalendarSubTitleView(Context context) {
        this(context, null);
    }

    public CalendarSubTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarSubTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        titlePaint = PaintFactory.createStrokePaint(Color.GRAY);
        titlePaint.setTextSize(sp(12));

        linePaint = PaintFactory.createStrokePaint(Color.GRAY);
        linePaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        linePath = new Path();

        titleHeight = dp(32);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected Calendar calcDate(int index) {
        return null;
    }

    @Override
    protected void setSelectPosition(int index) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < week.length; i++) {
            String text = week[i];
            float textHeight = textHeight(titlePaint, text);
            canvas.drawText(text, i * singleWidth + offsetCenterX(titlePaint, text), titleHeight - dp(12), titlePaint);
        }
        linePath.moveTo(paddingLeft + dp(6), titleHeight);
        linePath.lineTo(width - paddingRight - dp(6), titleHeight);
        canvas.drawPath(linePath, linePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (titleHeight + paddingTop));
    }

    public int getPaddingLeft() {
        return (int) offsetCenterX(titlePaint, week[0]);
    }
}
