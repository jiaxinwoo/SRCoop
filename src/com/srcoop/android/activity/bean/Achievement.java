package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Achievement extends BmobObject{
	/**
	 * modify by junsheng
	 */
	private static final long serialVersionUID = 1L;
	private String mTitle;
	private String mKeywords[];
	private String mFirstAuthor;
	private String mOtherAuthor;
	private String mPublishArea;
	private String mAbstract;
	private String mFileUrl;
	private String mFilePath;

	private BmobRelation mUser_Achievement;
	
	private Issue mIssue;
	
	public BmobRelation getUser_Achievement(){return mUser_Achievement;}
	public void setUser_Achievement(BmobRelation user_achievement){this.mUser_Achievement=user_achievement;}
	
	//对title的操作
	public String getTitle(){return mTitle;}
	public void setTile(String title){this.mTitle=title;}
	
	//对keywords的操作
	public String[] getKeywords(){return mKeywords;}
	public void setKeywords(String keywords[]){this.mKeywords=keywords;}
	
	//对publishArea（ 出版地）的操作
	public String getPublishArea(){return mPublishArea;}
	public void setPublishArea(String publishArea){this.mPublishArea=publishArea;}

	//对abstract的操作
	public String getAbstract(){return mAbstract;}
    public void setAbstract(String _abstract){this.mAbstract=_abstract;}
    
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
	
	//对作者进行操作
	public String getFirstAuthor() {
		return mFirstAuthor;
	}
	public void setFirstAuthor(String FirstAuthor) {
		this.mFirstAuthor = FirstAuthor;
	}
	public String getOtherAuthor() {
		return mOtherAuthor;
	}
	public void setOtherAuthor(String OtherAuthor) {
		this.mOtherAuthor = OtherAuthor;
	}
	
    
    @Override
    public String toString()
    {return "";}

}
