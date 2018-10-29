package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.ChildrenCollectListActivity;
import com.dt5000.ischool.entity.User;

/**
 * 我的收藏页面：学生端
 * 
 * @author 周锋
 * @date 2016年2月4日 上午9:36:49
 * @ClassInfo com.dt5000.ischool.activity.student.CollectChooseActivity
 * @Description
 */
public class CollectChooseActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_yuer;
	private TextView txt_wrong;
	private ImageView img_line_wrong;
	private TextView txt_hard;
	private ImageView img_line_hard;
	private TextView txt_whole;
	private ImageView img_line_whole;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_choose);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("我的收藏");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_yuer = (TextView) findViewById(R.id.txt_yuer);
		txt_wrong = (TextView) findViewById(R.id.txt_wrong);
		img_line_wrong = (ImageView) findViewById(R.id.img_line_wrong);
		txt_hard = (TextView) findViewById(R.id.txt_hard);
		img_line_hard = (ImageView) findViewById(R.id.img_line_hard);
		txt_whole = (TextView) findViewById(R.id.txt_whole);
		img_line_whole = (ImageView) findViewById(R.id.img_line_whole);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CollectChooseActivity.this.finish();
			}
		});

		// 点击育儿经收藏
		txt_yuer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CollectChooseActivity.this,
						ChildrenCollectListActivity.class));
			}
		});

		// 点击错题收藏
		txt_wrong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollectChooseActivity.this,
						StudyTestingCollectListActivity.class);
				intent.putExtra("type", 1);// 错题收藏type为1
				startActivity(intent);
			}
		});

		// 点击难题收藏
		txt_hard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollectChooseActivity.this,
						StudyTestingCollectListActivity.class);
				intent.putExtra("type", 2);// 难题收藏type为2
				startActivity(intent);
			}
		});

		// 点击套题收藏
		txt_whole.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollectChooseActivity.this,
						StudyTestingCollectListActivity.class);
				intent.putExtra("type", 3);// 套题收藏type为3
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		// 判断学校类型
		if (User.isKindergarten(user.getSchoolCode())) {
			// 幼儿园没有题目收藏
			txt_wrong.setVisibility(View.GONE);
			txt_hard.setVisibility(View.GONE);
			txt_whole.setVisibility(View.GONE);
			img_line_wrong.setVisibility(View.GONE);
			img_line_hard.setVisibility(View.GONE);
			img_line_whole.setVisibility(View.GONE);
		}
	}

}
