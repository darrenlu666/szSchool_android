package com.dt5000.ischool.activity.student;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.widget.ClickableSpannable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WxPayActivity extends AppCompatActivity {
    private TextView txt_title;
    private LinearLayout lLayout_back;

    private ImageView iv_scan;
    private TextView txtView;

    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_pay);
        initView();
        initListener();
        init();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("微信付款");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);

        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        txtView = (TextView) findViewById(R.id.txtView);
    }

    private void initListener() {
        lLayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_scan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(WxPayActivity.this)
                        .setMessage("是否保存图片!")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                iv_scan.setDrawingCacheEnabled(true);
                                if (saveImgToSd(iv_scan.getDrawingCache())) {
                                    Toast.makeText(WxPayActivity.this, "图片保存到:" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        }).create().show();
                return true;
            }
        });
    }


    private void init() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableTextSpan("请先长按保存上面二维码,点击进入微信进行付款。"));
        txtView.setMovementMethod(new LinkMovementMethod());
        txtView.setText(builder);
    }

    private SpannableString setClickableTextSpan(String text) {
        SpannableString nameSpanText = new SpannableString(text);
        nameSpanText.setSpan(new ClickableSpannable(getResources().getColor(R.color.add1)) {
            @Override
            public void onClick(View view) {
                intoWx();
            }
        }, 14, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return nameSpanText;
    }


    private void intoWx() {
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveImgToSd(Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡异常,请查看!", Toast.LENGTH_SHORT).show();
        }
        try {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "DCIM" + File.separator + "Camera";
            file = new File(sdPath, "scan.png");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            // 最后通知图库更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
