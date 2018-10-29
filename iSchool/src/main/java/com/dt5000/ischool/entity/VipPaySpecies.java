package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * VIP套餐实体类
 *
 * @author 周锋
 * @Date 2014年9月29日
 * @ClassInfo com.dt5000.ischool.entity.VipSpecies.java
 * @Description
 */
public class VipPaySpecies implements Serializable {

    private static final long serialVersionUID = 1L;
    private String goodsName;
    private String price;
    private String goodsId;
    private String orderId;
    private String orderNo;
    private boolean isFromDetails;

    public VipPaySpecies(String goodsName, String goodsId, String price) {
        this.goodsName = goodsName;
        this.goodsId = goodsId;
        this.price = price;
    }

    public VipPaySpecies(String goodsName, String price, String goodsId, String orderId, String orderNo) {
        this.goodsName = goodsName;
        this.price = price;
        this.goodsId = goodsId;
        this.orderId = orderId;
        this.orderNo = orderNo;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isFromDetails() {
        return isFromDetails;
    }

    public void setFromDetails(boolean fromDetails) {
        isFromDetails = fromDetails;
    }
}
