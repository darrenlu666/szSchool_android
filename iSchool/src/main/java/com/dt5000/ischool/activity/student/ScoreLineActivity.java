package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.MLog;

/**
 * 本学期成绩曲线页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月15日 上午10:04:56
 * @ClassInfo com.dt5000.ischool.activity.student.ScoreLineActivity
 * @Description
 */
public class ScoreLineActivity extends Activity {

	private TextView txt_title;
	private WebView webview_score_line;
	private LinearLayout lLayout_back;

	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_line);

		initView();
		initListener();
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("本学期成绩曲线");
		webview_score_line = (WebView) findViewById(R.id.webview_score_line);
		webview_score_line.getSettings().setJavaScriptEnabled(true);
		webview_score_line.getSettings().setDefaultTextEncodingName("UTF-8");
		webview_score_line.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	private void initListener() {
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScoreLineActivity.this.finish();
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		String url = UrlProtocol.HOMEWORK_HOST
				+ "android/scoreTrace_init.jhtml?sid=" + user.getUserId();
		MLog.i("成绩曲线地址：" + url);

		webview_score_line.loadUrl(url);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& webview_score_line.canGoBack()) {
			webview_score_line.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
