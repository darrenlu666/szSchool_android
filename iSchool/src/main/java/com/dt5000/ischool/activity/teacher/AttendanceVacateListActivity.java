package com.dt5000.ischool.activity.teacher;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.AttendanceVacateListAdapter;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.Vacate;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.google.gson.reflect.TypeToken;

/**
 * 考勤请假单列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年10月12日 上午11:28:27
 * @ClassInfo com.dt5000.ischool.activity.teacher.AttendanceVacateListActivity
 * @Description
 */
public class AttendanceVacateListActivity extends Activity {

	private ListView listview_vacate;
	private LinearLayout lLayout_loading;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private LinearLayout lLayout_back;
	private LoadMoreFooterView loadMoreFooterView;

	private List<Vacate> vacateList;
	private AttendanceVacateListAdapter attendanceVacateListAdapter;
	private User user;
	private FinalHttp finalHttp;
	private int pageNum = 1;
	private int isAgree = -1;// -1:全部 0:未确认 1:已确认 2:已撤销

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_vacate_list_teacher);

		initView();
		initListener();
		init();
		getData();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("请假");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("筛选");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
		lLayout_loading.setVisibility(View.GONE);
		listview_vacate = (ListView) findViewById(R.id.listview_vacate);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(
				AttendanceVacateListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_vacate.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttendanceVacateListActivity.this.finish();
			}
		});

		// 点击筛选
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ListAdapter adapter = new ArrayAdapter<String>(
						AttendanceVacateListActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"待确认", "全部" });

				new AlertDialog.Builder(AttendanceVacateListActivity.this)
						.setSingleChoiceItems(adapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										switch (which) {
										case 0:
											isAgree = 0;
											getData();
											break;
										case 1:
											isAgree = -1;
											getData();
											break;
										}
									}
								}).show();
			}
		});

		// 点击进入请假单编辑页面
		listview_vacate.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Vacate vacate = vacateList.get(position);
				Intent intent = new Intent(AttendanceVacateListActivity.this,
						AttendanceVacateEditActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("vacate", vacate);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
		});
	}

	private void init() {
		user = User.getUser(this);
		finalHttp = new FinalHttp();
		vacateList = new ArrayList<Vacate>();
	}

	private void getData() {
		pageNum = 1;// 页数置1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VACATE_LIST);
		map.put("userId", user.getUserId());
		map.put("roleId", String.valueOf(user.getRole()));
		map.put("isAgree", String.valueOf(isAgree));
		map.put("pageSize", "10");
		map.put("pageNum", String.valueOf(pageNum));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("请假单列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("leaveRecordList");

							// 解析实体类
							Type listType = new TypeToken<List<Vacate>>() {
							}.getType();
							List<Vacate> data = (List<Vacate>) GsonUtil
									.jsonToList(result, listType);
							if (data != null && data.size() > 0) {
								vacateList = data;
							}

							// 设置适配器
							attendanceVacateListAdapter = new AttendanceVacateListAdapter(
									AttendanceVacateListActivity.this,
									vacateList);
							listview_vacate
									.setAdapter(attendanceVacateListAdapter);

							// 底部FootView状态改变
							loadMoreFooterView.loadComplete();
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

	private void getMoreData() {
		pageNum++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VACATE_LIST);
		map.put("userId", user.getUserId());
		map.put("roleId", String.valueOf(user.getRole()));
		map.put("isAgree", String.valueOf(isAgree));
		map.put("pageSize", "10");
		map.put("pageNum", String.valueOf(pageNum));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("请假单列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("leaveRecordList");

							// 解析实体类
							Type listType = new TypeToken<List<Vacate>>() {
							}.getType();
							List<Vacate> moreData = (List<Vacate>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 更新适配器
								vacateList.addAll(moreData);
								attendanceVacateListAdapter
										.notifyDataSetChanged();
							} else {
								// 底部FootView状态改变
								loadMoreFooterView.noMore();
							}
						} catch (Exception e) {
							e.printStackTrace();
							// 底部FootView状态改变
							loadMoreFooterView.loadComplete();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// 底部FootView状态改变
						loadMoreFooterView.loadComplete();
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				getData();
			}
			break;
		}
	}

}
