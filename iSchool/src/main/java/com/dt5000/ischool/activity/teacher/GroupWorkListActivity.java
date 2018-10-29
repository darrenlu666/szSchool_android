package com.dt5000.ischool.activity.teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.teacher.HomeworkGroupListAdapter;
import com.dt5000.ischool.entity.GroupItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看作业前选择群组列表页面：教师端
 *
 * @author 周锋
 * @date 2016年1月13日 下午2:17:49
 * @ClassInfo com.dt5000.ischool.activity.teacher.GroupWorkListActivity
 * @Description
 */
public class GroupWorkListActivity extends Activity {

    private ListView listview_group;
    private LinearLayout lLayout_back;
    private TextView txt_topbar_btn;

    private User user;
    private List<GroupItem> groupList;
    private HomeworkGroupListAdapter homeworkGroupListAdapter;
    private FinalHttp finalHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_group_list);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        // 初始化View
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("选择群组");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("发布");
        listview_group = (ListView) findViewById(R.id.listview_group);
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupWorkListActivity.this.finish();
            }
        });

        // 点击进入发布作业页面
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupList != null && groupList.size() > 0) {
                    Intent intent = new Intent(GroupWorkListActivity.this,
                            GroupHomeworkAddActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("groupList",
                            (Serializable) groupList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                }
            }
        });

        // 点击进入作业列表页面
        listview_group.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                GroupItem groupItem = groupList.get(position);
                Intent intent = new Intent(GroupWorkListActivity.this,
                        GroupHomeworkListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("groupItem", groupItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        finalHttp = new FinalHttp();
    }

    private void getData() {
        // 配置参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_TEACHER_GROUP);
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this,
                user.getUserId());
        String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
        MLog.i("获取班级地址：" + httpURL);

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("groupItemList");

                            Type listType = new TypeToken<List<GroupItem>>() {
                            }.getType();
                            List<GroupItem> data = (List<GroupItem>) GsonUtil
                                    .jsonToList(result, listType);

                            if (data != null && data.size() > 0) {
                                groupList = data;
                                homeworkGroupListAdapter = new HomeworkGroupListAdapter(
                                        GroupWorkListActivity.this,
                                        groupList);
                                listview_group
                                        .setAdapter(homeworkGroupListAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    MToast.show(GroupWorkListActivity.this, "作业发布成功",
                            MToast.SHORT);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
