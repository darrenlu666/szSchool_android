package com.dt5000.ischool.entity;

/**
 * 数据实体类：根据type来区分，1日记、2校园公告、3教育资讯、4育儿经、5食谱
 * 
 * @author 周锋
 * @date 2016年2月2日 上午9:17:54
 * @ClassInfo com.dt5000.ischool.entity.Publication
 * @Description
 */
public class Publication {

	private String author;
	private String classInfoId;
	private String content;
	private String createAt;
	private String createBy;
	private String gradeInfoId;
	private String id;
	private String schoolId;
	private String title;
	private String type;
	private String visitorNum;

	public Publication() {
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getClassInfoId() {
		return classInfoId;
	}

	public void setClassInfoId(String classInfoId) {
		this.classInfoId = classInfoId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getGradeInfoId() {
		return gradeInfoId;
	}

	public void setGradeInfoId(String gradeInfoId) {
		this.gradeInfoId = gradeInfoId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getVisitorNum() {
		return visitorNum;
	}

	public void setVisitorNum(String visitorNum) {
		this.visitorNum = visitorNum;
	}

}
