package com.dt5000.ischool.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级消息数据库操作类
 *
 * @author 周锋
 * @date 2016年1月19日 下午1:28:45
 * @ClassInfo com.dt5000.ischool.db.ClassMessageDBManager
 * @Description
 */
public class ClassMessageDBManager {

    private DBHelper helper;
    private SQLiteDatabase db;
    private final static byte[] _writeLock = new byte[0];

    public ClassMessageDBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 将服务器获取的班级消息列表插入本地数据库
     *
     * @param data
     */
    public void addClassMsgList(List<ClassMessage> data) {
        synchronized (_writeLock) {
            try {
                db.beginTransaction();

                String sql = "INSERT INTO class_message (class_message_id, classinfo_id, clazz_name, sender_id, "
                        + "sender_name, content, send_date, read_status, pic_url, owner, sync_time, headpic) "
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,datetime('now','localtime'),?)";
                Object[] params = null;
                for (ClassMessage m : data) {
                    if (m.getOwner().equals(m.getSenderID())) {
                        // 如果是本人发给别人的消息，则设置为已读
                        m.setReadStatus(1);
                    }
                    params = new Object[]{m.getClassMessageID(),
                            m.getClassinfoID(), m.getClazzName(),
                            m.getSenderID(), m.getSenderName(), m.getContent(),
                            TimeUtil.fullTimeFormat(m.getSendDate()),
                            m.getReadStatus(), m.getPicUrl(), m.getOwner(),
                            m.getStuPhoto()};
                    db.execSQL(sql, params);
                }

                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 将一条班级消息插入本地数据库
     *
     * @param data
     */
    public void addClassMsgSingle(ClassMessage data) {
        synchronized (_writeLock) {
            db.beginTransaction();

            try {
                db.execSQL(
                        "INSERT INTO class_message(class_message_id, classinfo_id, clazz_name, sender_id, sender_name,"
                                + "	content, send_date, read_status, pic_url, owner, sync_date, headpic) "
                                + " VALUES(?,?,?,?,?,?,?,?,?,?,datetime('now','localtime'),?)",
                        new Object[]{data.getClassMessageID(),
                                data.getClassinfoID(), data.getClazzName(),
                                data.getSenderID(), data.getSenderName(),
                                data.getContent(),
                                TimeUtil.fullTimeFormat(data.getSendDate()),
                                data.getReadStatus(), data.getPicUrl(),
                                data.getOwner(), data.getStuPhoto()});

                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 查询并返回所有班级消息的列表数据
     *
     * @return List<Person>
     */
    public List<ClassMessage> queryAll() {
        synchronized (_writeLock) {
            ArrayList<ClassMessage> list = new ArrayList<ClassMessage>();
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                ClassMessage m = C2M(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * 查询time之后的数据 (新的)
     *
     * @return List<Person>
     */
    public List<ClassMessage> queryAfter(Integer clazzMsgId, String owner,
                                         String classinfoId) {
        synchronized (_writeLock) {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT t.* FROM class_message t ");
            sql.append(" WHERE t.owner=? AND t.classinfo_id=? ");

            String[] params = null;
            if (null != clazzMsgId) {
                sql.append(" AND t.class_message_id > ? ");
                params = new String[]{owner, classinfoId,
                        String.valueOf(clazzMsgId)};
            } else {
                params = new String[]{owner, classinfoId};
            }
            sql.append(" ORDER BY t.class_message_id ");

            Cursor c = db.rawQuery(sql.toString(), params);
            ArrayList<ClassMessage> list = new ArrayList<ClassMessage>();

            while (c.moveToNext()) {
                ClassMessage m = C2M(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * 查询最新的n条记录
     *
     * @return List<Person>
     */
    public List<ClassMessage> queryTop(int n, String owner, String classinfoId) {
        synchronized (_writeLock) {
            String sql = "SELECT * FROM (SELECT t.* FROM class_message t WHERE t.owner=? "
                    + " AND t.classinfo_id=? ORDER BY t.send_date DESC LIMIT "
                    + String.valueOf(n) + ") t1 ORDER BY t1.send_date ASC";

            Cursor c = db.rawQuery(sql, new String[]{owner, classinfoId});
            ArrayList<ClassMessage> list = new ArrayList<ClassMessage>();

            while (c.moveToNext()) {
                ClassMessage m = C2M(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * 查询time之前的数据 （旧的）
     *
     * @param classInfoId
     * @return List<Person>
     */
    public List<ClassMessage> queryBefore(Integer clazzMsgId, String owner,
                                          String classInfoId, int pageSize) {

        synchronized (_writeLock) {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from (SELECT t.* FROM class_message t ");
            sql.append(" WHERE t.owner=? AND t.classinfo_id=? ");
            String[] params = null;
            if (null != clazzMsgId) {
                sql.append(" AND t.class_message_id < ? ");
                params = new String[]{owner, classInfoId, String.valueOf(clazzMsgId)};
            } else {
                params = new String[]{owner, classInfoId};
            }
            sql.append(" ORDER BY t.send_date desc LIMIT ");
            sql.append(String.valueOf(pageSize));
            sql.append(") t1 order by t1.send_date asc");
            Cursor c = db.rawQuery(sql.toString(), params);
            ArrayList<ClassMessage> list = new ArrayList<ClassMessage>();
            while (c.moveToNext()) {
                ClassMessage m = C2M(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db
                .rawQuery(
                        "SELECT * FROM class_message t ORDER BY t.send_date DESC",
                        null);
        return c;
    }

    public void closeDB() {
        db.close();
    }

    /***
     * 游标转成对象
     */
    private ClassMessage C2M(Cursor c) {
        ClassMessage m = new ClassMessage();
        m.setClassMessageID((c.getInt(c.getColumnIndex("class_message_id"))));
        m.setSendDate(TimeUtil.parseFullTime((c.getString(c
                .getColumnIndex("send_date")))));
        m.setClazzName(c.getString(c.getColumnIndex("clazz_name")));
        m.setClassinfoID(c.getString(c.getColumnIndex("classinfo_id")));
        m.setSenderName(c.getString(c.getColumnIndex("sender_name")));
        m.setSenderID(c.getString(c.getColumnIndex("sender_id")));
        m.setPicUrl(c.getString(c.getColumnIndex("pic_url")));
        m.setContent(c.getString(c.getColumnIndex("content")));
        m.setOwner(c.getString(c.getColumnIndex("owner")));
        m.setReadStatus(c.getInt(c.getColumnIndex("read_status")));
        m.setStuPhoto(c.getString(c.getColumnIndex("headpic")));
        return m;
    }

    public void updateStatus(String userId) {
        synchronized (_writeLock) {
            db.execSQL(
                    "UPDATE class_message SET read_status=1 WHERE owner=? AND read_status=0",
                    new String[]{userId});
        }
    }

    /**
     * 更新消息状态，设置某个班级对话的所有消息为已读状态，原{@link #updateStatus}
     * 会导致所有班级会话都更改状态，更改时间2014.9.9
     *
     * @param userId
     * @param classId
     */
    public void updateMsgStatus(String userId, String classId) {
        MLog.i("改变消息的已读状态，userId=" + userId + "，classId=" + classId);
        synchronized (_writeLock) {
            db.execSQL("UPDATE class_message SET read_status=1 WHERE owner=? AND classinfo_id=? AND read_status=0",
                    new String[]{userId, classId});
        }
    }

    public int queryUnreadMsg(String userId) {
        synchronized (_writeLock) {
            int count = 0;
            Cursor c = db
                    .rawQuery(
                            "SELECT count(1) FROM class_message WHERE owner=? AND read_status=0",
                            new String[]{userId});
            c.moveToNext();
            count = c.getInt(0);
            c.close();
            return count;
        }
    }

    /**
     * 教师查询自己所带班级消息
     *
     * @param userId
     * @return
     */
    public List<ClassMessage> queryClazzInfo(Context ctx, List<ClassItem> classList, String userId) {
        synchronized (_writeLock) {
            ArrayList<ClassMessage> list = new ArrayList<ClassMessage>();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append("      t3.num, ");
            sql.append("      t.classinfo_id, ");
            sql.append("      t.clazz_name, ");
            sql.append("      t.content, ");
            sql.append("      t.send_date ");
            sql.append(" FROM ");
            sql.append("     ( ");
            sql.append("      SELECT ");
            sql.append("          MAX(class_message_id) AS class_message_id,");
            sql.append("          classinfo_id ");
            sql.append("      FROM ");
            sql.append("          class_message ");
            sql.append("      WHERE owner=? ");
            sql.append("      GROUP BY classinfo_id ");
            sql.append("     ) t1 ");
            sql.append(" LEFT JOIN ");
            sql.append("     ( ");
            sql.append("      SELECT ");
            sql.append("          classinfo_id, ");
            sql.append("          COUNT(1) AS num ");
            sql.append("      FROM ");
            sql.append("          class_message ");
            sql.append("      WHERE ");
            sql.append("          read_status=0 AND owner=? ");
            sql.append("      GROUP BY classinfo_id ");
            sql.append("     )  t3 ");
            sql.append(" ON ");
            sql.append("      t1.classinfo_id=t3.classinfo_id ");
            sql.append(" LEFT JOIN ");
            sql.append("      class_message t ");
            sql.append(" ON ");
            sql.append("      t1.class_message_id=t.class_message_id ");
            sql.append(" where t.owner = ? and t.classinfo_id = ?");

            for (ClassItem ic : classList) {
                ClassMessage m = new ClassMessage();
                m.setClassinfoID(ic.getClassId());
                m.setClazzName(ic.getClassName());
                Cursor c = db.rawQuery(sql.toString(), new String[]{userId,
                        userId, userId, ic.getClassId()});
                while (c.moveToNext()) {
                    m.setNewClzMsgCount((c.getInt(c.getColumnIndex("num"))));
                    m.setContent(c.getString(c.getColumnIndex("content")));
                    m.setSendDate(TimeUtil.parseFullTime((c.getString(c
                            .getColumnIndex("send_date")))));
                }
                c.close();
                list.add(m);
            }
            return list;
        }
    }

    //当用户发送给班级，更新用户信息
    public void updatePerson(List<ClassMessage> classMessages, Context context) {
        User user = User.getUser(context);
        if (classMessages != null && classMessages.size() > 0) {
            for (int i = 0; i < classMessages.size(); i++) {
                if (!user.getUserId().equals(classMessages.get(i).getSenderID())) {
                    updatePersonMessage(classMessages.get(i), user);
                }
            }
        }
    }

    public void updatePersonMessage(ClassMessage classMessage, User user) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update class_message ");
            sql.append("set headpic=? ");
            sql.append("where sender_id=? and owner=?");
            db.execSQL(sql.toString(), new String[]{classMessage.getStuPhoto(), classMessage.getSenderID(), user.getUserId()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}