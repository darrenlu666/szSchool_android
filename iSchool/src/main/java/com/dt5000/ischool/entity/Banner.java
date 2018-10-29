package com.dt5000.ischool.entity;

/**
 * 首页轮播实体类
 * 
 * @author 周锋
 * @date 2016年2月25日 上午11:22:57
 * @ClassInfo com.dt5000.ischool.entity.Banner
 * @Description
 */
public class Banner {

	private String imageUrl;
	private String linkUrl;

	public Banner() {
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

}
