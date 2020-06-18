package com.hyxy.viary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonthPickActivity extends Activity {
    private CalendarView calendarView;
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_month_select);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        selectYear=bundle.getInt(params.YearKey);
        selectMonth=bundle.getInt(params.MonthKey);
        //System.out.println("receive month:"+selectMonth);
        selectDay=bundle.getInt(params.DayKey);
        calendarView=(CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectYear=year;
                selectMonth=month+1;
                selectDay=dayOfMonth;
            }
        });
        Button button=(Button)findViewById(R.id.month_select_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                Bundle bundle1=new Bundle();
                bundle1.putInt(params.YearKey,selectYear);
                bundle1.putInt(params.MonthKey,selectMonth);
                bundle1.putInt(params.DayKey,selectDay);
                intent1.putExtras(bundle1);
                setResult(1,intent1);
                finish();
            }
        });

        //设置最大可选时间为现在
        Calendar calendar=Calendar.getInstance();
        calendarView.setMaxDate(calendar.getTimeInMillis());
        //设置默认显示月份为主页面正在显示的月份
        calendar.set(Calendar.YEAR,selectYear);
        calendar.set(Calendar.MONTH,selectMonth-1);
        calendar.set(Calendar.DAY_OF_MONTH,selectDay);
        calendarView.setDate(calendar.getTimeInMillis());
    }
}
