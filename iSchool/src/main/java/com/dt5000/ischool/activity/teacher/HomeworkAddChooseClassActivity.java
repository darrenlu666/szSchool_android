package com.dt5000.ischool.activity.teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.HomeworkAddChooseClassListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.utils.DialogAlert;

/**
 * 发布作业时选择班级页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午1:40:59
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkAddChooseClassActivity
 * @Description
 */
public class HomeworkAddChooseClassActivity extends Activity {

	private ListView listview_class;
	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;

	private List<ClassItem> classList;
	private HomeworkAddChooseClassListAdapter homeworkAddChooseClassListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_add_choose_class);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择班级");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("确定");
		listview_class = (ListView) findViewById(R.id.listview_class);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAddChooseClassActivity.this.finish();
			}
		});

		// 点击将选择后的班级列表返回上一个页面
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<ClassItem> newClassList = homeworkAddChooseClassListAdapter.getClassList();

				List<ClassItem> judgeClassList = new ArrayList<ClassItem>();
				for (int i = 0; i < newClassList.size(); i++) {
					ClassItem classItem = newClassList.get(i);
					if (classItem.isChoose()) {
						judgeClassList.add(classItem);
					}
				}

				if (judgeClassList != null && judgeClassList.size() > 0) {// 判断新列表是否全是未选中状态
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("newClassList", (Serializable) newClassList);
					intent.putExtras(bundle);
					HomeworkAddChooseClassActivity.this.setResult(RESULT_OK, intent);
					HomeworkAddChooseClassActivity.this.finish();
				} else {
					DialogAlert.show(HomeworkAddChooseClassActivity.this, "请选择班级");
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		classList = (List<ClassItem>) getIntent().getExtras().getSerializable("classList");

		homeworkAddChooseClassListAdapter = new HomeworkAddChooseClassListAdapter(this, classList);
		listview_class.setAdapter(homeworkAddChooseClassListAdapter);
	}

}
