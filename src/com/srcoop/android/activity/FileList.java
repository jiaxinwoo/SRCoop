package com.srcoop.android.activity;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

import com.srcoop.android.activity.bean.File;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.fragment.IssueFileFragment;
import com.srcoop.android.activity.view.HandyTextView;

public class FileList extends Activity implements ListView.OnItemClickListener,
		ListView.OnLongClickListener {

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}

	@Override
	public boolean onLongClick(View view) {
		return false;
	}

	@SuppressWarnings("unused")
	private ListView filelist_listview_files;

	private Button fileButton; // 文件上传按钮

	private TextView mFileTitle; // 文件夹标题

	// 文件路径选择需要用到跳转--用Bundle传值方法
	private Intent fileIntent;
	private Bundle fileBundle;
	private String filePath = null;

	//
	BmobFile bmobFile;

	private File file;

	private FlippingLoadingDialog mFlippingLoadingDialog;
	private FlippingLoadingDialog mDownloadFlippingLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);

		file = (File) getIntent().getSerializableExtra(
				IssueFileFragment.FILE_TAG);

		// 初始化
		filelist_listview_files = (ListView) findViewById(R.id.filelist_listview_files);
		fileButton = (Button) findViewById(R.id.filelist_image_addfiles);
		mFileTitle = (TextView) findViewById(R.id.my_file_title);

		BmobQuery<File> fileQuery = new BmobQuery<File>();
		fileQuery.getObject(this, file.getObjectId(), new GetListener<File>() {

			@Override
			public void onSuccess(File object) {

				showCustomToast("查询成功:" + object.getName() + " obj:"
						+ object.getObjectId() + "haha" + file.getName());
				mFileTitle.setText(object.getName());
				filePath = object.getFilePath();

			}

			@Override
			public void onFailure(int code, String msg) {

				showCustomToast("查询失败:" + msg);
			}
		});

		fileButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// 跳转到路径查找
				Intent intent = new Intent(FileList.this,
						SDFileExplorerActivity.class);
				startActivityForResult(intent, 1);
				// new saveFileEdited().execute();
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // resultCode为回传的标记，我在SDFileExploreActivity中回传的是RESULT_OK
		case RESULT_OK:
			fileBundle = data.getExtras(); // data为B中回传的Intent
			filePath = fileBundle.getString("path"); // path即为回传的值
			break;
		default:
			break;
		}
	}

	// private class saveFileEdited extends AsyncTask<Void, Void, Boolean>
	// {
	//
	// @Override
	// protected void onPreExecute()
	// {
	// super.onPreExecute();
	// mFlippingLoadingDialog.show();
	// }
	//
	// @Override
	// protected Boolean doInBackground(Void... params)
	// {
	// try
	// {
	// // TODO 在这里操作连接数据库并保存对任务信息的修改
	//
	// bmobFile = new BmobFile(new File(filePath));
	// bmobFile.upload(ReferencesList.this, new UploadFileListener()
	// {
	//
	// @Override
	// public void onSuccess()
	// {
	// String fileFileUrl = bmobFile.getFileUrl();
	//
	//
	// file.setFilePath(bmobFile.getFilename());
	// file.setFileUrl(fileFileUrl);
	// file.update(ReferencesList.this, new UpdateListener()
	// {
	//
	// @Override
	// public void onSuccess()
	// {
	// showCustomToast("保存修改成功");
	//
	// }
	//
	// @Override
	// public void onFailure(int code, String msg)
	// {
	// showCustomToast("保存失败:" + msg);
	// }
	// });
	// /*
	// * achievementAbstract.setText(bmobFile.getFileUrl())
	// * ;
	// */
	// }
	//
	// @Override
	// public void onFailure(int code, String msg)
	// {
	// showCustomToast("上传文件失败:" + msg);
	// }
	// });
	// Thread.sleep(1000);
	// } catch (InterruptedException e)
	// {
	// e.printStackTrace();
	// }
	// return true;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result)
	// {
	// super.onPostExecute(result);
	// mFlippingLoadingDialog.dismiss();
	// if (result)
	// {
	// showCustomToast("保存修改成功");
	// // mTaskAddStageBtn.setVisibility(View.GONE);
	// // mTaskAddStageAssignmentBtn.setVisibility(View.GONE);
	// // mTaskMenuRightBtn.setBackgroundResource(R.drawable.button_edit);
	// }
	// }
	// }

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
