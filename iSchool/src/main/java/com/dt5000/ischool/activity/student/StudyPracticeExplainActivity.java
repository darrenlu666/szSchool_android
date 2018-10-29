package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyPractice;
import com.dt5000.ischool.net.UrlProtocol;

/**
 * 自主学习同步练习答案解析页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月25日 下午2:12:23
 * @ClassInfo com.dt5000.ischool.activity.student.StudyPracticeExplainActivity
 * @Description
 */
public class StudyPracticeExplainActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_name;
	private TextView txt_page;
	private TextView txt_answer;
	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_practice_explain);

		initView();
		initListener();
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("答案与解析");
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_page = (TextView) findViewById(R.id.txt_page);
		txt_answer = (TextView) findViewById(R.id.txt_answer);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyPracticeExplainActivity.this.finish();
			}
		});
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");
		String page = bundle.getString("page");
		StudyPractice practice = (StudyPractice) bundle
				.getSerializable("practice");

		txt_name.setText(name);
		txt_page.setText("第" + page + "题");

		String practiceType = practice.getType();
		if (practiceType.equals("1") || practiceType.equals("2")
				|| practiceType.equals("3")) {
			txt_answer.setVisibility(View.VISIBLE);
			txt_answer.setText("正确答案：" + practice.getSubject());
		}

		webview.loadUrl(UrlProtocol.COURSE_PRACTISE_EXPLAIN + "?id="
				+ practice.getId());
	}

}
