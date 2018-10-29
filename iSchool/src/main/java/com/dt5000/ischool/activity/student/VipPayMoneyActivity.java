package com.dt5000.ischool.activity.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.entity.VipPaySpecies;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.utils.alipay.Keys;
import com.dt5000.ischool.utils.alipay.Result;
import com.dt5000.ischool.utils.alipay.Rsa;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * VIP支付页面：学生端
 *
 * @author 周锋
 * @date 2016年1月30日 下午1:30:33
 * @ClassInfo com.dt5000.ischool.activity.student.VipPayMoneyActivity
 * @Description
 */
public class VipPayMoneyActivity extends Activity {

    private TextView txt_title;
    private LinearLayout lLayout_back;
    private TextView txt_pay_species;
    private TextView txt_money;
    private Button btn_pay;

    private static final int RQF_PAY = 0x121;
    private VipPaySpecies species;
    private FinalHttp finalHttp;
    private ProgressDialog progressDialog;
    private User user;
    private String orderId = "";// 订单号

    /**
     * 支付宝合作商户网站唯一订单号，客户端在支付前生成订单的时候由商户后台生成并传给客户端，然后客户端将该号作为支付宝接口的参数传给支付宝，
     * 支付成功后支付宝再通过商户的notify_url通知商户后台，至此客户端生成的订单在商户后台生效
     */
    private String outTradeNo;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RQF_PAY:// 处理支付结果
                    Result result = new Result((String) msg.obj);
                    result.parseResult();
                    if (result.isSucc()) {
                        notifyBg();
                    } else {
                        // 如果支付未成功或者支付取消，则取消本页面，防止用户重复支付（支付宝网页支付完成后没有完成按钮，导致支付完成后无法正常完成而只能使用返回键到前面的页面，支付宝BUG）
                        VipPayMoneyActivity.this.setResult(RESULT_OK);
                        VipPayMoneyActivity.this.finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_money);

