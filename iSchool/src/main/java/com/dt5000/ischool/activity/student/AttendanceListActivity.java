package com.dt5000.ischool.activity.student;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.student.AttendanceListAdapter;
import com.dt5000.ischool.entity.Attendance;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 考勤列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年10月9日 上午10:20:07
 * @ClassInfo com.dt5000.ischool.activity.student.AttendanceListActivity
 * @Description
 */
public class AttendanceListActivity extends Activity {

	private ListView listview_attendance;
	private LinearLayout lLayout_loading;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private LinearLayout lLayout_back;

	private List<Attendance> attendanceList;
	private AttendanceListAdapter attendanceListAdapter;
	private User user;
	private FinalHttp finalHttp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("考勤");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("请假");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
		lLayout_loading.setVisibility(View.GONE);
		listview_attendance = (ListView) findViewById(R.id.listview_attendance);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendanceListActivity.this.finish();
			}
		});

		// 点击请假
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AttendanceListActivity.this,
						AttendanceVacateListActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);
		finalHttp = new FinalHttp();
		attendanceList = new ArrayList<Attendance>();
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_ATTENDANCE_LIST_STUDENT);
		map.put("userId", user.getUserId());
		map.put("schoolId", user.getSchoolbaseinfoId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("考勤列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("records");

							// 解析实体类
							Type listType = new TypeToken<List<Attendance>>() {
							}.getType();
							List<Attendance> data = (List<Attendance>) GsonUtil
									.jsonToList(result, listType);
							if (data != null && data.size() > 0) {
								attendanceList = data;
							}

							// 设置适配器
							attendanceListAdapter = new AttendanceListAdapter(
									AttendanceListActivity.this, attendanceList);
							listview_attendance
									.setAdapter(attendanceListAdapter);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
					}
				});
	}

}
