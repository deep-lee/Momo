package com.bmob.im.demo.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.id;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.LeftFragment;
import com.bmob.im.demo.ui.fragment.NearByFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutRightAnimator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.nineoldandroids.animation.Animator;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

/**
 * 登陆
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 下午2:45:35
 */
public class MainActivity extends ActivityBase implements EventListener, OnClickListener, OnMenuItemClickListener, OnMenuItemLongClickListener{

	private ImageButton[] mTabs;
	public ContactFragment contactFragment;
	public RecentFragment recentFragment;
	public NearByFragment nearByFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	
	ImageView iv_recent_tips,iv_contact_tips;//消息提示
	ImageView slideAvator;
	
	public static SlidingMenu menu;
	
	
	FragmentTransaction trx;
	
	private LayoutParams []params;
	
	Boolean menuFlag;
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	int nearsSex;
	
	RelativeLayout main_bottom_layout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		//如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(30);
		//开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		
		initView();
		initTab();
		
		sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		nearsSex = sharedPreferences.getInt("nearsSex", 2);
		
	}

	private void initView(){
		mTabs = new ImageButton[3];
		mTabs[0] = (ImageButton) findViewById(R.id.btn_message);
		mTabs[1] = (ImageButton) findViewById(R.id.btn_contract);
		mTabs[2] = (ImageButton) findViewById(R.id.btn_nears);
		iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);
		
		main_bottom_layout = (RelativeLayout) findViewById(R.id.main_bottom_layout);
		
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		
		mTabs[0].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_recents_p));
		
		initLeftView();
		
		// 启动获取照片墙的线程
		initWallPhoto();
		
		menuFlag = false;
		
		// 更新用户地理位置信息
        updateUserLocation();
        
        User user = userManager.getCurrentUser(User.class);
        CustomApplcation.sex = user.getSex();
        
        if (!CustomApplcation.sex) {
			main_bottom_layout.setBackgroundResource(R.drawable.common_main_bottom_bg_female);
		}
	}
	
	private void initWallPhoto() {
		
		Thread newThread; // 声明一个子线程
		newThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	// 这里写入子线程需要做的工作
		    	// 当前的user
		    	User u = (User) userManager.getCurrentUser(User.class);
		    	String allPhoto = u.getPhotoWallFile();
		    	if ((allPhoto != null) && (!allPhoto.equals(""))) {
		    		String myPhotoOrigin[] = allPhoto.split(";");
		    		
		    		CustomApplcation.numOfPhoto = myPhotoOrigin.length;
		    		
		    		for (int i = 0; i < myPhotoOrigin.length; i++) {
						CustomApplcation.myWallPhoto.add(myPhotoOrigin[i]);
						// ShowToast(myPhotoOrigin[i]);
					}
		    		
				}
		    }
		});
		newThread.start(); //启动线程
		    
	}
		
		
	private void initLeftView() {
		      
		// configure the SlidingMenu  
        menu = new SlidingMenu(this);  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset); 
        menu.setFadeEnabled(false);  
        menu.setBehindScrollScale(0.25f);  
        menu.setFadeDegree(0.25f);  
        menu.setMode(SlidingMenu.LEFT);  
           
        // 设置触摸屏幕的模式  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
//        menu.setShadowWidthRes(R.dimen.shadow_width);  
//        menu.setShadowDrawable(R.drawable.shadow);  
//  
//        // 设置滑动菜单视图的宽度  
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
//        // 设置渐入渐出效果的值  
//        menu.setFadeDegree(0.35f);  
        
        // 配置背景图片  
        menu.setBackgroundImage(R.drawable.icon_bg1);  
        // 设置专场动画效果  
        menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {  
            @Override  
            public void transformCanvas(Canvas canvas, float percentOpen) {  
                float scale = (float) (percentOpen * 0.25 + 0.75);  
                canvas.scale(scale, scale, -canvas.getWidth() / 2,  
                        canvas.getHeight() / 2);  
            }  
        });  
          
        menu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {  
            @Override  
            public void transformCanvas(Canvas canvas, float percentOpen) {  
                float scale = (float) (1 - percentOpen * 0.25);  
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);  
            }  
        });  
        
        /** 
         * SLIDING_WINDOW will include the Title/ActionBar in the content 
         * section of the SlidingMenu, while SLIDING_CONTENT does not. 
         */  
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
        
        //为侧滑菜单设置布局  
        menu.setMenu(R.layout.slide_fragment); 
        
      //侧滑打开时调用
        menu.setOnOpenedListener(new OnOpenedListener() {
			
			@Override
			public void onOpened() {
				// TODO Auto-generated method stub
				if(currentTabIndex == 2)
				{
					menuFlag = true;
					//trx = getSupportFragmentManager().beginTransaction();
					//trx.hide(nearByFragment).commit();
					nearByFragment.closeShakeListeber();
					// ShowToast("关闭附近的人监听器");
					
				}
				
			}
		});
        
        menu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub
				if(currentTabIndex == 2)
				{
					menuFlag = false;
					//trx = getSupportFragmentManager().beginTransaction();
					//trx.show(nearByFragment).commit();
					
					nearByFragment.openShakeListener();
					// ShowToast("打开附近的人监听器");
				}
			}
		});
           
        menu.setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				// ShowToast("" + menu.mViewAbove.getDestScrollX(1));
			}
		});
        
        menu.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				
			}
		});
        
