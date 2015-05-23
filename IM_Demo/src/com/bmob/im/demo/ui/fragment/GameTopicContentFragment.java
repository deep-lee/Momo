package com.bmob.im.demo.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GTContentAdapter;
import com.bmob.im.demo.bean.GameTopic;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.GameTopicDetailsActivity;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;

public class GameTopicContentFragment extends FragmentBase {

	
public static String TAG = "QiangContentFragment";
	
	protected Context mContext;
	
	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private ViewPager advPager = null;
	private AtomicInteger what = new AtomicInteger(0);
	private boolean isContinue = true;
	
	private List<ImageView> advPics;
	
	private View contentView ;
	private int currentIndex ;
	private int pageNum;
	private String lastItemTime; //当前列表结尾的条目的创建时间，
	
	private ArrayList<GameTopic> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private GTContentAdapter mAdapter;
	private ListView actualListView;  // 当前的可见的ListView
	
	Boolean first = true;
	
	public View headerView;
	
	String package_name;
	
	private TextView networkTips;
	private CustomProgressDialog progress;
	private boolean pullFromUser;
	public enum RefreshType{
		REFRESH,LOAD_MORE
	}
	private RefreshType mRefreshType = RefreshType.LOAD_MORE;
	
	
	
	public static Fragment newInstance(int index){
		Fragment fragment = new GameTopicContentFragment();
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
		
		contentView = inflater.inflate(R.layout.fragment_game_topic_content, null);
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
		
		initPager();
		
		actualListView = mPullRefreshListView.getRefreshableView();
		
		actualListView.addHeaderView(headerView);
		
		mListItems = new ArrayList<GameTopic>();
		mAdapter = new GTContentAdapter(mContext, mListItems);
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
				Intent intent = new Intent();
				intent.setClass(getActivity(), GameTopicDetailsActivity.class);
				intent.putExtra("data", mListItems.get(position-2));
				getActivity().startActivity(intent);
				
			}
		});
		return contentView;
	}
	
	
	// 状态获取数据
		public void fetchData(){
			setState(LOADING);
			
			BmobQuery<GameTopic> query = new BmobQuery<GameTopic>();
			// 排序
			query.order("-updatedAt");
			// 设置查询多少个，设置成15个，15也是每Page显示的状态条数
			query.setLimit(Config.NUMBERS_PER_PAGE);
			BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
			
			// 条件
			query.addWhereLessThan("createdAt", date);
			Log.i(TAG,"SIZE:" + Config.NUMBERS_PER_PAGE*pageNum);
			
			// 跳过数据，避免显示重复
			query.setSkip(Config.NUMBERS_PER_PAGE*(pageNum++));
			Log.i(TAG,"SIZE:"+Config.NUMBERS_PER_PAGE*pageNum);
			query.include("author");
			query.findObjects(getActivity(), new FindListener<GameTopic>() {
				
				@Override
				public void onSuccess(List<GameTopic> list) {
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
		
		
		public void initPager() {
			
			headerView = mInflater.inflate(R.layout.game_community_list_header, null);
			
			advPager = (ViewPager) headerView.findViewById(R.id.adv_pager);
			ViewGroup group = (ViewGroup) headerView.findViewById(R.id.viewGroup);
			
			advPics = new ArrayList<ImageView>();

			ImageView img1 = new ImageView(mContext);
			img1.setScaleType(ScaleType.MATRIX);
			img1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			img1.setBackgroundResource(R.drawable.advertising_default);
			advPics.add(img1);

			ImageView img2 = new ImageView(mContext);
			img2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			img2.setBackgroundResource(R.drawable.advertising_default);
			advPics.add(img2);

			ImageView img3 = new ImageView(mContext);
			img3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			img3.setScaleType(ScaleType.MATRIX);
			img3.setBackgroundResource(R.drawable.advertising_default);
			advPics.add(img3);

			ImageView img4 = new ImageView(mContext);
			img4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			img4.setScaleType(ScaleType.MATRIX);
			img4.setBackgroundResource(R.drawable.advertising_default);
			advPics.add(img4);
			
			imageViews = new ImageView[advPics.size()];

			for (int i = 0; i < advPics.size(); i++) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(20, 20));
				imageView.setPadding(5, 5, 5, 5);
				imageViews[i] = imageView;
				if (i == 0) {
					imageViews[i]
							.setBackgroundResource(R.drawable.banner_dian_focus);
				} else {
					imageViews[i]
							.setBackgroundResource(R.drawable.banner_dian_blur);
				}
				group.addView(imageViews[i]);
			}

			advPager.setAdapter(new AdvAdapter(advPics));
			advPager.setOnPageChangeListener(new GuidePageChangeListener());
			advPager.setOnTouchListener(new OnTouchListener() {
				
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						isContinue = false;
						break;
					case MotionEvent.ACTION_UP:
						isContinue = true;
						break;
					default:
						isContinue = true;
						break;
					}
					return false;
				}
			});
			
			
			// 使用ImageLoader来加载网络资源
			refreshImageView();
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						if (isContinue) {
							viewHandler.sendEmptyMessage(what.get());
							whatOption();
						}
					}
				}

			}).start();
			
		}
		
		private void whatOption() {
			what.incrementAndGet();
			if (what.get() > imageViews.length - 1) {
				what.getAndAdd(-4);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				
			}
		}
		
		public void refreshImageView() {
			
			advPics.get(0).setImageResource(R.drawable.icon_community_best_user);
			advPics.get(1).setImageResource(R.drawable.icon_community_game_suggest);
			advPics.get(2).setImageResource(R.drawable.icon_community_feedback);
			advPics.get(3).setImageResource(R.drawable.icon_community_comming_soon);
			
		}
		
		@SuppressLint("HandlerLeak")
		private final Handler viewHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				advPager.setCurrentItem(msg.what);
				super.handleMessage(msg);
			}

		};
		
		private final class GuidePageChangeListener implements OnPageChangeListener {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				what.getAndSet(arg0);
				for (int i = 0; i < imageViews.length; i++) {
					imageViews[arg0]
							.setBackgroundResource(R.drawable.banner_dian_focus);
					if (arg0 != i) {
						imageViews[i]
								.setBackgroundResource(R.drawable.banner_dian_blur);
					}
				}

			}

		}
		
		private final class AdvAdapter extends PagerAdapter {
			private List<ImageView> views = null;

			public AdvAdapter(List<ImageView> views) {
				this.views = views;
			}

			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView(views.get(arg1));
			}

			@Override
			public void finishUpdate(View arg0) {

			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public Object instantiateItem(View arg0, int arg1) {
				
				@SuppressWarnings("deprecation")
				LayoutParams layoutParams = new LayoutParams(
						android.support.v4.view.ViewPager.LayoutParams.FILL_PARENT, 
						android.support.v4.view.ViewPager.LayoutParams.FILL_PARENT);
				views.get(arg1).setLayoutParams(layoutParams);
				views.get(arg1).setScaleType(ScaleType.FIT_XY);
				
				((ViewPager) arg0).addView(views.get(arg1), 0);
				return views.get(arg1);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {

			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void startUpdate(View arg0) {

			}

		}
	
}

