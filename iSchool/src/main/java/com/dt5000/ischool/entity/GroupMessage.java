package com.dt5000.ischool.entity;

import java.util.Date;

/**
 * 群组消息实体类
 *
 */
public class GroupMessage {

	private String groupinfoID;
	private String groupName;
	private String senderID;
	private String senderName;
	private String content;
	private String picUrl;
	private String owner;
	private String stuPhoto;
	private Date sendDate;
	private Integer readStatus;
	private Integer groupMessageID;
	private Integer newGroupMsgCount = 0;
	private String videoUrl;

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public GroupMessage() {
	}

	public String getGroupinfoID() {
		return groupinfoID;
	}

	public void setGroupinfoID(String groupinfoID) {
		this.groupinfoID = groupinfoID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupMessageID() {
		return groupMessageID;
	}

	public void setGroupMessageID(Integer groupMessageID) {
		this.groupMessageID = groupMessageID;
	}

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}

	public Boolean isSend() {
		return this.owner.equalsIgnoreCase(this.senderID);
	}

	public Integer getNewGroupMsgCount() {
		return newGroupMsgCount;
	}

	public void setNewGroupMsgCount(Integer newGroupMsgCount) {
		this.newGroupMsgCount = newGroupMsgCount;
	}

	public String getStuPhoto() {
		return stuPhoto;
	}

	public void setStuPhoto(String stuPhoto) {
		this.stuPhoto = stuPhoto;
	}

}
