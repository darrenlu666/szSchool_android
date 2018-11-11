package com.dt5000.ischool.utils.httpclient;

import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.dt5000.ischool.activity.media.bean.MMImageBean;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.MLog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
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

    public static String doPostWithFiles(String url, List<File> fileList)
            throws Exception {
        String result = "";

        HttpClient httpClient = CustomHttpClient.getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        MultipartEntity entity = new MultipartEntity();
        for (File file : fileList) {
            entity.addPart("file", new FileBody(file));
        }

        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "Multipart/form-data");

        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity());
            httpPost.abort();
        }

        return result;
    }

    /**
     * @param uploadUrl          上传路径参数
     * @param uploadFilePathList 文件路径
     * @category 上传文件至Server的方法
     * @author ylbf_dev
     */
    public static String uploadFile(String uploadUrl, List<String> uploadFilePathList) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            for (String filePath : uploadFilePathList) {
                DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"test.jpg\"" + end);
//          dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
//                  + uploadFilePath.substring(uploadFilePath.lastIndexOf("/") + 1) + "\"" + end);
                dos.writeBytes(end);
                // 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[8192]; // 8k
                int count = 0;
                while ((count = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, count);

                }
                fis.close();
                System.out.println("file send to server............");
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();
                dos.close();
            }


            // 读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            is.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "error";
    }


    public static String upload(List<ImageMedia> uploadFiles, String actionUrl) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 发送POST请求必须设置如下两行
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream ds =
                    new DataOutputStream(con.getOutputStream());
            for (int i = 0; i < uploadFiles.size(); i++) {
                String uploadFile = uploadFiles.get(i).getPath();
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"test.jpg\"" + end);
                ds.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();

            // 读取服务器返回结果
            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            is.close();
            ds.close();
            return result;
        } catch (Exception e) {
        }
        return "error";
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
