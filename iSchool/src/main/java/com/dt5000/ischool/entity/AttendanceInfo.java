package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 考勤信息实体类
 * 
 * @author 周锋
 * @date 2016年10月11日 下午4:00:28
 * @ClassInfo com.dt5000.ischool.entity.AttendanceInfo
 * @Description
 */
public class AttendanceInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String arriveTime;
	private String ifLeave;
	private String kqUserId;
	private String leaveTime;
	private String realName;
	private String studentId;
	private String headImageUrl;

	public AttendanceInfo() {
		super();
	}

	public String getKqUserId() {
		return kqUserId;
	}

	public void setKqUserId(String kqUserId) {
		this.kqUserId = kqUserId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getIfLeave() {
		return ifLeave;
	}

	public void setIfLeave(String ifLeave) {
		this.ifLeave = ifLeave;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getHeadImageUrl() {
		return headImageUrl;
	}

	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}

}
