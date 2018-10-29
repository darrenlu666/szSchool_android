package com.dt5000.ischool.adapter;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.HomeworkAttach;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.MToast;
import com.dt5000.ischool.utils.MimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.HttpHandler;

/**
 * 作业附件列表适配器
 *
 * @author 周锋
 * @date 2016年1月13日 下午4:42:09
 * @ClassInfo com.dt5000.ischool.adapter.HomeworkAttachListAdapter
 * @Description
 */
public class HomeworkAttachListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<HomeworkAttach> list;
    private File fileDir;
    private FinalHttp fh;

    public HomeworkAttachListAdapter(Context ctx, List<HomeworkAttach> datas) {
        this.context = ctx;
        this.list = datas;
        inflater = LayoutInflater.from(context);
        fileDir = new File(Environment.getExternalStorageDirectory(), "Download/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        fh = new FinalHttp();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.view_list_item_homework_attach, null);
            holder = new ViewHolder();
            holder.txt_file_name = (TextView) convertView
                    .findViewById(R.id.txt_file_name);
            holder.btn_down = (Button) convertView.findViewById(R.id.btn_down);
            holder.btn_open = (Button) convertView.findViewById(R.id.btn_open);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HomeworkAttach attach = list.get(position);
        String fileUrl = attach.getFilePath();
        holder.txt_file_name.setText(fileUrl);

        final File downFile = new File(fileDir, fileUrl);
        if (downFile.exists()) {
            holder.btn_open.setVisibility(View.VISIBLE);
            holder.btn_down.setVisibility(View.GONE);
        } else {
            holder.btn_open.setVisibility(View.GONE);
            holder.btn_down.setVisibility(View.VISIBLE);
        }

        holder.btn_open.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(downFile);
            }
        });

        holder.btn_down.setOnClickListener(new DownLoadListener(
                holder.btn_open, holder.btn_down, fileUrl, downFile.getAbsolutePath()));

        return convertView;
    }

    private void openFile(File file) {
        try {
            if (file.exists()) {
                Intent intent = new Intent();
                String type = MimeUtil.getMIMEType(file);
                intent.setAction(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String authority = context.getPackageName() + ".provider";
                    Uri contentUri = FileProvider.getUriForFile(context, authority, file);
                    intent.setDataAndType(contentUri, type);
                } else {
                    intent.setDataAndType(Uri.fromFile(file), type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                    /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				String type = MimeUtil.getMIMEType(file);
				MLog.i("文件type: " + type);
				intent.setDataAndType(Uri.fromFile(file), type);*/
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogAlert.show(context, "没有找到相关程序支持此类文件");
        }
    }

    private HttpHandler<?> download(String fileUrl, String filePath,
                                    final Button btn_open, final Button btn_down) {
        String url = UrlProtocol.FILE_ATTACH + fileUrl;
        MLog.i("附件下载：" + url);
        return fh.download(url, filePath, true, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {
                btn_down.setText((int) (current / (float) count * 100) + "%");
            }

            @Override
            public void onSuccess(File t) {
                btn_down.setVisibility(View.GONE);
                btn_down.setEnabled(true);
                btn_open.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                MLog.i(strMsg);
                MToast.show(context, "下载失败，请重试", MToast.SHORT);
                btn_down.setText("下载");
                btn_down.setVisibility(View.VISIBLE);
                btn_down.setEnabled(true);
                btn_open.setVisibility(View.GONE);
            }
        });
    }

    private class DownLoadListener implements OnClickListener {
        private Button btn_open;
        private Button btn_down;
        private String fileUrl;
        private String filePath;
        private HttpHandler<?> handler;

        public DownLoadListener(Button btn_open, Button btn_down,
                                String fileUrl, String filePath) {
            this.btn_down = btn_down;
            this.btn_open = btn_open;
            this.filePath = filePath;
            this.fileUrl = fileUrl;
        }

        @Override
        public void onClick(View v) {
            btn_down.setEnabled(false);
            btn_down.setText("0%");
            handler = download(fileUrl, filePath, btn_open, btn_down);
        }
    }

    static class ViewHolder {
        Button btn_down;
        Button btn_open;
        TextView txt_file_name;
    }

}
