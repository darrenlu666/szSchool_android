package com.dt5000.ischool.adapter.student;

import android.support.annotation.Nullable;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.PayDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by weimy on 2017/11/28.
 */

public class VipPayDatielAdapter extends BaseQuickAdapter<PayDetails.OrdersBean, BaseViewHolder> {

    public VipPayDatielAdapter(@Nullable List<PayDetails.OrdersBean> data) {
        super(R.layout.vip_pay_details, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PayDetails.OrdersBean item) {
        if (item != null) {
            PayDetails.OrdersBean ordersBean = item;
            helper.setText(R.id.txtOrder, ordersBean.getStatus() != 1 ? "最新订单" : "历史订单");
            helper.setText(R.id.txtName, "套餐名称:" + ordersBean.getGoodsName());
            helper.setText(R.id.txtPrice, "金额:" + ordersBean.getPrice() + "元");
            try {
                if (ordersBean.getEndDate() != null) {
                    Log.i("convert", longToString(ordersBean.getEndDate().getTime(), "yyyy-MM-dd HH:mm:ss"));
                    helper.setText(R.id.txtDate, "有效期:" + longToString(ordersBean.getEndDate().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }else {
                    helper.setText(R.id.txtDate, "有效期:---");
                }

                if (ordersBean.getPayTime() != null) {
                    Log.i("convert", longToString(ordersBean.getPayTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                    helper.setText(R.id.txtTime, "支付时间:" + longToString(ordersBean.getPayTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }else {
                    helper.setText(R.id.txtTime, "支付时间:---");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (ordersBean.getStatus() != 1) {
                helper.setChecked(R.id.btnPay, true);
                helper.addOnClickListener(R.id.btnPay);
            } else {
                helper.setChecked(R.id.btnPay, false);
            }
            helper.setText(R.id.btnPay, ordersBean.getStatus() != 1 ? "等待支付" : "已支付");
        }
    }


    public String longToString(long currentTime, String formatType) throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

}
