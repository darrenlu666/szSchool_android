package com.dt5000.ischool.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 意见反馈页面
 * 
 * @author 周锋
 * @date 2016年1月30日 下午2:18:43
 * @ClassInfo com.dt5000.ischool.activity.FeedbackActivity
 * @Description
 */
public class FeedbackActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private EditText edit_title;
	private EditText edit_content;
	private EditText edit_contact;
	private Button btn_submit;
	private LinearLayout lLayout_suzhou;
	private CheckBox checkBox_suzhou;
	private LinearLayout lLayout_school;
	private CheckBox checkBox_school;

	private User user;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("意见反馈");
		edit_title = (EditText) findViewById(R.id.edit_title);
		edit_content = (EditText) findViewById(R.id.edit_content);
		edit_contact = (EditText) findViewById(R.id.edit_contact);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		lLayout_suzhou = (LinearLayout) findViewById(R.id.lLayout_suzhou);
		checkBox_suzhou = (CheckBox) findViewById(R.id.checkBox_suzhou);
		lLayout_school = (LinearLayout) findViewById(R.id.lLayout_school);
		checkBox_school = (CheckBox) findViewById(R.id.checkBox_school);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FeedbackActivity.this.finish();
			}
		});

		// 点击提交
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				feedBack();
			}
		});

		// 点击选中发送至苏州学堂
		lLayout_suzhou.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBox_suzhou.toggle();

				if (checkBox_suzhou.isChecked()) {
					checkBox_school.setChecked(false);
				} else {
					checkBox_school.setChecked(true);
				}
			}
		});

		// 点击选中发送至学校
		lLayout_school.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBox_school.toggle();

				if (checkBox_school.isChecked()) {
					checkBox_suzhou.setChecked(false);
				} else {
					checkBox_suzhou.setChecked(true);
				}
			}
		});
	}

	private void init() {
		user = User.getUser(this);
	}

	/**
	 * 发送请求提交意见
	 */
	private void feedBack() {
		String title = edit_title.getText().toString().trim();
		String content = edit_content.getText().toString().trim();
		String contact = edit_contact.getText().toString().trim();

		if (CheckUtil.stringIsBlank(title)) {
			MToast.show(this, "请输入标题", MToast.SHORT);
			return;
		}

		if (CheckUtil.stringIsBlank(content)) {
			MToast.show(this, "请输入内容", MToast.SHORT);
			return;
		}

		if (CheckUtil.stringIsBlank(contact)) {
			MToast.show(this, "请输入联系方式", MToast.SHORT);
			return;
		}

		// 发送类型
		String type = "";
		if (checkBox_suzhou.isChecked()) {
			type = "1";// 发送给苏州学堂
		} else {
			type = "2";// 发送给学校
		}

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_FEEDBACK);
		map.put("userName", user.getUserName());
		map.put("title", title);
		map.put("content", content);
		map.put("contactInfo", contact);
		map.put("type", type);
		map.put("role", String.valueOf(user.getRole()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						if (progressDialog == null) {
							progressDialog = new ProgressDialog(
									FeedbackActivity.this);
							progressDialog.setMessage("正在提交...");
							progressDialog.setCanceledOnTouchOutside(false);
						}
						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						MLog.i("意见反馈返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("result");
							if ("success".equals(result)) {
								progressDialog.dismiss();
								MToast.show(FeedbackActivity.this,
										"感谢您提出的宝贵意见", MToast.SHORT);
								FeedbackActivity.this.finish();
							} else {
								progressDialog.dismiss();
								DialogAlert.show(FeedbackActivity.this,
										"目前无法提交");
							}
						} catch (Exception e) {
							e.printStackTrace();
							progressDialog.dismiss();
							DialogAlert.show(FeedbackActivity.this, "目前无法提交");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
						DialogAlert.show(FeedbackActivity.this, "目前无法提交");
					}
				});
	}

}
