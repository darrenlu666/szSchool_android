package com.dt5000.ischool.entity;

import java.io.Serializable;

public class DocumentFlow implements Serializable{
	private static final long serialVersionUID = 1L;
     private String docId;
     private String status;//0是新建，1是完成，2是关闭
     private String teacherDocStatus;//0是已读,1是未读
     private String title;
     private String createDate;
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTeacherDocStatus() {
		return teacherDocStatus;
	}
	public void setTeacherDocStatus(String teacherDocStatus) {
		this.teacherDocStatus = teacherDocStatus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
}
