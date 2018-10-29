package com.dt5000.ischool.activity.teacher;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.SingleImageShowActivity;
import com.dt5000.ischool.adapter.teacher.AttendancePictureGridListAdapter;
import com.dt5000.ischool.entity.AttendanceInfo;
import com.dt5000.ischool.entity.AttendancePicture;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 考勤照片列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年10月12日 上午10:51:36
 * @ClassInfo 
 *            com.dt5000.ischool.activity.teacher.AttendancePictureGridListActivity
 * @Description
 */
public class AttendancePictureGridListActivity extends Activity {

	private GridView grid_attendance_picture;
	private TextView txt_title;
	private TextView txt_time;
	private LinearLayout lLayout_back;

	private AttendancePictureGridListAdapter attendancePictureGridListAdapter;
	private List<AttendancePicture> attendancePictureList;
	private AttendanceInfo attendanceInfo;
	private User user;
	private FinalHttp finalHttp;
	private String paramDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_picture_grid_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("考勤照片");
		txt_time = (TextView) findViewById(R.id.txt_time);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		grid_attendance_picture = (GridView) findViewById(R.id.grid_attendance_picture);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendancePictureGridListActivity.this.finish();
			}
		});

		// 点击查看图片
		grid_attendance_picture
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						AttendancePicture attendancePicture = attendancePictureList
								.get(position);
						Bundle bundle = new Bundle();
						bundle.putString("url", attendancePicture.getPhotoUrl());
						Intent intent = new Intent(
								AttendancePictureGridListActivity.this,
								SingleImageShowActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
	}

	private void init() {
		attendanceInfo = (AttendanceInfo) getIntent().getExtras()
				.getSerializable("attendanceInfo");

		paramDate = TimeUtil.yearMonthDayFormat(TimeUtil.parseFullTime(
				attendanceInfo.getArriveTime()).getTime());
		txt_time.setText(paramDate + " " + attendanceInfo.getRealName());

		user = User.getUser(this);
		finalHttp = new FinalHttp();
		attendancePictureList = new ArrayList<AttendancePicture>();
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_ATTENDANCE_PICTURE_LIST);
		map.put("kqUserId", attendanceInfo.getKqUserId());
		map.put("date", paramDate);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("考勤图片列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("imgList");

							// 解析实体类
							Type listType = new TypeToken<List<AttendancePicture>>() {
							}.getType();
							List<AttendancePicture> data = (List<AttendancePicture>) GsonUtil
									.jsonToList(result, listType);
							if (data != null && data.size() > 0) {
								attendancePictureList = data;

								// 设置适配器
								attendancePictureGridListAdapter = new AttendancePictureGridListAdapter(
										AttendancePictureGridListActivity.this,
										attendancePictureList);
								grid_attendance_picture
										.setAdapter(attendancePictureGridListAdapter);
							}
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
