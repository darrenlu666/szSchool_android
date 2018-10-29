package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 联系人组实体类
 * 
 * @author 周锋
 * @date 2016年1月28日 下午2:17:27
 * @ClassInfo com.dt5000.ischool.entity.GroupItem
 * @Description
 */
public class GroupItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String groupCount;
	private String groupDesc;
	private String groupId;
	private String groupName;
	private String groupType;
	private boolean choose =false;

	public boolean isChoose() {
		return choose;
	}

	public void setChoose(boolean choose) {
		this.choose = choose;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public GroupItem() {
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
