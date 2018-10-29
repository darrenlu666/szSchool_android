package com.dt5000.ischool.activity.student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyTest;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.SubjectUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 自主学习自测评估收藏试题的解析页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月28日 上午10:19:41
 * @ClassInfo 
 *            com.dt5000.ischool.activity.student.StudyTestingCollectAnalysisActivity
 * @Description 本页面逻辑和{@link StudyTestingAnalysisActivity}差不多，修改时同步参考修改
 */
public class StudyTestingCollectAnalysisActivity extends Activity {

	private LinearLayout lLayout_back;
	private LinearLayout lLayout_bottom;
	private TextView txt_title;
	private TextView txt_name;
	private TextView txt_page;
	private LinearLayout lLayout_previous;
	private LinearLayout lLayout_analysis;
	private LinearLayout lLayout_next;
	private ViewFlipper viewFilpper_test;

	private List<StudyTest> anslysisTestList;
	private User user;
	private int sortIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_testing_collect_analysis);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("试题解析");
		lLayout_bottom = (LinearLayout) findViewById(R.id.lLayout_bottom);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_page = (TextView) findViewById(R.id.txt_page);
		lLayout_previous = (LinearLayout) findViewById(R.id.lLayout_previous);
		lLayout_analysis = (LinearLayout) findViewById(R.id.lLayout_analysis);
		lLayout_next = (LinearLayout) findViewById(R.id.lLayout_next);
		viewFilpper_test = (ViewFlipper) findViewById(R.id.viewFilpper_test);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyTestingCollectAnalysisActivity.this.finish();
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

		// 点击跳转到试题解析答案页面
		lLayout_analysis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int tag = (int) viewFilpper_test.getCurrentView().getTag();
				StudyTest studyTest = anslysisTestList.get(tag - 1);

				Intent intent = new Intent(
						StudyTestingCollectAnalysisActivity.this,
						StudyTestingAnalysisAnswerActivity.class);
				intent.putExtra("testLineId", studyTest.getTestLineId());
				startActivity(intent);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		Bundle bundle = getIntent().getExtras();
		sortIndex = bundle.getInt("sortIndex");
		anslysisTestList = (List<StudyTest>) bundle
				.getSerializable("analysisTestList");

		user = User.getUser(this);

		// 给每个webView设置tag来标识位置
		int tag = 1;
		for (StudyTest studyTest : anslysisTestList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("operationType",
					UrlProtocol.OPERATION_TYPE_SELFTEST_SINGLE_ITEM);
			map.put("questionId", studyTest.getTestLineId());
			AjaxParams params = UrlBulider.getAjaxParams(map,
					StudyTestingCollectAnalysisActivity.this, user.getUserId());

			viewFilpper_test.addView(addWebView(params, tag));

			tag++;
		}

		// 如果传过来题号，就显示该题
		if (sortIndex >= 1) {
			viewFilpper_test.setDisplayedChild(sortIndex - 1);
		}

		// 设置题目类型、页码、选项框状态
		changePageView((int) viewFilpper_test.getCurrentView().getTag());

		// 底部操作布局显示
		lLayout_bottom.setVisibility(View.VISIBLE);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private View addWebView(AjaxParams params, int tag) {
		final MyWebView myWebView = new MyWebView(this);
		myWebView.setTag(tag);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setDefaultTextEncodingName("utf-8");
		new FinalHttp().post(UrlProtocol.MAIN_HOST, params,
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
				StudyTestingCollectAnalysisActivity.this,
				R.anim.flipper_left_in));
		viewFilpper_test.setOutAnimation(AnimationUtils.loadAnimation(
				StudyTestingCollectAnalysisActivity.this,
				R.anim.flipper_left_out));

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
				StudyTestingCollectAnalysisActivity.this,
				R.anim.flipper_right_in));
		viewFilpper_test.setOutAnimation(AnimationUtils.loadAnimation(
				StudyTestingCollectAnalysisActivity.this,
				R.anim.flipper_right_out));

		tag++;

		if (tag <= anslysisTestList.size()) {
			viewFilpper_test.showNext();
			changePageView(tag);
		}
	}

	private void changePageView(int tag) {
		// 获取当前页面位置对应的试题
		StudyTest studyTest = anslysisTestList.get(tag - 1);

		// 设置题目类型
		int typeNum = studyTest.getType();
		txt_name.setText(SubjectUtil.getStudyTestType(typeNum));

		// 设置页码
		txt_page.setText(tag + "/" + anslysisTestList.size());
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

}
