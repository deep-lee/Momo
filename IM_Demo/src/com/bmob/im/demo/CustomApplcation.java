package com.bmob.im.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * �Զ���ȫ��Applcation��
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @author smile
 * @date 2014-5-19 ����3:25:00
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	// ��һ�ζ�λ���ľ�γ��
	public static BmobGeoPoint lastPoint = null;
	
	public static ArrayList<String> myWallPhoto;
	
	public static List<String> gameList = new ArrayList<String>();
	public static List<String> loveList = new ArrayList<String>();
	public static List<String> gameDifficultyList = new ArrayList<String>();
	public static List<String> careerList = new ArrayList<String>();
	
	public static List<Integer> notificationId = new ArrayList<Integer>();
	
	public static int numOfPhoto = 0;
	
	public static Boolean sex = true;
	
	public static List<GameCard> gameCardList = new ArrayList<GameCard>();
	
	public static ArrayList<Integer> gameName = new ArrayList<Integer>();
	public static ArrayList<Integer> gameIcon = new ArrayList<Integer>();
	public static ArrayList<Integer> gameRuleDetails = new ArrayList<Integer>();
	public static ArrayList<Integer> gameWinMethod = new ArrayList<Integer>();
	
	public static ArrayList<String> gamePackageName = new ArrayList<String>();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// �Ƿ���debugģʽ--Ĭ�Ͽ���״̬
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
	 	
		
		init();
	}

	private void init() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		myWallPhoto = new ArrayList<String>();
		
		gameList.add("ˮ��������");
		gameList.add("������");
		gameList.add("Mixed color");
		gameList.add("oh my egg");
		
		loveList.add("����");
		loveList.add("����");
		loveList.add("ʧ��");
		loveList.add("����");
		
		gameDifficultyList.add("��");
		gameDifficultyList.add("һ��");
		gameDifficultyList.add("����");
		
		careerList.add(getString(R.string.career_list_item_IT));
		careerList.add(getString(R.string.career_list_item_Gong));
		careerList.add(getString(R.string.career_list_item_Shang));
		careerList.add(getString(R.string.career_list_item_Jin));
		careerList.add(getString(R.string.career_list_item_Wen));
		careerList.add(getString(R.string.career_list_item_YiShu));
		careerList.add(getString(R.string.career_list_item_YiLiao));
		careerList.add(getString(R.string.career_list_item_Fa));
		careerList.add(getString(R.string.career_list_item_Jiao));
		careerList.add(getString(R.string.career_list_item_Zheng));
		careerList.add(getString(R.string.career_list_item_Xue));
		careerList.add(getString(R.string.career_list_item_No));
		
		gameName.add(R.string.game_lianliankan_name);
		gameName.add(R.string.game_guess_number_name);
		gameName.add(R.string.game_mixed_color_name);
		gameName.add(R.string.game_oh_my_egg_name);
		
		gamePackageName.add("com.deep.lianliankan");
		gamePackageName.add("com.deeo.caishuzi");
		gamePackageName.add("com.deep.mixedcoloe");
		gamePackageName.add("com.nsu.ttgame.ohmyeggs");
		
		notificationId.add(0000);
		notificationId.add(0001);
		notificationId.add(0002);
		notificationId.add(0003);
		
		gameIcon.add(R.drawable.game_icon);
		gameIcon.add(R.drawable.game_icon);
		gameIcon.add(R.drawable.icon_mixed_color);
		gameIcon.add(R.drawable.icon_oh_my_egg);
		
		gameRuleDetails.add(R.string.game_lianliankan_rule_details);
		gameRuleDetails.add(R.string.game_guess_number_rule_details);
		gameRuleDetails.add(R.string.game_mixed_color_rule_details);
		gameRuleDetails.add(R.string.game_oh_my_egg_rule_details);
		
		gameWinMethod.add(R.string.game_lianliankan_win_method);
		gameWinMethod.add(R.string.game_guess_number_win_method);
		gameWinMethod.add(R.string.game_mixed_color_win_method);
		gameWinMethod.add(R.string.game_oh_my_egg_win_method);
		
		/*
		 * 0 δ���أ�δ��װ
		 * 1 �����أ�δ��װ
		 * 2 �����أ��Ѱ�װ
		 */
		
		for (int i = 0; i < gameName.size(); i++) {
			
			switch (i) {
			case 0:
				gameCardList.add(new GameCard(getResources().getString(gameName.get(i)), getResources().getString(gameRuleDetails.get(i)),
						getResources().getString(gameWinMethod.get(i)), gameIcon.get(i), 2));
				break;
				
			case 1:
				gameCardList.add(new GameCard(getResources().getString(gameName.get(i)), getResources().getString(gameRuleDetails.get(i)),
						getResources().getString(gameWinMethod.get(i)), gameIcon.get(i), 2));
				break;
				
			case 2:
				gameCardList.add(new GameCard(getResources().getString(gameName.get(i)), getResources().getString(gameRuleDetails.get(i)),
						getResources().getString(gameWinMethod.get(i)), gameIcon.get(i), 2));
				break;
				
			// Oh my egg
			case 3:
				
				int gameStatus = 0;
				Boolean hasInstalled = isAppInstalled(getApplicationContext(), "com.nsu.ttgame.ohmyeggs");
				
				if (hasInstalled) {
					gameStatus = 2;
				}else {
					Boolean hasDownloaded = isApkDownloaded(gameList.get(i) + "_" + notificationId.get(i));
					if (hasDownloaded) {
						gameStatus = 1;
					}
					else {
						gameStatus = 0;
					}
				}
				
				gameCardList.add(new GameCard(getResources().getString(gameName.get(i)), getResources().getString(gameRuleDetails.get(i)),
						getResources().getString(gameWinMethod.get(i)), gameIcon.get(i), gameStatus));
				break;

			default:
				break;
			}
			
			
		}
		
		// ��ʼ��ImageLoader
		initImageLoader(getApplicationContext());
		
		// ���û���½�������ȴӺ������ݿ���ȡ������list�����ڴ���
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// ��ȡ���غ���user list���ڴ�,�����Ժ��ȡ����list
			contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
		}
		initBaidu();
	}

	/**
	 * ��ʼ���ٶ����sdk initBaidumap
	 * @Title: initBaidumap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// ��ʼ����ͼSdk
		SDKInitializer.initialize(this);
		// ��ʼ����λsdk
		initBaiduLocClient();
	}

	/**
	 * ��ʼ���ٶȶ�λsdk
	 * @Title: initBaiduLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			if (lastPoint != null) {
				if (lastPoint.getLatitude() == location.getLatitude()
						&& lastPoint.getLongitude() == location.getLongitude()) {
//					BmobLog.i("���λ�ȡ������ͬ");
					
					// �����������ȡ���ĵ���λ����������ͬ�ģ����ٶ�λ
					mLocationClient.stop();
					return;
				}
			}
			
			// ���ñ��ζ�λ��Ϣ
			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}

	/** ��ʼ��ImageLoader */
	public static void initImageLoader(Context context) {
		// ��ȡ�������Ŀ¼��ַ
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");
		
		// ��������ImageLoader(���е�ѡ��ǿ�ѡ��,ֻʹ����Щ������붨��)����������趨��APPLACATION���棬����Ϊȫ�ֵ����ò���
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// �̳߳��ڼ��ص�����
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// �������ʱ���URI������MD5 ����
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// �Զ��建��·��
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// ȫ�ֳ�ʼ��������
	}

	// ����ģʽ�����ܼ�ʱ��������
	public static CustomApplcation getInstance() {
		return mInstance;
	}

	
	SharePreferenceUtil mSpUtil;
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	NotificationManager mNotificationManager;

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	MediaPlayer mMediaPlayer;

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}
	
	public final String PREF_LONGTITUDE = "longtitude";// ����
	private String longtitude = "";

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * ���þ���
	 * 
	 * @param pwd
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}

	public final String PREF_LATITUDE = "latitude";// γ��
	private String latitude = "";

	/**
	 * ��ȡγ��
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * ����ά��
	 * 
	 * @param pwd
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}

	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

	/**
	 * ��ȡ�ڴ��к���user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * ���ú���user list���ڴ���
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * �˳���¼,��ջ�������
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setLatitude(null);
		setLongtitude(null);
	}
	
	// �ж�Ӧ���Ƿ�װ
	public static boolean isAppInstalled(Context context,String packagename)
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
    
	public static Boolean isApkDownloaded(String fileName) {
		
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
	
	

}
