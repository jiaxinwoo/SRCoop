package com.srcoop.android.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.srcoop.android.activity.bean.Achievement;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.fragment.IssueAchiFragment;
import com.srcoop.android.activity.view.HandyTextView;

public class IssueAchievementActivity extends Activity {

	private Button editBtn; // 编辑按钮
	private Button fileButton;

	private TextView mAchiTitle; // 成果标题
	private TextView achievementLabelFile;

	private EditText achievementFilePath; // 文件路径

	private EditText achievementPublishArea;
	private EditText achievementFirstAuthor;
	private EditText achievementOtherAuthor;
	private EditText achievementAbstract;

	private ImageView achievementPublishAreaImage;
	private ImageView achievementFirstAuthorImage;
	private ImageView achievementOtherAuthorImage;
	private ImageView achievementAbstractImage;
	private ImageView achievementFilePathImage;

	// 文件路径选择需要用到跳转--用Bundle传值方法
	@SuppressWarnings("unused")
	private Intent fileIntent;
	private Bundle fileBundle;
	private String filePath = null;

	//
	BmobFile bmobFile;

	private Achievement achi;

	private FlippingLoadingDialog mFlippingLoadingDialog;
	@SuppressWarnings("unused")
	private FlippingLoadingDialog mDownloadFlippingLoadingDialog;

	// true表示当前状态是编辑状态 右上角图标此时应为issue_edit_button_confirm.png
	// false表示当前状态为默认状态即不可编辑， 右上角图标应此时为issue_display_button_edit
	private boolean iEditOrSave = false;
	private boolean iFileSave = false; // 是否上传或修改文件
	private boolean successTag;
	//
	private int successTest = 88; // 测试故障

	ProgressBar bar;
	DownUtil downUtil;
	private int mDownStatus = 0;

	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		 * Bmob.initialize(IssueAchievementActivity.this,
		 * "fa727ff9df3c8bd794eac3378eee90aa"); //初始化 Bmob SDK
		 */this.setContentView(R.layout.activity_achievement_display);

		achi = (Achievement) getIntent().getSerializableExtra(
				IssueAchiFragment.ACHI_TAG);

		// 初始化
		editBtn = (Button) findViewById(R.id.achievement_display_edit);
		fileButton = (Button) findViewById(R.id.achievement_file_upload);

		mAchiTitle = (TextView) findViewById(R.id.my_achi_title);
		achievementLabelFile = (TextView) findViewById(R.id.achievement_label_file);

		achievementPublishArea = (EditText) findViewById(R.id.achievement_publish_area);
		achievementFirstAuthor = (EditText) findViewById(R.id.achievement_first_author);
		achievementOtherAuthor = (EditText) findViewById(R.id.achievement_other_author);
		achievementAbstract = (EditText) findViewById(R.id.achievement_abstract);
		achievementFilePath = (EditText) findViewById(R.id.achievement_file_path);

		achievementPublishAreaImage = (ImageView) findViewById(R.id.achievement_publish_area_image);
		achievementFirstAuthorImage = (ImageView) findViewById(R.id.achievement_first_author_image);
		achievementOtherAuthorImage = (ImageView) findViewById(R.id.achievement_other_author_image);
		achievementAbstractImage = (ImageView) findViewById(R.id.achievement_abstract_image);
		achievementFilePathImage = (ImageView) findViewById(R.id.achievement_file_path_image);

		mFlippingLoadingDialog = new FlippingLoadingDialog(this, "保存修改中....");
		mDownloadFlippingLoadingDialog = new FlippingLoadingDialog(this,
				"文件下载中....");

		bar = (ProgressBar) findViewById(R.id.achi_bar);

