package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class Issue extends BmobObject {

	private static final long serialVersionUID = 1L;

	public static final String TAG = "Issue";

	private String mIssueName;
	private String mIssueContent;
	private BmobDate mIssueCreateTime;

	private Teacher mTeacher;

	private BmobRelation mIssue_Student;
	private BmobRelation mTasks;
	private BmobRelation mAchievements;
	private BmobRelation mFiles;
	private BmobRelation mFolders;

	public String getIssueName() {
		return mIssueName;
	}

	public void setIssueName(String issueName) {
		mIssueName = issueName;
	}

	public String getIssueContent() {
		return mIssueContent;
	}

	public void setIssueContent(String issueContent) {
		mIssueContent = issueContent;
	}

	public BmobDate getIssueCreateTime() {
		return mIssueCreateTime;
	}

	public void setIssueCreateTime(BmobDate issueCreateTime) {
		mIssueCreateTime = issueCreateTime;
	}

	public Teacher getTeacher() {
		return mTeacher;
	}

	public void setTeacher(Teacher teacher) {
		mTeacher = teacher;
	}

	public BmobRelation getIssue_Student() {
		return mIssue_Student;
	}

	public void setIssue_Student(BmobRelation Issue_student) {
		mIssue_Student = Issue_student;
	}

	public BmobRelation getTasks() {
		return mTasks;
	}

	public void setTasks(BmobRelation tasks) {
		mTasks = tasks;
	}

	public BmobRelation getAchievements() {
		return mAchievements;
	}

	public void setAchievements(BmobRelation achievements) {
		mAchievements = achievements;
	}

	public BmobRelation getFiles() {
		return mFiles;
	}

	public void setFiles(BmobRelation files) {
		mFiles = files;
	}

	public BmobRelation getFolders() {
		return mFolders;
	}

	public void setFolders(BmobRelation folders) {
		mFolders = folders;
	}

	@Override
	public String toString() {
		return mIssueName;
	}
}
