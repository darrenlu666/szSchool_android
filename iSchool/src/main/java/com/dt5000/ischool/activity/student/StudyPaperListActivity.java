package com.dt5000.ischool.activity.student;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.student.StudyPaperListAdapter;
import com.dt5000.ischool.entity.StudyPaper;
import com.dt5000.ischool.entity.User;
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
 * 自主学习自测评估试卷列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月25日 下午3:13:07
 * @ClassInfo com.dt5000.ischool.activity.student.StudyPaperListActivity
 * @Description
 */
public class StudyPaperListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private LinearLayout lLayout_loading;
	private TextView txt_grade;
	private ListView listview_paper;
	private LoadMoreFooterView loadMoreFooterView;

	private User user;
	private List<StudyPaper> paperList;
	private StudyPaperListAdapter studyPaperListAdapter;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private String subjectId;
	private int gradeCode;
	private FinalHttp finalHttp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_paper_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("自测评估");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_grade = (TextView) findViewById(R.id.txt_grade);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
		listview_paper = (ListView) findViewById(R.id.listview_paper);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(
				StudyPaperListActivity.this, new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_paper.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyPaperListActivity.this.finish();
			}
		});

		// 点击进入试题页面
		listview_paper
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						StudyPaper studyPaper = paperList.get(position);
						Intent intent = new Intent(StudyPaperListActivity.this,
								StudyTestingActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("paper", studyPaper);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
	}

	private void init() {
		Intent intent = getIntent();
		String subjectName = intent.getStringExtra("subjectName");
		String gradeName = intent.getStringExtra("gradeName");
		subjectId = intent.getStringExtra("subjectId");
		gradeCode = intent.getIntExtra("gradeCode", 0);

		txt_grade.setText(gradeName + "  " + subjectName);

		finalHttp = new FinalHttp();

		user = User.getUser(this);
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_LIST);
		map.put("gradeId", String.valueOf(gradeCode));
		map.put("subjectId", subjectId);
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("自测评估试卷列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("testPaperList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyPaper>>() {
							}.getType();
							List<StudyPaper> data = (List<StudyPaper>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								paperList = data;

								// 设置适配器
								studyPaperListAdapter = new StudyPaperListAdapter(
										StudyPaperListActivity.this, paperList);
								listview_paper
										.setAdapter(studyPaperListAdapter);
							}

							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// 隐藏加载进度条
						lLayout_loading.setVisibility(View.GONE);
					}
				});
	}

	private void getMoreData() {
		PAGE_NO++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_LIST);
		map.put("gradeId", String.valueOf(gradeCode));
		map.put("subjectId", subjectId);
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("testPaperList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyPaper>>() {
							}.getType();
							List<StudyPaper> moreData = (List<StudyPaper>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								paperList.addAll(moreData);

								// 更新适配器
								studyPaperListAdapter.notifyDataSetChanged();
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

}
