package com.dt5000.ischool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.Score;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MLog;

/**
 * 成绩饼图页面
 *
 * @author 周锋
 * @date 2016年3月22日 上午10:37:03
 * @ClassInfo com.dt5000.ischool.activity.ScorePieActivity
 * @Description
 */
public class ScorePieActivity extends Activity {

    private WebView webview_score_pie;
    private TextView txt_title;
    private LinearLayout lLayout_back;

    private User user;
    private Score score;
    private String stuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_pie);

        initView();
        initListener();
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        webview_score_pie = (WebView) findViewById(R.id.webview_score_pie);
        webview_score_pie.getSettings().setJavaScriptEnabled(true);
        webview_score_pie.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private void initListener() {
        lLayout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScorePieActivity.this.finish();
            }
        });
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        score = (Score) bundle.getSerializable("score");
        stuId = bundle.getString("stuId");

        user = User.getUser(this);

        if (CheckUtil.stringIsBlank(stuId)) {
            stuId = user.getUserId();
        }

        txt_title.setText(score.getExamName());

        String url = UrlProtocol.HOMEWORK_HOST
                + "android/scorePie_pie.jhtml?examId=" + score.getExamId()
                + "&sid=" + stuId;
        MLog.i("成绩饼图地址：" + url);
        webview_score_pie.loadUrl(url);
    }
}
