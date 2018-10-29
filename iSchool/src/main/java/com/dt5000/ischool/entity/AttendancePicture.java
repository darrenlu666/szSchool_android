package com.dt5000.ischool.entity;

/**
 * 考勤照片实体类
 * 
 * @author 周锋
 * @date 2016年10月12日 上午10:59:20
 * @ClassInfo com.dt5000.ischool.entity.AttendancePicture
 * @Description
 */
public class AttendancePicture {

	private String photoUrl;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

}
