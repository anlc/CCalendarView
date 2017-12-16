package com.n.ccalendarview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.n.ccalendarlib.CCalendarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n on 2017/12/15.
 */

public class ViewPagerActivity extends AppCompatActivity {

    private CCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        calendarView = findViewById(R.id.calendar_view);
        calendarView.setTitleView(R.layout.calendar_title, R.id.title_view);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            List<String> dates = new ArrayList<>();
            dates.add("2017-12-13");
            dates.add("2017-12-17");
            dates.add("2017-12-23");
            dates.add("2017-12-26");
            calendarView.setScheduleDate(dates);
        }
    }
}
