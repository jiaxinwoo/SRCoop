package com.srcoop.android.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.ProblemDisplay;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.adapter.SpecialAdapter;
import com.srcoop.android.activity.bean.Problem;
import com.srcoop.android.activity.bean.Task;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.view.HandyTextView;

public class ProblemListFragment  extends Fragment {

	public static final String PROBLEM_TAG = "com.srcoop.android.activity.fragment.ProblemListFragment";

	private ListView mListView_Unanswered;
	private ListView mListView_Answered;
	private Button mAddProblemBtn;
	private TextView mTaskName;

	private Task task;
	private Problem problem;
	private Problem Problem_toDisplay;
	
	private List<Map<String,Object>> mUnansweredProblemList = new ArrayList<Map<String,Object>>();
	private List<Map<String,Object>> mAnsweredProblemList = new ArrayList<Map<String,Object>>();
	private SpecialAdapter listAdapter_UnAnsweredProblem;
	private SpecialAdapter listAdapter_AnsweredProblem;

	private EditTextDialog mAddProblemDialog;
	private FlippingLoadingDialog mWaitingAddProblemDialog;
	
    private List<Map<String,Object>> addData_unansweredproblems(String title,String descrition,String from,String id)
    {
        for(int i=0;i<2;i++)
        {
            Map<String,Object>map=new HashMap<String,Object>();
            map.put("title",title);
            map.put("desciption",descrition);
            map.put("from",from);
            map.put("id", id);
            mUnansweredProblemList.add(map);
        }
        return mUnansweredProblemList;
    }

