package com.dt5000.ischool.utils.alipay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MLog;

public class Result {

	private static final Map<String, String> sResultStatus;

	private String mResult;

	String resultStatus = null;
	String memo = null;
	String result = null;
	boolean isSignOk = false;

	public Result(String result) {
		this.mResult = result;
	}

	static {
		sResultStatus = new HashMap<String, String>();
		sResultStatus.put("9000", "操作成功");
		sResultStatus.put("8000", "支付结果确认中");
		sResultStatus.put("4000", "系统异常");
		sResultStatus.put("4001", "数据格式不正确");
		sResultStatus.put("4003", "该用户绑定的支付宝账户被冻结或不允许支付");
		sResultStatus.put("4004", "该用户已解除绑定");
		sResultStatus.put("4005", "绑定失败或没有绑定");
		sResultStatus.put("4006", "订单支付失败");
		sResultStatus.put("4010", "重新绑定账户");
		sResultStatus.put("6000", "支付服务正在进行升级操作");
		sResultStatus.put("6001", "用户中途取消支付操作");
		sResultStatus.put("7001", "网页支付失败");
	}

	/** 获取memo结果信息 */
	public String getResult() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		return getContent(src, "memo=", ";result");
	}

	/** 判断支付结果 */
	public boolean isSucc() {
		if (mResult == null) {
			return false;
		}
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		String rs = getContent(src, "resultStatus=", ";memo");
		if ("9000".equals(rs)) {
			return true;
		} else {
			return false;
		}
	}

	/** 解析返回的结果 */
	public void parseResult() {
		try {
			String src = mResult.replace("{", "");
			src = src.replace("}", "");
			String rs = getContent(src, "resultStatus=", ";memo");
			if (sResultStatus.containsKey(rs)) {
				resultStatus = sResultStatus.get(rs);
			} else {
				resultStatus = "其他错误";
			}
			resultStatus += "(" + rs + ")";
			MLog.i("resultStatus-->" + resultStatus);
			
			memo = getContent(src, "memo=", ";result");
			MLog.i("memo-->" + memo);
			
			result = getContent(src, "result=", null);
			MLog.i("result-->" + result);
			
			if (!CheckUtil.stringIsBlank(result)) {
				isSignOk = checkSign(result);
				MLog.i("isSignOk-->" + isSignOk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 验证签名
	 * 
	 * @param result
	 * @return
	 */
	private boolean checkSign(String result) {
		boolean retVal = false;
		try {
			JSONObject json = string2JSON(result, "&");

			// 截取签名内容
			int pos = result.indexOf("&sign_type=");
			String signContent = result.substring(0, pos);

			// 获取签名类型,固定RSA
			String signType = json.getString("sign_type");
			signType = signType.replace("\"", "");

			// 获取已签名的sign
			String sign = json.getString("sign");
			sign = sign.replace("\"", "");

			// 验证签名
			if (signType.equalsIgnoreCase("RSA")) {
				retVal = Rsa.doCheck(signContent, sign, Keys.PUBLIC);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MLog.i("Exception =" + e);
		}
		return retVal;
	}

	/**
	 * 将返回的result拆解并构建成与所有返回参数对应的key-value形式的JSONObject对象
	 * 
	 * @param src
	 * @param split
	 * @return
	 */
	public JSONObject string2JSON(String src, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arr = src.split(split);
			for (int i = 0; i < arr.length; i++) {
				String[] arrKey = arr[i].split("=");
				json.put(arrKey[0], arr[i].substring(arrKey[0].length() + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 截取返回结果中的支付结果信息
	 * 
	 * @param src
	 * @param startTag
	 * @param endTag
	 * @return
	 */
	private String getContent(String src, String startTag, String endTag) {
		String content = src;
		int start = src.indexOf(startTag);
		start += startTag.length();
		try {
			if (endTag != null) {
				int end = src.indexOf(endTag);
				content = src.substring(start, end);
			} else {
				content = src.substring(start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
}
