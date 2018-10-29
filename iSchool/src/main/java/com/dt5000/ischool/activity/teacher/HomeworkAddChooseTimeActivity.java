package com.dt5000.ischool.activity.teacher;

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
import android.widget.TimePicker;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;

/**
 * 发布作业时选择截止时间页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午2:40:05
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkAddChooseTimeActivity
 * @Description
 */
public class HomeworkAddChooseTimeActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button btn_ok;

	private int mYear;
	private int mMouth;
	private int mDay;
	private int mHour;
	private int mMinter;
	private String paramTime = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_add_choose_time);

		initView();
		initListener();
		init();
	}

	@SuppressLint("NewApi")
	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("截止日期");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			datePicker.setCalendarViewShown(false);
		}
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAddChooseTimeActivity.this.finish();
			}
		});

		// 点击设置时间并返回给上一个页面
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				returnTime();
			}
		});
	}

	private void init() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.add(Calendar.HOUR_OF_DAY, 1);// 默认比当前小时数多1小时
		mYear = calendar.get(Calendar.YEAR);
		mMouth = calendar.get(Calendar.MONTH);// month数从0开始
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
		mMinter = 0;// 默认分钟数显式0
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

		timePicker.setCurrentHour(mHour);
		timePicker.setCurrentMinute(mMinter);
		timePicker
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {
						mHour = hourOfDay;
						mMinter = minute;
						updateDisplay();
					}
				});
	}

	private void updateDisplay() {
		mMouth++;// 月数显式调整（0-11改为1-12）

		StringBuilder stringBuilderParamTime = new StringBuilder().append(
				mYear + "-" + format(mMouth) + "-" + format(mDay)).append(
				" " + format(mHour) + ":" + format(mMinter) + ":00");
		paramTime = stringBuilderParamTime.toString();
		MLog.i("选择的时间：" + paramTime);

		StringBuilder stringBuilderShowText = new StringBuilder().append(
				mYear + "-" + format(mMouth) + "-" + format(mDay)).append(
				" " + format(mHour) + ":" + format(mMinter) + "    确定");
		btn_ok.setText(stringBuilderShowText.toString());

		mMouth--;// 月数实际数回位
	}

	/** 个位数时间显式修正，补全2位 */
	private String format(int x) {
		if (Integer.toString(x).length() == 1) {
			return "0" + Integer.toString(x);
		} else {
			return Integer.toString(x);
		}
	}

	private void returnTime() {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			Date chooseDate = simpleDateFormat.parse(paramTime);
			int dateFlag = chooseDate.compareTo(new Date());// 判断选择的时间是否小于当前时间
			if (dateFlag < 0) {
				DialogAlert.show(this, "时间选择错误");
				return;
			}

			// 将选择后的时间返回给上一个页面
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("paramTime", paramTime);
			intent.putExtras(bundle);
			HomeworkAddChooseTimeActivity.this.setResult(RESULT_OK, intent);
			HomeworkAddChooseTimeActivity.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
