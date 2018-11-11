package com.dt5000.ischool.activity.media.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.activity.ToolbarActivity;
import com.dt5000.ischool.activity.media.adapter.MMBaseRecyclerViewAdapter;
import com.dt5000.ischool.activity.media.adapter.MMImageGridAdapter;
import com.dt5000.ischool.activity.media.adapter.SpacesItemDecoration;
import com.dt5000.ischool.activity.media.bean.MMImageBean;
import com.dt5000.ischool.activity.media.camera.CaptureImageActivity;
import com.dt5000.ischool.activity.media.image.MMPreviewImageActivity;
import com.dt5000.ischool.activity.media.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片可上传8张，每张最大5M，格式支持JPG，PNG，JPEG，BMP等格式
 *
 * @author eachann
 */
public class MMImageAlbumFragment extends Fragment implements Handler.Callback {

    private static final int REQUEST_CODE_PREVIEW = 1 << 3;
    private static final int REQUEST_CODE_CAPTURE = 1 << 4;
    private static final int SPAN_COUNT = 4;
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    public static final String EXTRA_TYPE = ToolbarActivity.class.getPackage() + ".EXTRA_TYPE";
    public static final String TYPE_IMAGE = "TYPE_IMAGE";
    private static final String ARG_SIZE = "ARG_SIZE";
    private final static int WHAT = 0;
    private List<MMImageBean> mImageList = new ArrayList<>();
    private List<Integer> mSelectedIndex = new ArrayList<>();
    private TextView mTvPreview;
    private Button mConfirm;
    private MMImageGridAdapter mImageGridAdapter;
    /**
     * 需要几张图
     */
    private int mNeedImageCount;
    private Handler mHandler;

