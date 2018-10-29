package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * 自主学习选择学习方式页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月22日 上午10:14:18
 * @ClassInfo com.dt5000.ischool.activity.student.StudyChooseActivity
 * @Description
 */
public class StudyChooseActivity extends Activity {

	private TextView txt_title;
	private TextView txt_grade;
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_to_classroom;
	private LinearLayout lLayout_to_exam;

	private String subjectName;
	private String subjectId;
	private String gradeName;
	private int gradeCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_choose);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择学习方式");
		txt_grade = (TextView) findViewById(R.id.txt_grade);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_to_classroom = (LinearLayout) findViewById(R.id.lLayout_to_classroom);
		lLayout_to_exam = (LinearLayout) findViewById(R.id.lLayout_to_exam);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyChooseActivity.this.finish();
			}
		});

		// 点击进入同步课堂页面
		lLayout_to_classroom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StudyChooseActivity.this, StudyClassroomListActivity.class);
				intent.putExtra("gradeName", gradeName);
				intent.putExtra("gradeCode", gradeCode);
				intent.putExtra("subjectName", subjectName);
				startActivity(intent);
			}
		});

		// 点击进入自测评估页面
		lLayout_to_exam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StudyChooseActivity.this, StudyPaperListActivity.class);
				intent.putExtra("subjectName", subjectName);
				intent.putExtra("gradeName", gradeName);
				intent.putExtra("gradeCode", gradeCode);
				intent.putExtra("subjectId", subjectId);
				startActivity(intent);
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		subjectName = intent.getStringExtra("subjectName");
		subjectId = intent.getStringExtra("subjectId");
		gradeName = intent.getStringExtra("gradeName");
		gradeCode = intent.getIntExtra("gradeCode", 0);

		txt_grade.setText(gradeName + "  " + subjectName);
	}

}
