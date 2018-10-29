package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 考勤实体类
 * 
 * @author 周锋
 * @date 2016年10月9日 上午11:12:23
 * @ClassInfo com.dt5000.ischool.entity.Attendance
 * @Description
 */
public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String transactionTime;
	private String imgUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

}
