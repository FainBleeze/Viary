package com.hyxy.viary;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView diaryListView;
    private List<DiaryItem> monthDiaryList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        monthDiaryList.add(new DiaryItem(2020,6,16,"June sixteen"));
        monthDiaryList.add(new DiaryItem(2020,6,17,null));
        monthDiaryList.add(new DiaryItem(2020,6,18,"June eighteen"));
        monthDiaryList.add(new DiaryItem(2020,6,19,null));
        monthDiaryList.add(new DiaryItem(2020,6,20,null));
        monthDiaryList.add(new DiaryItem(2020,6,21,"June twenty-one"));
        DiaryAdapter adapter=new DiaryAdapter(MainActivity.this,monthDiaryList);
        diaryListView=(ListView)findViewById(R.id.diary_listview);
        diaryListView.setAdapter(adapter);
    }
}
