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
import com.dt5000.ischool.activity.SingleImageShowActivity;
import com.dt5000.ischool.entity.Attendance;
import com.dt5000.ischool.utils.ImageLoaderUtil;

/**
 * 考勤照片页面：学生端
 * 
 * @author 周锋
 * @date 2016年10月9日 下午4:09:20
 * @ClassInfo com.dt5000.ischool.activity.student.AttendancePictureActivity
 * @Description
 */
public class AttendancePictureActivity extends Activity {

	private TextView txt_title;
	private TextView txt_time;
	private LinearLayout lLayout_back;
	private ImageView img_picture;

	private Attendance attendance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_picture);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("考勤");
		txt_time = (TextView) findViewById(R.id.txt_time);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		img_picture = (ImageView) findViewById(R.id.img_picture);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendancePictureActivity.this.finish();
			}
		});

		// 点击查看照片
		img_picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AttendancePictureActivity.this,
						SingleImageShowActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("url", attendance.getImgUrl());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		attendance = (Attendance) getIntent().getExtras().getSerializable(
				"attendance");

		txt_time.setText(attendance.getTransactionTime());
		ImageLoaderUtil.createSimple(this).displayImage(attendance.getImgUrl(),
				img_picture);
	}

}
