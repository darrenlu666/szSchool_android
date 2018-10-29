package com.dt5000.ischool.entity;

/**
 * 用户VIP信息实体类
 * 
 * @author 周锋
 * @Date 2014年10月3日
 * @ClassInfo com.dt5000.ischool.entity.VipInfo.java
 * @Description
 */
public class VipInfo {

	private String goodsName;
	private String price;
	private String goodsId;
	private String startDate;
	private String startVip;
	private int vipStatus;
	private int remainDays;
	private String endDate;

	public VipInfo() {
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public int getVipStatus() {
		return vipStatus;
	}

	public void setVipStatus(int vipStatus) {
		this.vipStatus = vipStatus;
	}

	public int getRemainDays() {
		return remainDays;
	}

	public void setRemainDays(int remainDays) {
		this.remainDays = remainDays;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartVip() {
		return startVip;
	}

	public void setStartVip(String startVip) {
		this.startVip = startVip;
	}

}
