package com.dt5000.ischool.entity.green_entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by weimy on 2017/12/27.
 */
@Entity
public class GroupSendMessage {
    @Id(autoincrement = true)
    private Long id;
    private String content;
    private String imageUrl;
    private Long message_catalogue_id;
    private String owner;
    private String profileUrl;
    private String readNames;
    private String readNum;
    private String receiveNum;
    private String receiverIds;
    private String receiverNames;
    private String sendDatetime;
    private String senderId;
    private String senderName;
    private String unReadNames;
    private int isReader;
    public int getIsReader() {
        return this.isReader;
    }
    public void setIsReader(int isReader) {
        this.isReader = isReader;
    }
    public String getUnReadNames() {
        return this.unReadNames;
    }
    public void setUnReadNames(String unReadNames) {
        this.unReadNames = unReadNames;
    }
    public String getSenderName() {
        return this.senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getSenderId() {
        return this.senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getSendDatetime() {
        return this.sendDatetime;
    }
    public void setSendDatetime(String sendDatetime) {
        this.sendDatetime = sendDatetime;
    }
    public String getReceiverNames() {
        return this.receiverNames;
    }
    public void setReceiverNames(String receiverNames) {
        this.receiverNames = receiverNames;
    }
    public String getReceiverIds() {
        return this.receiverIds;
    }
    public void setReceiverIds(String receiverIds) {
        this.receiverIds = receiverIds;
    }
    public String getReceiveNum() {
        return this.receiveNum;
    }
    public void setReceiveNum(String receiveNum) {
        this.receiveNum = receiveNum;
    }
    public String getReadNum() {
        return this.readNum;
    }
    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }
    public String getReadNames() {
        return this.readNames;
    }
    public void setReadNames(String readNames) {
        this.readNames = readNames;
    }
    public String getProfileUrl() {
        return this.profileUrl;
    }
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public String getOwner() {
        return this.owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public Long getMessage_catalogue_id() {
        return this.message_catalogue_id;
    }
    public void setMessage_catalogue_id(Long message_catalogue_id) {
        this.message_catalogue_id = message_catalogue_id;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 171698302)
    public GroupSendMessage(Long id, String content, String imageUrl,
            Long message_catalogue_id, String owner, String profileUrl,
            String readNames, String readNum, String receiveNum,
            String receiverIds, String receiverNames, String sendDatetime,
            String senderId, String senderName, String unReadNames, int isReader) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.message_catalogue_id = message_catalogue_id;
        this.owner = owner;
        this.profileUrl = profileUrl;
        this.readNames = readNames;
        this.readNum = readNum;
        this.receiveNum = receiveNum;
        this.receiverIds = receiverIds;
        this.receiverNames = receiverNames;
        this.sendDatetime = sendDatetime;
        this.senderId = senderId;
        this.senderName = senderName;
        this.unReadNames = unReadNames;
        this.isReader = isReader;
    }
    @Generated(hash = 809262646)
    public GroupSendMessage() {
    }
}
