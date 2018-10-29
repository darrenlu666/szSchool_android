package com.dt5000.ischool.activity.student;

import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.StudyPractice;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 自主学习同步练习页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月22日 下午2:48:29
 * @ClassInfo com.dt5000.ischool.activity.student.StudyPracticeActivity
 * @Description
 */
public class StudyPracticeActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private TextView txt_name;
	private TextView txt_page;
	private ViewFlipper viewFlipper;

	private MyWebView myWebView;
	private String videoId;
	private String videoName;
	private List<StudyPractice> practiceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_practice);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("同步练习");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("解析");
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_page = (TextView) findViewById(R.id.txt_page);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyPracticeActivity.this.finish();
			}
		});

		// 点击跳转到答案解析页面
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int count = (int) viewFlipper.getCurrentView().getTag();
				StudyPractice practice = practiceList.get(count - 1);

				Intent intent = new Intent(StudyPracticeActivity.this,
						StudyPracticeExplainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", videoName);
				bundle.putString("page", String.valueOf(count));
				bundle.putSerializable("practice", practice);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		videoId = intent.getStringExtra("videoId");
		videoName = intent.getStringExtra("videoName");

		txt_name.setText(videoName);
	}

	private void getData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = UrlProtocol.COURSE_PRACTISE_LIST + "?flashid="
							+ videoId;
					String result = HttpClientUtil.doPost(url, null);
					MLog.i("同步练习返回结果：" + result);
					Message message = new Message();
					message.obj = result;
					handle.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			try {
				String result = (String) msg.obj;
				JSONObject jsonObject = new JSONObject(result);
				String json = jsonObject.optString("ds");
				List<StudyPractice> data = (List<StudyPractice>) GsonUtil
						.jsonToList(json, new TypeToken<List<StudyPractice>>() {
						}.getType());

				if (data != null && data.size() > 0) {
					practiceList = data;

					// 给ViewFlipper添加子页面
					int count = 1;
					for (StudyPractice p : data) {
						viewFlipper.addView(addWebView(
								UrlProtocol.COURSE_PRACTISE_DETAIL + "?id="
										+ p.getId(), count));
						count++;
					}

					// 设置页码
					int i = (int) viewFlipper.getCurrentView().getTag();
					txt_page.setText(i + "/" + practiceList.size());
				} else {
					txt_name.setText("暂无练习");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	@SuppressLint("SetJavaScriptEnabled")
	private View addWebView(String url, Integer count) {
		myWebView = new MyWebView(this, viewFlipper);
		myWebView.setTag(count);
		myWebView.loadUrl(url);
		myWebView.getSettings().setJavaScriptEnabled(true);
		return myWebView;
	}

	class MyWebView extends WebView {
		private ViewFlipper flipper;
		private float downX;
		private long downTime;

		public MyWebView(Context context, ViewFlipper flipper) {
			super(context);
			this.flipper = flipper;
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
					// 获取开始给WebView设置的tag来区分页码
					int i = (int) viewFlipper.getCurrentView().getTag();

					// 判断滑动方向
					if (downX < currentX) {// 右滑显示上一页
						// 设置ViewFlipper的滑动动画
						this.flipper.setInAnimation(getContext(),
								R.anim.flipper_left_in);
						this.flipper.setOutAnimation(getContext(),
								R.anim.flipper_left_out);

						// 页码减1
						i--;

						// WebView设置的tag页码是从1开始的
						if (i >= 1) {
							// 显示上一页
							viewFlipper.showPrevious();
							// 设置页码
							txt_page.setText(i + "/" + practiceList.size());
						}
					} else if (downX > currentX) {// 左滑显示下一页
						this.flipper.setInAnimation(getContext(),
								R.anim.flipper_right_in);
						this.flipper.setOutAnimation(getContext(),
								R.anim.flipper_right_out);

						i++;

						if (i <= practiceList.size()) {
							viewFlipper.showNext();
							txt_page.setText(i + "/" + practiceList.size());
						}
					}
				}
				break;
			}
			return super.onTouchEvent(evt);
		}
	}

}
