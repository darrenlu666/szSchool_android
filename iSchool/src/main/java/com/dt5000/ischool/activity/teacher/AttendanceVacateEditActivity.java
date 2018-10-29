package com.dt5000.ischool.activity.teacher;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.AttendanceVacateChooseTimeActivity;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.Vacate;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 考勤请假单编辑页面：教师端
 * 
 * @author 周锋
 * @date 2016年10月12日 下午2:16:40
 * @ClassInfo com.dt5000.ischool.activity.teacher.AttendanceVacateEditActivity
 * @Description
 */
public class AttendanceVacateEditActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private TextView txt_name;
	private TextView txt_time_start;
	private TextView txt_time_end;
	private EditText edittext_day;
	private TextView txt_reason;
	private Button btn_submit;
	private Button btn_cancel;

	private User user;
	private FinalHttp finalHttp;
	private Vacate vacate;
	private String paramStartTime;
	private String paramEndTime;
	private String paramDay;
	private boolean isAgreeVacate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_vacate_edit_teacher);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("请假单");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_time_start = (TextView) findViewById(R.id.txt_time_start);
		txt_time_end = (TextView) findViewById(R.id.txt_time_end);
		txt_reason = (TextView) findViewById(R.id.txt_reason);
		edittext_day = (EditText) findViewById(R.id.edittext_day);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendanceVacateEditActivity.this.finish();
			}
		});

		// 点击选择开始时间
		txt_time_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AttendanceVacateEditActivity.this,
						AttendanceVacateChooseTimeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("type", "start");
				bundle.putString("role", "teacher");
				String time = txt_time_start.getText().toString().trim();
				bundle.putString("time", time);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
		});

		// 点击选择结束时间
		txt_time_end.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AttendanceVacateEditActivity.this,
						AttendanceVacateChooseTimeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("type", "end");
				bundle.putString("role", "teacher");
				String time = txt_time_end.getText().toString().trim();
				bundle.putString("time", time);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});

		// 点击确认
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 标识批准
				isAgreeVacate = true;

				check();
			}
		});

		// 点击撤销
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 标识不批准
				isAgreeVacate = false;

				check();
			}
		});
	}

	private void init() {
		user = User.getUser(this);
		finalHttp = new FinalHttp();

		vacate = (Vacate) getIntent().getExtras().getSerializable("vacate");
		txt_name.setText(vacate.getRealName());
		txt_time_start.setText(TimeUtil.yearMonthDayHourMinuteFormat(TimeUtil
				.parseFullTime(vacate.getStartTime()).getTime()));
		txt_time_end.setText(TimeUtil.yearMonthDayHourMinuteFormat(TimeUtil
				.parseFullTime(vacate.getEndTime()).getTime()));
		edittext_day.setText(vacate.getDays());
		txt_reason.setText(vacate.getReason());

		String isAgree = vacate.getIsAgree();
		if ("2".equals(isAgree)) {// 已撤销状态下，按钮置灰
			btn_cancel.setBackgroundResource(R.drawable.shape_rect_button_bg_3);
			btn_cancel.setEnabled(false);
		}
	}

	private void check() {
		// 检查开始时间
		paramStartTime = txt_time_start.getText().toString().trim();
		if (CheckUtil.stringIsBlank(paramStartTime)) {
			DialogAlert.show(AttendanceVacateEditActivity.this, "请选择开始时间");
			return;
		}
		Date startDate = TimeUtil.parseTime(
				TimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE, paramStartTime);
		paramStartTime = TimeUtil.fullTimeFormat(startDate);

		// 检查结束时间
		paramEndTime = txt_time_end.getText().toString().trim();
		if (CheckUtil.stringIsBlank(paramEndTime)) {
			DialogAlert.show(AttendanceVacateEditActivity.this, "请选择结束时间");
			return;
		}
		Date endDate = TimeUtil.parseTime(
				TimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE, paramEndTime);
		paramEndTime = TimeUtil.fullTimeFormat(endDate);

		// 检查天数
		paramDay = edittext_day.getText().toString().trim();
		if (CheckUtil.stringIsBlank(paramDay)) {
			DialogAlert.show(AttendanceVacateEditActivity.this, "请输入请假天数");
			return;
		}

		// 判断开始时间是否小于结束时间
		int dateFlag = startDate.compareTo(endDate);
		if (dateFlag >= 0) {
			DialogAlert.show(AttendanceVacateEditActivity.this, "请选择正确的时间");
			return;
		}

		Builder builder = new AlertDialog.Builder(this);
		String positiveButtonText = "确定";
		if (isAgreeVacate) {
			builder.setMessage("是否批准此请假单？");
			positiveButtonText = "批准";
		} else {
			builder.setMessage("是否驳回此请假单？");
			positiveButtonText = "驳回";
		}
		builder.setPositiveButton(positiveButtonText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestToSubmit();
					}
				});
		builder.setNegativeButton("取消", null);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void requestToSubmit() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VACATE_SAVE);
		map.put("roleId", String.valueOf(user.getRole()));
		map.put("studentId", vacate.getStudentBaseInfoId());
		map.put("id", vacate.getId());
		if (isAgreeVacate) {
			map.put("isAgree", "1");
		} else {
			map.put("isAgree", "2");
		}
		map.put("type", "0");
		map.put("days", paramDay);
		map.put("reason", vacate.getReason());
		map.put("startTime", paramStartTime);
		map.put("endTime", paramEndTime);
		map.put("submitTime", vacate.getSubmitTime());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("提交请假单地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("提交请假单返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String resultStatus = obj.optString("resultStatus");
							if ("200".equals(resultStatus)) {
								AttendanceVacateEditActivity.this
										.setResult(RESULT_OK);
								AttendanceVacateEditActivity.this.finish();
							} else {
								DialogAlert.show(
										AttendanceVacateEditActivity.this,
										"请假单操作出现问题，请稍后再试");
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogAlert.show(AttendanceVacateEditActivity.this,
									"请假单操作出现问题，请稍后再试");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						DialogAlert.show(AttendanceVacateEditActivity.this,
								"请假单操作出现问题，请稍后再试");
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:// 选择开始时间返回结果
			if (resultCode == RESULT_OK) {
				String time = data.getExtras().getString("paramTime");
				txt_time_start.setText(time);
			}
			break;
		case 1:// 选择结束时间返回结果
			if (resultCode == RESULT_OK) {
				String time = data.getExtras().getString("paramTime");
				txt_time_end.setText(time);
			}
			break;
		}
	}

}
