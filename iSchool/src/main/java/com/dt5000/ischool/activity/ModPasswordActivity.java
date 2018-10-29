package com.dt5000.ischool.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 修改密码页面
 * 
 * @author 周锋
 * @date 2016年2月1日 上午10:09:33
 * @ClassInfo com.dt5000.ischool.activity.ModPasswordActivity
 * @Description
 */
public class ModPasswordActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private EditText edit_origin_password;
	private EditText edit_new_password;
	private EditText edit_again_password;
	private Button btn_submit;

	private User user;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mod_password);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("修改密码");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		edit_origin_password = (EditText) findViewById(R.id.edit_origin_password);
		edit_new_password = (EditText) findViewById(R.id.edit_new_password);
		edit_again_password = (EditText) findViewById(R.id.edit_again_password);
		btn_submit = (Button) findViewById(R.id.btn_submit);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ModPasswordActivity.this.finish();
			}
		});

		// 点击确认修改
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				modPassword();
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("加载中...");
		progressDialog.setCancelable(false);
	}

	/**
	 * 发请求修改密码
	 */
	private void modPassword() {
		// 隐藏键盘
		hideSoftInput(getCurrentFocus().getWindowToken());

		String originPassword = edit_origin_password.getText().toString()
				.trim();
		final String newPassword = edit_new_password.getText().toString()
				.trim();
		String againPassword = edit_again_password.getText().toString().trim();

		if (CheckUtil.stringIsBlank(originPassword)
				|| CheckUtil.stringIsBlank(newPassword)
				|| CheckUtil.stringIsBlank(againPassword)) {
			MToast.show(this, "请检查输入", MToast.SHORT);
			return;
		}

		if (newPassword.equalsIgnoreCase(originPassword)) {
			MToast.show(this, "新密码和原密码相同，无需修改", MToast.SHORT);
			return;
		}

		if (!newPassword.equalsIgnoreCase(againPassword)) {
			MToast.show(this, "请确认新密码", MToast.SHORT);
			return;
		}

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_MOD_PASSWORD);
		map.put("oldPassword", originPassword);
		map.put("newPassword", newPassword);
		map.put("userId1", user.getUserId());
		if (User.isTeacherRole(user.getRole())) {
			map.put("userType", "2");
		} else {
			map.put("userType", "3");
		}
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						MLog.i("修改密码返回结果：" + t);
						progressDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(t);
							String resultStatus = obj.optString("resultStatus");

							if ("200".equals(resultStatus)) {
								// 修改密码成功后将本地保存的密码更新
								SharedPreferences sharedPreferences = getSharedPreferences(
										MCon.SHARED_PREFERENCES_USER_INFO,
										Context.MODE_PRIVATE);
								sharedPreferences.edit()
										.putString("password", newPassword)
										.commit();

								MToast.show(ModPasswordActivity.this, "修改成功",
										MToast.SHORT);
								ModPasswordActivity.this.finish();
							} else {
								MToast.show(ModPasswordActivity.this, "修改失败",
										MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
							MToast.show(ModPasswordActivity.this, "修改失败",
									MToast.SHORT);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
						MToast.show(ModPasswordActivity.this, "修改失败",
								MToast.SHORT);
					}
				});
	}

	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
