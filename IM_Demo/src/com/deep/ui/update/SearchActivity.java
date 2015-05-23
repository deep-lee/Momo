package com.deep.ui.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.SearchListAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.BaseActivity;
import com.bmob.im.demo.util.CharacterParser;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.PinyinComparator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends BaseActivity {
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	
	private InputMethodManager inputMethodManager;
	
	private EditText et_search;
	private TextView tv_cancle;
	private ListView mListView;
	private SearchListAdapter mAdapter;
	
	private List<BmobRecent> recents;
	
	private List<User> friends = new ArrayList<User>();
	
	private List<BmobChatUser> chatUsers = new ArrayList<BmobChatUser>();
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 0:
            		
            		initListView();
            		break;
            		
            	case 1:
            		mListView.setVisibility(View.INVISIBLE);
            		break;
            
            }   
            super.handleMessage(msg);  
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Transparent);    
		//设置窗体背景模糊
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.activity_search);
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initView();
	}
	
	public void initView() {
		
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		et_search = (EditText) findViewById(R.id.header_search_et);
		tv_cancle = (TextView) findViewById(R.id.header_search_cancle_btn);
		mListView = (ListView) findViewById(R.id.fragment_search_list);
		tv_cancle.setClickable(true);
		
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				// 根据输入框中的值来过滤数据并更新ListView
				mListView.setVisibility(View.VISIBLE);
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
				BaseSlidingFragmentActivity.flag = true;
			}
		});
		
		mAdapter = new SearchListAdapter(SearchActivity.this, chatUsers);
		mListView.setAdapter(mAdapter);
		
		et_search.requestFocus();
		inputMethodManager.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
		
		initBmobChatUserList();
	}
	
	public void initBmobChatUserList() {
		new Thread(){
			@Override
			public void run(){
				
				recents = BmobDB.create(SearchActivity.this).queryRecents();
				
				queryMyfriends();
				
				for (Iterator<BmobRecent> iterator = recents.iterator(); iterator
						.hasNext();) {
					BmobRecent recent = (BmobRecent) iterator.next();
					
					BmobChatUser user = new BmobChatUser();
					user.setAvatar(recent.getAvatar());
					user.setNick(recent.getNick());
					user.setUsername(recent.getUserName());
					user.setObjectId(recent.getTargetid());
					
					chatUsers.add(user);
				}
				
				for (Iterator<User> iterator = friends.iterator(); iterator
						.hasNext();) {
					User user = (User) iterator.next();
					Boolean flag = true;
					for (Iterator<BmobChatUser> iterator2 = chatUsers.iterator(); iterator2
							.hasNext();) {
						BmobChatUser bmobChatUser = (BmobChatUser) iterator2
								.next();
						
						// 相同的好友
						if (user.getObjectId().equals(bmobChatUser.getObjectId())) {
							flag = false;
							break;
						}
						
					}
					
					if (flag) {
						chatUsers.add(user);
					}
					
				}
				
				Message message = new Message();
				message.what = 0;
				handler.sendMessage(message);
				
			}
		}.start();
	}
	
	public void initListView() {
		mAdapter.updateListView(chatUsers);
		// Toast.makeText(SearchActivity.this, "" + chatUsers.size(), Toast.LENGTH_SHORT).show();
	}
	
	/** 获取好友列表
	  * queryMyfriends
	  * @return void
	  * @throws
	  */
	private void queryMyfriends() {
		//在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SearchActivity.this).getContactList()));
	
		Map<String,BmobChatUser> users = CustomApplcation.getInstance().getContactList();
		//组装新的User
		filledData(CollectionUtils.map2list(users));

	}
	
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String nick = sortModel.getNick();
			// 若没有username
			if (nick != null) {
				String pinyin = characterParser.getSelling(sortModel.getNick());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<BmobChatUser> filterDateList = new ArrayList<BmobChatUser>();
		if (TextUtils.isEmpty(filterStr)) {
			// 当搜索的内容为空时，用所有的好友填充列表
			filterDateList = chatUsers;
			mListView.setVisibility(View.INVISIBLE);
		} else {
			filterDateList.clear();
			for (BmobChatUser sortModel : chatUsers) {
				String name = sortModel.getNick();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		mAdapter.updateListView(filterDateList);
		
		Log.i("TTTTTTTTTTTTTTTTTTT", "" + filterDateList.size());
	}
}
