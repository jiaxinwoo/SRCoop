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

import com.srcoop.android.activity.DocumentsDisplay;
import com.srcoop.android.activity.IssueActivity.onNavChangedListener;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.Files;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.view.HandyTextView;

/**
 * @author SS 导航栏对应的文件模块
 */
public class IssueFileFragment extends MyFragment implements
		onNavChangedListener {

	public static final String FILE_TAG = "com.srcoop.android.activity.fragment.IssueFileFragment";
	private Issue issue;
	private Files file;
	private LinkedList<Files> mFileList = new LinkedList<Files>();
	private FileAdapter listAdapter;

	private EditTextDialog mAddFileDialog;
	private FlippingLoadingDialog mWaitingAddFileDialog;

	private TextView mIssueName;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BmobQuery<Files> query = new BmobQuery<Files>();
		issue = (Issue) getActivity().getIntent().getSerializableExtra(
				HomeFragment.ISSUE_TAG);
		query.addWhereRelatedTo("mFiles", new BmobPointer(issue));
		query.findObjects(getActivity(), new FindListener<Files>() {

			@Override
			public void onSuccess(List<Files> files) {
				for (Files f : files)
					mFileList.addFirst(f);
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

		mWaitingAddFileDialog = new FlippingLoadingDialog(getActivity(),
				"添加文档中，请稍后...");

		View v = inflater.inflate(R.layout.temp_tasklist, null);

		mAddFileDialog = new EditTextDialog(getActivity());
		mAddFileDialog.setTitle("添加文档");
		mAddFileDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddFileDialog.setHint("输入文档名");
		mAddFileDialog.setButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String achiName = mAddFileDialog.getText();
				if (achiName == null) {
					mAddFileDialog.requestFocus();
					showCustomToast("请输入文档名");
				} else {
					mAddFileDialog.dismiss();
					mAddFileDialog.setTextNull();
					new addFileThread().execute(achiName);
				}

			}
		}, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAddFileDialog.cancel();
				mAddFileDialog.setTextNull();
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
		listAdapter = new FileAdapter(getActivity(), mFileList);
		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						DocumentsDisplay.class);
				Files file = mFileList.get(position);
				intent.putExtra(FILE_TAG, file);
				startActivity(intent);
			}
		});

		return v;
	}

	/* addFileThread <--0.添加文档线程 */
	private class addFileThread extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mWaitingAddFileDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				addFile(params[0]);
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mWaitingAddFileDialog.dismiss();
		}
	}

	/* addFile 函数 <--- 1.添加文档信息 addFileThread类会调用 */
	private void addFile(String fileName) {
		file = new Files();
		file.setName(fileName);
		file.setIssue(issue);
		file.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				addFileToIssue();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加文档失败");
			}
		});
	}

	/* addFileToIssue 函数 <--- 2.添加文档到课题的关联关系 addFile函数会调用 */
	private void addFileToIssue() {
		BmobRelation mFiles = new BmobRelation();
		mFiles.add(file);
		issue.setFiles(mFiles);
		issue.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				mFileList.addFirst(file);
				listAdapter.notifyDataSetChanged();
				showCustomToast("添加文档成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加文档失败");
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

	/* FileAdapter类 */
	private class FileAdapter extends ArrayAdapter<Files> {

		public FileAdapter(Context context, List<Files> objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.file_item, parent, false);
			}

			// 复用任务样式
			TextView mTvFileName = (TextView) convertView
					.findViewById(R.id.tv_task_name);
			mTvFileName.setText(getItem(position).getName());

			return convertView;
		}
	}

	@Override
	public void changeAddBtn() {
		mAddFileDialog.show();
	}
}