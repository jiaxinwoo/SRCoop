package com.srcoop.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Problem;

@SuppressWarnings("unused")
public class ProblemDisplay extends Activity implements View.OnClickListener{

	private MyUser myuser;
    private int userType=0;//1=student;0=teacher
    private int avtivityState=0;
    private Button problemsDisplay_button_back;
    private Button problemsDisplay_button_edit;
    private EditText problemsDisplay_input_title;
    private EditText problemsDisplay_input_depict;
    private EditText problemsDisplay_input_answer;
    private TextView problemsDisplay_textview_time;
	private TextView problemsDisplay_textview_from;
    private ScrollView problemsDisplay_scrollview_depict;
    private ScrollView problemsDisplay_scrollview_answer;
    private String title;
    private String description;
    private String answer;
    private String time;
    private String from;
    private String myUserId;
    
    private Problem mProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_display);
		mProblem = (Problem) getIntent().getSerializableExtra(ProblemsList.PROBLEM_TAG);
    	myuser=BmobUser.getCurrentUser(this,MyUser.class);
    	myUserId=myuser.getObjectId();

		initiate();
    	getInfo();

        problemsDisplay_button_back.setOnClickListener(this);
        problemsDisplay_button_edit.setOnClickListener(this);
    }
    
    private void getInfo()
    {
      BmobQuery<Problem> query=new BmobQuery<Problem>();
      query.getObject(ProblemDisplay.this, mProblem.getObjectId(), new GetListener<Problem>() {

			@Override
          public void onSuccess(Problem object) {
              // TODO Auto-generated method stub

		    	if(myUserId==object.getStudent().getObjectId())
		    	{
		    		userType=0;
		    		
		    	}
		    	else
		    	{
		    		userType=1;
		    	}
			
            title=  object.gettitle();
            description= object.getdescription();
            answer= object.getanswer();
        //  String mfrom=problemsDisplay_textview_from.getText().toString()+object.getStudent().getName().toString();
            time=problemsDisplay_textview_time.getText().toString()+object.getCreatedAt().toString();
            
            from=problemsDisplay_textview_from.getText().toString()+object.getStudent().getName().toString();
     	  	problemsDisplay_input_title.setText(title);
    	  	problemsDisplay_input_depict.setText(description);
    	  	problemsDisplay_input_answer.setText(answer);
    		problemsDisplay_textview_time.setText(from);
    	  	problemsDisplay_textview_time.setText(time);  
          }

          @Override
          public void onFailure(int code, String arg0) {
              // TODO Auto-generated method stub
              Toast toast = Toast.makeText(ProblemDisplay.this,"查询失败", Toast.LENGTH_SHORT);
              toast.show();  
          }

      });
    	
    }
    
    
    private void initiate()
    {
		problemsDisplay_button_back=(Button)findViewById(R.id.problemsdisplay_button_back);
		problemsDisplay_button_edit=(Button)findViewById(R.id.problemsdisplay_button_edit);
        problemsDisplay_input_title=(EditText)findViewById(R.id.problemsdisplay_input_title);
        problemsDisplay_input_depict=(EditText)findViewById(R.id.problemsdisplay_input_depict);
        problemsDisplay_input_answer=(EditText)findViewById(R.id.problemsdisplay_input_answer);
        problemsDisplay_textview_time=(TextView)findViewById(R.id.problemsdisplay_textview_time);
        problemsDisplay_textview_from=(TextView)findViewById(R.id.problemsdisplay_textview_from);
        problemsDisplay_scrollview_depict=(ScrollView)findViewById(R.id.problemsdisplay_scrollview_depict);
        problemsDisplay_scrollview_answer=(ScrollView)findViewById(R.id.problemsdisplay_scrollview_answer);
    	
    }
    
    private void getProblemInfo()
    {
		title=problemsDisplay_input_title.getText().toString();
		description=problemsDisplay_input_depict.getText().toString();
		answer=problemsDisplay_input_answer.getText().toString();
    }
    
    private void setProblemUpdate()
    {
    	if(userType==1)
    	{
	    	Problem mProblem=new Problem();
	    	getProblemInfo();
			mProblem.settile(title);
			mProblem.setdescription(description);
			mProblem.update(ProblemDisplay.this,this.mProblem.getObjectId(), new UpdateListener() {
	
			    @Override
			    public void onSuccess() {
			        // TODO Auto-generated method stub
	                Toast toast = Toast.makeText(ProblemDisplay.this,"更新成功", Toast.LENGTH_SHORT);
	                toast.show();  
	                
	                avtivityState=0;
	                problemsDisplay_button_edit.setBackgroundResource(R.drawable.problemsdisplay_button_edit);
                    problemsDisplay_input_title.setBackgroundResource(R.drawable.problemsdisplay_input_title);
                    problemsDisplay_scrollview_depict.setBackgroundResource(R.drawable.problemsdisplay_input_answer);
                    problemsDisplay_input_title.setEnabled(false);
                    problemsDisplay_input_depict.setEnabled(false);               
				    }
			    
	
			    @Override
			    public void onFailure(int code, String msg) {
			        // TODO Auto-generated method stub
			         Toast toast = Toast.makeText(ProblemDisplay.this,"QNMB", Toast.LENGTH_SHORT);
		                toast.show();  
	
			    }
			});
    	}
    	else
    	{
	    	Problem mProblem=new Problem();
	    	getProblemInfo();
			mProblem.setanswer(answer+"");
			if(answer!="")
			{
				mProblem.setisAnswered(true);
			}
			else
			{
				mProblem.setisAnswered(false);
			}
			mProblem.update(ProblemDisplay.this,this.mProblem.getObjectId(), new UpdateListener() {
	
			    @Override
			    public void onSuccess() {
			        // TODO Auto-generated method stub
	                Toast toast = Toast.makeText(ProblemDisplay.this,"更新成功", Toast.LENGTH_SHORT);
	                toast.show();
	                avtivityState=0;
	                problemsDisplay_button_edit.setBackgroundResource(R.drawable.edit_button);       
	                
	                if(userType==1)
	                {
	                    problemsDisplay_input_title.setBackgroundResource(R.drawable.problemsdisplay_input_title);
	                    problemsDisplay_scrollview_depict.setBackgroundResource(R.drawable.problemsdisplay_input_depict);
	                    problemsDisplay_input_title.setEnabled(false);
	                    problemsDisplay_input_depict.setEnabled(false);
	                    
	                }
	                else
	                {
	                	problemsDisplay_scrollview_answer.setBackgroundResource(R.drawable.problemsdisplay_input_answer);
	                    problemsDisplay_input_answer.setEnabled(false);
	                }

			    }
			    
	
			    @Override
			    public void onFailure(int code, String msg) {
			        // TODO Auto-generated method stub
			         Toast toast = Toast.makeText(ProblemDisplay.this,"QNMB", Toast.LENGTH_SHORT);
		                toast.show();  
	
			    }
			});
    		
    	}
  	
    }
    


    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.problemsdisplay_button_back)
        {        
        	Intent intent = new Intent(ProblemDisplay.this, ProblemsList.class);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(intent);
        	finish();
        }
        else if(view.getId()==R.id.problemsdisplay_button_edit)
        {
            if(avtivityState==0)
            {
                avtivityState=1;
                problemsDisplay_button_edit.setBackgroundResource(R.drawable.confirm_button);
                if(userType==1)
                {
                    problemsDisplay_input_title.setBackgroundResource(R.drawable.problemsedit_input_title);
                    problemsDisplay_scrollview_depict.setBackgroundResource(R.drawable.problemsedit_input_depict);
                    problemsDisplay_input_title.setEnabled(true);
                    problemsDisplay_input_depict.setEnabled(true);
                    
                }
                else
                {
                	problemsDisplay_scrollview_answer.setBackgroundResource(R.drawable.problemsedit_input_answer);
                    problemsDisplay_input_answer.setEnabled(true);
                }
            }
            else
            {
                setProblemUpdate();

            }
        }
    }

}
