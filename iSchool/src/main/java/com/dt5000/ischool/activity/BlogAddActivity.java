package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.BlogAddPicGridAdapter;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增博客页面
 *
 * @author 周锋
 * @date 2016年2月2日 下午7:31:35
 * @ClassInfo com.dt5000.ischool.activity.BlogAddActivity
 * @Description
 */
public class BlogAddActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_topbar_btn;
    private GridView grid_album;
    private EditText edit_content;

    private BlogAddPicGridAdapter blogAddPicGridAdapter;
    private List<String> picList;
    private FinalHttp finalHttp;
    private User user;
    private String classId;
    private File capture_file;// 拍摄的照片存储的文件
    private String token = "";// 上传图片时用到的Token
    private String content = "";// 发布的内容
    private String resourceIds = "";// 上传所有图片到七牛服务器后，返回的图片id拼接的字符串

    private ProgressDialog progressDialog;
    private UploadManager uploadManager;
    private boolean isUploadCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_add);

        initView();
        initListener();
        init();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("新增博客");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("发布");
        edit_content = (EditText) findViewById(R.id.edit_content);
        grid_album = (GridView) findViewById(R.id.grid_album);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlogAddActivity.this.finish();
            }
        });

        // 点击发布博客
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                content = edit_content.getText().toString().trim();
                if (CheckUtil.stringIsBlank(content) && picList.size() <= 1) {
                    DialogAlert.show(BlogAddActivity.this, "请输入内容或者插入图片");
                    return;
                }

                // 图片上传资源标识清空
                resourceIds = "";

                if (picList.size() <= 1) {// 未发布图片
                    MLog.i("发布博客无图片附件");
                    progressDialog.show();
                    progressDialog.setMessage("正在发布博客...");
                    addBlog();
                } else {// 发布时附带图片
                    MLog.i("发布博客附带图片");
                    progressDialog.show();
                    progressDialog.setMessage("获取验证...");
                    progressDialog.setCancelable(true);
                    requestToGetToken();
                }
            }
        });

        // 添加图片监听
        grid_album.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (picList != null && picList.size() > 0) {
                    String string = picList.get(position);
                    if ("icon_add".equals(string)) {
                        ListAdapter adapter = new ArrayAdapter<String>(
                                BlogAddActivity.this,
                                android.R.layout.simple_list_item_1,
                                new String[]{"相册"});

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                BlogAddActivity.this);
                        builder.setSingleChoiceItems(adapter, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        switch (which) {
                                            // case 0:// 拍照
                                            // getPicFromCamera();
                                            // break;
                                            case 0:// 相册
//											Intent intent = new Intent(
//													BlogAddActivity.this,
//													BlogAddChoosePicActivity.class);
//											startActivityForResult(
//													intent,
//													FlagCode.ACTIVITY_REQUEST_CODE_1);
                                                ImageSelectorUtils.openPhoto(BlogAddActivity.this, FlagCode.ACTIVITY_REQUEST_CODE_1, false, 9);
                                                break;
                                        }
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private void init() {
        classId = getIntent().getStringExtra("classId");

        user = User.getUser(this);

        finalHttp = new FinalHttp();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("发布中，请稍后...");
        progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isUploadCancelled = true;
            }
        });

        // 初始化图片列表，在最后加上固定图片（加号）
        picList = new ArrayList<String>();
        picList.add(picList.size(), "icon_add");
        blogAddPicGridAdapter = new BlogAddPicGridAdapter(BlogAddActivity.this,
                picList);
        grid_album.setAdapter(blogAddPicGridAdapter);
    }

    @SuppressWarnings("unused")
    private void getPicFromCamera() {
        try {
            File camera_file = new File(
                    Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
            if (!camera_file.exists()) {
                camera_file.mkdirs();
            }
            // 拍照后将图片保存在系统相册中
            capture_file = new File(camera_file,
                    ImageUtil.getPhotoFileNameWithCurrentTime());
            capture_file.createNewFile();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capture_file));
            startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FlagCode.ACTIVITY_REQUEST_CODE_0:// 拍照获取图片后返回结果
                try {
                    MLog.i("拍照返回结果：" + Uri.fromFile(capture_file));

                    // 拍摄成功后刷新相册
                    Uri localUri = Uri.fromFile(capture_file);
                    Intent localIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    sendBroadcast(localIntent);

                    // 将拍摄的图片添加到列表顶端，并刷新页面
                    String capturePicPath = capture_file.getAbsolutePath();
                    picList.add(0, capturePicPath);
                    blogAddPicGridAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FlagCode.ACTIVITY_REQUEST_CODE_1:// 从手机选择图片后返回的结果
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
//					picList = (List<String>) bundle.getSerializable("picList");
                        picList = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                        picList.add(picList.size(), "icon_add");
                        blogAddPicGridAdapter = new BlogAddPicGridAdapter(
                                BlogAddActivity.this, picList);
                        grid_album.setAdapter(blogAddPicGridAdapter);
                    }
                }
                break;
        }
    }

    /**
     * 发请求新增博客
     */
    private void addBlog() {
        // 封装参数
        Map<String, String> operationMap = new HashMap<String, String>();
        operationMap.put("operationType", UrlProtocol.OPERATION_TYPE_BLOG_ADD);
        operationMap.put("userId", user.getUserId());
        operationMap.put("content", content);
        operationMap.put("clazzId", classId);
        operationMap.put("resourceIds", resourceIds);
        operationMap.put("role", String.valueOf(user.getRole()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(operationMap, this,
                user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onStart() {
                        progressDialog.setCancelable(false);
                    }

                    @Override
                    public void onSuccess(String t) {
                        MLog.i("发布博客返回结果：" + t);
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String resultStatus = jsonObject
                                    .optString("resultStatus");
                            if ("200".equals(resultStatus)) {// 发布博客成功
                                progressDialog.dismiss();

                                // 将上传图片的缓存文件夹清空
                                File[] picFiles = FileUtil.getISchoolCacheDir()
                                        .listFiles();
                                if (picFiles != null && picFiles.length > 0) {
                                    for (int i = 0; i < picFiles.length; i++) {
                                        picFiles[i].delete();
                                    }
                                }

                                // 返回成功的操作结果给上一个页面
                                BlogAddActivity.this.setResult(RESULT_OK);
                                BlogAddActivity.this.finish();
                            } else {// 发布博客失败
                                progressDialog.dismiss();
                                DialogAlert.show(BlogAddActivity.this,
                                        "发布失败，请重试");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            DialogAlert.show(BlogAddActivity.this, "发布失败，请重试");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        progressDialog.dismiss();
                        DialogAlert.show(BlogAddActivity.this, "发布失败，请重试");
                    }
                });
    }

    /**
     * 上传图片时需要获取Token
     */
    private void requestToGetToken() {
        // 封装参数
        Map<String, String> operationMap = new HashMap<String, String>();
        operationMap.put("operationType",
                UrlProtocol.OPERATION_TYPE_BLOG_ADD_TOKEN);
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(operationMap, this,
                user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("上传图片前获取Token返回结果：" + t);
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String returnToken = jsonObject.optString("token");
                            if (!CheckUtil.stringIsBlank(returnToken)) {
                                token = returnToken;
                                uploadToQiniu();
                            } else {
                                progressDialog.dismiss();
                                DialogAlert.show(BlogAddActivity.this,
                                        "服务器异常，请稍后再试");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            DialogAlert.show(BlogAddActivity.this,
                                    "服务器异常，请稍后再试");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        progressDialog.dismiss();
                        DialogAlert.show(BlogAddActivity.this, "服务器异常，请稍后再试");
                    }
                });
    }

    /**
     * 将发布博客需要发送的图片上传到七牛服务器
     */
    private void uploadToQiniu() {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }

        // 对图片进行另存压缩
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File cacheFile = null;
                    List<File> multiFileList = new ArrayList<File>();
                    // 生成所有要上传的文件，文件列表最后一项是本地图片“+”号，不做上传
                    for (int i = 0; i < picList.size() - 1; i++) {
                        cacheFile = new File(FileUtil.getISchoolCacheDir(),
                                "blog_upload_cache_" + i + ".jpg");
                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                        cacheFile.createNewFile();

                        FileOutputStream fos = new FileOutputStream(cacheFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        Bitmap decodeBitmap = ImageUtil
                                .decodeBitmapWithThumbnailUtils(picList.get(i),
                                        1280);
                        // 压缩图片质量至67%（微信压缩图片比率在67%左右）
                        decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 67,
                                bos);
                        bos.flush();
                        bos.close();
                        fos.close();

                        multiFileList.add(cacheFile);
                    }

                    Message message = new Message();
                    message.what = 2;
                    message.obj = multiFileList;
                    uploadHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                            MLog.i("上传第" + (currentCount + 1)
                                    + "张图片成功--------------------------");
                            String resource = response.optString("resource");
                            resourceIds += resource + ",";
                            MLog.i("当前resourceIds: " + resourceIds);

                            // 上传成功后将本次上传的图片从列表中移除
                            picFileList.remove(0);
                            if (picFileList.size() <= 0) {// 已全部上传完毕
                                resourceIds = resourceIds.substring(0, resourceIds.lastIndexOf(","));
                                MLog.i("图片全部上传完毕，resourceIds: " + resourceIds);

                                MLog.i("开始发布博客...");
                                uploadHandler.sendEmptyMessage(1);
                                addBlog();
                            } else {// 还有剩余图片需要上传
                                int nextIndex = currentCount;
                                nextIndex++;
                                recursiveUpload(picFileList, nextIndex,
                                        totalCount);
                            }
                        } else if (info.isCancelled()) {
                            MLog.i("用户取消上传...");
                        } else {
                            progressDialog.dismiss();
                            DialogAlert.show(BlogAddActivity.this,
                                    "服务器异常，请稍后再试");
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
                    progressDialog.setMessage("正在发布...");
                    break;
                case 2:
                    @SuppressWarnings("unchecked")
                    List<File> multiFileList = (List<File>) msg.obj;
                    MLog.i("共需上传图片的数量：" + multiFileList.size());

                    // 取消上传标识默认
                    isUploadCancelled = false;

                    // 递归上传图片
                    recursiveUpload(multiFileList, 0, multiFileList.size());
                    break;
            }
        }

        ;
    };

}
