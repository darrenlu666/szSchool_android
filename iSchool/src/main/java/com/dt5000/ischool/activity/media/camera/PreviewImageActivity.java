package com.dt5000.ischool.activity.media.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.utils.MediaFileUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * Created by eachann on 2016/2/18.
 */
public class PreviewImageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_preview);
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.sdv_view);
        findViewById(R.id.tv_recapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getIntent().getStringExtra("EXTRA_DATA"));
                file.delete();
                MediaFileUtils.scanFile(PreviewImageActivity.this, getIntent().getStringExtra("EXTRA_DATA"));
                finish();
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("EXTRA_DATA", getIntent().getStringExtra("EXTRA_DATA"));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Uri uri = Uri.parse("file://" + getIntent().getStringExtra("EXTRA_DATA"));
        draweeView.setImageURI(uri);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaFileUtils.scanFile(PreviewImageActivity.this, getIntent().getStringExtra("EXTRA_DATA"));
    }
}
