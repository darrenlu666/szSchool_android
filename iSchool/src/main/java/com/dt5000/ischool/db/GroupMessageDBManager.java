package com.dt5000.ischool.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dt5000.ischool.entity.GroupMessage;
import com.dt5000.ischool.entity.GroupsItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组消息数据库操作类
 */
public class GroupMessageDBManager {

    private DBHelper helper;
    private SQLiteDatabase db;
    private final static byte[] _writeLock = new byte[0];

    public GroupMessageDBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 将服务器获取的群组消息列表插入本地数据库
     *
     * @param data
     */
    public void addGroupMsgList(List<GroupMessage> data) {
        synchronized (_writeLock) {
            try {
                db.beginTransaction();

                String sql = "INSERT INTO group_message (group_message_id, groupinfo_id, group_name, sender_id, "
                        + "sender_name, content, send_date, read_status, pic_url, owner, sync_time, headpic,video_url) "
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,datetime('now','localtime'),?,?)";
                Object[] params = null;
                for (GroupMessage m : data) {
                    if (m.getOwner().equals(m.getSenderID())) {
                        // 如果是本人发给别人的消息，则设置为已读
                        m.setReadStatus(1);
                    }
                    params = new Object[]{m.getGroupMessageID(),
                            m.getGroupinfoID(), m.getGroupName(),
                            m.getSenderID(), m.getSenderName(), m.getContent(),
                            TimeUtil.fullTimeFormat(m.getSendDate()),
                            m.getReadStatus(), m.getPicUrl(), m.getOwner(),
                            m.getStuPhoto(),m.getVideoUrl()};
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
     * 查询time之后的数据 (新的)
     *
     * @return List<Person>
     */
    public List<GroupMessage> queryAfter(Integer groupMsgId, String owner,
                                         String groupinfoId) {
        synchronized (_writeLock) {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT t.* FROM group_message t ");
            sql.append(" WHERE t.owner=? AND t.groupinfo_id=? ");

            String[] params = null;
            if (null != groupMsgId) {
                sql.append(" AND t.group_message_id > ? ");
                params = new String[]{owner, groupinfoId,
                        String.valueOf(groupMsgId)};
            } else {
                params = new String[]{owner, groupinfoId};
            }
            sql.append(" ORDER BY t.group_message_id ");

            Cursor c = db.rawQuery(sql.toString(), params);
            ArrayList<GroupMessage> list = new ArrayList<GroupMessage>();

            while (c.moveToNext()) {
                GroupMessage m = C2M(c);
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
    public List<GroupMessage> queryTop(int n, String owner, String groupinfoId) {
        synchronized (_writeLock) {
            String sql = "SELECT * FROM (SELECT t.* FROM group_message t WHERE t.owner=? "
                    + " AND t.groupinfo_id=? ORDER BY t.send_date DESC LIMIT "
                    + String.valueOf(n) + ") t1 ORDER BY t1.send_date ASC";

            Cursor c = db.rawQuery(sql, new String[]{owner, groupinfoId});
            ArrayList<GroupMessage> list = new ArrayList<GroupMessage>();

            while (c.moveToNext()) {
                GroupMessage m = C2M(c);
                list.add(m);
            }
            c.close();
            return list;
        }
    }

    /**
     * 查询time之前的数据 （旧的）
     *
     * @param groupInfoId
     * @return List<Person>
     */
    public List<GroupMessage> queryBefore(Integer groupMsgId, String owner,
                                          String groupInfoId, int pageSize) {

        synchronized (_writeLock) {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from (SELECT t.* FROM group_message t ");
            sql.append(" WHERE t.owner=? AND t.groupinfo_id=? ");
            String[] params = null;
            if (null != groupMsgId) {
                sql.append(" AND t.group_message_id < ? ");
                params = new String[]{owner, groupInfoId,
                        String.valueOf(groupMsgId)};
            } else {
                params = new String[]{owner, groupInfoId};
            }
            sql.append(" ORDER BY t.send_date desc LIMIT ");
            sql.append(String.valueOf(pageSize));
            sql.append(") t1 order by t1.send_date asc");
            Cursor c = db.rawQuery(sql.toString(), params);
            ArrayList<GroupMessage> list = new ArrayList<GroupMessage>();
            while (c.moveToNext()) {
                GroupMessage m = C2M(c);
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
                        "SELECT * FROM group_message t ORDER BY t.send_date DESC",
                        null);
        return c;
    }

    public void closeDB() {
        db.close();
    }

    /***
     * 游标转成对象
     */
    private GroupMessage C2M(Cursor c) {
        GroupMessage m = new GroupMessage();
        m.setGroupMessageID((c.getInt(c.getColumnIndex("group_message_id"))));
        m.setSendDate(TimeUtil.parseFullTime((c.getString(c
                .getColumnIndex("send_date")))));
        m.setGroupName(c.getString(c.getColumnIndex("group_name")));
        m.setGroupinfoID(c.getString(c.getColumnIndex("groupinfo_id")));
        m.setSenderName(c.getString(c.getColumnIndex("sender_name")));
        m.setSenderID(c.getString(c.getColumnIndex("sender_id")));
        m.setPicUrl(c.getString(c.getColumnIndex("pic_url")));
        m.setContent(c.getString(c.getColumnIndex("content")));
        m.setOwner(c.getString(c.getColumnIndex("owner")));
        m.setReadStatus(c.getInt(c.getColumnIndex("read_status")));
        m.setStuPhoto(c.getString(c.getColumnIndex("headpic")));
        m.setVideoUrl(c.getString(c.getColumnIndex("video_url")));
        return m;
    }

    /**
     * 更新消息状态，设置某个班级对话的所有消息为已读状态
     * 会导致所有班级会话都更改状态，更改时间2014.9.9
     *
     * @param userId
     * @param groupId
     */
    public void updateMsgStatus(String userId, String groupId) {
        MLog.i("改变消息的已读状态，userId=" + userId + "，groupId=" + groupId);
        synchronized (_writeLock) {
            db.execSQL(
                    "UPDATE group_message SET read_status=1 WHERE owner=? AND groupinfo_id=? AND read_status=0",
                    new String[]{userId, groupId});
        }
    }

    /**
     * 教师查询自己所带班级消息
     *
     * @param userId
     * @return
     */
    public List<GroupMessage> queryGroupInfo(Context ctx,
                                             List<GroupsItem> groupList, String userId) {
        synchronized (_writeLock) {
            ArrayList<GroupMessage> list = new ArrayList<GroupMessage>();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append("      t3.num, ");
            sql.append("      t.groupinfo_id, ");
            sql.append("      t.group_name, ");
            sql.append("      t.content, ");
            sql.append("      t.send_date ");
            sql.append(" FROM ");
            sql.append("     ( ");
            sql.append("      SELECT ");
            sql.append("          MAX(group_message_id) AS group_message_id,");
            sql.append("          groupinfo_id ");
            sql.append("      FROM ");
            sql.append("          group_message ");
            sql.append("      WHERE owner=? ");
            sql.append("      GROUP BY groupinfo_id ");
            sql.append("     ) t1 ");
            sql.append(" LEFT JOIN ");
            sql.append("     ( ");
            sql.append("      SELECT ");
            sql.append("          groupinfo_id, ");
            sql.append("          COUNT(1) AS num ");
            sql.append("      FROM ");
            sql.append("          group_message ");
            sql.append("      WHERE ");
            sql.append("          read_status=0 AND owner=? ");
            sql.append("      GROUP BY groupinfo_id ");
            sql.append("     ) t3 ");
            sql.append(" ON ");
            sql.append("      t1.groupinfo_id=t3.groupinfo_id ");
            sql.append(" LEFT JOIN ");
            sql.append("      group_message t ");
            sql.append(" ON ");
            sql.append("      t1.group_message_id=t.group_message_id ");
            sql.append(" where t.owner = ? and t.groupinfo_id = ?");

            for (GroupsItem ic : groupList) {
                GroupMessage m = new GroupMessage();
                m.setGroupinfoID(ic.getUserGroupId());
                m.setGroupName(ic.getUserGroupName());
                Cursor c = db.rawQuery(sql.toString(), new String[]{userId,
                        userId, userId, ic.getUserGroupId()});
                while (c.moveToNext()) {
                    m.setNewGroupMsgCount((c.getInt(c.getColumnIndex("num"))));
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
    public void updatePerson(List<GroupMessage> groupMessages, Context context) {
        User user = User.getUser(context);
        if (groupMessages != null && groupMessages.size() > 0) {
            for (int i = 0; i < groupMessages.size(); i++) {
                if (!user.getUserId().equals(groupMessages.get(i).getSenderID())) {
                    updatePersonMessage(groupMessages.get(i), user);
                }
            }
        }
    }

    public void updatePersonMessage(GroupMessage groupMessages, User user) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update group_message ");
            sql.append("set headpic=? ");
            sql.append("where sender_id=? and owner=?");
            db.execSQL(sql.toString(), new String[]{groupMessages.getStuPhoto(), groupMessages.getSenderID(), user.getUserId()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

}