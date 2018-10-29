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
import com.dt5000.ischool.adapter.DiaryAttachListAdapter;
import com.dt5000.ischool.entity.DiaryDoc;

/**
 * 日记附件列表页面
 * 
 * @author 周锋
 * @date 2016年3月1日 上午10:56:15
 * @ClassInfo com.dt5000.ischool.activity.DiaryAttachListActivity
 * @Description
 */
public class DiaryAttachListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview_attach;
	private List<DiaryDoc> docList;
	private DiaryAttachListAdapter diaryAttachListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_attach_list);

		initView();
		initListener();
		init();
	}

	private void initView() {
		listview_attach = (ListView) findViewById(R.id.listview_attach);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("附件");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DiaryAttachListActivity.this.finish();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		docList = (List<DiaryDoc>) getIntent().getExtras().getSerializable(
				"docList");
		
		diaryAttachListAdapter = new DiaryAttachListAdapter(
				DiaryAttachListActivity.this, docList);
		listview_attach.setAdapter(diaryAttachListAdapter);
	}

}
