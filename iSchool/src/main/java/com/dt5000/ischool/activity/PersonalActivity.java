package com.dt5000.ischool.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.db.daohelper.DaoHelper;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人信息页面
 *
 * @author 周锋
 * @date 2016年1月29日 下午2:05:32
 * @ClassInfo com.dt5000.ischool.activity.PersonalActivity
 * @Description
 */
public class PersonalActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private ImageView img_head;
    private TextView txt_name;
    private TextView txt_school;
    private TextView txt_grade;
    private TextView txt_class;
    private LinearLayout lLayout_grade;

    private User user;
    private File head_file;
    private File head_file_upload;
    private ProgressDialog progressDialog;
    private ImageLoader imageLoader;
    private final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initView();
        initListener();
        init();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("个人信息");
        img_head = (ImageView) findViewById(R.id.img_head);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_school = (TextView) findViewById(R.id.txt_school);
        txt_grade = (TextView) findViewById(R.id.txt_grade);
        txt_class = (TextView) findViewById(R.id.txt_class);
        lLayout_grade = (LinearLayout) findViewById(R.id.lLayout_grade);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalActivity.this.finish();
            }
        });

        // 点击设置头像
        img_head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAdapter adapter = new ArrayAdapter<String>(
                        PersonalActivity.this,
                        android.R.layout.simple_list_item_1, new String[]{
                        "拍照", "相册", "大图"});

                new AlertDialog.Builder(PersonalActivity.this)
                        .setSingleChoiceItems(adapter, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        switch (which) {
                                            case 0: // 拍照获取图片
                                                if (ContextCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(PersonalActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                            REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                                                } else {
                                                    getPicFromCamera();
                                                }
                                                break;
                                            case 1:// 相册获取图片
                                                getPicFromAlbum();
                                                break;
                                            case 2:// 查看大图
                                                showHead();
                                                break;
                                        }
                                    }
                                }).show();
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        txt_name.setText(user.getRealName());
        txt_school.setText(user.getSchoolName());

        if (User.isTeacherRole(user.getRole())) {// 教师
            lLayout_grade.setVisibility(View.GONE);

            List<ClassItem> clazzList = user.getClazzList();
            if (clazzList != null && clazzList.size() > 0) {
                String classNames = "";
                for (ClassItem classItem : clazzList) {
                    classNames += classItem.getClassName() + "\n\n";
                }
                classNames = classNames.substring(0,
                        classNames.lastIndexOf("\n\n"));
                txt_class.setText(classNames);
            }
        } else {// 学生
            lLayout_grade.setVisibility(View.VISIBLE);
            txt_grade.setText(user.getGradeName());
            txt_class.setText(user.getClassName());
        }

        imageLoader = ImageLoaderUtil.createSimple(this);

        // 设置头像
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        String headUrl = sharedPreferences.getString("headUrl", "");
        if (!CheckUtil.stringIsBlank(headUrl)) {
            imageLoader.displayImage(headUrl, img_head);
        }
    }

    private void showHead() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        String headUrl = sharedPreferences.getString("headUrl", "");
        if (CheckUtil.stringIsBlank(headUrl)) {
            MToast.show(this, "还未设置头像", MToast.SHORT);
        } else {
            Intent intent = new Intent(PersonalActivity.this,
                    SingleImageShowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", headUrl);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void getPicFromCamera() {
        try {
            File camera_file = new File(
                    Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
            if (!camera_file.exists()) {
                camera_file.mkdirs();
            }
            head_file = new File(camera_file, "ischool_head.jpg");
            head_file.createNewFile();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String authority = getPackageName() + ".provider";
                imageUri = FileProvider.getUriForFile(this, authority, head_file);
            } else {
                imageUri = Uri.fromFile(head_file);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPicFromCamera();
            } else {
                MToast.show(PersonalActivity.this, "Permission Denied", MToast.SHORT);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPicFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:// 拍照返回结果
                if (resultCode == RESULT_OK) {
                    MLog.i("拍照返回结果：" + Uri.fromFile(head_file));

                    setHeadPic(head_file.getAbsolutePath());
                }
                break;
            case 1:// 相册返回结果
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String picPath = PictureUtil.getPath(PersonalActivity.this,
                                data.getData());
                        MLog.i("相册返回结果：" + data.getData());
                        MLog.i("相册返回结果（处理后）：" + picPath);

                        setHeadPic(picPath);
                    }
                }
                break;
        }
    }

    /**
     * 发送请求设置头像
     *
     * @param path 图片在手机中的路径
     */
    private void setHeadPic(final String path) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在上传头像...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    String uploadPath = path;

                    // 将上传图片保存为ischool_head_upload.jpg
                    head_file_upload = new File(FileUtil.getCameraDir(),
                            "ischool_head_upload.jpg");
                    head_file_upload.createNewFile();

                    FileOutputStream fos = new FileOutputStream(
                            head_file_upload);
                    Bitmap decodeBitmap = ImageUtil
                            .decodeBitmapWithThumbnailUtils(uploadPath, 600);
                    // 压缩图片质量至80%
                    decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.flush();
                    fos.close();

                    uploadPath = head_file_upload.getAbsolutePath();

                    // 封装参数
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("operationType",
                            UrlProtocol.OPERATION_TYPE_UPLOAD_USER_ICON);
                    if (User.isStudentRole(user.getRole())) {
                        map.put("userType", "1");// 学生
                    } else {
                        map.put("userType", "2");// 教师
                    }
                    String httpURL = UrlBulider.getHttpURL(map,
                            PersonalActivity.this, user.getUserId());

                    // 发送请求
                    String response = HttpClientUtil.doPostWithSingleFile(
                            httpURL, uploadPath);
                    MLog.i("上传头像返回结果:" + response);

                    JSONObject obj = new JSONObject(response);
                    String resultStatus = obj.optString("resultStatus");
                    String picUrl = obj.optString("profileUrl");
                    if ("200".equals(resultStatus) && !CheckUtil.stringIsBlank(picUrl)) {
                        msg.what = FlagCode.SUCCESS;
                        msg.obj = picUrl;
                    } else {
                        msg.what = FlagCode.FAIL;
                    }
                } catch (Exception e) {
                    msg.what = FlagCode.FAIL;
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagCode.SUCCESS:// 上传头像成功
                    progressDialog.dismiss();

                    String photoUrl = (String) msg.obj;

                    // 显示头像
                    imageLoader.displayImage(photoUrl, img_head);

                    // 将头像地址保存在配置文件中
                    SharedPreferences sharedPreferences = getSharedPreferences(MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("headUrl", photoUrl).commit();

                    User user = User.getUser(PersonalActivity.this);
                    user.setProfileUrl(photoUrl);
                    sharedPreferences.edit().putString("userJson", new Gson().toJson(user)).commit();

                    //个人数据库头像更新
                    DaoHelper.updateUserInfo(user.getUserId(), photoUrl);

                    // 上传头像成功后将本地相关头像图片删除
                    if (head_file_upload != null && head_file_upload.exists()) {
                        head_file_upload.delete();
                    }
                    if (head_file != null && head_file.exists()) {
                        head_file.delete();
                    }
                    break;
                case FlagCode.FAIL:// 上传头像失败
                    progressDialog.dismiss();

                    MToast.show(PersonalActivity.this, "目前无法上传头像", MToast.SHORT);
                    break;
            }
        }

        ;
    };

    /**
     * 裁剪头像，备用，某些手机存在BUG，后期可以使用开源的图片裁剪插件
     */
    @SuppressWarnings("unused")
    private void setZoomBig(Uri uri) {
        Intent zoomIntent = new Intent("com.android.camera.action.CROP");
        zoomIntent.setDataAndType(uri, "image/*");
        zoomIntent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        zoomIntent.putExtra("aspectX", 1);
        zoomIntent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        zoomIntent.putExtra("outputX", 300);
        zoomIntent.putExtra("outputY", 300);
        zoomIntent.putExtra("scale", true);
        zoomIntent.putExtra("return-data", false);
        // 将剪裁的图片保存到headTemp文件中
        zoomIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(head_file));
        zoomIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        zoomIntent.putExtra("noFaceDetection", true);
        startActivityForResult(zoomIntent, 2);
    }

}
