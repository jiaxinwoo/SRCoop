package com.srcoop.android.activity;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ReferencesList extends Activity implements
		ListView.OnItemClickListener, ListView.OnLongClickListener {

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}

	@Override
	public boolean onLongClick(View view) {
		return false;
	}

	@SuppressWarnings("unused")
	private ListView referenceslist_listview_references;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_references_list);

		referenceslist_listview_references = (ListView) findViewById(R.id.referenceslist_listview);

	}

}
