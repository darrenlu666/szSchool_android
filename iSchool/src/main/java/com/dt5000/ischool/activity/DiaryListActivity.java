package com.dt5000.ischool.activity;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.DiaryListAdapter;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.Diary;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.google.gson.reflect.TypeToken;

/**
 * 日记列表页面
 * 
 * @author 周锋
 * @date 2016年2月4日 上午10:25:48
 * @ClassInfo com.dt5000.ischool.activity.DiaryListActivity
 * @Description
 */
public class DiaryListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview;
	private LoadMoreFooterView loadMoreFooterView;

	private List<Diary> diaryList;
	private DiaryListAdapter diaryListAdapter;
	private User user;
	private String classId;
	private FinalHttp finalHttp;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 1;
	private int clickPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		listview = (ListView) findViewById(R.id.listview);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(DiaryListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DiaryListActivity.this.finish();
			}
		});

		// 点击进入详情页面
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				clickPosition = position;//记录点击的位置
				
				Diary diary = diaryList.get(position);
				Intent intent = new Intent(DiaryListActivity.this,
						DiaryDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("classId", classId);
				bundle.putSerializable("diary", diary);
				intent.putExtras(bundle);
				startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {// 教师端先选择班级，并将班级id传递过来
			ClassItem classItem = (ClassItem) bundle
					.getSerializable("classItem");
			classId = classItem.getClassId();
			
			txt_title.setText(classItem.getClassName());
		} else {// 学生端直接使用班级id
			classId = user.getClassinfoId();
			
			txt_title.setText("我的日记");
		}

		finalHttp = new FinalHttp();
	}

	private void getData() {
		PAGE_NO = 1;// 页数置1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		if (User.isStudentRole(user.getRole())) {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_DIARY_LIST);
		} else {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_DIARY_LIST_TEACHER);
		}
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("userId", user.getUserId());
		map.put("clazzId", classId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("日记列表返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("diaryList");

							// 解析实体类
							Type listType = new TypeToken<List<Diary>>() {
							}.getType();
							List<Diary> data = (List<Diary>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 数据赋值
								diaryList = data;

								// 设置适配器
								diaryListAdapter = new DiaryListAdapter(
										DiaryListActivity.this, diaryList);
								listview.setAdapter(diaryListAdapter);
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
		if (User.isStudentRole(user.getRole())) {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_DIARY_LIST);
		} else {
			map.put("operationType",
					UrlProtocol.OPERATION_TYPE_DIARY_LIST_TEACHER);
		}
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("userId", user.getUserId());
		map.put("clazzId", classId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("更多日记列表返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("diaryList");

							// 解析实体类
							Type listType = new TypeToken<List<Diary>>() {
							}.getType();
							List<Diary> moreData = (List<Diary>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								diaryList.addAll(moreData);

								// 更新适配器
								diaryListAdapter.notifyDataSetChanged();
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
		switch (requestCode) {
		case FlagCode.ACTIVITY_REQUEST_CODE_0:
			if (resultCode == RESULT_OK) {
				int levelRank = data.getIntExtra("levelRank", 0);
				
				// 在详情页面评价过后返回本页时需要更新数据
				diaryList.get(clickPosition).setLevelRank(levelRank);
				
				// 更新适配器
				diaryListAdapter.notifyDataSetChanged();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
