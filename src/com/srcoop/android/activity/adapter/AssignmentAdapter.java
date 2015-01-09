package com.srcoop.android.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobDate;

import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.Assignment;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.fragment.FragmentObserver;
import com.srcoop.android.activity.util.TimeThread;

public class AssignmentAdapter extends BaseAdapter implements AdapterSubject {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Assignment> mAssList;
	private TimeThread<TextView> mTimeThread;

	private List<FragmentObserver> observers = new ArrayList<FragmentObserver>();
	private boolean isFinishCheched = false;

	private int current = -1;

	public AssignmentAdapter(Context context, ArrayList<Assignment> data,
			TimeThread<TextView> timeThread) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAssList = data;
		mTimeThread = timeThread;

	}

	@Override
	public int getCount() {
		return mAssList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAssList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.assignment_item, parent,
					false);
			holder.mTvAssName = (TextView) convertView
					.findViewById(R.id.tv_assignment_name);
			holder.mTvAssDesc = (TextView) convertView
					.findViewById(R.id.tv_assignment_desc);
			holder.mTvAssStartTime = (TextView) convertView
					.findViewById(R.id.tv_assignment_start_time);
			holder.mTvAssFinishTime = (TextView) convertView
					.findViewById(R.id.tv_assignment_finish_time);
			holder.mCbIsFinished = (CheckBox) convertView
					.findViewById(R.id.cb_assignment_finish);
			holder.mTvIsFinished = (TextView) convertView
					.findViewById(R.id.tv_assignment_finish);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Assignment assignment = mAssList.get(position);
		holder.mTvAssName.setText(assignment.getAssName());

		holder.mTvAssDesc.setText(assignment.getAssRequirement());

		BmobDate date = assignment.getAssStartTime();
		holder.mTvAssStartTime.setText(date.getDate());

		final int checkedColor = mContext.getResources().getColor(
				R.color.assignment_checked_color);
		final int unCheckedColor = mContext.getResources().getColor(
				R.color.assignment_unchecked_color);

		boolean checked = assignment.getIsFinished();
		holder.mCbIsFinished.setChecked(checked);
		holder.mCbIsFinished.setEnabled(!checked);
		holder.mTvIsFinished.setTextColor(checked ? checkedColor
				: unCheckedColor);
		holder.mTvIsFinished.setText(checked ? "已完成" : "未完成");

		holder.mCbIsFinished.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isFinishCheched) {
					isFinishCheched = true;
					getFinishCheckDialog().show();
				}
				if (holder.mCbIsFinished.isChecked()) {
					holder.mCbIsFinished.setEnabled(false);
					holder.mTvIsFinished.setText("已完成");
					holder.mTvIsFinished.setTextColor(checkedColor);
					current = position;
					notifyObserver();
				}
			}
		});

		holder.mTvAssFinishTime.setTag(position);
		if (checked) {
			holder.mTvAssFinishTime.setText(assignment.getAssFinishTime()
					.getDate());
		} else {
			mTimeThread.onTimeRunner(holder.mTvAssFinishTime, position);
		}

		//设置长按事件
		convertView.setLongClickable(true);
		return convertView;
	}

	static class ViewHolder {
		TextView mTvAssName;
		TextView mTvAssDesc;
		TextView mTvAssStartTime;
		TextView mTvAssFinishTime;
		CheckBox mCbIsFinished;
		TextView mTvIsFinished;
	}

	private BaseDialog getFinishCheckDialog() {
		BaseDialog mFinishCheckDialog = BaseDialog.getDialog(mContext, "提示",
				"完成当前阶段任务后将不能撤销", "确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		return mFinishCheckDialog;
	}

	@Override
	public void registerObserver(FragmentObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(FragmentObserver observer) {
		int i = observers.indexOf(observer);
		if (i >= 0)
			observers.remove(i);
	}

	@Override
	public void notifyObserver() {
		for (int i = 0; i < observers.size(); i++) {
			FragmentObserver o = observers.get(i);
			o.update(current);
		}
	}

}
