package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.ChatBgGroupDetailsAdapter;
import com.bmob.im.demo.bean.ChatBg;
import com.bmob.im.demo.bean.ChatBgGroup;

import B.in;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ChatBgGroupDetailsActivity extends BaseActivity {

	TextView tv_title;
	GridView gv_goup_details;
	
	private ChatBgGroup chatBgGroup;
	
	private ChatBgGroupDetailsAdapter mAdapter;
	
	private List<ChatBg> mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_bg_group_details);
		
		initView();
	}
	
	public void initView() {
		mList = new ArrayList<ChatBg>();
		chatBgGroup = (ChatBgGroup) getIntent().getSerializableExtra("chatBgGroup");
		tv_title = (TextView) findViewById(R.id.tv_title);
		gv_goup_details = (GridView) findViewById(R.id.chat_bg_group_details_gridview);
		
		tv_title.setText(chatBgGroup.getGroupName());
		
		mAdapter = new ChatBgGroupDetailsAdapter(mList, ChatBgGroupDetailsActivity.this);
		
		gv_goup_details.setAdapter(mAdapter);
		
		gv_goup_details.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ChatBgGroupDetailsActivity.this, ShowChatBgActivity.class);
				intent.putExtra("chatBg", (ChatBg)mAdapter.getItem(position));
				startActivity(intent);
			}
		});
		
		// 获取数据库背景图片信息
		initGridView();
	}
	
	private void initGridView() {
		BmobQuery<ChatBg> query = new BmobQuery<ChatBg>();
		query.addWhereEqualTo("belongTo", chatBgGroup.getGroupNo());
		query.findObjects(ChatBgGroupDetailsActivity.this, new FindListener<ChatBg>() {
			
			@Override
			public void onSuccess(List<ChatBg> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() > 0) {
					Log.i("FFFFFFFFFFFFFFFF", "图片数量：" + arg0.size());
					mList.addAll(arg0);
					mAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast(R.string.network_tips);
			}
		});
	}
}
