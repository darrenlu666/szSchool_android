package com.dt5000.ischool.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.AlbumClass;
import com.dt5000.ischool.entity.AlbumGrade;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 添加班级相册页面
 * 
 * @author 周锋
 * @date 2016年2月3日 上午10:52:36
 * @ClassInfo com.dt5000.ischool.activity.AlbumAddActivity
 * @Description
 */
public class AlbumAddActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private LinearLayout lLayout_down;
	private TextView txt_grade;
	private TextView txt_class;
	private EditText edit_album_name;
	private EditText edit_album_content;
	private Button btn_create;

	private ProgressDialog progressDialog;
	private FinalHttp finalHttp;
	private User user;
	private List<AlbumGrade> gradeList;
	private List<AlbumClass> classList;
	private String classinfoId;

	/**
	 * 标识是否是平江幼儿园，平江幼儿园创建相册无需选择年级班级
	 */
	private boolean pingjing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_add);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("创建相册");
		lLayout_down = (LinearLayout) findViewById(R.id.lLayout_down);
		txt_grade = (TextView) findViewById(R.id.txt_grade);
		txt_class = (TextView) findViewById(R.id.txt_class);
		edit_album_name = (EditText) findViewById(R.id.edit_album_name);
		edit_album_content = (EditText) findViewById(R.id.edit_album_content);
		btn_create = (Button) findViewById(R.id.btn_create);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlbumAddActivity.this.finish();
			}
		});

		// 点击选择年级
		txt_grade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> gradeNameList = new ArrayList<String>();
				for (int i = 0; i < gradeList.size(); i++) {
					gradeNameList.add(gradeList.get(i).getGradeName());
				}

				ListAdapter adapter = new ArrayAdapter<String>(
						AlbumAddActivity.this,
						android.R.layout.simple_list_item_1, gradeNameList);

				new AlertDialog.Builder(AlbumAddActivity.this)
						.setSingleChoiceItems(adapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

										AlbumGrade albumGrade = gradeList
												.get(which);

										// 获取当前年级下的班级列表
										classList = albumGrade
												.getClassInfoList();

										// 设置年级文字
										txt_grade.setText(albumGrade
												.getGradeName());

										// 清空班级id和文字
										txt_class.setText("选择班级");
										classinfoId = "";
									}
								}).show();
			}
		});

		// 点击选择班级
		txt_class.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (classList != null && classList.size() > 0) {
					List<String> classNameList = new ArrayList<String>();
					for (int i = 0; i < classList.size(); i++) {
						classNameList.add(classList.get(i).getBjmc());
					}

					ListAdapter adapter = new ArrayAdapter<String>(
							AlbumAddActivity.this,
							android.R.layout.simple_list_item_1, classNameList);

					new AlertDialog.Builder(AlbumAddActivity.this)
							.setSingleChoiceItems(adapter, -1,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

											AlbumClass albumClass = classList
													.get(which);

											// 设置班级文字
											txt_class.setText(albumClass
													.getBjmc());

											// 设置班级id
											classinfoId = albumClass
													.getClassinfoId();
										}
									}).show();
				}
			}
		});

		// 点击创建相册
		btn_create.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createAlbum();
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("加载中...");
		progressDialog.setCancelable(false);

		finalHttp = new FinalHttp();
	}

	/**
	 * 获取新增相册时年级班级数据
	 */
	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_GRADE);
		map.put("schoolbaseinfoId", user.getSchoolbaseinfoId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				AlbumAddActivity.this, user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map,
				AlbumAddActivity.this, user.getUserId());
		MLog.i("角色role: " + user.getRole());
		MLog.i("获取年级班级地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("新增相册年级班级返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							String result = jsonObject
									.optString("gradeInfoItemList");

							Type listType = new TypeToken<List<AlbumGrade>>() {
							}.getType();
							List<AlbumGrade> data = (List<AlbumGrade>) GsonUtil
									.jsonToList(result, listType);

							if (data != null && data.size() > 0) {
								gradeList = data;

								lLayout_down.setVisibility(View.VISIBLE);

								pingjing = jsonObject.optBoolean("pingjing");
								if (pingjing) {
									txt_grade.setVisibility(View.GONE);
									txt_class.setVisibility(View.GONE);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 发送请求创建相册
	 */
	private void createAlbum() {
		if (!pingjing && CheckUtil.stringIsBlank(classinfoId)) {
			MToast.show(AlbumAddActivity.this, "请选择班级", MToast.SHORT);
			return;
		}

		String worksName = edit_album_name.getText().toString().trim();
		String content = edit_album_content.getText().toString().trim();

		if (CheckUtil.stringIsBlank(worksName)) {
			MToast.show(AlbumAddActivity.this, "请输入相册名称", MToast.SHORT);
			return;
		}

		if (CheckUtil.stringIsBlank(content)) {
			MToast.show(AlbumAddActivity.this, "请输入相册描述信息", MToast.SHORT);
			return;
		}

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_ADD_ALBUMS);
		map.put("role", String.valueOf(user.getRole()));
		map.put("schoolbaseinfoId", user.getSchoolbaseinfoId());
		map.put("realName", user.getRealName());
		map.put("worksName", worksName);
		map.put("content", content);
		if (pingjing) {
			map.put("classinfoId", "");
		} else {
			map.put("classinfoId", classinfoId);
		}
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				AlbumAddActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						MLog.i("创建相册返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							boolean success = jsonObject.optBoolean("success");
							boolean verify = jsonObject.optBoolean("verify");

							if (success) {
								progressDialog.dismiss();

								if (verify) {// 教师端上传后默认审核通过
									AlbumAddActivity.this.setResult(RESULT_OK);
									AlbumAddActivity.this.finish();
								} else {// 学生端上传后默认未审核
									edit_album_name.setText("");
									edit_album_content.setText("");
									hideSoftInput(AlbumAddActivity.this
											.getCurrentFocus().getWindowToken());

									new AlertDialog.Builder(
											AlbumAddActivity.this)
											.setTitle("提示")
											.setMessage("已成功创建相册，正在审核中...")
											.setPositiveButton(
													"我知道了",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															AlbumAddActivity.this
																	.finish();
														}
													}).setCancelable(false)
											.show();
								}
							} else {
								progressDialog.dismiss();
								DialogAlert.show(AlbumAddActivity.this,
										"目前无法创建");
							}
						} catch (Exception e) {
							e.printStackTrace();
							progressDialog.dismiss();
							DialogAlert.show(AlbumAddActivity.this, "目前无法创建");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
						DialogAlert.show(AlbumAddActivity.this, "目前无法创建");
					}
				});
	}

	/**
	 * 隐藏软件盘
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
