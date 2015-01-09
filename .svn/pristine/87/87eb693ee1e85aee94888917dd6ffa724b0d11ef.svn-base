package com.srcoop.android.activity.fragment;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.srcoop.android.activity.IssueActivity;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Student;
import com.srcoop.android.activity.bean.Teacher;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.dialog.EditTextDialog;

public class HomeFragment extends MyFragment implements View.OnClickListener {

	public static final String ISSUE_TAG = "com.srcoop.android.activity.fragment.HomeFragment";

	private PullToRefreshListView mPullRefreshListView;
	private Button mAddIssueBtn;
	private int type;
	private Teacher teacher;
	private Student student;
	private EditTextDialog mEditTextDialog;
	private Issue clickIssue;
	private LinkedList<Issue> mIssueList = new LinkedList<Issue>();
	private IssueAdapter listAdapter;

	private BaseDialog mBaseDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = BmobUser.getCurrentUser(getActivity(), MyUser.class).getType();
		if (type == 0) {
			teacher = BmobUser.getCurrentUser(getActivity(), Teacher.class);
		} else {
			student = BmobUser.getCurrentUser(getActivity(), Student.class);
		}
		listAdapter = new IssueAdapter(getActivity(), mIssueList);
		queryIssues(0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_menuitem_home, null, false);
		initViews(v);
		initEvents();

		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null)
			parent.removeView(v);
		return v;
	}

	private void initViews(View v) {
		((TextView) getActivity().findViewById(R.id.activity_title))
				.setText("首页");
		mAddIssueBtn = ((Button) getActivity().findViewById(
				R.id.title_bar_right_menu));
		mAddIssueBtn.setBackgroundResource(R.drawable.add_button);
		mAddIssueBtn.setClickable(true);
		mEditTextDialog = new EditTextDialog(HomeFragment.this.getActivity());
		mPullRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.mIssueListView);

		mBaseDialog = new BaseDialog(getActivity());
	}

	private void initEvents() {
		mAddIssueBtn.setOnClickListener(this);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						new GetDataTask().execute();
					}
				});
		mPullRefreshListView.setAdapter(listAdapter);
		mPullRefreshListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(getActivity(),
								IssueActivity.class);
						Issue issue = mIssueList.get(position - 1);
						intent.putExtra(ISSUE_TAG, issue);
						intent.putExtra("currentIssue", issue);
						startActivity(intent);
					}
				});
		// 设置长按删除
		mPullRefreshListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						deleteIssuse(position);
						return true;
					}

				});
	}

	// 删除课题
	private void deleteIssuse(final int position) {
		clickIssue = mIssueList.get(position - 1);
		String issueTeacherId = clickIssue.getTeacher().getObjectId();
		if (type == 0 && teacher.getObjectId().equals(issueTeacherId)) {
			// 检查删除者为创建者
			mBaseDialog.setTitle("提示");
			mBaseDialog.setMessage("确认删除课题？删除课题后与课题相关的所有资料将会被删除，无法访问");
			mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener() {
				// 只删除课题本身
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mBaseDialog.dismiss();

					clickIssue.delete(getActivity(), new DeleteListener() {

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							showCustomToast("课题删除成功", getActivity());
							mIssueList.remove(position - 1);
							listAdapter.notifyDataSetChanged();
						}

					});

				}
			});

		} else {
			mBaseDialog.setTitle("提示");
			mBaseDialog.setMessage("确认退出课题？退出后将不可以访问与该课题相关的资料");
			mBaseDialog.setButton1("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mBaseDialog.dismiss();
					// 检查删除者是否为创建者
					if (type == 0) {
						// 若为老师,移除其ISSUE关联关系
						BmobRelation teacherIssue = new BmobRelation();
						teacherIssue.remove(clickIssue);
						teacher.setIssues(teacherIssue);
						teacher.update(getActivity(), new UpdateListener() {

							@Override
							public void onSuccess() {
								showCustomToast("老师退出成功", getActivity());
								mIssueList.remove(position - 1);
								listAdapter.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}
						});
					} else {
						BmobRelation studentIssue = new BmobRelation();
						studentIssue.remove(clickIssue);
						student.setStudent_Issue(studentIssue);
						student.update(getActivity(), new UpdateListener() {

							@Override
							public void onSuccess() {
								showCustomToast("学生退出成功", getActivity());
								mIssueList.remove(position - 1);
								listAdapter.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}
						});
					}

				}
			});

		}

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

	private void queryIssues(int refresh) {
		BmobQuery<Issue> query = new BmobQuery<Issue>();
		query.addWhereRelatedTo("mIssues", new BmobPointer(teacher));

		if (refresh == 0) {
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.setMaxCacheAge(10000L);
			// BmobQuery.clearAllCachedResults(getActivity());
		} else if (refresh == 1) {

			query.addWhereNotContainedIn("mIssueName", mIssueList);
		}
		query.findObjects(getActivity(), new FindListener<Issue>() {

			@Override
			public void onSuccess(List<Issue> issues) {
				for (Issue i : issues) {
					if (mIssueList.indexOf(i) >= 0)
						continue;
					mIssueList.addFirst(i);
				}
				listAdapter.notifyDataSetChanged();
				Log.i("HomeFragment", mIssueList.toString());
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	private void addIssue(final String title) {

		if (teacher.getType() == 0) {
			final Issue issue = new Issue();
			issue.setIssueName(title);
			BmobDate date = new BmobDate(new Date());
			issue.setIssueCreateTime(date);
			issue.setTeacher(teacher);
			issue.save(getActivity(), new SaveListener() {
				@Override
				public void onSuccess() {
					showCustomToast("创建课题名成功，课题名为:" + title, getActivity());
					BmobRelation mIssue = new BmobRelation();
					mIssue.add(issue);
					teacher.setIssues(mIssue);
					teacher.update(getActivity(), new UpdateListener() {
						@Override
						public void onSuccess() {
							mIssueList.addFirst(issue);
							listAdapter.notifyDataSetChanged();
							showCustomToast("添加课题成功", getActivity());
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							showCustomToast("添加课题失败", getActivity());
						}
					});
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					showCustomToast("添加课题失败" + arg1, getActivity());
				}

			});

		} else {
			showCustomToast("只有老师才能创建课题哦！", getActivity());
		}

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				queryIssues(1);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			listAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mAddIssueBtn) {

			if (type == 0) {
				mEditTextDialog.setTitle("添加课题");
				mEditTextDialog.setHint("输入课题名");
				mEditTextDialog
						.setButton1Background(R.drawable.btn_default_popsubmit);
				mEditTextDialog.setButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String title = mEditTextDialog.getText();
								if (title == null) {
									mEditTextDialog.requestFocus();
									showCustomToast("请输入课题名", getActivity());
								} else {
									mEditTextDialog.dismiss();
									addIssue(title);
									mEditTextDialog.setTextNull();
								}
							}

						}, "取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mEditTextDialog.cancel();
							}
						});
				mEditTextDialog.show();
			} else {
				showCustomToast("学生党你暂时没有此权限", getActivity());
			}
		}
	}

	private class IssueAdapter extends ArrayAdapter<Issue> {

		public IssueAdapter(Context context, List<Issue> objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.issue_item, parent, false);
			}

			TextView mTvIssueName = (TextView) convertView
					.findViewById(R.id.tv_issue_name);
			mTvIssueName.setText(getItem(position).getIssueName());

			TextView mTvIssueDesc = (TextView) convertView
					.findViewById(R.id.tv_issue_desc);
			mTvIssueDesc.setText(getItem(position).getIssueContent());

			return convertView;
		}
	}
}
