package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 作业附件实体类
 * 
 * @author 周锋
 * @Date 2014年9月1日
 * @ClassInfo com.dt5000.ischool.entity.HomeworkAttach.java
 * @Description
 */
public class HomeworkAttach implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String filePath;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
