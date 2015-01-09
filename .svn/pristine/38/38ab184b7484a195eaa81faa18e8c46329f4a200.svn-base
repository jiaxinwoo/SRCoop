package com.srcoop.android.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.srcoop.android.activity.util.NetworkChecker;
import com.srcoop.android.activity.view.HandyTextView;

public class LoginActivity extends Activity implements View.OnClickListener {

	private Button Login_button_login;
	private Button Login_button_register;
	private EditText Login_edittext_account;
	private EditText login_edittext_password;
	private LinearLayout Login_layout_loginboard;
	private ImageView Logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Bmob.initialize(this, "7e6af49a83e93afed01221eaf4fcf851");
		initiate();
		initialanimation();
		addlistener();
	}

	public void initiate() {
		Login_button_login = (Button) findViewById(R.id.login_button_login);
		Login_button_register = (Button) findViewById(R.id.login_button_register);
		Login_edittext_account = (EditText) findViewById(R.id.login_edittext_account);
		login_edittext_password = (EditText) findViewById(R.id.login_edittext_password);
		Logo = (ImageView) findViewById(R.id.logo);
		Login_layout_loginboard = (LinearLayout) findViewById(R.id.login_layout_loginboard);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.login_button_login) {
			NetworkChecker networkerchecker=new NetworkChecker(LoginActivity.this);
			
			final String mAccount = Login_edittext_account.getText().toString()
					.toLowerCase()
					+ "";
			final String mPassword = login_edittext_password.getText().toString()
					+ "";

			if(mAccount=="" || mPassword=="")
			{
				showCustomToast("账号密码不可为空！");
			}
			else if(!networkerchecker.isNetworkAvailable())
			{
				showCustomToast("当前无可用网络");
			}
			else
			{
				BmobQuery<BmobUser> query_user=new BmobQuery<BmobUser>();				
				query_user.addWhereEqualTo("username",mAccount);				
				query_user.findObjects(this, new FindListener<BmobUser>() {
	
	
					@Override
				    public void onSuccess(List<BmobUser> objects) 
					{
						if(objects.size()==0)
						{
							showCustomToast("用户不存在！");
							
						}
						else
						{
							if(	objects.get(0).getEmailVerified() == false)
							{
								showCustomToast("请先进行邮箱验证Orz");
								
							}
							else
							{
								showCustomToast("正待登录请稍后...");
								final BmobUser user = new BmobUser();
								user.setUsername(mAccount);
								user.setPassword(mPassword);
								user.login(LoginActivity.this, new SaveListener() {
									@Override
									public void onSuccess() {
								         showCustomToast("登录成功");
										Intent intent = new Intent();
										intent.setClass(LoginActivity.this,
												SrcoopActivity.class);
										startActivity(intent);
									}
	
									@Override
									public void onFailure(int code, String msg) {
										showCustomToast("登录失败,密码错误");
									}
								});
							}
							
						}

				    }
	
					@Override
					public void onError(int arg0, String arg1)
					{
						showCustomToast("未知错误！");
					}
	
				});
			}
			
		
			/*************************/
			/*************************/
			/*************************/
			/*************************/
			/*************************/
		}
		else if (view.getId() == R.id.login_button_register) 
		{

			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
		}

	}

	public void addlistener() {
		Login_button_login.setOnClickListener(this);
		Login_button_register.setOnClickListener(this);

	}

	public void initialanimation() {
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1.1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ScaleAnimation scaleAnimation_1 = new ScaleAnimation(0, 1.1f, 0, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		scaleAnimation.setDuration(1000);
		scaleAnimation_1.setDuration(300);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(scaleAnimation_1);
		Logo.startAnimation(animationSet);

		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setDuration(1000);
		Login_layout_loginboard.startAnimation(translateAnimation);

	}
	
	/** 显示自定义Toast提示(来自String) **/
	private void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
}
