package com.dt5000.ischool.net;

import android.content.Context;
import android.content.SharedPreferences;

import com.dt5000.ischool.entity.PublicJson;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DeviceUuidFactory;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.utils.encrypt.Md5Encrypt;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 发送http请求时拼接参数的辅助类
 *
 * @author 周锋
 * @date 2016年1月18日 下午1:59:05
 * @ClassInfo com.dt5000.ischool.net.UrlBulider
 * @Description
 */
public class UrlBulider {

    /**
     * 生成post请求键值对参数
     *
     * @param operationMap
     * @param context
     * @param userId       首次登录时userId=""
     * @return
     */
    public static Map<String, String> getMapParams(
            Map<String, String> operationMap, Context context, String userId) {
        // 获取设备相关信息
        String deviceId = null, versionName = null;
        try {
            DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
            deviceId = deviceUuidFactory.getDeviceUuid().toString();
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取推送用到的DeviceToken
        SharedPreferences sp = context.getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);
        String JPushRegistrationId = sp.getString("JPushRegistrationId", "");
        if (CheckUtil.stringIsBlank(JPushRegistrationId)) {
            JPushRegistrationId = JPushInterface.getRegistrationID(context);
        }
        MLog.i("本机的极光JPushRegistrationId: " + JPushRegistrationId);

        // 拼接逻辑参数部分operation
        Gson gs = new Gson();
        String operationJson = gs.toJson(operationMap);

        // 时间校验
        String curTime = String.valueOf(System.currentTimeMillis());

        // 拼接公共参数部分public
        String sign = Md5Encrypt.md5(operationJson + curTime
                + UrlProtocol.SignKey, "utf-8");
        PublicJson publicJs = new PublicJson();
        publicJs.setCookie(deviceId);
        publicJs.setProductId("android");
        publicJs.setProductVersion(versionName);
        publicJs.setDeviceToken(JPushRegistrationId);
        publicJs.setTime(curTime);
        publicJs.setSign(sign);
        publicJs.setUserId(userId);
        String publicJson = gs.toJson(publicJs);

        // 生成httpPost参数requestData
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("operation", operationJson);
        requestMap.put("public", publicJson);
        String requestJson = gs.toJson(requestMap);

        // 生成httpPost参数session
        String session = Md5Encrypt.md5(requestJson + UrlProtocol.SessionKey,
                "utf-8");

        // 添加httpPost参数requestData和session
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("requestData", requestJson);
        paramMap.put("session", session);

        return paramMap;
    }

    /**
     * 生成post请求参数集合：使用HttpClient
     *
     * @param operationMap
     * @param context
     * @param userId       首次登录时userId=""
     * @return
     */
    public static List<NameValuePair> getHttpParams(
            Map<String, String> operationMap, Context context, String userId) {
        // 获取设备相关信息
        String deviceId = null, versionName = null;
        try {
            DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
            deviceId = deviceUuidFactory.getDeviceUuid().toString();
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取推送用到的DeviceToken
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String JPushRegistrationId = sp.getString("JPushRegistrationId", "");
        if (CheckUtil.stringIsBlank(JPushRegistrationId)) {
            JPushRegistrationId = JPushInterface.getRegistrationID(context);
        }
        MLog.i("本机的极光JPushRegistrationId: " + JPushRegistrationId);//1104a8979292cb1bd87

        // 拼接逻辑参数部分operation
        Gson gs = new Gson();
        String operationJson = gs.toJson(operationMap);

        // 时间校验
        String curTime = String.valueOf(System.currentTimeMillis());

        // 拼接公共参数部分public
        String sign = Md5Encrypt.md5(operationJson + curTime + UrlProtocol.SignKey, "utf-8");
        PublicJson publicJs = new PublicJson();
        publicJs.setCookie(deviceId);
        publicJs.setProductId("android");
        publicJs.setProductVersion(versionName);
        publicJs.setDeviceToken(JPushRegistrationId);
        publicJs.setTime(curTime);
        publicJs.setSign(sign);
        publicJs.setUserId(userId);
        String publicJson = gs.toJson(publicJs);

        // 生成httpPost参数requestData
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("operation", operationJson);
        requestMap.put("public", publicJson);
        String requestJson = gs.toJson(requestMap);
        // 生成httpPost参数session
        String session = Md5Encrypt.md5(requestJson + UrlProtocol.SessionKey, "utf-8");

        // 添加httpPost参数requestData和session
        List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("requestData", requestJson));
        httpParams.add(new BasicNameValuePair("session", session));

        return httpParams;
    }

