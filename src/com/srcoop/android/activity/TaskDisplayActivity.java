package com.srcoop.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.srcoop.android.activity.adapter.SimpleListDialogAdapter;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.bean.Issue_Student;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Task;
import com.srcoop.android.activity.dialog.FlippingLoadingDialog;
import com.srcoop.android.activity.dialog.SimpleListDialog;
import com.srcoop.android.activity.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.srcoop.android.activity.view.HandyTextView;

public class TaskDisplayActivity extends Activity implements onSimpleListItemClickListener {

	private int totalSeletedCarrier;
	private ArrayList<Integer> selectedIndex;
	private Button taskEditBtn; // 编辑按钮
	private Button taskBackBtn; // 返回按钮
	private Button addCarrierBtn;//添加执行者按钮
	private EditText taskEditTitle;
	private EditText taskEditDepict;
	private ImageView taskEditTitleImage;
	private ImageView taskEditDepictImage;
	private ListView memListView; // 成员信息
	// false表示当前状态是编辑状态左上角图标此时应为issue_edit_button_confirm.png
	// true表示当前状态为默认状态即不可编辑，左上角图标应此时为issue_display_button_edit
	private boolean iEditOrSave = false;
	private Task currentTask;
	
	private SimpleListDialog mSimpleListDialog;

	
	private ArrayList<MyUser> candidateCarrierList = new ArrayList<MyUser>();// 当前任务对应所有可添加的执行者的集合
	private ArrayList<MyUser> selectedCarrierList = new ArrayList<MyUser>();// 选中的执行者的集合
	private ArrayList<String> candidateCarrierListName = new ArrayList<String>();
	
