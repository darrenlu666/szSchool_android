package com.dt5000.ischool.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.BlogListAdapter;
import com.dt5000.ischool.adapter.BlogListAdapter.OnClickShareListener;
import com.dt5000.ischool.entity.Blog;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.FlagCode;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.LoadMoreFooterView;
import com.dt5000.ischool.widget.LoadMoreFooterView.OnClickLoadMoreListener;
import com.dt5000.ischool.widget.MyToast;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 博客列表页面
 *
 * @author 周锋
 * @date 2016年2月2日 下午4:06:46
 * @ClassInfo com.dt5000.ischool.activity.BlogListActivity
 * @Description
 */
public class BlogListActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_topbar_btn;
    private ListView listview_blog;
    private LoadMoreFooterView loadMoreFooterView;

    private User user;
    private String classId;
    private FinalHttp finalHttp;
    private int PAGE_SIZE = 12;
    private int PAGE_NO = 1;
    private List<Blog> blogList;
    private BlogListAdapter blogListAdapter;
    private boolean canCreateBlog = false;// 标识是否能创建博客
    private boolean canCommentBlog = false;// 表示是否能评论博客
    private Dialog shareDialog;
    private Blog shareBlog;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    private IWXAPI wxapi;
    private Tencent tencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("班级博客");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("新增");
        txt_topbar_btn.setVisibility(View.GONE);
        listview_blog = (ListView) findViewById(R.id.listview_blog);

        // 添加FooterView
        loadMoreFooterView = new LoadMoreFooterView(BlogListActivity.this,
                new OnClickLoadMoreListener() {
                    @Override
                    public void onClickLoadMore() {
                        getMoreData();
                    }
                });
        listview_blog.addFooterView(loadMoreFooterView.create());
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlogListActivity.this.finish();
            }
        });

        // 点击跳转到新增博客页面
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlogListActivity.this, BlogAddActivity.class);
                intent.putExtra("classId", classId);
                startActivityForResult(intent, FlagCode.ACTIVITY_REQUEST_CODE_0);
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ClassItem classItem = (ClassItem) bundle.getSerializable("classItem");
            classId = classItem.getClassId();
        } else {
            classId = user.getClassinfoId();
        }

        finalHttp = new FinalHttp();

        // 注册微信APP_ID
        wxapi = WXAPIFactory.createWXAPI(this, MCon.WX_APP_ID, true);
        wxapi.registerApp(MCon.WX_APP_ID);

        // 注册腾讯APP_ID
        if (tencent == null) {
            tencent = Tencent.createInstance(MCon.QQ_APP_ID, this);
        }

        shareDialog = createShareDialog();
    }

    private void getData() {
        PAGE_NO = 1;// 页数置1

        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_BLOG);
        map.put("pageSize", String.valueOf(PAGE_SIZE));
        map.put("pageNo", String.valueOf(PAGE_NO));
        map.put("clazzId", classId);
        map.put("role", String.valueOf(user.getRole()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());
        String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
        MLog.i("班级博客列表地址： " + httpURL);

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        try {
                            MLog.i("班级博客列表Json： " + t);
                            // 取出Json串
                            JSONObject obj = new JSONObject(t);

                            // 是否可创建博客的标识
                            int blogPublish = obj.optInt("blogPublish");
                            canCreateBlog = blogPublish == 0 ? true : false;
                            if (canCreateBlog) {
                                txt_topbar_btn.setVisibility(View.VISIBLE);
                            }

                            // 是否可评论博客的标识
                            int blogComment = obj.optInt("blogComment");
                            canCommentBlog = blogComment == 0 ? true : false;

                            // 博客数据
                            String result = obj.optString("blogList");

                            // 解析实体类
                            Type listType = new TypeToken<List<Blog>>() {}.getType();
                            List<Blog> data = (List<Blog>) GsonUtil.jsonToList(result, listType);

                            // 判断返回的数据
                            if (data != null && data.size() > 0) {
                                blogList = data;

                                // 构造适配器
                                blogListAdapter = new BlogListAdapter(
                                        BlogListActivity.this, blogList, user, canCommentBlog);
                                // 添加分享点击监听
                                blogListAdapter
                                        .setOnClickShareListener(new OnClickShareListener() {
                                            @Override
                                            public void toShare(Blog blog, int position) {
                                                shareBlog = blog;
                                                shareDialog.show();
                                            }
                                        });
                                // 设置适配器
                                listview_blog.setAdapter(blogListAdapter);

                                // 保存最新的博客id
                                saveBlogMaxId(data.get(0));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getMoreData() {
        PAGE_NO++;// 页数加1

        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_BLOG);
        map.put("pageSize", String.valueOf(PAGE_SIZE));
        map.put("pageNo", String.valueOf(PAGE_NO));
        map.put("clazzId", classId);
        map.put("role", String.valueOf(user.getRole()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());
        String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
        MLog.i("更多班级博客列表地址： " + httpURL);

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        try {
                            // 取出Json串
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("blogList");

                            // 解析实体类
                            Type listType = new TypeToken<List<Blog>>() {
                            }.getType();
                            List<Blog> moreData = (List<Blog>) GsonUtil
                                    .jsonToList(result, listType);

                            // 判断返回的数据
                            if (moreData != null && moreData.size() > 0) {
                                // 底部FootView状态改变
                                loadMoreFooterView.loadComplete();

                                blogList.addAll(moreData);

                                // 更新适配器
                                blogListAdapter.notifyDataSetChanged();
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

    // 将最新一条博客的id存入配置文件
    private void saveBlogMaxId(Blog blog) {
        SharedPreferences preferences = getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("blogMaxId", blog.getId());
        editor.commit();
    }

    @SuppressLint("InflateParams")
    private Dialog createShareDialog() {
        View view = getLayoutInflater().inflate(R.layout.view_share_dialog,
                null);
        view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
        TextView txt_share_cancel = (TextView) view
                .findViewById(R.id.txt_share_cancel);
        txt_share_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
        ImageView img_share_wx = (ImageView) view
                .findViewById(R.id.img_share_wx);
        img_share_wx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

                shareToWeiXin(SendMessageToWX.Req.WXSceneSession);
            }
        });
        ImageView img_share_pyq = (ImageView) view
                .findViewById(R.id.img_share_pyq);
        img_share_pyq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

                shareToWeiXin(SendMessageToWX.Req.WXSceneTimeline);
            }
        });
        ImageView img_share_qq = (ImageView) view
                .findViewById(R.id.img_share_qq);
        img_share_qq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

                shareToQQ();
            }
        });
        ImageView img_share_qzone = (ImageView) view
                .findViewById(R.id.img_share_qzone);
        img_share_qzone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

                shareToQzone();
            }
        });

        Dialog dialog = new Dialog(this, R.style.ShareDialogStyle);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        return dialog;
    }

    private void shareToWeiXin(final int sceneType) {
        ImageLoaderUtil.createSimple(this).loadImage(
                shareBlog.getShareImgUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        MLog.i("imageUri:" + imageUri);
                        progressDialog.dismiss();

                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = shareBlog.getShareUrl();

                        WXMediaMessage msg = new WXMediaMessage(webpageObject);
                        msg.thumbData = ImageUtil.bmpToByteArray(loadedImage,
                                false);
                        msg.description = shareBlog.getShareDescription();
                        if (sceneType == SendMessageToWX.Req.WXSceneSession) {
                            msg.title = shareBlog.getShareTitle();
                        } else {
                            msg.title = shareBlog.getShareTitle() + "\n"
                                    + shareBlog.getShareDescription();
                        }

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage"
                                + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = sceneType;

                        wxapi.sendReq(req);
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(
                                    BlogListActivity.this);
                            progressDialog.setMessage("正在加载...");
                        }
                        progressDialog.show();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void shareToQQ() {
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, shareBlog.getShareTitle());
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY,
                shareBlog.getShareDescription());
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
                shareBlog.getShareUrl());
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                shareBlog.getShareImgUrl());
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "苏州学堂");

        final Bundle finalBundle = bundle;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tencent.shareToQQ(BlogListActivity.this, finalBundle,
                        shareQQListener);
            }
        });
    }

    private void shareToQzone() {
        Bundle bundle = new Bundle();
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE,
                shareBlog.getShareTitle());
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,
                shareBlog.getShareDescription());
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
                shareBlog.getShareUrl());
        ArrayList<String> imgUrlList = new ArrayList<String>();
        imgUrlList.add(shareBlog.getShareImgUrl());
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);

        final Bundle finalBundle = bundle;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tencent.shareToQzone(BlogListActivity.this, finalBundle,
                        shareQzoneListener);
            }
        });
    }

    private IUiListener shareQQListener = new IUiListener() {
        @Override
        public void onCancel() {
            MyToast.makeToast(getApplicationContext(), "取消分享",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object response) {
            MyToast.makeToast(getApplicationContext(), "分享成功",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError e) {
            MLog.i("分享给QQ好友出现错误：" + e.errorMessage);
        }
    };

    private IUiListener shareQzoneListener = new IUiListener() {
        @Override
        public void onCancel() {
            MyToast.makeToast(getApplicationContext(), "取消分享",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object response) {
            MyToast.makeToast(getApplicationContext(), "分享成功",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError e) {
            MLog.i("分享到QQ空间出现错误：" + e.errorMessage);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FlagCode.ACTIVITY_REQUEST_CODE_0:// 新增博客后返回操作结果
                if (resultCode == RESULT_OK) {
                    PAGE_NO = 1;
                    getData();
                }
                break;
            case Constants.REQUEST_QQ_SHARE:// 分享给QQ好友后返回结果
                Tencent.onActivityResultData(requestCode, resultCode, data,
                        shareQQListener);
                break;
            case Constants.REQUEST_QZONE_SHARE:// 分享到QQ空间后返回结果
                Tencent.onActivityResultData(requestCode, resultCode, data,
                        shareQzoneListener);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tencent != null) {
            tencent.releaseResource();
        }
    }

}
