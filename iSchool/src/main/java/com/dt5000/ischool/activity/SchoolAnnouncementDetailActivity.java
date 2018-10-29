package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.net.UrlProtocol;

/**
 * 校园公告详情页面
 * 
 * @author 周锋
 * @date 2016年2月2日 下午1:55:05
 * @ClassInfo com.dt5000.ischool.activity.SchoolAnnouncementDetailActivity
 * @Description
 */
public class SchoolAnnouncementDetailActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private WebView webView;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_announcement_detail);

		initView();
		initListener();
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("校园公告");

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setMax(100);
		progressBar.setProgress(0);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.requestFocus();

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBar.setProgress(0);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				progressBar.setProgress(newProgress);
				progressBar.postInvalidate();
			}
		});
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SchoolAnnouncementDetailActivity.this.finish();
			}
		});
	}

	private void init() {
		String publicationId = getIntent().getStringExtra("publicationId");

		webView.loadUrl(UrlProtocol.PUBLICATION_DETAIL + "?id=" + publicationId);
	}

}
