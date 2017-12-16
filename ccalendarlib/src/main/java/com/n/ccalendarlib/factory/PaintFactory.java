package com.n.ccalendarlib.factory;

import android.graphics.Paint;
import android.support.annotation.ColorInt;

public class PaintFactory {

    public static Paint createStrokePaint(@ColorInt int color){
        Paint paint = createPaint(color);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public static Paint createFillPaint(@ColorInt int color){
        Paint paint = createPaint(color);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private static Paint createPaint(@ColorInt int color){
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }
}
