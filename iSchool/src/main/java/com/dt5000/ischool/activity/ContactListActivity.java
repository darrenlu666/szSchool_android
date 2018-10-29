package com.dt5000.ischool.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.teacher.MassSendContactListActivity;
import com.dt5000.ischool.adapter.ContactExpandListAdapter;
import com.dt5000.ischool.adapter.ContactSearchListAdapter;
import com.dt5000.ischool.entity.ContactItem;
import com.dt5000.ischool.entity.ContactSearchItem;
import com.dt5000.ischool.entity.FriendItem;
import com.dt5000.ischool.entity.GroupItem;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
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
 * 通讯录列表页面
 *
 * @author 周锋
 * @date 2016年1月28日 下午7:13:38
 * @ClassInfo com.dt5000.ischool.activity.ContactListActivity
 * @Description
 */
public class ContactListActivity extends Activity {

    private LinearLayout lLayout_back;
    private TextView txt_title;
    private TextView txt_topbar_btn;
    private ExpandableListView expand_listview;
    private ListView listview_contact_search;
    private EditText edit_search;
    private LinearLayout lLayout_loading;

    private List<ContactItem> contactItemList;
    private List<ContactSearchItem> contactSearchItemList;
    private ContactExpandListAdapter contactExpandListAdapter;
    private ContactSearchListAdapter contactSearchListAdapter;
    private User user;
    private String massFlag;// 群发标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initView();
        initListener();
        init();
        getData();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("通讯录");
        txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
        txt_topbar_btn.setText("群发");
        expand_listview = (ExpandableListView) findViewById(R.id.expand_listview);
        listview_contact_search = (ListView) findViewById(R.id.listview_contact_search);
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        edit_search = (EditText) findViewById(R.id.edit_search);
        lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactListActivity.this.finish();
            }
        });

        // 点击跳转到教师群发消息页面
        txt_topbar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactItemList == null || contactItemList.size() <= 0) {
                    return;
                }

                // 判断是否是年级组长
                boolean gradeMaster = false;
                for (ContactItem contactItem : contactItemList) {
                    if ("3".equals(contactItem.getType())) {
                        gradeMaster = true;
                        break;
                    }
                }

                // 年级组长显示群发年级
                String[] strs;
                if (gradeMaster) {
                    strs = new String[]{"群发学生", "群发老师", "群发年级"};
                } else {
                    strs = new String[]{"群发学生", "群发老师"};
                }

                ListAdapter adapter = new ArrayAdapter<String>(
                        ContactListActivity.this,
                        android.R.layout.simple_list_item_1, strs);

                new AlertDialog.Builder(ContactListActivity.this)
                        .setSingleChoiceItems(adapter, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        switch (which) {
                                            case 0: // 群发学生
                                                massFlag = "student";
                                                massSend(massFlag);
                                                break;
                                            case 1:// 群发老师
                                                massFlag = "teacher";
                                                massSend(massFlag);
                                                break;
                                            case 2:// 群发年级
                                                massFlag = "grade";
                                                massSend(massFlag);
                                                break;
                                        }
                                    }
                                }).show();
            }
        });

        // 点击ExpandableListView子项进入聊天页面
        expand_listview.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object child = contactExpandListAdapter.getChild(groupPosition, childPosition);

                if (child instanceof FriendItem) {
                    // 跳转到个人聊天页面
                    String friendName = ((FriendItem) child).getFriendName();
                    String friendId = ((FriendItem) child).getFriendId();
                    Intent intent;
                    if (friendId.equals(user.getUserId())) {
                        // 在通讯录中点击自己则进入个人中心页面
                        intent = new Intent(ContactListActivity.this, PersonalActivity.class);
                    } else {
                        intent = new Intent(ContactListActivity.this, MsgTalkListActivity.class);
                        intent.putExtra("friendId", friendId);
                        intent.putExtra("friendName", friendName);
                    }
                    startActivity(intent);
                } else if (child instanceof GroupItem) {
                    if (((GroupItem) child).getGroupType().equals("0")) {
                        // 跳转到班级聊天页面
                        String groupName = ((GroupItem) child).getGroupName();
                        String groupId = ((GroupItem) child).getGroupId();
                        Intent intent = new Intent(ContactListActivity.this, ClassMsgTalkListActivity.class);
                        intent.putExtra("classInfoID", groupId);
                        intent.putExtra("className", groupName);
                        startActivity(intent);
                    } else if (((GroupItem) child).getGroupType().equals("2")) {
                        String groupName = ((GroupItem) child).getGroupName();
                        String groupId = ((GroupItem) child).getGroupId();
                        Intent intent = new Intent(ContactListActivity.this, GroupMsgTalkListActivity.class);
                        intent.putExtra("groupInfoID", groupId);
                        intent.putExtra("groupName", groupName);
                        startActivity(intent);
                    }
                }

                return false;
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contactSearchItemList != null && contactSearchItemList.size() > 0) {
                    if (CheckUtil.stringIsBlank(s)) {
                        listview_contact_search.setVisibility(View.GONE);
                        expand_listview.setVisibility(View.VISIBLE);
                    } else {
                        listview_contact_search.setVisibility(View.VISIBLE);
                        expand_listview.setVisibility(View.GONE);

                        showSearchList(s.toString());
                    }
                }
            }
        });

        listview_contact_search.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactSearchItem item = (ContactSearchItem) contactSearchListAdapter.getItem(position);

                if ("0".equals(item.getType())) {// 联系人
                    // 跳转到个人聊天页面
                    String friendName = item.getName();
                    String friendId = item.getId();
                    Intent intent;
                    if (friendId.equals(user.getUserId())) {
                        // 在通讯录中点击自己则进入个人中心页面
                        intent = new Intent(ContactListActivity.this, PersonalActivity.class);
                    } else {
                        intent = new Intent(ContactListActivity.this, MsgTalkListActivity.class);
                        intent.putExtra("friendId", friendId);
                        intent.putExtra("friendName", friendName);
                    }
                    startActivity(intent);
                } else {// 联系人组
                    // 跳转到班级聊天页面
                    String groupName = item.getName();
                    String groupId = item.getId();
                    Intent intent = new Intent(ContactListActivity.this, ClassMsgTalkListActivity.class);
                    intent.putExtra("classInfoID", groupId);
                    intent.putExtra("className", groupName);
                    startActivity(intent);
                }
            }
        });
    }

    private void init() {
        user = User.getUser(this);

        if (User.isTeacherRole(user.getRole())) {
            // 教师端有群发按钮
            txt_topbar_btn.setVisibility(View.VISIBLE);
        } else {
            txt_topbar_btn.setVisibility(View.GONE);
        }

        contactItemList = new ArrayList<ContactItem>();
        contactSearchItemList = new ArrayList<ContactSearchItem>();
    }

    private void getData() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_CONTACT_LIST);
        map.put("cid", user.getClassinfoId());
        map.put("role", String.valueOf(user.getRole()));
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, this, user.getUserId());

        String httpURL = UrlBulider.getHttpURL(map, this, user.getUserId());
        MLog.i("联系人请求地址：" + httpURL);

        // 发送请求
        new FinalHttp().post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onStart() {
                        lLayout_loading.setVisibility(View.VISIBLE);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String t) {
                        MLog.i("联系人返回结果：" + t);
                        try {
                            JSONObject obj = new JSONObject(t);
                            String result = obj.optString("contactList");

                            Type listType = new TypeToken<List<ContactItem>>() {
                            }.getType();
                            List<ContactItem> data = (List<ContactItem>) GsonUtil.jsonToList(result, listType);

                            // 判断数据
                            if (data != null && data.size() > 0) {
                                contactItemList = data;

                                // 生成contactSearchItemList
                                for (ContactItem contactItem : contactItemList) {
                                    String type = contactItem.getType();
                                    if ("0".equals(type) || "1".equals(type)) {// 联系人
                                        for (FriendItem friendItem : contactItem.getFriendList()) {
                                            ContactSearchItem contactSearchItem = new ContactSearchItem();
                                            contactSearchItem.setType("0");
                                            contactSearchItem.setName(friendItem.getFriendName());
                                            contactSearchItem.setId(friendItem.getFriendId());
                                            contactSearchItem.setPhone(friendItem.getFriendPhone());
                                            contactSearchItem.setRole(friendItem.getFriendRole());
                                            contactSearchItemList.add(contactSearchItem);
                                        }
                                    } else {// 联系人组
                                        for (GroupItem groupItem : contactItem.getGroupList()) {
                                            ContactSearchItem contactSearchItem = new ContactSearchItem();
                                            contactSearchItem.setType("2");
                                            contactSearchItem.setName(groupItem.getGroupName());
                                            contactSearchItem.setId(groupItem.getGroupId());
                                            contactSearchItemList.add(contactSearchItem);
                                        }
                                    }
                                }

                                // 设置适配器
                                contactExpandListAdapter = new ContactExpandListAdapter(ContactListActivity.this, contactItemList);
                                expand_listview.setAdapter(contactExpandListAdapter);

                                // 默认展开第一组
                                expand_listview.expandGroup(0);
                            }

                            lLayout_loading.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            lLayout_loading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        lLayout_loading.setVisibility(View.GONE);
                    }
                });
    }

    private void showSearchList(String txt) {
        List<ContactSearchItem> newList = new ArrayList<ContactSearchItem>();

        for (int i = 0; i < contactSearchItemList.size(); i++) {
            ContactSearchItem contactSearchItem = contactSearchItemList.get(i);
            if (contactSearchItem.getName().contains(txt)) {
                newList.add(contactSearchItem);
            }
        }

        contactSearchListAdapter = new ContactSearchListAdapter(this, newList, txt);
        listview_contact_search.setAdapter(contactSearchListAdapter);
    }

    private void massSend(String flag) {
        Intent intent = new Intent(ContactListActivity.this, MassSendContactListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("massFlag", flag);
        bundle.putSerializable("contactItemList", (Serializable) contactItemList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
