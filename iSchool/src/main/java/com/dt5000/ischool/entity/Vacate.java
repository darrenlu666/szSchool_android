package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 请假单实体类
 * 
 * @author 周锋
 * @date 2016年10月10日 上午10:42:50
 * @ClassInfo com.dt5000.ischool.entity.Vacate
 * @Description
 */
public class Vacate implements Serializable {

	private static final long serialVersionUID = 1L;
	private String realName;
	private String submitTime;
	private String startTime;
	private String endTime;
	private String isAgree;// 0.未确认 1.已确认 2.已撤销
	private String reason;
	private String days;
	private String id;
	private String studentBaseInfoId;
	private String type;// 0:事假 1.病假

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getIsAgree() {
		return isAgree;
	}

	public void setIsAgree(String isAgree) {
		this.isAgree = isAgree;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudentBaseInfoId() {
		return studentBaseInfoId;
	}

	public void setStudentBaseInfoId(String studentBaseInfoId) {
		this.studentBaseInfoId = studentBaseInfoId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

}
