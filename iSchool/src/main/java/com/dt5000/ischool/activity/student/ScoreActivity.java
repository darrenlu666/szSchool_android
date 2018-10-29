package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;

/**
 * 成绩查询选择查询方式页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午4:43:40
 * @ClassInfo com.dt5000.ischool.activity.student.ScoreActivity
 * @Description
 */
public class ScoreActivity extends Activity {

	private RelativeLayout rLayout_to_score_list;
	private RelativeLayout rLayout_to_score_line;
	private TextView txt_title;
	private LinearLayout lLayout_back;

	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("查询方式");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		rLayout_to_score_list = (RelativeLayout) findViewById(R.id.rLayout_to_score_list);
		rLayout_to_score_line = (RelativeLayout) findViewById(R.id.rLayout_to_score_line);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScoreActivity.this.finish();
			}
		});

		// 点击跳转到成绩列表页面
		rLayout_to_score_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ScoreActivity.this,
						ScoreListActivity.class));
			}
		});

		// 点击跳转到成绩曲线页面
		rLayout_to_score_line.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ScoreActivity.this,
						ScoreLineActivity.class));
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		// 小学不显示成绩曲线
		if (user.getGradeCode() <= 6) {
			rLayout_to_score_line.setVisibility(View.GONE);
		}
	}

}
