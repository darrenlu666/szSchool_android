package com.dt5000.ischool.activity.student;

import java.io.Serializable;
import java.text.DecimalFormat;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyTest;
import com.dt5000.ischool.entity.StudyTestResult;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.Gson;

/**
 * 自主学习自测评估结果页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月26日 下午3:59:59
 * @ClassInfo com.dt5000.ischool.activity.student.StudyTestingResultActivity
 * @Description
 */
public class StudyTestingResultActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_name;
	private TextView txt_all_num;
	private TextView txt_objective_num;
	private TextView txt_right_num;
	private TextView txt_wrong_num;
	private TextView txt_objective_accuracy;
	private Button btn_analysis_wrong;
	private Button btn_analysis_all;
	private Button btn_save;

	private User user;
	private List<StudyTest> testList;
	private List<StudyTest> wrongTestList;
	private List<StudyTestResult> resultList;
	private int testHeadId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing_result);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("测试结果");
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_all_num = (TextView) findViewById(R.id.txt_all_num);
		txt_right_num = (TextView) findViewById(R.id.txt_right_num);
		txt_wrong_num = (TextView) findViewById(R.id.txt_wrong_num);
		txt_objective_num = (TextView) findViewById(R.id.txt_objective_num);
		txt_objective_accuracy = (TextView) findViewById(R.id.txt_objective_accuracy);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_analysis_wrong = (Button) findViewById(R.id.btn_analysis_wrong);
		btn_analysis_all = (Button) findViewById(R.id.btn_analysis_all);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingResultActivity.this.finish();
			}
		});

		// 点击保存测试
		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveResultTest();
			}
		});

		// 点击错题解析
		btn_analysis_wrong.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (wrongTestList.size() > 0) {
					Intent intent = new Intent(StudyTestingResultActivity.this,
							StudyTestingAnalysisActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("testHeadId", testHeadId);
					bundle.putSerializable("analysisTestList",
							(Serializable) wrongTestList);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					MToast.show(StudyTestingResultActivity.this,
							"本次测试没有答错的试题", MToast.SHORT);
				}
			}
		});

		// 点击全套解析
		btn_analysis_all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StudyTestingResultActivity.this,
						StudyTestingAnalysisActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("testHeadId", testHeadId);
				bundle.putSerializable("analysisTestList",
						(Serializable) testList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");
		testHeadId = bundle.getInt("testHeadId");
		testList = (List<StudyTest>) bundle.getSerializable("testList");

		txt_name.setText(name);

		user = User.getUser(this);

		wrongTestList = new ArrayList<StudyTest>();
		resultList = new ArrayList<StudyTestResult>();

		int allNum = testList.size();// 总题数
		int rightNum = 0;// 客观题正确数
		int wrongNum = 0;// 客观题错误数
		for (StudyTest studyTest : testList) {
			if (studyTest.getType() <= 3) {// 客观题
				String userDaan = studyTest.getUserDaan();
				String daan = studyTest.getDaan();
				if (userDaan.equals(daan)) {
					rightNum++;
				} else {
					wrongNum++;
					wrongTestList.add(studyTest);
				}

				// 将客观题做题结果放入集合
				StudyTestResult testResult = new StudyTestResult();
				testResult.setTestLineId(studyTest.getTestLineId());
				testResult.setDaan(daan);
				String userAnswer = !CheckUtil.stringIsBlank(userDaan) ? userDaan
						: "";
				testResult.setUserDaan(userAnswer);
				resultList.add(testResult);
			}
		}

		// 计算客观题数
		int objectiveNum = rightNum + wrongNum;

		txt_all_num.setText(String.valueOf(allNum));
		txt_objective_num.setText(String.valueOf(objectiveNum));
		txt_right_num.setText(String.valueOf(rightNum));
		txt_wrong_num.setText(String.valueOf(wrongNum));

		// 计算客观题正确率
		double accuracy = ((double) rightNum / objectiveNum) * 100;
		txt_objective_accuracy.setText(new DecimalFormat("0.0")
				.format(accuracy) + "%");
	}

	/**
	 * 发送请求保存测试结果，将所有客观题做题结果上传至服务器，在做题记录页面可以查看做过的客观题
	 */
	private void saveResultTest() {
		if (resultList.size() > 0) {
			// 将做题结果转成Json
			String testResult = new Gson().toJson(resultList);

			// 封装参数
			Map<String, String> map = new HashMap<String, String>();
			map.put("operationType",
					UrlProtocol.OPERATION_TYPE_SELFTEST_SAVE_TEST);
			map.put("testHeadId", String.valueOf(testHeadId));
			map.put("testResult", testResult);
			AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
					user.getUserId());

			// 发送请求
			new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
					new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String t) {
							MLog.i("保存测试返回结果：" + t);
							try {
								JSONObject obj = new JSONObject(t);
								String result = obj.optString("result");
								if ("success".equals(result)) {
									DialogAlert.show(
											StudyTestingResultActivity.this,
											"已保存至服务器");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

}
