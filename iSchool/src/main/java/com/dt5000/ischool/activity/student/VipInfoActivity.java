package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MCon;

/**
 * VIP信息页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月29日 下午6:47:27
 * @ClassInfo com.dt5000.ischool.activity.student.VipInfoActivity
 * @Description
 */
public class VipInfoActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private TextView txt_vip_species;
	private TextView txt_vip_species_tip;
	private TextView txt_vip_endTime;
	private TextView txt_pay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_info);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("VIP用户");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_vip_species = (TextView) findViewById(R.id.txt_vip_species);
		txt_vip_species_tip = (TextView) findViewById(R.id.txt_vip_species_tip);
		txt_vip_endTime = (TextView) findViewById(R.id.txt_vip_endTime);
		txt_pay = (TextView) findViewById(R.id.txt_pay);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VipInfoActivity.this.finish();
			}
		});

		// 点击VIP续费
		txt_pay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击续费跳转到支付页面
				Intent intent = new Intent(VipInfoActivity.this,
						VipPayChooseActivity.class);
				startActivityForResult(intent, 1104);
			}
		});
	}

	private void init() {
		SharedPreferences sp = getSharedPreferences(
				MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
		txt_vip_endTime.setText(sp.getString("endDate", ""));
		String goodsName = sp.getString("goodsName", "");
		if (!CheckUtil.stringIsBlank(goodsName)) {
			txt_vip_species.setVisibility(View.VISIBLE);
			txt_vip_species_tip.setVisibility(View.VISIBLE);
			txt_vip_species.setText(goodsName);
		} else {
			txt_vip_species.setText("学年套餐");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1104:
			if (resultCode == RESULT_OK) {
				VipInfoActivity.this.finish();
			}
			break;
		}
	}

}
