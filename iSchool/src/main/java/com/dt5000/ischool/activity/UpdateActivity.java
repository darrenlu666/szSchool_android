package com.dt5000.ischool.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AppInfo;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.HttpHandler;
import com.dt5000.ischool.widget.PieProgress;

import java.io.File;

/**
 * 软件更新页面
 *
 * @author 周锋
 * @date 2016年2月1日 下午5:00:04
 * @ClassInfo com.dt5000.ischool.activity.UpdateActivity
 * @Description
 */
public class UpdateActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_progress;
    private PieProgress progressBar_update;

    private AppInfo appInfo;
    private FinalHttp finalHttp;
    private HttpHandler<?> downLoadHandler;
    private File apkFile;
    private static final int PERMISSION_REQUEST_CODE = 0X00000011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initView();
        initListener();
        init();
        checkPermissionAndLoadImages();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("软件更新");
        txt_progress = (TextView) findViewById(R.id.txt_progress);
        progressBar_update = (PieProgress) findViewById(R.id.progressBar_update);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toStopDownload();
            }
        });
    }

    private void init() {
        appInfo = (AppInfo) getIntent().getExtras().getSerializable("appInfo");

        finalHttp = new FinalHttp();

        apkFile = new File(FileUtil.getDownloadDir(), appInfo.getApkName());
        if (apkFile.exists()) {
            apkFile.delete();
        }
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
            downloadApk();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(UpdateActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
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
                downloadApk();
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

    private void downloadApk() {
        downLoadHandler = finalHttp.download(appInfo.getUpdateUrl(),
                apkFile.getAbsolutePath(), true, new AjaxCallBack<File>() {
                    @Override
                    public void onLoading(long count, long current) {
                        txt_progress.setText((int) (current / (float) count * 100) + "%");

                        progressBar_update.setProgress((int) (360 * (current / (float) count)));
                    }

                    @Override
                    public void onSuccess(File t) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //provider authorities
                            Uri apkUri = FileProvider.getUriForFile(UpdateActivity.this, "com.dt5000.ischool.provider", apkFile);
                            //Granting Temporary Permissions to a URI
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        if (errorNo == 0) {// 用户自己取消下载
                            //
                        } else {
                            DialogAlert.show(UpdateActivity.this, "暂时无法升级");
                            if (apkFile.exists()) {
                                apkFile.delete();
                            }
                        }
                    }
                });
    }

    private void toStopDownload() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("程序正在升级，是否退出？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 停止下载
                        downLoadHandler.stop();

                        // 将已经下载的apk删除
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }

                        UpdateActivity.this.finish();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toStopDownload();
        }
        return true;
    }

}
