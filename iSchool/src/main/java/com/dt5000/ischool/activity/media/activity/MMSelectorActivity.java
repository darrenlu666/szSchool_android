package com.dt5000.ischool.activity.media.activity;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.fragment.MMVideoAlbumFragment;

import butterknife.Bind;


/**
 * @author eachann
 */
public class MMSelectorActivity extends ToolbarActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    /**
     * 单位M
     */
    private static final int sVideoDefaultSize = 500;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_task_multimedia;
    }

    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    private static final String EXTRA_TYPE_IMAGE = "IMAGE";
    private static final String EXTRA_TYPE_AUDIO = "AUDIO";
    private static final String EXTRA_TYPE_VIDEO = "VIDEO";

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        if (getIntent().getStringExtra(EXTRA_TYPE) != null) {
            switch (getIntent().getStringExtra(EXTRA_TYPE)) {
                case EXTRA_TYPE_IMAGE:
                    mTitle.setText(getString(R.string.exam_image_select));
                    //replaceConentWithFragment(MMImageAlbumFragment.newInstance(getIntent().getIntExtra("EXTRA_SIZE", 8)));
                    break;
                case EXTRA_TYPE_AUDIO:
                    mTitle.setText(getString(R.string.exam_audio_select));
                    //replaceConentWithFragment(new MMAudioAlbumFragment());
                    break;
                case EXTRA_TYPE_VIDEO:
                    mTitle.setText(getString(R.string.exam_video_select));
                    replaceConentWithFragment(MMVideoAlbumFragment.newInstance(getIntent().getIntExtra("EXTRA_SIZE", sVideoDefaultSize) * 1024 * 1024 + 1));
                    break;
            }

        }
        setViewAnim(false, mTitle);
    }

    private void replaceConentWithFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content,fragment)
                .commitAllowingStateLoss();
    }
}
