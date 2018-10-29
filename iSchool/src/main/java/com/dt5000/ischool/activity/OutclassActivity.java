package com.dt5000.ischool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.MainApplication;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.teacher.BlogClassListActivity;
import com.dt5000.ischool.entity.Banner;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课外页面
 * 
 * @author 周锋
 * @date 2016年1月7日 下午1:33:15
 * @ClassInfo com.dt5000.ischool.activity.OutclassActivity
 * @Description
 */
public class OutclassActivity extends Activity {

	private TextView txt_blog_count;
	private ImageView img_banner;
	private RelativeLayout rLayout_to_blog;
	private RelativeLayout rLayout_to_school_announcement;
	private RelativeLayout rLayout_to_education;
	private RelativeLayout rLayout_to_diandiango;
	private RelativeLayout rLayout_to_album;
	private RelativeLayout rLayout_to_children;
	private RelativeLayout rLayout_to_food;
	private RelativeLayout rLayout_to_question;

	private User user;
	private List<Banner> bannerList;
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainApplication.activityList.add(this);
		setContentView(R.layout.activity_outclass);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_blog_count = (TextView) findViewById(R.id.txt_blog_count);
		img_banner = (ImageView) findViewById(R.id.img_banner);
		rLayout_to_blog = (RelativeLayout) findViewById(R.id.rLayout_to_blog);
		rLayout_to_school_announcement = (RelativeLayout) findViewById(R.id.rLayout_to_school_announcement);
		rLayout_to_education = (RelativeLayout) findViewById(R.id.rLayout_to_education);
		rLayout_to_diandiango = (RelativeLayout) findViewById(R.id.rLayout_to_diandiango);
		rLayout_to_children = (RelativeLayout) findViewById(R.id.rLayout_to_children);
		rLayout_to_album = (RelativeLayout) findViewById(R.id.rLayout_to_album);
		rLayout_to_food = (RelativeLayout) findViewById(R.id.rLayout_to_food);
		rLayout_to_question = (RelativeLayout) findViewById(R.id.rLayout_to_question);
	}

	private void initListener() {
		// 点击跳转到公告详情页
		img_banner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bannerList != null && bannerList.size() >= 2) {
					Banner banner = bannerList.get(1);
					Intent intent = new Intent(OutclassActivity.this,
							BannerDetailActivity.class);
					intent.putExtra("linkUrl", banner.getLinkUrl());
					startActivity(intent);
				}
			}
		});

		// 点击跳转到班级博客页面
		rLayout_to_blog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.isTeacherRole(user.getRole())) {
					startActivity(new Intent(OutclassActivity.this,
							BlogClassListActivity.class));
				} else {
					startActivity(new Intent(OutclassActivity.this,
							BlogListActivity.class));
				}
			}
		});

		// 点击跳转到校园公告页面
		rLayout_to_school_announcement
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(OutclassActivity.this, SchoolAnnouncementListActivity.class));
					}
				});

		// 点击跳转到教育资讯页面
		rLayout_to_education.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OutclassActivity.this, EducationListActivity.class));
			}
		});

		// 点击跳转到点点GO页面
		rLayout_to_diandiango.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OutclassActivity.this,
						DianDianGoActivity.class));
			}
		});

		// 点击跳转到班级相册页面
		rLayout_to_album.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断学校类型
				if (User.isKindergarten(user.getSchoolCode())) {
					startActivity(new Intent(OutclassActivity.this,
							AlbumListActivity.class));
				} else {
					// 普通学校班级相册接口待做
					// startActivity(new Intent(OutclassActivity.this,
					// AlbumListNoUseActivity.class));
					startActivity(new Intent(OutclassActivity.this,
							AlbumListActivity.class));
				}
			}
		});

		// 点击跳转到育儿经页面
		rLayout_to_children.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OutclassActivity.this,
						ChildrenListActivity.class));
			}
		});

		// 点击跳转到每日食谱页面
		rLayout_to_food.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OutclassActivity.this,
						FoodListActivity.class));
			}
		});

		// 点击跳转到问卷调查页面
		rLayout_to_question.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OutclassActivity.this,
						QuestionSurveyActivity.class));
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();

		// 判断学校类型
		if (User.isKindergarten(user.getSchoolCode())) {
			// 幼儿园没有点点GO模块
			rLayout_to_diandiango.setVisibility(View.INVISIBLE);
		}
	}

	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_BANNER);
		map.put("schoolId", String.valueOf(user.getSchoolbaseinfoId()));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("轮播数据返回结果：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("banners");

							// 解析实体类
							Type listType = new TypeToken<List<Banner>>() {
							}.getType();
							List<Banner> data = (List<Banner>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() >= 2) {
								bannerList = data;

								Banner banner = bannerList.get(1);
								ImageLoaderUtil.createSimple(
										OutclassActivity.this).displayImage(
										banner.getImageUrl(), img_banner);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 学生端获取动态更新数据
		if (User.isStudentRole(user.getRole())) {
			getDynamics();
		}
	}

	// 获取动态信息，包括作业、博客等
	private void getDynamics() {
		// 获取本地保存过的最大作业、博客等id
		SharedPreferences sharedPreferences = getSharedPreferences(
				MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
		String homeworkMaxId = sharedPreferences
				.getString("homeworkMaxId", "0");
		String blogMaxId = sharedPreferences.getString("blogMaxId", "0");

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_DYNAMICS);
		map.put("cid", user.getClassinfoId());
		map.put("homeworkMaxId", homeworkMaxId);
		map.put("blogMaxId", blogMaxId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());
		String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
		MLog.i("动态更新地址： " + httpURL);

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						try {
							JSONObject obj = new JSONObject(t);
							int blogNewCount = obj.optInt("blogNewCount");

							if (blogNewCount > 0) {
								txt_blog_count.setVisibility(View.VISIBLE);
								if (blogNewCount < 100) {
									txt_blog_count.setText(String
											.valueOf(blogNewCount));
								} else {
									txt_blog_count.setText("99");
								}
							} else {
								txt_blog_count.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MLog.i("OutclassActivity.onDestroy");
	}

	/** 用于监听返回键的时间计时 */
	private long lastBackClickTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 点击返回，判断时间间隔
			if ((System.currentTimeMillis() - lastBackClickTime) > 2000) {
				MToast.show(OutclassActivity.this, "再按一次退出", MToast.SHORT);
				lastBackClickTime = System.currentTimeMillis();
			} else {
				// 退出程序
				super.onKeyDown(keyCode, event);
			}
			return true;
		}
		return false;
	}

}
