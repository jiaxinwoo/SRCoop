package com.srcoop.android.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.srcoop.android.activity.bean.MyUser;
import com.srcoop.android.activity.bean.UserImage;
import com.srcoop.android.activity.fragment.HomeFragment;
import com.srcoop.android.activity.fragment.NoticeFragment;
import com.srcoop.android.activity.fragment.ProfileFragment;
import com.srcoop.android.activity.fragment.SettingFragment;
import com.srcoop.android.activity.view.CircleImageView;
import com.srcoop.android.activity.view.HandyTextView;

public class SrcoopActivity extends FragmentActivity implements
		View.OnClickListener {

	private ResideMenu mResideMenu;
	private ResideMenuItem itemProfile;
	private ResideMenuItem itemHome;
	private ResideMenuItem itemNotice;
	private ResideMenuItem itemSetting;

	private ProfileFragment mProfileFragment;
	private NoticeFragment mNoticeFragment;
	private SettingFragment mSettingFragment;
	private int mType;
	@SuppressWarnings("unused")
	private String mId;
	private MyUser c_myuser;
	private long mExitTime = 0;

	private CircleImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_srcoop);

		setUpMenu();

		if (savedInstanceState == null)
			changeFragment(new HomeFragment());

		c_myuser = BmobUser.getCurrentUser(this, MyUser.class);
		mType = c_myuser.getType();

		// 查询图片
		BmobQuery<UserImage> imageQuery = new BmobQuery<UserImage>();
		String uid = c_myuser.getObjectId();
		imageQuery.addWhereEqualTo("userid", uid);
		imageQuery.order("-updatedAt");
		imageQuery.findObjects(SrcoopActivity.this,
				new FindListener<UserImage>() {

					@Override
					public void onSuccess(List<UserImage> imageList) {
						// TODO Auto-generated method stub
						if (!imageList.isEmpty()) {
							c_myuser.setPhoto(imageList.get(0).getImage());
							BmobUser.getCurrentUser(SrcoopActivity.this,
									MyUser.class).setPhoto(
									imageList.get(0).getImage());
							//showCustomToast("查询图像成功");
						}

						// //设置图片
						imageView = (CircleImageView) itemProfile
								.findViewById(R.id.iv_icon);
						LayoutParams params = imageView.getLayoutParams();
						params.width *= 1.8;
						params.height *= 1.8;
						imageView.setLayoutParams(params);
						if (BmobUser.getCurrentUser(SrcoopActivity.this,
								MyUser.class).getPhoto() != null) {
							BmobUser.getCurrentUser(SrcoopActivity.this,
									MyUser.class).getPhoto()
									.loadImage(SrcoopActivity.this, imageView);
							// showCustomToast(c_myuser.getPhoto().getFilename());
						} else {
							imageView.setImageResource(R.drawable.blade);
						}
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});

	}

	@SuppressWarnings("unused")
	private void getcurrentuser() {
		BmobUser currentuser = BmobUser.getCurrentUser(this);
		String mId = currentuser.getObjectId();

		//Toast toast = Toast.makeText(SrcoopActivity.this, "获取type：" + mType
				//+ "id:" + mId, Toast.LENGTH_SHORT);
		//toast.show();
		getType(mId);
		// toast2 = Toast.makeText(SrcoopActivity.this,"获取type："+mType,
		// Toast.LENGTH_SHORT);
		// toast2.show();
	}

	private void getType(String mId) {
		BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
	//	Toast toast = Toast.makeText(SrcoopActivity.this, "传入参数" + mId,
		//		Toast.LENGTH_SHORT);
		//toast.show();
		query.getObject(SrcoopActivity.this, mId, new GetListener<BmobUser>() {

			@Override
			public void onSuccess(BmobUser object) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(SrcoopActivity.this, "查询成功！！！！！",
						Toast.LENGTH_SHORT);
				toast.show();
				// Toast toast3 =
				// Toast.makeText(SrcoopActivity.this,"获取id："+object.getType(),
				// Toast.LENGTH_SHORT);
				// toast3.show();
				// mType=object.getType();

			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(SrcoopActivity.this, "失败!!!!!!",
						Toast.LENGTH_SHORT);
				toast.show();
			}

		});

	}

	private void setUpMenu() {
		mResideMenu = new ResideMenu(this);
		mResideMenu.setBackground(R.drawable.menu_background);
		mResideMenu.attachToActivity(this);
		mResideMenu.setMenuListener(menuListener);
		mResideMenu.setScaleValue(0.63f);

		itemProfile = new ResideMenuItem(this);

		/*
		 * Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		 * R.drawable.icon_profile_photo);
		 */
		/*
		 * Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		 * R.drawable.blade);
		 */
		/* imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, 2)); */
		itemProfile.setTitle("昵称");

		itemHome = new ResideMenuItem(this, R.drawable.icon_home, "首页");
		itemNotice = new ResideMenuItem(this, R.drawable.icon_notice, "通知");
		itemSetting = new ResideMenuItem(this, R.drawable.icon_settings, "设置");

		itemProfile.setOnClickListener(this);
		itemHome.setOnClickListener(this);
		itemNotice.setOnClickListener(this);
		itemSetting.setOnClickListener(this);

		mResideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
		mResideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		mResideMenu.addMenuItem(itemNotice, ResideMenu.DIRECTION_LEFT);
		mResideMenu.addMenuItem(itemSetting, ResideMenu.DIRECTION_LEFT);

		mProfileFragment = new ProfileFragment();
		mNoticeFragment = new NoticeFragment();
		mSettingFragment = new SettingFragment();

		mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		findViewById(R.id.title_bar_left_menu).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
					}
				});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mResideMenu.dispatchTouchEvent(ev);
	}

	// @Override
	// public void onBackPressed() {
	// if (ProfileFragment.save) {
	// ProfileFragment.getDialog(this, "是否保存", "是", "否",
	// ProfileFragment.SAVE_PROFILE).show();
	// ProfileFragment.save = false;
	// } else {
	// super.onBackPressed();
	// }
	// }

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
		}

		@Override
		public void closeMenu() {
		}
	};

	@Override
	public void onClick(View v) {
		if (v == itemProfile) {
			changeFragment(mProfileFragment);
		} else if (v == itemHome) {
			changeFragment(new HomeFragment());
		} else if (v == itemNotice) {
			changeFragment(mNoticeFragment);
		} else if (v == itemSetting) {
			changeFragment(mSettingFragment);
		}
		mResideMenu.closeMenu();
	}

	private void changeFragment(Fragment targetFragment) {
		mResideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}

	@SuppressWarnings("unused")
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
				bitmap.getHeight() / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				showCustomToast("再按一次退出~");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 显示自定义Toast提示(来自String) **/
	private void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(300);
		toast.setView(toastRoot);
		toast.show();
	}
}
