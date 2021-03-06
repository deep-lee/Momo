package com.bmob.im.demo.ui;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.FindFragment;
import com.bmob.im.demo.ui.fragment.LeftFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.util.CollectionUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

/**
 * 登陆
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 下午2:45:35
 */
public class MainActivity extends BaseMainActivity implements EventListener, OnClickListener{

	private Button[] mTabs;
	public ContactFragment contactFragment;
	public RecentFragment recentFragment;
//	public NearByFragment nearByFragment;
	public FindFragment findFragemnt;
	
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
	
//	RelativeLayout main_bottom_layout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		//如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(30);
		
		// 使用推送服务时的初始化操作
	    BmobInstallation.getCurrentInstallation(this).save();
	    // 启动推送服务
//	    BmobPush.startWork(this, com.bmob.im.demo.config.Config.applicationId);
		
		//开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
//		initAutomaticAddToContactMessageBroadCast();
		
		initView();
		initTab();
		
		sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		nearsSex = sharedPreferences.getInt("nearsSex", 2);
		
		// ShowToast(CustomApplcation.gameList.size());
		
	}

	private void initView(){
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_contract);
		mTabs[2] = (Button) findViewById(R.id.btn_find);
		
//		for (int i = 0; i < 3; i++) {
//			mTabs[i].setScaleX((float) 0.8);
//			mTabs[i].setScaleY((float) 0.8);
//		}
		
		iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);
		
//		main_bottom_layout = (RelativeLayout) findViewById(R.id.main_bottom_layout);
		
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		
		initLeftView();
		
		// 启动获取照片墙的线程
		initWallPhoto();
		
		menuFlag = false;
		
		// 更新用户地理位置信息
        updateUserLocation();
        
        User user = userManager.getCurrentUser(User.class);
        CustomApplcation.sex = user.getSex();
        
        // ShowToast("" + CustomApplcation.gameList.size());

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
        
//        // 配置背景图片  
//        menu.setBackgroundImage(R.drawable.icon_bg1);  
//        // 设置专场动画效果  
//        menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {  
//            @Override  
//            public void transformCanvas(Canvas canvas, float percentOpen) {  
//                float scale = (float) (percentOpen * 0.25 + 0.75);  
//                canvas.scale(scale, scale, -canvas.getWidth() / 2,  
//                        canvas.getHeight() / 2);  
//            }  
//        });  
//          
//        menu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {  
//            @Override  
//            public void transformCanvas(Canvas canvas, float percentOpen) {  
//                float scale = (float) (1 - percentOpen * 0.25);  
//                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);  
//            }  
//        });  
        
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
				
			}
		});
        
        menu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub

				recentFragment.startAvatarIn();
			}
		});
           
        menu.setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				// ShowToast("" + menu.mViewAbove.getDestScrollX(1));
				// ShowToast("Opened");
				recentFragment.startAvatarOut();	
			}
		});
        
        menu.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				
			}
		});
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
//		nearByFragment = new NearByFragment(MainActivity.this);
		findFragemnt = new FindFragment(MainActivity.this);
		
		fragments = new Fragment[] {recentFragment, contactFragment, findFragemnt };
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
		case R.id.btn_find:
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
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
		
//		if (nearByFragment != null && nearByFragment.mShakeListener != null) {
//			nearByFragment.mShakeListener.stop();
//		}
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
	
//	AutomticAddToContactMessageReceiver addToContactMessageReceiver;
//	
//	private void initAutomaticAddToContactMessageBroadCast(){
//		addToContactMessageReceiver = new AutomticAddToContactMessageReceiver();
//		IntentFilter intentFilter = new IntentFilter(BmobConfig.);
//		
//		
//		//优先级要低于ChatActivity
//		intentFilter.setPriority(3);
//		registerReceiver(addToContactMessageReceiver, intentFilter);
//	}
//	
//	public class AutomticAddToContactMessageReceiver extends BroadcastReceiver{
//
//	    @Override
//	    public void onReceive(Context context, Intent intent) {
//	        // TODO Auto-generated method stub
//	        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
//	            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
//	            
//	            // 通知栏显示通知，并加为好友和更新本地好友数据库
//	            String[] message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING).split("_");
//	            String username = message[0];
//	            BmobQuery<User> query = new BmobQuery<User>();
//	            query.addWhereEqualTo("username", username);
//	            query.findObjects(MainActivity.this, new FindListener<User>() {
//					
//					@Override
//					public void onSuccess(List<User> arg0) {
//						// TODO Auto-generated method stub
//						if (arg0.size() > 0) {
//							
//							final User user = arg0.get(0);
//							
//							User currentUser = CustomApplcation.getInstance().getCurrentUser();
//							BmobRelation relation = new BmobRelation();
//							relation.add(user);
//							currentUser.setContacts(relation);
//							
//							currentUser.update(MainActivity.this, new UpdateListener() {
//								
//								@Override
//								public void onSuccess() {
//									// TODO Auto-generated method stub
//									ShowToast("添加成功！");
//									
//									// 更新本地好友数据库
//									BmobDB.create(MainActivity.this).saveContact(user);
//									
//									//保存到application中方便比较
//									CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity.this).getContactList()));	
//								}
//								
//								@Override
//								public void onFailure(int arg0, String arg1) {
//									// TODO Auto-generated method stub
//									ShowToast("添加失败！");
//								}
//							});
//						}
//					}
//					
//					@Override
//					public void onError(int arg0, String arg1) {
//						// TODO Auto-generated method stub
//						ShowToast("添加到通讯录失败");
//					}
//				});
//	        }
//	    }
//
//	}
	
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
	
	
//	/** 刷新好友请求
//	  * @Title: notifyAddUser
//	  * @Description: TODO
//	  * @param @param message 
//	  * @return void
//	  * @throws
//	  */
//	private void refreshInvite(BmobInvitation message){
//		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
//		if(isAllow){
//			CustomApplcation.getInstance().getMediaPlayer().start();
//		}
//		iv_contact_tips.setVisibility(View.VISIBLE);
//		if(currentTabIndex == 1){
//			if(contactFragment != null){
//				contactFragment.refresh();
//			}
//		}else{
//			//同时提醒通知
//			String tickerText = message.getFromname()+"请求添加好友";
//			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
//			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);
//		}
//	}
	
	private void refreshInvite(BmobInvitation message){
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if(currentTabIndex == 1){
			if(contactFragment != null){
				contactFragment.refresh();
			}
		}else{
			//同时提醒通知
			String tickerText = message.getFromname() + "(" + message.getNick() + ")" + "已把您加入通讯录";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
			
			// 把他加入通讯录
			automaticAddToContact(message.getFromname());
		}
	}
	
	public void automaticAddToContact(String username) {
		BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", username);
        query.findObjects(MainActivity.this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() > 0) {
					
					final User user = arg0.get(0);
					
					User currentUser = CustomApplcation.getInstance().getCurrentUser();
					BmobRelation relation = new BmobRelation();
					relation.add(user);
					currentUser.setContacts(relation);
					
					currentUser.update(MainActivity.this, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							ShowToast("添加成功！");
							
							// 更新本地好友数据库
							BmobDB.create(MainActivity.this).saveContact(user);
							
							//保存到application中方便比较
							CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity.this).getContactList()));	
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast("添加失败！");
						}
					});
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("添加到通讯录失败");
			}
		});
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
}
