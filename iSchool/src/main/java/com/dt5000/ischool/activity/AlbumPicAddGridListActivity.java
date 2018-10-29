package com.dt5000.ischool.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.AlbumPicAddGridListAdapter;
import com.dt5000.ischool.entity.AlbumItem;
import com.dt5000.ischool.entity.AlbumUploadChoosePic;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 班级相册上传图片页面
 *
 * @author 周锋
 * @date 2016年2月3日 下午2:59:47
 * @ClassInfo com.dt5000.ischool.activity.AlbumPicAddGridListActivity
 * @Description
 */
public class AlbumPicAddGridListActivity extends Activity {
    private String tag = AlbumPicAddGridListActivity.class.getSimpleName();
    private Subscription subscription = null;

    private static final int PERMISSION_REQUEST_CODE = 0X00000011;

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_topbar_btn;
    private GridView grid_album;

    private AlbumPicAddGridListAdapter albumPicAddGridListAdapter;
    private List<AlbumUploadChoosePic> picList;
    private ProgressDialog progressDialog;
    private User user;
    private AlbumItem albumItem;
    private FinalHttp finalHttp;
    private String token = "";// 上传图片时用到的Token
    private UploadManager uploadManager;
    private boolean isUploadCancelled = false;
    private String resourceIds = "";// 上传所有图片到七牛服务器后，返回的图片id拼接的字符串
    private List<AlbumUploadChoosePic> currentPicList;// 当前需要加载的图片列表
    private int lastVisibleItem = 0;// 当前GridView最后一个可见项的索引
    private int currentIndex = 0;// 标识当前加载到第几张图片
    private boolean hasMorePic = true;// 标识是否有更多图片供加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_pic_add_grid_list);

