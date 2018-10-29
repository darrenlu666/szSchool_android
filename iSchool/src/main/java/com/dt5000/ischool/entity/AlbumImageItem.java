package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 班级相册图片实体类
 * 
 * @author 周锋
 * @date 2016年2月3日 下午2:00:40
 * @ClassInfo com.dt5000.ischool.entity.AlbumImageItem
 * @Description
 */
public class AlbumImageItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private int imageId;
	private String imageUrl;
	private String qiniuUrl;
	private String qiniuUrlLarge;

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getQiniuUrl() {
		return qiniuUrl;
	}

	public void setQiniuUrl(String qiniuUrl) {
		this.qiniuUrl = qiniuUrl;
	}

	public String getQiniuUrlLarge() {
		return qiniuUrlLarge;
	}

	public void setQiniuUrlLarge(String qiniuUrlLarge) {
		this.qiniuUrlLarge = qiniuUrlLarge;
	}

}
