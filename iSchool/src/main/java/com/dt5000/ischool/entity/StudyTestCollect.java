package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习自测评估收藏试题实体类
 * 
 * @author 周锋
 * @date 2016年1月27日 下午6:56:00
 * @ClassInfo com.dt5000.ischool.entity.StudyTestCollect
 * @Description
 */
public class StudyTestCollect implements Serializable {

	private static final long serialVersionUID = 1L;
	private String testId;
	private int collectId;
	private int sortIndex;
	private String name;
	private Integer testHeadId;
	private int count;
	private int type;
	private String daan;

	public StudyTestCollect() {
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTestHeadId() {
		return testHeadId;
	}

	public void setTestHeadId(Integer testHeadId) {
		this.testHeadId = testHeadId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDaan() {
		return daan;
	}

	public void setDaan(String daan) {
		this.daan = daan;
	}

	public int getCollectId() {
		return collectId;
	}

	public void setCollectId(int collectId) {
		this.collectId = collectId;
	}
	
}
