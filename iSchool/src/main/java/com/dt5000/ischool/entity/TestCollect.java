package com.dt5000.ischool.entity;

// default package

import java.sql.Timestamp;

public class TestCollect implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private String studentbaseinfoId;
	private String testId;
	private Timestamp createTime;
	private Integer type;

	// Constructors

	/** default constructor */
	public TestCollect() {
	}

	public TestCollect(String studentbaseinfoId, String testId,
			Timestamp createTime, Integer type) {
		super();
		this.studentbaseinfoId = studentbaseinfoId;
		this.testId = testId;
		this.createTime = createTime;
		this.type = type;
	}

	public String getStudentbaseinfoId() {
		return studentbaseinfoId;
	}

	public void setStudentbaseinfoId(String studentbaseinfoId) {
		this.studentbaseinfoId = studentbaseinfoId;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}