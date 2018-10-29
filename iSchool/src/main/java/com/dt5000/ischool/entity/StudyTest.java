package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习自测评估试题实体类
 * 
 * @author 周锋
 * @date 2016年1月25日 下午5:44:44
 * @ClassInfo com.dt5000.ischool.entity.StudyTest
 * @Description
 */
public class StudyTest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String testLineId;
	private Integer type;
	private Integer sortIndex;
	private Integer score;
	private String daan;
	private String userDaan;

	/**
	 * 默认undone表示该题未作答，right表示该题用户选择正确，wrong表示该题用户选择错误
	 */
	private String result = "undone";

	public StudyTest() {
	}

	public String getTestLineId() {
		return testLineId;
	}

	public void setTestLineId(String testLineId) {
		this.testLineId = testLineId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getDaan() {
		return daan;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}