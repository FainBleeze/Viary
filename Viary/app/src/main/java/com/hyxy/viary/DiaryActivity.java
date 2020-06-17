package com.hyxy.viary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class DiaryActivity extends Activity {
    RadioGroup bar;
    ArrayList<TextAttr> cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_diary);
        bar = findViewById(R.id.bar);
        cards = new ArrayList<>();
        intRadioBtn();
        bar.setOnCheckedChangeListener(new barLis());
    }
    private class barLis implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int id=radioGroup.getCheckedRadioButtonId();
            if(id==cards.get(cards.size()-1).RadioBtnId){
                Intent choose = new Intent(DiaryActivity.this, ChooseActivity.class);
                //添加数据传送
                Bundle bdl = getIntent().getExtras();
                if(bdl!=null)
                    choose.putExtras(bdl);
                startActivity(choose);
                return;
            }
            TextAttr text = null;
            for(int k=0; k<cards.size(); k++){
                if(cards.get(k).RadioBtnId==id){
                    text = cards.get(k);
                    break;
                }
            }
            assert text != null;
            findViewById(R.id.card).setBackgroundColor(getResources().getColor(text.colorId));
            //下面更新时间、标题
        }
    }
    private void intRadioBtn(){
        Bundle bdl = getIntent().getExtras();
        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.HOUR) +":"+cal.get(Calendar.MINUTE);
        if(true){//留着用于读取数据库
            addRadioBtn(new TextAttr(params.TYPE_DIARY, "一篇日记", time));
            addRadioBtn(new TextAttr(params.TYPE_FILM, "一篇读后感", time));
            addRadioBtn(new TextAttr(params.TYPE_BOOK, "一篇观后感", time));
            addRadioBtn(new TextAttr(params.TYPE_PIC, "一张图片", time));
            addRadioBtn(new TextAttr(params.TYPE_ADD, getResources().getString(R.string.text_add), time));
        }
        else{
            switch(bdl.getInt(params.TextTypeKey)){
                case R.id.diary_entry:
                    addRadioBtn(new TextAttr(params.TYPE_DIARY, "一篇日记", time));
                    break;
                case R.id.film_entry:
                    addRadioBtn(new TextAttr(params.TYPE_FILM, "一篇观后感", time));
                    break;
                case R.id.book_entry:
                    addRadioBtn(new TextAttr(params.TYPE_BOOK, "一篇读后感", time));
                    break;
                case R.id.pic_entry:
                    addRadioBtn(new TextAttr(params.TYPE_PIC, "一张图片", time));
                    break;
            }
            addRadioBtn(new TextAttr(params.TYPE_ADD, getResources().getString(R.string.text_add), time));
            findViewById(R.id.card).setBackgroundColor(getResources().getColor(cards.get(0).colorId));
        }
    }
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
        if(cards.size()==0)
            btn.setChecked(true);
        bar.addView(btn, cards.size());
        cards.add(attr);
    }
}
