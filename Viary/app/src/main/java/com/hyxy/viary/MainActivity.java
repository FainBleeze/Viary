package com.hyxy.viary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {
    private ListView diaryListView;
    private int currentYear;//当前显示的年份
    private int currentMonth;//当前显示的月份
    private int currentDay;
    private List<DiaryItem> monthDiaryList=new ArrayList<>();
    Dbo db_helper;

    //request code
    private final int CHOOSE_ACT=0;
    private final int DIARY_ACT=1;
    private final int MONTH_ACT=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        //api高于23时，动态申请权限
        verifyStoragePermissions(MainActivity.this);

        //获取当前年、月以选择显示哪一月的diary
        Calendar calendar=Calendar.getInstance();
        currentYear=calendar.get(Calendar.YEAR);
        currentMonth=calendar.get(Calendar.MONTH)+1;//从0开始，要+1
        currentDay=calendar.get(Calendar.DAY_OF_MONTH);
        int maxDayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("year:"+currentYear+"\nmonth:"+currentMonth+"\nday:"+currentDay+"\nday of month:"+maxDayOfMonth);

        //打开数据库
        db_helper = new Dbo(MainActivity.this);
        //用下面这个读取数据库，返回到cursor
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String sql = "select * from "+params.DBTABLENAME+" where "
                +params.DBYEAR+"=? and "+params.DBMONTH+"=? and "+params.DBDAY+"=?;";
        //res.moveToFirst();
        DiaryItem item;
        monthDiaryList.clear();
        for(int dayIndex=1;dayIndex<=currentDay;dayIndex++) {
            //System.out.println("dayIndex:"+dayIndex);
            String[] sql_params={Integer.toString(currentYear),Integer.toString(currentMonth),Integer.toString(dayIndex)};
            Cursor res = db.rawQuery(sql, sql_params);
            if(res.getCount()==0){
                item=new DiaryItem(currentYear,currentMonth,dayIndex,null);
                monthDiaryList.add(item);
            }
            else{
                res.moveToFirst();
                String s=res.getString(res.getColumnIndex(params.DBCONTENT));
                //System.out.println("day "+dayIndex+"'s buf is:"+s);
                item=new DiaryItem(currentYear,currentMonth,dayIndex,s);
                monthDiaryList.add(item);
            }
            res.close();
        }
        db.close();

        DiaryAdapter adapter=new DiaryAdapter(MainActivity.this, monthDiaryList);
        adapter.notifyDataSetChanged();
        diaryListView=(ListView)findViewById(R.id.diary_listview);
        diaryListView.setAdapter(adapter);
        diaryListView.setOnItemClickListener(DiaryItemClickListener);
        diaryListView.setOnItemLongClickListener(DiaryItemLongClickListener);

        //设置日期选择按钮图片
        ImageView monthSelector=(ImageView)findViewById(R.id.month_selector);
        monthSelector.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,android.R.drawable.ic_menu_today));
        monthSelector.setOnClickListener(monthSelectorClickListener);

        //安卓21以上时设置状态栏颜色和皮肤风格统一，使界面更加美观
        params.windowColor(MainActivity.this);
    }

    private View.OnClickListener monthSelectorClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this,MonthPickActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt(params.YearKey,currentYear);
            bundle.putInt(params.MonthKey,currentMonth);
            bundle.putInt(params.DayKey,currentDay);
            intent.putExtras(bundle);
            startActivityForResult(intent,MONTH_ACT);
        }
    };

    //ListView监听
    private AdapterView.OnItemClickListener DiaryItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DiaryItem d=monthDiaryList.get(position);
            Intent intent;
            if(d.getDiaryContent()==null){
                intent=new Intent(MainActivity.this,ChooseActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt(params.YearKey,d.getYear());
                bundle.putInt(params.MonthKey,d.getMonth());
                bundle.putInt(params.DayKey,d.getDay());
                bundle.putBoolean("new", false);
                //！！这里传个标题就行，不要内容，显示的时候也显示标题即可
                //bundle.putString(params.TitleKey, d.getDiaryContent());
                //不过可以做测试用
                bundle.putString("content",d.getDiaryContent());
                intent.putExtras(bundle);
                startActivityForResult(intent,CHOOSE_ACT);
            }
            else{
                intent=new Intent(MainActivity.this,DiaryActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt(params.YearKey,d.getYear());
                bundle.putInt(params.MonthKey,d.getMonth());
                bundle.putInt(params.DayKey,d.getDay());
                bundle.putBoolean("new", false);
                //！！这里传个标题就行，不要内容，显示的时候也显示标题即可
                //bundle.putString(params.TitleKey, d.getDiaryContent());
                //不过可以做测试用
                bundle.putString("content",d.getDiaryContent());
                intent.putExtras(bundle);
                startActivityForResult(intent,DIARY_ACT);
            }
        }
    };

    private AdapterView.OnItemLongClickListener DiaryItemLongClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            DiaryItem item=monthDiaryList.get(position);
            final int year=item.getYear();
            final int month=item.getMonth();
            final int day=item.getDay();
            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("删除日记？");
            dialog.setMessage("删除"+year+"年"+month+"月"+day+"日的所有日记吗？");
            dialog.setCancelable(true);
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //打开数据库
                    db_helper = new Dbo(MainActivity.this);
                    //用下面这个读取数据库，返回到cursor
                    SQLiteDatabase db = db_helper.getReadableDatabase();
                    String sql = "delete from "+params.DBTABLENAME+" where "
                            +params.DBYEAR+"=? and "+params.DBMONTH+"=? and "+params.DBDAY+"=?;";
                    String[] sql_params={Integer.toString(year),Integer.toString(month),Integer.toString(day)};
                    db.execSQL(sql,sql_params);
                    db.close();
                    refresh(year,month);
                }
            });
            dialog.setNegativeButton("取消",null);
            dialog.show();
            return true;
        }
    };

    //接受日期选择
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode) {
            case CHOOSE_ACT:
            case DIARY_ACT:
            case MONTH_ACT:
                if (resultCode == 1) {
                    Bundle bundle = data.getExtras();
                    int selectYear = bundle.getInt(params.YearKey);
                    int selectMonth = bundle.getInt(params.MonthKey);
                    int selectDay = bundle.getInt(params.DayKey);
                    currentYear = selectYear;
                    currentMonth = selectMonth;
                    currentDay = selectDay;
                    System.out.println("selectYear:" + selectYear + "\nselectMonth:" + selectMonth + "\nselectDay:" + selectDay);
                    refresh(selectYear, selectMonth);
                }
                break;
            default:
                break;
        }
    }

    public void refresh(int year,int month){
        //打开数据库
        db_helper = new Dbo(MainActivity.this);
        //用下面这个读取数据库，返回到cursor
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String sql = "select * from "+params.DBTABLENAME+" where "
                +params.DBYEAR+"=? and "+params.DBMONTH+"=? and "+params.DBDAY+"=?;";
        DiaryItem item;
        monthDiaryList.clear();
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        int maxDayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int dayIndex=1;dayIndex<=maxDayOfMonth;dayIndex++) {
            //System.out.println("dayIndex:"+dayIndex);
            String[] sql_params={Integer.toString(year),Integer.toString(month),Integer.toString(dayIndex)};
            Cursor res = db.rawQuery(sql, sql_params);
            if(res.getCount()==0){
                item=new DiaryItem(currentYear,currentMonth,dayIndex,null);
                monthDiaryList.add(item);
            }
            else{
                res.moveToFirst();
                String s=res.getString(res.getColumnIndex(params.DBCONTENT));
                //System.out.println("day "+dayIndex+"'s buf is:"+s);
                item=new DiaryItem(currentYear,currentMonth,dayIndex,s);
                monthDiaryList.add(item);
            }
            res.close();
        }
        db.close();

        DiaryAdapter adapter=new DiaryAdapter(MainActivity.this, monthDiaryList);
        //adapter.notifyDataSetChanged();
        diaryListView=(ListView)findViewById(R.id.diary_listview);
        diaryListView.setAdapter(adapter);
        diaryListView.setOnItemClickListener(DiaryItemClickListener);

    }

    //读写权限列表
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    //申请读写权限
    public void verifyStoragePermissions(Activity activity){
        //低于6.0，无需动态申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检测是否有读的权限
            int permission = activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
            // 没有读的权限，去申请读写的权限，会弹出对话框
            if (permission != PackageManager.PERMISSION_GRANTED)
                activity.requestPermissions(PERMISSIONS_STORAGE, 1);
        }
    }

    //重写返回按钮，点击时返回桌面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return super.onKeyDown(keyCode, event);
    }
}
