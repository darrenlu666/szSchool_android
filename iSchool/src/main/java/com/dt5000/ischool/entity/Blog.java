package com.dt5000.ischool.entity;

import java.util.List;

/**
 * 博客实体类
 * 
 * @author 周锋
 * @date 2016年2月2日 下午4:35:12
 * @ClassInfo com.dt5000.ischool.entity.Blog
 * @Description
 */
public class Blog {

	private String id;
	private String createAt;
	private String createName;
	private String profileUrl;
	private List<BlogPic> attachList;
	private BlogDetail articleDetail;
	private List<BlogComment> commentList;
	private List<BlogLike> likeList;
	private boolean showDelete;
	private String shareDescription;
	private String shareImgUrl;
	private String shareTitle;
	private String shareUrl;

	public Blog() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public List<BlogPic> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<BlogPic> attachList) {
		this.attachList = attachList;
	}

	public BlogDetail getArticleDetail() {
		return articleDetail;
	}

	public void setArticleDetail(BlogDetail articleDetail) {
		this.articleDetail = articleDetail;
	}

	public List<BlogComment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<BlogComment> commentList) {
		this.commentList = commentList;
	}

	public List<BlogLike> getLikeList() {
		return likeList;
	}

	public void setLikeList(List<BlogLike> likeList) {
		this.likeList = likeList;
	}

	public boolean isShowDelete() {
		return showDelete;
	}

	public void setShowDelete(boolean showDelete) {
		this.showDelete = showDelete;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getShareDescription() {
		return shareDescription;
	}

	public void setShareDescription(String shareDescription) {
		this.shareDescription = shareDescription;
	}

	public String getShareImgUrl() {
		return shareImgUrl;
	}

	public void setShareImgUrl(String shareImgUrl) {
		this.shareImgUrl = shareImgUrl;
	}

	public String getShareTitle() {
		return shareTitle;
	}

	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

}
