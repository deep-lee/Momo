package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.ContactsInfo;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.ContactFriendAdapter;
import com.bmob.im.demo.adapter.InviteFriendAtapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.GetContactsInfo;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AddFriendsFromAddressBookActivity extends BaseActivity implements OnItemClickListener{
	
	ListView lv_syatem_contact;
	ListView lv_invite_contact;
	
	LinearLayout invite_layout;
	
	GetContactsInfo get_contact_info;
	
	
	List<ContactsInfo> system_contacts;
	List<ContactsInfo> add_able_contacts;
	List<ContactsInfo> has_registed_contacts;
	List<ContactsInfo> not_registed_contacts;
	List<ContactsInfo> invite_able_contacts;
	
	List<BmobChatUser> friends = new ArrayList<BmobChatUser>();
	
	TextView tv_num1, tv_num2;
	LinearLayout headView1, headView2;
	
	CustomProgressDialog progress;
	public LayoutInflater mInflater;
	
	@SuppressLint("HandlerLeak")
	Handler loadHandler = new Handler(){
		
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
            		
            		initListView();
            		
    				progress.dismiss();
    				progress = null;
            		break;
            
            }   
            super.handleMessage(msg);  
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends_from_address_book);
		
		initView();
	}
	
	public void initView() {
		
		lv_syatem_contact = (ListView) findViewById(R.id.add_friends_from_contact_listview);
		
		get_contact_info = new GetContactsInfo(AddFriendsFromAddressBookActivity.this);
		
		progress = new CustomProgressDialog(AddFriendsFromAddressBookActivity.this, "正在加载...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		// ProgressDialog展示
		getAddAbleContacts();
	}
	
	@SuppressLint("InflateParams")
	public void initListView() {
		
		ContactFriendAdapter adapter1 = new ContactFriendAdapter(AddFriendsFromAddressBookActivity.this, add_able_contacts);
		InviteFriendAtapter adapter2 = new InviteFriendAtapter(AddFriendsFromAddressBookActivity.this, not_registed_contacts);
		
		mInflater = LayoutInflater.from(AddFriendsFromAddressBookActivity.this);
		
		headView1 = (LinearLayout) mInflater.inflate(R.layout.add_friends_from_system_contact_head_view, null);
		headView2 = (LinearLayout) mInflater.inflate(R.layout.add_friends_from_system_contact_head_view, null);
		
		tv_num1 = (TextView) headView1.findViewById(R.id.add_friends_from_system_head_view_tv);
		tv_num2 = (TextView) headView2.findViewById(R.id.add_friends_from_system_head_view_tv);
		
		tv_num1.setText(add_able_contacts.size() + " 位好友可添加");
		tv_num2.setText(not_registed_contacts.size() + "位好友可邀请注册");
		
		invite_layout = (LinearLayout) mInflater.inflate(R.layout.invite_friends_list_layout, null);
		
		lv_invite_contact = (ListView) invite_layout.findViewById(R.id.invite_friends_from_contact_listview);
		
		lv_invite_contact.addHeaderView(headView2);
		
		lv_invite_contact.setAdapter(adapter2);
		
		// 如果不动态设置作为Footer的ListView的高度的话，ListView只会显示一项
		setListViewHeightBasedOnChildren(lv_invite_contact);

		// ShowToast("NUM:" + not_registed_contacts.size());
		
		lv_syatem_contact.addHeaderView(headView1);
		
		lv_syatem_contact.addFooterView(invite_layout);
		
		lv_syatem_contact.setAdapter(adapter1);
		
	}
	
	/**
	 * 动态设置listview的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if(adapter != null) {
			int totalHeight = 0;
			for(int i=0; i<adapter.getCount(); i++) {
				View listItem = adapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
			((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
			listView.setLayoutParams(params);
			System.out.println(params.height + "===" + adapter.getCount());
		}
	}
	
	// 判断是否是好友
	public void getAddAbleContacts() {
		
		// 开启线程
		new Thread()
		{
			@Override
			public void run() {
				
				system_contacts = get_contact_info.getLocalContactsInfos();
				
				// ShowToast("SYSTEM NUM:" + system_contacts.size());
				
				add_able_contacts = new ArrayList<ContactsInfo>();
				has_registed_contacts = new ArrayList<ContactsInfo>();
				not_registed_contacts = new ArrayList<ContactsInfo>();
				invite_able_contacts = new ArrayList<ContactsInfo>();
				
				queryMyfriends();
				
				// 先看数据库里有没有这些人
				for (int i = 0; i < system_contacts.size(); i++) {
					
					final ContactsInfo contact = system_contacts.get(i);
					BmobQuery<User> query = new BmobQuery<User>();
					query.addWhereEqualTo("username", contact.getContactsPhone());
					
					query.findObjects(AddFriendsFromAddressBookActivity.this, new FindListener<User>() {

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast("查询出错！");
						}

						@Override
						public void onSuccess(List<User> arg0) {
							// TODO Auto-generated method stub
							// 数据库里没有这个人
							if (arg0.size() == 0) {
								not_registed_contacts.add(contact);
							}
							// 数据库里有这个人
							else {
								contact.setContactNick(arg0.get(0).getNick());
								
								String avatar_url = arg0.get(0).getAvatar();
								contact.setAvatar_url(avatar_url);
								
								has_registed_contacts.add(contact);
							}
						}
					});
				}
				
				
				// 根据数据库里有的人判断是否已经是好友关系
				for (int i = 0; i < has_registed_contacts.size(); i++) {
					for (int j = 0; j < friends.size(); j++) {
						if (has_registed_contacts.get(i).getContactsPhone().equals(friends.get(j).getUsername())) {
							break;
						}
						
						if (j == friends.size() - 1) {
							add_able_contacts.add(has_registed_contacts.get(i));
						}
					}
				}
				
				Message message = new Message();
				message.what = 0;
				loadHandler.sendMessage(message);
			}
			
		}.start();
		
		
	}
	
	/** 获取好友列表
	  * queryMyfriends
	  * @return void
	  * @throws
	  */
	private void queryMyfriends() {

		//在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(AddFriendsFromAddressBookActivity.this).getContactList()));
	
		Map<String,BmobChatUser> users = CustomApplcation.getInstance().getContactList();
		//组装新的User
		friends = CollectionUtils.map2list(users);
		
		// ShowToast("TEST:" + friends.size());

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

}
