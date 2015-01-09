package com.srcoop.android.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SDFileExplorerActivity extends Activity{

	ListView listView;
	TextView textView;
	//记录当前的父文件夹
	File currentParent;
	//记录当前路径下的所有文件的文件数组
	File[] currentFiles;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = this.getIntent();       //用于回传值
		
		setContentView(R.layout.activity_achievement_file_upload);
		//获取列出全部文件的ListView
		listView=(ListView)findViewById(R.id.list);
		textView=(TextView)findViewById(R.id.path);
		//获取系统的SD卡的目录
		File root =new File("/mnt/sdcard");
		//如果SD卡存在
		if(root.exists())
		{
			currentParent=root;
			currentFiles=root.listFiles();
			//使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}
		
		
		//为ListView的列表项的单击事件绑定监听器
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) 
			{
				// 用户单击了文件，直接返回，不做任何处理
				if(currentFiles[position].isFile())
				{
					Bundle bundle =new Bundle();
					try {
						bundle.putString("path",currentParent.getCanonicalPath()+"/"+currentFiles[position].getName());
						Toast.makeText(SDFileExplorerActivity.this,currentParent.getCanonicalPath()+"/"+currentFiles[position].getName(), Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.putExtras(bundle);
					
					setResult(RESULT_OK, intent);
					finish();
					
					return;
				}			
				//获取用户点击的文件夹下的所有文件
				File[] tmp=currentFiles[position].listFiles();
				if(tmp==null||tmp.length==0)
				{
					Toast.makeText(SDFileExplorerActivity.this, "当前路径不可访问或该路径下没有文件", Toast.LENGTH_SHORT).show();
				}
				else
				{
					//获取用户单击的列表项对应的文件夹，设为当前的父文件夹
					currentParent=currentFiles[position];
					//保存当前的父文件夹内的全部文件和文件夹
					currentFiles=tmp;
					//再次更新ListView
					inflateListView(currentFiles);
				}
			}
			
		});
		
		
		//获取上一级目录的按钮
		Button parent =(Button)findViewById(R.id.file_back);
		parent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View source) {
				try
				{
					if(!currentParent.getCanonicalPath().equals("/mnt/sdcard"))
					{
						//获取上一级目录
						currentParent = currentParent.getParentFile();
						//列出当前目录下所有文件
						currentFiles = currentParent.listFiles();
						//再次更新ListView
						inflateListView(currentFiles);
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		
	}
	
	
	private void inflateListView(File [] files)
	{
		//创建一个List集合，List集合的元素是Map
		List<Map<String,Object>>listItems=new ArrayList<Map<String,Object>>();
		for(int i=0;i<files.length;i++)
		{
			Map<String,Object>listItem=new HashMap<String,Object>();
			//如果当前File是文件夹，使用folder图标；否则使用file图标
			if(files[i].isDirectory())
			{
				listItem.put("icon", R.drawable.scan_street);
			}
			else
			{
				listItem.put("icon", R.drawable.icon_file);
			}
			listItem.put("fileName", files[i].getName());
			//添加List项
			listItems.add(listItem);
		}
		//创建一个SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this
				, listItems, R.layout.activity_achievement_file_show_style
				, new String[]{"icon","fileName"}
		        , new int []{R.id.file_icon,R.id.file_name}
		);
		//为ListView设置Adapter
		listView.setAdapter(simpleAdapter);
		try
		{
			textView.setText("当前路径为："+currentParent.getCanonicalPath());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
