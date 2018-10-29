package com.dt5000.ischool.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * Created by weimy on 2017/11/27.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {
    private TextView txtContent, txtSelect;
    //content
    private String content;
    //interface
    private OnDialogClickListener onDialogClickListener;

    public LoginDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public LoginDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected LoginDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }


    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_dialog);
        setCanceledOnTouchOutside(false);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtSelect = (TextView) findViewById(R.id.txtSelect);

        txtContent.setText(TextUtils.isEmpty(content) ? "" : content);
        txtSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSelect:
                if (onDialogClickListener != null) {
                    onDialogClickListener.onClick(this);
                }
                dismiss();
                break;
        }
    }

    public LoginDialog setContent(String content) {
        this.content = content;
        txtContent.setText(content);
        return this;
    }

    public interface OnDialogClickListener {
        void onClick(Dialog dialog);
    }

    public LoginDialog setOnDialogClickListener(OnDialogClickListener listener) {
        this.onDialogClickListener = listener;
        return this;
    }
}
