package com.dt5000.ischool.net.interfaces;

import com.dt5000.ischool.entity.PayDetails;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;


/**
 * Created by weimy on 2017/9/29.
 */

public interface ApiManager {
    @POST
    Call<ResponseBody> upload(@Url() String url);

    //上传文件
    @POST
    Call<ResponseBody> uploadFile(@Url() String url, @Body RequestBody body);

    //修改手机号
    @POST
    Call<ResponseBody> changePhone(@Url() String url);

    //订单详情
    @POST
    Observable<PayDetails> orderDetatls(@Url() String url);

    //通知已读
    @POST
    Call<ResponseBody> notityReader(@Url() String url);
}