    public static MMImageAlbumFragment newInstance(int size) {
        MMImageAlbumFragment fragment = new MMImageAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SIZE, size);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        if (getArguments() != null) {
            mNeedImageCount = getArguments().getInt(ARG_SIZE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PREVIEW:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    boolean confirmed = data.getBooleanExtra(MMPreviewImageActivity.EXTRA_CONFIRM, false);
                    if (confirmed) {
                        Intent intent = new Intent();
                        ArrayList<MMImageBean> imageBeans = data.getParcelableArrayListExtra(EXTRA_DATA);
                        intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                        intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        ArrayList<MMImageBean> imageBeans = data.getParcelableArrayListExtra(EXTRA_DATA);
                        for (MMImageBean imageBean: imageBeans) {
                            if (!imageBean.isSelected()) {//取消选择的
                                Integer index = mImageList.indexOf(imageBean);
                                mSelectedIndex.remove(index);
                                mImageList.get(index).setSelected(false);
                            }
                        }
                        mImageGridAdapter.notifyDataSetChanged();
                        uploadButtonState();
                    }
                }
                break;
            case REQUEST_CODE_CAPTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                    MMImageBean imageBean = new MMImageBean(data.getStringExtra("EXTRA_DATA"), true, null);
                    imageBeans.add(imageBean);
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                    intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
                break;
        } // switch
    }

    private void uploadButtonState() {
        if (mSelectedIndex.size() > 0) {
            mConfirm.setText(getString(R.string.exam_image_upload, mSelectedIndex.size()));
            mTvPreview.setEnabled(true);
            mConfirm.setEnabled(true);
        } else {
            mConfirm.setText(getString(R.string.exam_image_upload_n));
            mTvPreview.setEnabled(false);
            mConfirm.setEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_multimedia_recycler, container, false);
        RecyclerView mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mTvPreview = (TextView) mRootView.findViewById(R.id.tv_preview);
        mConfirm = (Button) mRootView.findViewById(R.id.btn_confirm);
        mConfirm.setText(getString(R.string.exam_image_upload_n));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //计算图片组件宽度
        int gapPadding = UIUtils.dip2px(getActivity(), 1f);
        int screenSize = getContext().getResources().getDisplayMetrics().widthPixels;
        int imageWidth = screenSize / SPAN_COUNT - gapPadding;

        mImageGridAdapter = new MMImageGridAdapter(new ArrayList<MMImageBean>(), getActivity());
        mImageGridAdapter.setImageWidth(imageWidth);
        mRecyclerView.setAdapter(mImageGridAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(gapPadding));

        mImageGridAdapter.setOnInViewClickListener(R.id.v_selected_frame, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMImageBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMImageBean values) {
                if (position != 0) {//第一项是拍照，排除
                    RelativeLayout relativeLayout = (RelativeLayout) v;
                    if (relativeLayout.getChildAt(0) instanceof ImageView) {
                        if (values.isSelectable()) {
                            if (values.isSelected()) {
                                values.setSelected(false);
                                mSelectedIndex.remove(position);
                                relativeLayout.setBackgroundResource(R.color.transparent);
                                ((ImageView) relativeLayout.getChildAt(0)).setImageResource(R.drawable.ic_exam_select_n);
                            } else {
                                if (mSelectedIndex.size() == mNeedImageCount) {
                                    Snackbar.make(v, getString(R.string.exam_image_max_count, (mNeedImageCount)), Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                values.setSelected(true);
                                relativeLayout.setBackgroundResource(R.color.image_selected_color);
                                ((ImageView) relativeLayout.getChildAt(0)).setImageResource(R.drawable.ic_exam_select_p);
                                mSelectedIndex.add(position);
                            }
                        } else {
                            Snackbar.make(v, "你选择的图片过大，请选择小于5M的图片", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    uploadButtonState();
                }

            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMImageBean values) {

            }
        });
        mImageGridAdapter.setOnInViewClickListener(R.id.imgQueue, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMImageBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMImageBean values) {
                if (position == 0) {
                    startActivityForResult(new Intent(getActivity(), CaptureImageActivity.class), REQUEST_CODE_CAPTURE);
                }
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMImageBean values) { }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                for (Integer index: mSelectedIndex) {
                    imageBeans.add( mImageList.get(index));
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                for (Integer index: mSelectedIndex) {
                    imageBeans.add( mImageList.get(index));
                }
                Intent intent = new Intent(getActivity(), MMPreviewImageActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                startActivityForResult(intent, REQUEST_CODE_PREVIEW);
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mThread.isAlive()) {
                mThread.interrupt();
            }
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            ArrayList<MMImageBean> list = new ArrayList<>();
            ContentResolver cr = getContext().getContentResolver();
            // 扫描照片
            String projection[] = {Media._ID,
                    Media.DISPLAY_NAME,
                    Media.DATA,
                    Media.MIME_TYPE,
                    Media.SIZE
            };
            Cursor imageCursor = cr.query(
                    Media.EXTERNAL_CONTENT_URI, projection,
                    Media.SIZE + ">0 and ("
                            + Media.MIME_TYPE + "='image/jpeg' or "
                            + Media.MIME_TYPE + "='image/png' or "
                            + Media.MIME_TYPE + "='image/bmp')",
                    null, Media.DATE_ADDED + " ASC");
            //new String[]{"5242881"}
            try {
                if (imageCursor != null && imageCursor.getCount() > 0) {
                    while (imageCursor.moveToNext()) {
                        MMImageBean bean;
                        Cursor thumbCursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.Thumbnails.IMAGE_ID,
                                             MediaStore.Images.Thumbnails.DATA},
                                MediaStore.Images.Thumbnails.IMAGE_ID + "=" + imageCursor.getString(0), null, null);
                        try {
                            String thumbnails;
                            if (thumbCursor != null && thumbCursor.getCount() > 0 && thumbCursor.moveToFirst()) {
                                thumbnails = thumbCursor.getString(1);
                            } else {
                                thumbnails = imageCursor.getString(2);
                            }
                            bean = new MMImageBean(imageCursor.getString(2), false, thumbnails);
                            bean.setSelectable(imageCursor.getInt(4) <= 5242881);//图片大于5m不可选
                            list.add(bean);
                        } finally {
                            if (thumbCursor != null)
                                thumbCursor.close();
                        }
                    }

                }
            } finally {
                if (imageCursor != null)
                    imageCursor.close();
            }

            // show newest photo at beginning of the list
            Collections.reverse(list);
            list.add(0, new MMImageBean(null, false, null));
            Message message = new Message();
            message.what = WHAT;
            message.obj = list;
            mHandler.sendMessage(message);
        }
    });

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT:
                try {
                    mImageList = (ArrayList<MMImageBean>) msg.obj;
                    mImageGridAdapter.setList(mImageList);
                } catch (Exception e) {
                    e.printStackTrace();
                    mImageGridAdapter.setList(mImageList = new ArrayList<>());
                }
                break;

            default:
                break;
        }
        return false;
    }
}
