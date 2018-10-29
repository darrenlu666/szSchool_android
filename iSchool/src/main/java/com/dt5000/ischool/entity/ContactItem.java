package com.dt5000.ischool.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 联系人类别分组实体类
 * 
 * @author 周锋
 * @date 2016年1月28日 下午3:44:45
 * @ClassInfo com.dt5000.ischool.entity.ContactItem
 * @Description
 */
public class ContactItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String contactCount;
	private String contactId;
	private String contactName;
	private List<FriendItem> friendList;
	private List<GroupItem> groupList;

	/**
	 * 0老师，1学生，2班级，3年级	
	 */
	private String type;

	public ContactItem() {
	}

	public List<GroupItem> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<GroupItem> groupList) {
		this.groupList = groupList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContactCount() {
		return contactCount;
	}

	public void setContactCount(String contactCount) {
		this.contactCount = contactCount;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public List<FriendItem> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<FriendItem> friendList) {
		this.friendList = friendList;
	}
}
