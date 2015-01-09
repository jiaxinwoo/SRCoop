package com.srcoop.android.activity;

import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.srcoop.android.activity.bean.Student;
import com.srcoop.android.activity.bean.Teacher;
import com.srcoop.android.activity.util.NetworkChecker;
import com.srcoop.android.activity.view.HandyTextView;

@SuppressWarnings("unused")
public class RegisterActivity extends Activity implements View.OnClickListener,EditText.OnFocusChangeListener{



    private EditText Register_edittext_email;

    private EditText Register_edittext_password;
    private EditText Register_edittext_password2;
    private Button Register_button_student;
    private Button Register_button_teacher;
    private Button Register_button_finish;
    private Button Register_button_back;
    

    private String email;
    private String password;
    private String password2;
    private int type=1;
	private int IfExist_S=1;
	private int IfExist_T=1;
    //状态，用来验证password和email格式是否合格，合格为1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initiate();
        addlistener();
    }
    
    

    public void initiate()
    {

          Register_edittext_email=(EditText)findViewById(R.id.register_edittext_email);
          Register_edittext_password=(EditText)findViewById(R.id.register_edittext_password);
          Register_edittext_password2=(EditText)findViewById(R.id.register_edittext_password2);
          Register_button_student=(Button)findViewById(R.id.register_button_student);
          Register_button_teacher=(Button)findViewById(R.id.register_button_teacher);
          Register_button_finish=(Button)findViewById(R.id.register_button_finish);
          Register_button_back=(Button)findViewById(R.id.register_button_back);
          
    }
    
    public void addlistener(){

        Register_edittext_email.setOnFocusChangeListener(this);
        Register_edittext_password.setOnFocusChangeListener(this);
        Register_edittext_password2.setOnFocusChangeListener(this);
        Register_button_student.setOnClickListener(this);
        Register_button_teacher.setOnClickListener(this);
        Register_button_finish.setOnClickListener(this);
        Register_button_back.setOnClickListener(this);
    }


    @SuppressLint("DefaultLocale") @Override
    public void onClick(View view) {
        if(view.getId()==R.id.register_button_student)
        {
        	type=1;      	
        	Register_button_student.setBackgroundResource(R.drawable.register_button_selectedtype);
        	Register_button_student.setTextColor(Color.parseColor("#825a12"));
        	Register_button_teacher.setBackgroundResource(R.drawable.register_button_type);
        	Register_button_teacher.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(view.getId()==R.id.register_button_teacher)
        {
        	type=0;
        	Register_button_student.setBackgroundResource(R.drawable.register_button_type);
        	Register_button_student.setTextColor(Color.parseColor("#ffffff"));
        	Register_button_teacher.setBackgroundResource(R.drawable.register_button_selectedtype);
        	Register_button_teacher.setTextColor(Color.parseColor("#825a12"));
        }
        else if(view.getId()==R.id.register_button_finish)
        {
        	NetworkChecker networkerchecker=new NetworkChecker(RegisterActivity.this);
        	 email=Register_edittext_email.getText().toString().toLowerCase();
        	 password=Register_edittext_password.getText().toString();
        	 password2=Register_edittext_password2.getText().toString();
        	 //检查网络是否可用
        	 if(networkerchecker.isNetworkAvailable())
        	 {
        		//检查表格是否有未填项目
	        	if((!email.equals(""))  &&(!password.equals("")) && (!password2.equals("")))
	        	{
	        		//检查邮箱地址是否正确
	        		if(Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", email))
	        		{
	        				//检查两次密码是否一致
		        			if(Register_edittext_password.getText().toString().equals(Register_edittext_password2.getText().toString()))
		        			{
		        				showCustomToast("正在注册");
		        				register();	
		        			}
		        			else
		        			{
		        				showCustomToast("两次输入密码必须一致");
		         
		        			}
	        			
	        		}
	        		else
	        		{
	        			showCustomToast(" 邮箱格式输入不正确");
	        		}
	        		
	        	}
	        	else
	        	{
	        		showCustomToast(" 请完成表格，输入不可为空");
	        	}
        	}
        	 else
        	 {
        		 showCustomToast("无可用网络！");

        	 }
        	
        	
        	

        }
        else if(view.getId()==R.id.register_button_back)
        {
        	RegisterActivity.this.finish();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus){}
        else{
            if(view.getId()==R.id.register_edittext_email){}
            else if(view.getId()==R.id.register_edittext_password){
            	
            	 if((!Register_edittext_password.getText().toString().equals(Register_edittext_password2.getText().toString()))&& (!Register_edittext_password.getText().toString().equals("")) && (!Register_edittext_password2.getText().toString().equals("")))
                 {
                 	showCustomToast("两次输入密码必须一致");
                 	}
            	
            }
            else if(view.getId()==R.id.register_edittext_password2){
            	
           	 if((!Register_edittext_password.getText().toString().equals(Register_edittext_password2.getText().toString()))&& (!Register_edittext_password.getText().toString().equals("")) && (!Register_edittext_password2.getText().toString().equals("")))
                {
                	showCustomToast("两次输入密码必须一致");
                	}
            }
        }

    }
    
    public void register()
    {
		if(type==1)
    	{
      		final Student student=new Student();
      		student.setUsername(email);
      		student.setEmail(email);
      		student.setType(type);
      		student.setPassword(password);   
    		student.signUp(this, new SaveListener() {

    		    @Override
    		    public void onSuccess() {
    		        // TODO Auto-generated method stub
			         Toast toast = Toast.makeText(RegisterActivity.this,"注册成功!", Toast.LENGTH_SHORT);
		             toast.show();  
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this,LoginActivity.class);
						startActivity(intent);
                 	RegisterActivity.this.finish();
    		    }

    		    @Override
    		    public void onFailure(int code, String arg0) {
    		        // TODO Auto-generated method stub
    		    	showCustomToast("杯具，邮箱已被注册!");
    		        // 添加失败
    		    }
    		});
    	}
    	else{
      		final Teacher teacher=new Teacher();
      		teacher.setUsername(email);
      		teacher.setEmail(email);
      		teacher.setPassword(password);  
    		teacher.signUp(this, new SaveListener() {

    		    @Override
    		    public void onSuccess() {
    		        // TODO Auto-generated method stub
			         Toast toast = Toast.makeText(RegisterActivity.this,"注册成功!", Toast.LENGTH_SHORT);
		             toast.show();
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this,LoginActivity.class);
					startActivity(intent);
                    RegisterActivity.this.finish();
    		    }

    		    @Override
    		    public void onFailure(int code, String arg0) {
    		        // TODO Auto-generated method stub
    		    	showCustomToast("杯具，邮箱已被注册!");
    		        // 添加失败
    		    }
    		});
    	}
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
