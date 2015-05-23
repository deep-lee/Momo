package com.bmob.im.demo.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.ChatBgListAdapter;
import com.bmob.im.demo.bean.ChatBg;
import com.bmob.im.demo.bean.ChatBgGroup;
import com.bmob.im.demo.bean.User;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ChatBgSelectActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
	
	private static int SELECT_PICTURE = 100;
	
	private ListView chat_bg_list;
	private View headerView;
	private RelativeLayout rl_select_from_system_photo, rl_set_default_bg;
	private LayoutInflater mInflater;
	private ChatBgListAdapter mAdapter;
	
	private List<ChatBgGroup> mList;
	
	SharedPreferences mySharedPreferences;
	//实例化SharedPreferences.Editor对象（第二步） 
	SharedPreferences.Editor editor; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_bg_select);
		
		initView();
	}
	
	private void initView() {
		
		
		mySharedPreferences = getSharedPreferences("test", 
				Activity.MODE_PRIVATE); 
		editor = mySharedPreferences.edit(); 
		
		mList = new ArrayList<ChatBgGroup>();
		mInflater = LayoutInflater.from(ChatBgSelectActivity.this);
		chat_bg_list = (ListView) findViewById(R.id.chat_bg_list);
		headerView = mInflater.inflate(R.layout.chat_bg_list_header, null);
		rl_select_from_system_photo = (RelativeLayout) headerView.findViewById(R.id.chat_bg_select_from_system_photo);
		rl_set_default_bg = (RelativeLayout) headerView.findViewById(R.id.chat_bg_set_default_bg);
		rl_select_from_system_photo.setOnClickListener(this);
		rl_set_default_bg.setOnClickListener(this);
		mAdapter = new ChatBgListAdapter(mList, ChatBgSelectActivity.this);
		chat_bg_list.setOnItemClickListener(this);
		chat_bg_list.addHeaderView(headerView);
		chat_bg_list.setAdapter(mAdapter);
		
		// 获取服务器聊天背景数据
		initChatBgData();
		
	}
	
	public void initChatBgData() {
		BmobQuery<ChatBgGroup> query = new BmobQuery<ChatBgGroup>();
		query.findObjects(ChatBgSelectActivity.this, new FindListener<ChatBgGroup>() {
			
			@Override
			public void onSuccess(List<ChatBgGroup> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() > 0) {
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 从相册中选择背景
		case R.id.chat_bg_select_from_system_photo:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
            intent.addCategory(Intent.CATEGORY_OPENABLE);  
            intent.setType("image/*");  
            startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);   
			break;
		// 设置默认背景
		case R.id.chat_bg_set_default_bg:
			CustomApplcation.chatBgAddress = "default";
			ShowToast("设置默认聊天背景成功");
			
			editor.putString("chat_bg_address", "default");
			editor.commit();
			
			User user = new User();
			ChatBg chatBg = new ChatBg();
            chatBg.setBelongTo(0);
            chatBg.setObjectId("5vEm777e");
            chatBg.setPhotoName("默认背景");
			user.setChatBg(chatBg);
			updateUserData(user, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// ShowToast("更新成功！");
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					// ShowToast("更新失败！");
				}
			});
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

	    if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量

	        Log.e("MainActivity2","ActivityResult resultCode error");

	        return;

	    }
	    Bitmap bm = null;
	    //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口

	    ContentResolver resolver = getContentResolver();

	    //此处的用于判断接收的Activity是不是你想要的那个

	    if (requestCode == SELECT_PICTURE) {

	        try {

	            Uri originalUri = data.getData();        //获得图片的uri 

	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片

	            // 这里开始的第二部分，获取图片的路径：

	            String[] proj = {MediaStore.Images.Media.DATA};

	            //好像是android多媒体数据库的封装接口，具体的看Android文档

	            @SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(originalUri, proj, null, null, null); 

	            //按我个人理解 这个是获得用户选择的图片的索引值

	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

	            //将光标移至开头 ，这个很重要，不小心很容易引起越界

	            cursor.moveToFirst();

	            //最后根据索引值获取图片路径

	            String path = cursor.getString(column_index);
	            CustomApplcation.chatBgAddress = path;
	            
	            ShowToast("选择聊天背景成功");
	            
	            editor.putString("chat_bg_address", path);
				editor.commit();
	            
	            User user = new User();
	            ChatBg chatBg = new ChatBg();
	            chatBg.setBelongTo(0);
	            chatBg.setObjectId("5vEm777e");
	            chatBg.setPhotoName("默认背景");
				user.setChatBg(chatBg);
				updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						// ShowToast("更新成功！");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						// ShowToast("更新失败！");
					}
				});

	        }catch (IOException e) {

	            Log.e("TTTTTTTTTTTTT",e.toString()); 

	        }

	    }

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		if (position == 0) {
			// 从相册选择图片作为聊天背景
		}else {
			position--;
			ChatBgGroup chatBgGroup = (ChatBgGroup) mAdapter.getItem(position);
			Intent intent = new Intent();
			intent.setClass(ChatBgSelectActivity.this, ChatBgGroupDetailsActivity.class);
			intent.putExtra("chatBgGroup", chatBgGroup);
			startActivity(intent);
		}
		
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
