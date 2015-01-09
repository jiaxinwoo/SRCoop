package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;

public class Problem extends BmobObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String description;
	private String answer;
	private Boolean isAnswered;
	
	private Student mStudent;	
	private Task mTask;
	
	public Task getTask() {return mTask;}
	public void setTask(Task task) {this.mTask = task;}
	
	public String gettitle(){return title;}	
	public void settile(String title){this.title=title;}
	
	public String getdescription(){return description;}
	public void setdescription(String description){this.description=description;}
	
	public String getanswer(){return answer;}	
	public void setanswer(String answer){this.answer=answer;}
	
	public Boolean getisAnswered(){return isAnswered;}
	public void setisAnswered(Boolean isAnswered){this.isAnswered=isAnswered;}
	
	public Student getStudent(){return mStudent;}
	public void setStudent(Student student){this.mStudent=student;}
	
    @Override
    public String toString()
    {return "";}

}
