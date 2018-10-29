package com.dt5000.ischool.activity.teacher;

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
import com.dt5000.ischool.activity.BlogListActivity;
import com.dt5000.ischool.adapter.teacher.BlogClassListAdapter;
import com.dt5000.ischool.entity.ClassItem;
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
 * 查看博客前选择班级列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年2月19日 上午9:12:29
 * @ClassInfo com.dt5000.ischool.activity.teacher.BlogClassListActivity
 * @Description
 */
public class BlogClassListActivity extends Activity {

	private ListView listview_class;
	private LinearLayout lLayout_back;

	private User user;
	private List<ClassItem> classList;
	private BlogClassListAdapter blogClassListAdapter;
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_class_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		// 初始化View
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择班级");
		listview_class = (ListView) findViewById(R.id.listview_class);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BlogClassListActivity.this.finish();
			}
		});

		// 点击进入博客列表页面
		listview_class.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				ClassItem classItem = classList.get(position);
				Intent intent = new Intent(BlogClassListActivity.this, BlogListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("classItem", classItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();
	}

	/**
	 * 获取教师所带班级数据
	 */
	private void getData() {
		// 配置参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_TEACHER_CLASS);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("班级列表返回数据：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("classList");

							Type listType = new TypeToken<List<ClassItem>>() {
							}.getType();
							List<ClassItem> data = (List<ClassItem>) GsonUtil
									.jsonToList(result, listType);

							if (data != null && data.size() > 0) {
								classList = data;
								blogClassListAdapter = new BlogClassListAdapter(
										BlogClassListActivity.this, classList);
								listview_class.setAdapter(blogClassListAdapter);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
