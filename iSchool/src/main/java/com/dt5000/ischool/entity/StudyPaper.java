package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习自测评估试卷实体类
 * 
 * @author 周锋
 * @date 2016年1月25日 下午3:26:57
 * @ClassInfo com.dt5000.ischool.entity.StudyPaper
 * @Description
 */
public class StudyPaper implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer selfTestId;
	private Integer selfTestRetId;
	private String name;
	private String createTime;
	private Integer subjectId;

	public StudyPaper() {
	}

	public Integer getSelfTestId() {
		return selfTestId;
	}

	public void setSelfTestId(Integer selfTestId) {
		this.selfTestId = selfTestId;
	}

	public Integer getSelfTestRetId() {
		return selfTestRetId;
	}

	public void setSelfTestRetId(Integer selfTestRetId) {
		this.selfTestRetId = selfTestRetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

}
