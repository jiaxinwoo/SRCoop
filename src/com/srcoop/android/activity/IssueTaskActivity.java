package com.srcoop.android.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.bean.Assignment;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.bean.Stage;
import com.srcoop.android.activity.bean.Task;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.dialog.DoubleEditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.srcoop.android.activity.fragment.AssignmentListFragment;
import com.srcoop.android.activity.fragment.AssignmentListFragment.onAssignmentListener;
import com.srcoop.android.activity.fragment.IssueTaskFragment;
import com.srcoop.android.activity.view.HandyTextView;
import com.srcoop.android.activity.view.HorizontalListView;

public class IssueTaskActivity extends FragmentActivity implements
		onSimpleListItemClickListener, View.OnClickListener,
		onAssignmentListener, OnItemClickListener, OnItemLongClickListener {

	public interface onListChangedListener {
		public void onListUpdate(Assignment assignment);
	}

	private Issue currentIssue;
	public static String TASK_TAG = "com.srcoop.android.activity.IssueTaskListActivity";
	private onListChangedListener mListChangedListener;

	private Button mTaskMenuRightBtn;// 修改当前任务信息按钮
	private Button mTaskAddStageBtn;// 添加阶段按钮
	private Button mTaskAddStageAssignmentBtn;// 添加阶段任务按钮
	private Button mBackBtn;// 返回按钮 --------------------------------有Bug
	private Button mReferenceBtn;// 参考信息跳转按钮
	private Button mProblemBtn;// 问题信息跳转按钮
	private TextView mTvTitle;// 任务名称
	private HorizontalListView mStageListView;// 横向的listView用来显示所有的阶段
	private TextView mTvCurrentStageName;// 选中的阶段的名称
	private TextView mTvCurrentStageFinishTime;// 选中的阶段的完成时间

	private DoubleEditTextDialog mDoubleEditTextDialog;
	// private SimpleListDialog mSimpleListDialog;// 添加候选执行者的Dialog
	private FlippingLoadingDialog mFlippingLoadingDialog;
	private BaseDialog mBaseDialog;// 用户确认Dialog

	// temp for add carrier
	// private TextView carrier1;
	// private TextView carrier2;
	// private View carrier1_image;
	// private View carrier2_image;

	private final String StageTag = "CreateStage";
	private final String AssignmentTag = "CreateAssignment";

	private Task task;

	// false表示当前状态是编辑状态左上角图标此时应为button_confirm
	// true表示当前状态为默认状态即不可编辑，左上角图标应此时为button_edit
	private boolean mEditOrSave = false;

	private ArrayList<Stage> mStageList = new ArrayList<Stage>();// 当前任务对应所有阶段的集合
	private ArrayList<String> candidateCarrierList = new ArrayList<String>();// 当前任务对应所有可添加的执行者的集合
	private ArrayList<String> selectedCarrierList = new ArrayList<String>();// 选中的执行者的集合
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();// 所有阶段任务模块的集合
	private ArrayList<Long> stageFinishTimeList = new ArrayList<Long>();// 阶段完成时间（毫秒）的集合
	private int currentStage;// 选中的阶段的位置

	private StageAdapter mStageAdapter;// 自定义显示阶段的适配器

	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_edit);
		Intent intent = getIntent();
		task = (Task) intent.getSerializableExtra(IssueTaskFragment.TASK_TAG);
		// 若有传值对象，接收
		Bundle bundle = intent.getExtras();
		if (bundle.get("currentTask") != null) {
			task = (Task) bundle.get("currentTask");
		}
		if (bundle.get("currentIssue") != null) {
			currentIssue = (Issue) bundle.get("currentIssue");
		}
		fm = getSupportFragmentManager();
		final Fragment fragment = fm
				.findFragmentById(R.id.assignment_fragment_containor);

		BmobQuery<Stage> stageQuery = new BmobQuery<Stage>();
		stageQuery.addWhereRelatedTo("mStages", new BmobPointer(task));
		stageQuery.order("createdAt");
		stageQuery.findObjects(this, new FindListener<Stage>() {

			@Override
			public void onSuccess(List<Stage> list) {
				for (Stage s : list) {
					mStageList.add(s);
					fragmentList.add(AssignmentListFragment.newsIntance(s));// 添加阶段任务模块到集合中
					stageFinishTimeList.add((long) s.getStageFinishTime());// 添加阶段完成总时间到集合
				}
				mStageAdapter.notifyDataSetChanged();
				if (fragment != null && fragmentList.size() > 0) {
					fm.beginTransaction()
							.add(R.id.assignment_fragment_containor,
									fragmentList.get(0)).commit();// 默认显示第一个阶段任务模块
				}
				if (mTvCurrentStageName != null && mStageList.size() > 0
						&& mStageList.get(0) != null) {
					mTvCurrentStageName.setText(mStageList.get(0)
							.getStageName());
					if (mTvCurrentStageFinishTime != null) {
						setTextViewFinishTime(stageFinishTimeList.get(0));
					}
				}
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
		mStageAdapter = new StageAdapter(this);
		initViews();
		initEvents();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			mListChangedListener = (onListChangedListener) fragment;
		} catch (Exception e) {
			throw new ClassCastException(this.toString()
					+ " must implements onListChangedListener");
		}
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mListChangedListener = null;
	}

	private void initViews() {
		mTaskMenuRightBtn = (Button) findViewById(R.id.btn_task_edit);
		mTaskAddStageBtn = (Button) findViewById(R.id.btn_task_edit_stage_add);
		mTaskAddStageAssignmentBtn = (Button) findViewById(R.id.btn_assignment_add);
		mBackBtn = (Button) findViewById(R.id.title_bar_left_back2);
		mReferenceBtn = (Button) findViewById(R.id.btn_reference_detail);
		mProblemBtn = (Button) findViewById(R.id.btn_problem_detail);
		mTvTitle = (TextView) findViewById(R.id.tv_task_title);
		// TODO 会报空指针异常
		if (task.getTaskName() != null && !task.getTaskName().isEmpty())
			mTvTitle.setText(task.getTaskName());
		mTvCurrentStageName = (TextView) findViewById(R.id.tv_currentstage_label);
		mTvCurrentStageFinishTime = (TextView) findViewById(R.id.tv_currentstage_finishtime);
		mTvCurrentStageFinishTime.setText("");
		mStageListView = (HorizontalListView) findViewById(R.id.stage_listview);
		mStageListView.setAdapter(mStageAdapter);

		mDoubleEditTextDialog = new DoubleEditTextDialog(this);
		// mSimpleListDialog = new SimpleListDialog(this);
		mFlippingLoadingDialog = new FlippingLoadingDialog(this, "保存修改中....");
		mBaseDialog = new BaseDialog(IssueTaskActivity.this);
	}

	private void initEvents() {
		mTvTitle.setOnClickListener(this);
		mTaskMenuRightBtn.setOnClickListener(this);
		mTaskAddStageBtn.setOnClickListener(this);
		mTaskAddStageAssignmentBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mReferenceBtn.setOnClickListener(this);
		mProblemBtn.setOnClickListener(this);
		mStageListView.setOnItemClickListener(this);
		mStageListView.setOnItemLongClickListener(this);

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

	@Override
	public void onItemClick(int position, View v) {
		ImageView selectIcon = (ImageView) v
				.findViewById(R.id.listitem_dialog_icon);
		if (selectIcon.getVisibility() == View.GONE) {
			selectIcon.setVisibility(View.VISIBLE);
			selectedCarrierList.add(candidateCarrierList.get(position));
		} else {
			selectIcon.setVisibility(View.GONE);
			selectedCarrierList.remove(candidateCarrierList.get(position));
		}
	}

	/**
	 * @author SS 保存修改任务信息的后台任务类
	 */
	private class saveTaskEdited extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mFlippingLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// TODO 在这里操作连接数据库并保存对任务信息的修改
				task.update(IssueTaskActivity.this, new UpdateListener() {

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						showCustomToast("保存成功");
					}

				});
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mFlippingLoadingDialog.dismiss();
			if (result) {
				showCustomToast("保存修改成功");
				mTaskAddStageBtn.setVisibility(View.GONE);
				mTaskAddStageAssignmentBtn.setVisibility(View.GONE);
				mTaskMenuRightBtn.setBackgroundResource(R.drawable.button_edit);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mTaskMenuRightBtn) {
			if (!mEditOrSave) {
				mTaskAddStageBtn.setVisibility(View.VISIBLE);
				mTaskAddStageAssignmentBtn.setVisibility(View.VISIBLE);
				mTaskMenuRightBtn
						.setBackgroundResource(R.drawable.button_confirm);
			} else {
				new saveTaskEdited().execute();
			}
			mEditOrSave = !mEditOrSave;
		} else if (v == mTaskAddStageBtn) {
			showDoubleEditTextDialog("添加阶段", "输入阶段名", "输入阶段描述", "请输入阶段名",
					"请输入阶段描述", StageTag);
		} else if (v == mTaskAddStageAssignmentBtn) {
			if(mStageList.size()>0){
				showDoubleEditTextDialog("添加阶段任务", "输入阶段任务名", "输入阶段任务描述",
						"请输入阶段任务名", "请输入阶段任务描述", AssignmentTag);
			}else{
				showCustomToast("请先新增阶段任务");
			}
		} else if (v == mBackBtn) {
			Intent intent = new Intent(IssueTaskActivity.this,
					IssueActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("currentIssue", currentIssue);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		} else if (v == mReferenceBtn) {
			Intent intent = new Intent(IssueTaskActivity.this,
					ReferencesDisplay.class);
			intent.putExtra(TASK_TAG, task);
			startActivity(intent);
		} else if (v == mProblemBtn) {
			Intent intent = new Intent(IssueTaskActivity.this,
					ProblemsList.class);
			intent.putExtra(TASK_TAG, task);
			startActivity(intent);
		} else if (v == mTvTitle) {
			Intent intent = new Intent(IssueTaskActivity.this,
					TaskDisplayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("currentTask", task);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private void showDoubleEditTextDialog(String title, String nameHint,
			String descHint, final String nameNullToast,
			final String descNullToast, final String TAG) {
		mDoubleEditTextDialog.setTitle(title);
		mDoubleEditTextDialog.setNameHint(nameHint);
		mDoubleEditTextDialog.setDescHint(descHint);
		mDoubleEditTextDialog
				.setButton1Background(R.drawable.btn_default_popsubmit);
		mDoubleEditTextDialog.setButton("确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = mDoubleEditTextDialog.getNameText();
						String desc = mDoubleEditTextDialog.getDescText();
						if (name == null || "".equals(name)) {
							mDoubleEditTextDialog.requestNameFocus();
							showCustomToast(nameNullToast);
						} else if (desc == null || "".equals(desc)) {
							mDoubleEditTextDialog.requestDescFocus();
							showCustomToast(descNullToast);
						} else {
							mDoubleEditTextDialog.dismiss();
							if (TAG.equals(StageTag)) {
								addStage(name, desc);
							} else if (TAG.equals(AssignmentTag)) {
								addAssignment(name, desc);
							}
							mDoubleEditTextDialog.setTextNull();
						}
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDoubleEditTextDialog.cancel();
						mDoubleEditTextDialog.setTextNull();
					}
				});
		mDoubleEditTextDialog.show();
		mDoubleEditTextDialog.requestNameFocus();
	}

	/**
	 * 添加阶段
	 * 
	 * @param name
	 * @param desc
	 */
	private void addStage(String name, String desc) {
		final Stage stage = new Stage();
		stage.setStageName(name);
		stage.setStageDesc(desc);
		stage.setTask(task);
		stage.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				addStageToTask(stage);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("当前网络不好，添加失败");
			}
		});
	}

	/**
	 * 创建新建阶段和当前任务的关联关系
	 * 
	 * @param stage
	 */
	private void addStageToTask(final Stage stage) {
		BmobRelation mStages = new BmobRelation();
		mStages.add(stage);
		task.setStages(mStages);
		task.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				mStageList.add(stage);
				mStageAdapter.notifyDataSetChanged();
				AssignmentListFragment fragment = AssignmentListFragment
						.newsIntance(stage);
				fragmentList.add(fragment);
				long finishTime = 0;
				stageFinishTimeList.add(finishTime);
				// FIG BUG by xin
				FragmentTransaction ft = fm.beginTransaction();
				if (fragment.isAdded()) {
					fragment.onResume();
				} else {
					ft.add(R.id.assignment_fragment_containor, fragment);
				}
				ft.commit();
				// ///
				showCustomToast("创建阶段成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加阶段任务错误");
			}
		});
	}

	/**
	 * 添加阶段任务
	 * 
	 * @param name
	 * @param desc
	 */
	private void addAssignment(String name, String desc) {
		
		final Assignment assignment = new Assignment();
		assignment.setAssName(name);
		assignment.setAssRequirement(desc);
		assignment.setAssStartTime(new BmobDate(new Date()));
		assignment.setIsFinished(false);
		Stage stage = mStageList.get(currentStage);
		assignment.setStage(stage);
		assignment.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				addAssignmentToStage(assignment);
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
		// 关键的一步，必须调用，否则将永远只会在最后一个Fragment中添加数据。
		mListChangedListener = (onListChangedListener) fragmentList
				.get(currentStage);
		mListChangedListener.onListUpdate(assignment);
		
	}

	/**
	 * 创建新建阶段任务和当前选中阶段的关联关系
	 * 
	 * @param assignment
	 */
	private void addAssignmentToStage(Assignment assignment) {
		Stage stage = mStageList.get(currentStage);
		BmobRelation mAssignments = new BmobRelation();
		mAssignments.add(assignment);
		stage.setAssignments(mAssignments);
		stage.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				showCustomToast("创建阶段任务成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
	}

	@Override
	public void onAssignmentUpdate() {
		if(fragmentList.size()>0){
			AssignmentListFragment fragment = (AssignmentListFragment) fragmentList
				.get(currentStage);
			fragment.updateUI();
		}
	}

	@Override
	public void onTimeUpdate(long timeMillis) {
		long curStageTime = stageFinishTimeList.get(currentStage);
		curStageTime += timeMillis;
		stageFinishTimeList.set(currentStage, curStageTime);

		Stage stage = mStageList.get(currentStage);
		stage.setStageFinishTime((int) curStageTime);
		stage.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				setTextViewFinishTime(stageFinishTimeList.get(currentStage));
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
	}

	/**
	 * 将时间（毫秒）转换为对应的字符串（天-小时-分-秒）显示在TextView上
	 * 
	 * @param time
	 */
	private void setTextViewFinishTime(long time) {
		StringBuilder sb = new StringBuilder();
		long diffSeconds = time / 1000 % 60;
		long diffMinutes = time / (60 * 1000) % 60;
		long diffHours = time / (60 * 60 * 1000) % 24;
		long diffDays = time / (24 * 60 * 60 * 1000);
		if (diffDays != 0)
			sb.append(diffDays + "天");
		if (diffHours != 0)
			sb.append(diffHours + "小时");
		if (diffMinutes != 0)
			sb.append(diffMinutes + "分");
		if (diffSeconds != 0)
			sb.append(diffSeconds + "秒");
		mTvCurrentStageFinishTime.setText(sb.toString());
	}

	class StageAdapter extends ArrayAdapter<Stage> {
		public StageAdapter(Context context) {
			super(context, 0, mStageList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.stage_item,
						parent, false);
			}

			Stage s = getItem(position);
			TextView mTvStageName = (TextView) convertView
					.findViewById(R.id.tv_stage_name);
			mTvStageName.setText(s.getStageName());
			TextView mTvStageDesc = (TextView) convertView
					.findViewById(R.id.tv_stage_desc);
			mTvStageDesc.setText(s.getStageDesc());

			if (position == currentStage) {
				convertView
						.setBackgroundResource(R.drawable.button_circle_shape_orange);
			} else {
				convertView
						.setBackgroundResource(R.drawable.button_circle_shape_blue);
			}
			return convertView;
		}

		public void setSelectedItem(int selectedItme) {
			currentStage = selectedItme;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FragmentTransaction ft = fm.beginTransaction();
		getCurrentFragment().onPause();
		Fragment fragment = fragmentList.get(position);
		if (fragment.isAdded()) {
			fragment.onResume();
		} else {
			ft.add(R.id.assignment_fragment_containor, fragment);
		}
		showFragment(position);
		ft.commit();

		mStageAdapter.setSelectedItem(position);
		mStageAdapter.notifyDataSetChanged();

		mTvCurrentStageName
				.setText(mStageList.get(currentStage).getStageName());
		setTextViewFinishTime(stageFinishTimeList.get(currentStage));
	}

	/**
	 * 获得当前选中的阶段对应的阶段任务模块
	 * 
	 * @return
	 */
	private Fragment getCurrentFragment() {
		return fragmentList.get(currentStage);
	}

	/**
	 * 显示选择的阶段任务模块并隐藏其他模块
	 * 
	 * @param id
	 */
	private void showFragment(int id) {
		for (int i = 0; i < fragmentList.size(); i++) {
			FragmentTransaction ft = obtainFragmentTransaction(i);
			Fragment fragment = fragmentList.get(i);
			if (id == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
	}

	/**
	 * 切换阶段的动画
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fm.beginTransaction();
		if (index > currentStage) {
			ft.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_right_out);
		} else {
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
		}
		return ft;
	}

	/**
	 * 
	 * 长按事件,删除 anthor:wujx
	 * 
	 * */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		int parentId = parent.getId();
		// 阶段被长按
		if (parentId == R.id.stage_listview) {
			deleteStage(pos);
		}
		// 阶段任务被长按
		if (parentId == R.id.assignment_fragment_containor) {
			showCustomToast("assignment long click");
		}
		return false;
	}

	/**
	 * 取消删除阶段和当前任务的关联关系
	 * 
	 * @param stage
	 */
	private void removeStageToTask(final Stage stage) {
		BmobRelation mStages = new BmobRelation();
		mStages.remove(stage);
		task.setStages(mStages);
		task.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加阶段任务错误");
			}
		});
	}

	/**
	 * 删除阶段
	 * 
	 * @param name
	 * @param desc
	 */
	private void deleteStage(final int pos) {
		final Stage stage = mStageList.get(pos);
		mBaseDialog.setTitle("提示");
		mBaseDialog.setMessage("确认删除阶段？");
		mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mBaseDialog.dismiss();
				// 删除阶段和当前任务的关系
				removeStageToTask(stage);
				// 删除阶段拥有的fragment
				BmobQuery<Assignment> assignmentQuery = new BmobQuery<Assignment>();
				assignmentQuery.addWhereRelatedTo("mAssignments",
						new BmobPointer(stage));
				assignmentQuery.findObjects(IssueTaskActivity.this,
						new FindListener<Assignment>() {

							@Override
							public void onError(int arg0, String arg1) {
								showCustomToast("删除阶段任务失败");
							}

							@Override
							public void onSuccess(
									List<Assignment> assignmentList) {
								for (Assignment assign : assignmentList) {
									assign.delete(IssueTaskActivity.this,
											new DeleteListener() {
												@Override
												public void onFailure(int arg0,
														String arg1) {
												}

												@Override
												public void onSuccess() {
												}
											});
								}
								showCustomToast("删除阶段的阶段任务成功");
								// 删除相关的列表内容

								stageFinishTimeList.remove(pos);
								mStageList.remove(pos);

								// 删除任务本身
								stage.delete(IssueTaskActivity.this,
										new DeleteListener() {

											@Override
											public void onSuccess() {
												showCustomToast("删除阶段成功");
											}

											@Override
											public void onFailure(int arg0,
													String arg1) {
												showCustomToast("当前网络不好，删除失败");
											}
										});
								mStageAdapter.notifyDataSetChanged();
								// delete fragement
								FragmentTransaction ft = fm.beginTransaction();
								Fragment fragment = fragmentList.get(pos);
								if (fragment.isRemoving()) {
									fragment.onResume();
								} else {
									ft.remove(fragment);
								}
								ft.commit();
								fragmentList.remove(pos);
								if (fragmentList.size() > 0)
									currentStage = 0;
								onAssignmentUpdate();
							}

						});

			}
		});
		mBaseDialog.setButton2("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mBaseDialog.cancel();
			}
		});
		mBaseDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mBaseDialog.show();
	}

	@Override
	public void onTimeDelete(long timeMillis) {
		// TODO Auto-generated method stub
		long curStageTime = stageFinishTimeList.get(currentStage);
		curStageTime -= timeMillis;
		stageFinishTimeList.set(currentStage, curStageTime);

		Stage stage = mStageList.get(currentStage);
		stage.setStageFinishTime((int) curStageTime);
		stage.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				setTextViewFinishTime(stageFinishTimeList.get(currentStage));
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
	}
}
