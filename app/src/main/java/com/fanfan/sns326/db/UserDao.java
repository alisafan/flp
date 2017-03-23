package com.fanfan.sns326.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据访问层 Dao
 * Created by 钧 on 2016/6/17.
 */
public class UserDao {

    // 修改
    public void update(Context context, long time) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put("time", time);// 更新登录时间
        int row = db.update("person",
                values,
                "1=1",  // where 模板
                null);//-占位符的值
        Log.i("spl", "update row=" + row);
        db.close();// 关闭连接

    }

    // 删除
    public void delete(Context context) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL("delete from person");
        db.close();
    }

    // 增
    public void insert(Context context, String uname, String upass, long time) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        // 准备插入的数据
        ContentValues values = new ContentValues();// HashMap

        values.put("uname", uname);
        values.put("upass", upass);
        values.put("time", time);
        long id = db.insert("person", null, values);//"person"-表名, null-空值列, values
        Log.i("spl","insert id="+id);
        db.close();// 关闭连接
    }

    // 全查询
    public List<User> selectAll(Context context) {
        //
        List<User> list = new ArrayList<User>();
        // 实例化 辅助类
        DBHelper helper = new DBHelper(context);
        // 获取SQLite数据库对象
        SQLiteDatabase db = helper.getReadableDatabase();
        // 执行查询命令,接收游标返回
        Cursor cursor = db.rawQuery("select * from person",null);

        while(cursor.moveToNext()){//moveToNext:移动到下一行
            User user = new User();
            user.uname=cursor.getString(cursor.getColumnIndex("uname"));
            user.upass=cursor.getString(cursor.getColumnIndex("upass"));
            user.time=cursor.getLong(cursor.getColumnIndex("time"));
            list.add(user);
        }
        cursor.close();// 游标关闭
        db.close();// db关闭
        return list;
    }


}
