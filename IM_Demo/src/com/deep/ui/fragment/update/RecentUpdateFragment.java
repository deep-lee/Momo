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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
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
import com.bmob.im.demo.ui.BaseMainActivity;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.phoenix.PullToRefreshView;
import com.deep.ui.update.BaseSlidingFragmentActivity;
import com.deep.ui.update.MainActivity2;
import com.deep.ui.update.SearchActivity;

public class RecentUpdateFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	
	private Context mContext;
	
	private NaviFragment naviFragment;
	
	View headerView;
	
	public static final int REFRESH_DELAY = 2000;

	public PullToRefreshView mPullToRefreshView;
	
	List<BmobRecent> recents;
	
	SwipeMenuListView mListView;
	
	public User user;
	
	MessageRecentAdapter adapter;
	
	LayoutInflater inflater;
	
	ImageView search;
	
	public RecentUpdateFragment() {
		super();
	}
	
	public RecentUpdateFragment(Context mContext, NaviFragment naviFragment) {
		super();
		this.mContext = mContext;
		this.naviFragment = naviFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		return inflater.inflate(R.layout.fragment_recent_update, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		
		headerView = inflater.inflate(R.layout.fragment_search_header, null);
		
		search = (ImageView) headerView.findViewById(R.id.common_fragment_search_iv);
	
		mListView = (SwipeMenuListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);		
		
		mListView.setDividerHeight(1);
		
		mListView.addHeaderView(headerView);
		
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
						openItem.setTitle("标记已读");
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
							// 标记已读

							BmobRecent recent1 = (BmobRecent)adapter.getItem(position);
							//重置未读消息
							BmobDB.create(getActivity()).resetUnread(recent1.getTargetid());
							
							refresh();
							
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
		if (position-- == 0) {
			return true;
		}
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
		if (--position == -1) {
			// 点击了搜索head
			
			float y = 90;
			
			MainActivity2.y = y;
			
			TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -y);
			animation.setDuration(200);
			animation.setFillAfter(true);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					Intent intent = new Intent(getActivity(), SearchActivity.class);
					BaseSlidingFragmentActivity.flag = false;
					getActivity().startActivityForResult(intent, 200);
					getActivity().overridePendingTransition(R.anim.animationb,R.anim.animationa);
				}
			});
			MainActivity2.layout_all.startAnimation(animation);
			
			return;
		}
		BmobRecent recent = (BmobRecent) adapter.getItem(position);
		//重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		naviFragment.setChatTipState(false);
		
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
