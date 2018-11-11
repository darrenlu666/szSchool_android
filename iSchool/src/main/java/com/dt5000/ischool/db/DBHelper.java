package com.dt5000.ischool.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dt5000.ischool.utils.MLog;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_info.db";
    // 周锋2015.9.15修改数据库版本，由1改为2
    // 周锋2016.5.17修改数据库版本，由2改为3
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder clzSql = new StringBuilder();
        StringBuilder msgSql = new StringBuilder();
        StringBuilder groupSql = new StringBuilder();
        // 群组消息表
        groupSql.append("CREATE TABLE group_message (");
        groupSql.append("				group_message_id INTEGER NOT NULL,");
        groupSql.append("				groupinfo_id TEXT,");
        groupSql.append("				group_name TEXT,");
        groupSql.append("				sender_id TEXT,");
        groupSql.append("				sender_name TEXT,");
        groupSql.append("				content TEXT,");
        groupSql.append("				send_date TEXT,");
        groupSql.append("				read_status NUMERIC,");
        groupSql.append("				pic_url TEXT,");
        groupSql.append("				owner TEXT,");
        groupSql.append("				sync_time TEXT, ");
        groupSql.append("				headpic TEXT, ");
        groupSql.append("				video_url TEXT, ");// 周锋2015.9.15修改，数据库版本2中增加字段headpic
        groupSql.append("				constraint pk_t1 PRIMARY KEY (group_message_id, owner) )");

        // 个人消息表
        msgSql.append("CREATE TABLE message (");
        msgSql.append("				message_id INTEGER NOT NULL,");
        msgSql.append("				content TEXT,");
        msgSql.append("				sender_id TEXT,");
        msgSql.append("				sender_name TEXT,");
        msgSql.append("				receiver_id TEXT,");
        msgSql.append("				receiver_name TEXT,");
        msgSql.append("				send_datetime TEXT,");
        msgSql.append("				read_status INTEGER,");
        msgSql.append("				image_url TEXT,");
        msgSql.append("				owner TEXT,");
        msgSql.append("				sync_time TEXT, ");
        msgSql.append("				video_url TEXT, ");
        msgSql.append("				profile_url TEXT, ");// 周锋2016.5.17修改，数据库版本3中增加字段profile_url
        msgSql.append("				constraint pk_t1 PRIMARY KEY (message_id, owner) )");

        // 班级消息表
        clzSql.append("CREATE TABLE class_message (");
        clzSql.append("				class_message_id INTEGER NOT NULL,");
        clzSql.append("				classinfo_id TEXT,");
        clzSql.append("				clazz_name TEXT,");
        clzSql.append("				sender_id TEXT,");
        clzSql.append("				sender_name TEXT,");
        clzSql.append("				content TEXT,");
        clzSql.append("				send_date TEXT,");
        clzSql.append("				read_status NUMERIC,");
        clzSql.append("				pic_url TEXT,");
        clzSql.append("				owner TEXT,");
        clzSql.append("				sync_time TEXT, ");
        clzSql.append("				headpic TEXT, ");
        clzSql.append("				video_url TEXT, ");// 周锋2015.9.15修改，数据库版本2中增加字段headpic
        clzSql.append("				constraint pk_t1 PRIMARY KEY (class_message_id, owner) )");



        db.execSQL(clzSql.toString());
        db.execSQL(msgSql.toString());
        db.execSQL(groupSql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MLog.i("SQLiteOpenHelper.onUpgrade, oldVersion:" + oldVersion
                + ", newVersion:" + newVersion);

        try {
            if (oldVersion == 1) {// 周锋2015.9.15修改，在数据库版本2中的class_message表中增加字段headpic
                MLog.i("数据库升级，由版本1升级为版本2");
                String addColumnHeadpic = "ALTER TABLE class_message ADD COLUMN headpic TEXT";
                db.execSQL(addColumnHeadpic);
            } else if (oldVersion == 2) {// 周锋2016.5.17修改，数据库版本3中的message表中增加字段profile_url
                MLog.i("数据库升级，由版本2升级为版本3");
                String addColumnHeadpic = "ALTER TABLE message ADD COLUMN profile_url TEXT";
                db.execSQL(addColumnHeadpic);
            } else if (oldVersion == 3) {
                MLog.i("数据库升级，由版本3升级为版本4");
                StringBuilder groupSql = new StringBuilder();
                // 群组消息表
                groupSql.append("CREATE TABLE group_message (");
                groupSql.append("				group_message_id INTEGER NOT NULL,");
                groupSql.append("				groupinfo_id TEXT,");
                groupSql.append("				group_name TEXT,");
                groupSql.append("				sender_id TEXT,");
                groupSql.append("				sender_name TEXT,");
                groupSql.append("				content TEXT,");
                groupSql.append("				send_date TEXT,");
                groupSql.append("				read_status NUMERIC,");
                groupSql.append("				pic_url TEXT,");
                groupSql.append("				owner TEXT,");
                groupSql.append("				sync_time TEXT, ");
                groupSql.append("				headpic TEXT, ");
                groupSql.append("				constraint pk_t1 PRIMARY KEY (group_message_id, owner) )");
                db.execSQL(groupSql.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}