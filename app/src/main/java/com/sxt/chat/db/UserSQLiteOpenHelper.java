package com.sxt.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SXT on 2018/3/22.
 */

public class UserSQLiteOpenHelper extends SQLiteOpenHelper {

    private String tableName;

    public UserSQLiteOpenHelper(Context context, String tableName) {
        super(context, tableName, null, 1);
        this.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + tableName + "(_id integer primary key autoincrement," +
                "id integer,userName varchar(64),name varchar(64),userPwd text,age integer,gender varchar(2),phone varchar(64)," +
                "height float,weight float," + "idCard varchar(64),ticket varchar(64),accountId integer,education varchar(64)," +
                "imgUri text)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
