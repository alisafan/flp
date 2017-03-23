package com.fanfan.sns326.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 业务dao
 */
public class SearchDao {

    public SearchDao(){}

    /**
     * 构造器
     * @param context
     */
    public SearchDao(Context context){
        helper = new RecordSQLiteOpenHelper(context);
    }

    private SQLiteDatabase db;
    private RecordSQLiteOpenHelper helper;

    /**
     * 清空数据
     */
    public void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    public Cursor queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);

        return cursor;
    }

    /**
     * 插入数据
     */
    public void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    public boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?"
                , new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }
}
