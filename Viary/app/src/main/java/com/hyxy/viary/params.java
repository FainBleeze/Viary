package com.hyxy.viary;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class params {
    static String TextTypeKey = "type";
    static String YearKey = "year";
    static String MonthKey = "month";
    static String DayKey = "day";
    static String TitleKey = "title";
    static final int TYPE_DIARY = 1;
    static final int TYPE_FILM = 2;
    static final int TYPE_BOOK = 3;
    static final int TYPE_PIC = 4;
    static final int TYPE_ADD = 5;
    static String DATABASE = "DB_viary";
    static String DBTABLENAME = "DB_table";
    static String DBDATE = "DB_date";
    static String DBTITLE = "DB_title";
    static String DBTYPE = "DB_type";
    static String DBCONTENT = "DB_cnt";
    static String DBYEAR = "DB_year";
    static String DBMONTH = "DB_month";
    static String DBDAY = "DB_day";
    static void windowColor(Activity act){
        //安卓21以上时设置状态栏颜色和皮肤风格统一，使界面更加美观
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(act.getResources().getColor(R.color.beige));
        }
    }

}
