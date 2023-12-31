package com.example.note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaseHelper extends SQLiteOpenHelper{     //对数据库进行创建
    public static  final String CREATE_NOTE = "create table Note("
            + "id integer primary key autoincrement,"
            + "title text not null,"
            + "author text,"
            + "content text,"
            + "date datetime not null default current_time,"
            + "picture text)";
    private Context mContext;//获取上下文
    public MyDataBaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }
    @Override
    public void  onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_NOTE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}