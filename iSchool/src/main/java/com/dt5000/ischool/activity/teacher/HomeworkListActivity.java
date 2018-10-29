package com.dt5000.ischool.activity.teacher;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.HomeworkListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.Homework;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.SubjectUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.dt5000.ischool.widget.PullToRefreshListView;
import com.dt5000.ischool.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.reflect.TypeToken;

/**
 * 作业列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月13日 下午4:22:02
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkListActivity
 * @Description
 */
public class HomeworkListActivity extends Activity {

	private PullToRefreshListView listview_homework;
	private LinearLayout lLayout_loading;
	private LoadMoreFooterView loadMoreFooterView;
	private LinearLayout lLayout_back;
	private TextView txt_title;

	private HomeworkListAdapter homeworkListAdapter;
	private User user;
	private List<Homework> homeworkList;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private FinalHttp finalHttp;
	private ClassItem classItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		listview_homework = (PullToRefreshListView) this.findViewById(R.id.listview_homework);
		lLayout_loading = (LinearLayout) this.findViewById(R.id.lLayout_loading);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(HomeworkListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_homework.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkListActivity.this.finish();
			}
		});

		// 点击进入作业详情
		listview_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position == 0 || position > homeworkList.size()) {
							// 过滤头部下拉刷新和底部加载更多位置的点击事件，防止数组越界
							return;
						}

						Homework homework = homeworkList.get(position - 1);
						Intent intent = new Intent(HomeworkListActivity.this, HomeworkDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("homework", homework);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});

		// 下拉刷新
		listview_homework.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void toRefresh() {
				getData();
			}
		});
	}

	private void init() {
		classItem = (ClassItem) getIntent().getExtras().getSerializable("classItem");

		txt_title.setText(classItem.getClassName());

		finalHttp = new FinalHttp();

		homeworkList = new ArrayList<Homework>();

		user = User.getUser(HomeworkListActivity.this);
	}

	/**
	 * 获取作业列表数据，首次加载页面和下拉刷新都会调用此方法
	 */
	private void getData() {
		PAGE_NO = 0;// 页数置0

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_HOMEWORK_LIST);
		map.put("cid", classItem.getClassId());
		map.put("role", String.valueOf(user.getRole()));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNo", String.valueOf(PAGE_NO));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("作业列表返回数据：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("homeworkList");

							// 解析实体类
							Type listType = new TypeToken<List<Homework>>() {}.getType();
							List<Homework> data = (List<Homework>) GsonUtil.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 设置作业列表中固定的科目图标和科目名称
								SparseArray<String> subjectNames = SubjectUtil.getSubjectNames();
								SparseIntArray subjectImgs = SubjectUtil.getSubjectImgs();
								for (Homework h : data) {
									// 服务器返回的科目id
									int subjectId = h.getSubjectId();
									// 本地科目是固定的，新增科目固定为“其它”
									if (subjectId < 1 || subjectId > 15) {
										subjectId = 15;
									}
									h.setSubjectName(subjectNames.get(subjectId));
									h.setSubjectPicId(subjectImgs.get(subjectId));
								}

								// 设置适配器
								homeworkList = data;
								homeworkListAdapter = new HomeworkListAdapter(HomeworkListActivity.this, homeworkList);
								listview_homework.setAdapter(homeworkListAdapter);
							}

							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
							// 隐藏下拉刷新进度条
							listview_homework.onRefreshFinished();
						} catch (Exception e) {
							e.printStackTrace();
							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
							// 隐藏下拉刷新进度条
							listview_homework.onRefreshFinished();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// 隐藏加载进度条
						lLayout_loading.setVisibility(View.GONE);
						// 隐藏下拉刷新进度条
						listview_homework.onRefreshFinished();
					}
				});
	}

	/**
	 * 获取更多作业列表数据，点击ListView的FootView时会调用此方法
	 */
	private void getMoreData() {
		PAGE_NO++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_HOMEWORK_LIST);
		map.put("cid", classItem.getClassId());
		map.put("role", String.valueOf(user.getRole()));
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
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("homeworkList");

							// 解析实体类
							Type listType = new TypeToken<List<Homework>>() {
							}.getType();
							List<Homework> moreData = (List<Homework>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 设置作业列表中固定的科目图标和科目名称
								SparseArray<String> subjectNames = SubjectUtil
										.getSubjectNames();
								SparseIntArray subjectImgs = SubjectUtil
										.getSubjectImgs();
								for (Homework h : moreData) {
									// 服务器返回的科目id
									int subjectId = h.getSubjectId();
									// 本地科目是固定的，新增科目为“其它”
									if (subjectId < 1 || subjectId > 15) {
										subjectId = 15;
									}
									h.setSubjectName(subjectNames
											.get(subjectId));
									h.setSubjectPicId(subjectImgs
											.get(subjectId));
								}

								// 更新适配器
								homeworkList.addAll(moreData);
								homeworkListAdapter.notifyDataSetChanged();
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