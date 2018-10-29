package com.dt5000.ischool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.widget.UISwitchButton;

/**
 * 消息提醒设置页面
 * 
 * @author 周锋
 * @date 2016年1月14日 下午4:06:28
 * @ClassInfo com.dt5000.ischool.activity.NoticeSetActivity
 * @Description
 */
public class NoticeSetActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private UISwitchButton uiswitch_show;
	private UISwitchButton uiswitch_voice;
	private UISwitchButton uiswitch_vibrate;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_set);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("消息提醒");
		uiswitch_show = (UISwitchButton) findViewById(R.id.uiswitch_show);
		uiswitch_voice = (UISwitchButton) findViewById(R.id.uiswitch_voice);
		uiswitch_vibrate = (UISwitchButton) findViewById(R.id.uiswitch_vibrate);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NoticeSetActivity.this.finish();
			}
		});

		// 点击打开或关闭消息提醒
		uiswitch_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = sharedPreferences.edit();
				if (isChecked) {
					editor.putBoolean("isMsg", true).commit();
				} else {
					editor.putBoolean("isMsg", false).commit();
				}
			}
		});

		// 点击打开或关闭声音提醒
		uiswitch_voice
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sharedPreferences.edit();
						if (isChecked) {
							editor.putBoolean("isVoice", true).commit();
						} else {
							editor.putBoolean("isVoice", false).commit();
						}
					}
				});

		// 点击打开或关闭震动提醒
		uiswitch_vibrate
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sharedPreferences.edit();
						if (isChecked) {
							editor.putBoolean("isVibrator", true).commit();
						} else {
							editor.putBoolean("isVibrator", false).commit();
						}
					}
				});
	}

	private void init() {
		sharedPreferences = getSharedPreferences(
				MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);

		boolean isMsg = sharedPreferences.getBoolean("isMsg", true);
		boolean isVoice = sharedPreferences.getBoolean("isVoice", true);
		boolean isVibrator = sharedPreferences.getBoolean("isVibrator", true);
		uiswitch_show.setChecked(isMsg);
		uiswitch_voice.setChecked(isVoice);
		uiswitch_vibrate.setChecked(isVibrator);
	}

}
