package com.dt5000.ischool.entity;

import java.util.Date;

/**
 * 班级消息实体类
 * 
 * @author 周锋
 * @date 2016年1月19日 下午1:41:48
 * @ClassInfo com.dt5000.ischool.entity.ClassMessage
 * @Description
 */
public class ClassMessage {

	private String classinfoID;
	private String clazzName;
	private String senderID;
	private String senderName;
	private String content;
	private String picUrl;
	private String owner;
	private String stuPhoto;
	private Date sendDate;
	private Integer readStatus;
	private Integer classMessageID;
	private Integer newClzMsgCount = 0;
	private String videoUrl;

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public ClassMessage() {
	}

	public Integer getClassMessageID() {
		return classMessageID;
	}

	public void setClassMessageID(Integer classMessageID) {
		this.classMessageID = classMessageID;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public String getClassinfoID() {
		return classinfoID;
	}

	public void setClassinfoID(String classinfoID) {
		this.classinfoID = classinfoID;
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

	public Integer getNewClzMsgCount() {
		return newClzMsgCount;
	}

	public void setNewClzMsgCount(Integer newClzMsgCount) {
		this.newClzMsgCount = newClzMsgCount;
	}

	public String getStuPhoto() {
		return stuPhoto;
	}

	public void setStuPhoto(String stuPhoto) {
		this.stuPhoto = stuPhoto;
	}

}
