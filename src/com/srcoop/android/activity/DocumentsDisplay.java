package com.srcoop.android.activity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.srcoop.android.activity.bean.Document;
import com.srcoop.android.activity.bean.Files;
import com.srcoop.android.activity.dialog.FileConfirmDialog;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.fragment.IssueFileFragment;
import com.srcoop.android.activity.view.HandyTextView;

public class DocumentsDisplay extends Activity {
	public static final String DOC_TAG = "com.srcoop.android.activity.DocumentsDisplay";

	private Files file;
	private Document doc;
	private LinkedList<Document> mDocumentList = new LinkedList<Document>();
	private DocAdapter listAdapter;

	private FileConfirmDialog mAddDocDialog; // 确认文件路径的dialog
	private FlippingLoadingDialog mWaitingAddDocDialog;
	private ProgressDialog progressDialog; // 进度条

	BmobFile bmobFile;

	private Button mAddDocBtn;

	private ListView mListView;

	private Integer Uploadrate;

	// 文件路径选择需要用到跳转--用Bundle传值方法
	/* private Intent fileIntent; */
	private Bundle fileBundle;
	private String filePath = null;

	// 文件下载
	DownUtil downUtil;
	private int mDownStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);

		//
		mWaitingAddDocDialog = new FlippingLoadingDialog(DocumentsDisplay.this,
				"添加参考资料中，请稍后...");

		// 参考资料上传进度条
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("提示信息");
		progressDialog.setMessage("正在上传，请稍等。。。。");
		/*
		 * progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//进度条的样式
		 * ，横向 progressDialog.setCancelable(false);//进度条不消失，直到下载完成
		 */

		BmobQuery<Document> query = new BmobQuery<Document>();
		file = (Files) getIntent().getSerializableExtra(
				IssueFileFragment.FILE_TAG);
		query.addWhereRelatedTo("mDocuments", new BmobPointer(file));
		query.findObjects(DocumentsDisplay.this, new FindListener<Document>() {

			@Override
			public void onSuccess(List<Document> doc) {
				for (Document d : doc)
					mDocumentList.addFirst(d);
				listAdapter.notifyDataSetChanged();

			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});

		//
		mListView = (ListView) findViewById(R.id.filelist_listview_files);
		listAdapter = new DocAdapter(DocumentsDisplay.this, mDocumentList);
		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}

		});

		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						showCustomToast("hahah" + position);

						return false;
					}

				});

		// 弹出添加参考资料对话框Dialog 的设置
		mAddDocDialog = new FileConfirmDialog(DocumentsDisplay.this);
		mAddDocDialog.setTitle("文件路径");
		mAddDocDialog.setText("空");
		mAddDocDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddDocDialog.setButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (filePath == null) {
					showCustomToast("添加资料失败");
					mAddDocDialog.dismiss();
				} else {
					mAddDocDialog.dismiss();
					mAddDocDialog.setTextNull();
					new addRefThread().execute();
				}

			}
		}, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAddDocDialog.cancel();
				mAddDocDialog.setTextNull();

			}
		});
		// ------------

		// 实例化添加参考资料按钮
		mAddDocBtn = (Button) findViewById(R.id.filelist_image_addfiles);
		mAddDocBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DocumentsDisplay.this,
						SDFileExplorerActivity.class);

				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // resultCode为回传的标记，我在SDFileExploreActivity中回传的是RESULT_OK
		case RESULT_OK:
			fileBundle = data.getExtras(); // data为B中回传的Intent
			filePath = fileBundle.getString("path"); // path即为回传的值
			mAddDocDialog.setText(filePath);
			mAddDocDialog.show();
			break;
		default:
			break;
		}
	}

	/* addRefThread <--0.添加参考资料线程 */
	private class addRefThread extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mWaitingAddDocDialog.show();
			progressDialog.show();
		}

		protected void onProgressUpdate(Integer... value) {
			/* super.onProgressUpdate(value); */
			/* progressDialog.setProgress(value[0]); */
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				/* addRef(filePath); */
				// ---------------------------------------
				bmobFile = new BmobFile(new File(filePath));
				bmobFile.uploadblock(DocumentsDisplay.this,
						new UploadFileListener() {

							@Override
							public void onSuccess() {
								String docFileUrl = bmobFile.getFileUrl();
								doc = new Document();
								doc.setFileUrl(docFileUrl);
								doc.setFileName(bmobFile.getFilename());

								doc.save(DocumentsDisplay.this,
										new SaveListener() {

											@Override
											public void onSuccess() {
												addDocToTask();
											}

											@Override
											public void onFailure(int arg0,
													String msg) {
												showCustomToast("添加文档资料失败:"
														+ msg);
											}
										});
							}

							@Override
							public void onProgress(Integer value) {
								super.onProgress(value);
								Uploadrate = value;
								/* publishProgress(value); */
								progressDialog.setProgress(value);
							}

							@Override
							public void onFailure(int arg0, String msg) {
								showCustomToast("上传文件失败:" + msg);
							}
						});
				// ----------------------------------------

				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mWaitingAddDocDialog.dismiss();

		}
	}

	/* addRef 函数 <--- 1.添加参考资料 addRefThread类会调用 */
	private void addDoc(String filePath) {

		bmobFile = new BmobFile(new File(filePath));
		bmobFile.uploadblock(DocumentsDisplay.this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String docFileUrl = bmobFile.getFileUrl();
				doc = new Document();
				doc.setFileUrl(docFileUrl);
				doc.setFileName(bmobFile.getFilename());

				doc.save(DocumentsDisplay.this, new SaveListener() {

					@Override
					public void onSuccess() {
						addDocToTask();
					}

					@Override
					public void onFailure(int arg0, String msg) {
						showCustomToast("添加参考资料失败:" + msg);
					}
				});
			}

			@Override
			public void onProgress(Integer value) {
				super.onProgress(value);
				Uploadrate = value;
				/* publishProgress(value); */
			}

			@Override
			public void onFailure(int arg0, String msg) {
				showCustomToast("上传文件失败:" + msg);
			}
		});

	}

	/* addDocToTask 函数 <--- 2.添加参考资料到任务的关联关系 addRef函数会调用 */
	private void addDocToTask() {
		BmobRelation mDocs = new BmobRelation();
		mDocs.add(doc);
		file.setDocuments(mDocs);
		file.update(DocumentsDisplay.this, new UpdateListener() {

			@Override
			public void onSuccess() {
				mDocumentList.addFirst(doc);
				listAdapter.notifyDataSetChanged();
				showCustomToast("添加参考资料成功");
				filePath = null;
				progressDialog.dismiss();
			}

			@Override
			public void onFailure(int arg0, String msg) {
				showCustomToast("添加参考资料失败:" + msg);
			}
		});
	}

	/** showCustomToast函数 <--显示自定义Toast提示(来自String) **/
	private void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(DocumentsDisplay.this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(DocumentsDisplay.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/*
	 * RefAdapter类
	 */
	private class DocAdapter extends ArrayAdapter<Document> {

		public DocAdapter(Context context, List<Document> objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.reference_style, parent, false);

				// 在RefAdapter中添加“按钮的操作”
				Button dLoadDocBtn = (Button) convertView
						.findViewById(R.id.references_download);
				convertView.setTag(dLoadDocBtn);
			}

			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			// 创建一个Handler对象
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 0x123) {
						progressDialog.setProgress(mDownStatus);
					}
					if (mDownStatus >= 100) {
						showCustomToast("完成下载");
						progressDialog.dismiss();
					}
				}
			};
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			// 获取按钮 进行相应操作
			Button dLoadBtn = (Button) convertView.getTag();
			dLoadBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 下载文件
					progressDialog = new ProgressDialog(DocumentsDisplay.this);
					progressDialog.setTitle("提示信息");
					progressDialog.setMessage("正在下载 '"
							+ getItem(position).getFileName() + "'，请稍等...");
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 进度条的样式，横向
					/*
					 * progressDialog.setCancelable(false);//进度条不消失，直到下载完成
					 */

					final String downloadDir; // 目录--下载的文件文件存放于此
					downloadDir = "/mnt/sdcard/SRcoop/任务/" + file.getName()
							+ "/参考资料";
					BmobQuery<Document> query = new BmobQuery<Document>();
					query.getObject(DocumentsDisplay.this, getItem(position)
							.getObjectId(), new GetListener<Document>() {

						@Override
						public void onSuccess(Document object) {
							progressDialog.show();
							if (isFolderExists(downloadDir))// 如果存在文件夹
							{
								// 初始化DownUtil对象（最后一个参数指定线程数）
								downUtil = new DownUtil(object.getFileUrl(),
										downloadDir + "/"
												+ object.getFileName(), 6);
								new Thread() {
									@Override
									public void run() {
										try {
											// 开始下载
											downUtil.download();
										} catch (Exception e) {
											e.printStackTrace();
										}
										// 定义每秒调度获取一次系统的完成进度
										final Timer timer = new Timer();
										timer.schedule(new TimerTask() {
											@Override
											public void run() {
												// 获取下载任务的完成比率
												double completeRate = downUtil
														.getCompleteRate();
												mDownStatus = (int) (completeRate * 100);
												// 发送消息通知界面更新进度条
												handler.sendEmptyMessage(0x123);
												// 下载完全后取消任务调度
												if (mDownStatus >= 100) {
													timer.cancel();

												}
											}
										}, 0, 100);
									}
								}.start();
							}

						}

						@Override
						public void onFailure(int arg0, String arg1) {

						}
					});

				}
			});

			TextView mTvRefName = (TextView) convertView
					.findViewById(R.id.fileName);
			mTvRefName.setText(getItem(position).getFileName());

			TextView uploadTime = (TextView) convertView
					.findViewById(R.id.uploadTime);
			uploadTime.setText(getItem(position).getCreatedAt());

			LinearLayout mLayout = (LinearLayout) findViewById(R.id.reference_item_layout);
			/*
			 * if(position%2==1)
			 * mLayout.setBackgroundColor(Color.parseColor("#B0CEDC")); else
			 * mLayout.setBackgroundColor(Color.parseColor("#ffffff"));
			 */

			return convertView;
		}
	}

	/*
	 * 判断文件路径路径是否存在
	 */
	boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

}
