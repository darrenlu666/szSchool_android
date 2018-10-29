package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 科目实体类
 * 
 * @author 周锋
 * @date 2015年3月3日 下午3:10:28
 * @ClassInfo com.dt5000.ischool.entity.SubjectItem
 * @Description
 */
public class SubjectItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String subjectId;
	private String subjectName;
	private int subjectImg;

	public SubjectItem() {
	}

	public SubjectItem(String subjectId, String subjectName, int subjectImg) {
		this.subjectId = subjectId;
		this.subjectName = subjectName;
		this.subjectImg = subjectImg;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public int getSubjectImg() {
		return subjectImg;
	}

	public void setSubjectImg(int subjectImg) {
		this.subjectImg = subjectImg;
	}

}
