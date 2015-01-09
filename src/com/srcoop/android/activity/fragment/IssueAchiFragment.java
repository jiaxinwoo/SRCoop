package com.srcoop.android.activity.fragment;

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
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.IssueAchievementActivity;
import com.srcoop.android.activity.IssueActivity.onNavChangedListener;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.Achievement;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.view.HandyTextView;

/**
 * @author JUNSHENG 导航栏对应的成果模块
 */
public class IssueAchiFragment extends MyFragment implements
		onNavChangedListener {

	public static final String ACHI_TAG = "com.srcoop.android.activity.fragment.IssueAchiFragment";

	private EditTextDialog mAddAchiDialog;
	private FlippingLoadingDialog mWaitingAddAchiDialog;

	private Achievement achi;
	private Issue issue;
	private LinkedList<Achievement> mAchievementList = new LinkedList<Achievement>();
	private AchiAdapter listAdapter;

	private TextView mIssueName;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BmobQuery<Achievement> query = new BmobQuery<Achievement>();
		issue = (Issue) getActivity().getIntent().getSerializableExtra(
				HomeFragment.ISSUE_TAG);
		query.addWhereRelatedTo("mAchievements", new BmobPointer(issue));
		query.findObjects(getActivity(), new FindListener<Achievement>() {

			@Override
			public void onSuccess(List<Achievement> achis) {
				for (Achievement a : achis)
					mAchievementList.addFirst(a);
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

		mWaitingAddAchiDialog = new FlippingLoadingDialog(getActivity(),
				"添加成果中，请稍后...");

		View v = inflater.inflate(R.layout.temp_tasklist, null);

		// 弹出添加成果对话框Dialog 的设置
		mAddAchiDialog = new EditTextDialog(getActivity());
		mAddAchiDialog.setTitle("添加成果");
		mAddAchiDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddAchiDialog.setHint("输入成果名");
		mAddAchiDialog.setButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String achiName = mAddAchiDialog.getText();
				if (achiName == null) {
					mAddAchiDialog.requestFocus();
					showCustomToast("成果名不能为空");
				} else {
					mAddAchiDialog.dismiss();
					mAddAchiDialog.setTextNull();
					new addAchiThread().execute(achiName);
				}

			}
		}, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAddAchiDialog.cancel();
				mAddAchiDialog.setTextNull();
			}
		});

		mIssueName = (TextView) getActivity().findViewById(
				R.id.issue_activity_title);

		// TODO 用Bundle saveIntanceState保存issue，否则issue会报空指针异常
		mIssueName.setText(issue.getIssueName());

		mListView = (ListView) v.findViewById(R.id.temp_tasklist);

		listAdapter = new AchiAdapter(getActivity(), mAchievementList);
		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						IssueAchievementActivity.class);
				Achievement achi = mAchievementList.get(position);
				intent.putExtra(ACHI_TAG, achi);
				startActivity(intent);
			}
		});
		return v;
	}

	/* addAchiThread <--0.添加成果线程 */
	private class addAchiThread extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mWaitingAddAchiDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				addAchi(params[0]);
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mWaitingAddAchiDialog.dismiss();
		}
	}

	/* addAchi 函数 <--- 1.添加成果信息 addAchiThread类会调用 */
	private void addAchi(String achiName) {
		achi = new Achievement();
		achi.setTile(achiName);
		achi.setIssue(issue);
		achi.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				addAchiToIssue();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加任务失败");
			}
		});
	}

	/* addAchiToIssue 函数 <--- 2.添加成果到课题的关联关系 addAchi函数会调用 */
	private void addAchiToIssue() {
		BmobRelation mAchis = new BmobRelation();
		mAchis.add(achi);
		issue.setAchievements(mAchis);
		issue.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				mAchievementList.addFirst(achi);
				listAdapter.notifyDataSetChanged();
				showCustomToast("添加成果成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加成果失败");
			}
		});
	}

	/** showCustomToast函数 <--显示自定义Toast提示(来自String) **/
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

	/* AchiAdapter类 */
	private class AchiAdapter extends ArrayAdapter<Achievement> {

		public AchiAdapter(Context context, List<Achievement> objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.task_item, parent, false);
			}

			// 复用任务样式
			TextView mTvAchiName = (TextView) convertView
					.findViewById(R.id.tv_task_name);
			mTvAchiName.setText(getItem(position).getTitle());

			TextView mTvAchiDesc = (TextView) convertView
					.findViewById(R.id.tv_task_desc);
			mTvAchiDesc.setText(getItem(position).getAbstract());

			TextView mTvAchiLabelDesc = (TextView) convertView
					.findViewById(R.id.tv_task_label_desc);
			mTvAchiLabelDesc.setText("摘要：");

			return convertView;
		}
	}

	@Override
	public void changeAddBtn() {
		mAddAchiDialog.show();
	}

}
