package com.dt5000.ischool.activity.student;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.student.StudyClassroomListAdapter;
import com.dt5000.ischool.entity.ClassroomVideo;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.SoapUtil;
import com.dt5000.ischool.utils.SubjectUtil;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;

/**
 * 自主学习同步课堂列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月25日 下午3:07:42
 * @ClassInfo com.dt5000.ischool.activity.student.StudyClassroomListActivity
 * @Description
 */
public class StudyClassroomListActivity extends Activity {

	private TextView txt_title;
	private TextView txt_grade;
	private LinearLayout lLayout_back;
	private ListView listview_data;
	private LoadMoreFooterView loadMoreFooterView;

	private int PAGE_SIZE = 20;
	private int PAGE_NO = 1;
	private List<ClassroomVideo> classroomVideoList;
	private StudyClassroomListAdapter studyClassroomListAdapter;
	private String subjectName;
	private String gradeName;
	private Integer gradeCode;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FlagCode.CODE_0: {
				classroomVideoList = (List<ClassroomVideo>) msg.obj;

				// 设置适配器
				studyClassroomListAdapter = new StudyClassroomListAdapter(
						StudyClassroomListActivity.this, classroomVideoList);
				listview_data.setAdapter(studyClassroomListAdapter);
				break;
			}
			case FlagCode.CODE_1: {
				List<ClassroomVideo> moreData = (List<ClassroomVideo>) msg.obj;

				if (moreData != null && moreData.size() > 0) {
					classroomVideoList.addAll(moreData);
					// 更新适配器
					studyClassroomListAdapter.notifyDataSetChanged();

					loadMoreFooterView.loadComplete();
				} else {
					loadMoreFooterView.noMore();
				}
				break;
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_classroom_list);

		initView();
		initListener();
		init();
		getData();
	}

	public void initView() {
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("同步课堂");
		txt_grade = (TextView) findViewById(R.id.txt_grade);
		listview_data = (ListView) findViewById(R.id.listview_data);

		// 添加FooterView
		loadMoreFooterView = new LoadMoreFooterView(
				StudyClassroomListActivity.this, new OnClickLoadMoreListener() {
					@Override
					public void onClickLoadMore() {
						getMoreData();
					}
				});
		listview_data.addFooterView(loadMoreFooterView.create());
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StudyClassroomListActivity.this.finish();
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		subjectName = intent.getStringExtra("subjectName");
		gradeCode = intent.getIntExtra("gradeCode", 1);
		gradeName = intent.getStringExtra("gradeName");

		txt_grade.setText(gradeName + "  " + subjectName);
	}

	private void getData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<ClassroomVideo> data = getCourseList(getCourseXml());
					if (data != null && data.size() > 0) {
						Message message = new Message();
						message.what = FlagCode.CODE_0;
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void getMoreData() {
		PAGE_NO++;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<ClassroomVideo> data = getCourseList(getCourseXml());
					Message message = new Message();
					message.what = FlagCode.CODE_1;
					message.obj = data;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 根据年级和科目代码来获取课程的xml
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getCourseXml() throws Exception {
		String xml = "";

		// 根据年级代码获取年级标识
		String gradeId = SubjectUtil.getGradeGuid(gradeCode);

		// 根据科目名称获取科目标识
		String subId = SubjectUtil.getSubjectGuid(subjectName);

		// 定义WebService命名空间和属性名称
		String NAMESPACE = "http://tempuri.org/";
		String METHOD_NAME = "getListById";
		String SOAP_ACTION = "http://tempuri.org/getListById";
		String PROPERTY_NAME = "getListByIdResult";

		// 封装参数
		SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
		soapObject.addProperty("gradeId", gradeId);
		soapObject.addProperty("subId", subId);
		soapObject.addProperty("pageElementCount", PAGE_SIZE);
		soapObject.addProperty("nextPageNum", PAGE_NO);

		// 发送Webservice请求
		SoapObject result = SoapUtil.getResultByWebService(soapObject,
				UrlProtocol.COURSE_VIDEOS, SOAP_ACTION);

		// 解析
		Object obj = (Object) result.getProperty(PROPERTY_NAME);
		xml = SoapUtil.convertSpecialChars(obj.toString());

		return xml;
	}

	/**
	 * 解析课程xml获取课程数据
	 * 
	 * @param xml
	 * @return
	 */
	private List<ClassroomVideo> getCourseList(String xml) {
		ByteArrayInputStream byteArrayInputStream = null;
		List<ClassroomVideo> dataList = null;
		try {
			// 解析返回的XML数据
			SAXReader saxReader = new SAXReader();
			byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());
			Document document = saxReader.read(byteArrayInputStream);
			Element root = document.getRootElement();

			// 判断状态码 
			String statusCode = root.elementTextTrim("statusCode");
			if ("200".equals(statusCode)) {
				// 遍历课程视频列表
				Element videosElement = root.element("videos");
				List<Element> videoElementList = videosElement.elements();

				if (videoElementList != null && videoElementList.size() > 0) {
					dataList = new ArrayList<ClassroomVideo>();

					for (int i = 0; i < videoElementList.size(); i++) {
						Element videoElement = videoElementList.get(i);
						String videoName = videoElement.elementTextTrim("name");
						String videoId = videoElement
								.elementTextTrim("videoId");
						String videoUrl = videoElement.elementTextTrim("rtmp");

						ClassroomVideo video = new ClassroomVideo();
						video.setVideoId(videoId);
						video.setVideoName(videoName);
						video.setVideoUrl(videoUrl);
						dataList.add(video);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (byteArrayInputStream != null) {
					byteArrayInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dataList;
	}

}
