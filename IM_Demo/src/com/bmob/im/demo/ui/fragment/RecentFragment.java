package com.bmob.im.demo.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.bmob.im.demo.ui.AddPopWindow;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.util.CharacterParser;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutAnimator;
import com.deep.phoenix.PullToRefreshView;
import com.nostra13.universalimageloader.core.ImageLoader;



/** 最近会话
  * @ClassName: ConversationFragment
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 下午1:01:37
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	 // ClearEditText mClearEditText;
	
	LinearLayout search_back_layout;
	ImageView iv_back;
	EditText et_search;
	ImageView iv_search, iv_add;
	
	 public static final int REFRESH_DELAY = 2000;

	 public PullToRefreshView mPullToRefreshView;
	 
	 String avatarStr = "";
	 
	 List<BmobRecent> recents;
	
//	ListView listview;
	//XListView在顶上增加了一个heander，所以position位置要-1，不然要报数组溢出异常
	SwipeMenuListView mListView;
	
	public ImageView iv_fragment_avatar;
	
	public User user;
	
	public YoYo.AnimationComposer rotateOutAnimation, rotateInAnimation;
	
	MessageRecentAdapter adapter;
	
	private InputMethodManager inputMethodManager;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
//	/**
//	 * 根据拼音来排列ListView里面的数据类
//	 */
//	private PinyinComparator pinyinComparator;
	
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
		
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		characterParser = CharacterParser.getInstance();
//		pinyinComparator = new PinyinComparator();
		
		rotateOutAnimation = new YoYo.AnimationComposer(new RotateOutAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateInAnimation = new YoYo.AnimationComposer(new RotateInAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
	
		mListView = (SwipeMenuListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);		
		// 首先不允许上拉加载更多
		//mListView.setPullLoadEnable(false);
		// 允许下拉刷新
		//mListView.setPullRefreshEnable(true);
		// 设置监听器，监听下拉事件
		//mListView.setXListViewListener(this);
		mListView.setDividerHeight(0);
		
		iv_fragment_avatar = (ImageView) findViewById(R.id.fragment_find_action_bar_user_avatar);
		
		user = userManager.getCurrentUser(User.class);
		
		if (user != null) {
        	avatarStr = user.getAvatar();
        	refreshAvatar(avatarStr);
		}
		
		iv_fragment_avatar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.showLeft();
			}
		});
		
		search_back_layout = (LinearLayout) findViewById(R.id.search_back_layout);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		et_search = (EditText) findViewById(R.id.fragment_search_et);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		
		iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = new AddPopWindow(getActivity());
                addPopWindow.showPopupWindow(iv_add);
            }

        });
		
		
		
		iv_search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv_search.setVisibility(View.INVISIBLE);
				iv_add.setVisibility(View.INVISIBLE);
				iv_fragment_avatar.setVisibility(View.INVISIBLE);
				search_back_layout.setVisibility(View.VISIBLE);
				et_search.setVisibility(View.VISIBLE);
				
				et_search.requestFocus();
				
				inputMethodManager.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
			}
		});
		
		iv_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv_search.setVisibility(View.VISIBLE);
				iv_add.setVisibility(View.VISIBLE);
				iv_fragment_avatar.setVisibility(View.VISIBLE);
				search_back_layout.setVisibility(View.INVISIBLE);
				et_search.setVisibility(View.INVISIBLE);
				
				et_search.setText("");
				filterData("");
				
				et_search.clearFocus(); 
				
				inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
			}
		});
		
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				// 根据输入框中的值来过滤数据并更新ListView
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		
		
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
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<BmobRecent> filterDateList = new ArrayList<BmobRecent>();
		if (TextUtils.isEmpty(filterStr)) {
			// 当搜索的内容为空时，用所有的好友填充列表
			filterDateList = recents;
		} else {
			filterDateList.clear();
			for (BmobRecent sortModel : recents) {
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
		// 根据a-z进行排序
		// Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

//	/**
//	 * 为ListView填充数据
//	 * @param date
//	 * @return
//	 */
//	private void filledData(List<BmobChatUser> datas) {
//		recents.clear();
//		int total = datas.size();
//		for (int i = 0; i < total; i++) {
//			BmobChatUser user = datas.get(i);
//			BmobRecent sortModel = new User();
//			sortModel.setAvatar(user.getAvatar());
//			sortModel.setNick(user.getNick());
//			sortModel.setUsername(user.getUsername());
//			sortModel.setObjectId(user.getObjectId());
//			sortModel.setContacts(user.getContacts());
//			// 汉字转换成拼音
//			String nick = sortModel.getNick();
//			// 若没有username
//			if (nick != null) {
//				String pinyin = characterParser.getSelling(sortModel.getNick());
//				String sortString = pinyin.substring(0, 1).toUpperCase();
//				// 正则表达式，判断首字母是否是英文字母
//				if (sortString.matches("[A-Z]")) {
//					sortModel.setSortLetters(sortString.toUpperCase());
//				} else {
//					sortModel.setSortLetters("#");
//				}
//			} else {
//				sortModel.setSortLetters("#");
//			}
//			recents.add(sortModel);
//		}
//		// 根据a-z进行排序
//		// Collections.sort(friends, pinyinComparator);
//	}
	
	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_fragment_avatar,
					ImageLoadOptions.getOptions());
		} else {
			// 否则显示默认的头像
			iv_fragment_avatar.setImageResource(R.drawable.default_head);
		}
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
	
	public void startAvatarOut() {
		rotateOutAnimation.playOn(iv_fragment_avatar);
	}
	
	public void startAvatarIn() {
		rotateInAnimation.playOn(iv_fragment_avatar);
	}
	
}
