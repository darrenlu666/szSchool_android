package com.dt5000.ischool.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 个人消息数据库操作类
 *
 * @author 周锋
 * @date 2016年1月19日 下午1:10:47
 * @ClassInfo com.dt5000.ischool.db.PersonMessageDBManager
 * @Description
 */
public class PersonMessageDBManager {

    private DBHelper helper;
    private SQLiteDatabase db;
    private final static byte[] writeLock = new byte[0];

    public PersonMessageDBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 将服务器获取的个人消息列表插入本地数据库
     *
     * @param data
     */
    public void addPersonMsgList(List<PersonMessage> data) {
        synchronized (writeLock) {
            try {
                db.beginTransaction();

                String sql = "INSERT INTO message (message_id, content, sender_id, "
                        + "sender_name, receiver_id, receiver_name, send_datetime, "
                        + "read_status, image_url, owner, sync_time, profile_url, video_url) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,datetime('now','localtime'),?,?)";
                Object[] params = null;
                for (PersonMessage m : data) {
                    params = new Object[]{m.getMessageId(), m.getContent(),
                            m.getSenderId(), m.getSenderName(),
                            m.getReceiverId(), m.getReceiverName(),
                            TimeUtil.fullTimeFormat(m.getSendDatetime()),
                            m.getReadStatus(), m.getImageUrl(), m.getOwner(),
                            m.getProfileUrl(),m.getVideoUrl()};
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
     * 将一条个人消息插入本地数据库
     *
     */
    public void addPersonMsgSingle(PersonMessage data) {
        synchronized (writeLock) {
            try {
                db.beginTransaction();

                String sql = "INSERT INTO message (message_id, content, sender_id,"
                        + "sender_name, receiver_id, receiver_name, send_datetime, "
                        + "read_status, image_url, owner, sync_time, profile_url) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,datetime('now','localtime'),?)";
                db.execSQL(
                        sql,
                        new Object[]{
                                data.getMessageId(),
                                data.getContent(),
                                data.getSenderId(),
                                data.getSenderName(),
                                data.getReceiverId(),
                                data.getReceiverName(),
                                TimeUtil.fullTimeFormat(data.getSendDatetime()),
                                data.getReadStatus(), data.getImageUrl(),
                                data.getOwner(), data.getProfileUrl()});

                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 根据用户id查询该用户的通讯消息记录 连接表：根据对话人id进行分组，得出每组消息的最大id
     * 左连接表：根据对话人id进行分组，得出该组中未读消息的数量
     * 三表结合：根据对话人id进行结合得出与每个对话人的最新一条消息，以及与该对话人的未读消息数，组装成消息列表
     *
     * @param userId   登录用户的id
     * @param pageSize 分页数据,传递参数NULL则不使用limit查询
     * @return
     */
    public List<PersonMessage> queryMsg(String userId, Integer pageSize) {
        synchronized (writeLock) {
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" 	  t1.message_id, ");
            sql.append(" 	  t1.content, ");
            sql.append("      t1.sender_id, ");
            sql.append("      t1.sender_name, ");
            sql.append("      t1.receiver_id, ");
            sql.append("      t1.receiver_name, ");
            sql.append("      t1.send_datetime, ");
            sql.append("      t1.profile_url, ");
            sql.append("      t4.new_msg_count  ");
            sql.append(" FROM ");
            sql.append("     message t1 ");
            sql.append(" JOIN ");
            sql.append("      (SELECT ");
            sql.append("            MAX(t2.message_id) messageId, ");
            sql.append("            CASE WHEN t2.sender_id=? THEN t2.receiver_id ");
            sql.append("            ELSE t2.sender_id END AS receiverId ");
            sql.append("       FROM ");
            sql.append("            message t2 ");
            sql.append("       WHERE ");
            sql.append("            t2.owner=? AND ");
            sql.append("            (t2.sender_id=? OR t2.receiver_id=? ) ");
            sql.append("           GROUP BY receiverId ");
            sql.append("       ) t3 ");
            sql.append(" ON t1.message_id=t3.messageId ");
            sql.append(" LEFT JOIN ");
            sql.append("       ( ");
            sql.append("        SELECT ");
            sql.append("             sender_id, ");
            sql.append("             COUNT(1) AS new_msg_count ");
            sql.append("        FROM ");
            sql.append("             message ");
            sql.append("        WHERE ");
            sql.append("             owner=? AND read_status=0 ");
            sql.append("             AND receiver_id=?   ");
            sql.append("        GROUP BY sender_id ");
            sql.append("       ) t4 ");
            sql.append(" ON CASE ");
            sql.append(" WHEN t1.sender_id = ? THEN t1.receiver_id = t4.sender_id ");
            sql.append(" ELSE t1.sender_id = t4.sender_id ");
            sql.append(" END ");
            sql.append(" WHERE t1.owner=? ");
            sql.append(" ORDER BY t1.message_id DESC ");

            if (null != pageSize) {
                sql.append(" LIMIT " + pageSize);
            }
            Cursor cursor = db.rawQuery(sql.toString(), new String[]{userId,
                    userId, userId, userId, userId, userId, userId, userId});
            List<PersonMessage> list = new ArrayList<PersonMessage>();
            while (cursor.moveToNext()) {
                PersonMessage m = CursorToBean(cursor);
                list.add(m);
            }
            cursor.close();

            // -----------------周锋修改 2016/5/18
            // 查询对话列表中最新一条消息的时候，如果发送者是自己，则需要将头像改成对方的头像
            for (PersonMessage msg : list) {
                if (userId.equals(msg.getSenderId())) {// 对话中最新一条消息是自己发送，则需要将对方头像另外查询
                    //查询发送者是对方的所有信息，并降序排列，取第一条即为对方发给自己的最新消息，然后取头像
                    Cursor c = db
                            .rawQuery(
                                    "SELECT profile_url FROM message WHERE receiver_id=? AND sender_id=? ORDER BY message_id DESC",
                                    new String[]{userId, msg.getReceiverId()});
                    String profile_url = "";
                    if (c.moveToNext()) {
                        profile_url = c.getString(0);
                    }
                    msg.setProfileUrl(profile_url);
                    c.close();
                }
            }
            // -----------------

            return list;
        }
    }

    /**
     * 教师端：根据用户id查询该用户的通讯消息记录
     *
     * @param teaId    教师id
     * @param pageSize 每次查询多少条
     * @param loadCode 刷新 或者 更多
     * @return
     */
    public List<PersonMessage> queryMsg(String teaId, Integer messageId,
                                        Integer pageSize, int loadCode) {
        synchronized (writeLock) {
            String[] params = null;
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" 	  t1.message_id, ");
            sql.append(" 	  t1.content, ");
            sql.append("      t1.sender_id, ");
            sql.append("      t1.sender_name, ");
            sql.append("      t1.receiver_id, ");
            sql.append("      t1.receiver_name, ");
            sql.append("      t1.send_datetime, ");
            sql.append("      t1.profile_url, ");
            sql.append("      t4.new_msg_count  ");
            sql.append(" FROM");
            sql.append("      message t1 ");
            sql.append(" JOIN");
            sql.append("      (SELECT");
            sql.append("            MAX(t2.message_id) messageId,");
            sql.append("            CASE WHEN t2.sender_id=? THEN t2.receiver_id");
            sql.append("            ELSE t2.sender_id END AS receiverId");
            sql.append("       FROM");
            sql.append("            message t2 ");
            sql.append("       WHERE");
            sql.append("            t2.owner=? AND ");
            sql.append("            (t2.sender_id=? OR t2.receiver_id=?)");
            sql.append("           GROUP BY receiverId");
            sql.append("       ) t3 ");
            sql.append(" ON t1.message_id=t3.messageId ");
            sql.append(" LEFT JOIN");
            sql.append("       (");
            sql.append("        SELECT");
            sql.append("             sender_id,");
            sql.append("             COUNT(1) AS new_msg_count");
            sql.append("        FROM");
            sql.append("             message ");
            sql.append("        WHERE");
            sql.append("             owner=? AND read_status=0 ");
            sql.append("             AND receiver_id=?   ");
            sql.append("        GROUP BY sender_id");
            sql.append("       ) t4 ");
            sql.append(" ON t1.sender_id=t4.sender_id  ");
            sql.append(" WHERE t1.owner=? ");

            if (null != messageId) {
                params = new String[]{teaId, teaId, teaId, teaId, teaId,
                        teaId, teaId, String.valueOf(messageId)};
                if (loadCode == FlagCode.REFRESH_CODE) {
                    sql.append(" AND t1.message_id > ? ");
                } else if (loadCode == FlagCode.MORE_CODE) {
                    sql.append(" AND t1.message_id < ? ");
                }
            } else {
                params = new String[]{teaId, teaId, teaId, teaId, teaId,
                        teaId, teaId};
            }
            sql.append(" ORDER BY t1.message_id DESC LIMIT ");
            sql.append(String.valueOf(pageSize));

            Cursor c = db.rawQuery(sql.toString(), params);

            List<PersonMessage> list = new ArrayList<PersonMessage>();

            while (c.moveToNext()) {
                PersonMessage m = CursorToBean(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * 查询个人消息表中最新的pageSize条记录
     *
     * @param senderId   发件人id
     * @param receiverId 收件人id
     * @param pageSize   查询数量
     * @param owner      登录用户的标识
     * @return List<PersonMessage>
     */
    public List<PersonMessage> queryTop(String senderId, String receiverId,
                                        int pageSize, String owner) {
        synchronized (writeLock) {
            StringBuffer sql = new StringBuffer();
            sql.append("select * from ( SELECT");
            sql.append(" 	  t1.message_id,");
            sql.append(" 	  t1.content,");
            sql.append("      t1.sender_id,");
            sql.append("      t1.sender_name,");
            sql.append("      t1.receiver_id,");
            sql.append("      t1.receiver_name,");
            sql.append("      t1.send_datetime,");
            sql.append("      t1.profile_url, ");
            sql.append("      t1.image_url,  ");
            sql.append("      t1.video_url  ");
            sql.append("   FROM");
            sql.append("      message t1 ");
            sql.append("   WHERE");
            sql.append("       t1.owner=? AND ");
            sql.append("       (  ");
            sql.append("         ( t1.sender_id=? AND t1.receiver_id=? ) ");
            sql.append("         OR ");
            sql.append("         ( t1.receiver_id=? AND t1.sender_id=? ) ");
            sql.append("        )  ");
            sql.append("   ORDER BY t1.message_id desc LIMIT ");
            sql.append(String.valueOf(pageSize));
            sql.append("  ) t2 ");
            sql.append("  ORDER BY t2.message_id ASC");

            Cursor c = db.rawQuery(sql.toString(), new String[]{owner,
                    senderId, receiverId, senderId, receiverId});
            ArrayList<PersonMessage> list = new ArrayList<PersonMessage>();
            while (c.moveToNext()) {
                PersonMessage m = C2M(c);
                list.add(m);
            }
            c.close();

            return list;
        }
    }

    /**
     * 查询某条messageId之后的数据
     *
     * @param senderId
     * @param receiverId
     * @param messageId
     * @param owner
     * @return
     */
    public List<PersonMessage> queryAfter(String senderId, String receiverId,
                                          Integer messageId, String owner) {
        synchronized (writeLock) {
            StringBuffer sql = new StringBuffer();
            sql.append("   SELECT ");
            sql.append(" 	  t.message_id,");
            sql.append(" 	  t.content,");
            sql.append("      t.sender_id,");
            sql.append("      t.sender_name,");
            sql.append("      t.receiver_id,");
            sql.append("      t.receiver_name,");
            sql.append("      t.send_datetime,");
            sql.append("      t.profile_url, ");
            sql.append("      t.image_url,  ");
            sql.append("      t.video_url  ");
            sql.append("   FROM ");
            sql.append("      message t ");
            sql.append("   WHERE ");
            sql.append("      t.owner=? AND ");
            sql.append("      (  ");
            sql.append("         ( t.sender_id=? AND t.receiver_id=? ) ");
            sql.append("         OR ");
            sql.append("         ( t.receiver_id=? AND t.sender_id=? )");
            sql.append("      )  ");
            sql.append("      AND t.message_id > ? ");
            sql.append("    ORDER BY t.message_id ASC ");

            Cursor c = db.rawQuery(sql.toString(),
                    new String[]{owner, senderId, receiverId, senderId,
                            receiverId, String.valueOf(messageId)});
            ArrayList<PersonMessage> list = new ArrayList<PersonMessage>();
            while (c.moveToNext()) {
                PersonMessage m = C2M(c);
                list.add(m);
            }

            c.close();

            return list;
        }
    }

    /**
     * 查询最新的未读记录数
     *
     * @return List<Person>
     */
    public int newMsgQty(String owner, String lastTime) {
        synchronized (writeLock) {
            int count = 0;
            Date date = new Date(Long.parseLong(lastTime));
            String d = TimeUtil.fullTimeFormat(date);
            String sql = "SELECT COUNT(*) qty FROM message t WHERE t.read_status=0 AND "
                    + " t.owner=? AND t.receiver_id=? AND t.sync_time > datetime(?) ";

            Cursor c = db.rawQuery(sql, new String[]{owner, owner, d});
            c.moveToFirst();
            count = c.getInt(0);
            c.close();
            return count;
        }
    }

    /**
     * 查询最新的未读班级消息记录数
     *
     * @return List<Person>
     */
    public int newClzMsgQty(String owner, String lastTime) {
        synchronized (writeLock) {
            int count = 0;
            Date date = new Date(Long.parseLong(lastTime));
            String strDate = TimeUtil.fullTimeFormat(date);
            String sql = "SELECT COUNT(*) qty FROM class_message t WHERE t.read_status=0 "
                    + " AND t.owner=? AND t.sync_time>datetime(?)";
            Cursor c = db.rawQuery(sql, new String[]{owner, strDate});
            c.moveToFirst();
            count = c.getInt(0);
            c.close();
            return count;
        }
    }

    /**
     * 查询某条messageId之前的数据
     *
     * @param senderId
     * @param receiverId
     * @param pageSize
     * @param messageId
     * @param owner
     * @return
     */
    public List<PersonMessage> queryBefore(String senderId, String receiverId,
                                           int pageSize, Integer messageId, String owner) {
        synchronized (writeLock) {
            StringBuffer sql = new StringBuffer();
            sql.append("select * from (SELECT");
            sql.append(" 	  t1.message_id,");
            sql.append(" 	  t1.content,");
            sql.append("      t1.sender_id,");
            sql.append("      t1.sender_name,");
            sql.append("      t1.receiver_id,");
            sql.append("      t1.receiver_name,");
            sql.append("      t1.send_datetime,");
            sql.append("      t1.profile_url, ");
            sql.append("      t1.image_url, ");
            sql.append("      t1.video_url ");
            sql.append("FROM");
            sql.append("      message t1 ");
            sql.append("WHERE");
            sql.append("      t1.owner=? AND ");
            sql.append("      (  ");
            sql.append("         ( t1.sender_id=? AND t1.receiver_id=? ) ");
            sql.append("         OR");
            sql.append("         ( t1.receiver_id=? AND t1.sender_id=? ) ");
            sql.append("      ) ");
            sql.append("      AND t1.message_id < ?  ");
            sql.append("ORDER BY t1.message_id desc LIMIT ");
            sql.append(String.valueOf(pageSize));
            sql.append(") t2 order by t2.message_id asc;");
            Cursor c = db.rawQuery(sql.toString(),
                    new String[]{owner, senderId, receiverId, senderId,
                            receiverId, String.valueOf(messageId)});
            ArrayList<PersonMessage> list = new ArrayList<PersonMessage>();
            while (c.moveToNext()) {
                PersonMessage m = C2M(c);
                list.add(m);
            }

            c.close();

            return list;
        }
    }

    public void closeDB() {
        db.close();
    }

    private PersonMessage CursorToBean(Cursor c) {
        PersonMessage m = new PersonMessage();
        m.setMessageId(c.getInt(c.getColumnIndex("message_id")));
        m.setContent(c.getString(c.getColumnIndex("content")));
        m.setSenderId(c.getString(c.getColumnIndex("sender_id")));
        m.setSenderName(c.getString(c.getColumnIndex("sender_name")));
        m.setReceiverId(c.getString(c.getColumnIndex("receiver_id")));
        m.setReceiverName(c.getString(c.getColumnIndex("receiver_name")));
        m.setSendDatetime(TimeUtil.parseFullTime((c.getString(c
                .getColumnIndex("send_datetime")))));
        m.setNewMsgCount(c.getInt(c.getColumnIndex("new_msg_count")));
        m.setProfileUrl(c.getString(c.getColumnIndex("profile_url")));
        if(c.getColumnIndex("video_url")!=-1){
            m.setVideoUrl(c.getString(c.getColumnIndex("video_url")));
        }
        return m;
    }

    /***
     * 游标转成对象
     */
    private PersonMessage C2M(Cursor c) {
        PersonMessage m = new PersonMessage();
        m.setMessageId(c.getInt(c.getColumnIndex("message_id")));
        m.setContent(c.getString(c.getColumnIndex("content")));
        m.setSenderId(c.getString(c.getColumnIndex("sender_id")));
        m.setSenderName(c.getString(c.getColumnIndex("sender_name")));
        m.setReceiverId(c.getString(c.getColumnIndex("receiver_id")));
        m.setReceiverName(c.getString(c.getColumnIndex("receiver_name")));
        m.setSendDatetime(TimeUtil.parseFullTime((c.getString(c
                .getColumnIndex("send_datetime")))));
        m.setImageUrl(c.getString(c.getColumnIndex("image_url")));
        m.setProfileUrl(c.getString(c.getColumnIndex("profile_url")));
        if(c.getColumnIndex("video_url")!=-1){
            m.setVideoUrl(c.getString(c.getColumnIndex("video_url")));
        }
        return m;
    }

    public void deleteMsg(Integer messageId) {
        synchronized (writeLock) {
            if (null != messageId) {
                db.execSQL("DELETE FROM message WHERE message_id=?",
                        new Integer[]{messageId});
            }
        }
    }

    /**
     * 查询未读的班级消息数目
     *
     * @param userId
     * @return
     */
    public int queryUnreadClzMsg(String userId) {
        synchronized (writeLock) {
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
     * 查询未读的班级消息数目
     *
     * @param userId
     * @return
     */
    public int queryUnreadGroupMsg(String userId) {
        synchronized (writeLock) {
            int count = 0;
            Cursor c = db
                    .rawQuery(
                            "SELECT count(1) FROM group_message WHERE owner=? AND read_status=0",
                            new String[]{userId});
            c.moveToNext();
            count = c.getInt(0);
            c.close();
            return count;
        }
    }

    /**
     * 更改消息状态，设置与某人对话的所有消息为已读
     *
     * @param userId
     * @param friendId
     */
    public void updateMsgStatus(String userId, String friendId) {
        synchronized (writeLock) {
            db.execSQL(
                    "UPDATE message SET read_status=1 WHERE ((sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?)) AND read_status=0",
                    new String[]{userId, friendId, friendId, userId});
        }
    }

    /**
     * 查询本地数据库中个人消息的最大id
     *
     * @param owner
     * @return
     */
    public int queryMaxPersonMsgId(String owner) {
        synchronized (writeLock) {
            int max = 0;
            StringBuffer sql = new StringBuffer();
            sql.append("   SELECT ");
            sql.append(" 	  max(t.message_id)");
            sql.append("   FROM ");
            sql.append("      message t ");
            sql.append("   WHERE ");
            sql.append("      t.owner=? ");
            Cursor c = db.rawQuery(sql.toString(), new String[]{owner});
            if (c.moveToNext()) {
                max = c.getInt(0);
            }
            c.close();
            return max;
        }
    }

    /**
     * 查询本地数据库中班级消息的最大id
     *
     * @param owner
     * @return
     */
    public int queryMaxClassMsgId(String owner) {
        int max = 0;
        StringBuffer sqlC = new StringBuffer();
        sqlC.append("   SELECT ");
        sqlC.append(" 	  max(c.class_message_id)");
        sqlC.append("   FROM ");
        sqlC.append("      class_message c ");
        sqlC.append("   WHERE ");
        sqlC.append("      c.owner=? ");
        Cursor cc = db.rawQuery(sqlC.toString(), new String[]{owner});
        if (cc.moveToNext()) {
            max = cc.getInt(0);
        }
        cc.close();
        return max;
    }

    /**
     * 查询本地数据库中群组消息的最大id
     *
     * @param owner
     * @return
     */
    public int queryMaxGroupMsgId(String owner) {
        int max = 0;
        StringBuffer sqlC = new StringBuffer();
        sqlC.append("   SELECT ");
        sqlC.append(" 	  max(g.group_message_id)");
        sqlC.append("   FROM ");
        sqlC.append("      group_message g ");
        sqlC.append("   WHERE ");
        sqlC.append("      g.owner=? ");
        Cursor cc = db.rawQuery(sqlC.toString(), new String[]{owner});
        if (cc.moveToNext()) {
            max = cc.getInt(0);
        }
        cc.close();
        return max;
    }

    //当用户发送给自己，更新用户信息
    public void updatePerson(List<PersonMessage> personMessages, Context context) {
        User user = User.getUser(context);
        if (personMessages != null && personMessages.size() > 0) {
            for (int i = 0; i < personMessages.size(); i++) {
                if (!user.getUserId().equals(personMessages.get(i).getSenderId())) {
                    updatePersonMessage(personMessages.get(i), user);
                }
            }
        }
    }

    public void updatePersonMessage(PersonMessage personMessage, User user) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update message ");
            sql.append("set profile_url=? ");
            sql.append("where sender_id=? and owner=?");
            db.rawQuery(sql.toString(), new String[]{personMessage.getProfileUrl(), personMessage.getSenderId(), user.getUserId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}