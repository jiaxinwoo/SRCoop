package com.srcoop.android.activity.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.IssueTaskActivity;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.adapter.AssignmentAdapter;
import com.srcoop.android.activity.bean.Assignment;
import com.srcoop.android.activity.bean.Stage;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.util.DateUtil;
import com.srcoop.android.activity.util.TimeThread;
import com.srcoop.android.activity.view.HandyTextView;

/**
 * @author SS 阶段任务模块
 */
public class AssignmentListFragment extends Fragment implements
		IssueTaskActivity.onListChangedListener, FragmentObserver,OnItemLongClickListener {

	/**
	 * @author SS 自定义接口，和IssueTaskActivity通信
	 */
	public interface onAssignmentListener {
		public void onAssignmentUpdate();

		public void onTimeUpdate(long timeMillis);
		
		public void onTimeDelete(long timeMillis);
	}

	private onAssignmentListener mAssignmentListener;

	public static final String EXTRA_ASSIGNMENT_ID = "com.srcoop.android.activity.fragment.AssignmentFragment_id";

	private ListView mAssignmentListView;// 阶段任务的listView

	private AssignmentAdapter mAdapter;// 阶段任务适配器
	private ArrayList<Assignment> mAssList = new ArrayList<Assignment>(); // 当前阶段对应所有阶段任务的结婚
	private List<Integer> mCurrent = new ArrayList<Integer>();

	private TimeThread<TextView> timeThread;// 管理时间的线程，自动在指定TextView上更新系统时间

	private BaseDialog mBaseDialog;
	private Stage mStage;// 当前阶段

	public static AssignmentListFragment newsIntance(Stage stage) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_ASSIGNMENT_ID, stage);

		AssignmentListFragment fragment = new AssignmentListFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBaseDialog = new BaseDialog(getActivity());
		timeThread = new TimeThread<TextView>(new Handler());
		timeThread.setListener(new TimeThread.Listener<TextView>() {

			@Override
			public void onTimeRunned(int id, String date) {
				if (isVisible()) {
					for (int i = 0; i < mAssList.size(); i++) {
						if (mCurrent.indexOf(i) >= 0)
							continue;
						if (mAssList != null && mAssList.get(i) != null
								&& mAssList.get(i).getIsFinished())
							continue;
						TextView textView = (TextView) mAssignmentListView
								.findViewWithTag(i);
						if (textView != null & date != null) {
							textView.setText(date);
						}
					}
				}
			}
		});
		timeThread.start();
		timeThread.getLooper();

		mStage = (Stage) getArguments().getSerializable(EXTRA_ASSIGNMENT_ID);
		queryCurrentStage();
		mAdapter = new AssignmentAdapter(getActivity(), mAssList, timeThread);
		mAdapter.registerObserver(this);
	}

	private void queryCurrentStage() {
		BmobQuery<Assignment> assignQuery = new BmobQuery<Assignment>();
		assignQuery.addWhereRelatedTo("mAssignments", new BmobPointer(mStage));
		assignQuery.findObjects(getActivity(), new FindListener<Assignment>() {

			@Override
			public void onSuccess(List<Assignment> list) {
				for (Assignment a : list) {
					mAssList.add(a);
				}
				updateUI();
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		View v = inflater.inflate(R.layout.fragment_assignment_layout,
				container, false);
		mAssignmentListView = (ListView) v
				.findViewById(R.id.assignment_listview);
		mAssignmentListView.setAdapter(mAdapter);
		//为阶段任务添加长按事件
		mAssignmentListView.setOnItemLongClickListener(this);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			mAssignmentListener = (onAssignmentListener) activity;
		} catch (Exception e) {
			throw new ClassCastException(activity.toString()
					+ " must implements onNewAssignmentAddedListener");
		}
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mAssignmentListener = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timeThread.quit();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		timeThread.clearTime();
	}

	public void updateUI() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListUpdate(Assignment assignment) {
		mAssList.add(assignment);
		mAssignmentListener.onAssignmentUpdate();
	}

	@Override
	public void update(int current) {
		mCurrent.add(current);
		Assignment currentAssignment = mAssList.get(current);
		Date currentDate = new Date();
		currentAssignment.setAssFinishTime(new BmobDate(currentDate));
		currentAssignment.setIsFinished(true);
		String startTime = currentAssignment.getAssStartTime().getDate();
		try {
			long time = currentDate.getTime() - DateUtil.getDate(startTime);
			mAssignmentListener.onTimeUpdate(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		currentAssignment.update(getActivity(), new UpdateListener() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0, String arg1) {
			}
		});
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int pos, long id) {
		final  Assignment assignment = mAssList.get(pos);
		mBaseDialog.setTitle("提示");
		mBaseDialog.setMessage("确认删除此阶段任务？");
		mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mBaseDialog.dismiss();
				long deleteTime = 0;
				//如果阶段任务为完成状态，减去对应的时间
				if(assignment.getIsFinished()){
					String endTime = assignment.getAssFinishTime().getDate();
					String startTime = assignment.getAssStartTime().getDate();
					try {
						deleteTime =  DateUtil.getDate(endTime) - DateUtil.getDate(startTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mAssignmentListener.onTimeDelete(deleteTime);
				}
				//删除指定的阶段任务
				assignment.delete(getActivity(), new DeleteListener(){

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						showCustomToast("删除阶段任务成功");
						mAssList.remove(pos);
						mAdapter.notifyDataSetChanged();
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
		return true;
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

}
