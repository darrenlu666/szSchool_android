package com.dt5000.ischool.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 日记实体类
 * 
 * @author 周锋
 * @date 2016年2月4日 上午10:32:55
 * @ClassInfo com.dt5000.ischool.entity.Diary
 * @Description
 */
public class Diary implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String author;
	private String title;
	private String content;
	private int levelRank;// 1优秀，2良好，3阅，4加油
	private String createAt;
	private List<DiaryPic> attachList;
	private List<DiaryDoc> docList;

	public Diary() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public int getLevelRank() {
		return levelRank;
	}

	public void setLevelRank(int levelRank) {
		this.levelRank = levelRank;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DiaryPic> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<DiaryPic> attachList) {
		this.attachList = attachList;
	}

	public List<DiaryDoc> getDocList() {
		return docList;
	}

	public void setDocList(List<DiaryDoc> docList) {
		this.docList = docList;
	}

}
