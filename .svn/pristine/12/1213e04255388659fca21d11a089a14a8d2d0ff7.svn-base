package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class File extends BmobObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mName;
	private BmobFile mFile;
	private BmobObject mParent;
	private String mFilePath;
	private String mFileUrl;
	
	private Issue mIssue;
	
	
	//对name（文件名）的操作
	public String getName(){return mName;}	
	public void setName(String name){this.mName=name;}
	
	//对file（文件路径）的操作
	public BmobFile getannex(){return mFile;}	
	public void setannex(BmobFile file){this.mFile=file;}
	
	//对parent（文件路径）的操作
	public BmobObject getParent(){return mParent;}
	public void setparent(BmobObject parent){this.mParent=parent;}
	
	//对issue的操作
    public Issue getIssue() {
		return mIssue;
	}
	public void setIssue(Issue issue) {
		mIssue = issue;
	}
	
	
    
  //对filePath 和 fileUrl的操作
  	public String getFilePath() {
  		return mFilePath;
  	}
  	public void setFilePath(String FilePath) {
  		this.mFilePath = FilePath;
  	}
  	
  	public String getFileUrl() {
		return mFileUrl;
	}
	public void setFileUrl(String FileUrl) {
		this.mFileUrl = FileUrl;
	}
}
