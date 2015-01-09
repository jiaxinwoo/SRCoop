package com.srcoop.android.activity.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

public class Stage extends BmobObject {

	private static final long serialVersionUID = 1L;

	public static final String TAG = "Stage";

	private String mStageName;
	private String mStageDesc;
	private int mStageFinishTime = 0;
	private BmobDate mWorkTime;
	private BmobDate mCreateTime;

	private Task mTask;
	private BmobRelation mAssignments;

	public BmobRelation getAssignments() {
		return mAssignments;
	}

	public void setAssignments(BmobRelation assignments) {
		this.mAssignments = assignments;
	}

	public Task getTask() {
		return mTask;
	}

	public void setTask(Task task) {
		mTask = task;
	}

	public String getStageName() {
		return mStageName;
	}

	public void setStageName(String stageName) {
		mStageName = stageName;
	}

	public String getStageDesc() {
		return mStageDesc;
	}

	public void setStageDesc(String stageDesc) {
		mStageDesc = stageDesc;
	}

	public int getStageFinishTime() {
		return mStageFinishTime;
	}

	public void setStageFinishTime(int stageFinishTime) {
		mStageFinishTime = stageFinishTime;
	}

	public BmobDate getWorkTime() {
		return mWorkTime;
	}

	public void setWorkTime(BmobDate workTime) {
		mWorkTime = workTime;
	}

	public BmobDate getCreateTime() {
		return mCreateTime;
	}

	public void setCreateTime(BmobDate createTIme) {
		mCreateTime = createTIme;
	}

	@Override
	public String toString() {
		return mStageName;
	}
	
}
