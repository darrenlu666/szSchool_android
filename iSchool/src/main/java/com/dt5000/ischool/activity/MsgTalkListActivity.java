package com.dt5000.ischool.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.ImageSelectAdapter;
import com.dt5000.ischool.adapter.MsgTalkListAdapter;
import com.dt5000.ischool.db.PersonMessageDBManager;
import com.dt5000.ischool.entity.ImageMessage;
import com.dt5000.ischool.entity.PersonMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.RetrofitService;
import com.dt5000.ischool.thread.SendPersonMessageThread;
import com.dt5000.ischool.thread.SyncPersonMessageThread;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.widget.PullToRefreshListView;
import com.dt5000.ischool.widget.PullToRefreshListView.OnRefreshListener;
import com.dt5000.ischool.widget.UISwitchButton;
import com.dt5000.ischool.widget.WrapHeightViewPager;
import com.dt5000.ischool.widget.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人聊天列表页面
 *
 * @author 周锋
 * @date 2016年1月21日 上午10:42:31
 * @ClassInfo com.dt5000.ischool.activity.MsgTalkListActivity
 * @Description
 */
public class MsgTalkListActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private PullToRefreshListView listview_msg;
    private RelativeLayout rLayout_plus;
    private RelativeLayout rLayout_pic;
    private RelativeLayout rLayout_face;
    private RelativeLayout rLayout_emj;
    private RelativeLayout rLayout_sms;
    private UISwitchButton uiswitch_sms;
    private EditText edit_input;
    private Button btn_send;
    private ImageView img_camera;
    private ImageView img_album;
    private WrapHeightViewPager viewpager_emj;
    private CirclePageIndicator circlePageIndicator;

    //recyclerView
    private static RelativeLayout relativeImage;
    private RecyclerView recyclerView;
    protected static ImageSelectAdapter adapter;

    private List<PersonMessage> personMessageList = new ArrayList<PersonMessage>();
    private MsgTalkListAdapter msgTalkListAdapter;
    private String friendId;
    private String friendName;
    private Integer firstMsgId = 0;
    private Integer lastMsgId = 0;
    private Integer PAGE_SIZE = 20;
    private static List<String> picPaths = new ArrayList<>();
    private File capture_file;// 拍摄的照片存储的文件
    private User user;
    private boolean showPlus = false;// 是否显示底部添加附件布局
    private boolean showEmj = false;// 是否显示底部表情布局
    private boolean isSending = false;// 是否正在发送请求
    private ProgressDialog progressDialog;
    private MyHandler handler = new MyHandler(this);
    private final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 100;
    @SuppressLint("HandlerLeak")
    private Handler saveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new queryMsgThread(FlagCode.CODE_2).start();
        }
    };

    // 动态广播接收消息推送
    private BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new queryMsgThread(FlagCode.CODE_0).start();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_talk_list);

        initView();
        initListener();
        init();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        listview_msg = (PullToRefreshListView) findViewById(R.id.listview_msg);
        rLayout_face = (RelativeLayout) findViewById(R.id.rLayout_face);
        rLayout_emj = (RelativeLayout) findViewById(R.id.rLayout_emj);
        rLayout_plus = (RelativeLayout) findViewById(R.id.rLayout_plus);
        rLayout_pic = (RelativeLayout) findViewById(R.id.rLayout_pic);
        rLayout_sms = (RelativeLayout) findViewById(R.id.rLayout_sms);
        uiswitch_sms = (UISwitchButton) findViewById(R.id.uiswitch_sms);
        uiswitch_sms.setChecked(false);
        edit_input = (EditText) findViewById(R.id.edit_input);
        btn_send = (Button) findViewById(R.id.btn_send);
        img_camera = (ImageView) findViewById(R.id.img_camera);
        img_album = (ImageView) findViewById(R.id.img_album);
        viewpager_emj = (WrapHeightViewPager) findViewById(R.id.viewpager_emj);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
        //recyclerView
        relativeImage = (RelativeLayout) findViewById(R.id.relativeImage);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageSelectAdapter(this);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgTalkListActivity.this.finish();
            }
        });

        // 点击编辑框时隐藏表情布局
        edit_input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmj = false;
                rLayout_emj.setVisibility(View.GONE);
            }
        });

        // 编辑框获取焦点时隐藏表情布局
        edit_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showEmj = false;
                    rLayout_emj.setVisibility(View.GONE);
                }
            }
        });

        // 点击显示或隐藏表情布局
        rLayout_face.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmj = !showEmj;
                if (showEmj) {
                    hideSoftInput(getCurrentFocus().getWindowToken());
                    rLayout_emj.setVisibility(View.VISIBLE);
                } else {
                    rLayout_emj.setVisibility(View.GONE);
                }
            }
        });

        // 下拉加载之前的消息
        listview_msg.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void toRefresh() {
                new queryMsgThread(FlagCode.CODE_1).start();
            }
        });

        // 点击发送消息
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSending) {
                    String content = edit_input.getText().toString().trim();

                    if (!CheckUtil.stringIsBlank(content)
                            || (picPaths != null && picPaths.size() > 0)) {
                        if (!CheckUtil.stringIsBlank(content) && content.length() > 500) {
                            MToast.show(MsgTalkListActivity.this, "输入字数超出限制", MToast.SHORT);
                        } else {
                            PersonMessage personMessage = new PersonMessage();
                            personMessage.setContent(content);
                            personMessage.setSenderId(user.getUserId());
                            personMessage.setSenderName(user.getRealName());
                            personMessage.setReceiverId(friendId);
                            personMessage.setReceiverName(friendName);
//                            personMessage.setMessageType("4");

                            // 检查发短信按钮
                            String sendSMS = uiswitch_sms.isChecked() ? "true" : "false";

                            // 标识线程开启
                            isSending = true;

                            boolean isHaveContent = true;
                            if (CheckUtil.stringIsBlank(content)) {
                                isHaveContent = false;
                            }

                            RetrofitService.postFiles(picPaths,
                                    RetrofitService.postFilesMapPersonMessage(personMessage, sendSMS, picPaths, isHaveContent),
                                    MsgTalkListActivity.this, user, isHaveContent, handler);
                        }
                    } else {
                        MToast.show(MsgTalkListActivity.this, "请输入内容", MToast.SHORT);
                    }
                } else {
                    MToast.show(MsgTalkListActivity.this, "正在处理上一条消息，请稍后...", MToast.SHORT);
                }
            }
        });

        // 长按某条消息复制文字
        listview_msg.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] items = new String[]{"复制"};

                PersonMessage message = personMessageList.get(position - 1);
                final String content = message.getContent();
                if (CheckUtil.stringIsBlank(content)) {
                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MsgTalkListActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                cbm.setText(content);
                                MToast.show(MsgTalkListActivity.this, "已复制到剪切板", MToast.SHORT);
                                break;
                        }
                    }
                });
                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                return true;
            }
        });

        // 点击拍照获取图片
        img_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MsgTalkListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MsgTalkListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                } else {
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

        // 点击显示或隐藏底部添加照片布局
        rLayout_plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlus = !showPlus;
                if (showPlus) {
                    rLayout_pic.setVisibility(View.VISIBLE);
                } else {
                    rLayout_pic.setVisibility(View.GONE);
                    cleaner();
                }
            }
        });
    }

    private void init() {
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendId");
        friendName = intent.getStringExtra("friendName");

        txt_title.setText(friendName);

        user = User.getUser(this);

        // 教师端有发送短信功能
        if (User.isTeacherRole(user.getRole())) {
            rLayout_sms.setVisibility(View.VISIBLE);
        } else {
            rLayout_sms.setVisibility(View.GONE);
        }

        // 初始化表情页
        initEmojiPager();

        // 注册广播监听消息推送
        IntentFilter filter = new IntentFilter("com.dt5000.ischool.action.message");
        registerReceiver(messageBroadcastReceiver, filter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        new queryMsgThread(FlagCode.CODE_0).start();
    }

    @SuppressLint("InflateParams")
    private void initEmojiPager() {
        LayoutInflater inflater = getLayoutInflater();
        View view_emoji_page1 = inflater.inflate(R.layout.view_emoji_page1, null);
        View view_emoji_page2 = inflater.inflate(R.layout.view_emoji_page2, null);
        View view_emoji_page3 = inflater.inflate(R.layout.view_emoji_page3, null);
        View view_emoji_page4 = inflater.inflate(R.layout.view_emoji_page4, null);
        View view_emoji_page5 = inflater.inflate(R.layout.view_emoji_page5, null);
        final List<View> emjViews = new ArrayList<View>();
        emjViews.add(view_emoji_page1);
        emjViews.add(view_emoji_page2);
        emjViews.add(view_emoji_page3);
        emjViews.add(view_emoji_page4);
        emjViews.add(view_emoji_page5);

        viewpager_emj.setAdapter(new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return emjViews.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = emjViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        circlePageIndicator.setViewPager(viewpager_emj);
    }

    private void getPicFromCamera() {
        try {
            cleaner();
            // 拍照后将图片保存为homework_pic.jpg
            capture_file = new File(FileUtil.getCameraDir(),
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPicFromCamera();
            } else {
                MToast.show(MsgTalkListActivity.this, "Permission Denied", MToast.SHORT);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPicFromAlbum() {
        cleaner();
        ImageSelectorUtils.openPhoto(MsgTalkListActivity.this, FlagCode.ACTIVITY_REQUEST_CODE_1, false, 9);
    }


    //清空数据
    public static void cleaner() {
        //清空适配器
        List<ImageMessage> imageMessages1 = adapter.getImageMessages();
        if (imageMessages1 != null) {
            imageMessages1.clear();
            adapter.notifyDataSetChanged();
        }
        picPaths.clear();
        relativeImage.setVisibility(View.GONE);
    }

    /**
     * 发送表情
     *
     * @param view
     */
    public void sendEmojiMsg(View view) {
        String cds = (String) view.getContentDescription();
        cds = "[" + cds + "]";
        if (!isSending) {
            PersonMessage personMessage = new PersonMessage();
            personMessage.setContent(cds);
            personMessage.setSenderId(user.getUserId());
            personMessage.setSenderName(user.getRealName());
            personMessage.setReceiverId(friendId);
            personMessage.setReceiverName(friendName);

            // 检查发短信按钮
            String sendSMS = uiswitch_sms.isChecked() ? "true" : "false";

            // 标识线程开启
            isSending = true;

            // 开启线程发送信息
            new Thread(new SendPersonMessageThread(MsgTalkListActivity.this,
                    user, handler, null, personMessage, sendSMS)).start();
        } else {
            MToast.show(MsgTalkListActivity.this, "正在处理上次请求...", MToast.SHORT);
        }
    }

    class queryMsgThread extends Thread {
        private int code;

        public queryMsgThread(int code) {
            this.code = code;
        }

        @Override
        public void run() {
            List<PersonMessage> messageList = null;
            PersonMessageDBManager personMessageDBManager = null;
            Message handlerMessage = new Message();
            try {
                personMessageDBManager = new PersonMessageDBManager(MsgTalkListActivity.this);
                if (code == FlagCode.CODE_0) {// 首次加载
                    messageList = personMessageDBManager.queryTop(friendId,
                            user.getUserId(), PAGE_SIZE, user.getUserId());
                    // 更改消息状态
                    personMessageDBManager.updateMsgStatus(user.getUserId(), friendId);
                    handlerMessage.what = FlagCode.CODE_0;
                } else if (code == FlagCode.CODE_1) {// 下拉获取更早的数据
                    messageList = personMessageDBManager.queryBefore(friendId,
                            user.getUserId(), PAGE_SIZE, firstMsgId, user.getUserId());
                    handlerMessage.what = FlagCode.CODE_1;
                } else if (code == FlagCode.CODE_2) {// 发送消息后再次查询最新数据
                    messageList = personMessageDBManager.queryAfter(friendId,
                            user.getUserId(), lastMsgId, user.getUserId());
                    handlerMessage.what = FlagCode.CODE_2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handlerMessage.obj = messageList;
                handler.sendMessage(handlerMessage);
                if (personMessageDBManager != null) {
                    personMessageDBManager.closeDB();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FlagCode.ACTIVITY_REQUEST_CODE_0:// 拍照返回结果
                    MLog.i("拍照返回结果：" + Uri.fromFile(capture_file));
                    picPaths.add(capture_file.getAbsolutePath());
                    //设置适配器
                    adapter.setImageMessages(picPaths);
                    //显示
                    relativeImage.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    break;
                case FlagCode.ACTIVITY_REQUEST_CODE_1:// 相册返回结果
                    if (data != null) {
                        picPaths = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                        //设置适配器
                        adapter.setImageMessages(picPaths);
                        //显示
                        relativeImage.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(adapter);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler {
        WeakReference<MsgTalkListActivity> referActivity;

        MyHandler(MsgTalkListActivity activity) {
            referActivity = new WeakReference<MsgTalkListActivity>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MsgTalkListActivity msgTalkListActivity = referActivity.get();
            switch (msg.what) {
                case FlagCode.CODE_0: {// 首次加载
                    List<PersonMessage> data = (List<PersonMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中第一条和最后一条消息的id
                        int size = data.size();
                        PersonMessage firstData = data.get(0);
                        PersonMessage lastData = data.get(size - 1);
                        if (firstData != null) {
                            msgTalkListActivity.firstMsgId = firstData.getMessageId();
                        }
                        if (lastData != null) {
                            msgTalkListActivity.lastMsgId = lastData.getMessageId();
                        }

                        // 将集合替换为新数据
                        msgTalkListActivity.personMessageList.clear();
                        msgTalkListActivity.personMessageList.addAll(data);

                        // 设置适配器
                        if (msgTalkListActivity.msgTalkListAdapter == null) {
                            msgTalkListActivity.msgTalkListAdapter = new MsgTalkListAdapter(
                                    msgTalkListActivity,
                                    msgTalkListActivity.personMessageList,
                                    msgTalkListActivity.user);
                            msgTalkListActivity.listview_msg.setAdapter(msgTalkListActivity.msgTalkListAdapter);
                        } else {
                            msgTalkListActivity.msgTalkListAdapter.notifyDataSetChanged();
                        }

                        // 将ListView显示在最后一个位置
                        msgTalkListActivity.listview_msg.setSelection(size - 1);
                    }
                    break;
                }
                case FlagCode.CODE_1: { // 下拉获取更早的数据
                    List<PersonMessage> data = (List<PersonMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中第一条消息的id
                        int size = data.size();
                        PersonMessage firstData = data.get(0);
                        if (firstData != null) {
                            msgTalkListActivity.firstMsgId = firstData
                                    .getMessageId();
                        }

                        // 将数据添加到列表头部
                        msgTalkListActivity.personMessageList.addAll(0, data);

                        // 更新适配器
                        msgTalkListActivity.msgTalkListAdapter
                                .notifyDataSetChanged();

                        // 下拉后保持ListView位置不变
                        msgTalkListActivity.listview_msg.setSelection(size + 1);
                    }

                    msgTalkListActivity.listview_msg.onRefreshFinished();
                    break;
                }
                case FlagCode.SUCCESS: {// 发送消息成功
                    // 改变底部
                    msgTalkListActivity.showPlus = false;
                    msgTalkListActivity.rLayout_pic.setVisibility(View.GONE);
                    msgTalkListActivity.edit_input.setText("");
                    //清空适配器
                    cleaner();
                    // 隐藏键盘
                    msgTalkListActivity.hideSoftInput(msgTalkListActivity.getCurrentFocus().getWindowToken());


                    // 请求标识置空闲
                    msgTalkListActivity.isSending = false;

                    // 删除会话后如果又有新会话消息，则改变配置文件
                    SharedPreferences sp = msgTalkListActivity
                            .getSharedPreferences("msg_pref", Context.MODE_PRIVATE);
                    // 在配置文件中以“userId--friendId”形式来唯一确定一条会话
                    String itemShowTag = msgTalkListActivity.user.getUserId() + "--" + msgTalkListActivity.friendId;
                    // 标识该会话条目显示在列表中
                    sp.edit().putBoolean(itemShowTag, true).commit();

                    // 同步消息
                    new SyncPersonMessageThread(msgTalkListActivity.saveHandler,
                            msgTalkListActivity, msgTalkListActivity.user).start();

                    // 关闭加载进度条
                    msgTalkListActivity.progressDialog.dismiss();
                    break;
                }
                case FlagCode.FAIL: { // 发送消息失败
                    // 如果有加载进度条则关闭
                    msgTalkListActivity.progressDialog.dismiss();
                    cleaner();
                    // 请求标识置空闲
                    msgTalkListActivity.isSending = false;

                    MToast.show(msgTalkListActivity, "发送失败，请稍后重试", MToast.SHORT);
                    break;
                }
                case FlagCode.CODE_2: { // 发送消息并且同步个人消息后，从本地数据库获取到了新插入的数据
                    List<PersonMessage> data = (List<PersonMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中最后一条消息的id
                        int size = data.size();
                        PersonMessage lastData = data.get(size - 1);
                        if (lastData != null) {
                            msgTalkListActivity.lastMsgId = lastData.getMessageId();
                        }

                        // 将数据添加到列表尾部
                        msgTalkListActivity.personMessageList.addAll(data);

                        // 设置或者更新适配器
                        if (msgTalkListActivity.msgTalkListAdapter == null) {
                            msgTalkListActivity.msgTalkListAdapter = new MsgTalkListAdapter(
                                    msgTalkListActivity,
                                    msgTalkListActivity.personMessageList,
                                    msgTalkListActivity.user);
                            msgTalkListActivity.listview_msg
                                    .setAdapter(msgTalkListActivity.msgTalkListAdapter);
                        } else {
                            msgTalkListActivity.msgTalkListAdapter
                                    .notifyDataSetChanged();
                        }

                        // 将ListView显示在最后一个位置
                        msgTalkListActivity.listview_msg
                                .setSelection(msgTalkListActivity.personMessageList
                                        .size() - 1);
                    }
                    break;
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        user = User.getUser(this);
        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "PersonMsgTalk").commit();

        // 清除通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        if (msgTalkListAdapter != null) {
            msgTalkListAdapter.setUser(user);
            msgTalkListAdapter.notifyDataSetChanged();
        }

        //通知已读
        notityReader(friendId, user.getUserId());
    }

    public void notityReader(String sendUserId, String receiveUserId) {
        RetrofitService.notityReader(this, sendUserId, receiveUserId, handler);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
    }

}
