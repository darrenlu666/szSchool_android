package com.dt5000.ischool.activity.teacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.imageadapter.GridSpacingItemDecor;
import com.dt5000.ischool.adapter.imageadapter.RecyclerBaseAdapter;
import com.dt5000.ischool.adapter.imageadapter.RecyclerViewBaseHolder;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.SubjectItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.PictureUtil;
import com.dt5000.ischool.utils.httpclient.HttpClientUtil;
import com.dt5000.ischool.widget.UISwitchButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布班级作业页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月14日 上午10:24:20
 * @ClassInfo com.dt5000.ischool.activity.teacher.HomeworkAddActivity
 * @Description
 */
public class HomeworkAddActivity extends Activity {

	private LinearLayout lLayout_back;
	private Button btn_publish;
	private TextView txt_title;
	private EditText edit_homework_name;
	private EditText edit_homework_content;
	private RelativeLayout rLayout_choose_class;
	private RelativeLayout rLayout_choose_subject;
	private TextView txt_class;
	private TextView txt_subject;
	private ImageView img_from_camera;
	private ImageView img_from_album;
	private ImageView img_thumbnail;
	private RecyclerView rcv_pic_list;
	private UISwitchButton uiswitch_sms;

	private String picPath;// 发送的图片路径
	private Bitmap picBitmap = null;// 发送的图片缩略图
	private File capture_file;// 拍摄的照片存储的文件
	private File capture_file_upload;// 上传的照片存储的文件
	private User user;
	private SubjectItem subjectItem;
	private List<ClassItem> classList;
	private ProgressDialog progressDialog;
	private final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 100;

	private static List<String> picPaths = new ArrayList<>();//选择的照片集合

