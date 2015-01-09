package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Files extends BmobObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	public static final String FILE = "File";

	private String mName;
	private Issue mIssue;
	private BmobRelation mDocuments;
	
	
	//对name（文件名）的操作
	public String getName(){return mName;}	
	public void setName(String name){this.mName=name;}
	
	//对issue的操作
    public Issue getIssue() {
		return mIssue;
	}
	public void setIssue(Issue issue) {
		mIssue = issue;
	}
	public void setDocuments(BmobRelation documents) {
		mDocuments = documents;
	}
	
	public BmobRelation getDocuments() {
		return mDocuments;
	}
	
}
