package com.dt5000.ischool.entity;

/**
 * 博客评论实体类
 * 
 * @author 周锋
 * @date 2016年2月2日 下午4:36:12
 * @ClassInfo com.dt5000.ischool.entity.BlogComment
 * @Description
 */
public class BlogComment {

	private String id;
	private String createBy;
	private String content;
	private String createName;

	public BlogComment() {
	}

	public BlogComment(String content, String createName) {
		this.content = content;
		this.createName = createName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

}
