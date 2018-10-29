package com.dt5000.ischool.entity;

/**
 * Created by weimy on 2017/12/18.
 */

public class RequestMessage {

    /**
     * appstoreLink : "www.baidu.com"
     * highestVersion : 1.1.0
     * lowestVersion : 1.1.0
     * memo :
     * resultStatus : 200
     */

    private String appstoreLink;
    private String highestVersion;
    private String lowestVersion;
    private String memo;
    private String resultStatus;

    public String getAppstoreLink() {
        return appstoreLink;
    }

    public void setAppstoreLink(String appstoreLink) {
        this.appstoreLink = appstoreLink;
    }

    public String getHighestVersion() {
        return highestVersion;
    }

    public void setHighestVersion(String highestVersion) {
        this.highestVersion = highestVersion;
    }

    public String getLowestVersion() {
        return lowestVersion;
    }

    public void setLowestVersion(String lowestVersion) {
        this.lowestVersion = lowestVersion;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }
}
