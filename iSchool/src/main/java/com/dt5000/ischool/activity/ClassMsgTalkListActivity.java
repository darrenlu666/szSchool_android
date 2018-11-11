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

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.bilibili.boxing_impl.ui.BoxingViewActivity;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.activity.MMSelectorActivity;
import com.dt5000.ischool.activity.media.activity.ToolbarActivity;
import com.dt5000.ischool.activity.media.bean.MMImageBean;
import com.dt5000.ischool.activity.media.fragment.MMImageAlbumFragment;
import com.dt5000.ischool.activity.media.fragment.MMVideoAlbumFragment;
import com.dt5000.ischool.activity.teacher.HomeworkAddActivity;
import com.dt5000.ischool.adapter.ClassMsgTalkListAdapter;
import com.dt5000.ischool.adapter.ImageSelectAdapter;
import com.dt5000.ischool.db.ClassMessageDBManager;
import com.dt5000.ischool.entity.ClassMessage;
import com.dt5000.ischool.entity.ClassMessageSend;
import com.dt5000.ischool.entity.ImageMessage;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.RetrofitService;
import com.dt5000.ischool.thread.SendClassMessageThread;
import com.dt5000.ischool.thread.SyncClassMessageThread;
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
 * 班级聊天列表页面
 *
 * @author 周锋
 * @date 2016年1月21日 上午11:41:05
 * @ClassInfo com.dt5000.ischool.activity.ClassMsgTalkListActivity
 * @Description
 */
public class ClassMsgTalkListActivity extends Activity {

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
    private ImageView img_video;
    private WrapHeightViewPager viewpager_emj;
    private CirclePageIndicator circlePageIndicator;

    //recyclerView
    private static RelativeLayout relativeImage;
    private RecyclerView recyclerView;
    protected static ImageSelectAdapter adapter;

    private List<ClassMessage> classMessageList = new ArrayList<ClassMessage>();
    private ClassMsgTalkListAdapter classMsgAdapter;
    private Integer firstMsgId;
    private Integer lastMsgId;
    private Integer PAGE_SIZE = 20;
    private static List<String> picPaths = new ArrayList<>();
    private File capture_file;// 拍摄的照片存储的文件
    private User user;
    private boolean showPlus = false;// 是否显示底部添加附件布局
    private boolean showEmj = false;// 是否显示底部表情布局
    private boolean isSending = false;// 是否正在发送请求
    private ProgressDialog progressDialog;
    private String classInfoID;
    private MyHandler handler = new MyHandler(this);
    private final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 100;
    private static final int REQUEST_SELECT_IMAGES = 1 << 4;
    private static final int REQUEST_SELECT_VIDEO = 1 <<3;
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";

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
        setContentView(R.layout.activity_class_msg_talk_list);

