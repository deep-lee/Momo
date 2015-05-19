package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameCommunityAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ImageView.ScaleType;

public class GameCommunityActivity extends BaseMainActivity {
	
	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private ViewPager advPager = null;
	private AtomicInteger what = new AtomicInteger(0);
	private boolean isContinue = true;
	
	private List<ImageView> advPics;
	
	public LayoutInflater mInflater;
	
	public View headerView;
	
	private ListView mListView; 
	
	private GameCommunityAdapter mAdapter;
	
	public static User user;
	
	CustomProgressDialog dialog;
	
	int currentList = 0;
	
	
	CustomProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_game_community);
		
		initView();
	}
	
	public void initView() {
	
		mInflater = LayoutInflater.from(GameCommunityActivity.this);
		user = userManager.getCurrentUser(User.class);
		initListView();
	}
	
	public void initListView() {
		
		mListView = (ListView) findViewById(R.id.game_community_list_view);
		initPager();
		mListView.addHeaderView(headerView);
		
		mAdapter = new GameCommunityAdapter(GameCommunityActivity.this, CustomApplcation.gameList);
		mListView.setAdapter(mAdapter);
	}
	
	public void initPager() {
		
		headerView = mInflater.inflate(R.layout.game_community_list_header, null);
		
		advPager = (ViewPager) headerView.findViewById(R.id.adv_pager);
		ViewGroup group = (ViewGroup) headerView.findViewById(R.id.viewGroup);
		
		advPics = new ArrayList<ImageView>();

		ImageView img1 = new ImageView(this);
		img1.setBackgroundResource(R.drawable.advertising_default);
		advPics.add(img1);

		ImageView img2 = new ImageView(this);
		img2.setBackgroundResource(R.drawable.advertising_default);
		advPics.add(img2);

		ImageView img3 = new ImageView(this);
		img3.setBackgroundResource(R.drawable.advertising_default);
		advPics.add(img3);

		ImageView img4 = new ImageView(this);
		img4.setBackgroundResource(R.drawable.advertising_default);
		advPics.add(img4);
		
		imageViews = new ImageView[advPics.size()];

		for (int i = 0; i < advPics.size(); i++) {
			imageView = new ImageView(this);
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
	
	public void back(View view) {
   	 finish();
   	 overridePendingTransition(0, R.anim.base_slide_right_out);
    }
}
