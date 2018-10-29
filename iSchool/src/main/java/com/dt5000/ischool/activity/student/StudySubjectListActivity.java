package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.net.UrlProtocol;

/**
 * 自主学习科目列表页面：学生端
 *
 * @author 周锋
 * @date 2016年1月25日 下午3:56:40
 * @ClassInfo com.dt5000.ischool.activity.student.StudySubjectListActivity
 * @Description
 */
public class StudySubjectListActivity extends Activity {
    private WebView webView;
    private ImageView ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_subject_list);
        webView = (WebView) findViewById(R.id.webView);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        //WebView加载web资源
        webView.loadUrl(UrlProtocol.WEB);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.removeAllViews();
        webView.destroy();
        finish();
    }
}
