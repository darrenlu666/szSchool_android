package com.dt5000.ischool.entity;

/**
 * 作业评论图片实体类
 * 
 * @author 周锋
 * @date 2016年3月2日 下午1:44:39
 * @ClassInfo com.dt5000.ischool.entity.HomeworkCommentPic
 * @Description
 */
public class HomeworkCommentPic {

	private String smallUrl;
	private String url;

	public HomeworkCommentPic() {
	}

	public HomeworkCommentPic(String smallUrl, String url) {
		this.smallUrl = smallUrl;
		this.url = url;
	}

	public String getSmallUrl() {
		return smallUrl;
	}

	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
