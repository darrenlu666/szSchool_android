package com.dt5000.ischool.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

/**
 * Soap请求辅助类
 *
 * @author 周锋
 * @date 2016年1月22日 上午11:25:35
 * @ClassInfo com.dt5000.ischool.utils.SoapUtil
 * @Description
 */
public class SoapUtil {

    /**
     * 根据soapObject，url，action调用webservice获得result
     *
     * @param soapObject  绑定了传入的参数
     * @param URL         webservice的路径
     * @param SOAP_ACTION 调用的action名称
     * @return
     */
    public static SoapObject getResultByWebService(SoapObject soapObject, String URL, String SOAP_ACTION) throws Exception {
        SoapObject result = null;

        AndroidHttpTransport ht = new AndroidHttpTransport(URL);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);

        ht.debug = true;
        ht.call(SOAP_ACTION, envelope);
//        Log.i("SoapObject", ht.requestDump);
        result = (SoapObject) envelope.bodyIn;

        return result;
    }

    /**
     * 转换xml中的特殊字符
     *
     * @param xml
     * @return
     */
    public static String convertSpecialChars(String xml) {
        String result = "";

        if (!CheckUtil.stringIsBlank(xml.trim())) {
            result = xml.replace("&nbsp;", " ").replace("o:p", "p")
                    .replace("&", "???").replace("</br>", "<br/>");
        }

        return result;
    }

}
