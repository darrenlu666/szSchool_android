package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 自主学习自测评估做题记录实体类
 * 
 * @author 周锋
 * @date 2016年1月27日 上午11:28:01
 * @ClassInfo com.dt5000.ischool.entity.StudyTestRecord
 * @Description
 */
public class StudyTestRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer testHeadId;
	private Integer testResultHeadId;
	private String name;
	private long createTime;
	private Integer subjectId;
	private Integer seqNum;

	public StudyTestRecord() {
	}

	public Integer getTestHeadId() {
		return testHeadId;
	}

	public void setTestHeadId(Integer testHeadId) {
		this.testHeadId = testHeadId;
	}

	public Integer getTestResultHeadId() {
		return testResultHeadId;
	}

	public void setTestResultHeadId(Integer testResultHeadId) {
		this.testResultHeadId = testResultHeadId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

}