        initView();
        initListener();
        init();
        checkPermissionAndLoadImages();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("添加图片");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("上传");
        grid_album = (GridView) findViewById(R.id.grid_album);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumPicAddGridListActivity.this.finish();
            }
        });

        // 点击上传图片
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });

        // 相册活动到底部加载更多
        grid_album.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE
                        && lastVisibleItem == (albumPicAddGridListAdapter
                        .getCount() - 1)) {

                    if (hasMorePic) {
                        // 再次加载40张图片
                        loadPage();

                        // 更新适配器
                        albumPicAddGridListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    private void init() {
        albumItem = (AlbumItem) getIntent().getExtras().getSerializable("albumItem");
        MLog.i("在相册 " + albumItem.getAlbumName() + " 中上传图片，相册id为" + albumItem.getAlbumId());

        user = User.getUser(this);

        finalHttp = new FinalHttp();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isUploadCancelled = true;
            }
        });

        currentPicList = new ArrayList<AlbumUploadChoosePic>();
    }

    /**
     * 每次加载40张图
     */
    private void loadPage() {
        // 从上次记录的下标开始
        for (int i = currentIndex; i < picList.size(); i++) {
            hasMorePic = false;
            currentPicList.add(picList.get(i));

            if ((i + 1) % 40 == 0) {
                currentIndex = i + 1;// 记录当前加载到第几张图片
                hasMorePic = true;
                break;
            }
        }
    }

    /**
     * 筛选图片
     */
    private void choosePic() {
        if (picList != null && picList.size() > 0
                && albumPicAddGridListAdapter != null) {
            List<AlbumUploadChoosePic> choosedPicList = albumPicAddGridListAdapter
                    .getChoosedPicList();

            // 将未选择的图片从列表中移除
            List<String> newPicList = new ArrayList<String>();
            for (int i = 0; i < choosedPicList.size(); i++) {
                AlbumUploadChoosePic pic = choosedPicList.get(i);
                boolean isChoosed = pic.isChoose();
                if (isChoosed) {
                    newPicList.add(pic.getPicPath());
                }
            }

            // 根据最终选定的图片上传
            final List<String> uploadPicList = newPicList;
            if (newPicList == null || newPicList.size() <= 0) {
                DialogAlert.show(AlbumPicAddGridListActivity.this, "请选择图片");
            } else {
                MLog.i("选择后的图片数量：" + newPicList.size());
                new AlertDialog.Builder(AlbumPicAddGridListActivity.this)
                        .setMessage("已选择" + newPicList.size() + "张图片，是否上传？")
                        .setPositiveButton("上传",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 获取七牛token
                                        requestToGetToken(uploadPicList);
                                    }
                                }).setNegativeButton("取消", null).show();
            }
        }
    }

    /**
     * 上传图片时需要获取Token
     *
     * @param uploadPicList
     */
    private void requestToGetToken(final List<String> uploadPicList) {
        // 封装参数
        Map<String, String> operationMap = new HashMap<String, String>();
        operationMap.put("operationType",
                UrlProtocol.OPERATION_TYPE_UPLOAD_PIC_TOKEN);
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(operationMap, this,
                user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onStart() {
                        progressDialog.setMessage("获取验证...");
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        MLog.i("上传图片前获取Token返回结果：" + t);
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String returnToken = jsonObject.optString("token");

                            if (!CheckUtil.stringIsBlank(returnToken)) {
                                token = returnToken;
                                uploadToQiniu(uploadPicList);
                            } else {
                                progressDialog.dismiss();
                                DialogAlert.show(
                                        AlbumPicAddGridListActivity.this, "目前无法上传图片");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            DialogAlert.show(AlbumPicAddGridListActivity.this, "目前无法上传图片");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        progressDialog.dismiss();
                        DialogAlert.show(AlbumPicAddGridListActivity.this,
                                "目前无法上传图片");
                    }
                });
    }

    /**
     * 将图片上传到七牛服务器
     *
     * @param uploadPicList
     */
    private void uploadToQiniu(List<String> uploadPicList) {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }

        // 取消上传标识默认
        isUploadCancelled = false;

        // 生成所有要上传的文件
        List<File> multiFileList = new ArrayList<File>();
        for (int i = 0; i < uploadPicList.size(); i++) {
            File file = new File(uploadPicList.get(i));
            multiFileList.add(file);
        }
        MLog.i("共需上传图片的数量：" + multiFileList.size());

        recursiveUpload(multiFileList, 0, multiFileList.size());
    }

    /**
     * 将文件列表逐一上传到七牛服务
     *
     * @param list       文件列表集合
     * @param index      当前递归层级
     * @param totalCount 文件总数量
     */
    private void recursiveUpload(List<File> list, int index,
                                 final int totalCount) {
        final int currentCount = index;
        final List<File> picFileList = list;
        MLog.i("开始上传第" + (currentCount + 1) + "张图片--------------------------");

        uploadManager.put(picFileList.get(0), null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info,
                                         JSONObject response) {
                        if (info.isOK()) {
                            MLog.i("上传第" + (currentCount + 1) + "张图片成功--------------------------");
                            String resource = response.optString("resource");
                            resourceIds += resource + ",";
                            MLog.i("当前resourceIds: " + resourceIds);

                            // 上传成功后将本次上传的图片从列表中移除
                            picFileList.remove(0);
                            if (picFileList.size() <= 0) {// 已全部上传完毕
                                resourceIds = resourceIds.substring(0,
                                        resourceIds.lastIndexOf(","));
                                MLog.i("图片全部上传完毕，resourceIds: " + resourceIds);

                                MLog.i("开始发送请求给后台发表图片...");
                                uploadHandler.sendEmptyMessage(1);
                                requestToPublish();
                            } else {// 还有剩余图片需要上传
                                int nextIndex = currentCount;
                                nextIndex++;
                                recursiveUpload(picFileList, nextIndex,
                                        totalCount);
                            }
                        } else if (info.isCancelled()) {
                            MLog.i("用户取消上传...");
                            resourceIds = "";
                        } else {
                            progressDialog.dismiss();
                            resourceIds = "";
                            DialogAlert.show(AlbumPicAddGridListActivity.this,
                                    "目前无法上传图片");
                        }
                    }

                }, new UploadOptions(null, null, false,
                        new UpProgressHandler() {
                            @Override
                            public void progress(String key, double percent) {
                                MLog.i("当前进度: " + percent);
                                Message message = new Message();
                                message.what = 0;
                                message.arg1 = currentCount + 1;
                                message.arg2 = totalCount;
                                message.obj = percent;
                                uploadHandler.sendMessage(message);
                            }
                        }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return isUploadCancelled;
                    }
                }));
    }

    @SuppressLint("HandlerLeak")
    private Handler uploadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    int index = msg.arg1;
                    int total = msg.arg2;
                    progressDialog.setMessage("正在上传图片    " + index + " / " + total);
                    break;
                case 1:
                    progressDialog.setMessage("正在添加图片...");
                    break;
            }
        }

        ;
    };

    /**
     * 发请求把所有七牛的图片id传给后台
     */
    private void requestToPublish() {
        // 封装参数
        Map<String, String> operationMap = new HashMap<String, String>();
        operationMap.put("operationType",
                UrlProtocol.OPERATION_TYPE_ALBUMS_UPLOAD_PIC);
        operationMap.put("worksId", String.valueOf(albumItem.getAlbumId()));
        operationMap.put("role", String.valueOf(user.getRole()));
        operationMap.put("realName", user.getRealName());
        operationMap.put("resourceIds", resourceIds);
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(operationMap, this,
                user.getUserId());
        MLog.i("上传图片url: "
                + UrlBulider.getHttpURL(operationMap, this, user.getUserId()));

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("上传图片返回结果：" + t);
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String resultStatus = jsonObject
                                    .optString("resultStatus");

                            if ("200".equals(resultStatus)) {// 上传成功
                                boolean verify = jsonObject
                                        .optBoolean("verify");
                                if (verify) {// 教师端上传后默认审核通过
                                    MToast.show(
                                            AlbumPicAddGridListActivity.this,
                                            "上传成功", MToast.SHORT);
                                    MLog.i("上传图片成功，返回上一页面时需刷新页面");
                                    AlbumPicAddGridListActivity.this
                                            .setResult(RESULT_OK);
                                    AlbumPicAddGridListActivity.this.finish();
                                } else {// 学生端上传后默认未审核
                                    Builder builder = new AlertDialog.Builder(
                                            AlbumPicAddGridListActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("图片已成功上传，正在审核中...");
                                    builder.setPositiveButton(
                                            "我知道了",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    AlbumPicAddGridListActivity.this
                                                            .finish();
                                                }
                                            });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.show();
                                }
                            } else {// 上传失败
                                progressDialog.dismiss();
                                resourceIds = "";
                                DialogAlert.show(
                                        AlbumPicAddGridListActivity.this,
                                        "上传失败，请重试");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            resourceIds = "";
                            DialogAlert.show(AlbumPicAddGridListActivity.this,
                                    "上传失败，请重试");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        progressDialog.dismiss();
                        resourceIds = "";
                        DialogAlert.show(AlbumPicAddGridListActivity.this,
                                "上传失败，请重试");
                    }
                });
    }

    /**
     * 获取手机本地所有图片的地址列表
     *
     * @return
     */
    private List<AlbumUploadChoosePic> getLocalPicList() {
        List<AlbumUploadChoosePic> list = new ArrayList<AlbumUploadChoosePic>();

        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int cursorCount = cursor.getCount();
        MLog.i("查询到手机本地图片数量：" + cursorCount);
        for (int i = 0; i < cursorCount; i++) {
            String picPath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            long lastModTime = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            MLog.i("图片地址：" + picPath);
            MLog.i("图片最后修改时间：" + lastModTime);
            list.add(new AlbumUploadChoosePic(picPath, lastModTime, false));
            cursor.moveToNext();
        }
        cursor.close();

        // 按图片的最后修改时间进行从大到小排序
        Collections.sort(list, new PicListCompare());
        MLog.i("排序后-------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            AlbumUploadChoosePic pic = list.get(i);
            MLog.i("图片地址：" + pic.getPicPath());
            MLog.i("图片最后修改时间：" + pic.getLastModTime());
        }

        // 限制最大加载100张图片，多余的去掉
        // if (list.size() > 100) {
        // List<AlbumUploadChoosePic> newList = new
        // ArrayList<AlbumUploadChoosePic>();
        // for (int i = 0; i < 100; i++) {
        // AlbumUploadChoosePic pic = list.get(i);
        // newList.add(pic);
        // }
        // list = newList;
        // }

        return list;
    }

    private class PicListCompare implements Comparator<AlbumUploadChoosePic> {
        @Override
        public int compare(AlbumUploadChoosePic lhs, AlbumUploadChoosePic rhs) {
            if (lhs.getLastModTime() > rhs.getLastModTime()) {
                return -1;
            } else if (lhs.getLastModTime() < rhs.getLastModTime()) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (subscription != null) {
            subscription.unsubscribe();
        }

        finish();
    }

    private void checkPermissionAndLoadImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(this, "没有图片", Toast.LENGTH_LONG).show();
            return;
        }
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
            //有权限，加载图片。
            loadImageForSDCard();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(AlbumPicAddGridListActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void loadImageForSDCard() {
        subscription = Observable.just(getLocalPicList())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AlbumUploadChoosePic>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag, "onError");
                    }

                    @Override
                    public void onNext(List<AlbumUploadChoosePic> albumUploadChoosePics) {
                        Log.i(tag, "onNext");
                        try {
                            Thread.sleep(200);
                            picList = albumUploadChoosePics;
                            if (picList != null && picList.size() > 0) {
                                loadPage();
                                albumPicAddGridListAdapter = new AlbumPicAddGridListAdapter(AlbumPicAddGridListActivity.this, currentPicList);
                                grid_album.setAdapter(albumPicAddGridListAdapter);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，加载图片。
                loadImageForSDCard();
            } else {
                //拒绝权限，弹出提示框。
                showExceptionDialog();
            }
        }
    }


    /**
     * 发生没有权限等异常时，显示一个提示dialog.
     */
    private void showExceptionDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage("该相册需要赋予访问存储的权限，请到“设置”>“应用”>“权限”中配置权限。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

}
