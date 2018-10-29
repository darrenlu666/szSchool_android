package com.dt5000.ischool.entity.green_entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by weimy on 2017/11/28.
 */
@Entity
public class UserInfo {
    @Id(autoincrement = true)
    private Long id;
    private String userId;
    private String role;
    private String userName;
    private String pwd;
    private String profileUrl;
    private String realName;
    public int relevanceNum;
    public String relevance_UserId;

    public String getRelevance_UserId() {
        return this.relevance_UserId;
    }

    public void setRelevance_UserId(String relevance_UserId) {
        this.relevance_UserId = relevance_UserId;
    }

    public int getRelevanceNum() {
        return this.relevanceNum;
    }

    public void setRelevanceNum(int relevanceNum) {
        this.relevanceNum = relevanceNum;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProfileUrl() {
        return this.profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 938127107)
    public UserInfo(Long id, String userId, String role, String userName,
                    String pwd, String profileUrl, String realName, int relevanceNum,
                    String relevance_UserId) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.userName = userName;
        this.pwd = pwd;
        this.profileUrl = profileUrl;
        this.realName = realName;
        this.relevanceNum = relevanceNum;
        this.relevance_UserId = relevance_UserId;
    }

    public UserInfo(String userId, String role, String userName,
                    String pwd, String profileUrl, String realName,
                    int relevanceNum, String relevance_UserId) {
        this.userId = userId;
        this.role = role;
        this.userName = userName;
        this.pwd = pwd;
        this.profileUrl = profileUrl;
        this.realName = realName;
        this.relevanceNum = relevanceNum;
        this.relevance_UserId = relevance_UserId;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }
}
