package com.hyxy.viary;

public class DiaryItem {
    private int year;//年份
    private int month;//月份
    private int day;//日期
    private String diaryContent;//日记内容

    public DiaryItem(int year, int month, int day, String content){
        this.year=year;
        this.month=month;
        this.day=day;
        this.diaryContent=content;
    }

    public int getYear(){
        return this.year;
    }

    public void setYear(int year){
        this.year = year;
    }

    public int getMonth(){
        return this.month;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public int getDay(){
        return this.day;
    }

    public void setDay(int day){
        this.day = day;
    }

    public String getDiaryContent(){
        return this.diaryContent;
    }

    public void setDiaryContent(String diaryContent){
        this.diaryContent = diaryContent;
    }

}
