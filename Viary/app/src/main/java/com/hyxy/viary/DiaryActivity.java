package com.hyxy.viary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class DiaryActivity extends Activity {
    RadioGroup bar;
    Bundle bdl;
    ArrayList<TextAttr> cards;
    Dbo db_helper;
    TextAttr cur_attr;
    String new_file_time="";
    Boolean editing = false;
    Intent intent=getIntent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_diary);
        bar = findViewById(R.id.bar);
        cards = new ArrayList<>();
        bdl = getIntent().getExtras();
        db_helper = new Dbo(DiaryActivity.this);
        intRadioBtn();
        bar.setOnCheckedChangeListener(new barLis());
        findViewById(R.id.text).setOnTouchListener(new editLis());
        findViewById(R.id.date_title).setOnTouchListener(new editLis());
        //安卓21以上时设置状态栏颜色和皮肤风格统一，使界面更加美观
        params.windowColor(DiaryActivity.this);
    }

    //保存数据到数据库
    private boolean save(){
        //首先同步编辑框中的内容
        cur_attr.text = ((EditText)findViewById(R.id.text)).getText().toString();
        //没有任何修改时不记录
        if(cur_attr.title=="一篇日记"||cur_attr.title=="一篇观后感"||cur_attr.title=="一篇读后感"||cur_attr.title=="一张图片"){
            if(cur_attr.text.equals(new_file_time)){
                return false;
            }
        }
        SQLiteDatabase db = db_helper.getWritableDatabase();
        if(cur_attr.alreadyExist){
            String sql = "UPDATE "+params.DBTABLENAME+" SET "+params.DBTITLE+" = '"+cur_attr.title+"', "+params.DBCONTENT+" = '"+cur_attr.text+
                    "' WHERE "+params.DBDATE+" = '"+cur_attr.stamp+"';";
            db.execSQL(sql);
        }
        else{
            String sql = "INSERT INTO "+params.DBTABLENAME+" "
                    + "("+params.DBDATE+", "+ params.DBTITLE+", "+params.DBTYPE+", "+params.DBCONTENT+", "
                    +params.DBYEAR+", "+ params.DBMONTH+", "+params.DBDAY+") "
                    + "VALUES ('"+cur_attr.stamp+"', '"+cur_attr.title+"', "+cur_attr.type+", '"+cur_attr.text+"', "
                    +bdl.getInt(params.YearKey)+", "+bdl.getInt(params.MonthKey)+", "+bdl.getInt(params.DayKey)+")";
            db.execSQL(sql);
            cur_attr.alreadyExist=true;
        }
        db.close();
        return true;
    }

    //初始化所有需要显示的卡片
    private void intRadioBtn(){
        Calendar cal = Calendar.getInstance();
        new_file_time = String.format("%2d:%2d",cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)).replace(" ","0");
        new_file_time +="  ";

        //读取数据库中的内容
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String sql = "select * from "+params.DBTABLENAME+" where "
                +params.DBYEAR+"=? and "+params.DBMONTH+"=? and "+params.DBDAY+"=?;";
        String[] sql_params={String.valueOf(bdl.getInt(params.YearKey)),
                String.valueOf(bdl.getInt(params.MonthKey)),
                String.valueOf(bdl.getInt(params.DayKey))};
        Cursor res = db.rawQuery(sql, sql_params);
        //如果有以前的日记，循环创建卡片
        if(res.getCount()>0){
            res.moveToFirst();
            cur_attr = new TextAttr(res.getInt(res.getColumnIndex(params.DBTYPE)),
                    res.getString(res.getColumnIndex(params.DBTITLE)),
                    res.getString(res.getColumnIndex(params.DBCONTENT)),
                    res.getString(res.getColumnIndex(params.DBDATE)));
            addRadioBtn(cur_attr);
            for(res.moveToNext();!res.isAfterLast();res.moveToNext()){
                addRadioBtn(new TextAttr(res.getInt(res.getColumnIndex(params.DBTYPE)),
                        res.getString(res.getColumnIndex(params.DBTITLE)),
                        res.getString(res.getColumnIndex(params.DBCONTENT)),
                        res.getString(res.getColumnIndex(params.DBDATE))));
            }
        }
        //关闭数据库
        db.close();
        //判断是否新建文件
        Boolean new_file = bdl.getBoolean("new");
        if(new_file){
            String timeStamp = String.valueOf(cal.getTimeInMillis());
            bdl.remove("new");
            switch(bdl.getInt(params.TextTypeKey)){
                case R.id.diary_entry:
                    cur_attr = new TextAttr(params.TYPE_DIARY, "一篇日记", new_file_time, timeStamp);
                    cur_attr.alreadyExist = false;
                    addRadioBtn(cur_attr);
                    break;
                case R.id.film_entry:
                    cur_attr = new TextAttr(params.TYPE_FILM, "一篇观后感", new_file_time, timeStamp);
                    cur_attr.alreadyExist = false;
                    addRadioBtn(cur_attr);
                    break;
                case R.id.book_entry:
                    cur_attr = new TextAttr(params.TYPE_BOOK, "一篇读后感", new_file_time, timeStamp);
                    cur_attr.alreadyExist = false;
                    addRadioBtn(cur_attr);
                    break;
                case R.id.pic_entry:
                    cur_attr = new TextAttr(params.TYPE_PIC, "一张图片", new_file_time, timeStamp);
                    cur_attr.alreadyExist = false;
                    addRadioBtn(cur_attr);
                    break;
            }
        }
        //添加添加按钮
        assert cur_attr != null;
        addRadioBtn(new TextAttr(params.TYPE_ADD, getResources().getString(R.string.text_add), new_file_time, ""));
        //设置页面特色
        changeCard();
    }

    //切换卡片颜色和文本
    private void changeCard(){
        String year = String.valueOf(bdl.getInt(params.YearKey));
        String month = String.valueOf(bdl.getInt(params.MonthKey));
        String day = String.valueOf(bdl.getInt(params.DayKey));
        findViewById(R.id.card).setBackgroundColor(getResources().getColor(cur_attr.colorId));
        String title = "null";
        switch (cur_attr.type){
            case params.TYPE_DIARY:
                title = "“"+cur_attr.title+"”";
                break;
            case params.TYPE_FILM:
                title = "『"+cur_attr.title+"』";
                break;
            case params.TYPE_BOOK:
                title = "《"+cur_attr.title+"》";
                break;
            case params.TYPE_PIC:
                title = "【"+cur_attr.title+"】";
                adjustPicLayout();
                break;
        }
        String date_title = ""+ year +"/"+ month + "/" + day + "  " + title;
        ((TextView)findViewById(R.id.date_title)).setText(date_title);
        EditText txt = findViewById(R.id.text);
        txt.setText(cur_attr.text);
        //一开始不打开软键盘
        txt.setFocusable(false);
        txt.setFocusableInTouchMode(false);
    }

    //图片卡片需要显示ImageView
    private void adjustPicLayout() {
        ImageView image = findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.default_pic));
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //添加一个RadioButton
    private void addRadioBtn(TextAttr attr){
        RadioButton btn =new RadioButton(DiaryActivity.this);
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setGravity(Gravity.CENTER_VERTICAL);
        btn.setLines(1);
        Bitmap a=null;
        btn.setButtonDrawable(new BitmapDrawable(a));
        if(attr.type == params.TYPE_ADD) {
            btn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 68));
            btn.setBackground(getResources().getDrawable(R.drawable.bar_diary));
        }
        else {
            btn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btn.setBackground(getResources().getDrawable(R.drawable.bar_diary));
        }
        switch(attr.type){
            case params.TYPE_DIARY:
                attr.colorId =  R.color.sel_diary;
                break;
            case params.TYPE_BOOK:
                attr.colorId =  R.color.sel_book;
                break;
            case params.TYPE_FILM:
                attr.colorId =  R.color.sel_film;
                break;
            case params.TYPE_PIC:
                attr.colorId =  R.color.sel_pic;
                break;
            case params.TYPE_ADD:
                attr.colorId = R.color.sel_add;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setBackgroundTintList(getResources().getColorStateList(attr.colorId));
        }
        btn.setText(attr.title);
        btn.setPadding(20,0,30,0);
        int id = View.generateViewId();
        attr.RadioBtnId = id;
        btn.setId(id);
        bar.addView(btn, cards.size());
        if(attr.stamp.equals(cur_attr.stamp))
            btn.setChecked(true);
        cards.add(attr);
    }

    //RadioButton的监听器，用于切换卡片
    private class barLis implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //保存数据
            save();
            int id=radioGroup.getCheckedRadioButtonId();
            System.out.println(i);
            //点击添加，返回到choose界面选择添加的类型
            if(id==cards.get(cards.size()-1).RadioBtnId){
                System.out.println("aaa");
                Intent choose = new Intent(DiaryActivity.this, ChooseActivity.class);
                //添加数据传送
                if(bdl!=null)
                    choose.putExtras(bdl);
                startActivity(choose);
                System.out.println("bbbb");
                return;
            }
            //其他情况，找到被点击的卡片参数，改变card的背景颜色
            TextAttr text = null;
            for(int k=0; k<cards.size(); k++){
                if(cards.get(k).RadioBtnId==id){
                    text = cards.get(k);
                    break;
                }
            }
            assert text != null;
            //更新当前的卡片参数
            cur_attr = text;
            //下面更新时间、标题
            changeCard();
        }
    }

    //监听EditText的触摸事件使其可以输入
    private class editLis implements EditText.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //Toast.makeText(DiaryActivity.this, "get"+motionEvent.getAction(),Toast.LENGTH_LONG).show();
            switch (view.getId()){
                case R.id.text://点击时切换为可编辑
                    if(!editing) {
                        view.setFocusableInTouchMode(true);
                        view.setFocusable(true);
                        view.requestFocus();
                        editing = true;
                    }
                    return false;
                case R.id.date_title:
                    //弹出输入标题的对话框
                    final EditText inputServer = new EditText(DiaryActivity.this);
                    inputServer.setText(cur_attr.title);
                    AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivity.this);
                    builder.setTitle("请输入标题：").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                            .setNegativeButton("取消", null).setPositiveButton("修改", null);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cur_attr.title = inputServer.getText().toString();
                            cur_attr.text = ((EditText)findViewById(R.id.text)).getText().toString();
                            changeCard();
                            dialog.dismiss();
                            return;
                        }
                    });
                    return false;
            }
            return true;
        }
    }

    //重写按键监听方法，按 返回键 时直接返回主界面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            save();
            //finish();
            //DiaryActivity.this.setResult(RESULT_OK,intent);
            startActivity(new Intent(DiaryActivity.this, MainActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

}
