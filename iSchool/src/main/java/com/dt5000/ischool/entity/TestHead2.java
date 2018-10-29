package com.dt5000.ischool.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;



public class TestHead2 {
	
	
	private Integer testHeadId;
	@Expose
	private String name;
	private Integer gradeId;
	private Integer subjectId;
	private Integer status;
	private Timestamp createTime;
	private String lastUpdateBy;
	private Timestamp lastUpdateTime;
//	private Set<TestResultHead> testResultHeads = new HashSet<TestResultHead>(
//			0);
	private Set<StudyTest> studyTests = new HashSet<StudyTest>(0);
	private int count;
	public Integer getTestHeadId() {
		return testHeadId;
	}
	public void setTestHeadId(Integer testHeadId) {
		this.testHeadId = testHeadId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGradeId() {
		return gradeId;
	}
	public void setGradeId(Integer gradeId) {
		this.gradeId = gradeId;
	}
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getLastUpdateBy() {
		return lastUpdateBy;
	}
	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Set<StudyTest> getTestLines() {
		return studyTests;
	}
	public void setTestLines(Set<StudyTest> studyTests) {
		this.studyTests = studyTests;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	

}
