package com.dt5000.ischool.activity.teacher;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.ScorePieActivity;
import com.dt5000.ischool.adapter.teacher.ScoreListAdapter;
import com.dt5000.ischool.entity.Score;
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
 * 成绩列表界面：教师端
 * 
 * @author 周锋
 * @date 2016年1月28日 下午4:22:47
 * @ClassInfo com.dt5000.ischool.activity.teacher.ScoreListActivity
 * @Description
 */
public class ScoreListActivity extends Activity {

	private ListView listview_score;
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_loading;
	private TextView txt_title;
	private LoadMoreFooterView loadMoreFooterView;

	private User user;
	private FinalHttp finalHttp;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private String stuId;
	private List<Score> scoreList;
	private ScoreListAdapter scoreListAdapter;

	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		// 初始化View
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
		listview_score = (ListView) findViewById(R.id.listview_score);
		txt_title = (TextView) findViewById(R.id.txt_title);

		// 添加HeadView
		loadMoreFooterView = new LoadMoreFooterView(ScoreListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_score.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScoreListActivity.this.finish();
			}
		});

		// 点击进入成绩饼图页面
		listview_score.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Score score = scoreList.get(position);
				Intent intent = new Intent(ScoreListActivity.this, ScorePieActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("stuId", stuId);
				bundle.putSerializable("score", score);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");
		stuId = bundle.getString("stuId");

		txt_title.setText(name + "的成绩");

		user = User.getUser(this);

		finalHttp = new FinalHttp();

		scoreList = new ArrayList<Score>();
	}

	/**
	 * 获取成绩列表数据
	 */
	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_STUDENT_SCORE_LIST);
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("stuId", stuId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				ScoreListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("成绩列表返回数据：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("scoreItems");

							// 解析实体类
							Type listType = new TypeToken<List<Score>>() {
							}.getType();
							List<Score> data = (List<Score>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								scoreList = data;

								// 设置适配器
								scoreListAdapter = new ScoreListAdapter(
										ScoreListActivity.this, scoreList);
								listview_score.setAdapter(scoreListAdapter);
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

	/**
	 * 获取更多成绩列表数据
	 */
	private void getMoreData() {
		PAGE_NO++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_STUDENT_SCORE_LIST);
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("stuId", stuId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				ScoreListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("scoreItems");

							// 解析实体类
							Type listType = new TypeToken<List<Score>>() {
							}.getType();
							List<Score> moreData = (List<Score>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								scoreList.addAll(moreData);

								// 更新适配器
								scoreListAdapter.notifyDataSetChanged();
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
