package com.dt5000.ischool.entity;

/**
 * 请求参数中包含的公共参数对象，用于生成json字符串参数
 * 
 * @author 周锋
 * @Date 2014-5-19
 * @ClassInfo com.dt5000.ischool.entity-PublicParams.java
 * @Description
 */
public class PublicJson {
	/** cookie/deviceId */
	private String cookie;
	/** productId/pushType */
	private String productId;
	private String productVersion;
	private String channelId;
	private String userId;
	private String network;
	/** deviceToken/baiduId */
	private String deviceToken;
	private String time;
	private String sign;
	private String display;

	public PublicJson() {
	}

	public PublicJson(String cookie, String productId, String productVersion,
			String channelId, String userId, String network,
			String deviceToken, String time, String sign, String display) {
		this.cookie = cookie;
		this.productId = productId;
		this.productVersion = productVersion;
		this.channelId = channelId;
		this.userId = userId;
		this.network = network;
		this.deviceToken = deviceToken;
		this.time = time;
		this.sign = sign;
		this.display = display;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}
