package com.dt5000.ischool.activity.student;

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
import com.dt5000.ischool.adapter.student.VipPaySpeciesListAdapter;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.VipPaySpecies;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.GsonUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VIP套餐列表页面：学生端
 * 
 * @author 周锋
 * @date 2016年1月29日 下午7:34:52
 * @ClassInfo com.dt5000.ischool.activity.student.VipPaySpeciesListActivity
 * @Description
 */
public class VipPaySpeciesListActivity extends Activity {

	private TextView txt_title;
	private LinearLayout lLayout_back;
	private ListView listview_species;

	private List<VipPaySpecies> vipPaySpeciesList;
	private VipPaySpeciesListAdapter vipPaySpeciesListAdapter;
	private FinalHttp finalHttp;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_species_list);

		initView();
		initListener();
		init();
		getData();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("选择套餐");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		listview_species = (ListView) findViewById(R.id.listview_species);
	}

	private void initListener() {
		// 点击返回
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VipPaySpeciesListActivity.this.finish();
			}
		});

		// 点击判断是否已经支付过该套餐
		listview_species.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VipPaySpecies species = vipPaySpeciesList.get(position);

				MLog.i("点击的套餐id: " + species.getGoodsId() + "," +
						" 名称: " + species.getGoodsName() +
						", 价格: " + species.getPrice());

				judgeComboStatus(species);
			}
		});
	}

	private void init() {
		user = User.getUser(this);

		finalHttp = new FinalHttp();
	}

	private void getData() {
		// 封装参数
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("operationType", UrlProtocol.OPERATION_TYPE_VIP_PAY_CPMBO);
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(paramMap, VipPaySpeciesListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(String t) {
						MLog.i("VIP套餐返回结果：" + t);
						try {
							JSONObject obj = new JSONObject(t);
							String result = obj.optString("combo");

							List<VipPaySpecies> data = (List<VipPaySpecies>) GsonUtil
									.jsonToList(result,
											new TypeToken<List<VipPaySpecies>>() {
											}.getType());

							if (data != null && data.size() > 0) {
								vipPaySpeciesList = data;

								vipPaySpeciesListAdapter = new VipPaySpeciesListAdapter(VipPaySpeciesListActivity.this, vipPaySpeciesList);
								listview_species.setAdapter(vipPaySpeciesListAdapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 判断用户是否已经买过该套餐
	 * 
	 * @param species
	 */
	private void judgeComboStatus(final VipPaySpecies species) {
		// 封装参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("operationType", UrlProtocol.OPERATION_TYPE_VIP_CHECK_COMBO);
		map.put("goodsId", species.getGoodsId());
		AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, VipPaySpeciesListActivity.this, user.getUserId());

		// 发送请求
		finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						MLog.i("VIP套餐购买状态返回结果：" + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							boolean purchase = jsonObject.optBoolean("purchase");

							if (!purchase) {
								// 跳转到支付页面
								Intent intent = new Intent(VipPaySpeciesListActivity.this, VipPayMoneyActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable("species", species);
								intent.putExtras(bundle);
								startActivityForResult(intent, 1103);
							} else {
								DialogAlert.show(VipPaySpeciesListActivity.this, "您已购买过该套餐");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1103:
			if (resultCode == RESULT_OK) {
				VipPaySpeciesListActivity.this.setResult(RESULT_OK);
				VipPaySpeciesListActivity.this.finish();
			}
			break;
		}
	}

}
