package com.srcoop.android.activity.fragment;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.IssueActivity.onNavChangedListener;
import com.srcoop.android.activity.IssueTaskActivity;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Task;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.view.HandyTextView;

/**
 * @author SS 导航栏对应的任务模块
 */
public class IssueTaskFragment extends MyFragment implements
		onNavChangedListener {

	public static final String TASK_TAG = "com.srcoop.android.activity.fragment.IssueTaskListFragment";

	private ListView mListView;
	private TextView mIssueName;

	private Issue issue;
	private Task task;
	private LinkedList<Task> mTaskList = new LinkedList<Task>();
	private TaskAdapter listAdapter;

	private EditTextDialog mAddTaskDialog;
	private BaseDialog mBaseDialog;
	private FlippingLoadingDialog mWaitingAddTaskDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BmobQuery<Task> query = new BmobQuery<Task>();
		Intent intent = getActivity().getIntent();
		issue = (Issue) intent.getSerializableExtra(HomeFragment.ISSUE_TAG);
		Bundle bundle = intent.getExtras();
		if (bundle.get("currentIssue") != null) {
			issue = (Issue) bundle.get("currentIssue");
		}
		// query.addWhereEqualTo("mIssue", issue.getObjectId());
		query.addWhereRelatedTo("mTasks", new BmobPointer(issue));
		query.findObjects(getActivity(), new FindListener<Task>() {

			@Override
			public void onSuccess(List<Task> tasks) {
				for (Task t : tasks)
					mTaskList.addFirst(t);
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mWaitingAddTaskDialog = new FlippingLoadingDialog(getActivity(),
				"添加任务中，请稍后...");

		View v = inflater.inflate(R.layout.temp_tasklist, null);

		mBaseDialog = new BaseDialog(getActivity());
		
		mAddTaskDialog = new EditTextDialog(getActivity());
		mAddTaskDialog.setTitle("添加任务");
		mAddTaskDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddTaskDialog.setHint("输入任务名");
		mAddTaskDialog.setButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String taskName = mAddTaskDialog.getText();
				if (taskName == null) {
					mAddTaskDialog.requestFocus();
					showCustomToast("任务名不能为空");
				} else {
					mAddTaskDialog.dismiss();
					mAddTaskDialog.setTextNull();
					new addTaskThread().execute(taskName);
				}
			}
		}, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAddTaskDialog.cancel();
				mAddTaskDialog.setTextNull();
			}
		});
		mIssueName = (TextView) getActivity().findViewById(
				R.id.issue_activity_title);

		// TODO 用Bundle saveIntanceState保存issue，否则issue会报空指针异常
		mIssueName.setText(issue.getIssueName());

		// 如果你的布局包含了ListView，请在下面这条语句修改
		// 如R.id.yourListViewId
		// 否则不需要改动
		mListView = (ListView) v.findViewById(R.id.temp_tasklist);

		// 下面的代码不需要改动
		listAdapter = new TaskAdapter(getActivity(), mTaskList);
		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						IssueTaskActivity.class);
				Task task = mTaskList.get(position);
				intent.putExtra(TASK_TAG, task);
				startActivity(intent);
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				deleteTask(position);
				return true;
			}
		});
		return v;
	}

	private void deleteTask(final int position){
		task = mTaskList.get(position);
		if(BmobUser.getCurrentUser(getActivity(),MyUser.class).getType()==0){
			mBaseDialog.setTitle("提示");
			mBaseDialog.setMessage("确认删除任务？删除后与任务相关的资料将全被删除");
			mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//删除任务
					mBaseDialog.dismiss();
					BmobRelation issueTask  = new BmobRelation();
					issueTask.remove(task);
					issue.setTasks(issueTask);
					issue.update(getActivity(),new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							task.delete(getActivity(),new DeleteListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									showCustomToast("删除任务成功");
									mTaskList.remove(position);
									listAdapter.notifyDataSetChanged();
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									
								}
							});
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			});
			mBaseDialog.setButton2("取消",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mBaseDialog.cancel();
				}
			});
			mBaseDialog.setButton1Background(R.drawable.btn_default_popsubmit);
			mBaseDialog.show();
		}else{
			showCustomToast("不是任务的创建者，不可以删除任务");
		}
	}
	private void addTask(String taskName) {
		task = new Task();
		task.setTaskName(taskName);
		BmobDate date = new BmobDate(new Date());
		task.setTaskCreateTime(date);
		task.setIssue(issue);
		task.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				addTaskToIssue();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加任务失败");
			}
		});
	}

	private void addTaskToIssue() {
		BmobRelation mTasks = new BmobRelation();
		mTasks.add(task);
		issue.setTasks(mTasks);
		issue.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				mTaskList.addFirst(task);
				listAdapter.notifyDataSetChanged();
				showCustomToast("添加任务成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加任务失败");
			}
		});
	}

	/** 显示自定义Toast提示(来自String) **/
	private void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(getActivity()).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(getActivity());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	private class addTaskThread extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mWaitingAddTaskDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				addTask(params[0]);
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mWaitingAddTaskDialog.dismiss();
		}
	}

	private class TaskAdapter extends ArrayAdapter<Task> {

		public TaskAdapter(Context context, List<Task> objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.task_item, parent, false);
			}

			TextView mTvTaskName = (TextView) convertView
					.findViewById(R.id.tv_task_name);
			mTvTaskName.setText(getItem(position).getTaskName());

			TextView mTvTaskDesc = (TextView) convertView
					.findViewById(R.id.tv_task_desc);
			mTvTaskDesc.setText(getItem(position).getTaskContent());

			return convertView;
		}

	}

	@Override
	public void changeAddBtn() {
		mAddTaskDialog.show();
	}
}
