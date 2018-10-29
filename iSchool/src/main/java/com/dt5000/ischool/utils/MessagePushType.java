package com.dt5000.ischool.utils;

/**
 * 标识推送消息的类型 0-个人消息，1-班级消息，2-个人成绩，3-作业，4-请假
 * 
 * @author 周锋
 * @date 2016年3月28日 上午10:20:23
 * @ClassInfo com.dt5000.ischool.utils.MessagePushType
 * @Description
 */
public enum MessagePushType {

	Personal("0"), Clazz("1"), Score("2"), Homework("3"), Vacate("4"), Group("5");

	private String type;

	private MessagePushType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.type;
	}

}