package com.dt5000.ischool.utils.httpclient;

import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.MLog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.List;

/**
 * Apache HttpClient发送请求的辅助类
 *
 * @author 周锋
 * @date 2016年1月18日 下午2:24:58
 * @ClassInfo com.dt5000.ischool.utils.httpclient.HttpClientUtil
 * @Description
 */
public class HttpClientUtil {

    /**
     * 发送post请求
     *
     * @param url
     * @param params 已经封装好的NameValuePair数组
     * @return
     * @throws Exception
     */
    public static String doPost(String url, List<NameValuePair> params) throws Exception {
        String result = "";

        HttpClient httpClient = CustomHttpClient.getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        // 添加请求参数到请求对象
        if (params != null && !params.isEmpty()) {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        // 发送请求并等待响应
        HttpResponse response = httpClient.execute(httpPost);

        // 判断状态码为200
        if (null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 读取返回数据
            result = EntityUtils.toString(response.getEntity());
            httpPost.abort();
            MLog.i("状态码：" + response.getStatusLine().getStatusCode());
        } else {
            throw new RuntimeException("do post error: " + url);
        }

        return result;
    }

    /**
     * 发送post请求，带多个文件
     *
     * @param url      请求的地址，已经将字符串参数拼接进其中
     * @param fileList 文件列表
     * @return
     * @throws Exception
     */
    public static String doPostWithFiles(String url, List<File> fileList)
            throws Exception {
        String result = "";

        HttpClient httpClient = CustomHttpClient.getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        MultipartEntity entity = new MultipartEntity();
        for (File file : fileList) {
            entity.addPart(file.toString(), new FileBody(file));
        }
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity());
            httpPost.abort();
        }

        return result;
    }

    /**
     * 发送post请求，带单个文件，将文件转成字节流发送
     *
     * @param url
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static String doPostWithSingleFile(String url, String filePath) throws Exception {
        String result = "";

        try {
            HttpClient httpClient = CustomHttpClient.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);

            // 将文件转成字节流放在ByteArrayEntity参数中
            if (!CheckUtil.stringIsBlank(filePath)) {
                ByteArrayEntity arrayEntity = new ByteArrayEntity(FileUtil.readFromSD(filePath));
                arrayEntity.setContentType("application/octet-stream");
                httpPost.setEntity(arrayEntity);
            }

            HttpResponse response = httpClient.execute(httpPost);
            MLog.i("http响应：" + response.getStatusLine().getStatusCode());

            if (null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
                httpPost.abort();
            } else {
                throw new RuntimeException("do post error: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 发送get请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception {
        String result = "";

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = CustomHttpClient.getHttpClient();

        HttpResponse response = httpClient.execute(httpGet);
        if (response != null
                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity());
            httpGet.abort();
        } else {
            throw new RuntimeException("do get error: " + url);
        }

        return result;
    }

}
