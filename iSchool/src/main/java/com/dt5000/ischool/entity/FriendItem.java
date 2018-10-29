package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 联系人实体类
 * 
 * @author 周锋
 * @date 2016年1月28日 下午2:17:51
 * @ClassInfo com.dt5000.ischool.entity.FriendItem
 * @Description
 */
public class FriendItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String friendDesc;
	private String friendIcon;
	private String friendId;
	private String friendName;
	private String friendPhone;
	private String friendRole;
	private String friendSubjectName;

	public FriendItem() {
	}

	public String getFriendDesc() {
		return friendDesc;
	}

	public void setFriendDesc(String friendDesc) {
		this.friendDesc = friendDesc;
	}

	public String getFriendIcon() {
		return friendIcon;
	}

	public void setFriendIcon(String friendIcon) {
		this.friendIcon = friendIcon;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getFriendPhone() {
		return friendPhone;
	}

	public void setFriendPhone(String friendPhone) {
		this.friendPhone = friendPhone;
	}

	public String getFriendRole() {
		return friendRole;
	}

	public void setFriendRole(String friendRole) {
		this.friendRole = friendRole;
	}

	public String getFriendSubjectName() {
		return friendSubjectName;
	}

	public void setFriendSubjectName(String friendSubjectName) {
		this.friendSubjectName = friendSubjectName;
	}

}
