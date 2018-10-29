package com.dt5000.ischool.activity.teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.ScoreChooseExpandListAdapter;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.FriendItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 成绩查询选择学生列表页面：教师端
 * 
 * @author 周锋
 * @date 2014年11月11日 上午11:46:16
 * @ClassInfo com.dt5000.ischool.teacher.activity.ScoreStudentListActivity
 * @Description
 */
public class ScoreChooseListActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ExpandableListView expand_listview;

	private List<ContactItem> contactItemList;
	private ScoreChooseExpandListAdapter scoreChooseExpandListAdapter;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_choose_list);

		initView();
		init();
		initListener();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择学生");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		expand_listview = (ExpandableListView) findViewById(R.id.expand_listview);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScoreChooseListActivity.this.finish();
			}
		});

		// 点击ExpandableListView的子项进入学生成绩列表页面
		expand_listview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Object child = scoreChooseExpandListAdapter.getChild(groupPosition, childPosition);

				if (child instanceof FriendItem) {
					String sid = ((FriendItem) child).getFriendId();
					String name = ((FriendItem) child).getFriendName();
					Intent intent = new Intent(ScoreChooseListActivity.this, ScoreListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("stuId", sid);
					bundle.putString("name", name);
					intent.putExtras(bundle);
					startActivity(intent);
				}

				return false;
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		contactItemList = new ArrayList<ContactItem>();
	}

	/**
	 * 发请求获取联系人数据
	 */
	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_CONTACT_LIST);
		map.put("cid", user.getClassinfoId());
		map.put("role", String.valueOf(user.getRole()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("联系人返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("contactList");
							List<ContactItem> data = (List<ContactItem>) GsonUtil
									.jsonToList(result, new TypeToken<List<ContactItem>>() {}.getType());

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								contactItemList.addAll(data);

								// 返回的联系人包含老师、学生、年级和班级，需要筛选
								for (int i = 0; i < data.size(); i++) {
									ContactItem contactItem = data.get(i);
									String type = contactItem.getType();
									if ("0".equals(type) || "2".equals(type) || "3".equals(type)) {
										contactItemList.remove(contactItem);
									}
								}

								// 设置适配器
								scoreChooseExpandListAdapter = new ScoreChooseExpandListAdapter(
										ScoreChooseListActivity.this,
										contactItemList);
								expand_listview.setAdapter(scoreChooseExpandListAdapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
