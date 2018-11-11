package com.dt5000.ischool.activity.media.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.activity.RecordVideoActivity;
import com.dt5000.ischool.activity.media.activity.ToolbarActivity;
import com.dt5000.ischool.activity.media.activity.VideoPlayerActivity;
import com.dt5000.ischool.activity.media.adapter.MMBaseRecyclerViewAdapter;
import com.dt5000.ischool.activity.media.adapter.MMVideoGridAdapter;
import com.dt5000.ischool.activity.media.adapter.SpacesItemDecoration;
import com.dt5000.ischool.activity.media.bean.MMImageBean;
import com.dt5000.ischool.activity.media.bean.MMVideoBean;
import com.dt5000.ischool.activity.media.utils.DateUtil;
import com.dt5000.ischool.activity.media.utils.PermissionsUtils;
import com.dt5000.ischool.activity.media.utils.UIUtils;

import java.util.ArrayList;

/**
 * 视频MP4，最大500M。音视频可上传本地或者直接录制上传；音视频二者选其一上传；
 *
 * @author eachann
 */
public class MMVideoAlbumFragment extends Fragment implements Handler.Callback {
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    public static final String EXTRA_TYPE = ToolbarActivity.class.getPackage() + ".EXTRA_TYPE";
    public static final String TYPE_VIDEO = "TYPE_VIDEO";
    private static final String ARG_SIZE = "ARG_SIZE";
    private final static int WHAT = 0;
    private final static int EXCEPTION = -1;
    private static final int SPAN_COUNT = 4;
    private MMVideoGridAdapter mImageGridAdapter;
    private Handler mHandler;

    public static MMVideoAlbumFragment newInstance(int size) {
        MMVideoAlbumFragment fragment = new MMVideoAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SIZE, size);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 << 2 && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<MMImageBean> imageBeans = new ArrayList<>();
            MMImageBean bean = new MMImageBean(data.getStringExtra("EXTRA_DATA"), true, null);
            imageBeans.add(bean);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
            intent.putExtra(EXTRA_TYPE, TYPE_VIDEO);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_video_recycler, container, false);
        RecyclerView mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mImageGridAdapter = new MMVideoGridAdapter(new ArrayList<MMVideoBean>(), getActivity());
        mRecyclerView.setAdapter(mImageGridAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(UIUtils.dip2px(getActivity(), 1f)));
        mImageGridAdapter.setOnInViewClickListener(R.id.rl_item_video_list, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMVideoBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMVideoBean values) {
                if (position == 0) {
                    getGroupPermissons();
                } else {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra("EXTRA_VIDEO_PATH", values.getPath());
                    intent.putExtra("EXTRA_VIDEO_THUMB_PATH", values.getThumb());
                    intent.putExtra("EXTRA_VIDEO_DURATION", values.getTime());
                    startActivityForResult(intent, 1 << 2);
                }

            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMVideoBean values) {

            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getExternalStorage();
    }

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_GROUP = 2;

    public void getExternalStorage() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionsUtils.warn(getContext(), "未授予存储空间权限,请在设置中打开!");

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            mThread.start();
        }
    }

    public void getGroupPermissons() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionsUtils.warn(getContext(), "未授予存储空间权限,请在设置中打开!");

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                PermissionsUtils.warn(getContext(), "未授予相机权限,请在设置中打开!");

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                PermissionsUtils.warn(getContext(), "未授予录音权限,请在设置中打开!");

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intent = new Intent(getActivity(), RecordVideoActivity.class);
            intent.putExtra("EXTRA_SIZE", getArguments().getInt(ARG_SIZE, 500 * 1024 * 1024));//50M
            startActivityForResult(intent, 1 << 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mThread.start();

                } else {
                    PermissionsUtils.warn(getContext(), "未授予存储空间权限,请在设置中打开!");
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_GROUP:
                if (grantResults.length > 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), RecordVideoActivity.class);
                    intent.putExtra("EXTRA_SIZE", getArguments().getInt(ARG_SIZE, 500 * 1024 * 1024));//50M
                    startActivityForResult(intent, 1 << 2);

                }
                break;
        }
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
            // 扫描视频
            try {
                ArrayList<MMVideoBean> list = new ArrayList<>();
                if(getContext() == null)return;
                ContentResolver cr = getContext().getContentResolver();
                String str[] = {MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.DURATION};
                Cursor cursor = cr.query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, MediaStore.Video.Media.SIZE + "<? and " + MediaStore.Video.Media.SIZE + ">0 and " + MediaStore.Video.Media.DISPLAY_NAME + " like '%.mp4' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.FLV' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.RM' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.RMVB' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.AVI' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.ASF' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.MOV' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.MPG' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.MPEG' or " +
                                MediaStore.Video.Media.DISPLAY_NAME + " like '%.3GP'",
                        new String[]{String.valueOf(getArguments().getInt(ARG_SIZE, 500 * 1024 * 1024 + 1))}, MediaStore.Video.Media.DATE_ADDED + " DESC");
                try {
                    while (cursor != null && cursor.moveToNext()) {
                        if (!TextUtils.isEmpty(cursor.getString(4)) && Long.parseLong(cursor.getString(4)) > 0) {
                            //先得到缩略图的URL和对应的图片id
                            MMVideoBean bean;
                            Cursor thumbCursor = cr.query(
                                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                    new String[]{
                                            MediaStore.Video.Thumbnails.VIDEO_ID,
                                            MediaStore.Video.Thumbnails.DATA
                                    },
                                    MediaStore.Video.Thumbnails.VIDEO_ID + "=" + cursor.getString(0),
                                    null,
                                    null);
                            try {
                                if (thumbCursor != null && thumbCursor.getCount() > 0 && thumbCursor.moveToFirst()) {
                                    do {
                                        bean = new MMVideoBean(cursor.getString(1), cursor.getString(2), DateUtil.formatMediaTime(Long.parseLong(cursor.getString(4))), thumbCursor.getString(1));
                                        list.add(bean);
                                    } while (thumbCursor.moveToNext());

                                } else {
                                    bean = new MMVideoBean(cursor.getString(1), cursor.getString(2), DateUtil.formatMediaTime(Long.parseLong(cursor.getString(4))), cursor.getString(2));
                                    list.add(bean);
                                }
                            } finally {
                                if (thumbCursor != null)
                                    thumbCursor.close();
                            }
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                list.add(0, new MMVideoBean(null, null, null));
                Message message = new Message();
                message.what = WHAT;
                message.obj = list;
                mHandler.sendMessage(message);
            } catch (SecurityException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = EXCEPTION;
                mHandler.sendMessage(message);
            }
        }
    });

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT:
                try {
                    ArrayList<MMVideoBean> mVideoList = (ArrayList<MMVideoBean>) msg.obj;
                    mImageGridAdapter.setList(mVideoList);
                } catch (Exception e) {
                    e.printStackTrace();
                    mImageGridAdapter.setList(new ArrayList<MMVideoBean>());
                }
                break;
            case EXCEPTION:
                PermissionsUtils.warn(getContext(), "未授予存储空间权限,请在设置中打开!");
                break;
            default:
                break;
        }
        return false;
    }
}
