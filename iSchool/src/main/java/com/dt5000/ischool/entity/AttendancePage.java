package com.dt5000.ischool.entity;

import java.util.List;

/**
 * 考勤总览实体类
 * 
 * @author 周锋
 * @date 2016年10月11日 下午4:01:19
 * @ClassInfo com.dt5000.ischool.entity.AttendancePage
 * @Description
 */
public class AttendancePage {

	private String date;
	private String dateCN;
	private List<AttendanceInfo> list;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateCN() {
		return dateCN;
	}

	public void setDateCN(String dateCN) {
		this.dateCN = dateCN;
	}

	public List<AttendanceInfo> getList() {
		return list;
	}

	public void setList(List<AttendanceInfo> list) {
		this.list = list;
	}

}
