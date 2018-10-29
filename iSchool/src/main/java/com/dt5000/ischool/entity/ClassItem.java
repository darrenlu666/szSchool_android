package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 班级信息实体类
 * 
 * @author 周锋
 * @date 2016年1月13日 下午3:54:48
 * @ClassInfo com.dt5000.ischool.entity.ClassItem
 * @Description
 */
public class ClassItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String classId;
	private String className;
	private boolean choose;

	public ClassItem() {
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isChoose() {
		return choose;
	}

	public void setChoose(boolean choose) {
		this.choose = choose;
	}

}
