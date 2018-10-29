package com.dt5000.ischool.activity;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.widget.UISwitchButton;

/**
 * 免打扰设置页面
 * 
 * @author 周锋
 * @date 2016年2月1日 下午2:02:17
 * @ClassInfo com.dt5000.ischool.activity.DisturbSetActivity
 * @Description
 */
public class DisturbSetActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private LinearLayout lLayout_time;
	private AbstractWheel wheel_time1;
	private AbstractWheel wheel_time2;
	private UISwitchButton uiswitch_disturb;

	private String[] times;
	private String time1;
	private String time2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disturb_set);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("免打扰设置");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("保存");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_time = (LinearLayout) findViewById(R.id.lLayout_time);
		wheel_time1 = (AbstractWheel) findViewById(R.id.wheel_time1);
		wheel_time2 = (AbstractWheel) findViewById(R.id.wheel_time2);
		uiswitch_disturb = (UISwitchButton) findViewById(R.id.uiswitch_disturb);
		uiswitch_disturb.setChecked(false);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DisturbSetActivity.this.finish();
			}
		});

		// 点击保存
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (time1.equals(time2)
						|| ("00:00".equals(time1) && "24:00".equals(time2))
						|| ("24:00".equals(time1) && "00:00".equals(time2))) {
					MToast.show(DisturbSetActivity.this, "请选择正确的时间段",
							MToast.SHORT);
					return;
				}

				new AlertDialog.Builder(DisturbSetActivity.this)
						.setMessage(
								"系统将自动屏蔽 " + time1 + "-" + time2 + " 之间的消息提醒")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 将免打扰时间段写入配置文件
										SharedPreferences sharedPreferences = getSharedPreferences(
												MCon.SHARED_PREFERENCES_USER_INFO,
												Context.MODE_PRIVATE);
										Editor edit = sharedPreferences.edit();
										edit.putInt("no_disturb_time1",
												parseIntTime(time1));
										edit.putInt("no_disturb_time2",
												parseIntTime(time2));
										edit.commit();

										MToast.show(DisturbSetActivity.this,
												"设置成功", MToast.SHORT);
										DisturbSetActivity.this.finish();
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 第一个滚轮值变化监听
		wheel_time1.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				time1 = times[wheel.getCurrentItem()];
			}
		});

		// 第二个滚轮值变化监听
		wheel_time2.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				time2 = times[wheel.getCurrentItem()];
			}
		});

		// 免打扰开关监听
		uiswitch_disturb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences sharedPreferences = getSharedPreferences(
								MCon.SHARED_PREFERENCES_USER_INFO,
								Context.MODE_PRIVATE);

						if (isChecked) {// 开启免打扰
							txt_topbar_btn.setVisibility(View.VISIBLE);
							lLayout_time.setVisibility(View.VISIBLE);

							// 将开启免打扰的标识写入配置文件
							sharedPreferences.edit()
									.putBoolean("no_disturb", true).commit();

							// 获取配置文件中设置的时间段
							int no_disturb_time1 = sharedPreferences.getInt(
									"no_disturb_time1", -1);
							int no_disturb_time2 = sharedPreferences.getInt(
									"no_disturb_time2", -1);

							if (no_disturb_time1 == -1
									&& no_disturb_time2 == -1) {
								// 之前未设置过时间段，将默认时间段写入，23:00-08:00
								no_disturb_time1 = 23;
								no_disturb_time2 = 8;
								Editor edit = sharedPreferences.edit();
								edit.putInt("no_disturb_time1",
										no_disturb_time1);
								edit.putInt("no_disturb_time2",
										no_disturb_time2);
								edit.commit();
							}

							// 设置滚轮的时间显示
							wheel_time1.setCurrentItem(no_disturb_time1);
							wheel_time2.setCurrentItem(no_disturb_time2);

							// 时间段赋值
							time1 = times[wheel_time1.getCurrentItem()];
							time2 = times[wheel_time2.getCurrentItem()];
						} else {// 关闭免打扰
							txt_topbar_btn.setVisibility(View.GONE);
							lLayout_time.setVisibility(View.GONE);

							// 将关闭免打扰的标识写入配置文件
							sharedPreferences.edit()
									.putBoolean("no_disturb", false).commit();
						}
					}
				});
	}

	private void init() {
		// 初始化时间数组
		times = new String[] { "00:00", "01:00", "02:00", "03:00", "04:00",
				"05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
				"12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
				"19:00", "20:00", "21:00", "22:00", "23:00", "24:00" };

		// 设置滚轮适配器
		wheel_time1.setViewAdapter(new ArrayWheelAdapter<String>(this, times));
		wheel_time2.setViewAdapter(new ArrayWheelAdapter<String>(this, times));

		// 获取配置文件中保存的开关状态
		SharedPreferences sharedPreferences = getSharedPreferences(
				MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
		boolean no_disturb = sharedPreferences.getBoolean("no_disturb", false);
		uiswitch_disturb.setChecked(no_disturb);

		if (no_disturb) {// 开启了免打扰
			txt_topbar_btn.setVisibility(View.VISIBLE);
			lLayout_time.setVisibility(View.VISIBLE);

			// 获取配置文件中设置的时间段
			int no_disturb_time1 = sharedPreferences.getInt("no_disturb_time1",
					23);
			int no_disturb_time2 = sharedPreferences.getInt("no_disturb_time2",
					8);

			// 设置滚轮的时间显示
			wheel_time1.setCurrentItem(no_disturb_time1);
			wheel_time2.setCurrentItem(no_disturb_time2);

			// 时间段赋值
			time1 = times[wheel_time1.getCurrentItem()];
			time2 = times[wheel_time2.getCurrentItem()];
		} else {
			txt_topbar_btn.setVisibility(View.GONE);
			lLayout_time.setVisibility(View.GONE);
		}
	}

	private int parseIntTime(String time) {
		String[] split = time.split(":");
		return Integer.parseInt(split[0]);
	}

	@SuppressWarnings("unused")
	private String parseStrTime(int time) {
		return String.format(Locale.CHINA, "%02d:00", time);
	}

}
