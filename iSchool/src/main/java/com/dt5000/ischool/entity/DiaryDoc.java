package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 日记文档实体类
 * 
 * @author 周锋
 * @date 2016年3月1日 上午10:24:37
 * @ClassInfo com.dt5000.ischool.entity.DiaryDoc
 * @Description
 */
public class DiaryDoc implements Serializable{

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String docUrl;

	public DiaryDoc() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}

}