	private CarrierAdapter carrierAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_task_display);

		//获取传来的参数
		Intent intent  = getIntent();
		Bundle bundle = intent.getExtras();
		currentTask = (Task)bundle.get("currentTask");
	
		//设置执行者的listView
		carrierAdapter = new CarrierAdapter(selectedCarrierList);
		memListView = (ListView) findViewById(R.id.memberListView);
		memListView.setAdapter(carrierAdapter);
		//modifyH(memListView);

		// 初始化
		taskEditBtn = (Button) findViewById(R.id.task_display_edit_w);
		taskBackBtn = (Button)findViewById(R.id.task_display_back_w);
		addCarrierBtn = (Button)findViewById(R.id.bt_taskDisplay_addCarrier);
		taskEditTitle = (EditText) findViewById(R.id.et_task_title);
		taskEditDepict = (EditText) findViewById(R.id.et_task_depict);
		taskEditTitleImage = (ImageView) findViewById(R.id.task_title_image);
		taskEditDepictImage = (ImageView) findViewById(R.id.task_depict_image);
		
		selectedIndex = new ArrayList<Integer>();
		//按钮为不可见
		addCarrierBtn.setVisibility(View.GONE);
		
		//设置初始信息
		initMessage();
		//设置点击事件
		taskEditBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!iEditOrSave) {
					taskEditBtn
							.setBackgroundResource(R.drawable.issue_edit_button_confirm);
					taskEditTitle.setEnabled(true);
					taskEditTitle.setTextColor(Color.rgb(42, 148, 197));
					taskEditDepict.setEnabled(true);
					taskEditDepict.setTextColor(Color.rgb(42, 148, 197));
					taskEditTitleImage
							.setImageResource(R.drawable.issue_edit_input_name);
					taskEditDepictImage
							.setImageResource(R.drawable.issue_edit_input_discription);
					addCarrierBtn.setVisibility(View.VISIBLE);
					
				} else {
					if(!taskEditTitle.getText().toString().isEmpty()){
						if(!taskEditDepict.getText().toString().isEmpty()){
							//设置样式为显示状态
							taskEditBtn.setBackgroundResource(R.drawable.issue_display_button_edit);
							taskEditTitle.setEnabled(false);
							taskEditTitle.setTextColor(Color.rgb(255, 255, 255));
							taskEditDepict.setEnabled(false);
							taskEditDepict.setTextColor(Color.rgb(255, 255, 255));
							taskEditTitleImage
									.setImageResource(R.drawable.issue_display_input_name);
							taskEditDepictImage
									.setImageResource(R.drawable.issue_display_input_discription);
							addCarrierBtn.setVisibility(View.GONE);
							
							
							String title = taskEditTitle.getText().toString();
							String descrip = taskEditDepict.getText().toString();
							
							currentTask.setTaskName(title);
							currentTask.setTaskContent(descrip);
							//save carrier
							BmobRelation tCarriers = new BmobRelation();
							for(int i=0;i<selectedCarrierList.size();i++){
								tCarriers.add(selectedCarrierList.get(i));
							}
							if(selectedCarrierList.size()>0){
								currentTask.setCarriers(tCarriers);
							}
							currentTask.update(TaskDisplayActivity.this,new UpdateListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									showCustomToast("保存成功");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									
								}
							});
						}else{
							showCustomToast("任务描述不能为空");
						}
					}else{
						showCustomToast("任务名不能为空");
					}
				}
				iEditOrSave = !iEditOrSave;
			}
		});
		taskBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//back to task page
				Intent intent = new Intent(TaskDisplayActivity.this,IssueTaskActivity.class);
				Bundle bundle  =  new Bundle();
				bundle.putSerializable("currentTask", currentTask);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		addCarrierBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//清空之前添加的执行者，temp use
				//selectedCarrierList.clear();
				mSimpleListDialog = new SimpleListDialog(TaskDisplayActivity.this);
				mSimpleListDialog.setTitle("添加执行者");
				mSimpleListDialog.setTitleLineVisibility(View.GONE);
				// TODO 添加执行者
				
				mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(
						TaskDisplayActivity.this, candidateCarrierListName));
				mSimpleListDialog
						.setOnSimpleListItemClickListener(TaskDisplayActivity.this);
				mSimpleListDialog.setButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								String all = "";
								for (MyUser user : selectedCarrierList) {
									//若没有设置名称
									if(user.getName()==null)
										all+=user.getUsername() + " ";
									else
										all += user.getName() + " ";
								}
								if (selectedCarrierList.size() == 0) {
									showCustomToast("未添加执行者");
								} else {
									showCustomToast("已添加执行者:" + all);
									//静态显示默认执行者
									mSimpleListDialog.dismiss();
									carrierAdapter.notifyDataSetChanged();
								}
								for(int i=0;i<selectedCarrierList.size();i++){
									candidateCarrierList.remove(selectedCarrierList.get(i));
								}
								candidateCarrierListName.clear();
								for(int i=0;i<candidateCarrierList.size();i++){
									if(candidateCarrierList.get(i).getName()!=null)
										candidateCarrierListName.add(candidateCarrierList.get(i).getName());
									else{
										candidateCarrierListName.add(candidateCarrierList.get(i).getUsername());
									}
								}
								totalSeletedCarrier = selectedCarrierList.size();
							}
						}, "取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mSimpleListDialog.cancel();
							}
						});
				mSimpleListDialog
						.setButton1Background(R.drawable.btn_default_popsubmit);
				if(candidateCarrierListName.size()==0&&totalSeletedCarrier==0){
					showCustomToast("课题成员人数为0，请先添加课题成员，再分配任务");
				}
				if(candidateCarrierList.size()>0){
					mSimpleListDialog.show();
				}
				if(candidateCarrierList.size()==0&&totalSeletedCarrier>0){
					showCustomToast("全部课题成员已经被添加");
				}
			}
		});
	}
	private void initMessage(){
		if(currentTask!=null){
			taskEditTitle.setText(currentTask.getTaskName());
			taskEditDepict.setText(currentTask.getTaskContent());
			//查找任务的执行者
			findTaskCarrier();
			//查找课题成员
			findIssueStudent();
		}
	}
	//查找课题的成员
	private void findIssueStudent()
	{
		Issue issue = currentTask.getIssue();
		BmobQuery<Issue_Student> issueStudent = new BmobQuery<Issue_Student>();
		issueStudent.addWhereRelatedTo("mIssue_Student", new BmobPointer(issue));
		issueStudent.findObjects(TaskDisplayActivity.this, new FindListener<Issue_Student>() {
			
			@Override
			public void onSuccess(List<Issue_Student> issueStudentList) {
				// TODO Auto-generated method stub
				showCustomToast("total issue student:"+issueStudentList.size());
				for(Issue_Student iStudent : issueStudentList){
					MyUser user = iStudent.getStudent();
					BmobQuery<MyUser> userInfo = new BmobQuery<MyUser>();
					userInfo.getObject(TaskDisplayActivity.this, user.getObjectId(), new GetListener<MyUser>() {
						
						@Override
						public void onSuccess(MyUser myuser) {
							// TODO Auto-generated method stub
							int flag=0;
							for(int i=0;i<selectedCarrierList.size();i++){
								if(myuser.getObjectId().equals(selectedCarrierList.get(i).getObjectId())){
									flag=1;
									break;
								}
							}
							if(flag==0)
								candidateCarrierList.add(myuser);
							//
							candidateCarrierListName.clear();
							for(int i=0;i<candidateCarrierList.size();i++){
								MyUser user = candidateCarrierList.get(i);
								if(user.getName()==null)
									candidateCarrierListName.add(user.getUsername());
								else
									candidateCarrierListName.add(user.getName());
							}
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							showCustomToast("查找课题成员失败");
						}
					});
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	//查找任务的执行者
	private void findTaskCarrier()
	{
		BmobQuery<MyUser> taskCarrier = new BmobQuery<MyUser>();
		taskCarrier.addWhereRelatedTo("mCarriers", new BmobPointer(currentTask));
		taskCarrier.findObjects(TaskDisplayActivity.this, new FindListener<MyUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<MyUser> taskCarrierList) {
				// TODO Auto-generated method stub
				
				for(int i=0;i<taskCarrierList.size();i++)
					selectedCarrierList.add(taskCarrierList.get(i));
				carrierAdapter.notifyDataSetChanged();
			}
		});
	}
	// 2.写MyAdapter继承SimpleAdapter
	class CarrierAdapter extends ArrayAdapter<MyUser> {

		private ArrayList<MyUser> carrierList;
		
		public CarrierAdapter(List<MyUser> objects) {
			super(TaskDisplayActivity.this,0,objects);
			carrierList = (ArrayList<MyUser>) objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = TaskDisplayActivity.this.getLayoutInflater().inflate(
						R.layout.activity_issue_member_style, null);
			}
			TextView tv_studentName =(TextView)convertView.findViewById(R.id.student_name);
			TextView tv_studentNum =(TextView)convertView.findViewById(R.id.student_num);
			MyUser  user = carrierList.get(position);
			if(user.getName()==null)
				tv_studentName.setText(user.getUsername());
			else
				tv_studentName.setText(user.getName());
			if(user.getTel()==null)
				tv_studentNum.setText("他还没有设置哦~");
			else
				tv_studentNum.setText(user.getTel());
			
			return convertView;
		}
		
	}

	public void modifyH(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		int rows = adapter.getCount();
		int totalheight = 0;
		for (int i = 0; i < rows; i++) {
			View view = adapter.getView(i, null, listView);

			view.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);

			totalheight += view.getMeasuredHeight();
		}
		float gap = listView.getDividerHeight() * (rows - 1);
		totalheight += gap;
		LayoutParams pa = listView.getLayoutParams();
		pa.height = totalheight;
		listView.setLayoutParams(pa);
	}
	@Override
	public void onItemClick(int position, View v) {
		//添加执行者选择框
		ImageView selectIcon = (ImageView) v
				.findViewById(R.id.listitem_dialog_icon);
		if (selectIcon.getVisibility() == View.GONE) {
			selectIcon.setVisibility(View.VISIBLE);
			selectedIndex.add(position);
			selectedCarrierList.add(candidateCarrierList.get(position));
		} else {
			selectIcon.setVisibility(View.GONE);
			selectedCarrierList.remove(candidateCarrierList.get(position));
			int index =selectedIndex.indexOf(position);
			selectedIndex.remove(index);
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