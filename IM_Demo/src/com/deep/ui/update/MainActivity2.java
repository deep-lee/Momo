package com.deep.ui.update;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.ui.fragment.update.NaviFragment;
import com.deep.ui.fragment.update.PersonInfoUpdateFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends BaseSlidingFragmentActivity implements OnClickListener, EventListener,
	OnMenuItemClickListener, OnMenuItemLongClickListener{
	
	public static String AUTOMATICADDFRIEND = "automatic_add_friends";
	
	private FragmentManager fragmentManager;
	private DialogFragment mMenuDialogFragment;
	
	Boolean status = false;
	
	public static int ETIT_MY_INFO = 100;
	
	public static final String TAG = "MainActivity";
	private NaviFragment naviFragment;
	private ImageView leftMenu;
	private SlidingMenu mSlidingMenu;
	
	public static View layout_all;
	public static TextView tv_edit_my_info;
	public static ImageView iv_select_nears_sex;
	public static ImageView iv_nears_sex_icon;
	
	public int nearsSex = 2;
	
	public static float y;
	
	public static int currentTabIndex = 0;
	
	public static TextView iv_tips;
	
	SharedPreferences mySharedPreferences;
	//ʵ����SharedPreferences.Editor���󣨵ڶ����� 
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
		
		//������ʱ�����񣨵�λΪ�룩-���������̨�Ƿ���δ������Ϣ���еĻ���ȡ����
		//�������ü�����ȽϺ������͵�������Ҳ����ȥ����仰-ͬʱ����onDestory���������stopPollService����
		BmobChat.getInstance(this).startPollService(30);
				
		// ʹ�����ͷ���ʱ�ĳ�ʼ������
		BmobInstallation.getCurrentInstallation(this).save();
				
		//�����㲥������
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		
		BmobUpdateAgent.initAppVersion(MainActivity2.this);
		
		BmobUpdateAgent.update(this);
		
		mySharedPreferences = getSharedPreferences("test", 
				Activity.MODE_PRIVATE); 
		editor = mySharedPreferences.edit(); 
		
		nearsSex = mySharedPreferences.getInt("nearsSex", 2);
		
		// ��ȡ�û����õ����챳��
		getUserChatBg();
		
		layout_all = findViewById(R.id.layout_all);
		tv_edit_my_info = (TextView) findViewById(R.id.topbar_edit_my_info);
		iv_tips = (TextView) findViewById(R.id.tv_main_tips);
		tv_edit_my_info.setClickable(true);
		leftMenu = (ImageView) findViewById(R.id.topbar_menu_left);
		leftMenu.setOnClickListener(this);
		tv_edit_my_info.setOnClickListener(this);
		iv_select_nears_sex = (ImageView) findViewById(R.id.iv_nears_selector_show);
		
		
		// ������ȡ��Ƭǽ���߳�
		initWallPhoto();
		
		// �����û�����λ����Ϣ
        updateUserLocation();
		
		initFragment();
		
		iv_nears_sex_icon = (ImageView) findViewById(R.id.iv_nears_sex_icon);
		
		switch (nearsSex) {
		// Ů
		case 0:
			iv_nears_sex_icon.setVisibility(View.VISIBLE);
			iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_female);
			break;
		
		// ��
		case 1:
			iv_nears_sex_icon.setVisibility(View.VISIBLE);
			iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_male);
			break;
		
		// ��Ů
		case 2:
			iv_nears_sex_icon.setVisibility(View.INVISIBLE);
			break;
		}
		
		status = CustomApplcation.getInstance().getCurrentUser().getStatus();
		if (status == null) {
			status = true;
		}
		
		fragmentManager = getSupportFragmentManager();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), 
        		getMenuObjects());
        
        iv_select_nears_sex.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
			}
		});
		
	}
	
	private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<MenuObject>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);
        close.setBgResource(R.color.common_action_bar_bg_color);

        MenuObject send = new MenuObject("ֻ��Ů��");
        send.setResource(R.drawable.icon_info_female);
        send.setBgResource(R.color.common_action_bar_bg_color);

        MenuObject like = new MenuObject("ֻ������");
        like.setResource(R.drawable.icon_info_male);
        like.setBgResource(R.color.common_action_bar_bg_color);

        MenuObject addFr = new MenuObject("�鿴ȫ��");
        addFr.setResource(R.drawable.ic_nears_all_people);
        addFr.setBgResource(R.color.common_action_bar_bg_color);
  
        MenuObject addFav = new MenuObject("�������λ����Ϣ���˳�");
        addFav.setResource(R.drawable.ic_nears_clean_position_info);
        addFav.setBgResource(R.color.common_action_bar_bg_color);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        
        return menuObjects;
    }
	
	private void initFragment() {
		mSlidingMenu = getSlidingMenu();
		setBehindContentView(R.layout.frame_navi); // ��������slidingmenu��fragment�ƶ�layout
		naviFragment = new NaviFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_navi, naviFragment).commit();
		// ����slidingmenu������
		mSlidingMenu.setMode(SlidingMenu.LEFT);// ����slidingmeni���Ĳ����
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// ֻ���ڱ��ϲſ��Դ�
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// ƫ����
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setFadeDegree(0.5f);
		mSlidingMenu.setMenu(R.layout.frame_navi);

		Bundle mBundle = null;
		// �����򿪼����¼�
		mSlidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				naviFragment.showOpenAnimation();
				//ShowToast("Opened");
			}
		});
		
		// �����رռ����¼�
		mSlidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
			}
		});
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
		    		
		    		CustomApplcation.myWallPhoto.clear();
		    		
		    		for (int i = 0; i < myPhotoOrigin.length; i++) {
						CustomApplcation.myWallPhoto.add(myPhotoOrigin[i]);
					}
		    		
				}
		    }
		});
		newThread.start(); //�����߳�
		    
	}
	
	/*
	 * ��ȡ�õ����챳��
	 */
	public void getUserChatBg() {
		Log.i("bu����", "������");
		new Thread(){
			@Override
			public void run(){
				
				if (CustomApplcation.getInstance().getCurrentUser().getChatBg().getBelongTo() != 0) {
					downLoadChatBgToSD(CustomApplcation.getInstance().getCurrentUser().getChatBg());
				}else {
					CustomApplcation.chatBgAddress = "default";
					editor.putString("chat_bg_address", "default");
				}
				
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
		//���Ŀ���ļ��Ѿ����ڣ���ɾ�����������Ǿ��ļ���Ч��
		if(file.exists())
		{
		    file.delete();
		}
		
		try {
	         // ����URL   
	         URL url = new URL(urlStr);   
	         // ������   
	         URLConnection con = url.openConnection();
	         //����ļ��ĳ���
	         int contentLength = con.getContentLength();
	         System.out.println("���� :"+contentLength);
	         // ������   
	         InputStream is = con.getInputStream();  
	         // 1K�����ݻ���   
	         byte[] bs = new byte[1024];   
	         // ��ȡ�������ݳ���   
	         int len;   
	         // ������ļ���   
	         OutputStream os = new FileOutputStream(newFilename);   
	         // ��ʼ��ȡ   
	         while ((len = is.read(bs)) != -1) {   
	             os.write(bs, 0, len);   
	         }  
	         // ��ϣ��ر���������   
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
		//ȡ����ʱ������
		BmobChat.getInstance(this).stopPollService();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// ȡ���������͵���Ϣ
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//СԲ����ʾ
		// �����δ������Ϣ
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_tips.setVisibility(View.VISIBLE);
			naviFragment.setChatTipState(true);
			Log.i("MainActivity2", "��δ����Ϣ");
			
		}else{
			iv_tips.setVisibility(View.GONE);
			naviFragment.setChatTipState(false);
			Log.i("MainActivity2", "û��δ����Ϣ");
		}
		
		// ������µĺ�������
		if(BmobDB.create(this).hasNewInvite()){
			iv_tips.setVisibility(View.VISIBLE);
			naviFragment.setContactTipState(true);
			Log.i("MainActivity2", "��δ����������");
		}else{
			iv_tips.setVisibility(View.GONE);
			naviFragment.setContactTipState(false);
			Log.i("MainActivity2", "û��δ����������");
		}
		MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
		//���
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
	
	/** ˢ�½���
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		
		ShowToast("�յ�������Ϣ");
		// ������ʾ
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_tips.setVisibility(View.VISIBLE);
		naviFragment.setChatTipState(true);
		//ҲҪ�洢����
		if(message!=null){
			BmobChatManager.getInstance(MainActivity2.this).saveReceiveMessage(true,message);
		}
		
		if(currentTabIndex == 0){
			//��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
			if(naviFragment != null && naviFragment.getmRecentUpdateFragment() != null){
				// ˢ�»Ựҳ��
				naviFragment.getmRecentUpdateFragment().refresh();
				iv_tips.setVisibility(View.GONE);
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
			//ͬʱ����֪ͨ
			String tickerText = message.getFromname() + "(" + message.getNick() + ")" + "ͨ���������õ���Ϸ�����Ѱ�������ͨѶ¼";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
			
			// ��������ͨѶ¼
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
							ShowToast("��ӳɹ���");
							
							// ���±��غ������ݿ�
							BmobDB.create(MainActivity2.this).saveContact(user);
							
							//���浽application�з���Ƚ�
							CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(MainActivity2.this).getContactList()));	
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
						Toast.makeText(CustomApplcation.getInstance(), "��ӳɹ�", Toast.LENGTH_LONG).show();
						
						// ���±��غ������ݿ�
						BmobDB.create(CustomApplcation.getInstance()).saveContact(user);
						
						//���浽application�з���Ƚ�
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(CustomApplcation.getInstance()).getContactList()));	
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(CustomApplcation.getInstance(), "���ʧ��", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		position--;
		// ShowToast("" + position);
		// TODO Auto-generated method stub
		if (position >= 0 && position <= 2) {
			if (nearsSex == position) {
				
			}else {
				editor.putInt("nearsSex", position);
				nearsSex = position;
				editor.commit();
				
				// �ı�ͼ��
//				nearByFragment.nearBySexChanged(position);
				
				switch (position) {
				// Ů
				case 0:
					iv_nears_sex_icon.setVisibility(View.VISIBLE);
					iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_female);
					break;
				
				// ��
				case 1:
					iv_nears_sex_icon.setVisibility(View.VISIBLE);
					iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_male);
					break;
				
				// ��Ů
				case 2:
					iv_nears_sex_icon.setVisibility(View.INVISIBLE);
					break;

				default:
					break;
				}

			}
		}
		
		// �������λ����Ϣ
		if (position == 3) {
			User user = new User();
			user.setStatus(false);
			updateUserData(user, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// ShowToast("�������λ����Ϣ�ɹ����������˽�����������");
					showClearTips(true);
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					// ShowToast("�������λ����Ϣʧ�ܣ������ԣ�");
					showClearTips(false);
				}
			});
		}
	}
	
	public void showClearTips(final Boolean flag) {
		
		String message;
		if (flag) {
			message = "����λ����Ϣ�����";
		}else {
			message = "�������λ����Ϣʧ��";
		}
		
		DialogTips dialogTips = new DialogTips(MainActivity2.this, "��ʾ", message, 
				"ȷ��", false, true);
		
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		dialogTips.show();
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	public static void setSexShowStatus(Boolean flag) {
		if (flag) {
			iv_nears_sex_icon.setVisibility(View.VISIBLE);
		}else {
			iv_nears_sex_icon.setVisibility(View.INVISIBLE);
		}
	}
	
	public static void setSexSelecteStatus(Boolean flag) {
		if (flag) {
			iv_select_nears_sex.setVisibility(View.VISIBLE);
		}
		else {
			iv_select_nears_sex.setVisibility(View.INVISIBLE);
		}
	}
}
