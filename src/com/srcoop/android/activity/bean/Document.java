package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Document extends BmobObject {
	private static final long serialVersionUID = 1L;
	private String mName;
	private String mType;
	private String mSize;
	private Files mFiles;
	
	//@junsheng use it 
	private BmobFile mDocument;
	private String mFileUrl;
	private String mFileName;
	
    public String getFileUrl() {
		return mFileUrl;
	}

	public void setFileUrl(String mFileUrl) {
		this.mFileUrl = mFileUrl;
	}

	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String mFileName) {
		this.mFileName = mFileName;
	}

	@Override
    public String toString()
    {return "";}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getSize() {
		return mSize;
	}

	public void setSize(String size) {
		mSize = size;
	}

	public BmobFile getDocument() {
		return mDocument;
	}

	public void setDocument(BmobFile document) {
		mDocument = document;
	}

	public Files getFiles() {
		return mFiles;
	}

	public void setFiles(Files files) {
		mFiles = files;
	}

}