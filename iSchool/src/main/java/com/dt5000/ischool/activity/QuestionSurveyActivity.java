package com.dt5000.ischool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * 问卷调查页面
 * 
 * @author 周锋
 * @date 2016年2月3日 下午4:53:21
 * @ClassInfo com.dt5000.ischool.activity.QuestionSurveyActivity
 * @Description
 */
public class QuestionSurveyActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_survey);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("问卷调查");
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionSurveyActivity.this.finish();
			}
		});
	}

	private void init() {

	}

}