//      //缩放动画效果，超级棒的，大神的参数我没看懂
//        CanvasTransformer mCanvasTransformer =  new CanvasTransformer(){  
//            @Override  
//            public void transformCanvas(Canvas canvas, float percentOpen) {  
//                float scale = (float) (percentOpen*0.25 + 0.75);  
//                canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);                
//            }  
//              
//        };  
//        menu.setBehindCanvasTransformer(mCanvasTransformer);
        
        
        android.app.Fragment leftMenuFragment = new LeftFragment(MainActivity.this);  
       // setBehindContentView(R.layout.left_menu_frame);  
        getFragmentManager().beginTransaction()  
                .replace(R.id.left_fragment, leftMenuFragment).commit();   
        
        
        
	}
	
	
	/*
	 * 动态显示左侧菜单
	 */
	public static void showLeft() {
		menu.showMenu();
	}

	
	private void initTab(){
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		nearByFragment = new NearByFragment(MainActivity.this);
		
		fragments = new Fragment[] {recentFragment, contactFragment, nearByFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment).
			add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment).commit();
	}
	
	
	
	/**
	 * button点击事件
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			break;
		case R.id.btn_contract:
			index = 1;
			break;
		case R.id.btn_nears:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
			
			
			
			switch (index) {
			case 0:
				if (currentTabIndex == 1) {
					mTabs[1].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_contacts_n));
				}
				else if (currentTabIndex == 2) {
					mTabs[2].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_nears_n));
				}
				mTabs[index].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_recents_p));
				break;
				
			case 1:
				if (currentTabIndex == 0) {
					mTabs[0].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_recents_n));
				}
				else if (currentTabIndex == 2) {
					mTabs[2].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_nears_n));
				}
				mTabs[index].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_contacts_p));
				break;
				
			case 2:
				if (currentTabIndex == 1) {
					mTabs[1].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_contacts_n));
				}
				else if (currentTabIndex == 0) {
					mTabs[0].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_recents_n));
				}
				mTabs[index].setImageDrawable(getResources().getDrawable(R.drawable.comon_main_bottom_nears_p));
				break;

			default:
				break;
			}		
		}
		mTabs[currentTabIndex].setSelected(false);
		//把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//小圆点提示
		// 如果有未读的消息
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
		
		// 如果有新的好友邀请
		if(BmobDB.create(this).hasNewInvite()){
			iv_contact_tips.setVisibility(View.VISIBLE);
		}else{
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		//清空
		MyMessageReceiver.mNewNum=0;
		
		if (currentTabIndex == 2 && nearByFragment != null && nearByFragment.mShakeListener != null) {
			
			if (!nearByFragment.getFlag() && menuFlag == false) {
				nearByFragment.openShakeListener();
			}	
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
		
		if (nearByFragment != null && nearByFragment.mShakeListener != null) {
			nearByFragment.mShakeListener.stop();
		}
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}
	
	
	/** 刷新界面
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		//也要存储起来
		if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}
		if(currentTabIndex==0){
			//当前页面如果为会话页面，刷新此页面
			if(recentFragment != null){
				// 刷新会话页面
				recentFragment.refresh();
			}
		}
	}
	
	NewBroadcastReceiver  newReceiver;
	
	private void initNewMessageBroadCast(){
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}
	
	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	TagBroadcastReceiver  userReceiver;
	
	private void initTagMessageBroadCast(){
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		//优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}
	
	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if(isNetConnected){
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}
	
	
	/** 刷新好友请求
	  * @Title: notifyAddUser
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshInvite(BmobInvitation message){
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if(currentTabIndex==1){
			if(contactFragment != null){
				contactFragment.refresh();
			}
		}else{
			//同时提醒通知
			String tickerText = message.getFromname()+"请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}
	
	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}
	
	
	private static long firstTime;
	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		//取消定时检测服务
		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		
		position--;
		// ShowToast("" + position);
		// TODO Auto-generated method stub
		if (position >= 0 && position <= 2) {
			if (nearsSex == position) {
				
			}else {
				editor.putInt("nearsSex", position);
				nearsSex = position;
				editor.commit();
				
				nearByFragment.nearBySexChanged(position);

			}
		}
		
		// 清除地理位置信息
		if (position == 3) {
			
		}
	}
}
