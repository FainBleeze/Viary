package com.hyxy.viary;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DiaryAdapter extends BaseAdapter {
    //type表示有无diary内容，以使用不同的viewholder
    private final int TYPE_A=0;
    private final int TYPE_B=1;
    private int NUM_A = 0;
    private int NUM_B = 0;

    private Context context;
    private List<DiaryItem> data;
    public DiaryAdapter(Context context, List<DiaryItem> data){
        this.context=context;
        this.data=data;
    }

    public int getItemViewType(int position){
        int result;
        if(data.get(position).getDiaryContent()!=null && data.get(position).getDiaryContent()!=""){
            result=TYPE_A;
        }
        else{
            result=TYPE_B;
        }
        return result;
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data==null?null:data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderDiary holderDiary=null;
        ViewHolderDot holderDot=null;
        int type=getItemViewType(position);
        //System.out.println("type:"+type);

        //if view未被实例化，new viewholder
//        if(convertView==null){
        switch(type){
            case TYPE_A:
                if(convertView==null||convertView.getTag(R.id.tag_first)==null) {
                    holderDiary = new ViewHolderDiary();
                    convertView = View.inflate(context, R.layout.list_item, null);
                    holderDiary.dayInWeek = (TextView) convertView.findViewById(R.id.day_in_week);
                    holderDiary.dayInMonth = (TextView) convertView.findViewById(R.id.day_in_month);
                    holderDiary.diaryContent = (TextView) convertView.findViewById(R.id.diary_content);
                    convertView.setTag(R.id.tag_first, holderDiary);
                }
                else{
                    holderDiary=(ViewHolderDiary)convertView.getTag(R.id.tag_first);
                }
                break;
            case TYPE_B:
                if(convertView==null||convertView.getTag(R.id.tag_second)==null) {
                    holderDot = new ViewHolderDot();
                    convertView = View.inflate(context, R.layout.list_item_dot, null);
                    holderDot.dot = convertView.findViewById(R.id.dot);
                    convertView.setTag(R.id.tag_second, holderDot);
                }else {
                    holderDot=(ViewHolderDot)convertView.getTag(R.id.tag_second);
                }
                break;
        }
//        }
        //else view已初始化，根据tag取出viewholder
//        else{
//            switch(type){
//                case TYPE_A:
//                    holderDiary=(ViewHolderDiary)convertView.getTag(R.id.tag_first);
//                    break;
//                case TYPE_B:
//                    holderDot=(ViewHolderDot)convertView.getTag(R.id.tag_second);
//                    break;
//            }
//        }

        DiaryItem d=data.get(position);

        //计算周几
        String month = String.format("%2d",d.getMonth()).replace(" ","0");
        String day= String.format("%2d",d.getDay()).replace(" ","0");
        String week=getWeek(Integer.toString(d.getYear())+month+day);
//        if(10>d.getMonth()){
//            month="0"+Integer.toString(d.getMonth());
//        }
//        else{
//            month=Integer.toString(d.getMonth());
//        }
//        if(10>d.getDay()){
//            day="0"+Integer.toString(d.getDay());
//        }
//        else{
//            day=Integer.toString(d.getDay());
//        }
        //System.out.println(week);

        switch(type){
            case TYPE_A:
                holderDiary.dayInWeek.setText(week);
                if(week.equals("Sun")){
                    holderDiary.dayInWeek.setTextColor(ContextCompat.getColor(context,R.color.red));
                }
                holderDiary.dayInMonth.setText(Integer.toString(d.getDay()));
                holderDiary.diaryContent.setText(d.getDiaryContent());
                break;
            case TYPE_B:
                if(week.equals("Sun")){
                    holderDot.dot.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_red));
                }
                else{
                    holderDot.dot.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_grey));
                }
                break;
        }
        return convertView;
    }

    public static class ViewHolderDiary{
        TextView dayInWeek;
        TextView dayInMonth;
        TextView diaryContent;
    }

    public static class ViewHolderDot{
        View dot;
    }

    private String getWeek(String pTime) {
        String Week = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "Sun";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "Mon";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "Tue";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "Wed";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "Thu";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "Fri";
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "Sat";
        }

        return Week;
    }
}
