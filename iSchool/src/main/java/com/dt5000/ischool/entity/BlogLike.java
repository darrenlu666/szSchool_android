package com.dt5000.ischool.entity;

/**
 * 博客点赞人实体类
 * 
 * @author 周锋
 * @date 2016年2月24日 上午11:23:11
 * @ClassInfo com.dt5000.ischool.entity.BlogLike
 * @Description
 */
public class BlogLike {

	private String createName;

	public BlogLike() {
	}

	public BlogLike(String createName) {
		this.createName = createName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

}
