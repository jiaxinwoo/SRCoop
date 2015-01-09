package com.srcoop.android.activity.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.srcoop.android.activity.R;

public class SimpleListDialog extends BaseDialog implements OnItemClickListener {
	private ListView mLvDisplay;
	private BaseAdapter mAdapter;
	private onSimpleListItemClickListener mOnSimpleListItemClickListener;

	public SimpleListDialog(Context context) {
		super(context);
		setDialogContentView(R.layout.include_dialog_simplelist);
		mLvDisplay = (ListView) findViewById(R.id.dialog_simplelist_list);
		mLvDisplay.setOnItemClickListener(this);
	}

	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		if (mAdapter != null) {
			mLvDisplay.setAdapter(mAdapter);
		}
	}

	public void notifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public void setButton(CharSequence text1,
			DialogInterface.OnClickListener listener1, CharSequence text2,
			DialogInterface.OnClickListener listener2) {
		super.setButton1(text1, listener1);
		super.setButton2(text2, listener2);
	}

	public void setOnSimpleListItemClickListener(
			onSimpleListItemClickListener listener) {
		mOnSimpleListItemClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mOnSimpleListItemClickListener != null) {
			mOnSimpleListItemClickListener.onItemClick(position ,v);
			// dismiss();
		}
	}

	public interface onSimpleListItemClickListener {
		public void onItemClick(int position, View v);
	}
}
