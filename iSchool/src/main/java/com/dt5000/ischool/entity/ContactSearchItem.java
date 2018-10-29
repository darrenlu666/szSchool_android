package com.dt5000.ischool.entity;

/**
 * 联系人搜索实体类
 * 
 * @author 周锋
 * @date 2017年3月17日 下午2:23:49
 * @ClassInfo com.dt5000.ischool.entity.ContactSearchItem
 * @Description
 */
public class ContactSearchItem {

	private String name;
	private String type;
	private String id;
	private String phone;
	private String role;

	public ContactSearchItem() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
