package com.dt5000.ischool.activity.media.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.utils.UIUtils;

import butterknife.ButterKnife;

/**
 * Created by eachann on 2015/12/23.
 */
public abstract class ToolbarActivity extends AppCompatActivity {
    private static int TRANSLATION_Y = -38;
    protected static final String EXTRA_TYPE = ToolbarActivity.class.getPackage().getName() + ".TYPE";
    protected static final String EXTRA_INFO = ToolbarActivity.class.getPackage().getName() + ".INFO";
    protected static final String EXTRA_NAME = ToolbarActivity.class.getPackage().getName() + ".NAME";
    protected static final String EXTRA_TITLE = ToolbarActivity.class.getPackage() + ".TITLE";
    protected static final String EXTRA_EXAM_IS_ARRANGE = ToolbarActivity.class.getPackage() + ".IS_ARRANGE";
    protected static final String EXTRA_EXAM_ID = ToolbarActivity.class.getPackage() + ".EXAM_ID";
    protected static final String EXTRA_URL = ToolbarActivity.class.getPackage() + ".URL";
    protected static final String EXTRA_EXAM_TASK_ID = ToolbarActivity.class.getPackage() + ".EXAM_TASK_ID";
    protected static final String EXTRA_EXAM_RESULT_ID = ToolbarActivity.class.getPackage() + ".EXTRA_EXAM_RESULT_ID";
    protected static final String EXTRA_TASK_URL = ToolbarActivity.class.getPackage() + ".TASK_URL";
    protected static final String EXTRA_NO_EXAM_DETAIL = ToolbarActivity.class.getPackage() + ".NO_EXAM_DETAIL";
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        ButterKnife.bind(this);
        initToolbar();
    }

    /**
     * 初始化toolbar
     *
     * @param toolbar
     */
    protected void initToolbar(Toolbar toolbar) {
        if (toolbar == null)
            return;
        toolbar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setToolbarElevation(toolbar);
        }
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setToolbarElevation(Toolbar toolbar) {
        toolbar.setElevation(UIUtils.dip2px(this, 2));
    }

    /**
     * 设置overflowIcon
     *
     * @param toolbar
     * @param resId
     */
    public void setOverFlowIcon(Toolbar toolbar, int resId) {
        toolbar.setOverflowIcon(getResources().getDrawable(resId));
    }

    /**
     * 增加了默认的返回finish事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBack();//add by kmdai
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();//add by kmdai
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @LayoutRes
    protected abstract int getLayoutView();

    protected abstract void initToolbar();

    /**
     * 重写这个方法，可以通知子类返回回调
     * add by kmdai
     */
    protected void onBack() {

    }

    /**
     * 拟物动画
     * add by eachann
     *
     * @param isPlus
     * @param view
     */
    protected void setViewAnim(boolean isPlus, View... view) {
        float translationY = UIUtils.dip2px(this, isPlus ? -TRANSLATION_Y : TRANSLATION_Y);
        for (View v : view) {
            v.setAlpha(0.8f);
            v.setTranslationY(translationY);
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(300L)
                    .setDuration(200L)
                    .setInterpolator(new FastOutLinearInInterpolator())
                    .start();
        }

    }

    /**
     * added by eachann 2016-01-29
     * unbind butterKnife
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
