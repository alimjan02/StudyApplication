package com.sxt.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SXT on 2018/3/22.
 */

public class SQLiteUserDao {
    private static SQLiteUserDao sqLiteUserDao = new SQLiteUserDao();
    private static UserSQLiteOpenHelper dbHelper;
    private static Context context;

    /**
     * String sql = "create table " + tableName + "(_id integer primary key autoincrement," +
     * "id integer,userName varchar(64),age integer,gender varchar(2),phone varchar(64)," +
     * "height float,weight float," + "idCard varchar(64),ticket varchar(64),accountId integer)";
     */

    public static SQLiteUserDao getInstance(Context context) {
        SQLiteUserDao.context = context;
        if (dbHelper == null) {
            dbHelper = new UserSQLiteOpenHelper(context, "user");
        }
        return sqLiteUserDao;
    }

    public boolean addUser(User user) {
        if (user != null) {
            List<User> users = queryUserByUserName(user.getUsername());
            if (users != null && users.size() > 0) {
                updateUserByUserName(user.getUsername(), user);
                return false;//已经有记录了, 无须重复添加用户
            }
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", user.getId());
            values.put("name", user.getName());
            values.put("userName", user.getUserName());
            values.put("userPwd", user.getUserPwd());
            values.put("age", user.getAge());
            values.put("gender", user.getGender());
            values.put("phone", user.getPhone());
            values.put("height", user.getHeight());
            values.put("weight", user.getWeight());
            values.put("idCard", user.getIdCard());
            values.put("ticket", user.getTicket());
            values.put("accountId", user.getAccountId());
            values.put("education", user.getEducation());
            values.put("imgUri", user.getImgUri());
            long insert = db.insert("user", null, values);
            db.close();
            if (insert != -1) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("user", "id=?", new String[]{String.valueOf(userId)});
        db.close();
        if (result != -1) {
            return true;
        }
        return false;
    }

    public boolean deleteUser(String userName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("user", "userName=?", new String[]{userName});
        db.close();
        if (result != -1) {
            return true;
        }
        return false;
    }

    public boolean deleteUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("user", null, null);
        db.close();
        if (result != -1) {
            return true;
        }
        return false;
    }

    public boolean updateUserTicket(int userId, String ticket, int accountId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ticket", ticket);
        values.put("accountId", accountId);
        int result = db.update("user", values, "id=?", new String[]{String.valueOf(userId)});
        db.close();
        if (result != 0) {
            return true;
        }
        return false;
    }

    public boolean updateUserDetailById(int userId, User user) {

        ContentValues values = new ContentValues();
        values.put("age", user.getAge());
        values.put("gender", user.getGender());
        values.put("name", user.getName());
        values.put("phone", user.getPhone());
        values.put("height", user.getHeight());
        values.put("weight", user.getWeight());
        values.put("idCard", user.getIdCard());
        values.put("education", user.getEducation());
        values.put("imgUri", user.getImgUri());
//        values.put("ticket", user.getTicket());
//        values.put("accountId", user.getAccountId());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.update("user", values, "id=?", new String[]{String.valueOf(userId)});
        db.close();
        if (result != 0) {
            return true;
        }

        return false;
    }

    public boolean updateUserById(int userId, User user) {
        ContentValues values = new ContentValues();
        List<User> userList = queryUserById(userId);
        values.put("age", user.getAge());
        values.put("gender", user.getGender());
        values.put("name", user.getName());
        values.put("phone", user.getPhone());
        values.put("height", user.getHeight());
        values.put("weight", user.getWeight());
        values.put("idCard", user.getIdCard());
        values.put("ticket", user.getTicket());
        values.put("accountId", user.getAccountId());
        values.put("education", user.getEducation());
        values.put("imgUri", user.getImgUri());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.update("user", values, "id=?", new String[]{String.valueOf(userId)});
        db.close();
        if (result != 0) {
            return true;
        }

        return false;
    }

    public boolean updateUserByUserName(String userName, User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("userName", user.getUserName());
        values.put("age", user.getAge());
        values.put("gender", user.getGender());
        values.put("phone", user.getPhone());
        values.put("height", user.getHeight());
        values.put("weight", user.getWeight());
        values.put("idCard", user.getIdCard());
        values.put("ticket", user.getTicket());
        values.put("accountId", user.getAccountId());
        values.put("education", user.getEducation());
        values.put("imgUri", user.getImgUri());
        int result = db.update("user", values, "userName=?", new String[]{userName});
        db.close();
        if (result != 0) {
            return true;
        }
        return false;
    }

    public List<User> queryUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        User user;
        List<User> userList = null;
        while (cursor.moveToNext()) {
            user = new User();
            if (userList == null) userList = new ArrayList<>();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
            user.setUserPwd(cursor.getString(cursor.getColumnIndex("userPwd")));
            user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
            user.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
            user.setIdCard(cursor.getString(cursor.getColumnIndex("idCard")));
            user.setTicket(cursor.getString(cursor.getColumnIndex("ticket")));
            user.setAccountId(cursor.getInt(cursor.getColumnIndex("accountId")));
            user.setEducation(cursor.getString(cursor.getColumnIndex("education")));
            user.setImgUri(cursor.getString(cursor.getColumnIndex("imgUri")));
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }

    public List<User> queryUserByUserName(String userName) {
        if (userName == null) return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, "userName=?", new String[]{userName}, null, null, null);
        User user;
        List<User> userList = null;
        while (cursor.moveToNext()) {
            user = new User();
            if (userList == null) userList = new ArrayList<>();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
            user.setUserPwd(cursor.getString(cursor.getColumnIndex("userPwd")));
            user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
            user.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
            user.setIdCard(cursor.getString(cursor.getColumnIndex("idCard")));
            user.setTicket(cursor.getString(cursor.getColumnIndex("ticket")));
            user.setAccountId(cursor.getInt(cursor.getColumnIndex("accountId")));
            user.setEducation(cursor.getString(cursor.getColumnIndex("education")));
            user.setImgUri(cursor.getString(cursor.getColumnIndex("imgUri")));
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }

    public List<User> queryUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        User user;
        List<User> userList = null;
        while (cursor.moveToNext()) {
            user = new User();
            if (userList == null) userList = new ArrayList<>();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
            user.setUserPwd(cursor.getString(cursor.getColumnIndex("userPwd")));
            user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
            user.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
            user.setIdCard(cursor.getString(cursor.getColumnIndex("idCard")));
            user.setTicket(cursor.getString(cursor.getColumnIndex("ticket")));
            user.setAccountId(cursor.getInt(cursor.getColumnIndex("accountId")));
            user.setEducation(cursor.getString(cursor.getColumnIndex("education")));
            user.setImgUri(cursor.getString(cursor.getColumnIndex("imgUri")));
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }
}
