package com.hyxy.viary;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.zip.Inflater;

public class DiaryActivity extends Activity {
    RadioGroup bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_diary);
        bar = findViewById(R.id.bar);
        intRadioBtn();

    }
    private class barLis implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {

        }
    }
    private void intRadioBtn(){
        Bundle bdl = getIntent().getExtras();
        if(false){//留着用于读取数据库

        }
        else{
            switch(bdl.getInt(params.TextTypeKey)){
                case R.id.diary_entry:
                    addRadioBtn(params.TYPE_DIARY, "一篇日记");
            }
        }
    }
    private void addRadioBtn(int btn_type, String title){
        RadioButton btn =new RadioButton(DiaryActivity.this);
        if(btn_type == params.TYPE_ADD)
            btn.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,68));
        else
            btn.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,100));
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setGravity(Gravity.CENTER_VERTICAL);
        btn.setLines(1);
        Bitmap a=null;
        btn.setButtonDrawable(new BitmapDrawable(a));
        btn.setPadding(20,0,30,0);
        btn.setText(title);
        switch(btn_type){
            case params.TYPE_DIARY:
                setBg(btn, R.drawable.sel_diary, R.color.dark_diary, R.color.unchecked);
                break;
            case params.TYPE_BOOK:
                setBg(btn, R.drawable.sel_book, R.color.dark_book, R.color.unchecked);
                break;
            case params.TYPE_FILM:
                setBg(btn, R.drawable.sel_film, R.color.dark_film, R.color.unchecked);
                break;
            case params.TYPE_PIC:
                setBg(btn, R.drawable.sel_pic, R.color.dark_pic, R.color.unchecked);
                break;
            case params.TYPE_ADD:
                setBg(btn, R.drawable.sel_add, R.color.red, R.color.unchecked);
                break;
        }
        bar.addView(btn, 0);
    }
    private void setBg(View btn, int drawableId, int checkColorId, int uncheckColorId){
        btn.setBackground(getResources().getDrawable(drawableId));
        int[] colors = new int[]{getResources().getColor(checkColorId), getResources().getColor(uncheckColorId)};
        int[][] states = new int[][]{{android.R.attr.state_checked},{android.R.attr.state_checkable}};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setBackgroundTintList(new ColorStateList(states, colors));
        }
        btn.setId(View.generateViewId());
    }
}