    /**
     * 生成post请求参数集合：使用Afinal
     *
     * @param operationMap
     * @param context
     * @param userId       首次登录时userId=""
     * @return
     */
    public static AjaxParams getAjaxParams(Map<String, String> operationMap, Context context, String userId) {
        List<NameValuePair> httpParams = getHttpParams(operationMap, context, userId);
        AjaxParams ajaxParams = new AjaxParams();
        for (NameValuePair nvp : httpParams) {
            String name = nvp.getName();
            String value = nvp.getValue();
            ajaxParams.put(name, value);
        }

        return ajaxParams;
    }

    /**
     * 生成get请求地址
     *
     * @param operationMap
     * @param context
     * @param userId       首次登录时userId=""
     * @return
     */
    public static String getHttpURL(Map<String, String> operationMap,
                                    Context context, String userId) {
        // 获取设备相关信息
        String deviceId = null, versionName = null;
        try {
            DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
            deviceId = deviceUuidFactory.getDeviceUuid().toString();
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取推送用到的DeviceToken
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String JPushRegistrationId = sp.getString("JPushRegistrationId", "");
        if (CheckUtil.stringIsBlank(JPushRegistrationId)) {
            JPushRegistrationId = JPushInterface.getRegistrationID(context);
        }

        // 拼接逻辑参数部分operation
        Gson gs = new Gson();
        String operationJson = gs.toJson(operationMap);

        // 时间校验
        String curTime = String.valueOf(System.currentTimeMillis());
        // 拼接公共参数部分public
        String sign = Md5Encrypt.md5(operationJson + curTime + UrlProtocol.SignKey, "utf-8");
        PublicJson publicJs = new PublicJson();
        publicJs.setCookie(deviceId);
        publicJs.setProductId("android");
        publicJs.setProductVersion(versionName);
        publicJs.setDeviceToken(JPushRegistrationId);
        publicJs.setTime(curTime);
        publicJs.setSign(sign);
        publicJs.setUserId(userId);
        String publicJson = gs.toJson(publicJs);

        // 生成httpPost参数requestData
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("operation", operationJson);
        requestMap.put("public", publicJson);
        String requestJson = gs.toJson(requestMap);
        // 生成httpPost参数session
        String session = Md5Encrypt.md5(requestJson + UrlProtocol.SessionKey, "utf-8");

        String encodeRequestJson = URLEncoder.encode(requestJson);
        String encodeSession = URLEncoder.encode(session);
        // 添加httpGet参数requestData和session
        String url = UrlProtocol.MAIN_HOST + "?requestData="
                + encodeRequestJson + "&session=" + encodeSession;

        return url;
    }


    public static String getHttpParamss(Map<String, String> operationMap, Context context, String userId) {
        // 获取设备相关信息
        String deviceId = null, versionName = null;
        try {
            DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
            deviceId = deviceUuidFactory.getDeviceUuid().toString();
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取推送用到的DeviceToken
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String JPushRegistrationId = sp.getString("JPushRegistrationId", "");
        if (CheckUtil.stringIsBlank(JPushRegistrationId)) {
            JPushRegistrationId = JPushInterface.getRegistrationID(context);
        }

        //
        Gson gson = new Gson();
        String operationJson = gson.toJson(operationMap);

        // 时间校验
        String curTime = String.valueOf(System.currentTimeMillis());
        String sign = Md5Encrypt.md5(operationJson + curTime + UrlProtocol.SignKey, "utf-8");
        PublicJson publicJs = new PublicJson();
        publicJs.setCookie(deviceId);
        publicJs.setProductId("android");
        publicJs.setProductVersion(versionName);
        publicJs.setDeviceToken(JPushRegistrationId);
        publicJs.setTime(curTime);
        publicJs.setSign(sign);
        publicJs.setUserId(userId);
        String publicJson = gson.toJson(publicJs);

        // 生成httpPost参数requestData
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("operation", operationJson);
        requestMap.put("public", publicJson);
        String requestJson = gson.toJson(requestMap);


        String session = Md5Encrypt.md5(requestJson + UrlProtocol.SessionKey, "utf-8");
        String encodeRequestJson = URLEncoder.encode(requestJson);
        String encodeSession = URLEncoder.encode(session);

        String url = UrlProtocol.MAIN_HOST  + "?requestData="
                + encodeRequestJson + "&session=" + encodeSession;
        return url;
    }
}
