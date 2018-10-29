package com.dt5000.ischool.net;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dt5000.ischool.entity.ClassMessageSend;
import com.dt5000.ischool.entity.GroupMessageSend;
import com.dt5000.ischool.entity.PayDetails;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.RequestMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.interfaces.ApiManager;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.HelpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weimy on 2017/12/18.
 */
public class RetrofitService {
    private static ApiManager apiManager = null;
    private static RequestBody requestBody = null;
    private static Call<ResponseBody> call = null;


    public static void init() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(UrlProtocol.MAIN_HOST + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiManager = retrofit.create(ApiManager.class);
    }


    private static Map<String, String> fileMap(GroupMessageSend groupMessageSend, String sendSMS, String pic) {
        Gson gson = new GsonBuilder().create();
        String jsonMessage = gson.toJson(groupMessageSend);

        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_SEND_GROUP_MSG);
        map.put("jsonMessage", jsonMessage);
        map.put("sendMsg", sendSMS);
        if (!CheckUtil.stringIsBlank(pic)) {
            String subffix = pic.substring(pic.lastIndexOf(".") + 1, pic.length());
            map.put("subffix", subffix);
        }

        return map;
    }

    private static Map<String, String> fileMappostFilesMapClassMessageSend(ClassMessageSend classMessageSend, String sendSMS, String pic) {
        Gson gson = new GsonBuilder().create();
        String jsonMessage = gson.toJson(classMessageSend);

        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_SEND_CLASS_MSG);
        map.put("jsonMessage", jsonMessage);
        map.put("sendMsg", sendSMS);
        if (!CheckUtil.stringIsBlank(pic)) {
            String subffix = pic.substring(pic.lastIndexOf(".") + 1, pic.length());
            map.put("subffix", subffix);
        }

        return map;
    }

    private static Map<String, String> fileMappostFilesMapPersonMessage(PersonMessage personMessage, String sendSMS, String pic) {
        Gson gson = new GsonBuilder().create();
        String jsonMessage = gson.toJson(personMessage);

        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_SEND_PERSON_MSG);
        map.put("jsonMessage", jsonMessage);
        map.put("sendMsg", sendSMS);
        if (!CheckUtil.stringIsBlank(pic)) {
            String subffix = pic.substring(pic.lastIndexOf(".") + 1, pic.length());
            map.put("subffix", subffix);
        }

        return map;
    }

    public static List<Map<String, String>> postFilesMap(GroupMessageSend groupMessageSend, String sendSMS, List<String> images, boolean isHaveContent) {

        List<Map<String, String>> mapList = new ArrayList<>();

        if (images == null) {
            mapList.add(fileMap(groupMessageSend, sendSMS, null));
        } else {
            if (images.size() == 1) {
                mapList.add(fileMap(groupMessageSend, sendSMS, images.get(0)));
            } else {
                if (isHaveContent) {
                    mapList.add(fileMap(groupMessageSend, sendSMS, null));
                }
                for (int i = 0; i < images.size(); i++) {
                    groupMessageSend.setContent("");
                    mapList.add(fileMap(groupMessageSend, sendSMS, images.get(i)));
                }
            }
        }
        return mapList;
    }

    public static List<Map<String, String>> postFilesMapClassMessageSend(ClassMessageSend classMessageSend, String sendSMS, List<String> images, boolean isHaveContent) {

        List<Map<String, String>> mapList = new ArrayList<>();

        if (images == null) {
            mapList.add(fileMappostFilesMapClassMessageSend(classMessageSend, sendSMS, null));
        } else {
            if (images.size() == 1) {
                mapList.add(fileMappostFilesMapClassMessageSend(classMessageSend, sendSMS, images.get(0)));
            } else {
                if (isHaveContent) {
                    mapList.add(fileMappostFilesMapClassMessageSend(classMessageSend, sendSMS, null));
                }
                for (int i = 0; i < images.size(); i++) {
                    classMessageSend.setContent("");
                    mapList.add(fileMappostFilesMapClassMessageSend(classMessageSend, sendSMS, images.get(i)));
                }
            }
        }
        return mapList;
    }

    public static List<Map<String, String>> postFilesMapPersonMessage(PersonMessage personMessage, String sendSMS, List<String> images, boolean isHaveContent) {

        List<Map<String, String>> mapList = new ArrayList<>();

        if (images == null) {
            mapList.add(fileMappostFilesMapPersonMessage(personMessage, sendSMS, null));
        } else {
            if (images.size() == 1) {
                mapList.add(fileMappostFilesMapPersonMessage(personMessage, sendSMS, images.get(0)));
            } else {
                if (isHaveContent) {
                    mapList.add(fileMappostFilesMapPersonMessage(personMessage, sendSMS, null));
                }
                for (int i = 0; i < images.size(); i++) {
                    personMessage.setContent("");
                    mapList.add(fileMappostFilesMapPersonMessage(personMessage, sendSMS, images.get(i)));
                }
            }
        }
        return mapList;
    }

    public static void postFiles(List<String> images, List<Map<String, String>> mapList, Context context, User user, boolean isHaveContent, Handler handler) {
        if (images == null) {
            call = apiManager.upload(UrlBulider.getHttpParamss(mapList.get(0), context, user.getUserId()));
            call(handler);
        } else {
            if (images.size() == 1) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(images.get(0)));
                call = apiManager.uploadFile(UrlBulider.getHttpParamss(mapList.get(0), context, user.getUserId()), requestBody);
                call(handler);
            } else {
                if (isHaveContent) {
                    call = apiManager.upload(UrlBulider.getHttpParamss(mapList.get(0), context, user.getUserId()));
                    call(handler);
                }

                for (int i = 0; i < images.size(); i++) {
                    requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(images.get(i)));
                    int j = i;
                    if (isHaveContent) {
                        j += 1;
                    }
                    call = apiManager.uploadFile(UrlBulider.getHttpParamss(mapList.get(j), context, user.getUserId()), requestBody);
                    call(handler);
                }
            }
        }
    }

    private static void call(final Handler handler) {
        final Message message = new Message();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    Log.i("onResponse", "onResponse:" + json);
                    RequestMessage requestMessage = new Gson().fromJson(json, RequestMessage.class);
                    if (requestMessage.getResultStatus().equals("200")) {
                        message.what = FlagCode.SUCCESS;
                    } else {
                        message.what = FlagCode.FAIL;
                    }
                    handler.sendMessage(message);
                    call.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("onFailure", "onFailure" + t.getMessage());
                message.what = FlagCode.FAIL;
                handler.sendMessage(message);
            }
        });
    }

    public static void changeUserPhone(final Context context, final User user, final String newPhone, final Handler handler) {
        Log.i("changeUserPhone", user.getRole() + "---"+user.getUserId());
        call = apiManager.changePhone(HelpUtils.changePhone((Activity) context, user.getUserId(), User.isTeacherRole(user.getRole()) ? "1" : "0", newPhone));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    Log.i("onResponse", "onResponse:" + json);
                    RequestMessage requestMessage = new Gson().fromJson(json, RequestMessage.class);
                    if (requestMessage.getResultStatus().equals("200")) {
                        handler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("onFailure", "onFailure" + t.getMessage());
            }
        });
    }

    public static rx.Observable<PayDetails> payOrder(Context context, String userId, int pageSize, int pageNum) {
        return apiManager.orderDetatls(HelpUtils.payOrder((Activity) context, userId, pageSize, pageNum))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //通知已读
    public static void notityReader(final Context context, String sendUserId, String receiveUserId, final Handler handler) {
        call = apiManager.notityReader(HelpUtils.notityReader((Activity) context, sendUserId, receiveUserId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String json = response.body().string();
                        Log.i("notityReader", "onResponse:" + json);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("notityReader", "onFailure" + t.getMessage());
            }
        });
    }
}
