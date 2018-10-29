package com.dt5000.ischool.activity;

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
import com.dt5000.ischool.adapter.AlbumListAdapter;
import com.dt5000.ischool.entity.AlbumItem;
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

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级相册列表页面
 * 
 * @author 周锋
 * @date 2016年2月3日 上午9:56:34
 * @ClassInfo com.dt5000.ischool.activity.AlbumListActivity
 * @Description
 */
public class AlbumListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private ListView listview_album;
	private LoadMoreFooterView loadMoreFooterView;

	private AlbumListAdapter albumListAdapter;
	private List<AlbumItem> albumList;
	private int PAGE_SIZE = 12;
	private int PAGE_NO = 0;
	private User user;
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_list);

		initView();
		initListener();
		init();
		getData();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("班级相册");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("新增");
		listview_album = (ListView) findViewById(R.id.listview_album);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(AlbumListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_album.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlbumListActivity.this.finish();
			}
		});

		// 点击新增相册
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AlbumListActivity.this,
						AlbumAddActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		// 点击进入相册图片列表页面
		listview_album.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AlbumItem albumItem = albumList.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("albumItem", albumItem);
				Intent intent = new Intent(AlbumListActivity.this, AlbumPicGridListActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		// 学生端无法创建相册
		if (User.isStudentRole(user.getRole())) {
			txt_topbar_btn.setVisibility(View.GONE);
		}

		finalHttp = new FinalHttp();
	}

	/**
	 * 获取相册列表数据
	 */
	private void getData() {
		PAGE_NO = 0;// 页数置0

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		if (User.isTeacherRole(user.getRole())) {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_TEACHER);
		} else {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_STDUENT);
		}
		map.put("schoolbaseinfoId", user.getSchoolbaseinfoId());
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
						MLog.i("班级相册列表返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("albumItems");

							Type listType = new TypeToken<List<AlbumItem>>() {
							}.getType();
							List<AlbumItem> data = (List<AlbumItem>) GsonUtil
									.jsonToList(result, listType);

							if (data != null && data.size() > 0) {
								albumList = data;

								// 设置适配器
								albumListAdapter = new AlbumListAdapter(
										AlbumListActivity.this, albumList);
								listview_album.setAdapter(albumListAdapter);
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
		if (User.isTeacherRole(user.getRole())) {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_TEACHER);
		} else {
			map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_STDUENT);
		}
		map.put("schoolbaseinfoId", user.getSchoolbaseinfoId());
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
						MLog.i("更多班级相册列表返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("albumItems");

							Type listType = new TypeToken<List<AlbumItem>>() {
							}.getType();
							List<AlbumItem> moreData = (List<AlbumItem>) GsonUtil
									.jsonToList(result, listType);

							if (moreData != null && moreData.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								albumList.addAll(moreData);

								// 更新适配器
								albumListAdapter.notifyDataSetChanged();
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
		case 0:// 创建相册成功后返回本页面时刷新数据
			if (resultCode == RESULT_OK) {
				getData();
			}
			break;
		case 1:// 在相册界面上传图片成功后返回本页面时刷新数据
			if (resultCode == RESULT_OK) {
				getData();
			}
			break;
		}
	}

}
