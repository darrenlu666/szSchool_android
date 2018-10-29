package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 群组信息实体类
 */
public class GroupsItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userGroupId;
    private String userGroupName;
    private boolean choose;

    public GroupsItem() {
    }

    public String getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

}
