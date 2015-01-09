package com.srcoop.android.activity.bean;

import cn.bmob.v3.datatype.BmobRelation;

public class Teacher extends MyUser {

	private static final long serialVersionUID = 1L;
	public static final String TAG = "Teacher";
	
	private BmobRelation mIssues;
	private BmobRelation mProbles;
	
	public BmobRelation getIssues() {return mIssues;}
	public void setIssues(BmobRelation Issues) {this.mIssues = Issues;}

	public BmobRelation getProblems() {return mProbles;}
	public void setProblems(BmobRelation Probles) {this.mProbles = Probles;}
	
    @Override
    public String toString()
    {return "";}
}
