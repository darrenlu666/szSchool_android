package com.dt5000.ischool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * 课程表备用页面：无功能
 * 
 * @author 周锋
 * @date 2016年3月17日 上午10:14:30
 * @ClassInfo com.dt5000.ischool.activity.LessonTableNoUseActivity
 * @Description
 */
public class LessonTableNoUseActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lesson_table_no_use);

		initView();
		initListener();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("课程表");
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LessonTableNoUseActivity.this.finish();
			}
		});
	}

}