        initView();
        initListener();
        init();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        listview_msg = (PullToRefreshListView) findViewById(R.id.listview_msg);
        rLayout_plus = (RelativeLayout) findViewById(R.id.rLayout_plus);
        rLayout_face = (RelativeLayout) findViewById(R.id.rLayout_face);
        rLayout_emj = (RelativeLayout) findViewById(R.id.rLayout_emj);
        rLayout_pic = (RelativeLayout) findViewById(R.id.rLayout_pic);
        rLayout_sms = (RelativeLayout) findViewById(R.id.rLayout_sms);
        uiswitch_sms = (UISwitchButton) findViewById(R.id.uiswitch_sms);
        img_video = (ImageView)findViewById(R.id.img_video);
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
                ClassMsgTalkListActivity.this.finish();
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
                sendMsg();
            }
        });

        // 长按某条消息复制文字
        listview_msg.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                String[] items = new String[]{"复制"};

                ClassMessage message = classMessageList.get(position - 1);
                final String content = message.getContent();
                if (CheckUtil.stringIsBlank(content)) {
                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ClassMsgTalkListActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                cbm.setText(content);
                                MToast.show(ClassMsgTalkListActivity.this,
                                        "已复制到剪切板", MToast.SHORT);
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
                if (ContextCompat.checkSelfPermission(ClassMsgTalkListActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ClassMsgTalkListActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                } else {
                    getPicFromCamera();
                }
            }
        });

        // 点击从相册获取图片
        img_album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //getPicFromAlbum();
                /*Intent intent = new Intent(ClassMsgTalkListActivity.this, MMSelectorActivity.class);
                intent.putExtra("EXTRA_TYPE", "VIDEO");
                startActivityForResult(intent, REQUEST_SELECT_VIDEO);*/
                BoxingConfig singleImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG);
                Boxing.of(singleImgConfig).withIntent(ClassMsgTalkListActivity.this, BoxingActivity.class).start(ClassMsgTalkListActivity.this, REQUEST_SELECT_IMAGES);
            }
        });

        img_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassMsgTalkListActivity.this, MMSelectorActivity.class);
                intent.putExtra("EXTRA_SIZE",20);
                intent.putExtra("EXTRA_TYPE", "VIDEO");
                startActivityForResult(intent, REQUEST_SELECT_VIDEO);
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
                    //清空适配器
                    cleaner();
                }
            }
        });
    }

    private void sendMsg(){
        if (!isSending) {
            String content = edit_input.getText().toString().trim();

            if (!CheckUtil.stringIsBlank(content) || (picPaths != null && picPaths.size() > 0)) {
                if (!CheckUtil.stringIsBlank(content)
                        && content.length() > 500) {
                    MToast.show(ClassMsgTalkListActivity.this, "输入字数超出限制", MToast.SHORT);
                } else {
                    ClassMessageSend classMessageSend = new ClassMessageSend();
                    classMessageSend.setClassinfoId(classInfoID);
                    classMessageSend.setContent(content);
                    classMessageSend.setMessageType("1");

                    if (User.isStudentRole(user.getRole())) {
                        classMessageSend.setStudentId(user.getUserId());
                    } else {
                        classMessageSend.setTeacherId(user.getUserId());
                    }

                    // 检查发短信按钮
                    String sendSMS = uiswitch_sms.isChecked() ? "true" : "false";
                    // 标识线程开启
                    isSending = true;
                    boolean isHaveContent = true;
                    if (CheckUtil.stringIsBlank(content)) {
                        isHaveContent = false;
                    }

                    RetrofitService.postFiles(picPaths,
                            RetrofitService.postFilesMapClassMessageSend(classMessageSend, sendSMS, picPaths, isHaveContent),
                            ClassMsgTalkListActivity.this, user, isHaveContent, handler);
                }
            } else {
                MToast.show(ClassMsgTalkListActivity.this, "请输入内容", MToast.SHORT);
            }
        } else {
            MToast.show(ClassMsgTalkListActivity.this, "正在处理上一条消息，请稍后...", MToast.SHORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPicFromCamera();
            } else {
                MToast.show(ClassMsgTalkListActivity.this, "Permission Denied", MToast.SHORT);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init() {
        Intent intent = getIntent();
        String className = intent.getStringExtra("className");
        classInfoID = intent.getStringExtra("classInfoID");

        user = User.getUser(this);

        // 设置标题为班级名称
        txt_title.setText(className);

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
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView((View) object);
            }
        });

        circlePageIndicator.setViewPager(viewpager_emj);
    }

    /*private void getPicFromCamera(){
        Intent intent = new Intent(this, MMSelectorActivity.class);
        intent.putExtra("EXTRA_TYPE", "IMAGE");
        intent.putExtra("EXTRA_SIZE", 1);
        startActivityForResult(intent, REQUEST_SELECT_IMAGES);
    }*/


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

    private void getPicFromAlbum() {
        cleaner();
        ImageSelectorUtils.openPhoto(ClassMsgTalkListActivity.this, FlagCode.ACTIVITY_REQUEST_CODE_1, false, 9);
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
            ClassMessageSend classMessageSend = new ClassMessageSend();
            classMessageSend.setClassinfoId(classInfoID);
            classMessageSend.setContent(cds);
            if (User.isStudentRole(user.getRole())) {
                classMessageSend.setStudentId(user.getUserId());
            } else {
                classMessageSend.setTeacherId(user.getUserId());
            }

            // 检查发短信按钮
            String sendSMS = uiswitch_sms.isChecked() ? "true" : "false";

            // 标识线程开启
            isSending = true;

            // 开启线程发送信息
            new Thread(new SendClassMessageThread(handler, null,
                    ClassMsgTalkListActivity.this, user, classMessageSend,
                    sendSMS)).start();
        } else {
            MToast.show(ClassMsgTalkListActivity.this, "正在处理上次请求...", MToast.SHORT);
        }
    }

    class queryMsgThread extends Thread {
        private int code;

        public queryMsgThread(int code) {
            this.code = code;
        }

        @Override
        public void run() {
            List<ClassMessage> messageList = null;
            ClassMessageDBManager classMessageDBManager = null;
            Message handlerMessage = new Message();
            try {
                classMessageDBManager = new ClassMessageDBManager(ClassMsgTalkListActivity.this);
                if (code == FlagCode.CODE_0) {// 首次加载
                    messageList = classMessageDBManager.queryTop(PAGE_SIZE, user.getUserId(), classInfoID);
                    // 更改消息状态
                    // classMessageDBManager.updateStatus(user.getUserId());
                    classMessageDBManager.updateMsgStatus(user.getUserId(), classInfoID);
                    handlerMessage.what = FlagCode.CODE_0;
                } else if (code == FlagCode.CODE_1) {// 下拉获取更早的数据
                    messageList = classMessageDBManager.queryBefore(firstMsgId, user.getUserId(), classInfoID, PAGE_SIZE);
                    handlerMessage.what = FlagCode.CODE_1;
                } else if (code == FlagCode.CODE_2) {// 发送消息后再次查询最新数据
                    messageList = classMessageDBManager.queryAfter(lastMsgId, user.getUserId(), classInfoID);
                    handlerMessage.what = FlagCode.CODE_2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handlerMessage.obj = messageList;
                handler.sendMessage(handlerMessage);
                if (classMessageDBManager != null) {
                    classMessageDBManager.closeDB();
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
                case REQUEST_SELECT_VIDEO://视频
                    if(data!= null){
                        ArrayList<MMImageBean> mImageList = data.getParcelableArrayListExtra(EXTRA_DATA);
                        if(mImageList!=null && mImageList.size()>0){
                            picPaths.add(mImageList.get(0).getPath());
                            sendMsg();
                        }
                    }
                    break;
                case REQUEST_SELECT_IMAGES://图片
                    ArrayList<BaseMedia> mImageList = Boxing.getResult(data);
                    for (BaseMedia imageBean : mImageList) {
                        picPaths.add(imageBean.getPath());
                    }
                    //设置适配器
                    adapter.setImageMessages(picPaths);
                    //显示
                    relativeImage.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler {
        WeakReference<ClassMsgTalkListActivity> referActivity;

        MyHandler(ClassMsgTalkListActivity activity) {
            referActivity = new WeakReference<ClassMsgTalkListActivity>(
                    activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ClassMsgTalkListActivity classMsgTalkListActivity = referActivity
                    .get();
            switch (msg.what) {
                case FlagCode.CODE_0: {// 首次加载
                    List<ClassMessage> data = (List<ClassMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中第一条和最后一条消息的id
                        int size = data.size();
                        ClassMessage firstData = data.get(0);
                        ClassMessage lastData = data.get(size - 1);
                        if (firstData != null) {
                            classMsgTalkListActivity.firstMsgId = firstData.getClassMessageID();
                        }
                        if (lastData != null) {
                            classMsgTalkListActivity.lastMsgId = lastData.getClassMessageID();
                        }

                        // 将集合替换为新数据
                        classMsgTalkListActivity.classMessageList.clear();
                        classMsgTalkListActivity.classMessageList.addAll(data);

                        // 设置或更新适配器
                        if (classMsgTalkListActivity.classMsgAdapter == null) {
                            classMsgTalkListActivity.classMsgAdapter = new ClassMsgTalkListAdapter(
                                    classMsgTalkListActivity,
                                    classMsgTalkListActivity.classMessageList,
                                    classMsgTalkListActivity.user);
                            classMsgTalkListActivity.listview_msg.setAdapter(classMsgTalkListActivity.classMsgAdapter);
                        } else {
                            classMsgTalkListActivity.classMsgAdapter.notifyDataSetChanged();
                        }

                        // 将ListView显示在最后一个位置
                        classMsgTalkListActivity.listview_msg.setSelection(size - 1);
                    }
                    break;
                }
                case FlagCode.CODE_1: {// 下拉获取更早的数据
                    List<ClassMessage> data = (List<ClassMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中第一条消息的id
                        int size = data.size();
                        ClassMessage firstData = data.get(0);
                        if (firstData != null) {
                            classMsgTalkListActivity.firstMsgId = firstData
                                    .getClassMessageID();
                        }

                        // 将数据添加到列表头部
                        classMsgTalkListActivity.classMessageList.addAll(0, data);

                        // 更新适配器
                        classMsgTalkListActivity.classMsgAdapter
                                .notifyDataSetChanged();

                        // 下拉后保持ListView位置不变
                        classMsgTalkListActivity.listview_msg
                                .setSelection(size + 1);
                    }

                    classMsgTalkListActivity.listview_msg.onRefreshFinished();
                    break;
                }
                case FlagCode.SUCCESS: // 发送消息成功
                    // 改变底部
                    classMsgTalkListActivity.showPlus = false;
                    classMsgTalkListActivity.rLayout_pic.setVisibility(View.GONE);
                    classMsgTalkListActivity.edit_input.setText("");

                    cleaner();

                    // 隐藏键盘
                    classMsgTalkListActivity.hideSoftInput(classMsgTalkListActivity.getCurrentFocus().getWindowToken());

                    // 请求标识置空闲
                    classMsgTalkListActivity.isSending = false;

                    // 同步消息
                    new SyncClassMessageThread(
                            classMsgTalkListActivity.saveHandler,
                            classMsgTalkListActivity, classMsgTalkListActivity.user)
                            .start();

                    // 关闭加载进度条
                    classMsgTalkListActivity.progressDialog.dismiss();
                    break;
                case FlagCode.FAIL: {// 发送消息失败
                    // 如果有加载进度条则关闭
                    classMsgTalkListActivity.progressDialog.dismiss();

                    cleaner();

                    // 请求标识置空闲
                    classMsgTalkListActivity.isSending = false;

                    MToast.show(classMsgTalkListActivity, "发送失败，请稍后重试", MToast.SHORT);
                    break;
                }
                case FlagCode.CODE_2: {// 发送消息并且同步个人消息后，从本地数据库获取到了新插入的数据
                    List<ClassMessage> data = (List<ClassMessage>) msg.obj;
                    if (data != null && data.size() > 0) {
                        // 记录当前页面中最后一条消息的id
                        int size = data.size();
                        ClassMessage lastData = data.get(size - 1);
                        if (lastData != null) {
                            classMsgTalkListActivity.lastMsgId = lastData.getClassMessageID();
                        }

                        // 将数据添加到列表尾部
                        classMsgTalkListActivity.classMessageList.addAll(data);

                        // 设置或更新适配器
                        if (classMsgTalkListActivity.classMsgAdapter == null) {
                            classMsgTalkListActivity.classMsgAdapter = new ClassMsgTalkListAdapter(
                                    classMsgTalkListActivity,
                                    classMsgTalkListActivity.classMessageList,
                                    classMsgTalkListActivity.user);
                            classMsgTalkListActivity.listview_msg.setAdapter(classMsgTalkListActivity.classMsgAdapter);
                        } else {
                            classMsgTalkListActivity.classMsgAdapter.notifyDataSetChanged();
                        }

                        // 将ListView显示在最后一个位置
                        classMsgTalkListActivity.listview_msg.setSelection(classMsgTalkListActivity.classMessageList.size() - 1);
                    }
                    break;
                }
            }
        }
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
        // 标记当前所在的页面，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "ClassMsgTalk").commit();

        // 清除通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        if (classMsgAdapter != null) {
            user = User.getUser(this);
            classMsgAdapter.setUser(user);
            classMsgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 清除当前所在的页面标记，用于消息推送
        SharedPreferences sharedPreferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("currentActivity", "").commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(messageBroadcastReceiver);
        cleaner();
    }

}
