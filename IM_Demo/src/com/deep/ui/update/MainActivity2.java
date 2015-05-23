package com.deep.ui.update;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
import cn.bmob.v3.update.BmobUpdateAgent;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ChatBg;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.EditMyInfoActivity;
import com.bmob.im.demo.ui.NewFriendActivity;
import com.bmob.im.demo.util.CollectionUtils;
import com.deep.ui.fragment.update.NaviFragment;
import com.deep.ui.fragment.update.PersonInfoUpdateFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends BaseSlidingFragmentActivity implements OnClickListener, EventListener{
	
	public static String AUTOMATICADDFRIEND = "automatic_add_friends";
	
	public static int ETIT_MY_INFO = 100;
	
	public static final String TAG = "MainActivity";
	private NaviFragment naviFragment;
	private ImageView leftMenu;
	private SlidingMenu mSlidingMenu;
	
	public static View layout_all;
	public static TextView tv_edit_my_info;

	public static float y;
	
	public static int currentTabIndex = 0;
	
	public static TextView iv_tips;
	
	SharedPreferences mySharedPreferences;
	//实例化SharedPreferences.Editor对象（第二步） 
	SharedPreferences.Editor editor; 
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
    				Bundle data = msg.getData();
    				String chat_bg_address = data.getString("chat_bg_address");
    				CustomApplcation.chatBgAddress = chat_bg_address;
    				
    				editor.putString("chat_bg_address", chat_bg_address);
    				editor.commit();
    				
            		break;
            
            }   
            super.handleMessage(msg);   
       }
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_2);
		
		//开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		//如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(30);
				
		// 使用推送服务时的初始化操作
		BmobInstallation.getCurrentInstallation(this).save();
				
		//开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		
		BmobUpdateAgent.initAppVersion(MainActivity2.this);
		
		BmobUpdateAgent.update(this);
		
		mySharedPreferences = getSharedPreferences("test", 
				Activity.MODE_PRIVATE); 
		editor = mySharedPreferences.edit(); 
		
		// 获取用户设置的聊天背景
		getUserChatBg();
		
		layout_all = findViewById(R.id.layout_all);
		tv_edit_my_info = (TextView) findViewById(R.id.topbar_edit_my_info);
		iv_tips = (TextView) findViewById(R.id.tv_main_tips);
		tv_edit_my_info.setClickable(true);
		leftMenu = (ImageView) findViewById(R.id.topbar_menu_left);
		leftMenu.setOnClickListener(this);
		tv_edit_my_info.setOnClickListener(this);
		
		// 启动获取照片墙的线程
		initWallPhoto();
		
		// 更新用户地理位置信息
        updateUserLocation();
		
		initFragment();
		
	}
	
	private void initFragment() {
		mSlidingMenu = getSlidingMenu();
		setBehindContentView(R.layout.frame_navi); // 给滑出的slidingmenu的fragment制定layout
		naviFragment = new NaviFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_navi, naviFragment).commit();
		// 设置slidingmenu的属性
		mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置slidingmeni从哪侧出现
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 只有在边上才可以打开
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 偏移量
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setFadeDegree(0.5f);
		mSlidingMenu.setMenu(R.layout.frame_navi);

		Bundle mBundle = null;
		// 导航打开监听事件
		mSlidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				naviFragment.showOpenAnimation();
				//ShowToast("Opened");
			}
		});
		
		// 导航关闭监听事件
		mSlidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
			}
		});
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
		    		
		    		CustomApplcation.myWallPhoto.clear();
		    		
		    		for (int i = 0; i < myPhotoOrigin.length; i++) {
						CustomApplcation.myWallPhoto.add(myPhotoOrigin[i]);
					}
		    		
				}
		    }
		});
		newThread.start(); //启动线程
		    
	}
	
	/*
	 * 获取用的聊天背景
	 */
	public void getUserChatBg() {
		Log.i("bu下载", "调用了");
		new Thread(){
			@Override
			public void run(){
				
				downLoadChatBgToSD(CustomApplcation.getInstance().getCurrentUser().getChatBg());
			}
			
		}.start();
	}
	
	public void downLoadChatBgToSD(ChatBg chatBg) {
		
		String urlStr = chatBg.getFile().getFileUrl(MainActivity2.this);
		String dirName = "";
		dirName = Environment.getExternalStorageDirectory()+"/Find/";
		File f = new File(dirName);
		if(!f.exists())
		{
		    f.mkdir();
		}
		
		String newFilename = urlStr.substring(urlStr.lastIndexOf("/") + 1);
		newFilename = dirName + newFilename;
		File file = new File(newFilename);
		//如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if(file.exists())
		{
		    file.delete();
		}
		
		try {
	         // 构造URL   
	         URL url = new URL(urlStr);   
	         // 打开连接   
	         URLConnection con = url.openConnection();
	         //获得文件的长度
	         int contentLength = con.getContentLength();
	         System.out.println("长度 :"+contentLength);
	         // 输入流   
	         InputStream is = con.getInputStream();  
	         // 1K的数据缓冲   
	         byte[] bs = new byte[1024];   
	         // 读取到的数据长度   
	         int len;   
	         // 输出的文件流   
	         OutputStream os = new FileOutputStream(newFilename);   
	         // 开始读取   
	         while ((len = is.read(bs)) != -1) {   
	             os.write(bs, 0, len);   
	         }  
	         // 完毕，关闭所有链接   
	         os.close();  
	         is.close();
	         
	         Message message = new Message();
	         message.what = 0;
	         Bundle data = new Bundle();
	         data.putString("chat_bg_address", file.getAbsolutePath());
	         message.setData(data);
	         handler.sendMessage(message);
	            
		} catch (Exception e) {
		        e.printStackTrace();
		        CustomApplcation.chatBgAddress = mySharedPreferences.getString("chat_bg_address", "default");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.topbar_menu_left:
			mSlidingMenu.toggle();
			break;
		case R.id.topbar_edit_my_info:
			Intent intent2 = new Intent();
			intent2.setClass(MainActivity2.this, EditMyInfoActivity.class);
			startActivityForResult(intent2, ETIT_MY_INFO);
			break;
		default:
			break;
		}
	}
	
	public void setLayoutAnimation(Animation animation) {
		layout_all.startAnimation(animation);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case 100:
			Boolean update = data.getBooleanExtra("update", true);
			PersonInfoUpdateFragment.update = update;
			break;
			
		case 200:
			TranslateAnimation animation = new TranslateAnimation(0, 0, -y, 0);
			animation.setDuration(200);
			animation.setFillAfter(true);
			layout_all.startAnimation(animation);
			super.onActivityResult(requestCode, resultCode, data);
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		mSlidingMenu.toggle();
	}
	
	public static void showEditMyInfo() {
		tv_edit_my_info.setVisibility(View.VISIBLE);
	}
	
	public static void hideEditMyInfo() {
		tv_edit_my_info.setVisibility(View.INVISIBLE);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//小圆点提示
		// 如果有未读的消息
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_tips.setVisibility(View.VISIBLE);
			naviFragment.setChatTipState(true);
			Log.i("MainActivity2", "有未读消息");
			
		}else{
			iv_tips.setVisibility(View.GONE);
			naviFragment.setChatTipState(false);
			Log.i("MainActivity2", "没有未读消息");
		}
		
		// 如果有新的好友邀请
		if(BmobDB.create(this).hasNewInvite()){
			iv_tips.setVisibility(View.VISIBLE);
			naviFragment.setContactTipState(true);
			Log.i("MainActivity2", "有未读好友请求");
		}else{
			iv_tips.setVisibility(View.GONE);
			naviFragment.setContactTipState(false);
			Log.i("MainActivity2", "没有未读好友请求");
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		//清空
		MyMessageReceiver.mNewNum = 0;
		
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

	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
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
	
	/** 刷新界面
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		
		ShowToast("收到了新消息");
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_tips.setVisibility(View.VISIBLE);
		naviFragment.setChatTipState(true);
		//也要存储起来
		if(message!=null){
			BmobChatManager.getInstance(MainActivity2.this).saveReceiveMessage(true,message);
		}
		
		if(currentTabIndex == 0){
			//当前页面如果为会话页面，刷新此页面
			if(naviFragment != null && naviFragment.getmRecentUpdateFragment() != null){
				// 刷新会话页面
				naviFragment.getmRecentUpdateFragment().refresh();
				iv_tips.setVisibility(View.GONE);
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
	
	private void refreshInvite(BmobInvitation message){
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_tips.setVisibility(View.VISIBLE);
		naviFragment.setContactTipState(true);
		if(currentTabIndex == 1){
			if(naviFragment != null && naviFragment.getmContactUpdateFragment() != null){
				naviFragment.getmContactUpdateFragment().refresh();
				iv_tips.setVisibility(View.GONE);
			}
		}else{
			//同时提醒通知
			String tickerText = message.getFromname() + "(" + message.getNick() + ")" + "通过了你设置的游戏，并已把您加入通讯录";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
			
			// 把他加入通讯录
			automaticAddToContact(message.getFromname());
		}
	}
	
	public void automaticAddToContact(String username) {
		BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", username);
        query.findObjects(MainActivity2.this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() > 0) {
					
					final User user = arg0.get(0);
					
					User currentUser = CustomApplcation.getInstance().getCurrentUser();
					BmobRelation relation = new BmobRelation();
					relation.add(user);
					currentUser.setContacts(relation);
					
					currentUser.update(MainActivity2.this, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							ShowToast("添加成功！");
							
							// 更新本地好友数据库
							BmobDB.create(MainActivity2.this).saveContact(user);
							
							//保存到application中方便比较
							CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity2.this).getContactList()));	
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
	
	public static void	addToContact(String objectId){
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", objectId);
		query.findObjects(CustomApplcation.getInstance(), new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(CustomApplcation.getInstance(), CustomApplcation.getInstance().getString(R.string.network_tips), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				final User user = arg0.get(0);
				
				User currentUser = CustomApplcation.getInstance().getCurrentUser();
				BmobRelation relation = new BmobRelation();
				relation.add(user);
				currentUser.setContacts(relation);
				
				currentUser.update(CustomApplcation.getInstance(), new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(CustomApplcation.getInstance(), "添加成功", Toast.LENGTH_LONG).show();
						
						// 更新本地好友数据库
						BmobDB.create(CustomApplcation.getInstance()).saveContact(user);
						
						//保存到application中方便比较
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(CustomApplcation.getInstance()).getContactList()));	
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(CustomApplcation.getInstance(), "添加失败", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
}
