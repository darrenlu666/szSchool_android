package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 作业评论实体类
 * 
 * @author 周锋
 * @date 2016年1月12日 下午3:25:56
 * @ClassInfo com.dt5000.ischool.entity.HomeworComment
 * @Description
 */
public class HomeworkComment implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer commentId;
	private String content;
	private Integer homeworkId;
	private HomeworkCommentPic image;
	private Integer isRead;
	private Integer parentId;
	private Long publishDate;
	private String userName;
	private Integer userRole;
	private String stid;
	private HomeworkComment childComment;

	public HomeworkComment() {
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getStid() {
		return stid;
	}

	public void setStid(String stid) {
		this.stid = stid;
	}

	public Integer getHomeworkId() {
		return homeworkId;
	}

	public void setHomeworkId(Integer homeworkId) {
		this.homeworkId = homeworkId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Long publishDate) {
		this.publishDate = publishDate;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public HomeworkComment getChildComment() {
		return childComment;
	}

	public void setChildComment(HomeworkComment childComment) {
		this.childComment = childComment;
	}

	public HomeworkCommentPic getImage() {
		return image;
	}

	public void setImage(HomeworkCommentPic image) {
		this.image = image;
	}

}