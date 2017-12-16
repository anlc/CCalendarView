package com.n.ccalendarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class BaseCalendarView extends View {

    protected int width;
    protected int height;
    protected float singleWidth;
    protected float singleHeight;

    protected float paddingLeft;
    protected float paddingRight;
    protected float paddingTop;
    protected float paddingBottom;

    public BaseCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CCalendarView);

        singleWidth = array.getDimension(R.styleable.CCalendarView_c_single_width, dp(42));
        singleHeight = array.getDimension(R.styleable.CCalendarView_c_single_height, dp(42));
        paddingLeft = array.getDimension(R.styleable.CCalendarView_c_padding_left, dp(16));
        paddingRight = array.getDimension(R.styleable.CCalendarView_c_padding_right, dp(16));
        paddingTop = array.getDimension(R.styleable.CCalendarView_c_padding_top, dp(0));
        paddingBottom = array.getDimension(R.styleable.CCalendarView_c_padding_bottom, dp(0));

        array.recycle();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        float tempWidth = (width - paddingLeft - paddingRight) / 7;
        singleWidth = Math.max(tempWidth, singleWidth);
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
}
