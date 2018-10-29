package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 班级相册实体类
 * 
 * @author 周锋
 * @date 2016年2月3日 上午10:11:10
 * @ClassInfo com.dt5000.ischool.entity.AlbumItem
 * @Description
 */
public class AlbumItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private int albumId;
	private String albumName;
	private String coverUrl;
	private String qiniuCoverUrl;
	private int size;

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getQiniuCoverUrl() {
		return qiniuCoverUrl;
	}

	public void setQiniuCoverUrl(String qiniuCoverUrl) {
		this.qiniuCoverUrl = qiniuCoverUrl;
	}

}