        initView();
        initListener();
        init();
    }

    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText("确认付款");
        txt_pay_species = (TextView) findViewById(R.id.txt_pay_species);
        txt_money = (TextView) findViewById(R.id.txt_money);
        btn_pay = (Button) findViewById(R.id.btn_pay);
    }

    private void initListener() {
        // 点击返回
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VipPayMoneyActivity.this.finish();
            }
        });

        // 点击付款
        btn_pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (species.isFromDetails()) {
                    orderId = species.getOrderId();
                    outTradeNo = species.getOrderNo();
                    toPay();
                    return;
                }
                createOrder();
            }
        });
    }

    private void init() {
        species = (VipPaySpecies) getIntent().getExtras().getSerializable("species");

        txt_pay_species.setText(species.getGoodsName());
        txt_money.setText("￥" + species.getPrice());

        user = User.getUser(this);

        finalHttp = new FinalHttp();

        progressDialog = new ProgressDialog(VipPayMoneyActivity.this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
    }

    /**
     * 在苏州学堂后台生成订单
     */
    private void createOrder() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_VIP_PAY_ORDER);
        map.put("goodsId", species.getGoodsId());
        map.put("phone", "");
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, VipPayMoneyActivity.this, user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        MLog.i("生成订单结果：" + t);
                        try {
                            JSONObject obj = new JSONObject(t);
                            String oid = obj.optString("orderId");
                            // 生成订单后后台传回outTradeNo用于和支付宝的交互
                            String orderNo = obj.optString("orderNo");
                            if (!CheckUtil.stringIsBlank(oid) && !CheckUtil.stringIsBlank(orderNo)) {
                                orderId = oid;
                                outTradeNo = orderNo;
                                progressDialog.dismiss();

                                // 开启支付宝
                                toPay();
                            } else {
                                progressDialog.dismiss();
                                MToast.show(VipPayMoneyActivity.this, "目前无法生成订单", MToast.SHORT);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            MToast.show(VipPayMoneyActivity.this, "目前无法生成订单", MToast.SHORT);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        progressDialog.dismiss();
                        MToast.show(VipPayMoneyActivity.this, "目前无法生成订单", MToast.SHORT);
                    }
                });
    }

    private void toPay() {
        // 获取初步info
        String info = getNewOrderInfo(species.getPrice());
        // String info = getNewOrderInfo("0.02");// 测试2分钱

        // 对订单参数数据进行签名，为保证安全该步骤最好放在商户服务器进行
        String sign = Rsa.sign(info, Keys.PRIVATE);
        try {
            // 参数sign不可空，并且必须进行URLEncoder
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 组装完orderInfo
        info += "&sign=\"" + sign + "\"&" + getSignType();

        MLog.i("start pay...");
        MLog.i("info: " + info);

        final String orderInfo = info;
        new Thread() {
            public void run() {
                PayTask payTask = new PayTask(VipPayMoneyActivity.this);
                String result = payTask.pay(orderInfo, true);
//				AliPay alipay = new AliPay(VipPayMoneyActivity.this, mHandler);
//				String result = alipay.pay(orderInfo);
                MLog.i("result: " + result);
                Message msg = new Message();
                msg.what = RQF_PAY;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 设置订单参数
     *
     * @param price 该笔订单的金额，单位为RMB-Yuan，取值范围为[0.01,100000000.00]，精确到小数点后两位
     * @return
     */
    @SuppressWarnings("deprecation")
    private String getNewOrderInfo(String price) {
        StringBuilder sb = new StringBuilder();

        // 参数partner不可空:商户签约的支付宝账号对应的支付宝唯一用户号，以2088开头的16位纯数字组成
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);

        // 参数out_trade_no不可空:支付宝合作商户网站唯一订单号
        sb.append("\"&out_trade_no=\"");
        sb.append(outTradeNo);

        // 参数subject不可空:商品的标题、交易标题、订单标题、订单关键字等，该参数最长为128个汉字
        sb.append("\"&subject=\"");
        sb.append("苏州学堂VIP会员支付（安卓端）");

        // 参数body可空:对一笔交易的具体描述信息，如果是多种商品，请将商品描述字符串累加传给body
        sb.append("\"&body=\"");
        sb.append("苏州学堂VIP套餐（安卓端）：" + species.getGoodsName());

        // 参数total_fee不可空:该笔订单的资金总额，单位为RMB-Yuan，取值范围为[0.01,100000000.00]，精确到小数点后两位
        sb.append("\"&total_fee=\"");
        sb.append(price);

        // 参数notify_url不可空:支付宝服务器主动通知商户网站里指定的页面http路径，网址需要做URL编码
        sb.append("\"&notify_url=\"");
        // 通知参数中包含了outTradeNo，然后后台根据outTradeNo使对应的订单生效
        sb.append(URLEncoder.encode(UrlProtocol.HOMEWORK_HOST
                + "android/wandroidPay-saveNotify.jhtml"));

        // 参数service不可空:接口名称(固定值)
        sb.append("\"&service=\"mobile.securitypay.pay");

        // 参数_input_charset不可空:编码格式，固定值
        sb.append("\"&_input_charset=\"UTF-8");

        // 参数return_url可空:支付宝处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径，网址需要做URL编码
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://www.szdiantong.com"));

        // 参数payment_type不可空:默认值为1（商品购买）
        sb.append("\"&payment_type=\"1");

        // 参数seller_id不可空:卖家支付宝账号对应的支付宝唯一用户号，以2088开头的纯16位数字
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);

        // 参数show_url可空:收银台页面上，商品展示的超链接，如果为空，这个参数字段则不传
        // sb.append("\"&show_url=\"");

        // 参数it_b_pay可空:设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭；
        // 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0 点关闭）；
        // 该参数数值不接受小数点，如1.5h，可转换为90m。该功能需要联系支付宝配置关闭时间。
        sb.append("\"&it_b_pay=\"30m");
        sb.append("\"");

        return new String(sb);
    }

    /**
     * 支付宝合作商户网站唯一订单号，目前不采用客户端生成，改为由后台生成并在支付前生成商户自己订单的时候传给客户端
     *
     * @return
     */
    @SuppressWarnings("unused")
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.CHINA);
        Date date = new Date();
        String key = format.format(date);

        java.util.Random r = new java.util.Random();
        key += r.nextInt();
        key = key.substring(0, 15);

        return key;
    }

    /**
     * 签名类型
     *
     * @return
     */
    private String getSignType() {
        // 参数sign_type不可空:签名类型，目前仅支持RSA
        return "sign_type=\"RSA\"";
    }

    /**
     * 支付成功后通知后台使订单生效
     */
    private void notifyBg() {
        // 封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("operationType", UrlProtocol.OPERATION_TYPE_VIP_UPDATE_ORDER);
        map.put("orderId", orderId);
        map.put("status", "1");
        AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
                VipPayMoneyActivity.this, user.getUserId());

        // 发送请求
        finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
                new AjaxCallBack<String>() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        MLog.i("通知后台使订单生效结果：" + t);
                        try {
                            JSONObject obj = new JSONObject(t);
                            String orderStatus = obj.optString("orderStatus");
                            if ("1".equals(orderStatus)) {// 成功
                                MToast.show(VipPayMoneyActivity.this, "VIP购买成功", MToast.SHORT);
                                dealSuccessOrder();
                            } else {// 失败
                                MToast.show(VipPayMoneyActivity.this, "订单异常", MToast.SHORT);
                            }

                            // 关闭加载进度条
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            MToast.show(VipPayMoneyActivity.this, "订单异常",
                                    MToast.SHORT);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        progressDialog.dismiss();
                        MToast.show(VipPayMoneyActivity.this, "订单异常",
                                MToast.SHORT);
                    }
                });

    }

    ;

    /**
     * 支付以及生成订单成功后，将VIP套餐信息写入配置文件中
     */
    private void dealSuccessOrder() {
        // 将VIP相关信息存入本地
        SharedPreferences sp = getSharedPreferences(MCon.SHARED_PREFERENCES_VIP_INFO, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("goodsId", species.getGoodsId());
        edit.putString("goodsName", species.getGoodsName());
        edit.putString("price", species.getPrice());
        edit.putString("vipStatus", "1");
        edit.putInt("remainDays", 30);// 默认写入30天
        edit.commit();

        VipPayMoneyActivity.this.setResult(RESULT_OK);
        VipPayMoneyActivity.this.finish();
    }

}
