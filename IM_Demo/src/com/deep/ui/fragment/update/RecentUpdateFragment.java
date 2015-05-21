package com.deep.ui.fragment.update;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.phoenix.PullToRefreshView;

public class RecentUpdateFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	
	private Context mContext;
	
	public static final int REFRESH_DELAY = 2000;

	public PullToRefreshView mPullToRefreshView;
	
	List<BmobRecent> recents;
	
	SwipeMenuListView mListView;
	
	public User user;
	
	MessageRecentAdapter adapter;
	
	public RecentUpdateFragment() {
		super();
	}
	
	public RecentUpdateFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent_update, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
	
		mListView = (SwipeMenuListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);		
		
		mListView.setDividerHeight(0);
		
		user = userManager.getCurrentUser(User.class);
		
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
		 mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	                mPullToRefreshView.postDelayed(new Runnable() {
	                    @Override
	                    public void run() {
	                    	ShowToast("聊天消息加载完了哦!");
	        				refresh();
	                        mPullToRefreshView.setRefreshing(false);
	                    }
	                }, REFRESH_DELAY);
	            }
	        });
		 
		 recents = BmobDB.create(getActivity()).queryRecents();
		
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, recents);
		mListView.setAdapter(adapter);
		
		// step 1. create a MenuCreator
				SwipeMenuCreator creator = new SwipeMenuCreator() {

					@Override
					public void create(SwipeMenu menu) {
						// create "open" item
						SwipeMenuItem openItem = new SwipeMenuItem(
								getActivity().getApplicationContext());
						// set item background
						openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
								0xCE)));
						// set item width
						openItem.setWidth(dp2px(90));
						// set item title
						openItem.setTitle("Open");
						// set item title fontsize
						openItem.setTitleSize(18);
						// set item title font color
						openItem.setTitleColor(Color.WHITE);
						// add to menu
						menu.addMenuItem(openItem);

						// create "delete" item
						SwipeMenuItem deleteItem = new SwipeMenuItem(
								getActivity().getApplicationContext());
						// set item background
						deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
								0x3F, 0x25)));
						// set item width
						deleteItem.setWidth(dp2px(90));
						// set a icon
						deleteItem.setIcon(R.drawable.ic_delete);
						// add to menu
						menu.addMenuItem(deleteItem);
					}
				};
				// set creator
				mListView.setMenuCreator(creator);

				// step 2. listener item click event
				mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu, int index) {

						switch (index) {
						case 0:
							// open

							BmobRecent recent1 = (BmobRecent)adapter.getItem(position);
							//重置未读消息
							BmobDB.create(getActivity()).resetUnread(recent1.getTargetid());
							//组装聊天对象
							BmobChatUser user = new BmobChatUser();
							user.setAvatar(recent1.getAvatar());
							user.setNick(recent1.getNick());
							user.setUsername(recent1.getUserName());
							user.setObjectId(recent1.getTargetid());
							Intent intent = new Intent(getActivity(), ChatActivity.class);
							intent.putExtra("user", user);
							startAnimActivity(intent);
							break;
						case 1:
							// delete
							BmobRecent recent2 = (BmobRecent)adapter.getItem(position);
							deleteRecent(position,recent2);	
							break;
						}
					}
				});
				
				// set SwipeListener
				mListView.setOnSwipeListener(new OnSwipeListener() {
					
					@Override
					public void onSwipeStart(int position) {
						// swipe start
					}
					
					@Override
					public void onSwipeEnd(int position) {
						// swipe end
					}
				});
		
	}
	
	/** 删除会话
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteRecent(int position, BmobRecent recent){
		adapter.remove(position);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = (BmobRecent) adapter.getItem(position);
		showDeleteDialog(position, recent);
		return true;
	}
	
	public void showDeleteDialog(final int position, final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getNick(),"删除会话", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(position,recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = (BmobRecent) adapter.getItem(position);
		//重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);			
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					
					recents = BmobDB.create(getActivity()).queryRecents();
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, recents);
					mListView.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
