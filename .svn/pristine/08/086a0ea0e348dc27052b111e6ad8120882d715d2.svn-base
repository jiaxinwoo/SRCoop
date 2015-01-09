package com.srcoop.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.SlideCallback;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.view.ViewHelper;

@SuppressLint("NewApi")
public class IssueActivity extends FragmentActivity {

	public interface onNavChangedListener {
		public void changeAddBtn();
	}

	private onNavChangedListener mNavListener;

	private int currentNav = 0;

	@ViewInject(R.id.jazzyPager)
	private JazzyViewPager jazzyPager;
	private TabHost tabHost;
	private Button mBackBtn;
	private Button mAddBtn;
	private TextView mTvIssueName;

	private List<Map<String, View>> tabViews = new ArrayList<Map<String, View>>();
	private List<Fragment> navFragments = new ArrayList<Fragment>();

	private final String packName = "com.srcoop.android.activity.fragment";
	private final String[] issueNavNames = new String[] {
			packName + ".IssueTaskFragment", packName + ".IssueAchiFragment",
			packName + ".IssueFileFragment", packName + ".IssueMemberFragment" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue);
		ViewUtils.inject(this);
		mBackBtn = (Button) findViewById(R.id.title_bar_left_back);
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IssueActivity.this,
						SrcoopActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		mTvIssueName = (TextView) findViewById(R.id.issue_activity_title);
		mTvIssueName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IssueActivity.this,
						IssueDisplayActivity.class);
				startActivity(intent);
			}
		});
		mAddBtn = (Button) findViewById(R.id.btn__add);
		mAddBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNavListener.changeAddBtn();
			}
		});

		initFragment();

		// --------------------
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("0").setIndicator(createTab("任务", 0))
				.setContent(android.R.id.tabcontent));
		tabHost.addTab(tabHost.newTabSpec("1").setIndicator(createTab("成果", 1))
				.setContent(android.R.id.tabcontent));
		tabHost.addTab(tabHost.newTabSpec("2").setIndicator(createTab("文档", 2))
				.setContent(android.R.id.tabcontent));
		tabHost.addTab(tabHost.newTabSpec("3").setIndicator(createTab("成员", 3))
				.setContent(android.R.id.tabcontent));
		// 点击tabHost 来切换不同的消息
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int index = Integer.parseInt(tabId);
				setTabSelectedState(index, 4);
				tabHost.getTabContentView().setVisibility(View.GONE);// 隐藏content
				currentNav = index;
				changeListener();
			}
		});
		tabHost.setCurrentTab(currentNav);
		changeListener();
		initJazzyPager(TransitionEffect.Accordion);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			mNavListener = (onNavChangedListener) navFragments.get(currentNav);
		} catch (Exception e) {
			throw new ClassCastException(fragment.toString()
					+ " must implements onChangeNavListener");
		}
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNavListener = null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setTabSelectedState(tabHost.getCurrentTab(), 4);
	}

	private View createTab(String tabLabelText, int tabIndex) {
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.main_tabwidget_layout, null);
		TextView normalTv = (TextView) tabIndicator.findViewById(R.id.normalTV);
		TextView selectedTv = (TextView) tabIndicator
				.findViewById(R.id.selectedTV);
		normalTv.setText(tabLabelText);
		selectedTv.setText(tabLabelText);
		ImageView normalImg = (ImageView) tabIndicator
				.findViewById(R.id.normalImg);
		ImageView selectedImg = (ImageView) tabIndicator
				.findViewById(R.id.selectedImage);
		switch (tabIndex) {
		case 0:
			normalImg.setImageResource(R.drawable.scan_book);
			selectedImg.setImageResource(R.drawable.scan_book_hl);
			break;
		case 1:
			normalImg.setImageResource(R.drawable.scan_qr);
			selectedImg.setImageResource(R.drawable.scan_qr_hl);
			break;
		case 2:
			normalImg.setImageResource(R.drawable.scan_street);
			selectedImg.setImageResource(R.drawable.scan_street_hl);
			break;
		case 3:
			normalImg.setImageResource(R.drawable.scan_word);
			selectedImg.setImageResource(R.drawable.scan_word_hl);
			break;
		}
		View normalLayout = tabIndicator.findViewById(R.id.normalLayout);
		normalLayout.setAlpha(1f);// 透明度显示
		View selectedLayout = tabIndicator.findViewById(R.id.selectedLayout);
		selectedLayout.setAlpha(0f);// 透明的隐藏
		Map<String, View> map = new HashMap<String, View>();
		map.put("normal", normalLayout);
		map.put("selected", selectedLayout);
		tabViews.add(map);

		return tabIndicator;
	}

	/**
	 * 设置tab状态选中
	 * 
	 * @param index
	 */
	private void setTabSelectedState(int index, int tabCount) {
		for (int i = 0; i < tabCount; i++) {
			if (i == index) {
				tabViews.get(i).get("normal").setAlpha(0f);
				tabViews.get(i).get("selected").setAlpha(1f);
			} else {
				tabViews.get(i).get("normal").setAlpha(1f);
				tabViews.get(i).get("selected").setAlpha(0f);
			}
		}
		jazzyPager.setCurrentItem(index, false);// false表示 代码切换 pager
												// 的时候不带scroll动画
	}

	private void initJazzyPager(TransitionEffect effect) {
		jazzyPager.setTransitionEffect(effect);
		// jazzyPager.setAdapter(new IssueAdapter());
		jazzyPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		jazzyPager.setPageMargin(0);
		jazzyPager.setFadeEnabled(true);
		jazzyPager.setSlideCallBack(new SlideCallback() {
			@Override
			public void callBack(int position, float positionOffset) {
				Map<String, View> map = tabViews.get(position);
				ViewHelper.setAlpha(map.get("selected"), positionOffset);
				ViewHelper.setAlpha(map.get("normal"), 1 - positionOffset);
			}
		});
		jazzyPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tabHost.setCurrentTab(position);
				currentNav = position;
				// mNavListener = (onChangeNavListener) getCurNavFragment();
				changeListener();
			}

			@Override
			public void onPageScrolled(int paramInt1, float paramFloat,
					int paramInt2) {
			}

			@Override
			public void onPageScrollStateChanged(int paramInt) {
			}
		});
	}

	private void initFragment() {
		for (int i = 0; i < 4; i++) {
			String navName = issueNavNames[i];
			Fragment issueNavFragment = null;
			try {
				Class<?> c = Class.forName(navName);
				issueNavFragment = (Fragment) c.newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			navFragments.add(issueNavFragment);
		}
	}

	private Fragment getCurNavFragment() {
		return navFragments.get(currentNav);
	}

	private void changeListener() {
		mNavListener = (onNavChangedListener) getCurNavFragment();
	}

	final class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int position) {
			return navFragments.get(position);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			jazzyPager.setObjectForPosition(obj, position);
			return obj;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			if (object != null) {
				return ((Fragment) object).getView() == view;
			} else {
				return false;
			}
		}
	}

}
