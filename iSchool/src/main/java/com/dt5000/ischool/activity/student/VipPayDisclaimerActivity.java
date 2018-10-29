package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * VIP支付免责声明页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月29日 下午7:27:10
 * @ClassInfo com.dt5000.ischool.activity.student.VipPayDisclaimerActivity
 * @Description
 */
public class VipPayDisclaimerActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private Button btn_next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_pay_disclaimer);

		initView();
		initListener();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("免责声明");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		btn_next = (Button) findViewById(R.id.btn_next);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VipPayDisclaimerActivity.this.finish();
			}
		});

		// 点击进入VIP套餐选择页面
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VipPayDisclaimerActivity.this, VipPaySpeciesListActivity.class);
				startActivityForResult(intent, 1102);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1102:
			if (resultCode == RESULT_OK) {
				VipPayDisclaimerActivity.this.setResult(RESULT_OK);
				VipPayDisclaimerActivity.this.finish();
			}
			break;
		}
	}

}
