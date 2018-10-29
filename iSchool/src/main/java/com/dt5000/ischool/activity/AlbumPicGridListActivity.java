package com.dt5000.ischool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.AlbumPicGridListAdapter;
import com.dt5000.ischool.entity.AlbumImageItem;
import com.dt5000.ischool.entity.AlbumItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级相册图片列表页面
 *
 * @author 周锋
 * @date 2016年2月3日 下午1:52:05
 * @ClassInfo com.dt5000.ischool.activity.AlbumPicGridListActivity
 * @Description
 */
public class AlbumPicGridListActivity extends Activity {

    private GridView grid_album;
    private TextView txt_title;
    private TextView txt_topbar_btn;
    private LinearLayout lLayout_back;

    private AlbumPicGridListAdapter albumPicGridListAdapter;
    private AlbumItem albumItem;
    private List<AlbumImageItem> albumImageList;
    private User user;
    private FinalHttp finalHttp;
    private int lastVisibleItem = 0;// 当前GridView最后一个可见项的索引
    private int PAGE_SIZE = 40;
    private int PAGE_NO = 0;
    private boolean hasMoreData = true;
    private boolean picChanged = false;

    private static final int REQUEST_CODE = 0x00000011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_pic_grid_list);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("班级图片");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("添加");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        grid_album = (GridView) findViewById(R.id.grid_album);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });

        // 点击进入图片浏览页面
        grid_album.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                bundle.putSerializable("albumImageList",
                        (Serializable) albumImageList);
                Intent intent = new Intent(AlbumPicGridListActivity.this,
                        AlbumPicPagerListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 点击添加图片
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				Intent intent = new Intent(AlbumPicGridListActivity.this, AlbumPicAddGridListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("albumItem", albumItem);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
            }
        });

        // 相册活动到底部加载更多
        grid_album.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE
                        && lastVisibleItem == (albumPicGridListAdapter.getCount() - 1)) {
                    if (hasMoreData) {
                        getMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    private void init() {
        albumItem = (AlbumItem) getIntent().getExtras().getSerializable("albumItem");

        user = User.getUser(this);

        finalHttp = new FinalHttp();

        albumImageList = new ArrayList<AlbumImageItem>();
    }

    /**
     * 获取图片列表数据
     */
    private void getData() {
        PAGE_NO = 0;// 页数置0

        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_IMAGE);
        map.put("albumId", String.valueOf(albumItem.getAlbumId()));
        map.put("pageNo", String.valueOf(PAGE_NO));
        map.put("pageSize", String.valueOf(PAGE_SIZE));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("图片列表返回结果：" + t);
                        try {
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("albumImageItems");

                            Type listType = new TypeToken<List<AlbumImageItem>>() {
                            }.getType();
                            List<AlbumImageItem> data = (List<AlbumImageItem>) GsonUtil
                                    .jsonToList(result, listType);

                            if (data != null && data.size() > 0) {
                                albumImageList = data;

                                // 设置适配器
                                albumPicGridListAdapter = new AlbumPicGridListAdapter(
                                        AlbumPicGridListActivity.this,
                                        albumImageList);
                                grid_album.setAdapter(albumPicGridListAdapter);
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
        map.put("operationType", UrlProtocol.OPERATION_TYPE_ALBUMS_IMAGE);
        map.put("albumId", String.valueOf(albumItem.getAlbumId()));
        map.put("pageNo", String.valueOf(PAGE_NO));
        map.put("pageSize", String.valueOf(PAGE_SIZE));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("更多图片列表返回结果：" + t);
                        try {
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("albumImageItems");

                            Type listType = new TypeToken<List<AlbumImageItem>>() {
                            }.getType();
                            List<AlbumImageItem> moreData = (List<AlbumImageItem>) GsonUtil
                                    .jsonToList(result, listType);

                            if (moreData != null && moreData.size() > 0) {
                                albumImageList.addAll(moreData);

                                // 设置适配器
                                albumPicGridListAdapter = new AlbumPicGridListAdapter(
                                        AlbumPicGridListActivity.this,
                                        albumImageList);
                                grid_album.setAdapter(albumPicGridListAdapter);

                                // 标识还有数据可以加载
                                hasMoreData = true;
                            } else {
                                // 标识没有数据可以加载
                                hasMoreData = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
        }
        return true;
    }

    /**
     * 处理返回事件
     */
    private void handleBack() {
        if (picChanged) {
            MLog.i("已经成功上传过图片，返回上一个页面需刷新数据");
            AlbumPicGridListActivity.this.setResult(RESULT_OK);
        }
        AlbumPicGridListActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:// 上传图片成功后返回本页面刷新数据，并设置图片上传成功的标识为true
                if (resultCode == RESULT_OK) {
                    picChanged = true;// 标识已经成功上传过图片
                    getData();
                }
                break;
//            case REQUEST_CODE:
//                ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
//
//                break;
        }
    }

}
