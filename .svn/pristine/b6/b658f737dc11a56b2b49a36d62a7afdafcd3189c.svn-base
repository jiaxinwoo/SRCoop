package com.srcoop.android.activity.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobDate;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.srcoop.android.activity.R;
import com.srcoop.android.activity.bean.EventNotice;
import com.srcoop.android.activity.dialog.BaseDialog;

public class NoticeFragment extends MyFragment implements OnClickListener{

	private ArrayList<EventNotice> mNoticeList;
	
	private SwipeMenuListView mListView;
	private NoticeAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNoticeList = getNoticeData();

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_menuitem_notice, container,
				false);

		mListView = (SwipeMenuListView) v.findViewById(R.id.list);
		mAdapter = new NoticeAdapter(mNoticeList);
		mListView.setAdapter(mAdapter);
		
		//添加侧滑删除动作
		//setListSwipe();

		((TextView) getActivity().findViewById(R.id.activity_title))
				.setText("通知");
		Button button = ((Button) getActivity().findViewById(
				R.id.title_bar_right_menu));
		button.setBackgroundResource(R.drawable.menu_right_ok);
		button.setClickable(true);
		button.setOnClickListener(this);
		return v;
	}

	private void setListSwipe() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(100));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					mNoticeList.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				}
				return false;
			}
		});

		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
	}

	private class NoticeAdapter extends ArrayAdapter<EventNotice> {
		private ArrayList<EventNotice> list;

		public NoticeAdapter(List<EventNotice> objects) {
			super(getActivity(), 0, objects);
			list = (ArrayList<EventNotice>) objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.notice_item, null);
			}
			//设置通知标题，内容，发布者，发布时间
			TextView tv_notice_title = (TextView) convertView
					.findViewById(R.id.tv_notice_title);
			TextView tv_notice_content = (TextView) convertView
					.findViewById(R.id.tv_notice_shortmsg);
			TextView tv_publisher = (TextView)convertView.findViewById(R.id.tv_publisher);
			TextView tv_publish_time = (TextView)convertView.findViewById(R.id.tv_publish_time);
			
			tv_notice_title.setText(list.get(position).getTitle());
			tv_notice_content.setText(list.get(position).getContent());
			tv_publisher.setText("");//need to feed
			tv_publish_time.setText(list.get(position).getPostTime().getDate());
		
			if (list.get(position).getIsRead()) {
				convertView.findViewById(R.id.tv_notice_unread).setVisibility(
						View.INVISIBLE);
			}
			if (position == list.size() - 1) {
				convertView.findViewById(R.id.notice_divider).setVisibility(
						View.INVISIBLE);
			}
			return convertView;
		}
	}

	private ArrayList<EventNotice> getNoticeData() {
		ArrayList<EventNotice> noticeList = new ArrayList<EventNotice>();
		for(int i=0;i<5;i++){
			EventNotice eventNotice = new EventNotice();
			eventNotice.setTile("任务分配");
			Date date = new Date();
			date.setMinutes(i);
			BmobDate pdate = new BmobDate(date);
			eventNotice.setContent("任务分配"+i);
			eventNotice.setPostTime(pdate);
			if(i%2==0){
				eventNotice.setIsRead(true);
			}else{
				eventNotice.setIsRead(false);
			}
			noticeList.add(eventNotice);
		}
		return noticeList;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	public void onClick(View v) {
		
		final BaseDialog baseDialog = new BaseDialog(getActivity());
		baseDialog.setTitle("提示");
		baseDialog.setMessage("确认将所有信息标记为已读？");
		baseDialog.setButton1("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//设置所有为已读
				baseDialog.dismiss();
				for(int j=0;j<mNoticeList.size();j++){
					mNoticeList.get(j).setIsRead(true);
				}
				mAdapter.notifyDataSetChanged();		
				showCustomToast("已经将所有通知标记为已读", getActivity());
			}
		});
		baseDialog.setButton2("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				baseDialog.cancel();
			}
		});
		baseDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		baseDialog.show();
	}
}
