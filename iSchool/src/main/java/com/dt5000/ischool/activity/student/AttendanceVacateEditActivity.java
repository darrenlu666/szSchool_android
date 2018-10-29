package com.dt5000.ischool.activity.student;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
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
 * 考勤请假单编辑页面：学生端
 * 
 * @author 周锋
 * @date 2016年10月10日 下午2:07:51
 * @ClassInfo com.dt5000.ischool.activity.student.AttendanceVacateEditActivity
 * @Description
 */
public class AttendanceVacateEditActivity extends Activity {

	private TextView txt_title;
	private TextView txt_name;
	private LinearLayout lLayout_back;
	private EditText edittext_day;
	private EditText edittext_reason;
	private TextView txt_time_start;
	private TextView txt_time_end;
	private Button btn_submit;
	private Button btn_cancel;

	private User user;
	private FinalHttp finalHttp;
	private boolean isNew = true;
	private Vacate vacate;
	private String paramStartTime;
	private String paramEndTime;
	private String paramDay;
	private String paramReason;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_vacate_edit);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("请假单");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_name = (TextView) findViewById(R.id.txt_name);
		edittext_day = (EditText) findViewById(R.id.edittext_day);
		edittext_reason = (EditText) findViewById(R.id.edittext_reason);
		txt_time_start = (TextView) findViewById(R.id.txt_time_start);
		txt_time_end = (TextView) findViewById(R.id.txt_time_end);
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
				bundle.putString("role", "student");
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
				bundle.putString("role", "student");
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
				// 检查开始时间
				paramStartTime = txt_time_start.getText().toString().trim();
				if (CheckUtil.stringIsBlank(paramStartTime)) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请选择开始时间");
					return;
				}
				Date startDate = TimeUtil.parseTime(
						TimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE,
						paramStartTime);
				paramStartTime = TimeUtil.fullTimeFormat(startDate);

				// 检查结束时间
				paramEndTime = txt_time_end.getText().toString().trim();
				if (CheckUtil.stringIsBlank(paramEndTime)) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请选择结束时间");
					return;
				}
				Date endDate = TimeUtil.parseTime(
						TimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE,
						paramEndTime);
				paramEndTime = TimeUtil.fullTimeFormat(endDate);

				// 检查天数
				paramDay = edittext_day.getText().toString().trim();
				if (CheckUtil.stringIsBlank(paramDay)) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请输入请假天数");
					return;
				}
				if (Float.parseFloat(paramDay) < 0.5) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请输入正确的天数");
					return;
				}

				// 检查请假原因
				paramReason = edittext_reason.getText().toString().trim();
				if (CheckUtil.stringIsBlank(paramReason)) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请输入请假原因");
					return;
				}
				if (CheckUtil.containsEmoji(paramReason)) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"不支持emoji表情，请重新填写");
					return;
				}

				// 判断开始时间是否小于结束时间
				int dateFlag = startDate.compareTo(endDate);
				if (dateFlag >= 0) {
					DialogAlert.show(AttendanceVacateEditActivity.this,
							"请选择正确的时间");
					return;
				}

				new AlertDialog.Builder(AttendanceVacateEditActivity.this)
						.setMessage("是否提交请假单？")
						.setPositiveButton("提交",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										requestToSubmit();
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 点击撤销
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isNew) {// 新建页面点击取消
					AttendanceVacateEditActivity.this.finish();
				} else {// 修改页面点击发送请求删除
					new AlertDialog.Builder(AttendanceVacateEditActivity.this)
							.setMessage("是否删除此请假单？")
							.setPositiveButton("删除",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											requestToCancel();
										}
									}).setNegativeButton("取消", null).show();
				}
			}
		});
	}

	private void init() {
		user = User.getUser(this);
		txt_name.setText(user.getRealName());
		finalHttp = new FinalHttp();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			isNew = false;
			vacate = (Vacate) bundle.getSerializable("vacate");
			txt_time_start.setText(TimeUtil
					.yearMonthDayHourMinuteFormat(TimeUtil.parseFullTime(
							vacate.getStartTime()).getTime()));
			txt_time_end.setText(TimeUtil.yearMonthDayHourMinuteFormat(TimeUtil
					.parseFullTime(vacate.getEndTime()).getTime()));
			edittext_day.setText(vacate.getDays());
			edittext_reason.setText(vacate.getReason());

			String isAgree = vacate.getIsAgree();
			if ("0".equals(isAgree)) {// 未确认，可以修改和撤销
				btn_submit.setText("修改");
				btn_cancel.setText("删除");
			} else {// 已确认和已撤销，无法操作
				btn_submit.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
				edittext_day.setEnabled(false);
				edittext_reason.setEnabled(false);
				txt_time_start.setEnabled(false);
				txt_time_end.setEnabled(false);
			}
		} else {
			btn_cancel.setText("取消");
		}
	}

	private void requestToSubmit() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VACATE_SAVE);
		map.put("roleId", String.valueOf(user.getRole()));
		map.put("studentId", user.getUserId());
		if (vacate != null) {
			map.put("id", vacate.getId());
		} else {
			map.put("id", "");
		}
		map.put("isAgree", "0");
		map.put("type", "0");
		map.put("days", paramDay);
		map.put("reason", paramReason);
		map.put("startTime", paramStartTime);
		map.put("endTime", paramEndTime);
		map.put("submitTime", TimeUtil.fullTimeFormat(new Date()));
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
										"提交请假单出现问题，请稍后再试");
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogAlert.show(AttendanceVacateEditActivity.this,
									"提交请假单出现问题，请稍后再试");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						DialogAlert.show(AttendanceVacateEditActivity.this,
								"提交请假单出现问题，请稍后再试");
					}
				});
	}

	private void requestToCancel() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VACATE_DELETE);
		map.put("roleId", String.valueOf(user.getRole()));
		map.put("userId", user.getUserId());
		map.put("id", vacate.getId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("删除请假单地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("删除请假单返回结果：" + t);
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
										"当前无法操作，请稍后再试");
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogAlert.show(AttendanceVacateEditActivity.this,
									"当前无法操作，请稍后再试");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						DialogAlert.show(AttendanceVacateEditActivity.this,
								"当前无法操作，请稍后再试");
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
