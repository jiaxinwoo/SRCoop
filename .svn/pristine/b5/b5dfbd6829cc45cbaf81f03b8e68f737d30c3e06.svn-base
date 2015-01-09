package com.srcoop.android.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.srcoop.android.activity.IssueActivity.onNavChangedListener;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.adapter.SpecialAdapter;
import com.srcoop.android.activity.bean.Issue;
import com.srcoop.android.activity.bean.Issue_Student;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Student;
import com.srcoop.android.activity.bean.Student_Issue;
import com.srcoop.android.activity.bean.Teacher;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.dialog.EditTextDialog;
import com.srcoop.android.activity.view.HandyTextView;

/**
 * @author SS 导航栏对应的成员模块
 */
public class IssueMemberFragment extends MyFragment implements
		onNavChangedListener,OnItemLongClickListener {

	private ImageView tportrait;
	private TextView tname;
	private TextView temail;
	private TextView tnumber;
	
	private int deletePosition;

	private BaseDialog mConfirmAddDialog;
	private BaseDialog mConfirmDeleteMember;
	private EditTextDialog mAddMemberDialog;
	
	private TextView mIssueName;
	private Issue mIssue;
	private Student mStudent;
	private BmobFile sFile;	

	private Issue_Student mIssue_student;
	private Student_Issue mStudent_Issue;

	private SpecialAdapter memberadapter;
	private ListView mMemberListView;
	private List<Map<String, Object>> mMemberList = new ArrayList<Map<String, Object>>();

	private String SearchMemberResult;
	
	public void setStudent(Student student) {
		this.mStudent = student;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIssue = (Issue) getActivity().getIntent().getSerializableExtra(
				HomeFragment.ISSUE_TAG);

		BmobQuery<Issue_Student> query_S = new BmobQuery<Issue_Student>();
		query_S.addWhereEqualTo("mIssue", mIssue);
		query_S.findObjects(getActivity(), new FindListener<Issue_Student>() {

			@Override
			public void onSuccess(List<Issue_Student> issue_student) {
				
	
			//String str = String.valueOf(issue_student.size());
			//showCustomToast(str);
			//showCustomToast(issue_student.get(0).getStudent().getObjectId());
				
			for(int i=0;i<issue_student.size();i++)
			{
				String Memberid=issue_student.get(i).getStudent().getObjectId();
				BmobQuery<MyUser> query_ds = new BmobQuery<MyUser>();
				query_ds.getObject(getActivity(),Memberid, new GetListener<MyUser>() {

					    @Override
					    public void onSuccess(MyUser object) {
					        // TODO Auto-generated method stub
					    	
					    	String id=object.getObjectId();
					    	String name=object.getName();
							String email=object.getEmail();
							String tel=object.getTel();
							BmobFile pho=object.getPhoto();
							addData_student(id,name,email,tel,pho);
						
							memberadapter.notifyDataSetChanged();	
					    }

					    @Override
					    public void onFailure(int code, String arg0) {
					        // TODO Auto-generated method stub
					    	showCustomToast("无法完成数据加载！");
					    }

				});
					
	
							
				}
				//for (Issue_Student s : issue_student)

				
				
					
				//sFile.loadImage(getActivity(), spotrait,80,80);
				
	
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_memberlist, null);

		tportrait = (ImageView)v.findViewById(R.id.memberlist_image_tportrait);

		tname = (TextView) v.findViewById(R.id.memberlist_textview_tname);
		temail = (TextView) v.findViewById(R.id.memberlist_textview_temail);
		tnumber = (TextView) v.findViewById(R.id.memberlist_textview_tnumber);

		mAddMemberDialog = new EditTextDialog(getActivity());
		mAddMemberDialog.setTitle("添加成员");
		mAddMemberDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mAddMemberDialog.setHint("输入成员邮箱");
		mAddMemberDialog.setButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String MemberName = mAddMemberDialog.getText();
				if (MemberName == null) {
					mAddMemberDialog.requestFocus();
					showCustomToast("邮箱不能为空");
				} else {
					mAddMemberDialog.dismiss();
					mAddMemberDialog.setTextNull();
					queryMember(MemberName);
					
					// new addMemberThread().execute(MemberName);
				}
			}
		}, "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAddMemberDialog.cancel();
				mAddMemberDialog.setTextNull();
			}
		});
		
		mConfirmAddDialog = new BaseDialog(getActivity());
		mConfirmAddDialog.setTitle("确认信息");
		
		mConfirmAddDialog.setButton1("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				showCustomToast("正在添加用户中");
				addMemberToIssue(mStudent);	
				mConfirmAddDialog.cancel();
			}
		});
		mConfirmAddDialog.setButton2("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mConfirmAddDialog.cancel();
			}
		});
		
		
		mConfirmDeleteMember = new BaseDialog(getActivity());
		mConfirmDeleteMember.setTitle("确认信息");
		mConfirmDeleteMember.setMessage("确认删除该用户吗");
		mConfirmDeleteMember.setButton1("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteMember(deletePosition); 
				mConfirmDeleteMember.cancel();
			}
		});
		mConfirmDeleteMember.setButton2("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mConfirmDeleteMember.cancel();
			}
		});

		

		mIssueName = (TextView) getActivity().findViewById(
				R.id.issue_activity_title);
		// TODO 用Bundle saveIntanceState保存issue，否则issue会报空指针异常
		mIssueName.setText(mIssue.getIssueName());

		mMemberListView = (ListView) v.findViewById(R.id.temp_memberlist);
		memberadapter = new SpecialAdapter(getActivity(), mMemberList,
				R.layout.memberlist_item, new String[] { "name", "email",
						"number" }, new int[] { 
			
			R.id.memberlist_textview_name,
						R.id.memberlist_textview_email,
						R.id.memberlist_textview_number });
		
		mMemberListView.setAdapter(memberadapter);
		
		mMemberListView.setOnItemLongClickListener(this);
		
		String teacherId=mIssue.getTeacher().getObjectId();
		String S_tname,S_tnumber;
		
		if(mIssue.getTeacher().getName()==""||mIssue.getTeacher().getName()==null)
		{
			S_tname="未设定";
		}
		else
		{
			S_tname=mIssue.getTeacher().getName();
		}
		
		if(mIssue.getTeacher().getTel()==""||mIssue.getTeacher().getTel()==null)
		{
			S_tnumber="未设定";
		}
		else
		{
			S_tnumber=mIssue.getTeacher().getTel();
		}
		tname.setText(S_tname + "");
		temail.setText( mIssue.getTeacher().getEmail()+ "");
		tnumber.setText(S_tnumber + "");
		
		BmobQuery<Teacher> query_t = new BmobQuery<Teacher>();
		query_t.getObject(getActivity(),teacherId, new GetListener<Teacher>() {

		    @Override
		    public void onSuccess(Teacher object) {
		        // TODO Auto-generated method stub
				tname.setText(object.getName()+"");
				temail.setText(object.getEmail()+"");
				tnumber.setText(object.getTel()+"");
				if(object.getPhoto()!=null)
				{
					BmobFile bmobFile=object.getPhoto();			
					bmobFile.loadImage(getActivity(), tportrait,80,80);
					
				}				
		    }

		    @Override
		    public void onFailure(int code, String arg0) {
		        // TODO Auto-generated method stub
		    	showCustomToast("获取教师信息失败！");
		    }

		});
		return v;
	}
	


	private void queryMember(String email) {
		BmobQuery<Student> query_student = new BmobQuery<Student>();
		query_student.addWhereEqualTo("username", email);
		query_student.findObjects(getActivity(), new FindListener<Student>() {

			@Override
			public void onSuccess(List<Student> objects) {
				mStudent = objects.get(0);

				BmobQuery<Issue_Student> query_i_s = new BmobQuery<Issue_Student>();
				query_i_s.addWhereEqualTo("mIssue", mIssue);
				query_i_s.addWhereEqualTo("mStudent", mStudent);
				query_i_s.findObjects(getActivity(),
						new FindListener<Issue_Student>() {
							@Override
							public void onSuccess(List<Issue_Student> objects) {
								if (objects.size() > 0)
									showCustomToast("该学生已存在");
								else {
									if(mStudent.getName()!="" ||mStudent.getName()!= null)
									{
										SearchMemberResult="是否确认添加‘"+mStudent.getName()+"'为成员";	
									}
									else
									{
										SearchMemberResult="是否确认添加‘"+"未命名用户"+"'为成员";
									}
									mConfirmAddDialog.setMessage(SearchMemberResult);
									mConfirmAddDialog.show();									
								}
							}

							@Override
							public void onError(int arg0, String arg1) {
								showCustomToast("发生未知错误:"+arg1);
							}
						});

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				showCustomToast("无法找到该学生" + arg1);
			}

		});

	}

	//
	private void addMemberToIssue(Student student) {
		mIssue_student = new Issue_Student();
		mStudent_Issue= new Student_Issue();
	
		mIssue_student.setIssue(mIssue);
		mIssue_student.setStudent(mStudent);
		
		mStudent_Issue.setIssue(mIssue);
		mStudent_Issue.setStudent(mStudent);

		mIssue_student.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				BmobRelation Issue_student = new BmobRelation();
				Issue_student.add(mIssue_student);

				mIssue.setIssue_Student(Issue_student);
				mStudent.setStudent_Issue(Issue_student);
				
				//showCustomToast("mIssue.update");
				
				mIssue.update(getActivity(), new UpdateListener() {
					@Override
					public void onSuccess() {}

					@Override
					public void onFailure(int arg0, String arg1) 
					{showCustomToast("添加成员失败" + arg1);
					}
				});
			}
			@Override
			public void onFailure(int arg0, String arg1) {
				showCustomToast("添加成员失败" + arg1);
			}
		});
		
		
		mStudent_Issue.save(getActivity(), new SaveListener() {
			@Override
			public void onSuccess() {
				BmobRelation Student_issue = new BmobRelation();
				Student_issue.add(mStudent_Issue);

				mStudent.setStudent_Issue(Student_issue);
				
				mIssue.update(getActivity(), new UpdateListener() {
					@Override
					public void onSuccess() {
						addData_student(mStudent.getObjectId(),mStudent.getName(),
								mStudent.getEmail(), mStudent.getTel(),mStudent.getPhoto()
								);
						


						memberadapter.notifyDataSetChanged();
						showCustomToast("添加成员成功");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						showCustomToast("添加成员失败" + arg1);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) 
			{showCustomToast("添加成员失败" + arg1);}});

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

	private List<Map<String, Object>> addData_student(String id,String name,
			String email, String number,BmobFile image)
			{
		Map<String, Object> map = new HashMap<String, Object>();
		if(name==""||name==null)
		{
			name="未设定";
		}
		
		if(number==""||number==null)
		{
			number="未设定";
		}
		map.put("id",id);
		map.put("name", name);
		map.put("email", email);
		map.put("number", number);
		mMemberList.add(map);
		return mMemberList;
	}

	@Override
	public void changeAddBtn() {
		mAddMemberDialog.show();
	}
	
	
	private void deleteMember(final int position) 
	{
		Map<String, Object> d_map = mMemberList.get(position);
		String DmemberId=(String) d_map.get("id");
				
		BmobQuery<Student> query_ds = new BmobQuery<Student>();
		query_ds.getObject(getActivity(),DmemberId, new GetListener<Student>() {

		    @Override
		    public void onSuccess(Student object) {
		        // TODO Auto-generated method stub
			
		    	Student s=object;
		    	s.delete(getActivity(), new DeleteListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						showCustomToast("成员删除成功");
						//mMemberList.remove(position+1);
						memberadapter.notifyDataSetChanged();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}

				});

		    }

		    @Override
		    public void onFailure(int code, String arg0) {
		        // TODO Auto-generated method stub
		    	showCustomToast("未知错误！");
		    }

		});
	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		deletePosition=position;
		mConfirmDeleteMember.show();

		return false;
	}
	
}


