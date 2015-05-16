package com.bmob.im.demo.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AIContentAdapter;
import com.bmob.im.demo.bean.QiangYu;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.ui.CommentActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.deep.db.DatabaseUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * @author kingofglory
 *         email: kingofglory@yeah.net
 *         blog:  http:www.google.com
 * @date 2014-2-23s
 * TODO
 */

public class QiangContentFragment extends FragmentBase{
	
	public static String TAG = "QiangContentFragment";
	
	protected Context mContext;
	
	private View contentView ;
	private int currentIndex ;
	private int pageNum;
	private String lastItemTime; //当前列表结尾的条目的创建时间，
	
	private ArrayList<QiangYu> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private AIContentAdapter mAdapter;
	private ListView actualListView;  // 当前的可见的ListView
	
	private TextView networkTips;
	private CustomProgressDialog progress;
	private boolean pullFromUser;
	public enum RefreshType{
		REFRESH,LOAD_MORE
	}
	private RefreshType mRefreshType = RefreshType.LOAD_MORE;
	
	public static Fragment newInstance(int index){
		Fragment fragment = new QiangContentFragment();
		Bundle args = new Bundle();
		args.putInt("page",index);
		fragment.setArguments(args);
		return fragment;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime(){
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String times = formatter.format(new Date(System.currentTimeMillis()));
	     return times;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		// 传来的当前的位置
		currentIndex = getArguments().getInt("page");
		pageNum = 0;
		lastItemTime = getCurrentTime();
		Log.i(TAG,"curent time:"+lastItemTime);
		
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		contentView = inflater.inflate(R.layout.fragment_qiangcontent, null);
		mPullRefreshListView = (PullToRefreshListView)contentView
				.findViewById(R.id.pull_refresh_list);
		networkTips = (TextView)contentView.findViewById(R.id.networkTips);
		progress = new CustomProgressDialog(mContext, "正在加载...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				mPullRefreshListView.setMode(Mode.BOTH);
				pullFromUser = true;
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
		mAdapter = new AIContentAdapter(mContext, mListItems);
		actualListView.setAdapter(mAdapter);
		if(mListItems.size() == 0){
			
			// 获取状态数据
			fetchData();
		}
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				// 进入评论页面
				CustomApplcation.getInstance().setCurrentQiangYu(mListItems.get(position-1));
				Intent intent = new Intent();
				intent.setClass(getActivity(), CommentActivity.class);
				intent.putExtra("data", mListItems.get(position-1));
				getActivity().startActivity(intent);
			}
		});
		return contentView;
	}
	
	
	// 状态获取数据
	public void fetchData(){
		
		setState(LOADING);
		BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
		
		// 排序
		query.order("-createdAt");
//		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		
		// 设置查询多少个，设置成15个，15也是每Page显示的状态条数
		query.setLimit(Config.NUMBERS_PER_PAGE);
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		
		// 条件
		query.addWhereLessThan("createdAt", date);
		Log.i(TAG,"SIZE:" + Config.NUMBERS_PER_PAGE*pageNum);
		
		// 跳过数据，避免显示重复
		query.setSkip(Config.NUMBERS_PER_PAGE*(pageNum++));
		//LogUtils.i(TAG,"SIZE:"+Constant.NUMBERS_PER_PAGE*pageNum);
		query.include("author");
		query.findObjects(getActivity(), new FindListener<QiangYu>() {
			
			@Override
			public void onSuccess(List<QiangYu> list) {
				// TODO Auto-generated method stub
				//LogUtils.i(TAG,"find success."+list.size());
				if(list.size() != 0 && list.get(list.size() - 1) != null){
					
					if(mRefreshType==RefreshType.REFRESH){
						// 如果是刷新动作的话，就先清空当前的mListItems，后面再加入
						mListItems.clear();
					}
					if(list.size()<Config.NUMBERS_PER_PAGE){
						Log.i(TAG,"已加载完所有数据~");
					}
					if(userManager.getCurrentUser() != null){
						
						// 设置当前用户的内容收藏状态，也就是循环看当前用户有没有收藏这条状态，然后改变收藏的图标
						list = DatabaseUtil.getInstance(mContext).setFav(list);
					}
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();
					
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				}else{
					
					ShowToast("暂无更多数据～");
					pageNum--;
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				//LogUtils.i(TAG,"find failed."+arg1);
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
}
