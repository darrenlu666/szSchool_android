package com.dt5000.ischool.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.Diary;
import com.dt5000.ischool.entity.DiaryDoc;
import com.dt5000.ischool.entity.DiaryPic;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;

/**
 * 日记详情页面
 * 
 * @author 周锋
 * @date 2016年2月4日 上午11:19:18
 * @ClassInfo com.dt5000.ischool.activity.DiaryDetailActivity
 * @Description
 */
public class DiaryDetailActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;
	private TextView txt_diary_title;
	private ImageView img_level;
	private Button btn_appraise;
	private TextView txt_time;
	private LinearLayout lLayout_doc_attach;
	private TextView txt_attach;
	private TextView txt_content;
	private RelativeLayout rLayout_pic_attach;
	private ImageView img_pic_attach;
	private TextView txt_pic_count;

	private User user;
	private Diary diary;
	private String classId;
	private List<DiaryPic> attachList;
	private List<DiaryDoc> docList;
	private boolean appraiseSuccess = false;// 标识有没有评价成功

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_detail);

		initView();
		initListener();
		init();
	}

	private void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("日记详情");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("分享");
		txt_diary_title = (TextView) findViewById(R.id.txt_diary_title);
		txt_time = (TextView) findViewById(R.id.txt_time);
		img_level = (ImageView) findViewById(R.id.img_level);
		btn_appraise = (Button) findViewById(R.id.btn_appraise);
		lLayout_doc_attach = (LinearLayout) findViewById(R.id.lLayout_doc_attach);
		txt_attach = (TextView) findViewById(R.id.txt_attach);
		txt_content = (TextView) findViewById(R.id.txt_content);
		rLayout_pic_attach = (RelativeLayout) findViewById(R.id.rLayout_pic_attach);
		img_pic_attach = (ImageView) findViewById(R.id.img_pic_attach);
		txt_pic_count = (TextView) findViewById(R.id.txt_pic_count);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleBack();
			}
		});

		// 点击图片
		img_pic_attach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DiaryDetailActivity.this,
						DiaryPicPagerListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("attachList", (Serializable) attachList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 点击附件
		txt_attach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DiaryDetailActivity.this,
						DiaryAttachListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("docList", (Serializable) docList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 点击评价
		btn_appraise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ListAdapter adapter = new ArrayAdapter<String>(
						DiaryDetailActivity.this,
						android.R.layout.simple_list_item_1, new String[] {
								"阅", "优", "良", "加油" });

				new AlertDialog.Builder(DiaryDetailActivity.this)
						.setSingleChoiceItems(adapter, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

										appraiseDialog(which);
									}
								}).show();
			}
		});

		// 点击分享
		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DiaryDetailActivity.this)
						.setMessage("是否分享到班级博客？")
						.setPositiveButton("分享",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										diaryShare();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		classId = bundle.getString("classId");
		diary = (Diary) bundle.getSerializable("diary");

		user = User.getUser(this);

		// 只有教师端才有日记分享
		if (User.isTeacherRole(user.getRole())) {
			txt_topbar_btn.setVisibility(View.VISIBLE);
		} else {
			txt_topbar_btn.setVisibility(View.GONE);
		}

		txt_diary_title.setText(diary.getTitle());
		txt_time.setText(diary.getAuthor() + " " + diary.getCreateAt());
		txt_content.setText(diary.getContent());

		// 判断是否被评价过
		int level = diary.getLevelRank();
		if (level < 1) {// 没有被评价过
			img_level.setVisibility(View.GONE);

			// 学生端没有评价功能
			if (User.isStudentRole(user.getRole())) {
				btn_appraise.setVisibility(View.GONE);
			} else {
				btn_appraise.setVisibility(View.VISIBLE);
			}
		} else {// 已经被评价过
			img_level.setVisibility(View.VISIBLE);
			btn_appraise.setVisibility(View.GONE);
			if (level == 4) {
				img_level.setBackgroundResource(R.drawable.a_level_jiayou);
			} else if (level == 2) {
				img_level.setBackgroundResource(R.drawable.a_level_liang);
			} else if (level == 1) {
				img_level.setBackgroundResource(R.drawable.a_level_you);
			} else if (level == 3) {
				img_level.setBackgroundResource(R.drawable.a_level_yue);
			}
		}

		// 判断图片附件
		attachList = diary.getAttachList();
		if (attachList != null && attachList.size() > 0) {
			rLayout_pic_attach.setVisibility(View.VISIBLE);
			txt_pic_count.setVisibility(View.VISIBLE);
			// 加载图片附件中的第一张图片
			ImageLoaderUtil.createSimple(this).displayImage(
					attachList.get(0).getImageUrl(), img_pic_attach);
			// 图片数量
			txt_pic_count.setText(attachList.size() + "张图片");
		} else {
			rLayout_pic_attach.setVisibility(View.GONE);
			txt_pic_count.setVisibility(View.GONE);
		}

		// 判断文档附件
		docList = diary.getDocList();
		if (docList != null && docList.size() > 0) {
			lLayout_doc_attach.setVisibility(View.VISIBLE);
			// 显示第一个附件的名称
			txt_attach.setText(docList.get(0).getFileName());
		} else {
			lLayout_doc_attach.setVisibility(View.GONE);
		}
	}

	/**
	 * 日记评价提示
	 * 
	 * @param type
	 */
	private void appraiseDialog(int type) {
		String typeStr = "";
		int level = 0;

		switch (type) {
		case 0:
			typeStr = "阅";
			level = 3;
			break;
		case 1:
			typeStr = "优";
			level = 1;
			break;
		case 2:
			typeStr = "良";
			level = 2;
			break;
		case 3:
			typeStr = "加油";
			level = 4;
			break;
		}

		final int levelRank = level;

		new AlertDialog.Builder(this)
				.setMessage("这篇日记将被评为<<" + typeStr + ">>\n\n评价过后将无法修改，是否评价？")
				.setPositiveButton("评价", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						diaryAppraise(levelRank);
					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 发送请求评价日记
	 * 
	 * @param levelRank
	 */
	private void diaryAppraise(final int levelRank) {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_DIARY_APPRAISE);
		map.put("diaryId", diary.getId());
		map.put("level", String.valueOf(levelRank));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("日记评价返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							String resultStatus = jsonObject
									.optString("resultStatus");
							if ("200".equals(resultStatus)) {
								diary.setLevelRank(levelRank);// 更新数据
								appraiseSuccess = true;// 标识已经评价成功，返回上一个页面时需要实时刷新

								img_level.setVisibility(View.VISIBLE);
								btn_appraise.setVisibility(View.GONE);

								if (levelRank == 4) {
									img_level
											.setBackgroundResource(R.drawable.a_level_jiayou);
								} else if (levelRank == 2) {
									img_level
											.setBackgroundResource(R.drawable.a_level_liang);
								} else if (levelRank == 1) {
									img_level
											.setBackgroundResource(R.drawable.a_level_you);
								} else if (levelRank == 3) {
									img_level
											.setBackgroundResource(R.drawable.a_level_yue);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 发送请求分享博客
	 */
	private void diaryShare() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_DIARY_SHARE);
		map.put("diaryId", diary.getId());
		map.put("userId", user.getUserId());
		map.put("clazzId", classId);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("日记分享返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							String resultStatus = jsonObject
									.optString("resultStatus");
							if ("200".equals(resultStatus)) {
								MToast.show(DiaryDetailActivity.this,
										"已分享到班级博客", MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handleBack();
		}
		return true;
	}

	private void handleBack() {
		if (appraiseSuccess) {
			// 将评价的结果返回给上一个页面
			Intent intent = new Intent();
			intent.putExtra("levelRank", diary.getLevelRank());
			DiaryDetailActivity.this.setResult(RESULT_OK, intent);
			DiaryDetailActivity.this.finish();
		} else {
			DiaryDetailActivity.this.finish();
		}
	}

}
