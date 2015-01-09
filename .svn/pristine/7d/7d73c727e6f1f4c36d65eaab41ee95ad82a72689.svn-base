package com.srcoop.android.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.srcoop.android.activity.R;
import com.srcoop.android.activity.view.HandyTextView;

public class SimpleListDialogAdapter extends BaseArrayListAdapter {

	public SimpleListDialogAdapter(Context context, List<String> datas) {
		super(context, datas);
	}

	public SimpleListDialogAdapter(Context context, String... datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_dialog, null);
		}
		((HandyTextView) convertView.findViewById(R.id.listitem_dialog_text))
				.setText((CharSequence) getItem(position));
		return convertView;
	}
}
