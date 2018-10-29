package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.ImageUtil;
import com.dt5000.ischool.utils.MCon;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 关于我们页面
 * 
 * @author 周锋
 * @date 2016年2月1日 上午9:44:17
 * @ClassInfo com.dt5000.ischool.activity.AboutActivity
 * @Description
 */
public class AboutActivity extends Activity {

	private LinearLayout lLayout_back;
	private TextView txt_title;
	private TextView txt_topbar_btn;

	private IWXAPI wxapi;
	private Dialog shareDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);

		initView();
		initListener();
		init();
	}

	private void initView() {
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText("关于我们");
		txt_topbar_btn = (TextView) findViewById(R.id.txt_topbar_btn);
		txt_topbar_btn.setText("分享");
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
	}

	private void initListener() {
		lLayout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});

		txt_topbar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.show();
			}
		});
	}

	private void init() {
		wxapi = WXAPIFactory.createWXAPI(this, MCon.WX_APP_ID, true);
		wxapi.registerApp(MCon.WX_APP_ID);

		shareDialog = createShareDialog();
	}

	private void shareToWeiXin(final int sceneType) {
		ImageLoaderUtil.createSimple(this).loadImage(
				"http://sz.aroundu.net/images/new/side_code.png",
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						WXWebpageObject webpageObject = new WXWebpageObject();
						webpageObject.webpageUrl = "http://sz.aroundu.net";

						WXMediaMessage msg = new WXMediaMessage(webpageObject);
						msg.title = "苏州学堂";
						msg.description = "苏州点通教育科技有限公司与市教育局合作构建的教育一站式云平台，集作业管理、成绩管理、校园生活、排课管理、空中课堂、家校互联等强大的功能于一体";
						msg.thumbData = ImageUtil.bmpToByteArray(loadedImage,
								false);

						SendMessageToWX.Req req = new SendMessageToWX.Req();
						req.transaction = "webpage"
								+ System.currentTimeMillis();
						req.message = msg;
						req.scene = sceneType;

						wxapi.sendReq(req);
					}
				});
	}

	@SuppressLint("InflateParams")
	private Dialog createShareDialog() {
		View view = getLayoutInflater().inflate(R.layout.view_share_dialog,
				null);
		view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
		TextView txt_share_cancel = (TextView) view
				.findViewById(R.id.txt_share_cancel);
		txt_share_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();
			}
		});
		ImageView img_share_wx = (ImageView) view
				.findViewById(R.id.img_share_wx);
		img_share_wx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();

				shareToWeiXin(SendMessageToWX.Req.WXSceneSession);
			}
		});
		ImageView img_share_pyq = (ImageView) view
				.findViewById(R.id.img_share_pyq);
		img_share_pyq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();

				shareToWeiXin(SendMessageToWX.Req.WXSceneTimeline);
			}
		});
		ImageView img_share_qq = (ImageView) view
				.findViewById(R.id.img_share_qq);
		img_share_qq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();
			}
		});
		ImageView img_share_qzone = (ImageView) view
				.findViewById(R.id.img_share_qzone);
		img_share_qzone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();
			}
		});

		Dialog dialog = new Dialog(this, R.style.ShareDialogStyle);
		dialog.setContentView(view);
		dialog.getWindow().setGravity(Gravity.BOTTOM);

		return dialog;
	}

}
