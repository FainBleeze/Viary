package com.hyxy.viary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    private ListView diaryListView;
    private int currentYear;//当前显示的年份
    private int currentMonth;//当前显示的月份
    private int currentDay;
    private List<DiaryItem> monthDiaryList=new ArrayList<>();
    Dbo db_helper;

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
        int dayOfCurrentMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("year:"+currentYear+"\nmonth:"+currentMonth+"\nday:"+currentDay+"\nday of month:"+dayOfCurrentMonth);

        //打开数据库
        db_helper = new Dbo(MainActivity.this);
        //用下面这个读取数据库，返回到cursor
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String sql = "select * from "+params.DBTABLENAME+" where "
                +params.DBYEAR+"=? and "+params.DBMONTH+"=? and "+params.DBDAY+"=?;";
        //res.moveToFirst();
        DiaryItem item;
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
                System.out.println("day "+dayIndex+"'s buf is:"+s);
                item=new DiaryItem(currentYear,currentMonth,dayIndex,s);
                monthDiaryList.add(item);
            }
            res.close();
        }
        db.close();

        //测试用实例
        /*
        monthDiaryList.add(new DiaryItem(2020,6,16,"June sixteen"));
        monthDiaryList.add(new DiaryItem(2020,6,17,null));
        monthDiaryList.add(new DiaryItem(2020,6,18,"June eighteen"));
        monthDiaryList.add(new DiaryItem(2020,6,19,null));
        monthDiaryList.add(new DiaryItem(2020,6,20,null));
        monthDiaryList.add(new DiaryItem(2020,6,21,null));
        */

        DiaryAdapter adapter=new DiaryAdapter(MainActivity.this,monthDiaryList);
        adapter.notifyDataSetChanged();
        diaryListView=(ListView)findViewById(R.id.diary_listview);
        diaryListView.setAdapter(adapter);
        diaryListView.setOnItemClickListener(DiaryItemClickListener);

        //安卓21以上时设置状态栏颜色和皮肤风格统一，使界面更加美观
        params.windowColor(MainActivity.this);
    }

    private AdapterView.OnItemClickListener DiaryItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DiaryItem d=monthDiaryList.get(position);
            Intent intent;
            if(d.getDiaryContent()==null){
                intent=new Intent(MainActivity.this,ChooseActivity.class);
            }
            else{
                intent=new Intent(MainActivity.this,DiaryActivity.class);
            }
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
            startActivity(intent);
        }
    };

    /*
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (resultCode){
            case RESULT_OK:
                //TODO: refresh();
                System.out.println("MainActivity receive RESULT_OK");
                break;
            default:
                break;
        }
        //startActivity(new Intent(MainActivity.this, ChooseActivity.class));
    }

     */

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
