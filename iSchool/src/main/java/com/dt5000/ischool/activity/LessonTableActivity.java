package com.dt5000.ischool.activity;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.LessonTable;
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
 * 课程表页面
 * 
 * @author 周锋
 * @date 2016年3月4日 上午9:22:09
 * @ClassInfo com.dt5000.ischool.activity.LessonTableActivity
 * @Description
 */
public class LessonTableActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;

	private User user;
	private List<LessonTable> lessonTableList;

	private TextView txt_mon_1;
	private TextView txt_tue_1;
	private TextView txt_wed_1;
	private TextView txt_thur_1;
	private TextView txt_fri_1;
	private TextView txt_sat_1;
	private TextView txt_sun_1;

	private TextView txt_mon_2;
	private TextView txt_tue_2;
	private TextView txt_wed_2;
	private TextView txt_thur_2;
	private TextView txt_fri_2;
	private TextView txt_sat_2;
	private TextView txt_sun_2;

	private TextView txt_mon_3;
	private TextView txt_tue_3;
	private TextView txt_wed_3;
	private TextView txt_thur_3;
	private TextView txt_fri_3;
	private TextView txt_sat_3;
	private TextView txt_sun_3;

	private TextView txt_mon_4;
	private TextView txt_tue_4;
	private TextView txt_wed_4;
	private TextView txt_thur_4;
	private TextView txt_fri_4;
	private TextView txt_sat_4;
	private TextView txt_sun_4;

	private TextView txt_mon_5;
	private TextView txt_tue_5;
	private TextView txt_wed_5;
	private TextView txt_thur_5;
	private TextView txt_fri_5;
	private TextView txt_sat_5;
	private TextView txt_sun_5;

	private TextView txt_mon_6;
	private TextView txt_tue_6;
	private TextView txt_wed_6;
	private TextView txt_thur_6;
	private TextView txt_fri_6;
	private TextView txt_sat_6;
	private TextView txt_sun_6;

	private TextView txt_mon_7;
	private TextView txt_tue_7;
	private TextView txt_wed_7;
	private TextView txt_thur_7;
	private TextView txt_fri_7;
	private TextView txt_sat_7;
	private TextView txt_sun_7;

	private TextView txt_mon_8;
	private TextView txt_tue_8;
	private TextView txt_wed_8;
	private TextView txt_thur_8;
	private TextView txt_fri_8;
	private TextView txt_sat_8;
	private TextView txt_sun_8;

	private String classId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lesson_table);

		initView();
		init();
		initListener();
		getData();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("课程表");

		txt_mon_1 = (TextView) findViewById(R.id.txt_mon_1);
		txt_tue_1 = (TextView) findViewById(R.id.txt_tue_1);
		txt_wed_1 = (TextView) findViewById(R.id.txt_wed_1);
		txt_thur_1 = (TextView) findViewById(R.id.txt_thur_1);
		txt_fri_1 = (TextView) findViewById(R.id.txt_fri_1);
		txt_sat_1 = (TextView) findViewById(R.id.txt_sat_1);
		txt_sun_1 = (TextView) findViewById(R.id.txt_sun_1);

		txt_mon_2 = (TextView) findViewById(R.id.txt_mon_2);
		txt_tue_2 = (TextView) findViewById(R.id.txt_tue_2);
		txt_wed_2 = (TextView) findViewById(R.id.txt_wed_2);
		txt_thur_2 = (TextView) findViewById(R.id.txt_thur_2);
		txt_fri_2 = (TextView) findViewById(R.id.txt_fri_2);
		txt_sat_2 = (TextView) findViewById(R.id.txt_sat_2);
		txt_sun_2 = (TextView) findViewById(R.id.txt_sun_2);

		txt_mon_3 = (TextView) findViewById(R.id.txt_mon_3);
		txt_tue_3 = (TextView) findViewById(R.id.txt_tue_3);
		txt_wed_3 = (TextView) findViewById(R.id.txt_wed_3);
		txt_thur_3 = (TextView) findViewById(R.id.txt_thur_3);
		txt_fri_3 = (TextView) findViewById(R.id.txt_fri_3);
		txt_sat_3 = (TextView) findViewById(R.id.txt_sat_3);
		txt_sun_3 = (TextView) findViewById(R.id.txt_sun_3);

		txt_mon_4 = (TextView) findViewById(R.id.txt_mon_4);
		txt_tue_4 = (TextView) findViewById(R.id.txt_tue_4);
		txt_wed_4 = (TextView) findViewById(R.id.txt_wed_4);
		txt_thur_4 = (TextView) findViewById(R.id.txt_thur_4);
		txt_fri_4 = (TextView) findViewById(R.id.txt_fri_4);
		txt_sat_4 = (TextView) findViewById(R.id.txt_sat_4);
		txt_sun_4 = (TextView) findViewById(R.id.txt_sun_4);

		txt_mon_5 = (TextView) findViewById(R.id.txt_mon_5);
		txt_tue_5 = (TextView) findViewById(R.id.txt_tue_5);
		txt_wed_5 = (TextView) findViewById(R.id.txt_wed_5);
		txt_thur_5 = (TextView) findViewById(R.id.txt_thur_5);
		txt_fri_5 = (TextView) findViewById(R.id.txt_fri_5);
		txt_sat_5 = (TextView) findViewById(R.id.txt_sat_5);
		txt_sun_5 = (TextView) findViewById(R.id.txt_sun_5);

		txt_mon_6 = (TextView) findViewById(R.id.txt_mon_6);
		txt_tue_6 = (TextView) findViewById(R.id.txt_tue_6);
		txt_wed_6 = (TextView) findViewById(R.id.txt_wed_6);
		txt_thur_6 = (TextView) findViewById(R.id.txt_thur_6);
		txt_fri_6 = (TextView) findViewById(R.id.txt_fri_6);
		txt_sat_6 = (TextView) findViewById(R.id.txt_sat_6);
		txt_sun_6 = (TextView) findViewById(R.id.txt_sun_6);

		txt_mon_7 = (TextView) findViewById(R.id.txt_mon_7);
		txt_tue_7 = (TextView) findViewById(R.id.txt_tue_7);
		txt_wed_7 = (TextView) findViewById(R.id.txt_wed_7);
		txt_thur_7 = (TextView) findViewById(R.id.txt_thur_7);
		txt_fri_7 = (TextView) findViewById(R.id.txt_fri_7);
		txt_sat_7 = (TextView) findViewById(R.id.txt_sat_7);
		txt_sun_7 = (TextView) findViewById(R.id.txt_sun_7);

		txt_mon_8 = (TextView) findViewById(R.id.txt_mon_8);
		txt_tue_8 = (TextView) findViewById(R.id.txt_tue_8);
		txt_wed_8 = (TextView) findViewById(R.id.txt_wed_8);
		txt_thur_8 = (TextView) findViewById(R.id.txt_thur_8);
		txt_fri_8 = (TextView) findViewById(R.id.txt_fri_8);
		txt_sat_8 = (TextView) findViewById(R.id.txt_sat_8);
		txt_sun_8 = (TextView) findViewById(R.id.txt_sun_8);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LessonTableActivity.this.finish();
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {// 教师端先选择班级，并将班级id传递过来
			ClassItem classItem = (ClassItem) bundle
					.getSerializable("classItem");
			classId = classItem.getClassId();
		} else {// 学生端直接使用班级id
			classId = user.getClassinfoId();
		}
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_COURSE_TABLE);
		map.put("classId", classId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				LessonTableActivity.this, user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("课程表返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							String result = jsonObject
									.optString("courseTableList");

							Type listType = new TypeToken<List<LessonTable>>() {
							}.getType();
							lessonTableList = (List<LessonTable>) GsonUtil
									.jsonToList(result, listType);

							if (lessonTableList != null
									&& lessonTableList.size() > 0) {
								setLessonTable();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void setLessonTable() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.MONDAY:
			txt_mon_1.setTextColor(Color.parseColor("#f19149"));
			txt_mon_2.setTextColor(Color.parseColor("#f19149"));
			txt_mon_3.setTextColor(Color.parseColor("#f19149"));
			txt_mon_4.setTextColor(Color.parseColor("#f19149"));
			txt_mon_5.setTextColor(Color.parseColor("#f19149"));
			txt_mon_6.setTextColor(Color.parseColor("#f19149"));
			txt_mon_7.setTextColor(Color.parseColor("#f19149"));
			txt_mon_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.TUESDAY:
			txt_tue_1.setTextColor(Color.parseColor("#f19149"));
			txt_tue_2.setTextColor(Color.parseColor("#f19149"));
			txt_tue_3.setTextColor(Color.parseColor("#f19149"));
			txt_tue_4.setTextColor(Color.parseColor("#f19149"));
			txt_tue_5.setTextColor(Color.parseColor("#f19149"));
			txt_tue_6.setTextColor(Color.parseColor("#f19149"));
			txt_tue_7.setTextColor(Color.parseColor("#f19149"));
			txt_tue_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.WEDNESDAY:
			txt_wed_1.setTextColor(Color.parseColor("#f19149"));
			txt_wed_2.setTextColor(Color.parseColor("#f19149"));
			txt_wed_3.setTextColor(Color.parseColor("#f19149"));
			txt_wed_4.setTextColor(Color.parseColor("#f19149"));
			txt_wed_5.setTextColor(Color.parseColor("#f19149"));
			txt_wed_6.setTextColor(Color.parseColor("#f19149"));
			txt_wed_7.setTextColor(Color.parseColor("#f19149"));
			txt_wed_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.THURSDAY:
			txt_thur_1.setTextColor(Color.parseColor("#f19149"));
			txt_thur_2.setTextColor(Color.parseColor("#f19149"));
			txt_thur_3.setTextColor(Color.parseColor("#f19149"));
			txt_thur_4.setTextColor(Color.parseColor("#f19149"));
			txt_thur_5.setTextColor(Color.parseColor("#f19149"));
			txt_thur_6.setTextColor(Color.parseColor("#f19149"));
			txt_thur_7.setTextColor(Color.parseColor("#f19149"));
			txt_thur_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.FRIDAY:
			txt_fri_1.setTextColor(Color.parseColor("#f19149"));
			txt_fri_2.setTextColor(Color.parseColor("#f19149"));
			txt_fri_3.setTextColor(Color.parseColor("#f19149"));
			txt_fri_4.setTextColor(Color.parseColor("#f19149"));
			txt_fri_5.setTextColor(Color.parseColor("#f19149"));
			txt_fri_6.setTextColor(Color.parseColor("#f19149"));
			txt_fri_7.setTextColor(Color.parseColor("#f19149"));
			txt_fri_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.SATURDAY:
			txt_sat_1.setTextColor(Color.parseColor("#f19149"));
			txt_sat_2.setTextColor(Color.parseColor("#f19149"));
			txt_sat_3.setTextColor(Color.parseColor("#f19149"));
			txt_sat_4.setTextColor(Color.parseColor("#f19149"));
			txt_sat_5.setTextColor(Color.parseColor("#f19149"));
			txt_sat_6.setTextColor(Color.parseColor("#f19149"));
			txt_sat_7.setTextColor(Color.parseColor("#f19149"));
			txt_sat_8.setTextColor(Color.parseColor("#f19149"));
			break;
		case Calendar.SUNDAY:
			txt_sun_1.setTextColor(Color.parseColor("#f19149"));
			txt_sun_2.setTextColor(Color.parseColor("#f19149"));
			txt_sun_3.setTextColor(Color.parseColor("#f19149"));
			txt_sun_4.setTextColor(Color.parseColor("#f19149"));
			txt_sun_5.setTextColor(Color.parseColor("#f19149"));
			txt_sun_6.setTextColor(Color.parseColor("#f19149"));
			txt_sun_7.setTextColor(Color.parseColor("#f19149"));
			txt_sun_8.setTextColor(Color.parseColor("#f19149"));
			break;
		}

		// 第一节课数据
		LessonTable course1 = lessonTableList.get(0);
		txt_mon_1.setText(course1.getMon());
		txt_tue_1.setText(course1.getTue());
		txt_wed_1.setText(course1.getWed());
		txt_thur_1.setText(course1.getThur());
		txt_fri_1.setText(course1.getFri());
		txt_sat_1.setText(course1.getSat());
		txt_sun_1.setText(course1.getSun());

		// 第二节课数据
		LessonTable course2 = lessonTableList.get(1);
		txt_mon_2.setText(course2.getMon());
		txt_tue_2.setText(course2.getTue());
		txt_wed_2.setText(course2.getWed());
		txt_thur_2.setText(course2.getThur());
		txt_fri_2.setText(course2.getFri());
		txt_sat_2.setText(course2.getSat());
		txt_sun_2.setText(course2.getSun());

		// 第三节课数据
		LessonTable course3 = lessonTableList.get(2);
		txt_mon_3.setText(course3.getMon());
		txt_tue_3.setText(course3.getTue());
		txt_wed_3.setText(course3.getWed());
		txt_thur_3.setText(course3.getThur());
		txt_fri_3.setText(course3.getFri());
		txt_sat_3.setText(course3.getSat());
		txt_sun_3.setText(course3.getSun());

		// 第四节课数据
		LessonTable course4 = lessonTableList.get(3);
		txt_mon_4.setText(course4.getMon());
		txt_tue_4.setText(course4.getTue());
		txt_wed_4.setText(course4.getWed());
		txt_thur_4.setText(course4.getThur());
		txt_fri_4.setText(course4.getFri());
		txt_sat_4.setText(course4.getSat());
		txt_sun_4.setText(course4.getSun());

		// 第五节课数据
		LessonTable course5 = lessonTableList.get(4);
		txt_mon_5.setText(course5.getMon());
		txt_tue_5.setText(course5.getTue());
		txt_wed_5.setText(course5.getWed());
		txt_thur_5.setText(course5.getThur());
		txt_fri_5.setText(course5.getFri());
		txt_sat_5.setText(course5.getSat());
		txt_sun_5.setText(course5.getSun());

		// 第六节课数据
		LessonTable course6 = lessonTableList.get(5);
		txt_mon_6.setText(course6.getMon());
		txt_tue_6.setText(course6.getTue());
		txt_wed_6.setText(course6.getWed());
		txt_thur_6.setText(course6.getThur());
		txt_fri_6.setText(course6.getFri());
		txt_sat_6.setText(course6.getSat());
		txt_sun_6.setText(course6.getSun());

		// 第七节课数据
		LessonTable course7 = lessonTableList.get(6);
		txt_mon_7.setText(course7.getMon());
		txt_tue_7.setText(course7.getTue());
		txt_wed_7.setText(course7.getWed());
		txt_thur_7.setText(course7.getThur());
		txt_fri_7.setText(course7.getFri());
		txt_sat_7.setText(course7.getSat());
		txt_sun_7.setText(course7.getSun());

		// 第八节课数据
		LessonTable course8 = lessonTableList.get(7);
		txt_mon_8.setText(course8.getMon());
		txt_tue_8.setText(course8.getTue());
		txt_wed_8.setText(course8.getWed());
		txt_thur_8.setText(course8.getThur());
		txt_fri_8.setText(course8.getFri());
		txt_sat_8.setText(course8.getSat());
		txt_sun_8.setText(course8.getSun());
	}

}
