package com.srcoop.android.activity.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.Student;
import com.srcoop.android.activity.bean.Teacher;
import com.srcoop.android.activity.bean.UserImage;
import com.srcoop.android.activity.dialog.BaseDialog;
import com.srcoop.android.activity.view.CircleImageView;
import com.srcoop.android.activity.view.HandyTextView;

public class ProfileFragment extends MyFragment implements View.OnClickListener {

	private InputMethodManager imm;

	private TextView mEmail;
	private EditText mPhone;
	private TextView mJob;
	private EditText mUsername;
	private Button mMenuRightBtn;
	private CircleImageView mPhoto;
	private LinearLayout layout = null;
	private MyUser currentUser;
	
	public static final int SELECT_PHOTO = 0;
	public static final int SAVE_PROFILE = 1;

	private static final int SELECT_FROM_CAMERA = 0;
	private static final int SELECT_FROM_FILE = 1;

	public static boolean save = false;
	private int k = 0;
	private boolean hasChangeImage;
	
	private byte[] mContent;
	private String imagePath;
	Bitmap myBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
		currentUser=BmobUser.getCurrentUser(getActivity(), MyUser.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((TextView) getActivity().findViewById(R.id.activity_title))
				.setText("个人信息");
	

		View v = inflater.inflate(R.layout.fragment_menuitem_profile,
				container, false);

		mEmail = (TextView) v.findViewById(R.id.tv_profile_email);
		mPhone = (EditText) v.findViewById(R.id.et_profile_phone);
		mJob = (TextView) v.findViewById(R.id.tv_profile_job);
		mUsername = (EditText) v.findViewById(R.id.et_profile_username);
		initEditTextChangedListener();
		mPhoto = (CircleImageView) v.findViewById(R.id.iv_profile_selectphoto);
		mPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getDialog(getActivity(), "选择头像", "相机", "相册", SELECT_PHOTO)
						.show();
			}
		});

		layout = (LinearLayout) v.findViewById(R.id.profile_layout);
		layout.setFocusable(true);
		layout.setFocusableInTouchMode(true);
		layout.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				layout.requestFocus();
				imm.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
				//imm.hideSoftInputFromWindow(mJob.getWindowToken(), 0);
				return false;
			}
		});
		//初始化信息
		initProfileInfo();
		
		
		setEditAble(mUsername);
		setEditAble(mPhone);
		//取消职业的可编辑 ---wjx
		//setEditAble(mJob);

		//setMenuRightBtn unclickable
		mMenuRightBtn = ((Button) getActivity().findViewById(
				R.id.title_bar_right_menu));
		mMenuRightBtn.setClickable(false);
		mMenuRightBtn
				.setBackgroundResource(R.drawable.menu_right_save_disable);
		mMenuRightBtn.setOnClickListener(null);
		return v;
	}

	//初始化用户信息
	private void initProfileInfo(){
		//在服务器中查找指定用户信息
		final String userid = currentUser.getObjectId();
		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.getObject(getActivity(), userid, new GetListener<MyUser>() {
			
			@Override
			public void onSuccess(MyUser user) {
				// TODO Auto-generated method stub
				
				mUsername.setText(user.getName());
				currentUser.setName(user.getName());
				mEmail.setText(user.getEmail());
				mPhone.setText(user.getTel());
				currentUser.setTel(user.getTel());
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		//设置头像
		if(currentUser.getPhoto()!=null){
			currentUser.getPhoto().loadImage(getActivity(), mPhoto);
		}else{
			mPhoto.setImageResource(R.drawable.blade);
		}
	
		if(currentUser.getType()==0){
			mJob.setText("老师");
		}else{
			mJob.setText("学生");
		}
		//初始化为未修改图像
		hasChangeImage = false;
	}
	private void initEditTextChangedListener() {
		mUsername.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mMenuRightBtn.setClickable(true);
				mMenuRightBtn.setOnClickListener(ProfileFragment.this);
				mMenuRightBtn.setBackgroundResource(R.drawable.menu_right_save);
				k++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mPhone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mMenuRightBtn.setClickable(true);
				mMenuRightBtn.setOnClickListener(ProfileFragment.this);
				mMenuRightBtn.setBackgroundResource(R.drawable.menu_right_save);
				k++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void setEditAble(final EditText editText) {
		editText.setEnabled(true);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setSelection(editText.getText().length()); // 光标移动最后
		editText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editText.setFocusableInTouchMode(true);
				editText.setFocusable(true);
				editText.requestFocus();
				imm.showSoftInput(editText, 0); // 第二次点击后显示软键盘
				// imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		});
	}

	public BaseDialog getDialog(Context context, String msg, String leftBtn,
			String rightBtn, final int state) {
		BaseDialog baseDialog = BaseDialog.getDialog(context, "提示", msg,
				leftBtn, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (state == SAVE_PROFILE) {
						} else if (state == SELECT_PHOTO) {
							Intent intent = new Intent(
									"android.media.action.IMAGE_CAPTURE");
							startActivityForResult(intent, SELECT_FROM_CAMERA);
						}
						dialog.dismiss();
					}
				}, rightBtn, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (state == SAVE_PROFILE) {
							dialog.cancel();
						} else if (state == SELECT_PHOTO) {
							Intent intent = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, SELECT_FROM_FILE);
							dialog.dismiss();
						}
					}
				});
		if (state == SAVE_PROFILE)
			baseDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		return baseDialog;
	}

	@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		ContentResolver resolver = getActivity().getContentResolver();
		if (data != null) {
			if (requestCode == SELECT_FROM_FILE) {

				try {
					// 获得图片的uri
					Uri originalUri = data.getData();
					imagePath = originalUri.toString();//originalUri.getPath();
					// 将图片内容解析成字节数组
					mContent = readStream(resolver.openInputStream(Uri
							.parse(originalUri.toString())));
					// 将字节数组转换为ImageView可调用的Bitmap对象
					myBitmap = getPicFromBytes(mContent, null);
					//写入 sdcard方便上传
					String sdStatus = Environment.getExternalStorageState();
					if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
						return;
					}
					File file = new File("/sdcard/myImage/");
					file.mkdirs();// 创建文件夹，名称为myimage
					// 照片的命名，tempImage
					String fileName = "/sdcard/myImage/" + "tempImage" + ".jpg";
					//写入
					FileOutputStream b = null;
					try {
						b = new FileOutputStream(fileName);
						myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					imagePath =fileName;
					//把得到的图片绑定在控件上显示
					mPhoto.setImageBitmap(myBitmap);
					mMenuRightBtn.setClickable(true);
					mMenuRightBtn.setOnClickListener(ProfileFragment.this);
					mMenuRightBtn
							.setBackgroundResource(R.drawable.menu_right_save);
					k++;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else if (requestCode == SELECT_FROM_CAMERA) {

				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					return;
				}
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				FileOutputStream b = null;
				File file = new File("/sdcard/myImage/");
				file.mkdirs();// 创建文件夹，名称为myimage

				// 照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。
				String str = null;
				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取当前时间，进一步转化为字符串
				date = new Date();
				str = format.format(date);
				String fileName = "/sdcard/myImage/" + str + ".jpg";
				try {
					b = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (b != null) {
							b.flush();
							b.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (data != null) {
						Bitmap cameraBitmap = (Bitmap) data.getExtras().get(
								"data");
						System.out.println("fdf================="
								+ data.getDataString());
						mPhoto.setImageBitmap(cameraBitmap);
						mMenuRightBtn.setClickable(true);
						mMenuRightBtn.setOnClickListener(ProfileFragment.this);
						mMenuRightBtn
								.setBackgroundResource(R.drawable.menu_right_save);
						k++;

						System.out.println("成功======" + cameraBitmap.getWidth()
								+ cameraBitmap.getHeight());
					}
					imagePath = fileName;

				}
			}
			showCustomToast(imagePath, getActivity());
			//标志已修改图像
			hasChangeImage = true;
		}
	}

	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	@Override
	public void onClick(View v) {
		if (v == mMenuRightBtn && k != 0) {
			//保存信息到服务器
			String username = mUsername.getText().toString();
			String tel = mPhone.getText().toString();
			//验证手机是否符合格式要求
			if(Pattern.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", tel)){
				//判断是否更改了图像，有则上传
				if(hasChangeImage){
					uploadUserImage(currentUser.getObjectId(),imagePath);
				}
				currentUser.setName(username);
				Context context = ProfileFragment.this.getActivity();
				currentUser.update(context,new UpdateListener(){

					@Override
					public void onFailure(int arg0,
							String arg1) {
						showCustomToast("修改失败");
						
					}

					@Override
					public void onSuccess() {
						showCustomToast("修改成功");
						imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
						//showCustomToast("保存成功");
						mUsername.setFocusable(false);
						mPhone.setFocusable(false);
						mMenuRightBtn.setClickable(false);
						mMenuRightBtn
								.setBackgroundResource(R.drawable.menu_right_save_disable);
					}
					
				});
			}else{
				showCustomToast("手机格式不正确，请重新输入");
			}
		}
	}

	private String uploadUserImage(final String userid2, String imagePath) {
		final BmobFile bmobFile = new BmobFile(new File(imagePath));
		final Context context = getActivity();
		final String result = null;
		bmobFile.uploadblock(context, new UploadFileListener(){
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void onSuccess() {
				//上传文件成功
				UserImage userImage = new UserImage(userid2,bmobFile);
				userImage.save(context, new SaveListener(){
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						showCustomToast("上传图片成功", context);
					}
					
				});
				
			}
			
		});
		return result;
		
		
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

}
