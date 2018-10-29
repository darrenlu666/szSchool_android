package com.dt5000.ischool.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 育儿经详情页面
 * 
 * @author 周锋
 * @date 2016年2月2日 下午2:06:23
 * @ClassInfo com.dt5000.ischool.activity.ChildrenDetailActivity
 * @Description
 */
public class ChildrenDetailActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private WebView webView;
	private ProgressBar progressBar;

	private User user;
	private FinalHttp finalHttp;
	private String publicationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_children_detail);

		initView();
		initListener();
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("育儿经");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("收藏");

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
				ChildrenDetailActivity.this.finish();
			}
		});

		// 点击收藏
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ChildrenDetailActivity.this)
						.setMessage("是否收藏此页面？")
						.setPositiveButton("收藏",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										collect();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
	}

	private void init() {
		publicationId = getIntent().getStringExtra("publicationId");

		user = User.getUser(this);

		// 教师端没有收藏功能
		if (User.isTeacherRole(user.getRole())) {
			txt_topbar_btn.setVisibility(View.GONE);
		}

		finalHttp = new FinalHttp();

		webView.loadUrl(UrlProtocol.PUBLICATION_DETAIL + "?id=" + publicationId);
	}

	/**
	 * 发请求收藏育儿经
	 */
	private void collect() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_PUBLICATION_COLLECT);
		map.put("favId", publicationId);
		map.put("favType", "2");
		map.put("userId1", user.getUserId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("收藏育儿经返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String resultStatus = obj.optString("resultStatus");
							if ("200".equals(resultStatus)) {
								MToast.show(ChildrenDetailActivity.this,
										"已加入收藏", MToast.LONG);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
