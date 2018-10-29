package com.dt5000.ischool.db.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dt5000.ischool.entity.green_entity.GroupSendMessage;
import com.dt5000.ischool.entity.green_entity.UserInfo;

import com.dt5000.ischool.db.greendao.GroupSendMessageDao;
import com.dt5000.ischool.db.greendao.UserInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig groupSendMessageDaoConfig;
    private final DaoConfig userInfoDaoConfig;

    private final GroupSendMessageDao groupSendMessageDao;
    private final UserInfoDao userInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        groupSendMessageDaoConfig = daoConfigMap.get(GroupSendMessageDao.class).clone();
        groupSendMessageDaoConfig.initIdentityScope(type);

        userInfoDaoConfig = daoConfigMap.get(UserInfoDao.class).clone();
        userInfoDaoConfig.initIdentityScope(type);

        groupSendMessageDao = new GroupSendMessageDao(groupSendMessageDaoConfig, this);
        userInfoDao = new UserInfoDao(userInfoDaoConfig, this);

        registerDao(GroupSendMessage.class, groupSendMessageDao);
        registerDao(UserInfo.class, userInfoDao);
    }
    
    public void clear() {
        groupSendMessageDaoConfig.clearIdentityScope();
        userInfoDaoConfig.clearIdentityScope();
    }

    public GroupSendMessageDao getGroupSendMessageDao() {
        return groupSendMessageDao;
    }

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

}