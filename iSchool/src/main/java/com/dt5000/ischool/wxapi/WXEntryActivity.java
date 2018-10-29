package com.dt5000.ischool.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.MCon;
import com.dt5000.ischool.widget.MyToast;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 接收微信返回结果页面
 * 
 * @author 周锋
 * @date 2017年1月6日 下午3:02:36
 * @ClassInfo com.dt5000.ischool.wxapi.WXEntryActivity
 * @Description
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI wxapi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wx_entry);

		wxapi = WXAPIFactory.createWXAPI(this, MCon.WX_APP_ID, false);
		wxapi.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		wxapi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			MyToast.makeToast(getApplicationContext(), "分享成功",
					Toast.LENGTH_SHORT).show();
			WXEntryActivity.this.finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			MyToast.makeToast(getApplicationContext(), "取消分享",
					Toast.LENGTH_SHORT).show();
			WXEntryActivity.this.finish();
			break;
		default:
			break;
		}
	}

}