package com.srcoop.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.adapter.SpecialAdapter;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Problem;
import com.srcoop.android.activity.bean.Student;
import com.srcoop.android.activity.bean.Task;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.view.HandyTextView;

public class ProblemsList extends Activity implements View.OnClickListener {

	public static final String PROBLEM_TAG = "com.srcoop.android.activity.fragment.ProblemListFragment";

	private Student student;
	private Button problemsList_button_back;
	private Button problemsList_image_addProblems;

	private TextView problemslist_button_answeredlistbutton;
	private TextView problemslist_button_unansweredlistbutton;

	private int listviewstatue = 1;
	private ListView problemslist_listview_unansweredproblemslistview;
	private ListView problemslist_listview_answeredproblemslistview;

	// *******************************************************************

	private Task task;
	private Problem problem;
	private Problem Problem_toDisplay;

	private List<Map<String, Object>> mUnansweredProblemList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mAnsweredProblemList = new ArrayList<Map<String, Object>>();

	private SpecialAdapter listAdapter_UnAnsweredProblem;
	private SpecialAdapter listAdapter_AnsweredProblem;

	private EditTextDialog mAddProblemDialog;
	private FlippingLoadingDialog mWaitingAddProblemDialog;

	// *******************************************************************

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problems_list);

		task = (Task) getIntent().getSerializableExtra(
				IssueTaskActivity.TASK_TAG);
		student = BmobUser.getCurrentUser(this, Student.class);
		initiate();

		setListener();
		findtask();
		addnewproblem();
		// ************************************************************************

		listAdapter_UnAnsweredProblem = new SpecialAdapter(this,
				mUnansweredProblemList, R.layout.unansweredproblem_item,
				new String[] { "title", "desciption", "from" }, new int[] {
						R.id.problemslist_textview_title,
						R.id.problemslist_textview_desciption,
						R.id.problemslist_textview_from });
		listAdapter_AnsweredProblem = new SpecialAdapter(this,
				mAnsweredProblemList, R.layout.answeredproblem_item,
				new String[] { "title", "desciption", "from", "answer" },
				new int[] { R.id.problemslist_textview_title2,
						R.id.problemslist_textview_desciption2,
						R.id.problemslist_textview_from2,
						R.id.problemslist_textview_answer2 });

		problemslist_listview_answeredproblemslistview
				.setAdapter(listAdapter_AnsweredProblem);
		problemslist_listview_unansweredproblemslistview
				.setAdapter(listAdapter_UnAnsweredProblem);

	}

	private void addnewproblem() {
		// ***************************************************************************
		mWaitingAddProblemDialog = new FlippingLoadingDialog(this,
				"添加问题中，请稍后...");
		mAddProblemDialog = new EditTextDialog(this);
		mAddProblemDialog.setTitle("提问问题");
		mAddProblemDialog
				.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddProblemDialog.setHint("输入问题标题");

		// ******************************************************************
		mAddProblemDialog.setButton("确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String taskName = mAddProblemDialog.getText();

						if (taskName == null) {
							mAddProblemDialog.requestFocus();
							showCustomToast("问题标题不能为空");
						} else {
							mAddProblemDialog.dismiss();
							mAddProblemDialog.setTextNull();
							new addTaskThread().execute(taskName);
						}
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mAddProblemDialog.cancel();
						mAddProblemDialog.setTextNull();
					}
				});

	}

	private void findtask() {
		BmobQuery<Task> query = new BmobQuery<Task>();
		query.getObject(this, task.getObjectId(), new GetListener<Task>() {

			@Override
			public void onSuccess(Task object) {
				task = object;
				// TODO Auto-generated method stub
				query_unansweredproblems();
				query_answeredproblems();
			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				showCustomToast("查询失败：" + arg0);
			}

		});

	}

	private void query_unansweredproblems() {
		// *************************查找未回答问题***************************************
		BmobQuery<Problem> Un_query = new BmobQuery<Problem>();

		Un_query.addWhereRelatedTo("mProblems", new BmobPointer(task));
		Un_query.addWhereEqualTo("isAnswered", false);
		Un_query.findObjects(this, new FindListener<Problem>() {

			@Override
			public void onSuccess(List<Problem> problems) {
				for (Problem t : problems) {
					addData_unansweredproblems(t.gettitle(),
							t.getdescription(), t.getStudent().getName() + "",
							t.getObjectId());

				}
				listAdapter_UnAnsweredProblem.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {
				showCustomToast("查询失败：" + arg0);
			}
		});
		// *************************查找未回答问题***************************************
	}

	private void query_answeredproblems() {
		// *************************查找已回答问题***************************************
		BmobQuery<Problem> query = new BmobQuery<Problem>();

		query.addWhereRelatedTo("mProblems", new BmobPointer(task));
		query.addWhereEqualTo("isAnswered", true);

		query.findObjects(this, new FindListener<Problem>() {

			@Override
			public void onSuccess(List<Problem> problems) {
				for (Problem t : problems) {
					addData_answeredproblems(t.gettitle(), t.getdescription(),
							t.getStudent().getName() + "", t.getanswer(),
							t.getObjectId());
				}
				listAdapter_AnsweredProblem.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {
				showCustomToast("查询失败：" + arg0);
			}
		});
		// *************************查找已回答问题***************************************

	}

	private void initiate() {
		problemsList_button_back = (Button) findViewById(R.id.problemslist_button_back);
		problemsList_image_addProblems = (Button) findViewById(R.id.problemslist_image_addproblems);

		problemslist_button_answeredlistbutton = (TextView) findViewById(R.id.problemslist_button_answeredlistbutton);
		problemslist_button_unansweredlistbutton = (TextView) findViewById(R.id.problemslist_button_unansweredlistbutton);

		problemslist_listview_answeredproblemslistview = (ListView) findViewById(R.id.problemslist_listview_answeredproblemslistview);
		problemslist_listview_unansweredproblemslistview = (ListView) findViewById(R.id.problemslist_listview_unansweredproblemslistview);

	}

	private void setListener() {
		problemsList_button_back.setOnClickListener(this);
		problemsList_image_addProblems.setOnClickListener(this);

		problemslist_button_answeredlistbutton.setOnClickListener(this);
		problemslist_button_unansweredlistbutton.setOnClickListener(this);

		problemsList_image_addProblems
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mAddProblemDialog.show();
					}
				});

		problemslist_listview_unansweredproblemslistview
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						String mId = mUnansweredProblemList.get(position)
								.get("id").toString();

						BmobQuery<Problem> query = new BmobQuery<Problem>();
						query.getObject(ProblemsList.this, mId,
								new GetListener<Problem>() {

									@Override
									public void onSuccess(Problem object) {
										// TODO Auto-generated method stub
										Problem_toDisplay = object;
										Intent intent = new Intent(
												ProblemsList.this,
												ProblemDisplay.class);
										intent.putExtra(PROBLEM_TAG,
												Problem_toDisplay);
										startActivity(intent);
									}

									@Override
									public void onFailure(int code, String arg0) {
										// TODO Auto-generated method stub
										showCustomToast("该条问题已被删除");
									}

								});

					}
				});

		problemslist_listview_answeredproblemslistview
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						String mId = mAnsweredProblemList.get(position)
								.get("id").toString();

						BmobQuery<Problem> query = new BmobQuery<Problem>();
						query.getObject(ProblemsList.this, mId,
								new GetListener<Problem>() {

									@Override
									public void onSuccess(Problem object) {
										// TODO Auto-generated method stub
										Problem_toDisplay = object;
										Intent intent = new Intent(
												ProblemsList.this,
												ProblemDisplay.class);
										intent.putExtra(PROBLEM_TAG,
												Problem_toDisplay);
										startActivity(intent);
									}

									@Override
									public void onFailure(int code, String arg0) {
										// TODO Auto-generated method stub
										showCustomToast("该条问题已被删除");
									}

								});
					}
				});

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.problemslist_button_back) {
		}

		else if (v.getId() == R.id.problemslist_image_addproblems) {
			if (BmobUser.getCurrentUser(this, MyUser.class).getType() == 1) {
				showCustomToast(BmobUser.getCurrentUser(this, MyUser.class)
						.getType() + "");
				// new
				// AlertDialog.Builder(this).setTitle("提问问题").setIcon(android.R.drawable.ic_dialog_info).setView(new
				// EditText(this)).setPositiveButton("确定",
				// null).setNegativeButton("取消", null).show();
				// Toast toast2 = Toast.makeText(ProblemsList.this, "more",
				// Toast.LENGTH_SHORT);
				// toast2.show();
			} else {
				showCustomToast("您暂时还没有此项权限");

			}
		}

		else if (v.getId() == R.id.problemslist_button_unansweredlistbutton) {
			if (listviewstatue == 0) {
				listviewstatue = 1;
				// showCustomToast("listAdapter_UnAnsweredProblem");

				problemslist_listview_unansweredproblemslistview
						.setVisibility(View.VISIBLE);

				AnimationSet animationSet_answered = new AnimationSet(true);
				AnimationSet animationSet_unanswered = new AnimationSet(true);
				TranslateAnimation translateAnimation_answered = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 2f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);
				TranslateAnimation translateAnimation_unanswered = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, -2f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);
				translateAnimation_answered.setDuration(1000);
				translateAnimation_unanswered.setDuration(1000);

				animationSet_answered.addAnimation(translateAnimation_answered);
				animationSet_unanswered
						.addAnimation(translateAnimation_unanswered);

				problemslist_listview_unansweredproblemslistview
						.startAnimation(animationSet_unanswered);
				problemslist_listview_answeredproblemslistview
						.startAnimation(animationSet_answered);

				problemslist_listview_answeredproblemslistview
						.setVisibility(View.GONE);

				problemslist_button_unansweredlistbutton
						.setBackgroundResource(R.drawable.transpant);
				problemslist_button_answeredlistbutton
						.setBackgroundResource(R.drawable.problemslist_image_cuttingline2_0);

			}

		} else if (v.getId() == R.id.problemslist_button_answeredlistbutton) {
			if (listviewstatue == 1) {
				listviewstatue = 0;
				// showCustomToast("listAdapter_AnsweredProblem");

				problemslist_listview_answeredproblemslistview
						.setVisibility(View.VISIBLE);

				AnimationSet animationSet_answered = new AnimationSet(true);
				AnimationSet animationSet_unanswered = new AnimationSet(true);
				TranslateAnimation translateAnimation_answered = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 2f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);
				TranslateAnimation translateAnimation_unanswered = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, -2f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);
				translateAnimation_answered.setDuration(1000);
				translateAnimation_unanswered.setDuration(1000);

				animationSet_answered.addAnimation(translateAnimation_answered);
				animationSet_unanswered
						.addAnimation(translateAnimation_unanswered);

				problemslist_listview_unansweredproblemslistview
						.startAnimation(animationSet_unanswered);
				problemslist_listview_answeredproblemslistview
						.startAnimation(animationSet_answered);

				problemslist_listview_unansweredproblemslistview
						.setVisibility(View.GONE);

				problemslist_button_unansweredlistbutton
						.setBackgroundResource(R.drawable.problemslist_image_cuttingline2_0);
				problemslist_button_answeredlistbutton
						.setBackgroundResource(R.drawable.transpant);

			}
		}
	}

	private List<Map<String, Object>> addData_unansweredproblems(String title,
			String descrition, String from, String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("desciption", descrition);
		map.put("from", from);
		map.put("id", id);
		mUnansweredProblemList.add(map);
		return mUnansweredProblemList;
	}

	private List<Map<String, Object>> addData_answeredproblems(String title,
			String descrition, String from, String Answer, String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("desciption", descrition);
		map.put("from", from);
		map.put("answer", Answer);
		map.put("id", id);
		mAnsweredProblemList.add(map);
		return mAnsweredProblemList;
	}

	private void addProblem(String problemTitle) {
		problem = new Problem();
		problem.settile(problemTitle);
		problem.setTask(task);
		problem.setStudent(student);
		problem.setisAnswered(false);
		problem.save(this, new SaveListener() {

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
		task.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {

				addData_unansweredproblems(problem.gettitle(),
						problem.getdescription(), "未取名", problem.getObjectId());
				listAdapter_UnAnsweredProblem.notifyDataSetChanged();
				if (listviewstatue == 0) {
					listviewstatue = 1;
					// showCustomToast("listAdapter_UnAnsweredProblem");

					problemslist_listview_unansweredproblemslistview
							.setVisibility(View.VISIBLE);

					AnimationSet animationSet_answered = new AnimationSet(true);
					AnimationSet animationSet_unanswered = new AnimationSet(
							true);
					TranslateAnimation translateAnimation_answered = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 2f,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f);
					TranslateAnimation translateAnimation_unanswered = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, -2f,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f);
					translateAnimation_answered.setDuration(1000);
					translateAnimation_unanswered.setDuration(1000);

					animationSet_answered
							.addAnimation(translateAnimation_answered);
					animationSet_unanswered
							.addAnimation(translateAnimation_unanswered);

					problemslist_listview_unansweredproblemslistview
							.startAnimation(animationSet_unanswered);
					problemslist_listview_answeredproblemslistview
							.startAnimation(animationSet_answered);

					problemslist_listview_answeredproblemslistview
							.setVisibility(View.GONE);

					problemslist_button_unansweredlistbutton
							.setBackgroundResource(R.drawable.transpant);
					problemslist_button_answeredlistbutton
							.setBackgroundResource(R.drawable.problemslist_image_cuttingline2_0);

				}
				showCustomToast("提问问题成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("提问问题失败");
			}
		});
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

	/** 显示自定义Toast提示(来自String) **/
	private void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

}
