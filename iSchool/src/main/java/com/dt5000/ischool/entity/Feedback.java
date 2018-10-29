package com.dt5000.ischool.entity;


import java.io.Serializable;

public class Feedback implements Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private String userId;
	private String userName;
	private String title;
	private String content;

	public Feedback(){
		
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}