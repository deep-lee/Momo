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
	//ʵ����SharedPreferences.Editor���󣨵ڶ����� 
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
		
		// ��ȡ���������챳������
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
		// �������ѡ�񱳾�
		case R.id.chat_bg_select_from_system_photo:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
            intent.addCategory(Intent.CATEGORY_OPENABLE);  
            intent.setType("image/*");  
            startActivityForResult(Intent.createChooser(intent, "ѡ��ͼƬ"), SELECT_PICTURE);   
			break;
		// ����Ĭ�ϱ���
		case R.id.chat_bg_set_default_bg:
			CustomApplcation.chatBgAddress = "default";
			ShowToast("����Ĭ�����챳���ɹ�");
			
			editor.putString("chat_bg_address", "default");
			editor.commit();
			
			User user = new User();
			ChatBg chatBg = new ChatBg();
            chatBg.setBelongTo(0);
            chatBg.setObjectId("5vEm777e");
            chatBg.setPhotoName("Ĭ�ϱ���");
			user.setChatBg(chatBg);
			updateUserData(user, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// ShowToast("���³ɹ���");
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					// ShowToast("����ʧ�ܣ�");
				}
			});
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

	    if (resultCode != RESULT_OK) {        //�˴��� RESULT_OK ��ϵͳ�Զ����һ������

	        Log.e("MainActivity2","ActivityResult resultCode error");

	        return;

	    }
	    Bitmap bm = null;
	    //���ĳ������ContentProvider���ṩ���� ����ͨ��ContentResolver�ӿ�

	    ContentResolver resolver = getContentResolver();

	    //�˴��������жϽ��յ�Activity�ǲ�������Ҫ���Ǹ�

	    if (requestCode == SELECT_PICTURE) {

	        try {

	            Uri originalUri = data.getData();        //���ͼƬ��uri 

	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //�Եõ�bitmapͼƬ

	            // ���￪ʼ�ĵڶ����֣���ȡͼƬ��·����

	            String[] proj = {MediaStore.Images.Media.DATA};

	            //������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�

	            @SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(originalUri, proj, null, null, null); 

	            //���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ

	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

	            //�����������ͷ ���������Ҫ����С�ĺ���������Խ��

	            cursor.moveToFirst();

	            //����������ֵ��ȡͼƬ·��

	            String path = cursor.getString(column_index);
	            CustomApplcation.chatBgAddress = path;
	            
	            ShowToast("ѡ�����챳���ɹ�");
	            
	            editor.putString("chat_bg_address", path);
				editor.commit();
	            
	            User user = new User();
	            ChatBg chatBg = new ChatBg();
	            chatBg.setBelongTo(0);
	            chatBg.setObjectId("5vEm777e");
	            chatBg.setPhotoName("Ĭ�ϱ���");
				user.setChatBg(chatBg);
				updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						// ShowToast("���³ɹ���");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						// ShowToast("����ʧ�ܣ�");
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
			// �����ѡ��ͼƬ��Ϊ���챳��
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
