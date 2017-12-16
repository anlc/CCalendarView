package com.n.ccalendarview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.n.ccalendarlib.CCalendarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.view_pager).setOnClickListener(this);
        findViewById(R.id.week).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_pager:
                startActivity(new Intent(this, ViewPagerActivity.class));
                break;
            case R.id.week:
                startActivity(new Intent(this, WeekActivity.class));
                break;
        }
    }
}
