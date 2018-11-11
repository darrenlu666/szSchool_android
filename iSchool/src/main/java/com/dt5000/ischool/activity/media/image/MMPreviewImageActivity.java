package com.dt5000.ischool.activity.media.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.activity.ToolbarActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 图片预览
 *
 * @author lijian
 */
public class MMPreviewImageActivity extends ToolbarActivity {

    private static final String TAG = MMPreviewImageActivity.class.getSimpleName();

    public static final String EXTRA_CONFIRM = "com.codyy.erpsportal.EXTRA_CONFIRM";

    private ArrayList<MMImageBean> mImageBeans;
    private int mPosition;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.view_pager)
    HackyViewPager mHackyViewPager;
    @Bind(R.id.btn_confirm)
    Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText(getString(R.string.exam_image_preview));
        mImageBeans = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
        Log.e(TAG, mImageBeans.toString());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mImageBeans.size() > 0) {
                    mHackyViewPager.setAdapter(new HackyPagerAdapter(mImageBeans, MMPreviewImageActivity.this));
                    mConfirmButton.setText( getString(R.string.exam_image_upload, mImageBeans.size()));
                }
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_DATA, mImageBeans);
                intent.putExtra(EXTRA_CONFIRM, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mViewPagerChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        };
        mHackyViewPager.addOnPageChangeListener(mViewPagerChangeListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mImageBeans.get(mPosition).isSelected()) {
            mCheck.setIcon(R.drawable.ic_exam_select_p);
        } else {
            mCheck.setIcon(R.drawable.ic_exam_select_n);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private MenuItem mCheck;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multimedia_menu_preview, menu);
        mCheck = menu.findItem(R.id.action_check);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        } else {
            if (mImageBeans.get(mPosition).isSelected()) {
                mImageBeans.get(mPosition).setSelected(false);
            } else {
                mImageBeans.get(mPosition).setSelected(true);
            }
            invalidateOptionsMenu();
            ArrayList<MMImageBean> imageBeans = new ArrayList<>();
            for (MMImageBean imageBean : mImageBeans) {
                if (imageBean.isSelected()) {
                    imageBeans.add(imageBean);
                }
            }
            mConfirmButton.setText(getString(R.string.exam_image_upload, imageBeans.size()));
            return true;
        }
    }

    @Override
    protected void onBack() {
        super.onBack();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_DATA, mImageBeans);
        intent.putExtra(EXTRA_CONFIRM, false);
        setResult(RESULT_OK, intent);
    }

    class HackyPagerAdapter extends PagerAdapter {
        List<MMImageBean> mList;
        Context mContext;

        public HackyPagerAdapter(List<MMImageBean> list, Activity activity) {
            this.mList = list;
            this.mContext = activity;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            RelativeLayout relativeLayout = new RelativeLayout(container.getContext());
            container.addView(relativeLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ZoomableDraweeView photoView = new ZoomableDraweeView(container.getContext());
            RelativeLayout.LayoutParams photoViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            photoViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(
                    Uri.parse("file://" + mList.get(position).getPath())).setTapToRetryEnabled(true).build();
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setProgressBarImage(new ProgressBarDrawable())
                    .build();

            photoView.setController(ctrl);
            photoView.setHierarchy(hierarchy);
            photoView.setLayoutParams(photoViewLayoutParams);
            relativeLayout.addView(photoView);
            return relativeLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.multimedia_view_pager;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    private ViewPager.OnPageChangeListener mViewPagerChangeListener;

    @Override
    protected void onDestroy() {
        mHackyViewPager.removeOnPageChangeListener(mViewPagerChangeListener);
        super.onDestroy();
    }
}
