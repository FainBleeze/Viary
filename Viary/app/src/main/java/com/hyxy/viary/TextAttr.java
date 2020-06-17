package com.hyxy.viary;

public class TextAttr {
    public String title;
    public int type;
    public int colorId;
    public int RadioBtnId;
    public String text;
    public String stamp;
    public Boolean alreadyExist = true;
    public TextAttr(int type, String title, String text, String timeStamp){
        this.type = type;
        this.title = title;
        this.text = text;
        this.stamp = timeStamp;
    }
}