		BmobQuery<Achievement> achiQuery = new BmobQuery<Achievement>();
		achiQuery.getObject(this, achi.getObjectId(),
				new GetListener<Achievement>() {

					@Override
					public void onSuccess(Achievement object) {

						//showCustomToast("查询成功:" + object.getTitle() + " obj:"
							//	+ object.getObjectId() + "haha"
								//+ achi.getTitle());
						mAchiTitle.setText(object.getTitle());
						achievementPublishArea.setText(object.getPublishArea());
						achievementAbstract.setText(object.getAbstract());
						achievementFirstAuthor.setText(object.getFirstAuthor());
						achievementOtherAuthor.setText(object.getOtherAuthor());
						filePath = object.getFilePath();
						achievementFilePath.setText(object.getFilePath());

						// 当有文件时显示"文件下载"
						if (filePath == null) {
							achievementLabelFile.setText("文件上传");
							achievementLabelFile.setTextColor(Color.rgb(42,
									148, 197));
							fileButton.setEnabled(false);

						} else {
							achievementLabelFile.setText("文件下载");
							achievementLabelFile.setTextColor(Color.rgb(255,
									255, 255));
							fileButton.setEnabled(true);
						}

					}

					@Override
					public void onFailure(int code, String msg) {

						//showCustomToast("查询失败:" + msg);
					}
				});

