package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 新增博客时选择手机图片的实体类
 * 
 * @author 周锋
 * @date 2016年2月2日 下午7:47:33
 * @ClassInfo com.dt5000.ischool.entity.BlogAddChoosePic
 * @Description
 */
public class BlogAddChoosePic implements Serializable {

	private static final long serialVersionUID = 1L;
	private String picPath;
	private long lastModTime;
	private boolean isChoose;

	public BlogAddChoosePic() {
	}

	public BlogAddChoosePic(String picPath, long lastModTime, boolean isChoose) {
		this.picPath = picPath;
		this.lastModTime = lastModTime;
		this.isChoose = isChoose;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public long getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(long lastModTime) {
		this.lastModTime = lastModTime;
	}

}
