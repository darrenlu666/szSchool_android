package com.dt5000.ischool.entity;

/**
 * 育儿经收藏实体类
 * 
 * @author 周锋
 * @date 2016年2月4日 上午10:00:44
 * @ClassInfo com.dt5000.ischool.entity.ChildrenCollect
 * @Description
 */
public class ChildrenCollect {

	private String createAt;
	private String creator;
	private String id;
	private String title;

	public ChildrenCollect() {
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
