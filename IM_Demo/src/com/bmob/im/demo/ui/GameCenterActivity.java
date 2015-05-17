package com.bmob.im.demo.ui;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameCenterHasInstalledAdapter;
import com.bmob.im.demo.adapter.GameCenterUninstalledAdapter;
import com.bmob.im.demo.bean.SuggestedGame;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CharacterParser;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class GameCenterActivity extends BaseMainActivity {
	
	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private ViewPager advPager = null;
	private AtomicInteger what = new AtomicInteger(0);
	private boolean isContinue = true;
	private List<ImageView> advPics;
	
	public LayoutInflater mInflater;
	
	public View headerView;
	public View footerView;
	public TextView header_tv;
	public TextView footer_tv;
	public ListView uninstalled_list_view;
	
	public View line_2;
	
	public List<GameInfo> hasInstalledGameData;
	public List<GameInfo> unInstalledGameData;
	
	private List<String> imageURL;
	
	private ListView mListView;  
	
//	private ClearEditText mClearEditText;
	
	public static User user;
	
	int currentList = 0;
	
	GameCenterHasInstalledAdapter mAdapter;
	GameCenterUninstalledAdapter uninstalledAdapter;
	CustomProgressDialog dialog;
	
	public ImageView iv_search;
	
	Boolean flag = false;
	
	Boolean first_launch = true;
	
	ImageView iv_back;
	
	public EditText et_search;
	public TextView title;
	
	private InputMethodManager inputMethodManager;
	
	CustomProgressDialog progress;
	
	@SuppressLint("HandlerLeak")
	Handler loadeHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				
				progress.dismiss();
				progress = null;
				
				initListView();
        		
				break;
				
			case 1:
				
				ShowToast("NOTIFY");
				mAdapter.notifyDataSetChanged();
				uninstalledAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
		
	};
	
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		setContentView(R.layout.activity_game_center);
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		

		
		mInflater = LayoutInflater.from(GameCenterActivity.this);
		
		
		characterParser = CharacterParser.getInstance();
		
		
		user = userManager.getCurrentUser(User.class);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		et_search = (EditText) findViewById(R.id.game_center_search_et);
		title = (TextView) findViewById(R.id.tv_title);
		
		line_2 = findViewById(R.id.view_temp2);
		
		iv_search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv_search.setVisibility(View.INVISIBLE);
				title.setVisibility(View.INVISIBLE);
				et_search.setVisibility(View.VISIBLE);
				line_2.setVisibility(View.INVISIBLE);
				et_search.requestFocus();
				
				inputMethodManager.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
				
				flag = true;
			}
		});
		
		iv_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag) {
					iv_search.setVisibility(View.VISIBLE);
					title.setVisibility(View.VISIBLE);
					line_2.setVisibility(View.VISIBLE);
					et_search.setVisibility(View.INVISIBLE);
					et_search.setText("");
					filterData("");
					
					et_search.clearFocus(); 
					
					inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
					
					flag = false;
				}else {
					finish();
				}
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
		
		 
	     
		progress = new CustomProgressDialog(GameCenterActivity.this, "正在加载...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		initGameData(false);
	     
	}
	
	public void initGameData(final Boolean update) {
		
		new Thread(){
			
			@Override
			public void run(){
				
				hasInstalledGameData = new ArrayList<GameInfo>();
				unInstalledGameData = new ArrayList<GameInfo>();
				
				for (Iterator<GameInfo> iterator = CustomApplcation.gameList.iterator(); iterator.hasNext();) {
					GameInfo gameInfo = (GameInfo) iterator.next();
					
					int gameStatus = gameInfo.getGame_status();
					// 未下载
					if (gameStatus == 0 || gameStatus == 1) {
						unInstalledGameData.add(gameInfo);
					}else if (gameStatus == 2) {
						hasInstalledGameData.add(gameInfo);
					}
					
				}
				
				Message message = new Message();
				message.what = update? 1 : 0;
				loadeHandler.sendMessage(message);
				
			}
			
		}.start();
		
		
	}
	
	
	
	public void initListView() {
		
		 mListView=(ListView) findViewById(R.id.game_list_view);  
		 headerView = mInflater.inflate(R.layout.game_center_list_header, null);
		 initPage();
		 footerView = mInflater.inflate(R.layout.game_center_uninstalled_list_item, null);
		 uninstalled_list_view = (ListView) footerView.findViewById(R.id.game_uninstalled_list_view);
		 footer_tv = (TextView) footerView.findViewById(R.id.game_center_footer_tv);
		 
		 footer_tv.setText("未下载的游戏 (" + unInstalledGameData.size() + ")");
		 header_tv.setText("已下载的游戏 (" + hasInstalledGameData.size() + ")");
		 
		 uninstalledAdapter = new GameCenterUninstalledAdapter(GameCenterActivity.this, unInstalledGameData);
		 uninstalled_list_view.setAdapter(uninstalledAdapter);
		 
		 mListView.addHeaderView(headerView);
		 mListView.addFooterView(footerView);
		 
	     mAdapter = new GameCenterHasInstalledAdapter(this, hasInstalledGameData);  
	     mListView.setAdapter(mAdapter); 
	}
	
	public void initPage() {
		imageURL = new ArrayList<String>();
		
		advPager = (ViewPager) headerView.findViewById(R.id.adv_pager);
		ViewGroup group = (ViewGroup) headerView.findViewById(R.id.viewGroup);
		header_tv = (TextView) headerView.findViewById(R.id.game_center_header_tv);
		
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
	
	public void refreshImageView() {
		
		BmobQuery<SuggestedGame> query = new BmobQuery<SuggestedGame>();
		query.findObjects(GameCenterActivity.this, new FindListener<SuggestedGame>() {
			
			@Override
			public void onSuccess(List<SuggestedGame> arg0) {
				// TODO Auto-generated method stub
				
				for (Iterator<SuggestedGame> iterator = arg0.iterator(); iterator.hasNext();) {
					SuggestedGame suggestedGame = (SuggestedGame) iterator.next();
					imageURL.add(suggestedGame.getImageShowFile().getFileUrl(GameCenterActivity.this));
				}
				
				// 显示图片
				for (int i = 0; i < imageURL.size(); i++) {
					 
					String url = imageURL.get(i);
					
					ImageLoader.getInstance().displayImage(url, advPics.get(i));
					
				}
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast(R.string.network_tips);
			}
		});
	}
	
	/**
	 * 动态设置listview的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if(adapter != null) {
			int totalHeight = 0;
			for(int i=0; i<adapter.getCount(); i++) {
				View listItem = adapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
			((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
			listView.setLayoutParams(params);
			System.out.println(params.height + "===" + adapter.getCount());
		}
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
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GameInfo> filterHasInstalledDateList = new ArrayList<GameInfo>();
		List<GameInfo> filterUninstalledDateList = new ArrayList<GameInfo>();
		if (TextUtils.isEmpty(filterStr)) {
			// 当搜索的内容为空时，用所有的游戏填充列表
			filterHasInstalledDateList = hasInstalledGameData;
			filterUninstalledDateList = unInstalledGameData;
		} else {
			filterHasInstalledDateList.clear();
			for (GameInfo sortModel : hasInstalledGameData) {
				String name = sortModel.getGame_name();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterHasInstalledDateList.add(sortModel);
					}
				}
			}
			
			filterUninstalledDateList.clear();
			for (GameInfo sortModel : unInstalledGameData) {
				String name = sortModel.getGame_name();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterUninstalledDateList.add(sortModel);
					}
				}
			}
		}
		// 根据a-z进行排序
		// Collections.sort(filterDateList, pinyinComparator);
		
		
		uninstalledAdapter.updateListView(filterUninstalledDateList);
		mAdapter.updateListView(filterHasInstalledDateList);
	}
	
	public void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (!first_launch) {
			updateGamesInfo();
			mAdapter.notifyDataSetChanged();
			uninstalledAdapter.notifyDataSetChanged();
		}
		
		
		
	}
	
	// 判断是否有数据更新
	public void updateGamesInfo() {
		
		for (int i = 0; i < CustomApplcation.gameList.size(); i++) {
			GameInfo gameInfo = CustomApplcation.gameList.get(i);
			
			int game_status = CustomApplcation.getInstance().getGameStatus(gameInfo.package_name, gameInfo.game_name, gameInfo.getNotificationId());
			
			// 如果游戏状态发生改变，就更新gameList和本地数据库
			if (game_status != gameInfo.game_status) {
				gameInfo.setGame_status(game_status);
			}
		}
		
		hasInstalledGameData.clear();
		unInstalledGameData.clear();
		
		for (Iterator<GameInfo> iterator = CustomApplcation.gameList.iterator(); iterator.hasNext();) {
			GameInfo gameInfo = (GameInfo) iterator.next();
			
			int gameStatus = gameInfo.getGame_status();
			// 未下载
			if (gameStatus == 0 || gameStatus == 1) {
				unInstalledGameData.add(gameInfo);
			}else if (gameStatus == 2) {
				hasInstalledGameData.add(gameInfo);
			}
			
		}
		
		header_tv.setText("已下载的游戏 (" + hasInstalledGameData.size() + ")");
		footer_tv.setText("未下载的游戏 (" + unInstalledGameData.size() + ")");
		
	}
	
	
	// 判断应用是否安装
		public boolean isAppInstalled(Context context,String packagename)
	    {
	    	PackageInfo packageInfo; 
	    	
	    	try {
	                
	    		packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
	             
	    	}catch (NameNotFoundException e) {
	    		
	    		packageInfo = null;
	    		
	    		e.printStackTrace();
	    		
	    	}
	             
	    	if(packageInfo ==null){
	    		
	    		return false;
	    		
	    	}else{
	    		
	    		return true;
	    		
	    	}
	    }
	    
		public Boolean isApkDownloaded(String fileName) {
			
			String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
			File rootFile = new File(apkDir);
			
			if (!rootFile.exists()) {
				return false;
			}
			
			File tempFile = new File(apkDir + fileName + ".apk");
			if (tempFile.exists()){
				return true;
			}else {
				return false;
			}
			
		}

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			
			first_launch = false;
			
		}
	
	
}
