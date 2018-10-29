package com.dt5000.ischool.activity.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.MassSendContactExpandListAdapter;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.FriendItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.thread.SendMassMessageThread;
import com.dt5000.ischool.thread.SyncPersonMessageThread;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.widget.UISwitchButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群发消息联系人列表页面：教师端
 * 
 * @author 周锋
 * @date 2016年1月29日 上午9:22:05
 * @ClassInfo com.dt5000.ischool.activity.teacher.MassSendContactListActivity
 * @Description
 */
public class MassSendContactListActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private EditText edit_input_comment;
	private ExpandableListView expand_listview;
	private TextView txt_symbol;
	private Button btn_send;
	private UISwitchButton uiswitch_sms;

	private List<ContactItem> contactItemList;
	private String massFlag;
	private MassSendContactExpandListAdapter massSendContactExpandListAdapter;
	private User user;
	private boolean massChecked = false;// 联系人默认是否全部选中
	private boolean isSending = false;
	private String symbol;// 通配符
	private MyHandler handler = new MyHandler(this);
	private ProgressDialog progressDialog;

	@SuppressLint("HandlerLeak")
	private Handler saveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mass_send_contact_list);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("群发消息");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		expand_listview = (ExpandableListView) findViewById(R.id.expand_listview);
		txt_symbol = (TextView) findViewById(R.id.txt_symbol);
		uiswitch_sms = (UISwitchButton) findViewById(R.id.uiswitch_sms);
		uiswitch_sms.setChecked(false);
		edit_input_comment = (EditText) findViewById(R.id.edit_input_comment);
		btn_send = (Button) findViewById(R.id.btn_send);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MassSendContactListActivity.this.finish();
			}
		});

		// 点击通配符
		txt_symbol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 将通配符名称拼接到输入文字后面
				String inputStr = edit_input_comment.getText().toString();
				String newInputStr = inputStr + symbol;
				edit_input_comment.setText(newInputStr);
				edit_input_comment.setSelection(newInputStr.length());
			}
		});

		// 点击发送
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSending) {
					String content = edit_input_comment.getText().toString();
					// 获取群发目标的id
					String receiveIds = getMassReceiveIds();
					if (!CheckUtil.stringIsBlank(content)) {
						if (content.length() > 240) {
							MToast.show(MassSendContactListActivity.this, "输入字数超出限制", MToast.SHORT);
						} else {
							if (CheckUtil.stringIsBlank(receiveIds)) {
								MToast.show(MassSendContactListActivity.this,
										"请选择发送对象", MToast.SHORT);
							} else {
								Map<String, String> map = new HashMap<String, String>();
								map.put("operationType", UrlProtocol.OPERATION_TYPE_SEND_MASS_MSG);
								map.put("content", content);// 参数：群发的内容
								map.put("sendName", user.getRealName());// 参数：群发的发送者名字
								String sendSMS = uiswitch_sms.isChecked() ? "true" : "false";
								map.put("sendMsg", sendSMS); // 参数：是否发短信按钮
								map.put("receiveIds", receiveIds);// 参数：群发目标的id，用逗号分隔
								map.put("messageType", "7");// 2018 1月8日更新 群发消息时需要传入messageType=7参数作为判断

								// 标识线程开启
								isSending = true;

								// 弹出加载框
								if (progressDialog == null) {
									progressDialog = new ProgressDialog(MassSendContactListActivity.this);
									progressDialog.setMessage("正在发送...");
								}
								progressDialog.show();

								// 开启线程
								new Thread(new SendMassMessageThread(handler,
										MassSendContactListActivity.this, user, map)).start();
							}
						}
					} else {
						MToast.show(MassSendContactListActivity.this, "请输入内容",
								MToast.SHORT);
					}
				} else {
					MToast.show(MassSendContactListActivity.this,
							"正在处理上一条消息，请稍后...", MToast.SHORT);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void init() {
		Bundle bundle = getIntent().getExtras();
		massFlag = bundle.getString("massFlag");
		List<ContactItem> contacts = (List<ContactItem>) bundle
				.getSerializable("contactItemList");

		user = User.getUser(this);

		// 将传递来的数据添加到集合中
		contactItemList = new ArrayList<ContactItem>();
		contactItemList.addAll(contacts);

		// 判断发送对象
		if ("student".equals(massFlag)) {// 群发学生
			symbol = "%*学生名*%";
			txt_symbol.setText("学生名");

			for (int i = 0; i < contacts.size(); i++) {
				ContactItem contactItem = contacts.get(i);
				if ("0".equals(contactItem.getType())
						|| "2".equals(contactItem.getType())
						|| "4".equals(contactItem.getType())
						|| "3".equals(contactItem.getType())) {
					// 去掉老师、班级、年级
					contactItemList.remove(contactItem);
				}
			}
		} else if ("teacher".equals(massFlag)) {// 群发教师
			symbol = "%*教师名*%";
			txt_symbol.setText("教师名");

			for (int i = 0; i < contacts.size(); i++) {
				ContactItem contactItem = contacts.get(i);
				if ("1".equals(contactItem.getType())
						|| "2".equals(contactItem.getType())
						|| "4".equals(contactItem.getType())
						|| "3".equals(contactItem.getType())) {
					// 去掉学生、班级、年级
					contactItemList.remove(contactItem);
				}
			}
		} else if ("grade".equals(massFlag)) {// 群发年级
			massChecked = true;
			symbol = "%*学生名*%";
			txt_symbol.setText("学生名");

			for (int i = 0; i < contacts.size(); i++) {
				ContactItem contactItem = contacts.get(i);
				// 去掉老师、学生、班级
				if ("0".equals(contactItem.getType())
						|| "1".equals(contactItem.getType())
						|| "4".equals(contactItem.getType())
						|| "2".equals(contactItem.getType())) {
					contactItemList.remove(contactItem);
				}
			}
		}

		// 设置适配器
		massSendContactExpandListAdapter = new MassSendContactExpandListAdapter(
				MassSendContactListActivity.this, contactItemList, massChecked);
		expand_listview.setAdapter(massSendContactExpandListAdapter);
	}

	/**
	 * 获取群发目标的id，格式以逗号隔开
	 * 
	 * @return
	 */
	private String getMassReceiveIds() {
		String receiveIds = "";
		List<String> receiveIdList = new ArrayList<String>();
		List<ContactItem> contactData = contactItemList;

		List<Boolean> groupCheckStatus = massSendContactExpandListAdapter
				.getGroupCheckStatus();
		SparseArray<List<Boolean>> chlidCheckStatus = massSendContactExpandListAdapter
				.getChlidCheckStatus();

		for (int i = 0; i < groupCheckStatus.size(); i++) {
			Boolean groupCheck = groupCheckStatus.get(i);
			if (groupCheck) {// 选中了某个组项
				ContactItem contactItem = contactData.get(i);
				List<FriendItem> friendList = contactItem.getFriendList();
				for (FriendItem friendItem : friendList) {
					String friendId = friendItem.getFriendId();
					receiveIdList.add(friendId);
				}
			} else {// 选中了部分子项
				List<Boolean> list = chlidCheckStatus.get(i);
				List<FriendItem> friendList = contactData.get(i)
						.getFriendList();
				for (int j = 0; j < list.size(); j++) {
					Boolean childCheck = list.get(j);
					if (childCheck) {
						FriendItem friendItem = friendList.get(j);
						receiveIdList.add(friendItem.getFriendId());
					}
				}
			}
		}

		if (receiveIdList.size() > 0) {
			// 将id数组转换为字符串，用逗号隔开
			for (String s : receiveIdList) {
				receiveIds = receiveIds + s + ",";
			}
		}

		MLog.i("群发消息receiveIds：" + receiveIds);
		return receiveIds;
	}

	static class MyHandler extends Handler {
		WeakReference<MassSendContactListActivity> referActivity;

		MyHandler(MassSendContactListActivity activity) {
			referActivity = new WeakReference<MassSendContactListActivity>(
					activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final MassSendContactListActivity msgDetailActivity = referActivity.get();
			switch (msg.what) {
			case FlagCode.SUCCESS: // 群发成功
				msgDetailActivity.edit_input_comment.setText("");

				// 关闭加载框
				msgDetailActivity.progressDialog.dismiss();

				// 隐藏键盘
				msgDetailActivity.hideSoftInput(msgDetailActivity.getCurrentFocus().getWindowToken());

				// 请求标识置空闲
				msgDetailActivity.isSending = false;

				// 同步消息
				new SyncPersonMessageThread(msgDetailActivity.saveHandler,
						msgDetailActivity, msgDetailActivity.user).start();

				// 弹出提示框
				new AlertDialog.Builder(msgDetailActivity)
						.setMessage("消息发送成功")
						.setPositiveButton("返回",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										msgDetailActivity.finish();
									}
								}).setNegativeButton("继续发送", null).show();
				break;
			case FlagCode.FAIL: // 群发失败
				// 关闭加载框
				msgDetailActivity.progressDialog.dismiss();

				// 请求标识置空闲
				msgDetailActivity.isSending = false;

				// 弹出提示框
				DialogAlert.show(msgDetailActivity, "发送失败，请稍后再试");
				break;
			}
		}
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
