package com.bmob.im.demo.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.StateLineAdapter;
import com.bmob.im.demo.bean.QiangYu;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.deep.db.DatabaseUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StateTimeLineActivity extends BaseActivity {
	
	private ArrayList<QiangYu> mListItems;
	
	private PullToRefreshListView mPullRefreshListView;
	
	private ListView actualListView;
	
	Boolean flag = true;
	
	private User user;
	
	private int pageNum;
	
	private String lastItemTime;//当前列表结尾的条目的创建时间，
	
	private StateLineAdapter mAdapter;
	
	LinearLayout headerView;
	
	TextView tv_header_name, tv_header_personalized_signature;
	ImageView iv_header_avatar;
	
	private RefreshType mRefreshType = RefreshType.LOAD_MORE;
	
	private CustomProgressDialog progress;
	
	private TextView networkTips;
	
	public enum RefreshType{
		REFRESH,LOAD_MORE
	}
	
	public LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_state_time_line);
		
		pageNum = 0;
		
		lastItemTime = getCurrentTime();
		
		flag = getIntent().getBooleanExtra("flag", true);
				
		// 初始化user
		if (flag) {
			user = CustomApplcation.getInstance().getCurrentUser();
			initView();
			
		}else {
			String username = getIntent().getStringExtra("username");
			
			if (username == null) {
				ShowToast(R.string.network_tips);
				username = CustomApplcation.getInstance().getCurrentUser().getUsername();
			}
			
			BmobQuery<User> query = new BmobQuery<User>();
			query.addWhereEqualTo("username", username);
			query.findObjects(StateTimeLineActivity.this, new FindListener<User>() {

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					//ShowToast("找不到");
				}
				@Override
				public void onSuccess(List<User> arg0) {
					// TODO Auto-generated method stub
					if (arg0.size() > 0) {
						user = arg0.get(0);
						//ShowToast("初始化View");
						initView();
					}
				}
			});
		}
		
	}
	
	public void initView() {
		// true代表是自己，false代表是别人
		
		mInflater = LayoutInflater.from(StateTimeLineActivity.this);
				
		headerView = (LinearLayout) mInflater.inflate(R.layout.state_time_line_header, null);
				
		tv_header_name = (TextView) headerView.findViewById(R.id.state_time_line_nick);
		tv_header_personalized_signature = (TextView) headerView.findViewById(R.id.state_time_line_personalized_signature);
		iv_header_avatar = (ImageView) headerView.findViewById(R.id.state_time_line_avartar);
				
		tv_header_name.setText(user.getNick());
		tv_header_personalized_signature.setText(user.getPersonalizedSignature());
		ImageLoader.getInstance().displayImage(user.getAvatar(), iv_header_avatar);
				
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_states_line_list_view);
		
		networkTips = (TextView)findViewById(R.id.networkTips);
				
		progress = new CustomProgressDialog(StateTimeLineActivity.this, "正在加载...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(StateTimeLineActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				mPullRefreshListView.setMode(Mode.BOTH);
				mRefreshType = RefreshType.REFRESH;
				pageNum = 0;
				lastItemTime = getCurrentTime();
				fetchData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mRefreshType = RefreshType.LOAD_MORE;
				fetchData();
			}
		});
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
						
			}
		});
				
		actualListView = mPullRefreshListView.getRefreshableView();
		mListItems = new ArrayList<QiangYu>();
		actualListView.addHeaderView(headerView);
		
		mAdapter = new StateLineAdapter(StateTimeLineActivity.this, mListItems);
				
		actualListView.setAdapter(mAdapter);
				
		if(mListItems.size() == 0){
					
			Log.i("TTTTTTTTTTT", "fetch data");
			// 获取状态数据
			fetchData();
		}
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				MyApplication.getInstance().setCurrentQiangYu(mListItems.get(position-1));
				Intent intent = new Intent();
				intent.setClass(StateTimeLineActivity.this, CommentActivity.class);
				intent.putExtra("data", mListItems.get(position-2));
				startActivity(intent);
			}
		});
	}
	
	// 状态获取数据
	public void fetchData(){
		setState(LOADING);
		BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
		
		query.addWhereEqualTo("author", user);
		
		// 排序
		query.order("-createdAt");
//		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			
		// 设置查询多少个，设置成15个，15也是每Page显示的状态条数
		query.setLimit(Config.NUMBERS_PER_PAGE);
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
			
		// 条件
		query.addWhereLessThan("createdAt", date);
			
		// 跳过数据，避免显示重复
		query.setSkip(Config.NUMBERS_PER_PAGE*(pageNum++));
		
		query.include("author");
		query.findObjects(StateTimeLineActivity.this, new FindListener<QiangYu>() {
				
			@Override
			public void onSuccess(List<QiangYu> list) {
				// TODO Auto-generated method stub

				if(list.size() != 0 && list.get(list.size() - 1) != null){
					if(mRefreshType==RefreshType.REFRESH){
						// 如果是刷新动作的话，就先清空当前的mListItems，后面再加入
						mListItems.clear();
					}
					if(list.size() < Config.NUMBERS_PER_PAGE){
					}
					if(CustomApplcation.getInstance().getCurrentUser() != null){
							
						// 设置当前用户的内容收藏状态，也就是循环看当前用户有没有收藏这条状态，然后改变收藏的图标
						list = DatabaseUtil.getInstance(StateTimeLineActivity.this).setFav(list);
					}
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();
						
					setState(LOADING_COMPLETED);
					
					//ShowToast("加载完成");
					mPullRefreshListView.onRefreshComplete();
				}else{
					ActivityUtil.show(StateTimeLineActivity.this, "暂无更多数据~");
					pageNum--;
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				pageNum--;
				setState(LOADING_FAILED);
				mPullRefreshListView.onRefreshComplete();
			}
		});
	}
		
	private static final int LOADING = 1;
	private static final int LOADING_COMPLETED = 2;
	private static final int LOADING_FAILED =3;
	private static final int NORMAL = 4;
	public void setState(int state){
		switch (state) {
		case LOADING:
			if(mListItems.size() == 0){
				mPullRefreshListView.setVisibility(View.GONE);
				progress.show();
			}
			networkTips.setVisibility(View.GONE);
				
			break;
		case LOADING_COMPLETED:
			networkTips.setVisibility(View.GONE);
			progress.dismiss();
				
			mPullRefreshListView.setVisibility(View.VISIBLE);
			mPullRefreshListView.setMode(Mode.BOTH);

				
			break;
		case LOADING_FAILED:
			if(mListItems.size()==0){
				mPullRefreshListView.setVisibility(View.VISIBLE);
				mPullRefreshListView.setMode(Mode.PULL_FROM_START);
				networkTips.setVisibility(View.VISIBLE);
			}
			progress.dismiss();
			break;
		case NORMAL:
				
			break;
		default:
			break;
		}
	}
		
	private String getCurrentTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String times = formatter.format(new Date(System.currentTimeMillis()));
		 return times;
	}
	
}
