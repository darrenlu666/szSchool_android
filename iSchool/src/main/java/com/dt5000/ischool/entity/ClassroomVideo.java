package com.dt5000.ischool.entity;

/**
 * 自主学习同步课堂视频实体类
 * 
 * @author 周锋
 * @date 2016年1月22日 下午1:37:45
 * @ClassInfo com.dt5000.ischool.entity.ClassroomVideo
 * @Description
 */
public class ClassroomVideo {

	private String videoId;
	private String videoName;
	private String videoUrl;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

}
