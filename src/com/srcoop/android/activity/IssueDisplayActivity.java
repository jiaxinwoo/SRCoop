package com.srcoop.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class IssueDisplayActivity extends Activity {

	private Button IssueEditBtn; // 编辑按钮
	private EditText IssueEditTitle;
	private EditText IssueEditDepict;
	private ImageView IssueEditTitleImage;
	private ImageView IssueEditDepictImage;
	private ListView memListView; // 成员信息
	// false表示当前状态是编辑状态左上角图标此时应为issue_edit_button_confirm.png
	// true表示当前状态为默认状态即不可编辑，左上角图标应此时为issue_display_button_edit
	private boolean iEditOrSave = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_issue_display);

		//
		String user[] = { "name", "num" };
		int kongjian[] = { R.id.student_name, R.id.student_num };

		memListView = (ListView) findViewById(R.id.memberListView);
		memListView.setAdapter(new MyAdapter(this, getData(),
				R.layout.activity_issue_member_style, user, kongjian));
//		modifyH(memListView);

		// 初始化
		IssueEditBtn = (Button) findViewById(R.id.issue_display_edit_w);
		IssueEditTitle = (EditText) findViewById(R.id.issue_title);
		IssueEditDepict = (EditText) findViewById(R.id.issue_depict);
		IssueEditTitleImage = (ImageView) findViewById(R.id.issue_title_image);
		IssueEditDepictImage = (ImageView) findViewById(R.id.issue_depict_image);

		IssueEditBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!iEditOrSave) {
					IssueEditBtn
							.setBackgroundResource(R.drawable.issue_edit_button_confirm);
					IssueEditTitle.setEnabled(true);
					IssueEditTitle.setTextColor(Color.rgb(42, 148, 197));
					IssueEditDepict.setEnabled(true);
					IssueEditDepict.setTextColor(Color.rgb(42, 148, 197));
					IssueEditTitleImage
							.setImageResource(R.drawable.issue_edit_input_name);
					IssueEditDepictImage
							.setImageResource(R.drawable.issue_edit_input_discription);

				} else {
					IssueEditBtn
							.setBackgroundResource(R.drawable.issue_display_button_edit);
					IssueEditTitle.setEnabled(false);
					IssueEditTitle.setTextColor(Color.rgb(255, 255, 255));
					IssueEditDepict.setEnabled(false);
					IssueEditDepict.setTextColor(Color.rgb(255, 255, 255));
					IssueEditTitleImage
							.setImageResource(R.drawable.issue_display_input_name);
					IssueEditDepictImage
							.setImageResource(R.drawable.issue_display_input_discription);
				}
				iEditOrSave = !iEditOrSave;
			}
		});
	}

	// 1.写数据<---成员信息(姓名+电话)
	public List<Map<String, Object>> getData() {
		List<Map<String, Object>> lt = new ArrayList<Map<String, Object>>();

		Map<String, Object> wujunsheng = new HashMap<String, Object>();
		wujunsheng.put("name", "吴俊生");
		wujunsheng.put("num", "13715776661");

		Map<String, Object> shesong = new HashMap<String, Object>();
		shesong.put("name", "佘松");
		shesong.put("num", "13715278881");

		Map<String, Object> wujiaxin = new HashMap<String, Object>();
		wujiaxin.put("name", "吴嘉欣");
		wujiaxin.put("num", "13715775551");

		Map<String, Object> jiahuiming = new HashMap<String, Object>();
		jiahuiming.put("name", "贾慧明");
		jiahuiming.put("num", "13715775555");

		Map<String, Object> linkanghua = new HashMap<String, Object>();
		linkanghua.put("name", "林康华");
		linkanghua.put("num", "13715276666");

		lt.add(wujunsheng);
		lt.add(shesong);
		lt.add(wujiaxin);
		lt.add(jiahuiming);
		lt.add(linkanghua);
		return lt;
	}

	// 2.写MyAdapter继承SimpleAdapter
	class MyAdapter extends SimpleAdapter {

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
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

}
