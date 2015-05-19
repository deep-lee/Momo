package com.bmob.im.demo.ui;

import java.util.List;

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
 * ��½
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 ����2:45:35
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
	
	ImageView iv_recent_tips,iv_contact_tips;//��Ϣ��ʾ
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
		//������ʱ�����񣨵�λΪ�룩-���������̨�Ƿ���δ������Ϣ���еĻ���ȡ����
		//�������ü�����ȽϺ������͵�������Ҳ����ȥ����仰-ͬʱ����onDestory���������stopPollService����
		BmobChat.getInstance(this).startPollService(30);
		
		// ʹ�����ͷ���ʱ�ĳ�ʼ������
	    BmobInstallation.getCurrentInstallation(this).save();
	    // �������ͷ���
//	    BmobPush.startWork(this, com.bmob.im.demo.config.Config.applicationId);
		
		//�����㲥������
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
		
		//�ѵ�һ��tab��Ϊѡ��״̬
		mTabs[0].setSelected(true);
		
		initLeftView();
		
		// ������ȡ��Ƭǽ���߳�
		initWallPhoto();
		
		menuFlag = false;
		
		// �����û�����λ����Ϣ
        updateUserLocation();
        
        User user = userManager.getCurrentUser(User.class);
        CustomApplcation.sex = user.getSex();
        
        ShowToast("" + CustomApplcation.gameList.size());

	}
	
	private void initWallPhoto() {
		
		Thread newThread; // ����һ�����߳�
		newThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	// ����д�����߳���Ҫ���Ĺ���
		    	// ��ǰ��user
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
		newThread.start(); //�����߳�
		    
	}
		
		
	private void initLeftView() {
		      
		// configure the SlidingMenu  
        menu = new SlidingMenu(this);  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset); 
        menu.setFadeEnabled(false);  
        menu.setBehindScrollScale(0.25f);  
        menu.setFadeDegree(0.25f);  
        menu.setMode(SlidingMenu.LEFT);  
           
        // ���ô�����Ļ��ģʽ  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
//        menu.setShadowWidthRes(R.dimen.shadow_width);  
//        menu.setShadowDrawable(R.drawable.shadow);  
//  
//        // ���û����˵���ͼ�Ŀ��  
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
//        // ���ý��뽥��Ч����ֵ  
//        menu.setFadeDegree(0.35f);  
        
        // ���ñ���ͼƬ  
        menu.setBackgroundImage(R.drawable.icon_bg1);  
        // ����ר������Ч��  
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
        
        //Ϊ�໬�˵����ò���  
        menu.setMenu(R.layout.slide_fragment); 
        
      //�໬��ʱ����
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
	 * ��̬��ʾ���˵�
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
		// �����ʾ��һ��fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment).
			add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment).commit();
	}
	
	
	
	/**
	 * button����¼�
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
		//�ѵ�ǰtab��Ϊѡ��״̬
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//СԲ����ʾ
		// �����δ������Ϣ
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
		
		// ������µĺ�������
		if(BmobDB.create(this).hasNewInvite()){
			iv_contact_tips.setVisibility(View.VISIBLE);
		}else{
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
		//���
		MyMessageReceiver.mNewNum=0;
		
//		if (currentTabIndex == 2 && nearByFragment != null && nearByFragment.mShakeListener != null) {
//			
//			if (!nearByFragment.getFlag() && menuFlag == false) {
//				nearByFragment.openShakeListener();
//			}	
//		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// ȡ���������͵���Ϣ
		
//		if (nearByFragment != null && nearByFragment.mShakeListener != null) {
//			nearByFragment.mShakeListener.stop();
//		}
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}
	
	
	/** ˢ�½���
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		// ������ʾ
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		//ҲҪ�洢����
		if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}
		if(currentTabIndex==0){
			//��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
			if(recentFragment != null){
				// ˢ�»Ựҳ��
				recentFragment.refresh();
			}
		}
	}
	
	NewBroadcastReceiver  newReceiver;
	
	private void initNewMessageBroadCast(){
		// ע�������Ϣ�㲥
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//���ȼ�Ҫ����ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}
	
	/**
	 * ����Ϣ�㲥������
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//ˢ�½���
			refreshNewMsg(null);
			// �ǵðѹ㲥���ս��
			abortBroadcast();
		}
	}
	
	TagBroadcastReceiver  userReceiver;
	
	private void initTagMessageBroadCast(){
		// ע�������Ϣ�㲥
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		//���ȼ�Ҫ����ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}
	
	/**
	 * ��ǩ��Ϣ�㲥������
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			refreshInvite(message);
			// �ǵðѹ㲥���ս��
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
//		//���ȼ�Ҫ����ChatActivity
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
//	            Log.d("bmob", "�ͻ����յ��������ݣ�" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
//	            
//	            // ֪ͨ����ʾ֪ͨ������Ϊ���Ѻ͸��±��غ������ݿ�
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
//									ShowToast("��ӳɹ���");
//									
//									// ���±��غ������ݿ�
//									BmobDB.create(MainActivity.this).saveContact(user);
//									
//									//���浽application�з���Ƚ�
//									CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity.this).getContactList()));	
//								}
//								
//								@Override
//								public void onFailure(int arg0, String arg1) {
//									// TODO Auto-generated method stub
//									ShowToast("���ʧ�ܣ�");
//								}
//							});
//						}
//					}
//					
//					@Override
//					public void onError(int arg0, String arg1) {
//						// TODO Auto-generated method stub
//						ShowToast("��ӵ�ͨѶ¼ʧ��");
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
	
	
//	/** ˢ�º�������
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
//			//ͬʱ����֪ͨ
//			String tickerText = message.getFromname()+"������Ӻ���";
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
			//ͬʱ����֪ͨ
			String tickerText = message.getFromname() + "(" + message.getNick() + ")" + "�Ѱ�������ͨѶ¼";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
			
			// ��������ͨѶ¼
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
							ShowToast("��ӳɹ���");
							
							// ���±��غ������ݿ�
							BmobDB.create(MainActivity.this).saveContact(user);
							
							//���浽application�з���Ƚ�
							CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity.this).getContactList()));	
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast("���ʧ�ܣ�");
						}
					});
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("��ӵ�ͨѶ¼ʧ��");
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
	 * ���������η��ؼ����˳�
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("�ٰ�һ���˳�����");
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
		//ȡ����ʱ������
		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onMenuItemLongClick(View clickedView, int position) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onMenuItemClick(View clickedView, int position) {
//		
//		position--;
//		// ShowToast("" + position);
//		// TODO Auto-generated method stub
//		if (position >= 0 && position <= 2) {
//			if (nearsSex == position) {
//				
//			}else {
//				editor.putInt("nearsSex", position);
//				nearsSex = position;
//				editor.commit();
//				
//				nearByFragment.nearBySexChanged(position);
//
//			}
//		}
//		
//		// �������λ����Ϣ
//		if (position == 3) {
//			
//		}
//	}
}
