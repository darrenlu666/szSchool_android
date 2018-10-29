package com.dt5000.ischool.activity.teacher;

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
import com.dt5000.ischool.adapter.teacher.AttendanceClassListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤选择班级列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年10月10日 下午4:15:37
 * @ClassInfo com.dt5000.ischool.activity.teacher.AttendanceClassListActivity
 * @Description
 */
public class AttendanceClassListActivity extends Activity {

	private TextView txt_title;
	private TextView txt_topbar_btn;
	private ListView listview_class;
	private LinearLayout lLayout_back;

	private User user;
	private List<ClassItem> classList;
	private AttendanceClassListAdapter attendanceClassListAdapter;
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_class_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择班级");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("假单");
		listview_class = (ListView) findViewById(R.id.listview_class);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendanceClassListActivity.this.finish();
			}
		});

		// 点击进入请假单页面
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AttendanceClassListActivity.this,
						AttendanceVacateListActivity.class);
				startActivity(intent);
			}
		});

		// 点击进入考勤列表页面
		listview_class.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				ClassItem classItem = classList.get(position);
				Intent intent = new Intent(AttendanceClassListActivity.this, AttendancePageListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("classItem", classItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

//		if (user.getRole() != 4) {
//			txt_topbar_btn.setVisibility(View.GONE);
//			txt_topbar_btn.setEnabled(false);
//		}

		finalHttp = new FinalHttp();
	}

	/**
	 * 获取教师所带班级数据
	 */
	private void getData() {
		// 配置参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_ATTENDANCE_CLASS_LIST);
		map.put("userId", user.getUserId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("考勤班级列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("classLists");

							Type listType = new TypeToken<List<ClassItem>>() {
							}.getType();
							List<ClassItem> data = (List<ClassItem>) GsonUtil
									.jsonToList(result, listType);

							if (data != null && data.size() > 0) {
								classList = data;
								attendanceClassListAdapter = new AttendanceClassListAdapter(
										AttendanceClassListActivity.this,
										classList);
								listview_class
										.setAdapter(attendanceClassListAdapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
