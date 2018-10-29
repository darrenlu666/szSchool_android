package com.dt5000.ischool.entity;

/**
 * 教师作业详情评论
 * 
 * @author 周锋
 * @Date 2014-5-30
 * @ClassInfo com.dt5000.ischool.pojo-TeaComment.java
 * @Description
 */
public class TeaComment implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Integer commentId;
	private String content;
	private Integer homeworkId;
	private Integer isRead;
	private Integer parentId;
	private Long publishDate;
	private String userId;
	private String userName;
	private Integer userRole;

	public TeaComment() {
	}

	public TeaComment(Integer commentId, String content, Integer homeworkId,
			Integer isRead, Integer parentId, Long publishDate, String userId,
			String userName, Integer userRole) {
		this.commentId = commentId;
		this.content = content;
		this.homeworkId = homeworkId;
		this.isRead = isRead;
		this.parentId = parentId;
		this.publishDate = publishDate;
		this.userId = userId;
		this.userName = userName;
		this.userRole = userRole;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
}