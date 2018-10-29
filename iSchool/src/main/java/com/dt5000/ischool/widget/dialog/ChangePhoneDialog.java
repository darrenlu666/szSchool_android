package com.dt5000.ischool.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;

/**
 * Created by weimy on 2017/11/27.
 */

public class ChangePhoneDialog extends Dialog implements View.OnClickListener {
    private TextView txtConfirmPhone;
    private Button btnNo, btnYes;
    private OnSelectListener mOnSelectListener;

    public ChangePhoneDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public ChangePhoneDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected ChangePhoneDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.change_phone_dialog);
//        setCanceledOnTouchOutside(false);
//        initView();
//    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.change_phone_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(false);
        txtConfirmPhone = (TextView) view.findViewById(R.id.txtConfirmPhone);
        btnNo = (Button) view.findViewById(R.id.btnNo);
        btnYes = (Button) view.findViewById(R.id.btnYes);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYes:
                if (mOnSelectListener != null) {
                    mOnSelectListener.onClick(this, true);
                }
                break;
            case R.id.btnNo:
                if (mOnSelectListener != null) {
                    mOnSelectListener.onClick(this, false);
                }
                break;
        }
    }

    public ChangePhoneDialog setConfirmPhone(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            txtConfirmPhone.setText("确认将手机号码修改为:\n\t\t" + phone + "?");
        }

        return this;
    }

    //interface
    public interface OnSelectListener {
        void onClick(Dialog dialog, boolean isYes);
    }

    public ChangePhoneDialog setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
        return this;
    }
}
