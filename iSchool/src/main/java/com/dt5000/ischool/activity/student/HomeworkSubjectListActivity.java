package com.dt5000.ischool.activity.student;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.teacher.HomeworkDetailActivity;
import com.dt5000.ischool.entity.Homework;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.SubjectUtil;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 单科作业列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月8日 下午4:43:39
 * @ClassInfo com.dt5000.ischool.activity.student.HworkSubjectActivity
 * @Description
 */
public class HomeworkSubjectListActivity extends Activity {

	private TextView txt_title;// 标题
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_loading;// 加载进度条布局
	private ListView listview_homework;// 作业列表
	private LoadMoreFooterView loadMoreFooterView;

	private User user;
	private int imgResId;// 科目图标资源id
	private List<Homework> homeworkList;// 单科作业列表数据
	private HomeworkSubjectListAdapter homeworkSubjectListAdapter;// 作业列表适配器
	private int subjectId;// 科目id
	private int PAGE_SIZE = 12;// 每次请求的页数
	private int PAGE_NO = 0;// 每次请求的页面
	private FinalHttp finalHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_subject_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		// 初始化View
		listview_homework = (ListView) findViewById(R.id.listview_homework);
		txt_title = (TextView) findViewById(R.id.txt_title);
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);

		// 添加FootView
		loadMoreFooterView = new LoadMoreFooterView(
				HomeworkSubjectListActivity.this,
				new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_homework.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkSubjectListActivity.this.finish();
			}
		});

		// 点击进入作业详情
		listview_homework.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= homeworkList.size()) {
					// 过滤底部加载更多位置的点击事件，防止数组越界
					return;
				}

				Homework homework = homeworkList.get(position);
				Intent intent = new Intent(HomeworkSubjectListActivity.this,
						HomeworkDetailActivity.class);
				intent.putExtra("homeworkId", homework.getHomeworkId());
				startActivity(intent);
			}
		});
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		subjectId = bundle.getInt("subjectId");

		finalHttp = new FinalHttp();

		homeworkList = new ArrayList<Homework>();

		user = User.getUser(HomeworkSubjectListActivity.this);

		SparseIntArray subjectImgs = SubjectUtil.getSubjectImgs();// 科目图标数组
		SparseArray<String> subjectNames = SubjectUtil.getSubjectNames();// 科目名称数组
		if (subjectId < 1 || subjectId > 15) {
			subjectId = 15;
		}
		imgResId = subjectImgs.get(subjectId);
		String subjectName = subjectNames.get(subjectId);
		txt_title.setText(subjectName + "作业");
	}

	/**
	 * 获取单科作业列表数据，首次加载页面会调用此方法
	 */
	private void getData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_HOMEWORK_SUBJECT_LIST);
		map.put("role", String.valueOf(user.getRole()));
		map.put("cid", user.getClassinfoId());
		map.put("subjectId", String.valueOf(subjectId));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("homeworkList");

							// 解析实体类
							Type listType = new TypeToken<List<Homework>>() {
							}.getType();
							List<Homework> data = (List<Homework>) GsonUtil
									.jsonToList(result, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								// 设置适配器
								homeworkList = data;
								homeworkSubjectListAdapter = new HomeworkSubjectListAdapter();
								listview_homework
										.setAdapter(homeworkSubjectListAdapter);
							}

							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
							// 隐藏加载进度条
							lLayout_loading.setVisibility(View.GONE);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// 隐藏加载进度条
						lLayout_loading.setVisibility(View.GONE);
					}
				});
	}

	/**
	 * 获取更多单科作业列表数据，点击ListView的FootView时会调用此方法
	 */
	private void getMoreData() {
		PAGE_NO++;// 页数加1

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_HOMEWORK_SUBJECT_LIST);
		map.put("role", String.valueOf(user.getRole()));
		map.put("cid", user.getClassinfoId());
		map.put("subjectId", String.valueOf(subjectId));
		map.put("pageNo", String.valueOf(PAGE_NO));
		map.put("pageSize", String.valueOf(PAGE_SIZE));
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("homeworkList");

							// 解析实体类
							Type listType = new TypeToken<List<Homework>>() {
							}.getType();
							List<Homework> moreDatas = new Gson().fromJson(
									result, listType);

							// 判断返回的数据
							if (moreDatas != null && moreDatas.size() > 0) {
								// 底部FootView状态改变
								loadMoreFooterView.loadComplete();

								// 更新适配器
								homeworkList.addAll(moreDatas);
								homeworkSubjectListAdapter
										.notifyDataSetChanged();
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

	/** 单科目作业列表适配器 */
	private class HomeworkSubjectListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return homeworkList == null ? 0 : homeworkList.size();
		}

		@Override
		public Object getItem(int position) {
			return homeworkList == null ? null : homeworkList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.view_list_item_homework_subject, null);
				holder.lLayout = (LinearLayout) convertView
						.findViewById(R.id.lLayout);
				holder.img_subject = (ImageView) convertView
						.findViewById(R.id.img_subject);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.txt_hw_content = (TextView) convertView
						.findViewById(R.id.txt_hw_content);
				holder.txt_hw_creator = (TextView) convertView
						.findViewById(R.id.txt_hw_creator);
				holder.txt_hw_name = (TextView) convertView
						.findViewById(R.id.txt_hw_name);
				holder.txt_hw_time_day = (TextView) convertView
						.findViewById(R.id.txt_hw_time_day);
				holder.txt_hw_time_hour = (TextView) convertView
						.findViewById(R.id.txt_hw_time_hour);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置科目图标并判断是否显示
			holder.img_subject.setImageResource(imgResId);
			if (position == 0) {
				holder.lLayout.setVisibility(View.VISIBLE);
			} else {
				holder.lLayout.setVisibility(View.GONE);
			}

			// 设置与科目颜色对应的item连接点背景颜色图片
			switch (subjectId) {
			case 1:
				holder.img.setBackgroundResource(R.drawable.shape_round_yuwen);
				break;
			case 2:
				holder.img.setBackgroundResource(R.drawable.shape_round_shuxue);
				break;
			case 3:
				holder.img.setBackgroundResource(R.drawable.shape_round_yingyu);
				break;
			case 4:
				holder.img.setBackgroundResource(R.drawable.shape_round_wuli);
				break;
			case 5:
				holder.img.setBackgroundResource(R.drawable.shape_round_huaxue);
				break;
			case 6:
				holder.img
						.setBackgroundResource(R.drawable.shape_round_shengwu);
				break;
			case 7:
				holder.img.setBackgroundResource(R.drawable.shape_round_lishi);
				break;
			case 8:
				holder.img.setBackgroundResource(R.drawable.shape_round_dili);
				break;
			case 9:
				holder.img
						.setBackgroundResource(R.drawable.shape_round_zhengzhi);
				break;
			case 10:
				holder.img.setBackgroundResource(R.drawable.shape_round_xinxi);
				break;
			case 11:
				holder.img.setBackgroundResource(R.drawable.shape_round_yinyue);
				break;
			case 12:
				holder.img.setBackgroundResource(R.drawable.shape_round_tiyu);
				break;
			case 13:
				holder.img.setBackgroundResource(R.drawable.shape_round_meishu);
				break;
			case 14:
				holder.img
						.setBackgroundResource(R.drawable.shape_round_shijian);
				break;
			case 15:
				holder.img.setBackgroundResource(R.drawable.shape_round_qita);
				break;
			default:
				holder.img.setBackgroundResource(R.drawable.shape_round_homew);
				break;
			}

			// 设置作业内容
			Homework hworkItem = homeworkList.get(position);
			// 作业名称
			holder.txt_hw_name.setText(hworkItem.getName());
			// 作业内容
			holder.txt_hw_content.setText(hworkItem.getSubContent());
			// 作业发布人
			holder.txt_hw_creator.setText(hworkItem.getCreateBy());
			// 作业发布时间
			holder.txt_hw_time_day.setText(TimeUtil
					.yearMonthDayFormat(hworkItem.getCreateTime()));
			holder.txt_hw_time_hour.setText(TimeUtil.hourMinuteFormat(hworkItem
					.getCreateTime()));

			return convertView;
		}

		class ViewHolder {
			TextView txt_hw_name;
			TextView txt_hw_content;
			TextView txt_hw_creator;
			TextView txt_hw_time_hour;
			TextView txt_hw_time_day;
			LinearLayout lLayout;
			ImageView img_subject;
			ImageView img;
		}
	}

}
