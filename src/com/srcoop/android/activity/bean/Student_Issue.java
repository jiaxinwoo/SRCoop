package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;

public class Student_Issue extends BmobObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Student mStudent;
	private Issue mIssue;
	
	public Student getStudent()
	{return mStudent;}
	
	public void setStudent(Student student)
	{this.mStudent=student;}
	
	public Issue getIssue()
	{return mIssue;}
	
	public void setIssue(Issue issue)
	{this.mIssue=issue;}
	
    @Override
    public String toString()
    {return "";}

}
