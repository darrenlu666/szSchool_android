package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习自测评估做题结果实体类
 * 
 * @author 周锋
 * @date 2016年1月26日 下午4:54:30
 * @ClassInfo com.dt5000.ischool.entity.StudyTestResult
 * @Description
 */
public class StudyTestResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private String testLineId;
	private String daan;
	private String userDaan;

	public StudyTestResult() {
	}

	public String getTestLineId() {
		return testLineId;
	}

	public String getDaan() {
		return daan;
	}

	public void setTestLineId(String testLineId) {
		this.testLineId = testLineId;
	}

	public void setDaan(String daan) {
		this.daan = daan;
	}

	public String getUserDaan() {
		return userDaan;
	}

	public void setUserDaan(String userDaan) {
		this.userDaan = userDaan;
	}

}