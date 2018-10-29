package com.dt5000.ischool.activity.teacher;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.HomeworkClassListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 查看作业前选择班级列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月13日 下午2:17:49
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkClassListActivity
 * @Description
 */
public class HomeworkClassListActivity extends Activity {

	private ListView listview_class;
	private LinearLayout lLayout_back;
	private TextView txt_topbar_btn;

	private User user;
	private List<ClassItem> classList;
	private HomeworkClassListAdapter homeworkClassListAdapter;
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_class_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		// 初始化View
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择班级");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("发布");
		listview_class = (ListView) findViewById(R.id.listview_class);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkClassListActivity.this.finish();
			}
		});

		// 点击进入发布作业页面
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (classList != null && classList.size() > 0) {
					Intent intent = new Intent(HomeworkClassListActivity.this, HomeworkAddActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("classList", (Serializable) classList);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
				}
			}
		});

		// 点击进入作业列表页面
		listview_class.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				ClassItem classItem = classList.get(position);
				Intent intent = new Intent(HomeworkClassListActivity.this, HomeworkListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("classItem", classItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();
	}

	private void getData() {
		// 配置参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_TEACHER_CLASS);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("获取班级地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("classList");

							Type listType = new TypeToken<List<ClassItem>>() {
							}.getType();
							List<ClassItem> data = (List<ClassItem>) GsonUtil
									.jsonToList(result, listType);

							if (data != null && data.size() > 0) {
								classList = data;
								homeworkClassListAdapter = new HomeworkClassListAdapter(HomeworkClassListActivity.this, classList);
								listview_class.setAdapter(homeworkClassListAdapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				MToast.show(HomeworkClassListActivity.this, "作业发布成功", MToast.SHORT);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
