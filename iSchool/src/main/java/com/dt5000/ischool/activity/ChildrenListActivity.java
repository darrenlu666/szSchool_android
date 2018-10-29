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
import com.dt5000.ischool.adapter.ChildrenListAdapter;
import com.dt5000.ischool.entity.Publication;
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
 * 育儿经列表页面
 * 
 * @author 周锋
 * @date 2016年2月1日 下午7:14:03
 * @ClassInfo com.dt5000.ischool.activity.ChildrenListActivity
 * @Description
 */
public class ChildrenListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview;
	private LoadMoreFooterView loadMoreFooterView;

	private List<Publication> publicationList;
	private ChildrenListAdapter childrenListAdapter;
	private User user;
	private FinalHttp finalHttp;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_children_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("育儿经");
		listview = (ListView) findViewById(R.id.listview);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(ChildrenListActivity.this,
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
				ChildrenListActivity.this.finish();
			}
		});

		// 点击进入详情页面
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Publication publication = publicationList.get(position);
				Intent intent = new Intent(ChildrenListActivity.this, ChildrenDetailActivity.class);
				intent.putExtra("publicationId", publication.getId());
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_PUBLICATION);
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNum", String.valueOf(PAGE_NO));
		map.put("type", "4");
		map.put("schoolId", user.getSchoolbaseinfoId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("育儿经列表地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("details");

							// 解析实体类
							Type listType = new TypeToken<List<Publication>>() {
							}.getType();
							List<Publication> data = (List<Publication>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								publicationList = data;

								// 设置适配器
								childrenListAdapter = new ChildrenListAdapter(
										ChildrenListActivity.this,
										publicationList);
								listview.setAdapter(childrenListAdapter);
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
		map.put("operationType", UrlProtocol.OPERATION_TYPE_PUBLICATION);
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		map.put("pageNum", String.valueOf(PAGE_NO));
		map.put("type", "4");
		map.put("schoolId", user.getSchoolbaseinfoId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("育儿经返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("details");

							// 解析实体类
							Type listType = new TypeToken<List<Publication>>() {
							}.getType();
							List<Publication> moreData = (List<Publication>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								publicationList.addAll(moreData);

								// 更新适配器
								childrenListAdapter.notifyDataSetChanged();
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