		editBtn.setOnClickListener(new View.OnClickListener() { // 编辑按钮

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!iEditOrSave) {
					editBtn.setBackgroundResource(R.drawable.issue_edit_button_confirm);
					achievementLabelFile.setTextColor(Color.rgb(255, 255, 255));
					achievementLabelFile.setText("文件上传");
					fileButton.setEnabled(true);

					achievementPublishArea.setEnabled(true);
					achievementPublishArea.setTextColor(Color.rgb(42, 148, 197));
					achievementPublishAreaImage
							.setImageResource(R.drawable.issue_edit_input_name);
					achievementFirstAuthor.setEnabled(true);
					achievementFirstAuthor.setTextColor(Color.rgb(42, 148, 197));
					achievementFirstAuthorImage
							.setImageResource(R.drawable.issue_edit_input_name);
					achievementOtherAuthor.setEnabled(true);
					achievementOtherAuthor.setTextColor(Color.rgb(42, 148, 197));
					achievementOtherAuthorImage
							.setImageResource(R.drawable.issue_edit_input_discription);
					achievementAbstract.setEnabled(true);
					achievementAbstract.setTextColor(Color.rgb(42, 148, 197));
					achievementAbstractImage
							.setImageResource(R.drawable.issue_edit_input_discription);

				} else {
					new saveAchiEdited().execute();
				}
				iEditOrSave = !iEditOrSave;

			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// 创建一个Handler对象
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					bar.setProgress(mDownStatus);
				}
				if (mDownStatus >= 100) {
					showCustomToast("完成下载");
					bar.setVisibility(View.INVISIBLE);
				}
			}
		};
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		fileButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SdCardPath") @Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (iEditOrSave) {
					// 修改文件
					Intent intent = new Intent(IssueAchievementActivity.this,
							SDFileExplorerActivity.class);

					startActivityForResult(intent, 1);
				} else {
					/* new Thread(runnable).start(); */
					/* new downloadFile().execute(); */
					// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
					// 下载文件
					final String downloadDir; // 目录--下载的文件文件存放于此
					/*
					 * downloadDir="/mnt/sdcard/SRcoop/achievement/"+achi.getTitle
					 * ()+"/";
					 */
					downloadDir = "/mnt/sdcard/SRcoop/成果/" + achi.getTitle();
					BmobQuery<Achievement> query = new BmobQuery<Achievement>();
					query.getObject(IssueAchievementActivity.this,
							achi.getObjectId(), new GetListener<Achievement>() {

								@Override
								public void onSuccess(Achievement object) {
									if (isFolderExists(downloadDir))// 如果存在文件夹
									{
										showCustomToast("开始下载:");
										bar.setVisibility(View.VISIBLE);
										// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
										// 初始化DownUtil对象（最后一个参数指定线程数）
										downUtil = new DownUtil(object
												.getFileUrl(), downloadDir
												+ "/" + object.getFilePath(), 6);
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
										// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

									} else {
										showCustomToast("下载失败:文件路径不存在");
									}

								}

								@Override
								public void onFailure(int code, String msg) {
									showCustomToast("下载失败");

								}

							});
					// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

				}

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // resultCode为回传的标记，我在SDFileExploreActivity中回传的是RESULT_OK
		case RESULT_OK:
			fileBundle = data.getExtras(); // data为B中回传的Intent
			filePath = fileBundle.getString("path"); // path即为回传的值
			achievementFilePath.setText(filePath);
			achievementFilePath.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			achievementFilePath.setVisibility(View.VISIBLE);
			achievementFilePathImage.setVisibility(View.VISIBLE);
			iFileSave = true;
			break;
		default:
			break;
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

	/**
	 * 保存修改成果信息的后台任务类
	 */
	private class saveAchiEdited extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {

			String publichArea = achievementPublishArea.getText().toString();
			String firstAuthor = achievementFirstAuthor.getText().toString();
			String otherAuthor = achievementOtherAuthor.getText().toString();
			String achiAbstract = achievementAbstract.getText().toString();

			successTest = 10;
			if (!iFileSave) { // 无添加或修改文件
				achi.setPublishArea(publichArea);
				achi.setFirstAuthor(firstAuthor);
				achi.setOtherAuthor(otherAuthor);
				achi.setAbstract(achiAbstract);
				successTest = 11;
				try {
					successTest = 12;
					achi.update(IssueAchievementActivity.this,
							new UpdateListener() {

								@Override
								public void onSuccess() {
									showCustomToast("保存修改成功");
									successTag = true;
									successTest = 0;
								}

								@Override
								public void onFailure(int arg0, String msg) {
									showCustomToast("保存失败");
									successTag = false;
									successTest = 1;
								}
							});
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				successTest = 21;
				try {
					successTest = 22;
					bmobFile = new BmobFile(new File(filePath));
					successTest = 23;
					bmobFile.uploadblock(IssueAchievementActivity.this,
							new UploadFileListener() {

								@Override
								public void onSuccess() {
									String publichArea = achievementPublishArea
											.getText().toString();
									String firstAuthor = achievementFirstAuthor
											.getText().toString();
									String otherAuthor = achievementOtherAuthor
											.getText().toString();
									String achiAbstract = achievementAbstract
											.getText().toString();
									String achiFileUrl = bmobFile.getFileUrl();

									achi.setPublishArea(publichArea);
									achi.setFirstAuthor(firstAuthor);
									achi.setOtherAuthor(otherAuthor);
									achi.setAbstract(achiAbstract);
									achi.setFilePath(bmobFile.getFilename());
									achi.setFileUrl(achiFileUrl);
									successTest = 24;
									achi.update(IssueAchievementActivity.this,
											new UpdateListener() {

												@Override
												public void onSuccess() {
													iFileSave = false;
													showCustomToast("保存修改成功");
													successTag = true;
													successTest = 2;
												}

												@Override
												public void onFailure(int code,
														String msg) {
													showCustomToast("保存失败");
													successTag = false;
													successTest = 3;
												}
											});
									/*
									 * achievementAbstract.setText(bmobFile.
									 * getFileUrl());
									 */
								}

								@Override
								public void onFailure(int code, String msg) {
									showCustomToast("上传文件失败");
									successTag = false;
									successTest = 4;
								}
							});

					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return successTag;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mFlippingLoadingDialog.dismiss();
			if (result) {

				editBtn.setBackgroundResource(R.drawable.issue_display_button_edit);

				achievementPublishArea.setEnabled(false);
				achievementPublishArea.setTextColor(Color.rgb(255, 255, 255));
				achievementPublishAreaImage
						.setImageResource(R.drawable.issue_display_input_name);
				achievementFirstAuthor.setEnabled(false);
				achievementFirstAuthor.setTextColor(Color.rgb(255, 255, 255));
				achievementFirstAuthorImage
						.setImageResource(R.drawable.issue_display_input_name);
				achievementOtherAuthor.setEnabled(false);
				achievementOtherAuthor.setTextColor(Color.rgb(255, 255, 255));
				achievementOtherAuthorImage
						.setImageResource(R.drawable.issue_display_input_discription);
				achievementAbstract.setEnabled(false);
				achievementAbstract.setTextColor(Color.rgb(255, 255, 255));
				achievementAbstractImage
						.setImageResource(R.drawable.issue_display_input_discription);

				if (filePath == null) {
					achievementLabelFile.setText("文件上传");
					achievementLabelFile.setTextColor(Color.rgb(42, 148, 197));
					fileButton.setEnabled(false);
				} else {
					achievementLabelFile.setText("文件下载");
					achievementLabelFile.setTextColor(Color.rgb(255, 255, 255));
					fileButton.setEnabled(true);
				}

			} else {
				showCustomToast("保存修改失败");
				successTest = 88;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mFlippingLoadingDialog.show();
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
