package com.dt5000.ischool.activity.student;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.dt5000.ischool.adapter.student.StudyTestingRecordListAdapter;
import com.dt5000.ischool.entity.StudyPaper;
import com.dt5000.ischool.entity.StudyTest;
import com.dt5000.ischool.entity.StudyTestRecord;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.google.gson.reflect.TypeToken;

/**
 * 自主学习自测评估做题记录列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月27日 上午11:10:13
 * @ClassInfo com.dt5000.ischool.activity.student.StudyTestingRecordListActivity
 * @Description
 */
public class StudyTestingRecordListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview_record;
	private LoadMoreFooterView loadMoreFooterView;

	private List<StudyTestRecord> testRecordList;
	private StudyTestingRecordListAdapter studyTestingRecordListAdapter;
	private User user;
	private FinalHttp finalHttp;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing_record_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("做题记录");
		listview_record = (ListView) findViewById(R.id.listview_record);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(
				StudyTestingRecordListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_record.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingRecordListActivity.this.finish();
			}
		});

		// 点击删除、解析获取重做
		listview_record.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final StudyTestRecord testRecord = testRecordList.get(position);

				ListAdapter listAdapter = new ArrayAdapter<>(
						StudyTestingRecordListActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"删除", "解析", "重做" });

				new AlertDialog.Builder(StudyTestingRecordListActivity.this)
						.setSingleChoiceItems(listAdapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										switch (which) {
										case 0:// 删除
											new AlertDialog.Builder(
													StudyTestingRecordListActivity.this)
													.setMessage("确定删除该记录？")
													.setPositiveButton(
															"删除",
															new DialogInterface.OnClickListener() {
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	deleteTestRecord(testRecord);
																}
															})
													.setNegativeButton("取消",
															null).show();
											break;
										case 1:// 解析
											analysisTestRecord(testRecord);
											break;
										case 2:// 重做
											redoTestRecord(testRecord);
											break;
										}
									}
								}).show();
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();

		testRecordList = new ArrayList<StudyTestRecord>();

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("加载中...");
		progressDialog.setCancelable(false);
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_TEST_RECORD);
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNo", String.valueOf(PAGE_NO));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("做题记录连接：" + httpURL);
		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("做题记录返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("recordList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyTestRecord>>() {
							}.getType();
							List<StudyTestRecord> data = (List<StudyTestRecord>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								testRecordList = data;

								// 设置适配器
								studyTestingRecordListAdapter = new StudyTestingRecordListAdapter(
										StudyTestingRecordListActivity.this,
										testRecordList);
								listview_record
										.setAdapter(studyTestingRecordListAdapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void getMoreData() {
		PAGE_NO++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_TEST_RECORD);
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNo", String.valueOf(PAGE_NO));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("更多做题记录返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("recordList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyTestRecord>>() {
							}.getType();
							List<StudyTestRecord> moreData = (List<StudyTestRecord>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								testRecordList.addAll(moreData);

								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 更新适配器
								studyTestingRecordListAdapter
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

	/**
	 * 发送请求删除做题记录
	 * 
	 * @param testRecord
	 */
	private void deleteTestRecord(final StudyTestRecord testRecord) {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_DEL_RECORD);
		map.put("recordId", String.valueOf(testRecord.getTestResultHeadId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("删除做题记录返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String memo = obj.optString("memo");
							if ("".equals(memo)) {
								// 更新适配器
								testRecordList.remove(testRecord);
								studyTestingRecordListAdapter
										.notifyDataSetChanged();

								MToast.show(
										StudyTestingRecordListActivity.this,
										"已删除", MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 解析记录试题
	 * 
	 * @param testRecord
	 */
	private void analysisTestRecord(final StudyTestRecord testRecord) {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_ANALYSIS_RECORD);
		map.put("testHeadId", String.valueOf(testRecord.getTestHeadId()));
		map.put("testResultHeadId",
				String.valueOf(testRecord.getTestResultHeadId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						progressDialog.show();
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						// 关闭加载进度条
						progressDialog.dismiss();

						MLog.i("获取做题记录试题数据返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("analysisRecordList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyTest>>() {
							}.getType();
							List<StudyTest> data = (List<StudyTest>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								// 跳转到解析页面
								Intent intent = new Intent(
										StudyTestingRecordListActivity.this,
										StudyTestingAnalysisActivity.class);
								Bundle bundle = new Bundle();
								bundle.putInt("testHeadId",
										testRecord.getTestHeadId());
								bundle.putSerializable("analysisTestList",
										(Serializable) data);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
					}
				});
	}

	/**
	 * 重做记录试题
	 * 
	 * @param testRecord
	 */
	private void redoTestRecord(StudyTestRecord testRecord) {
		// 根据做题记录创建试卷
		StudyPaper paper = new StudyPaper();
		paper.setName(testRecord.getName());
		paper.setSubjectId(testRecord.getSubjectId());
		paper.setSelfTestId(testRecord.getTestHeadId());

		// 跳转到试题页面
		Intent intent = new Intent(StudyTestingRecordListActivity.this,
				StudyTestingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("paper", paper);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
