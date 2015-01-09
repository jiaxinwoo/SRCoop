package com.srcoop.android.activity.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.srcoop.android.activity.R;
import com.srcoop.android.activity.view.HandyTextView;

public abstract class MyFragment extends Fragment{

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text, Context context) {
		View toastRoot = LayoutInflater.from(context).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
}
