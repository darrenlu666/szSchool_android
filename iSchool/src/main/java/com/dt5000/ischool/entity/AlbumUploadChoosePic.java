package com.dt5000.ischool.entity;

/**
 * 班级相册上传图片时选择手机图片的实体类
 * 
 * @author 周锋
 * @date 2015年9月28日 下午3:39:10
 * @ClassInfo com.dt5000.ischool.entity.AlbumUploadChoosePic
 * @Description
 */
public class AlbumUploadChoosePic {

	private String picPath;
	private long lastModTime;
	private boolean isChoose;

	public AlbumUploadChoosePic() {
	}

	public AlbumUploadChoosePic(String picPath, long lastModTime,
			boolean isChoose) {
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

	public long getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(long lastModTime) {
		this.lastModTime = lastModTime;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

}
