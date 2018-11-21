package com.dt5000.ischool.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MsgLocalPathDBManager {

    private DBHelper helper;
    private SQLiteDatabase db;
    private final static byte[] _writeLock = new byte[0];

    public MsgLocalPathDBManager(Context context) {
        helper = new DBHelper(context);
        //db = helper.getWritableDatabase();
    }

    public void inputPath(int msgId, String path) {
        synchronized (_writeLock) {
            db = helper.getWritableDatabase();
            String sql = "INSERT INTO msg_video_path (class_message_id, video_local_path) "
                    + " VALUES('" + msgId + "','" + path + "')";
            db.execSQL(sql);
            db.close();
        }
    }

    public String getPath(int msgId) {
        synchronized (_writeLock) {
            db = helper.getWritableDatabase();
            String sql = "select * from msg_video_path where class_message_id = '" + msgId + "'";
            Cursor c = db.rawQuery(sql, null);
            String path = null;
            while (c.moveToNext()) {
                if (c.getColumnIndex("video_local_path") != -1)
                    path = c.getString(c.getColumnIndex("video_local_path"));
            }

            c.close();
            db.close();
            return path;
        }
    }
}
