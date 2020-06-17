package com.hyxy.viary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.ConstraintHorizontalLayout;

public class ChooseActivity extends Activity {
    LinearLayout diary, film, book, pic;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choose);
        diary = findViewById(R.id.diary_entry);
        film=findViewById(R.id.film_entry);
        book=findViewById(R.id.book_entry);
        pic=findViewById(R.id.pic_entry);
        MyTouchLis lis = new MyTouchLis();
        diary.setOnTouchListener(lis);
        film.setOnTouchListener(lis);
        book.setOnTouchListener(lis);
        pic.setOnTouchListener(lis);
        //安卓21以上时设置状态栏颜色和皮肤风格统一，使界面更加美观
        params.windowColor(ChooseActivity.this);
    }
    private class MyTouchLis implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    darkColor(view.getId());
                    return true;
                case MotionEvent.ACTION_UP:
                    recoverColor(view.getId());
                    if(isInView(motionEvent, view)) {
                        Intent txt = new Intent(ChooseActivity.this, DiaryActivity.class);
                        //添加数据传送
                        Bundle bdl = getIntent().getExtras();
                        if(bdl==null)
                            bdl = new Bundle();
                        bdl.putInt(params.TextTypeKey, view.getId());
                        bdl.putBoolean("new", true);
                        txt.putExtras(bdl);
                        startActivity(txt);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    return true;
                default:
                    recoverColor(view.getId());

            }
            return false;
        }
    }
    private void darkColor(int id){
        switch (id){
            case R.id.diary_entry:
                diary.setBackgroundColor(getResources().getColor(R.color.dark_diary));
                return;
            case R.id.film_entry:
                film.setBackgroundColor(getResources().getColor(R.color.dark_film));
                return;
            case R.id.book_entry:
                book.setBackgroundColor(getResources().getColor(R.color.dark_book));
                return;
            case R.id.pic_entry:
                pic.setBackgroundColor(getResources().getColor(R.color.dark_pic));
                return;
        }
    }
    private void recoverColor(int id){
        switch (id){
            case R.id.diary_entry:
                diary.setBackgroundColor(getResources().getColor(R.color.color_diary));
                return;
            case R.id.film_entry:
                film.setBackgroundColor(getResources().getColor(R.color.color_film));
                return;
            case R.id.book_entry:
                book.setBackgroundColor(getResources().getColor(R.color.color_book));
                return;
            case R.id.pic_entry:
                pic.setBackgroundColor(getResources().getColor(R.color.color_pic));
                return;
        }
    }
    private boolean isInView(MotionEvent event, View v) {
        float touchX = event.getX();
        float touchY = event.getY();
        float maxX = v.getWidth();
        float maxY = v.getHeight();
        //Toast.makeText(ChooseActivity.this,"touchX"+touchX+"touchY"+touchY+"maxX"+maxX+"maxY"+maxY, Toast.LENGTH_LONG).show();
        return touchX>0 && touchX<maxX && touchY > 0 && touchY < maxY;
    }

    //重写按键监听方法，按 返回键 时直接返回主界面
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(ChooseActivity.this, MainActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

     */
}
