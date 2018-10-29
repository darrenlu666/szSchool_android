package com.dt5000.ischool.activity.teacher;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.AttendancePagerListAdapter;
import com.dt5000.ischool.entity.AttendanceInfo;
import com.dt5000.ischool.entity.AttendancePage;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 考勤列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年10月10日 下午4:25:00
 * @ClassInfo com.dt5000.ischool.activity.teacher.AttendancePageListActivity
 * @Description
 */
public class AttendancePageListActivity extends Activity {

	private TextView txt_title;
	private TextView txt_topbar_btn;
	private LinearLayout lLayout_back;
	private ViewPager viewpager_attendance;
	private LinearLayout lLayout_left;
	private LinearLayout lLayout_right;
	private TextView txt_date;
	private ImageView img_arrow_left;
	private ImageView img_arrow_right;

	private AttendancePagerListAdapter attendancePagerListAdapter;
	private List<AttendancePage> attendancePageList;
	private List<AttendancePage> attendancePageListAll;
	private List<AttendancePage> attendancePageListArrive;
	private List<AttendancePage> attendancePageListNotArrive;
	private List<AttendancePage> attendancePageListVacate;
	private ClassItem classItem;
	private int currentIndex = 0;
	private User user;
	private FinalHttp finalHttp;
	private String paramDate;
	private int paramDayNum = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_page_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("考勤");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("筛选");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		viewpager_attendance = (ViewPager) findViewById(R.id.viewpager_attendance);
		lLayout_left = (LinearLayout) findViewById(R.id.lLayout_left);
		lLayout_right = (LinearLayout) findViewById(R.id.lLayout_right);
		txt_date = (TextView) findViewById(R.id.txt_date);
		img_arrow_left = (ImageView) findViewById(R.id.img_arrow_left);
		img_arrow_right = (ImageView) findViewById(R.id.img_arrow_right);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendancePageListActivity.this.finish();
			}
		});

		// 点击筛选
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ListAdapter adapter = new ArrayAdapter<String>(
						AttendancePageListActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"未到校", "已到校", "请假学生", "全部" });

				new AlertDialog.Builder(AttendancePageListActivity.this)
						.setSingleChoiceItems(adapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										if (attendancePagerListAdapter == null) {
											return;
										}
										
										switch (which) {
										case 0:
											attendancePageList = attendancePageListNotArrive;
											break;
										case 1:
											attendancePageList = attendancePageListArrive;
											break;
										case 2:
											attendancePageList = attendancePageListVacate;
											break;
										case 3:
											attendancePageList = attendancePageListAll;
											break;
										}
										
										attendancePagerListAdapter = new AttendancePagerListAdapter(
												AttendancePageListActivity.this,
												attendancePageList);
										viewpager_attendance
												.setAdapter(attendancePagerListAdapter);
										
										viewpager_attendance.setCurrentItem(currentIndex);
									}
								}).show();

			}
		});

		// 点击筛选时间
		txt_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ListAdapter adapter = new ArrayAdapter<String>(
						AttendancePageListActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"选择日期", "近期考勤" });

				new AlertDialog.Builder(AttendancePageListActivity.this)
						.setSingleChoiceItems(adapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										switch (which) {
										case 0:
											paramDayNum = 1;
											chooseTime();
											break;
										case 1:
											paramDayNum = 7;
											paramDate = TimeUtil
													.yearMonthDayFormat(new Date()
															.getTime());
											getData();
											break;
										}
									}
								}).show();
			}
		});

		// 点击左箭头
		lLayout_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (attendancePageList != null && attendancePageList.size() > 0) {
					if (currentIndex > 0) {
						viewpager_attendance.setCurrentItem(currentIndex - 1,
								true);
					}
				}
			}
		});

		// 点击右箭头
		lLayout_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (attendancePageList != null && attendancePageList.size() > 0) {
					if (currentIndex < attendancePageList.size() - 1) {
						viewpager_attendance.setCurrentItem(currentIndex + 1,
								true);
					}
				}
			}
		});

		// 切换监听
		viewpager_attendance
				.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						currentIndex = position;

						if (currentIndex == 0) {
							img_arrow_left.setVisibility(View.GONE);
						} else {
							img_arrow_left.setVisibility(View.VISIBLE);
						}

						if (currentIndex == attendancePageList.size() - 1) {
							img_arrow_right.setVisibility(View.GONE);
						} else {
							img_arrow_right.setVisibility(View.VISIBLE);
						}

						AttendancePage attendancePage = attendancePageList
								.get(position);
						txt_date.setText(attendancePage.getDateCN());
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	private void init() {
		classItem = (ClassItem) getIntent().getExtras().getSerializable("classItem");

		txt_title.setText(classItem.getClassName());

		user = User.getUser(this);
		finalHttp = new FinalHttp();
		attendancePageList = new ArrayList<AttendancePage>();

		paramDate = TimeUtil.yearMonthDayFormat(new Date().getTime());
	}

	private void chooseTime() {
		Calendar calendar = Calendar.getInstance();
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH);
		int d = calendar.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(this, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				Date setDate = calendar.getTime();
				MLog.i("设置时间：" + TimeUtil.fullTimeFormat(setDate));
				calendar.setTime(new Date());

				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				Date judgeDate = calendar.getTime();
				MLog.i("限时时间：" + TimeUtil.fullTimeFormat(judgeDate));
				calendar.setTime(new Date());

				int dateFlag = setDate.compareTo(judgeDate);
				if (dateFlag > 0) {
					DialogAlert.show(AttendancePageListActivity.this, "暂无记录");
					return;
				}

				paramDate = TimeUtil.yearMonthDayFormat(setDate.getTime());
				getData();
			}
		}, y, m, d).show();
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_ATTENDANCE_LIST_TEACHER);
		map.put("userId", user.getUserId());
		map.put("classId", classItem.getClassId());
		map.put("pageNo", String.valueOf(paramDayNum));
		map.put("date", paramDate);
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
							String result = obj.optString("list");

							// 解析实体类
							Type listType = new TypeToken<List<AttendancePage>>() {
							}.getType();
							List<AttendancePage> data = (List<AttendancePage>) GsonUtil
									.jsonToList(result, listType);
							if (data != null && data.size() > 0) {
								attendancePageList = data;
								setFliterAttendancePageList(attendancePageList);

								// 设置适配器
								attendancePagerListAdapter = new AttendancePagerListAdapter(
										AttendancePageListActivity.this,
										attendancePageList);
								viewpager_attendance.setAdapter(attendancePagerListAdapter);

								txt_date.setText(attendancePageList.get(0)
										.getDateCN());

								img_arrow_left.setVisibility(View.GONE);

								if (data.size() > 1) {
									img_arrow_right.setVisibility(View.VISIBLE);
								} else {
									img_arrow_right.setVisibility(View.GONE);
								}
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

	private void setFliterAttendancePageList(List<AttendancePage> list) {
		if (attendancePageListAll != null && attendancePageListAll.size() > 0) {
			attendancePageListAll.clear();
			attendancePageListAll = null;
		}
		attendancePageListAll = new ArrayList<AttendancePage>();

		if (attendancePageListArrive != null
				&& attendancePageListArrive.size() > 0) {
			attendancePageListArrive.clear();
			attendancePageListArrive = null;
		}
		attendancePageListArrive = new ArrayList<AttendancePage>();

		if (attendancePageListNotArrive != null
				&& attendancePageListNotArrive.size() > 0) {
			attendancePageListNotArrive.clear();
			attendancePageListNotArrive = null;
		}
		attendancePageListNotArrive = new ArrayList<AttendancePage>();

		if (attendancePageListVacate != null
				&& attendancePageListVacate.size() > 0) {
			attendancePageListVacate.clear();
			attendancePageListVacate = null;
		}
		attendancePageListVacate = new ArrayList<AttendancePage>();

		// 全部学生
		attendancePageListAll = list;

		// 请假学生
		for (int i = 0; i < list.size(); i++) {
			AttendancePage attendancePage = list.get(i);
			List<AttendanceInfo> infoList = attendancePage.getList();
			List<AttendanceInfo> newInfoList = new ArrayList<AttendanceInfo>();
			for (int j = 0; j < infoList.size(); j++) {
				AttendanceInfo info = infoList.get(j);
				if ("1".equals(info.getIfLeave())) {
					newInfoList.add(info);
				}
			}

			AttendancePage newAttendancePage = new AttendancePage();
			newAttendancePage.setDate(attendancePage.getDate());
			newAttendancePage.setDateCN(attendancePage.getDateCN());
			newAttendancePage.setList(newInfoList);
			attendancePageListVacate.add(newAttendancePage);
		}

		// 已到校学生
		for (int i = 0; i < list.size(); i++) {
			AttendancePage attendancePage = list.get(i);
			List<AttendanceInfo> infoList = attendancePage.getList();
			List<AttendanceInfo> newInfoList = new ArrayList<AttendanceInfo>();
			for (int j = 0; j < infoList.size(); j++) {
				AttendanceInfo info = infoList.get(j);
				if (!CheckUtil.stringIsBlank(info.getArriveTime())) {
					newInfoList.add(info);
				}
			}

			AttendancePage newAttendancePage = new AttendancePage();
			newAttendancePage.setDate(attendancePage.getDate());
			newAttendancePage.setDateCN(attendancePage.getDateCN());
			newAttendancePage.setList(newInfoList);
			attendancePageListArrive.add(newAttendancePage);
		}

		// 未到校学生
		for (int i = 0; i < list.size(); i++) {
			AttendancePage attendancePage = list.get(i);
			List<AttendanceInfo> infoList = attendancePage.getList();
			List<AttendanceInfo> newInfoList = new ArrayList<AttendanceInfo>();
			for (int j = 0; j < infoList.size(); j++) {
				AttendanceInfo info = infoList.get(j);
				if (CheckUtil.stringIsBlank(info.getArriveTime())) {
					newInfoList.add(info);
				}
			}

			AttendancePage newAttendancePage = new AttendancePage();
			newAttendancePage.setDate(attendancePage.getDate());
			newAttendancePage.setDateCN(attendancePage.getDateCN());
			newAttendancePage.setList(newInfoList);
			attendancePageListNotArrive.add(newAttendancePage);
		}
	}

}