	private RecyclerBaseAdapter mImagePreviewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_add);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("发布作业");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		btn_publish = (Button) findViewById(R.id.btn_publish);
		rLayout_choose_class = (RelativeLayout) findViewById(R.id.rLayout_choose_class);
		rLayout_choose_subject = (RelativeLayout) findViewById(R.id.rLayout_choose_subject);
		txt_class = (TextView) findViewById(R.id.txt_class);
		txt_subject = (TextView) findViewById(R.id.txt_subject);
		edit_homework_name = (EditText) findViewById(R.id.edit_homework_name);
		edit_homework_content = (EditText) findViewById(R.id.edit_homework_content);
		img_from_camera = (ImageView) findViewById(R.id.img_from_camera);
		img_from_album = (ImageView) findViewById(R.id.img_from_album);
		img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        initPicListRev();
		uiswitch_sms = (UISwitchButton) findViewById(R.id.uiswitch_sms);
		uiswitch_sms.setChecked(true);
	}

	private void initPicListRev(){
        rcv_pic_list = (RecyclerView)findViewById(R.id.rcv_pic_list);
        rcv_pic_list.setLayoutManager(new GridLayoutManager(this,3));
		rcv_pic_list.addItemDecoration(GridSpacingItemDecor.builder()
				.spanCount(3)
				.horizontalSpaceDp(this, 10)
				.verticalSpaceDp(this, 10)
				.includeVerticalEdge(true)
				.build());
		mImagePreviewAdapter = new RecyclerBaseAdapter<String>(this,R.layout.view_list_img_publish,picPaths){
			@Override
			public void onBindView(RecyclerViewBaseHolder holder, int position) {
				Bitmap bitmap = ImageUtil.decodeBitmapToFixSize(picPaths.get(position),100,100);
				((ImageView)holder.getView(R.id.iv_work_pic)).setImageBitmap(bitmap);
			}
		};
		rcv_pic_list.setAdapter(mImagePreviewAdapter);
        rcv_pic_list.setHasFixedSize(true);
        rcv_pic_list.setNestedScrollingEnabled(false);

    }

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeworkAddActivity.this.finish();
			}
		});

		// 点击发布作业
		btn_publish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publishHomework();
			}
		});

		// 点击进入选择班级页面
		rLayout_choose_class.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeworkAddActivity.this, HomeworkAddChooseClassActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("classList", (Serializable) classList);
				intent.putExtras(bundle);
				startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
			}
		});

		// 点击进入选择科目页面
		rLayout_choose_subject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent subjectIntent = new Intent(HomeworkAddActivity.this, HomeworkAddChooseSubjectActivity.class);
				startActivityForResult(subjectIntent, FlagCode.ACTIVITY_REQUEST_CODE_1);
			}
		});

		// 点击拍照获取图片
		img_from_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(HomeworkAddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(HomeworkAddActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
				}else{
					getPicFromCamera();
				}

			}
		});

		// 点击从相册获取图片
		img_from_album.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				//getPicFromAlbum();
				ImageSelectorUtils.openPhoto(HomeworkAddActivity.this, FlagCode.ACTIVITY_REQUEST_CODE_3, false, 9);
			}
		});

		// 点击清除当前图片
		img_thumbnail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(HomeworkAddActivity.this)
						.setMessage("清除选中图片？")
						.setPositiveButton("清除",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										img_thumbnail.setVisibility(View.GONE);
										img_thumbnail.setImageBitmap(null);
										picPath = null;
										picBitmap.recycle();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getPicFromCamera();
			}else{
				MToast.show(HomeworkAddActivity.this, "Permission Denied", MToast.SHORT);
			}
		}

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@SuppressWarnings("unchecked")
	private void init() {
		classList = (List<ClassItem>) getIntent().getExtras().getSerializable("classList");

		// 将所有班级设置为选中状态
		for (int i = 0; i < classList.size(); i++) {
			ClassItem classItem = classList.get(i);
			classItem.setChoose(false);
		}

		user = User.getUser(this);
	}

	private void publishHomework() {
		// 检查标题
		String title = edit_homework_name.getText().toString().trim();
		if (CheckUtil.stringIsBlank(title)) {
			DialogAlert.show(this, "请输入标题");
			return;
		}

		// 检查内容
		String content = edit_homework_content.getText().toString().trim();
		if (CheckUtil.stringIsBlank(content)) {
			DialogAlert.show(this, "请输入内容");
			return;
		}

		String paramClassIds = "";
		for (int i = 0; i < classList.size(); i++) {
			ClassItem classItem = classList.get(i);
			if (classItem.isChoose()) {
				paramClassIds += classItem.getClassId() + ",";
			}
		}
		paramClassIds = paramClassIds.substring(0, paramClassIds.lastIndexOf(","));
		MLog.i("班级id参数：" + paramClassIds);

		// 检查科目选择
		if (subjectItem == null) {
			DialogAlert.show(this, "请选择科目");
			return;
		}
		String paramSubjectId = subjectItem.getSubjectId();
		MLog.i("科目id参数：" + paramSubjectId);

		// 拼接作业json
		Map<String, String> map = new HashMap<String, String>();
		map.put("homeworkTitle", title);
		map.put("content", content);
		map.put("classStr", paramClassIds);
		map.put("subjectId", paramSubjectId);
		final String homeworkJson = new Gson().toJson(map);
		MLog.i("发布作业json: " + homeworkJson);

		// 弹出加载框
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("发布中，请稍后...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();

		// 开启线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				try {
//					if (!CheckUtil.stringIsBlank(picPath)) {
//						// 将上传图片保存为homework_attach_时间.jpg
//						capture_file_upload = new File(
//								FileUtil.getCameraDir(),
//								"homework_attach_" + ImageUtil.getPhotoFileNameWithCurrentTime());
//						capture_file_upload.createNewFile();
//
//						FileOutputStream fos = new FileOutputStream(capture_file_upload);
//						Bitmap decodeBitmap = ImageUtil.decodeBitmapWithThumbnailUtils(picPath, 2000);
//						// 压缩图片质量至80%
//						decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
//						fos.flush();
//						fos.close();
//
//						picPath = capture_file_upload.getAbsolutePath();
//					}

					List<File> imgFiles = new ArrayList<>(9);
					if(picPaths!=null && picPaths.size()>0){
						for(int i =0 ;i<picPaths.size();i++){
							// 将上传图片保存为homework_attach_时间.jpg
							capture_file_upload = new File(
									FileUtil.getCameraDir(),
									"homework_attach_" + ImageUtil.getPhotoFileNameWithCurrentTime());
							capture_file_upload.createNewFile();

							FileOutputStream fos = new FileOutputStream(capture_file_upload);
							Bitmap decodeBitmap = ImageUtil.decodeBitmapWithThumbnailUtils(picPaths.get(i), 2000);
							// 压缩图片质量至80%
							decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
							fos.flush();
							fos.close();

							picPath = capture_file_upload.getAbsolutePath();
							File file = new File(picPath);
							imgFiles.add(file);
						}
					}

					// 设置请求参数
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("operationType", UrlProtocol.OPERATION_TYPE_ADD_HOMEWORK_MULTIFILE);
					paramMap.put("jsonHomework", homeworkJson);
					paramMap.put("sendMsg", uiswitch_sms.isChecked() ? "true" : "false");
					paramMap.put("imageOldName", CheckUtil.stringIsBlank(picPath) ? "" : new File(picPath).getName());
					String httpURL = UrlBulider.getHttpURL(paramMap, HomeworkAddActivity.this, user.getUserId());
					MLog.i("发布作业地址：" + httpURL);

					// 发送请求
					String response = HttpClientUtil.doPostWithFiles(httpURL, imgFiles);

					JSONObject jsonObject = new JSONObject(response);
					boolean success = jsonObject.optBoolean("success");
					if (success) {
						message.what = FlagCode.SUCCESS;
					} else {
						message.what = FlagCode.FAIL;
					}
				} catch (Exception e) {
					message.what = FlagCode.FAIL;
					e.printStackTrace();
				}

				handler.sendMessage(message);
			}
		}).start();
	}

	private void getPicFromCamera() {
		try {
			// 拍照后将图片保存为homework_pic.jpg
			capture_file = new File(FileUtil.getCameraDir(), "homework_pic.jpg");
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
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//TODO 修改的部分
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getPicFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_3);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FlagCode.ACTIVITY_REQUEST_CODE_0:// 点击班级选择返回结果
			if (resultCode == RESULT_OK) {
				classList = (List<ClassItem>) data.getExtras().getSerializable("newClassList");
				MLog.i("选择班级返回结果...");
				for (int i = 0; i < classList.size(); i++) {
					ClassItem classItem = classList.get(i);
					MLog.i(classItem.getClassName() + "/id为"
							+ classItem.getClassId() + "---选中状态---"
							+ classItem.isChoose());
				}

				// 设置班级文字显示
				int count = 0;
				String classStr = "";
				for (int i = 0; i < classList.size(); i++) {
					ClassItem classItem = classList.get(i);
					if (classItem.isChoose()) {
						count++;
						classStr += classItem.getClassName() + "，";
					}
				}

				if (count == classList.size()) {
					classStr = "所有班级";
				} else {
					classStr = classStr.substring(0, classStr.lastIndexOf("，"));
				}
				txt_class.setText(classStr);
			}
			break;
		case FlagCode.ACTIVITY_REQUEST_CODE_1:// 选择科目选择返回结果
			if (resultCode == RESULT_OK) {
				subjectItem = (SubjectItem) data.getExtras().getSerializable("subjectItem");
				txt_subject.setText(subjectItem.getSubjectName());
			}
			break;
		case FlagCode.ACTIVITY_REQUEST_CODE_2:// 拍照返回结果
			MLog.i("拍照返回结果：" + Uri.fromFile(capture_file));
            picPaths.add(capture_file.getAbsolutePath());

			// 设置底部的缩略图
			picBitmap = ImageUtil.decodeBitmapToFixSize(picPath, 100, 100);
			img_thumbnail.setImageBitmap(picBitmap);
			img_thumbnail.setVisibility(View.VISIBLE);
			break;
		case FlagCode.ACTIVITY_REQUEST_CODE_3:// 相册返回结果
			if (data != null) {
                picPaths = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
				mImagePreviewAdapter.setData(picPaths);
//				picPath = PictureUtil.getPath(HomeworkAddActivity.this, data.getData());
//				MLog.i("相册返回结果：" + data.getData());
//				MLog.i("相册返回结果（处理后）：" + picPath);
//
//				// 设置底部的缩略图
//				picBitmap = ImageUtil.decodeBitmapToFixSize(picPath, 100, 100);
//				img_thumbnail.setImageBitmap(picBitmap);
//				img_thumbnail.setVisibility(View.VISIBLE);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FlagCode.SUCCESS:// 发布作业成功
				progressDialog.dismiss();

				// 发布作业成功后将上传的相关图片删除
				if (capture_file_upload != null && capture_file_upload.exists()) {
					capture_file_upload.delete();
				}
				if (capture_file != null && capture_file.exists()) {
					capture_file.delete();
				}

				HomeworkAddActivity.this.setResult(RESULT_OK);
				HomeworkAddActivity.this.finish();
				break;
			case FlagCode.FAIL:// 发布作业失败
				progressDialog.dismiss();
				MToast.show(HomeworkAddActivity.this, "作业发布失败", MToast.SHORT);
				break;
			}
		};
	};


}
