package com.dt5000.ischool.activity.student;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.HomeworkAttachListActivity;
import com.dt5000.ischool.adapter.HomeworkCommentListAdapter;
import com.dt5000.ischool.entity.Homework;
import com.dt5000.ischool.entity.HomeworkAttach;
import com.dt5000.ischool.entity.HomeworkComment;
import com.dt5000.ischool.entity.HomeworkCommentPic;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

/**
 * 作业详情页面：学生端
 * 
 * @author 周锋
 * @date 2016年2月16日 下午1:53:03
 * @ClassInfo com.dt5000.ischool.activity.student.HomeworkDetailActivity
 * @Description
 */
public class HomeworkDetailActivity extends Activity {

	private Button btn_send_comment;
	private EditText edit_input_comment;
	private LinearLayout lLayout_back;
	private TextView txt_title;
	private ListView listview_comment;
	private LinearLayout lLayout_loading;
	private RelativeLayout rLayout_plus;
	private RelativeLayout rLayout_pic;
	private ImageView img_camera;
	private ImageView img_album;
	private ImageView img_small_pic;

	// HeadView控件
	private ImageView imgSubject;
	private TextView txtWorkName;
	private TextView txtCreateBy;
	private TextView txtCreateTime;
	private TextView txtContent;
	private TextView txtAttach;
	private RelativeLayout viewAttach;

