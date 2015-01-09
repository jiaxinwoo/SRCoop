package com.srcoop.android.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.srcoop.android.activity.LoginActivity;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.dialog.BaseDialog;

public class SettingFragment extends MyFragment {
	
	private InputMethodManager imm;
	
	private Button btnLogout;
	private BaseDialog mBaseDialog;
	
	private MyUser currentUser;
	@SuppressLint("ResourceAsColor")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
		currentUser=BmobUser.getCurrentUser(getActivity(), MyUser.class);
	}
	@SuppressLint("ResourceAsColor") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		((TextView) getActivity().findViewById(R.id.activity_title))
				.setText("设置");
		
		//设置setting页面
		View v =  inflater.inflate(R.layout.fragment_menuitem_setting, container,
				false);
		
		Button button = ((Button) getActivity().findViewById(
				R.id.title_bar_right_menu));
		button.setBackgroundColor(R.color.transparent);
		button.setClickable(false);
		button.setOnClickListener(null);
		
		//注销
		btnLogout = (Button) v.findViewById(R.id.btn_logout);
		btnLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaseDialog = new BaseDialog(getActivity());
				mBaseDialog.setTitle("提示");
				mBaseDialog.setMessage("确认登出");
				mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mBaseDialog.dismiss();
						BmobUser.logOut(getActivity());
						BmobUser currentUser = BmobUser.getCurrentUser(getActivity()); // 现在的currentUser是null了
						//跳转到登录页面
						Intent intent = new Intent(getActivity(),LoginActivity.class);
						getActivity().startActivity(intent);
					}
				
				});
				mBaseDialog.setButton2("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mBaseDialog.cancel();
					}
				
				});
				mBaseDialog.setButton1Background(R.drawable.btn_default_popsubmit);
				mBaseDialog.show();
			}
		});
		return v;
	}
}
