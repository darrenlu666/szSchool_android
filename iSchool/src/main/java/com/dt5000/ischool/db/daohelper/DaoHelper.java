package com.dt5000.ischool.db.daohelper;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.db.greendao.GroupSendMessageDao;
import com.dt5000.ischool.db.greendao.UserInfoDao;
import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
import com.dt5000.ischool.entity.green_entity.UserInfo;

import java.util.List;

/**
 * Created by weimy on 2017/11/28.
 * https://www.jianshu.com/p/9acb0670a97d（Android GreenDao数据库升级操作）
 */
public class DaoHelper {
    private final static byte[] writeLock = new byte[0];

    //用户更新和添加
    public static void insertAndUpdateUserInfo(UserInfo userInfo) {
        UserInfoDao userInfoDao = MainApplication.getDaoSession().getUserInfoDao();
        if (!getUserInfo(userInfo.getUserId(), userInfoDao)) {
            userInfoDao.insert(userInfo);
        } else {
            List<UserInfo> userInfos = userInfoDao.queryBuilder()
                    .where(UserInfoDao.Properties.UserId.eq(userInfo.getUserId()))
                    .list();
            UserInfo info = userInfos.get(0);
            info.setUserId(userInfo.getUserId());
            info.setRole(userInfo.getRole());
            info.setUserName(userInfo.getUserName());
            info.setPwd(userInfo.getPwd());
            info.setProfileUrl(userInfo.getProfileUrl());
            info.setRealName(userInfo.getRealName());
            info.setRelevanceNum(userInfo.getRelevanceNum());
            info.setRelevance_UserId(userInfo.getRelevance_UserId());

            userInfoDao.insertOrReplace(info);
        }
    }

    //判断数据库是否存在该用户
    public static boolean getUserInfo(String userId, UserInfoDao userInfoDao) {
        List<UserInfo> userInfos = userInfoDao.queryBuilder().list();
        if (userInfos != null && userInfos.size() > 0) {
            for (int i = 0; i < userInfos.size(); i++) {
                if (userInfos.get(i).getUserId().equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    //删除对应用户信息
    public static synchronized boolean deleteUserInfo(UserInfo userInfo) {
        UserInfoDao userInfoDao = MainApplication.getDaoSession().getUserInfoDao();
        List<UserInfo> userInfos = userInfoDao.queryBuilder()
                .where(UserInfoDao.Properties.Id.eq(userInfo.getId()))
                .list();
        if (userInfos != null && userInfos.size() > 0) {
            userInfoDao.delete(userInfos.get(0));
        }
        return false;
    }

    //删除所有用户信息
    public static synchronized boolean deleteAll() {
        UserInfoDao userInfoDao = MainApplication.getDaoSession().getUserInfoDao();
        List<UserInfo> userInfos = userInfoDao.queryBuilder().list();
        if (userInfos != null && userInfos.size() > 0) {
            for (int i = 0; i < userInfos.size(); i++) {
                userInfoDao.delete(userInfos.get(i));
            }
        }
        return true;
    }

    //更新用户头像信息
    public static synchronized void updateUserInfo(String userId, String pic) {
        UserInfoDao userInfoDao = MainApplication.getDaoSession().getUserInfoDao();
        List<UserInfo> userInfos = userInfoDao.queryBuilder()
                .where(UserInfoDao.Properties.UserId.eq(userId))
                .list();
        if (userInfos != null && userInfos.size() > 0) {
            UserInfo userInfo = userInfos.get(0);
            userInfo.setProfileUrl(pic);
            userInfoDao.update(userInfo);
        }
    }

    //添加群发信息 https://www.jianshu.com/p/9c32f1804cd1
    public static synchronized void addGroupSend(List<GroupSendMessage> messageList) {
        GroupSendMessageDao groupSendMessageDao = MainApplication.getDaoSession().getGroupSendMessageDao();
        List<GroupSendMessage> groupSendMessageList = getGroupSendList();
        if (groupSendMessageList != null && groupSendMessageList.size() > 0) {
            for (GroupSendMessage groupSend : groupSendMessageList) {
                groupSendMessageDao.delete(groupSend);
            }
        }

        for (GroupSendMessage groupSend : messageList) {
            groupSendMessageDao.insert(groupSend);
        }
    }

    public static synchronized List<GroupSendMessage> getGroupSendList() {
        List<GroupSendMessage> groupSendMessages = null;
        GroupSendMessageDao groupSendMessageDao = MainApplication.getDaoSession().getGroupSendMessageDao();
        groupSendMessages = groupSendMessageDao.queryBuilder().orderDesc(GroupSendMessageDao.Properties.SendDatetime).list();
        return groupSendMessages;
    }

    //删除一跳数据
    public static synchronized void deleteGroup(GroupSendMessage groupSendMessage) {
        GroupSendMessageDao groupSendMessageDao = MainApplication.getDaoSession().getGroupSendMessageDao();
        groupSendMessageDao.delete(groupSendMessage);
    }
}
