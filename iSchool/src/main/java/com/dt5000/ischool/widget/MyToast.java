package com.dt5000.ischool.widget;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;

public class MyToast extends Toast {

	public MyToast(Context context) {
		super(context);
	}

	@SuppressLint("InflateParams")
	public static Toast makeToast(Context context, String text, int duration) {
		View toastView = LayoutInflater.from(context).inflate(
				R.layout.view_my_toast, null);
		LinearLayout lLayout_root = (LinearLayout) toastView
				.findViewById(R.id.lLayout_root);
		TextView txt = (TextView) toastView.findViewById(R.id.txt);
		txt.setText(text);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		params.width = displayMetrics.widthPixels;
		lLayout_root.setLayoutParams(params);

		Toast toast = makeText(context, text, duration);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setView(toastView);

		try {
			Field mTNField = toast.getClass().getDeclaredField("mTN");
			if (mTNField != null) {
				mTNField.setAccessible(true);
				Object mTN = mTNField.get(toast);

				if (mTN != null) {
					Field mParamsField = mTN.getClass().getDeclaredField(
							"mParams");

					if (mParamsField != null) {
						mParamsField.setAccessible(true);
						Object mParams = mParamsField.get(mTN);

						if (mParams != null
								&& mParams instanceof WindowManager.LayoutParams) {
							WindowManager.LayoutParams layoutParams = (LayoutParams) mParams;
							layoutParams.windowAnimations = R.style.MyToastAnim;
						}
					}
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return toast;
	}

}