	private Homework homework;
	private User user;
	private HomeworkCommentListAdapter homeworkCommentListAdapter;
	private List<HomeworkComment> commentList;
	private List<HomeworkAttach> homeworkAttachList;
	private FinalHttp finalHttp;
	private boolean showPlus = false;// 是否显示底部添加附件布局
	private String picPath;// 发送的图片路径
	private Bitmap picBitmap = null;// 发送的图片缩略图
	private File capture_file;// 拍摄的照片存储的文件
	private boolean isSending = false;// 是否正在发送请求
	private String token;// 上传图片时用到的Token
	private String resourceId;// 上传七牛返回的图片id
	private ProgressDialog progressDialog;
	private UploadManager uploadManager;
	private boolean isUploadCancelled = false;// 标识是否取消上传图片至七牛
	private String qiniuSmallUrl;// 上传图片至七牛返回的小图地址
	private String qiniuUrl;// 上传图片至七牛返回的大图地址
	private final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 100;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_detail);

		initView();
		initListener();
		init();
		getData();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		// 初始化View
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("作业详情");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		edit_input_comment = (EditText) findViewById(R.id.edit_input_comment);
		btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
		listview_comment = (ListView) findViewById(R.id.listview_comment);
		rLayout_plus = (RelativeLayout) findViewById(R.id.rLayout_plus);
		rLayout_pic = (RelativeLayout) findViewById(R.id.rLayout_pic);
		img_camera = (ImageView) findViewById(R.id.img_camera);
		img_album = (ImageView) findViewById(R.id.img_album);
		img_small_pic = (ImageView) findViewById(R.id.img_small_pic);

		// 添加HeadView
		View headView = LayoutInflater.from(this).inflate(
				R.layout.view_list_headview_homework_detail, null);
		imgSubject = (ImageView) headView.findViewById(R.id.imgsubject);
		txtWorkName = (TextView) headView.findViewById(R.id.txtworkname);
		txtCreateBy = (TextView) headView.findViewById(R.id.txtcreateby);
		txtCreateTime = (TextView) headView.findViewById(R.id.txtcreateTime);
		txtContent = (TextView) headView.findViewById(R.id.txtcontent);
		txtAttach = (TextView) headView.findViewById(R.id.txtattach);
		viewAttach = (RelativeLayout) headView.findViewById(R.id.viewAttach);
		listview_comment.addHeaderView(headView);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkDetailActivity.this.finish();
			}
		});

		// 点击复制作业详情
		txtContent.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(HomeworkDetailActivity.this).setItems(
						new String[] { "复制" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
								ClipData clipData = ClipData.newPlainText(
										"作业详情", txtContent.getText().toString()
												.trim());
								cm.setPrimaryClip(clipData);
								MToast.show(HomeworkDetailActivity.this, "已复制",
										MToast.SHORT);
							}
						}).show();

				return true;
			}
		});

		// 点击跳转到附件列表页面
		txtAttach.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeworkDetailActivity.this,
						HomeworkAttachListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("homeworkAttachs",
						(Serializable) homeworkAttachList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 点击显示或隐藏底部添加照片布局
		rLayout_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPlus = !showPlus;
				if (showPlus) {
					rLayout_pic.setVisibility(View.VISIBLE);
				} else {
					rLayout_pic.setVisibility(View.GONE);
				}
			}
		});

		// 点击拍照获取图片
		img_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(HomeworkDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
				{
					ActivityCompat.requestPermissions(HomeworkDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
				}else{
					getPicFromCamera();
				}
			}
		});

		// 点击从相册获取图片
		img_album.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPicFromAlbum();
			}
		});

		// 点击清除当前图片
		img_small_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(HomeworkDetailActivity.this)
						.setMessage("清除选中图片？")
						.setPositiveButton("清除",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										img_small_pic.setVisibility(View.GONE);
										img_small_pic.setImageBitmap(null);
										picPath = null;
										picBitmap.recycle();
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 点击发表评论
		btn_send_comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 七牛资源预先清空
				resourceId = "";
				qiniuUrl = "";
				qiniuSmallUrl = "";

				if (CheckUtil.stringIsBlank(picPath)) {
					// 检查输入
					if (CheckUtil.stringIsBlank(edit_input_comment.getText()
							.toString().trim())) {
						MToast.show(HomeworkDetailActivity.this, "请输入内容",
								MToast.SHORT);
					} else {
						// 隐藏键盘
						hideSoftInput(getCurrentFocus().getWindowToken());

						sendComment();
					}
				} else {
					// 隐藏键盘
					hideSoftInput(getCurrentFocus().getWindowToken());

					requestToGetToken();
				}
			}
		});
	}

	private void init() {
		homework = (Homework) getIntent().getExtras().getSerializable(
				"homework");

		finalHttp = new FinalHttp();

		commentList = new ArrayList<HomeworkComment>();

		user = User.getUser(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("加载中...");
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				isUploadCancelled = true;
			}
		});

		// 填充页面数据
		// 科目图标
		imgSubject.setImageResource(homework.getSubjectPicId());
		// 标题
		txtWorkName.setText(homework.getName());
		// 作者
		txtCreateBy.setText(homework.getCreateBy());
		// 时间
		txtCreateTime.setText(TimeUtil.yearMonthDayChineseFormat(homework
				.getCreateTime()));
		// 内容
		String content = homework.getContent();
		String newContent = content.replaceAll("(<[^>]+>)|(&nbsp;)", "");// 过滤html标签/(<[^>]+>)|(&nbsp;)/
		txtContent.setText(newContent);

		// 附件
		homeworkAttachList = homework.getHomeworkAttachs();
		if (homeworkAttachList != null && homeworkAttachList.size() > 0) {
			viewAttach.setVisibility(View.VISIBLE);
		} else {
			viewAttach.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取作业详情，这里只用到里面的评论数据
	 */
	private void getData() {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_HOMEWORK_DETAIL);
		map.put("homeworkId", String.valueOf(homework.getHomeworkId()));
		map.put("userId", user.getUserId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				HomeworkDetailActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("作业详情返回数据：" + t);
						try {
							// 取出Json串
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("homeworkItem");
							JSONObject obj2 = new JSONObject(result);
							String result2 = obj2
									.optString("homeworkCommentItems");

							// 解析实体类
							Type listType = new TypeToken<List<HomeworkComment>>() {
							}.getType();
							List<HomeworkComment> data = (List<HomeworkComment>) GsonUtil
									.jsonToList(result2, listType);

							// 判断返回的数据
							if (data != null && data.size() > 0) {
								commentList = data;
							}

							// 设置评论列表适配器
							homeworkCommentListAdapter = new HomeworkCommentListAdapter(
									HomeworkDetailActivity.this, commentList);
							listview_comment
									.setAdapter(homeworkCommentListAdapter);

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
	 * 上传图片时需要获取Token
	 */
	private void requestToGetToken() {
		// 封装参数
		Map<String, String> operationMap = new HashMap<String, String>();
		operationMap.put("operationType",
				UrlProtocol.OPERATION_TYPE_UPLOAD_PIC_TOKEN);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(operationMap, this,
				user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						MLog.i("上传图片前获取Token返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							String returnToken = jsonObject.optString("token");
							if (!CheckUtil.stringIsBlank(returnToken)) {
								token = returnToken;
								uploadToQiniu();
							} else {
								progressDialog.dismiss();
								DialogAlert.show(HomeworkDetailActivity.this,
										"服务器异常，请稍后再试");
							}
						} catch (Exception e) {
							e.printStackTrace();
							progressDialog.dismiss();
							DialogAlert.show(HomeworkDetailActivity.this,
									"服务器异常，请稍后再试");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
						DialogAlert.show(HomeworkDetailActivity.this,
								"服务器异常，请稍后再试");
					}
				});
	}

	/**
	 * 将图片上传至七牛
	 */
	private void uploadToQiniu() {
		if (uploadManager == null) {
			uploadManager = new UploadManager();
		}

		// 上传标识改变
		isUploadCancelled = false;

		uploadManager.put(new File(picPath), null, token,
				new UpCompletionHandler() {
					@Override
					public void complete(String key, ResponseInfo info,
										 JSONObject response) {
						if (info.isOK()) {
							resourceId = response.optString("resource");
							qiniuSmallUrl = response.optString("smallUrl");
							qiniuUrl = response.optString("url");
							MLog.i("七牛返回resourceId: " + resourceId);
							MLog.i("七牛返回大图地址: " + qiniuUrl);
							MLog.i("七牛返回小图地址: " + qiniuSmallUrl);

							// 发表评论
							sendComment();
						} else if (info.isCancelled()) {
							MLog.i("用户取消上传...");
						} else {
							progressDialog.dismiss();
							DialogAlert.show(HomeworkDetailActivity.this,
									"服务器异常，请稍后再试");
						}
					}
				}, new UploadOptions(null, null, false, null,
						new UpCancellationSignal() {
							@Override
							public boolean isCancelled() {
								return isUploadCancelled;
							}
						}));
	}

	/**
	 * 发表评论
	 */
	private void sendComment() {
		if (isSending) {
			MToast.show(HomeworkDetailActivity.this, "正在处理上一条请求，请稍后...",
					MToast.SHORT);
			return;
		}

		final String userComment = edit_input_comment.getText().toString();

		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType",
				UrlProtocol.OPERATION_TYPE_HOMEWORK_DETAIL_COMMENT);
		map.put("homeworkId", String.valueOf(homework.getHomeworkId()));
		map.put("role", String.valueOf(user.getRole()));
		map.put("userName", user.getRealName());
		map.put("content", userComment);
		map.put("isPublic", "0");// 不公开
		if (!CheckUtil.stringIsBlank(resourceId)) {
			map.put("resourceId", resourceId);
		}
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
				HomeworkDetailActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onStart() {
						// 标识线程开启
						isSending = true;

						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						MLog.i("发布作业评论返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String memo = obj.optString("memo");
							if ("".equals(memo)) {// 发表评论成功
								HomeworkComment comment = new HomeworkComment();
								comment.setHomeworkId(homework.getHomeworkId());
								comment.setContent(userComment);
								comment.setStid(user.getUserId());
								comment.setUserName(user.getRealName());
								comment.setUserRole(user.getRole());
								if (!CheckUtil.stringIsBlank(qiniuUrl)
										&& !CheckUtil
										.stringIsBlank(qiniuSmallUrl)) {
									comment.setImage(new HomeworkCommentPic(
											qiniuSmallUrl, qiniuUrl));
								}
								Timestamp commentDate = new Timestamp(
										new Date().getTime());
								comment.setPublishDate(commentDate.getTime());
								// 将新评论加入列表
								commentList.add(0, comment);

								// 刷新适配器
								homeworkCommentListAdapter
										.notifyDataSetChanged();

								// 输入框清空
								edit_input_comment.setText("");

								// 隐藏键盘
								hideSoftInput(getCurrentFocus()
										.getWindowToken());

								// 隐藏底部图片布局，并回收图片
								rLayout_pic.setVisibility(View.GONE);
								img_small_pic.setVisibility(View.GONE);
								img_small_pic.setImageBitmap(null);
								picPath = null;
								if (picBitmap != null) {
									picBitmap.recycle();
								}

								MToast.show(HomeworkDetailActivity.this,
										"留言成功", MToast.SHORT);
							} else {
								MToast.show(HomeworkDetailActivity.this,
										"留言失败", MToast.SHORT);
							}
						} catch (Exception e) {
							e.printStackTrace();
							MToast.show(HomeworkDetailActivity.this, "留言失败",
									MToast.SHORT);
						}

						// 请求标识置空闲
						isSending = false;

						// 隐藏加载进度条
						progressDialog.dismiss();
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
										  String strMsg) {
						MToast.show(HomeworkDetailActivity.this, "留言失败",
								MToast.SHORT);

						// 请求标识置空闲
						isSending = false;

						// 隐藏加载进度条
						progressDialog.dismiss();
					}
				});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getPicFromCamera();
			}else{
				MToast.show(HomeworkDetailActivity.this, "Permission Denied", MToast.SHORT);
			}
		}

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	private void getPicFromCamera() {
		try {
			File camera_file = new File(
					Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
			if (!camera_file.exists()) {
				camera_file.mkdirs();
			}
			capture_file = new File(camera_file,
					ImageUtil.getPhotoFileNameWithCurrentTime());
			capture_file.createNewFile();

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri imageUri;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				String authority = getPackageName() + ".provider";
				imageUri = FileProvider.getUriForFile(this, authority, capture_file);
			} else {
				imageUri = Uri.fromFile(capture_file);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getPicFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case FlagCode.ACTIVITY_REQUEST_CODE_0:// 拍照返回结果
				MLog.i("拍照返回结果：" + Uri.fromFile(capture_file));
				picPath = capture_file.getAbsolutePath();

				// 设置底部的缩略图
				picBitmap = ImageUtil.decodeBitmapToFixSize(picPath, 100, 100);
				img_small_pic.setImageBitmap(picBitmap);
				img_small_pic.setVisibility(View.VISIBLE);
				break;
			case FlagCode.ACTIVITY_REQUEST_CODE_1:// 相册返回结果
				if (data != null) {
					picPath = PictureUtil.getPath(HomeworkDetailActivity.this,
							data.getData());
					MLog.i("相册返回结果：" + data.getData());
					MLog.i("相册返回结果（处理后）：" + picPath);

					// 设置底部的缩略图
					picBitmap = ImageUtil.decodeBitmapToFixSize(picPath, 100,
							100);
					img_small_pic.setImageBitmap(picBitmap);
					img_small_pic.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
