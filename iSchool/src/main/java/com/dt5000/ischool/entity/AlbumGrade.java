package com.dt5000.ischool.entity;

import java.util.List;

/**
 * 新增班级相册时用到的年级信息实体
 * 
 * @author 周锋
 * @date 2015年10月16日 下午1:12:50
 * @ClassInfo com.dt5000.ischool.entity.AlbumGrade
 * @Description
 */
public class AlbumGrade {

	private String gradeId;
	private String gradeName;
	private String status;
	private List<AlbumClass> classInfoList;

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public List<AlbumClass> getClassInfoList() {
		return classInfoList;
	}

	public void setClassInfoList(List<AlbumClass> classInfoList) {
		this.classInfoList = classInfoList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
