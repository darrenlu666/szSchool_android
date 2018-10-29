package com.dt5000.ischool.entity;

/**
 * 博客图片实体类
 * 
 * @author 周锋
 * @date 2016年2月24日 上午9:57:58
 * @ClassInfo com.dt5000.ischool.entity.BlogPic
 * @Description
 */
public class BlogPic {

	private String imageUrl;
	private String smallImage;
	private String mediumImage;

	public BlogPic(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}

	public String getMediumImage() {
		return mediumImage;
	}

	public void setMediumImage(String mediumImage) {
		this.mediumImage = mediumImage;
	}

}
