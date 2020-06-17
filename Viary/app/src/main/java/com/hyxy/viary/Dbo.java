package com.hyxy.viary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Dbo extends SQLiteOpenHelper {

    public Dbo(Context context) {
        super(context, params.DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //重写onCreate()方法，创建数据表，其中date字段作为主键
        String sql = "create table "+params.DBTABLENAME+"("
                +params.DBDATE+" text primary key, "
                +params.DBTITLE+" text, "
                +params.DBTYPE+" integer, "
                +params.DBCONTENT+" text, "
                +params.DBYEAR+" integer, "
                +params.DBMONTH+" integer, "
                +params.DBDAY+" integer);";
        sqLiteDatabase.execSQL(sql);    //执行SQL语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        return;
    }
}
