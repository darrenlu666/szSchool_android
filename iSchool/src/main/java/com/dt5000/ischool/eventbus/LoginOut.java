package com.dt5000.ischool.eventbus;

import com.dt5000.ischool.entity.green_entity.UserInfo;

/**
 * Created by weimy on 2017/11/29.
 */

public class LoginOut {
    private UserInfo info;

    public LoginOut(UserInfo info) {
        this.info = info;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

}
