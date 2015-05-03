package com.bmob.im.demo.ui.fragment;

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
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.phoenix.PullToRefreshView;



/** ����Ự
  * @ClassName: ConversationFragment
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 ����1:01:37
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	 // ClearEditText mClearEditText;
	
	 public static final int REFRESH_DELAY = 2000;

	 public PullToRefreshView mPullToRefreshView;
	
//	ListView listview;
	//XListView�ڶ���������һ��heander������positionλ��Ҫ-1����ȻҪ����������쳣
	SwipeMenuListView mListView;
	
	MessageRecentAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		initTopBarForLeft("�Ự", R.drawable.base_common_bar_recent_more_selector, new onLeftImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				MainActivity.showLeft();
			}
		});
		
		
		// Ů������
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
		}
		
		mListView = (SwipeMenuListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);		
		// ���Ȳ������������ظ���
		//mListView.setPullLoadEnable(false);
		// ��������ˢ��
		//mListView.setPullRefreshEnable(true);
		// ���ü����������������¼�
		//mListView.setXListViewListener(this);
		mListView.setDividerHeight(0);
		
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
		 mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	                mPullToRefreshView.postDelayed(new Runnable() {
	                    @Override
	                    public void run() {
	                    	ShowToast("������Ϣ��������Ŷ!");
	        				refresh();
	                        mPullToRefreshView.setRefreshing(false);
	                    }
	                }, REFRESH_DELAY);
	            }
	        });
		
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
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

							BmobRecent recent1 = adapter.getItem(position);
							//����δ����Ϣ
							BmobDB.create(getActivity()).resetUnread(recent1.getTargetid());
							//��װ�������
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
							BmobRecent recent2 = adapter.getItem(position);
							deleteRecent(recent2);	
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

				// other setting
//				listView.setCloseInterpolator(new BounceInterpolator());
				
//				// test item long click
//				mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//					@Override
//					public boolean onItemLongClick(AdapterView<?> parent, View view,
//							int position, long id) {
//						Toast.makeText(getActivity().getApplicationContext(), position + " long click", 0).show();
//						return false;
//					}
//				});
		
//		mClearEditText = (ClearEditText)findViewById(R.id.et_recent_msg_search);
//		mClearEditText.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				adapter.getFilter().filter(s);
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//		});
		
	}
	
	/** ɾ���Ự
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getNick(),"ɾ���Ự", "ȷ��",true,true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		//����δ����Ϣ
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//��װ�������
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
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
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
	
//	@Override
//	public void onRefresh() {
//		// TODO Auto-generated method stub
//		Handler handler = new Handler();
//		 handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				ShowToast("������Ϣ��������Ŷ!");
//				refresh();
//				// mListView.stopRefresh();								
//			}
//		}, 1000);
//	}
//	@Override
//	public void onLoadMore() {
//		// TODO Auto-generated method stub
//		
//	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
}
