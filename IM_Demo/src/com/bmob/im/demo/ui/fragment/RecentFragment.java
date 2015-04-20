package com.bmob.im.demo.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.view.ClearEditText;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;

/** 最近会话
  * @ClassName: ConversationFragment
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 下午1:01:37
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener,IXListViewListener{

	ClearEditText mClearEditText;
	
//	ListView listview;
	//XListView在顶上增加了一个heander，所以position位置要-1，不然要报数组溢出异常
	XListView mListView;
	
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
		initTopBarForLeft("会话", R.drawable.base_common_bar_recent_more_selector, new onLeftImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				MainActivity.showLeft();
			}
		});
		mListView = (XListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);		
		// 首先不允许上拉加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉刷新
		mListView.setPullRefreshEnable(true);
		// 设置监听器，监听下拉事件
		mListView.setXListViewListener(this);
		mListView.setDividerHeight(0);
		
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
		mListView.setAdapter(adapter);
		
		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	
	/** 删除会话
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
		BmobRecent recent = adapter.getItem(position-1);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"删除会话", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
				if(position == 0)
				{
					//刷新界面点击无事件响应
				}
				else {
					BmobRecent recent = adapter.getItem(position-1);
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
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Handler handler = new Handler();
		 handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				ShowToast("聊天消息加载完了哦!");
				refresh();
				mListView.stopRefresh();								
			}
		}, 1000);
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
}
