package com.dt5000.ischool.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.HomeworkAttachListAdapter;
import com.dt5000.ischool.entity.HomeworkAttach;

/**
 * 作业附件列表页面
 * 
 * @author 周锋
 * @date 2016年1月13日 下午4:36:05
 * @ClassInfo com.dt5000.ischool.activity.HomeworkAttachListActivity
 * @Description
 */
public class HomeworkAttachListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview_attach;
	private List<HomeworkAttach> homeworkAttachList;
	private HomeworkAttachListAdapter homeworkAttachListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_attach_list);

		initView();
		initListener();
		init();
	}

	private void initView() {
		listview_attach = (ListView) findViewById(R.id.listview_attach);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("作业附件");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAttachListActivity.this.finish();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		Bundle bundle = getIntent().getExtras();
		homeworkAttachList = (List<HomeworkAttach>) bundle.getSerializable("homeworkAttachs");
		homeworkAttachListAdapter = new HomeworkAttachListAdapter(HomeworkAttachListActivity.this, homeworkAttachList);
		listview_attach.setAdapter(homeworkAttachListAdapter);
	}
}
