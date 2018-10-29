package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习同步练习题实体类
 * 
 * @author 周锋
 * @date 2016年1月22日 下午4:08:51
 * @ClassInfo com.dt5000.ischool.entity.StudyPractice
 * @Description
 */
public class StudyPractice implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String type;
	private String subject;
	private String myAnswer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMyAnswer() {
		return myAnswer;
	}

	public void setMyAnswer(String myAnswer) {
		this.myAnswer = myAnswer;
	}

}
