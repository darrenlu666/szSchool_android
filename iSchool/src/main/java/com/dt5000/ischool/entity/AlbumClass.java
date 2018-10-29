package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 新增班级相册时用到的班级信息实体
 * 
 * @author 周锋
 * @date 2015年10月16日 下午1:14:55
 * @ClassInfo com.dt5000.ischool.entity.AlbumClass
 * @Description
 */
public class AlbumClass implements Serializable {

	private static final long serialVersionUID = 1L;
	private String classinfoId;
	private String bjmc;
	private String status;

	public String getClassinfoId() {
		return classinfoId;
	}

	public void setClassinfoId(String classinfoId) {
		this.classinfoId = classinfoId;
	}

	public String getBjmc() {
		return bjmc;
	}

	public void setBjmc(String bjmc) {
		this.bjmc = bjmc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
