package com.dt5000.ischool.activity.student;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 自主学习自测评估解析答案页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月27日 上午10:09:41
 * @ClassInfo 
 *            com.dt5000.ischool.activity.student.StudyTestingAnalysisAnswerActivity
 * @Description
 */
public class StudyTestingAnalysisAnswerActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private WebView webView;

	private User user;
	private String testLineId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing_analysis_answer);

		initView();
		initListener();
		init();
		getData();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("答案解析");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
	}

	private void initListener() {
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingAnalysisAnswerActivity.this.finish();
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		testLineId = intent.getStringExtra("testLineId");

		user = User.getUser(this);
	}

	private void getData() {
		// 配置参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_ANALYSIS);
		map.put("questionId", testLineId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						try {
							JSONObject obj = new JSONObject(t);
							String webData = obj.optString("url");
							webView.loadDataWithBaseURL(UrlProtocol.MAIN_HOST,
									webData, "text/html", "utf-8", null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
