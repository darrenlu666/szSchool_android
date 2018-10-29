package com.dt5000.ischool.activity.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.CustomLoadMoreView;
import com.dt5000.ischool.R;
import com.dt5000.ischool.adapter.student.VipPayDatielAdapter;
import com.dt5000.ischool.entity.PayDetails;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.VipPaySpecies;
import com.dt5000.ischool.net.RetrofitService;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * VIP支付方式选择界面：学生端
 *
 * @author 周锋
 * @date 2016年1月29日 下午7:18:01
 * @ClassInfo com.dt5000.ischool.activity.student.VipPayChooseActivity
 * @Description
 */
public class VipPayChooseActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private String tag = VipPayChooseActivity.class.getSimpleName();
    //header
    private View headerView;
    private RelativeLayout rLayout_alipay;
    private RelativeLayout rLayout_wx;

    private TextView txt_title;
    private LinearLayout lLayout_back;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private VipPayDatielAdapter vipPayDatielAdapter;

    private User user;
    private int pageSize = 20;
    private int pageNum = 1;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_pay_detial);
        initView();
        init();
        initListener();
    }

    private void initView() {
        //header
        headerView = LayoutInflater.from(this).inflate(R.layout.activity_vip_pay_choose, null);
        rLayout_alipay = (RelativeLayout) headerView.findViewById(R.id.rLayout_alipay);
        rLayout_wx = (RelativeLayout) headerView.findViewById(R.id.rLayout_wx);

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("选择支付方式");
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VipPayChooseActivity.this.finish();
            }
        });

        // 点击进入VIP免责声明页面
        rLayout_alipay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VipPayChooseActivity.this, VipPayDisclaimerActivity.class);
                startActivityForResult(intent, 1101);
            }
        });

        rLayout_wx.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VipPayChooseActivity.this, WxPayActivity.class));
            }
        });

        vipPayDatielAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.btnPay:
                        List<PayDetails.OrdersBean> ordersBeanList = adapter.getData();
                        if (ordersBeanList != null && ordersBeanList.size() > 0) {
                            PayDetails.OrdersBean ordersBean = ordersBeanList.get(position);
                            if (ordersBean != null) {
                                Intent intent = new Intent(VipPayChooseActivity.this, VipPayMoneyActivity.class);
                                Bundle bundle = new Bundle();
                                VipPaySpecies species = new VipPaySpecies(ordersBean.getGoodsName(),
                                        String.valueOf(ordersBean.getGoodsId()), ordersBean.getPrice());
                                species.setFromDetails(true);//40704---0103144318-1164
                                Log.i("tag", "" + ordersBean.getOrderId() + "---" + ordersBean.getOrderNo());//40704---0103144318-1164  41087////0104092846-1449
                                bundle.putSerializable("species", species);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 1101);
                            }
                        }
                        break;
                }
            }
        });
    }

    private void init() {
        user = User.getUser(this);
        vipPayDatielAdapter = new VipPayDatielAdapter(null);
        vipPayDatielAdapter.addHeaderView(headerView);
        vipPayDatielAdapter.setOnLoadMoreListener(this, recyclerView);
        vipPayDatielAdapter.setLoadMoreView(new CustomLoadMoreView());
        recyclerView.setAdapter(vipPayDatielAdapter);
        onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1101:
                if (resultCode == RESULT_OK) {
                    VipPayChooseActivity.this.setResult(RESULT_OK);
                    VipPayChooseActivity.this.finish();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        vipPayDatielAdapter.setEnableLoadMore(false);
        swipeRefreshLayout.setRefreshing(true);
        payOrders(true);
    }


    @Override
    public void onLoadMoreRequested() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        pageNum++;
        payOrders(false);
    }


    public void payOrders(final boolean isRefresh) {
        subscription = RetrofitService.payOrder(this, user.getUserId(), pageSize, pageNum)
                .subscribe(new Subscriber<PayDetails>() {
                    @Override
                    public void onCompleted() {
                        Log.i(tag, "onCompleted");
                        if (isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                            vipPayDatielAdapter.setEnableLoadMore(true);
                        } else {
                            swipeRefreshLayout.setEnabled(true);
                            vipPayDatielAdapter.loadMoreComplete();
                            vipPayDatielAdapter.setEnableLoadMore(false);
                        }

                        subscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag, "onError");
                        if (isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                            vipPayDatielAdapter.setEnableLoadMore(true);
                        } else {
                            swipeRefreshLayout.setEnabled(true);
                            vipPayDatielAdapter.loadMoreFail();
                        }
                        subscription.unsubscribe();
                    }

                    @Override
                    public void onNext(PayDetails payDetails) {
                        if (vipPayDatielAdapter != null && payDetails.getOrders() != null) {
                            Log.i(tag, "size:" + payDetails.getOrders().size());
                            if (isRefresh) {
                                vipPayDatielAdapter.setNewData(payDetails.getOrders());
                                swipeRefreshLayout.setRefreshing(false);
                                vipPayDatielAdapter.setEnableLoadMore(true);
                            } else {
                                vipPayDatielAdapter.addData(payDetails.getOrders());
                                swipeRefreshLayout.setEnabled(true);
                                if (payDetails.getOrders().size() < pageSize) {
                                    vipPayDatielAdapter.loadMoreEnd();
                                    return;
                                }
                                vipPayDatielAdapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
