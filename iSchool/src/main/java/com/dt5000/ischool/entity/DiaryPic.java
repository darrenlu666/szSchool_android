package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 日记图片实体类
 * 
 * @author 周锋
 * @date 2016年2月19日 下午3:09:19
 * @ClassInfo com.dt5000.ischool.entity.DiaryPic
 * @Description
 */
public class DiaryPic implements Serializable{

	private static final long serialVersionUID = 1L;
	private String imageUrl;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
