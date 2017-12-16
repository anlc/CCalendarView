package com.n.ccalendarlib.factory;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by n on 2017/11/23.
 */

public class ConfigPaint {

    public Paint textPaint;
    public Paint todayPaint;
    public Paint lunarPaint;
    public Paint todayLunarPaint;

    public Paint otherMonthLunarPaint;
    public Paint otherMonthTextPaint;

    public Paint selectTextPaint;
    public Paint selectLunarPaint;
    public Paint selectDotPaint;

    public Paint selectBgPaint;
    public Paint dotPaint;

    public static ConfigPaint create() {
        return new ConfigPaint(new Build());
    }

    public ConfigPaint(Build build) {
        textPaint = PaintFactory.createStrokePaint(build.textColor);
        todayPaint = PaintFactory.createStrokePaint(build.todayTextColor);
        lunarPaint = PaintFactory.createStrokePaint(build.lunarColor);
        todayLunarPaint = PaintFactory.createStrokePaint(build.todayTextColor);
        textPaint.setTextSize(build.textSize);
        todayPaint.setTextSize(build.textSize);
        lunarPaint.setTextSize(build.lunarTextSize == -1 ? build.textSize : build.lunarTextSize);
        todayLunarPaint.setTextSize(build.lunarTextSize == -1 ? build.textSize : build.lunarTextSize);

        otherMonthTextPaint = PaintFactory.createStrokePaint(build.otherMonthTextColor);
        otherMonthLunarPaint = PaintFactory.createStrokePaint(build.otherMonthTextColor);
        otherMonthTextPaint.setTextSize(build.textSize);
        otherMonthLunarPaint.setTextSize(build.lunarTextSize == -1 ? build.textSize : build.lunarTextSize);

        selectTextPaint = PaintFactory.createStrokePaint(build.selectTextColor);
        selectLunarPaint = PaintFactory.createStrokePaint(build.selectTextColor);
        selectTextPaint.setTextSize(build.textSize);
        selectLunarPaint.setTextSize(build.lunarTextSize == -1 ? build.textSize : build.lunarTextSize);

        selectBgPaint = PaintFactory.createFillPaint(build.selectBgColor);
        selectDotPaint = PaintFactory.createFillPaint(build.selectTextColor);
        dotPaint = PaintFactory.createFillPaint(build.dotColor);
    }

    public static class Build {
        private int textColor;
        private int lunarColor;
        private int todayTextColor;
        private int selectTextColor;
        private int selectBgColor;
        private int otherMonthTextColor;
        private int dotColor;
        private float textSize;
        private float lunarTextSize = -1;

        public Build() {
            textColor = Color.parseColor("#7C8A95");
            lunarColor = Color.GRAY;
            todayTextColor = Color.RED;
            selectTextColor = Color.WHITE;
            selectBgColor = Color.parseColor("#00ADDF");
            dotColor = Color.parseColor("#00ADDF");
            otherMonthTextColor = Color.parseColor("#CBD1D2");
        }

        public Build textPaint(float textSize, int textColor) {
            this.textSize = textSize;
            if (textColor == -1) return this;
            this.textColor = textColor;
            return this;
        }

        public Build lunarPaint(float textSize, int textColor) {
            this.lunarTextSize = textSize;
            if (textColor == -1) return this;
            this.textColor = textColor;
            return this;
        }

        public ConfigPaint build() {
            return new ConfigPaint(this);
        }
    }
}