    private List<Map<String,Object>> addData_answeredproblems(String title,String descrition,String from,String Answer,String id)
    {
            Map<String,Object>map=new HashMap<String,Object>();
            map.put("title",title);
            map.put("desciption",descrition);
            map.put("from",from);
            map.put("answer",Answer);
            map.put("id", id);
            mAnsweredProblemList.add(map);
            return mAnsweredProblemList;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		task = (Task) getActivity().getIntent().getSerializableExtra(IssueTaskFragment.TASK_TAG);
		//*************************查找未回答问题***************************************
		BmobQuery<Problem> Un_query = new BmobQuery<Problem>();		
		
		Un_query.addWhereRelatedTo("mProblems", new BmobPointer(task));
		Un_query.addWhereEqualTo("isAnswered",false);
		Un_query.findObjects(getActivity(), new FindListener<Problem>() {

			@Override
			public void onSuccess(List<Problem> problems)
			{
				for (Problem t : problems)
					addData_unansweredproblems(t.gettitle(),t.getdescription(),t.getStudent().getName(),t.getObjectId());
				    listAdapter_UnAnsweredProblem.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1)
			{}
		});
		//*************************查找未回答问题***************************************
		//*************************查找已回答问题***************************************
		BmobQuery<Problem> query = new BmobQuery<Problem>();		
		
		query.addWhereRelatedTo("mProblems", new BmobPointer(task));
		query.addWhereEqualTo("isAnswered",true);

		query.findObjects(getActivity(), new FindListener<Problem>() {

			@Override
			public void onSuccess(List<Problem> problems)
			{
				for (Problem t : problems)
					addData_answeredproblems(t.gettitle(),t.getdescription(),t.getStudent().getName(),t.getanswer(),t.getObjectId());
				    listAdapter_AnsweredProblem.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1)
			{}
		});
		//*************************查找已回答问题***************************************
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		mWaitingAddProblemDialog = new FlippingLoadingDialog(getActivity(),"添加问题中，请稍后...");
		
		// 林康华，请在View v = inflater.inflate(R.layout.temp_tasklist, null);中
		// 修改你的布局。如:R.layout.yourLayoutName
		View v = inflater.inflate(R.layout.temp_tasklist, null);
		mAddProblemDialog = new EditTextDialog(getActivity());
		mAddProblemDialog.setTitle("提问问题");
		mAddProblemDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddProblemDialog.setHint("输入问题标题");
		
		//******************************************************************
		mAddProblemDialog.setButton("确认", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String taskName = mAddProblemDialog.getText();
				
				if (taskName == null)
				{
					mAddProblemDialog.requestFocus();
					showCustomToast("问题标题不能为空");
				} 
				else 
				{
					mAddProblemDialog.dismiss();
					mAddProblemDialog.setTextNull();
					new addTaskThread().execute(taskName);
				}
			}
		}
		, "取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				mAddProblemDialog.cancel();
				mAddProblemDialog.setTextNull();
			}
		});		
		//******************************************************************
		
		mAddProblemBtn = (Button) getActivity().findViewById(R.id.problemslist_image_addproblems);
		mAddProblemBtn.setVisibility(View.VISIBLE);
		mAddProblemBtn.setBackgroundResource(R.drawable.problemslist_image_addproblems);
		
		//*****************************************************************
		mAddProblemBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mAddProblemDialog.show();
			}
		});
		//*****************************************************************
		
		mTaskName = (TextView) getActivity().findViewById(R.id.tv_task_title);
		// TODO 用Bundle saveIntanceState保存issue，否则issue会报空指针异常
		mTaskName.setText(task.getTaskName());
		// 如果你的布局包含了ListView，请在下面这条语句修改
		// 如R.id.yourListViewId
		// 否则不需要改动
		mListView_Unanswered = (ListView) v.findViewById(R.id.problemslist_layout_unansweredlistview);
		mListView_Answered= (ListView) v.findViewById(R.id.problemslist_layout_unansweredlistview);
		// 下面的代码不需要改动
		
		listAdapter_UnAnsweredProblem= new SpecialAdapter(getActivity(),mUnansweredProblemList,R.layout.unansweredproblem_item,new String[]{"title","desciption","from"},new int[]{R.id.problemslist_textview_title,R.id.problemslist_textview_desciption,R.id.problemslist_textview_from});
	    listAdapter_AnsweredProblem= new SpecialAdapter(getActivity(),mAnsweredProblemList,R.layout.answeredproblem_item,new String[]{"title","desciption","from","answer"},new int[]{R.id.problemslist_textview_title,R.id.problemslist_textview_desciption,R.id.problemslist_textview_from,R.id.problemslist_textview_answer2});
		
		mListView_Unanswered.setAdapter(listAdapter_UnAnsweredProblem);
		mListView_Answered.setAdapter(listAdapter_AnsweredProblem);
		
		
		//****************** 设置item事件监听*********************************************
		
		mListView_Unanswered.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id)
			{
				Intent intent = new Intent(getActivity(),ProblemDisplay.class);
				String mId = mUnansweredProblemList.get(position).get("id").toString();

				BmobQuery<Problem> query = new BmobQuery<Problem>();				
				query.getObject(getActivity(), mId, new GetListener<Problem>() {

				    @Override
				    public void onSuccess(Problem object) {
				        // TODO Auto-generated method stub
				    	Problem_toDisplay=object;
				    }

				    @Override
				    public void onFailure(int code, String arg0) {
				        // TODO Auto-generated method stub
				    	showCustomToast("该条问题已被删除");
				    }

				});
				
				intent.putExtra(PROBLEM_TAG, Problem_toDisplay);
				startActivity(intent);
			}
		});
		
		mListView_Answered.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id)
			{
				Intent intent = new Intent(getActivity(),ProblemDisplay.class);
				String mId = mAnsweredProblemList.get(position).get("id").toString();

				BmobQuery<Problem> query = new BmobQuery<Problem>();				
				query.getObject(getActivity(), mId, new GetListener<Problem>() {

				    @Override
				    public void onSuccess(Problem object) {
				        // TODO Auto-generated method stub
				    	Problem_toDisplay=object;
				    }

				    @Override
				    public void onFailure(int code, String arg0) {
				        // TODO Auto-generated method stub
				    	showCustomToast("该条问题已被删除");
				    }

				});
				
				intent.putExtra(PROBLEM_TAG, Problem_toDisplay);
				startActivity(intent);
			}
		});
		//****************** 设置item事件监听*********************************************
		
		return v;
	}
	

	private void addProblem(String problemTitle) {
		problem = new Problem();
		problem.settile(problemTitle);
		problem.setTask(task);
		problem.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				addProblemToIask();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("提问问题失败失败");
			}
		});
	}

	private void addProblemToIask() {
		BmobRelation mProblems = new BmobRelation();
		mProblems.add(problem);
		task.setProblems(mProblems);
		task.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				
				addData_unansweredproblems(problem.gettitle(),problem.getdescription(),problem.getStudent().getName(),problem.getObjectId());
				listAdapter_UnAnsweredProblem.notifyDataSetChanged();
				showCustomToast("提问问题成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("提问问题失败");
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
			mWaitingAddProblemDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				addProblem(params[0]);
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mWaitingAddProblemDialog.dismiss();
		}
	}
}
