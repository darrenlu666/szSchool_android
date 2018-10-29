package com.dt5000.ischool.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.TimeUtil;

/**
 * 考勤请假单选择时间页面
 * 
 * @author 周锋
 * @date 2016年10月14日 下午4:57:36
 * @ClassInfo com.dt5000.ischool.activity.AttendanceVacateChooseTimeActivity
 * @Description
 */
public class AttendanceVacateChooseTimeActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private DatePicker datePicker;
	private Button btn_ok;
	private AbstractWheel wheel_hour;

	private int mYear;
	private int mMouth;
	private int mDay;
	private int mHour;
	private int mMinter;
	private String paramTime;
	private String[] hours;
	private String type;
	private String role;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_vacate_choose_time);

		initView();
		initListener();
		init();
	}

	@SuppressLint("NewApi")
	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("设置时间");
		wheel_hour = (AbstractWheel) findViewById(R.id.wheel_hour);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			datePicker.setCalendarViewShown(false);
		}
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendanceVacateChooseTimeActivity.this.finish();
			}
		});
		
		// 点击设置时间并返回给上一个页面
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				returnTime();
			}
		});

		// 设置小时滚轮监听
		wheel_hour.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				if ("start".equals(type)) {
					mHour = wheel.getCurrentItem() + 7;
				} else {
					mHour = wheel.getCurrentItem() + 8;
				}

				updateDisplay();
			}
		});
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		type = bundle.getString("type");
		role = bundle.getString("role");
		String time = bundle.getString("time");

		// 初始化小时选择项
		if ("start".equals(type)) {
			txt_title.setText("设置开始时间");

			mHour = 7;

			hours = new String[] { "07:00", "08:00", "09:00", "10:00", "11:00",
					"12:00", "13:00", "14:00", "15:00", "16:00" };
			wheel_hour
					.setViewAdapter(new ArrayWheelAdapter<String>(this, hours));
			wheel_hour.setCurrentItem(0);
		} else {
			txt_title.setText("设置结束时间");

			mHour = 17;

			hours = new String[] { "08:00", "09:00", "10:00", "11:00", "12:00",
					"13:00", "14:00", "15:00", "16:00", "17:00" };
			wheel_hour
					.setViewAdapter(new ArrayWheelAdapter<String>(this, hours));
			wheel_hour.setCurrentItem(9);
		}

		// 初始化日期选择项
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		if (!CheckUtil.stringIsBlank(time)) {// 解析上个页面传过来的时间
			Date date = TimeUtil.parseTime(
					TimeUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE, time);
			calendar.setTime(date);

			// 调整滚轮的显示数据
			mHour = calendar.get(Calendar.HOUR_OF_DAY);
			if ("start".equals(type)) {
				wheel_hour.setCurrentItem(mHour - 7);
			} else {
				wheel_hour.setCurrentItem(mHour - 8);
			}
		} else {
			calendar.add(Calendar.DAY_OF_MONTH, 1);// 上个页面未传时间，则使用比当前天数多1天
		}
		mYear = calendar.get(Calendar.YEAR);
		mMouth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mMinter = 0;
		updateDisplay();

		datePicker.init(mYear, mMouth, mDay,
				new DatePicker.OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mYear = year;
						mMouth = monthOfYear;
						mDay = dayOfMonth;
						updateDisplay();
					}
				});
	}

	private void updateDisplay() {
		mMouth++;// 月数显式调整（0-11改为1-12）

		StringBuilder stringBuilderParamTime = new StringBuilder().append(
				mYear + "-" + format(mMouth) + "-" + format(mDay)).append(
				" " + format(mHour) + ":" + format(mMinter));
		paramTime = stringBuilderParamTime.toString();

		btn_ok.setText(paramTime + "    确定");

		mMouth--;// 月数实际数回位
	}

	/**
	 * 个位数时间显式修正，补全2位
	 * 
	 * @param x
	 * @return
	 */
	private String format(int x) {
		if (Integer.toString(x).length() == 1) {
			return "0" + Integer.toString(x);
		} else {
			return Integer.toString(x);
		}
	}

	private void returnTime() {
		try {
			if ("student".equals(role)) {//学生端需要判断选择的时间是否小于当前时间
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm", Locale.CHINA);
				Date chooseDate = simpleDateFormat.parse(paramTime);
				int dateFlag = chooseDate.compareTo(new Date());
				if (dateFlag < 0) {
					DialogAlert.show(this, "请选择正确的时间");
					return;
				}
			}

			// 将选择后的时间返回给上一个页面
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("paramTime", paramTime);
			intent.putExtras(bundle);
			AttendanceVacateChooseTimeActivity.this
					.setResult(RESULT_OK, intent);
			AttendanceVacateChooseTimeActivity.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
