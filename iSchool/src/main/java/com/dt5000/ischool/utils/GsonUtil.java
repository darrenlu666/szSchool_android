package com.dt5000.ischool.utils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	/**
	 * Type属于java.lang.reflect.Type<br>
	 * Type listType = new TypeToken<List<object>>() {}.getType()
	 */
	public static List<?> jsonToList(String json, Type listType) {
		List<?> resultList = null;
		if (!CheckUtil.stringIsBlank(json)) {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			resultList = gson.fromJson(json, listType);
		}
		return resultList;
	}

	/**
	 * Type属于java.lang.reflect.Type<br>
	 * Type Type type = new TypeToken<Map<object, object>>(){}.getType()
	 */
	public static Map<Integer, String> jsonToMap(String json, Type mapType) {
		Map<Integer, String> resultMap = null;
		try {
			if (!CheckUtil.stringIsBlank(json)) {
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				resultMap = gson.fromJson(json, mapType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 将json转换成bean对象
	 */
	public static Object jsonToBean(String jsonStr, Class<?> cl) {
		Object obj = null;
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		obj = gson.fromJson(jsonStr, cl);
		return obj;
	}

}
