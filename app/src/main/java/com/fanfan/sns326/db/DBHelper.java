package com.fanfan.sns326.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite辅助类
 */
public class DBHelper extends SQLiteOpenHelper{

    /**
     * 构造器
     * @param context
     */
    public DBHelper(Context context) {
        super(context, "dbperson", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建: 建表+插入数

        db.execSQL(
                "create table person(_id integer primary key autoincrement,uname varchar,upass varchar, time integer)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级
    }
}
