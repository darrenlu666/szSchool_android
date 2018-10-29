package com.dt5000.ischool.activity.student;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyPaper;
import com.dt5000.ischool.entity.StudyTest;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.SubjectUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

/**
 * 自主学习自测评估试题页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月25日 下午4:12:48
 * @ClassInfo com.dt5000.ischool.activity.student.StudyTestingActivity
 * @Description
 */
public class StudyTestingActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_name;
	private TextView txt_page;
	private TextView txt_subjective;
	private LinearLayout lLayout_bottom;
	private LinearLayout lLayout_option;
	private CheckBox checkBoxA;
	private CheckBox checkBoxB;
	private CheckBox checkBoxC;
	private CheckBox checkBoxD;
	private Button btn_confirm;
	private Button btn_unfinished;
	private LinearLayout lLayout_previous;
	private LinearLayout lLayout_collect;
	private LinearLayout lLayout_submit;
	private LinearLayout lLayout_next;
	private ViewFlipper viewFilpper_test;

	private StudyPaper paper;
	private List<StudyTest> testList;
	private User user;
	private FinalHttp finalHttp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing);

		initView();
		initListener();
		init();
		getData();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_page = (TextView) findViewById(R.id.txt_page);
		txt_subjective = (TextView) findViewById(R.id.txt_subjective);
		lLayout_bottom = (LinearLayout) findViewById(R.id.lLayout_bottom);
		lLayout_option = (LinearLayout) findViewById(R.id.lLayout_option);
		checkBoxA = (CheckBox) findViewById(R.id.checkBoxA);
		checkBoxB = (CheckBox) findViewById(R.id.checkBoxB);
		checkBoxC = (CheckBox) findViewById(R.id.checkBoxC);
		checkBoxD = (CheckBox) findViewById(R.id.checkBoxD);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_unfinished = (Button) findViewById(R.id.btn_unfinished);
		lLayout_previous = (LinearLayout) findViewById(R.id.lLayout_previous);
		lLayout_next = (LinearLayout) findViewById(R.id.lLayout_next);
		lLayout_collect = (LinearLayout) findViewById(R.id.lLayout_collect);
		lLayout_submit = (LinearLayout) findViewById(R.id.lLayout_submit);
		viewFilpper_test = (ViewFlipper) findViewById(R.id.viewFilpper_test);
	}

	@SuppressLint("InflateParams")
	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingActivity.this.finish();
			}
		});

		// 点击显示ViewFlipper上一页
		lLayout_previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPreviousFlipper();
			}
		});

		// 点击显示ViewFlipper下一页
		lLayout_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextFlipper();
			}
		});

		// 点击确定按钮，保存用户的选项答案并转至下一题
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userAnswer = "";
				if (checkBoxA.isChecked()) {
					userAnswer += checkBoxA.getText().toString();
				}

				if (checkBoxB.isChecked()) {
					userAnswer += checkBoxB.getText().toString();
				}

				if (checkBoxC.isChecked()) {
					userAnswer += checkBoxC.getText().toString();
				}

				if (checkBoxD.isChecked()) {
					userAnswer += checkBoxD.getText().toString();
				}

				// 获取当前页面的试题
				int tag = (Integer) viewFilpper_test.getCurrentView().getTag();
				if (tag > testList.size()) {
					return;
				}
				StudyTest studyTest = testList.get(tag - 1);

				// 更新当前试题的用户答案
				if (!"".equals(userAnswer)) {
					studyTest.setUserDaan(userAnswer);
					if (studyTest.getDaan().equals(userAnswer)) {
						studyTest.setResult("right");
					} else {
						studyTest.setResult("false");
					}
				}

				// 显示下一页
				showNextFlipper();
			}
		});

		// 点击收藏
		lLayout_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ListAdapter listAdapter = new ArrayAdapter<>(
						StudyTestingActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"错题收藏", "难题收藏", "套题收藏" });

				new AlertDialog.Builder(StudyTestingActivity.this)
						.setSingleChoiceItems(listAdapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										switch (which) {
										case 0:// 错题收藏，type为1
											collectStudyTest(1);
											break;
										case 1:// 难题收藏，type为2
											collectStudyTest(2);
											break;
										case 2:// 套题收藏，type为3
											collectStudyTest(3);
											break;
										}
									}
								}).show();
			}
		});

		// 点击交卷
		lLayout_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String undonStr = "";
				int count = 0;
				for (int i = 0; i < testList.size(); i++) {
					StudyTest studyTest = testList.get(i);
					if (studyTest.getType() <= 3) {
						if ("undone".equals(studyTest.getResult())) {
							undonStr += (i + 1) + "  ";
							count++;
						}
					}
				}

				if (count > 0) {
					new AlertDialog.Builder(StudyTestingActivity.this)
							.setTitle(count + "道题目未完成")
							.setMessage(undonStr)
							.setPositiveButton("交卷",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											submitStudyTest();
										}
									}).setNegativeButton("取消", null).show();
				} else {
					submitStudyTest();
				}
			}
		});

		// 点击未完成
		btn_unfinished.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String undonStr = "";
				for (int i = 0; i < testList.size(); i++) {
					StudyTest studyTest = testList.get(i);
					if (studyTest.getType() <= 3) {
						if ("undone".equals(studyTest.getResult())) {
							undonStr += (i + 1) + "  ";
						}
					}
				}

				if (!"".equals(undonStr)) {
					new AlertDialog.Builder(StudyTestingActivity.this)
							.setTitle("以下题目未完成").setPositiveButton("确定", null)
							.setMessage(undonStr).show();
				}
			}
		});
	}

	private void init() {
		paper = (StudyPaper) getIntent().getExtras().getSerializable("paper");

		txt_title.setText(paper.getName());

		user = User.getUser(this);

		finalHttp = new FinalHttp();

		testList = new ArrayList<StudyTest>();
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_PAGER);
		map.put("testHeadId", String.valueOf(paper.getSelfTestId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				StudyTestingActivity.this, user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, StudyTestingActivity.this, user.getUserId());
		MLog.i("自测评估试题地址：" + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("questionList");

							// 解析实体类
							Type listType = new TypeToken<List<StudyTest>>() {
							}.getType();
							List<StudyTest> data = (List<StudyTest>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								testList = data;

								// 给每个webView设置tag来标识位置
								int tag = 1;
								for (StudyTest studyTest : testList) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("operationType",
											UrlProtocol.OPERATION_TYPE_SELFTEST_SINGLE_ITEM);
									map.put("questionId",
											studyTest.getTestLineId());
									AjaxParams params = UrlBulider
											.getAjaxParams(map,
													StudyTestingActivity.this,
													user.getUserId());
									String httpURL = UrlBulider.getHttpURL(map, StudyTestingActivity.this, user.getUserId());
									MLog.i("自测评估单个试题地址：" + httpURL);

									viewFilpper_test.addView(addWebView(params,
											tag));

									tag++;
								}

								// 设置题目类型、页码、选项框状态
								changePageView((int) viewFilpper_test
										.getCurrentView().getTag());

								// 底部操作布局显示
								lLayout_bottom.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							e.printStackTrace();
							lLayout_bottom.setVisibility(View.GONE);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						lLayout_bottom.setVisibility(View.GONE);
					}
				});
	}

	@SuppressLint("SetJavaScriptEnabled")
	private View addWebView(AjaxParams ajaxParams, int tag) {
		final MyWebView myWebView = new MyWebView(this);
		myWebView.setTag(tag);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setDefaultTextEncodingName("utf-8");
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						try {
							JSONObject obj = new JSONObject(t);
							String webData = obj.optString("url");
							myWebView.setUrlData(webData);
							myWebView.loadDataWithBaseURL(null, webData,
									"text/html", "utf-8", null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		return myWebView;
	}

	private void showPreviousFlipper() {
		// 获取开始给WebView设置的tag来区分页码
		int tag = (int) viewFilpper_test.getCurrentView().getTag();

		// 设置ViewFlipper的滑动动画
		viewFilpper_test.setInAnimation(AnimationUtils.loadAnimation(
				StudyTestingActivity.this, R.anim.flipper_left_in));
		viewFilpper_test.setOutAnimation(AnimationUtils.loadAnimation(
				StudyTestingActivity.this, R.anim.flipper_left_out));

		// 页码减1
		tag--;

		// WebView设置的tag页码是从1开始的
		if (tag >= 1) {
			// 显示上一页
			viewFilpper_test.showPrevious();

			// 设置题目类型、页码、选项框状态
			changePageView(tag);
		}
	}

	private void showNextFlipper() {
		int tag = (int) viewFilpper_test.getCurrentView().getTag();

		viewFilpper_test.setInAnimation(AnimationUtils.loadAnimation(
				StudyTestingActivity.this, R.anim.flipper_right_in));
		viewFilpper_test.setOutAnimation(AnimationUtils.loadAnimation(
				StudyTestingActivity.this, R.anim.flipper_right_out));

		tag++;

		if (tag <= testList.size()) {
			viewFilpper_test.showNext();
			changePageView(tag);
		}
	}

	private void changePageView(int tag) {
		// 选项框改变
		cancleChecked();

		// 获取当前页面位置对应的试题
		StudyTest studyTest = testList.get(tag - 1);

		// 设置题目类型
		int typeNum = studyTest.getType();
		txt_name.setText(SubjectUtil.getStudyTestType(typeNum));

		// 设置页码
		txt_page.setText(tag + "/" + testList.size());

		if (typeNum <= 3) {// 选择题
			lLayout_option.setVisibility(View.VISIBLE);
			txt_subjective.setVisibility(View.GONE);

			if (!"undone".equals(studyTest.getResult())) {
				String userAnswer = studyTest.getUserDaan();
				checkedByAnswer(userAnswer);
			}
		} else {// 主观题
			lLayout_option.setVisibility(View.GONE);
			txt_subjective.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 将所有选项CheckBox置为非选中状态
	 */
	private void cancleChecked() {
		checkBoxA.setChecked(false);
		checkBoxB.setChecked(false);
		checkBoxC.setChecked(false);
		checkBoxD.setChecked(false);
	}

	/**
	 * 根据用户的答案设置选项CheckBox的状态
	 * 
	 * @param userAnswer
	 */
	private void checkedByAnswer(String userAnswer) {
		for (int i = 0; i < userAnswer.length(); i++) {
			char item = userAnswer.charAt(i);
			switch (item) {
			case 'A':
				checkBoxA.setChecked(true);
				break;
			case 'B':
				checkBoxB.setChecked(true);
				break;
			case 'C':
				checkBoxC.setChecked(true);
				break;
			case 'D':
				checkBoxD.setChecked(true);
				break;
			}
		}
	}

	class MyWebView extends WebView {
		private float downX;
		private long downTime;
		private String urlData;

		public MyWebView(Context context) {
			super(context);
		}

		public String getUrlData() {
			return urlData;
		}

		public void setUrlData(String urlData) {
			this.urlData = urlData;
		}

		@Override
		public void reload() {
			super.loadDataWithBaseURL(null, urlData, "text/html", "utf-8", null);
		}

		@Override
		public boolean performClick() {
			return super.performClick();
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			performClick();
			switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = evt.getX();
				downTime = evt.getEventTime();
				break;
			case MotionEvent.ACTION_UP:
				float currentX = evt.getX();
				long currentTime = evt.getEventTime();
				float distanceX = Math.abs(downX - currentX);
				long distanceTime = currentTime - downTime;

				// 判断是否满足滑动条件，X方向滑动距离超过50，并且滑动时间小于220毫秒
				if (distanceX > 50 && distanceTime < 220) {
					// 判断滑动方向
					if (downX < currentX) {// 右滑显示上一页
						showPreviousFlipper();
					} else if (downX > currentX) {// 左滑显示下一页
						showNextFlipper();
					}
				}
				break;
			}
			return super.onTouchEvent(evt);
		}
	}

	/**
	 * 发送请求收藏错题、难题和套题
	 * 
	 * @param type
	 */
	private void collectStudyTest(int type) {
		int tag = (int) viewFilpper_test.getCurrentView().getTag();
		StudyTest studyTest = testList.get(tag - 1);

		// 配置参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_SELFTEST_SAVE_QUE);
		map.put("type", String.valueOf(type));
		if (type == 3) {// 套题收藏使用试卷id
			map.put("testId", String.valueOf(paper.getSelfTestId()));
		} else {// 难题和错题收藏使用试题id
			map.put("testId", studyTest.getTestLineId());
		}
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("收藏试题返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("result");
							if ("success".equals(result)) {
								MToast.show(StudyTestingActivity.this,
										"收藏成功", MToast.SHORT);
							} else {
								MToast.show(StudyTestingActivity.this,
										"收藏失败", MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
							MToast.show(StudyTestingActivity.this, "收藏失败",
									MToast.SHORT);
						}
					}
				});
	}

	/**
	 * 交卷，跳转到自测结果页面，计算成绩
	 */
	private void submitStudyTest() {
		Intent intent = new Intent(StudyTestingActivity.this,
				StudyTestingResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("testHeadId", paper.getSelfTestId());
		bundle.putString("subjectId", String.valueOf(paper.getSubjectId()));
		bundle.putString("name", paper.getName());
		bundle.putSerializable("testList", (Serializable) testList);
		intent.putExtras(bundle);
		startActivity(intent);
		StudyTestingActivity.this.finish();
	}

}
