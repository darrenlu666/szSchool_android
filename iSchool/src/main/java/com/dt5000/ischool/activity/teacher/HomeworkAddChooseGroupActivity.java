package com.dt5000.ischool.activity.teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.HomeworkAddChooseClassListAdapter;
import com.dt5000.ischool.adapter.teacher.HomeworkAddChooseGroupListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.GroupItem;
import com.dt5000.ischool.utils.DialogAlert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布作业时选择班级页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午1:40:59
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkAddChooseClassActivity
 * @Description
 */
public class HomeworkAddChooseGroupActivity extends Activity {

	private ListView listview_class;
	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;

	private List<GroupItem> classList;
	private HomeworkAddChooseGroupListAdapter homeworkAddChooseClassListAdapter;

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
		txt_title.setText("选择群組");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("确定");
		listview_class = (ListView) findViewById(R.id.listview_class);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAddChooseGroupActivity.this.finish();
			}
		});

		// 点击将选择后的班级列表返回上一个页面
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCloose = false;
				for (int i=0;i<classList.size();i++){
					if(classList.get(i).isChoose()){
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("newGroup",
								classList.get(i));
						intent.putExtras(bundle);
						HomeworkAddChooseGroupActivity.this.setResult(RESULT_OK,
								intent);
						isCloose =true;
						finish();
					}
				}
				if(!isCloose){
					DialogAlert.show(HomeworkAddChooseGroupActivity.this,
							"请选择群組");
				}

			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		classList = (List<GroupItem>) getIntent().getExtras().getSerializable(
				"groupList");

		homeworkAddChooseClassListAdapter = new HomeworkAddChooseGroupListAdapter(
				this, classList);
		listview_class.setAdapter(homeworkAddChooseClassListAdapter);
		listview_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for (int i=0;i<classList.size();i++){
					if(i==position){
						classList.get(i).setChoose(!classList.get(i).isChoose());
					}else{
						classList.get(i).setChoose(false);
					}
				}
				homeworkAddChooseClassListAdapter.notifyDataSetChanged();
			}
		});
	}

}
