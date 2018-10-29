package com.dt5000.ischool.activity.teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
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
import com.dt5000.ischool.adapter.teacher.HomeworkAddChooseSubjectListAdapter;
import com.dt5000.ischool.entity.SubjectItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 发布作业时选择科目页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 下午2:22:54
 * @ClassInfo 
 *            com.dt5000.ischool.activity.teacher.HomeworkAddChooseSubjectActivity
 * @Description
 */
public class HomeworkAddChooseSubjectActivity extends Activity {

	private ListView listview_subject;
	private LinearLayout lLayout_back;
	private TextView txt_title;

	private User user;
	private List<SubjectItem> subjectList;
	private HomeworkAddChooseSubjectListAdapter homeworkAddChooseSubjectListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_add_choose_subject);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择科目");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		listview_subject = (ListView) findViewById(R.id.listview_subject);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAddChooseSubjectActivity.this.finish();
			}
		});

		// 点击选中科目并回传给上一个页面
		listview_subject.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SubjectItem subjectItem = subjectList.get(position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("subjectItem", subjectItem);
				intent.putExtras(bundle);
				HomeworkAddChooseSubjectActivity.this.setResult(RESULT_OK, intent);
				HomeworkAddChooseSubjectActivity.this.finish();
			}
		});
	}

	private void init() {
		user = User.getUser(this);
	}

	/**
	 * 获取科目列表数据
	 */
	private void getData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SUBJECT);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());
		FinalHttp fh = new FinalHttp();
		fh.post(UrlProtocol.MAIN_HOST, ajaxParams, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				MLog.i("科目返回结果：" + t);
				try {
					JSONObject jsonObject = new JSONObject(t);
					String subjectStr = jsonObject.optString("subjectItems");
					
					subjectList = new Gson().fromJson(subjectStr,
							new TypeToken<List<SubjectItem>>() {
							}.getType());

					if (subjectList != null && subjectList.size() > 0) {
						homeworkAddChooseSubjectListAdapter = new HomeworkAddChooseSubjectListAdapter(
								HomeworkAddChooseSubjectActivity.this, subjectList);
						listview_subject.setAdapter(homeworkAddChooseSubjectListAdapter);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
