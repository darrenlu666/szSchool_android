package com.dt5000.ischool.activity.student;

import java.io.Serializable;
import java.lang.reflect.Type;
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
import com.dt5000.ischool.adapter.student.StudyTestingCollectListAdapter;
import com.dt5000.ischool.entity.StudyTest;
import com.dt5000.ischool.entity.StudyTestCollect;
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
 * 自主学习自测评估试题收藏列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月27日 下午5:25:36
 * @ClassInfo 
 *            com.dt5000.ischool.activity.student.StudyTestingCollectListActivity
 * @Description
 */
public class StudyTestingCollectListActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private ListView listview_test;
	private LoadMoreFooterView loadMoreFooterView;

	private User user;
	private FinalHttp finalHttp;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private List<StudyTestCollect> testCollectList;
	private StudyTestingCollectListAdapter studyTestingCollectListAdapter;
	private ProgressDialog progressDialog;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing_collect_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		listview_test = (ListView) findViewById(R.id.listview_test);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(
				StudyTestingCollectListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_test.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingCollectListActivity.this.finish();
			}
		});

		// 点击删除或解析
		listview_test.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final StudyTestCollect testCollect = testCollectList
						.get(position);

				ListAdapter listAdapter = new ArrayAdapter<>(
						StudyTestingCollectListActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"删除", "解析" });

				new AlertDialog.Builder(StudyTestingCollectListActivity.this)
						.setSingleChoiceItems(listAdapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										switch (which) {
										case 0:// 删除
											new AlertDialog.Builder(
													StudyTestingCollectListActivity.this)
													.setMessage("确定删除该收藏？")
													.setPositiveButton(
															"删除",
															new DialogInterface.OnClickListener() {
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	deleteTestCollect(testCollect);
																}
															})
													.setNegativeButton("取消",
															null).show();
											break;
										case 1:// 解析
											if (type == 3) {
												analysisTestCollect(testCollect);
											} else {
												analysisTestCollect(testCollect);
											}
											break;
										}
									}
								}).show();
			}
		});
	}

	private void init() {
		type = getIntent().getIntExtra("type", 0);

		// 设置标题
		switch (type) {
		case 1:
			txt_title.setText("错题收藏");
			break;
		case 2:
			txt_title.setText("难题收藏");
			break;
		case 3:
			txt_title.setText("套题收藏");
			break;
		}

		user = User.getUser(this);

		finalHttp = new FinalHttp();

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("加载中...");
		progressDialog.setCancelable(false);
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		if (type == 3) {// 套题
			map.put("operationType", UrlProtocol.OPERATION_TYPE_SETS_COLLECTION);
		} else {// 难题错题
			map.put("operationType",
					UrlProtocol.OPERATION_TYPE_QUESTION_COLLECTION);
		}
		map.put("type", String.valueOf(type));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				StudyTestingCollectListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("试题收藏列表返回数据：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = "";
							if (type == 3) {// 套题
								result = obj.optString("setsList");
							} else {// 难题错题
								result = obj.optString("questionList");
							}

							// 解析实体类
							Type listType = new TypeToken<List<StudyTestCollect>>() {
							}.getType();
							List<StudyTestCollect> data = (List<StudyTestCollect>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								testCollectList = data;

								// 设置适配器
								studyTestingCollectListAdapter = new StudyTestingCollectListAdapter(
										StudyTestingCollectListActivity.this,
										testCollectList);
								listview_test
										.setAdapter(studyTestingCollectListAdapter);
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
		if (type == 3) {// 套题
			map.put("operationType", UrlProtocol.OPERATION_TYPE_SETS_COLLECTION);
		} else {// 难题错题
			map.put("operationType",
					UrlProtocol.OPERATION_TYPE_QUESTION_COLLECTION);
		}
		map.put("type", String.valueOf(type));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				StudyTestingCollectListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("更多试题收藏列表返回数据：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = "";
							if (type == 3) {// 套题
								result = obj.optString("setsList");
							} else {// 难题错题
								result = obj.optString("questionList");
							}

							// 解析实体类
							Type listType = new TypeToken<List<StudyTestCollect>>() {
							}.getType();
							List<StudyTestCollect> moreData = (List<StudyTestCollect>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								testCollectList.addAll(moreData);

								// 更新适配器
								studyTestingCollectListAdapter
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
	 * 删除收藏试题
	 * 
	 * @param testCollect
	 */
	private void deleteTestCollect(final StudyTestCollect testCollect) {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_DELETE_COLLECTION);
		map.put("collectId", String.valueOf(testCollect.getCollectId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("删除收藏试题返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String memo = obj.optString("memo");
							if ("".equals(memo)) {
								testCollectList.remove(testCollect);
								studyTestingCollectListAdapter
										.notifyDataSetChanged();

								MToast.show(
										StudyTestingCollectListActivity.this,
										"已删除", MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 发送请求获取难题、错题、套题所在的整套试题数据，并将试题数据传递给解析页面，如果是错题难题的话将题号也传过去让其页面默认显示该题
	 * 
	 * @param testCollect
	 */
	private void analysisTestCollect(StudyTestCollect testCollect) {
		final int sortIndex = testCollect.getSortIndex();

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_PAGER);
		map.put("testHeadId", String.valueOf(testCollect.getTestHeadId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				StudyTestingCollectListActivity.this, user.getUserId());

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

						MLog.i("收藏试题所在的整套试题返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("questionList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyTest>>() {
							}.getType();
							List<StudyTest> data = (List<StudyTest>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								// 跳转到解析页面
								Intent intent = new Intent(
										StudyTestingCollectListActivity.this,
										StudyTestingCollectAnalysisActivity.class);
								Bundle bundle = new Bundle();
								bundle.putInt("sortIndex",
										sortIndex >= 1 ? sortIndex : 0);
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

}
