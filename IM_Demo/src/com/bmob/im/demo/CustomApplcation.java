package com.bmob.im.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.QiangYu;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ActivityManagerUtils;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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
	
	public static List<GameInfo> gameList = new ArrayList<GameInfo>();
	
	public static List<String> loveList = new ArrayList<String>();
	public static List<String> gameDifficultyList = new ArrayList<String>();
	public static List<String> careerList = new ArrayList<String>();
	
	public static int numOfPhoto = 0;
	
	public static Boolean sex = true;
	
	private QiangYu currentQiangYu = null;
	
	
	
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
		
		
//		databaseUtil = GameDatabaseUtil.getInstance(getApplicationContext());
//		databaseUtil.deleteTable();
		
		// ��ѯ��Ϸ���ݿ�
		// ���±�����Ϸ��Ϣ
//		updateLocalGameDB();
		
		// ��ʼ��gameList
		initGameList();
		
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
	
	public void initGameList() {
//		gameList.clear();
//		
//		databaseUtil.queryAllGame();
		
		GameInfo gameInfo1 = new GameInfo();
		gameInfo1.setGame_icon("game_icon.png");
		gameInfo1.setGame_name("ˮ��������");
		gameInfo1.setGame_rule(getString(R.string.game_lianliankan_rule_details));
		gameInfo1.setGame_status(2);
		gameInfo1.setGame_win_method(getString(R.string.game_lianliankan_win_method));
		gameInfo1.setObject_id("lianliankan");
		gameInfo1.setPackage_name("gamelianliankan");
		gameInfo1.setGame_version("1.0");
		gameInfo1.setNotificationId(0);
		
		GameInfo gameInfo2 = new GameInfo();
		gameInfo2.setGame_icon("game_icon.png");
		gameInfo2.setGame_name("������");
		gameInfo2.setGame_rule(getString(R.string.game_guess_number_rule_details));
		gameInfo2.setGame_status(2);
		gameInfo2.setGame_win_method(getString(R.string.game_guess_number_win_method));
		gameInfo2.setObject_id("guessnumber");
		gameInfo2.setPackage_name("gameguessnumber");
		gameInfo2.setGame_version("1.0");
		gameInfo2.setNotificationId(1);
		
		GameInfo gameInfo3 = new GameInfo();
		gameInfo3.setGame_icon("icon_mixed_color.png");
		gameInfo3.setGame_name("Mixed color");
		gameInfo3.setGame_rule(getString(R.string.game_mixed_color_rule_details));
		gameInfo3.setGame_status(2);
		gameInfo3.setGame_win_method(getString(R.string.game_mixed_color_win_method));
		gameInfo3.setObject_id("mixedcolor");
		gameInfo3.setPackage_name("gamemixedcolor");
		gameInfo3.setGame_version("1.0");
		gameInfo3.setNotificationId(2);
		
		gameList.add(gameInfo1);
		gameList.add(gameInfo2);
		gameList.add(gameInfo3);
		
		BmobQuery<GameFile> query = new BmobQuery<GameFile>();
		query.setLimit(100);
		query.findObjects(getApplicationContext(), new FindListener<GameFile>() {
			
			@Override
			public void onSuccess(List<GameFile> arg0) {
				// TODO Auto-generated method stub
				// ������Ϸ
				
				Toast.makeText(getApplicationContext(), "" + arg0.size(), Toast.LENGTH_LONG).show();
				
				for (Iterator<GameFile> iterator = arg0.iterator(); iterator.hasNext();) {
					
					GameFile gameFile = (GameFile) iterator.next();
					GameInfo gameInfo = new GameInfo();
					gameInfo.setGame_icon(gameFile.getGameIcon());
					gameInfo.setGame_name(gameFile.getGameName());
					gameInfo.setGame_rule(gameFile.getGameRule());
					gameInfo.setGame_status(getGameStatus(gameFile.getPackageName(), gameFile.getGameName(), gameFile.getNotificationId()));
					gameInfo.setGame_win_method(gameFile.getGameWinMethod());
					gameInfo.setObject_id(gameFile.getObjectId());
					gameInfo.setPackage_name(gameFile.getPackageName());
					gameInfo.setGame_version(gameFile.getGameVersion());
					gameInfo.setNotificationId(gameFile.getNotificationId());
					
//					databaseUtil.insertGame(gameInfo);
					gameList.add(gameInfo);
					
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
//	// ��ѯ��Ϸ���ݿ�
//	// ���±�����Ϸ��Ϣ
//	public void updateLocalGameDB() {
//		// ���жϱ������ݿ��Ƿ�Ϊ��
//		
//		// ����������ݿ��ǿյģ��ʹ������ȡ���ݣ���д�뱾�����ݿ�
//		if (databaseUtil.isDBNull()) {
//			
//			Log.i("NNNNNNNNNNNNNNNNNNNNNNNNN", "�������ݿ�Ϊ��");
//			
//			// �Ȳ���������������Ϸ��Ϣ
//			insertThreeBasicGame();
//			
//			// ������չ����Ϸ��Ϣ
//			insertExtendGame();
//			
//		}else {  // ���������Ϸ���ݿⲻΪ��
//			// �ж��Ƿ���Ҫ���£���Ҫ�͸��£�������ݿ�û��ĳ����Ϣ�ʹӲ���
//			Log.i("FFFFFFFFFFFFFFFFFFFFFFFF", "�������ݿⲻΪ��");
//			insertExtendGame();
//		}
//	}
//	
//	public void insertThreeBasicGame() {
//		
//		GameInfo gameInfo1 = new GameInfo();
//		gameInfo1.setGame_icon("game_icon.png");
//		gameInfo1.setGame_name("ˮ��������");
//		gameInfo1.setGame_rule(getString(R.string.game_lianliankan_rule_details));
//		gameInfo1.setGame_status(2);
//		gameInfo1.setGame_win_method(getString(R.string.game_lianliankan_win_method));
//		gameInfo1.setObject_id("lianliankan");
//		gameInfo1.setPackage_name("gamelianliankan");
//		gameInfo1.setGame_version("1.0");
//		gameInfo1.setNotificationId(0);
//		
//		GameInfo gameInfo2 = new GameInfo();
//		gameInfo2.setGame_icon("game_icon.png");
//		gameInfo2.setGame_name("������");
//		gameInfo2.setGame_rule(getString(R.string.game_guess_number_rule_details));
//		gameInfo2.setGame_status(2);
//		gameInfo2.setGame_win_method(getString(R.string.game_guess_number_win_method));
//		gameInfo2.setObject_id("guessnumber");
//		gameInfo2.setPackage_name("gameguessnumber");
//		gameInfo2.setGame_version("1.0");
//		gameInfo2.setNotificationId(1);
//		
//		GameInfo gameInfo3 = new GameInfo();
//		gameInfo3.setGame_icon("icon_mixed_color.png");
//		gameInfo3.setGame_name("Mixed color");
//		gameInfo3.setGame_rule(getString(R.string.game_mixed_color_rule_details));
//		gameInfo3.setGame_status(2);
//		gameInfo3.setGame_win_method(getString(R.string.game_mixed_color_win_method));
//		gameInfo3.setObject_id("mixedcolor");
//		gameInfo3.setPackage_name("gamemixedcolor");
//		gameInfo3.setGame_version("1.0");
//		gameInfo3.setNotificationId(2);
//		
//		databaseUtil.insertGame(gameInfo1);
//		databaseUtil.insertGame(gameInfo2);
//		databaseUtil.insertGame(gameInfo3);
//		
//	}
//	
//	public void insertExtendGame() {
//		
//		BmobQuery<GameFile> query = new BmobQuery<GameFile>();
//		query.setLimit(100);
//		query.findObjects(getApplicationContext(), new FindListener<GameFile>() {
//			
//			@Override
//			public void onSuccess(List<GameFile> arg0) {
//				// TODO Auto-generated method stub
//				// ������Ϸ
//				
//				Toast.makeText(getApplicationContext(), "" + arg0.size(), Toast.LENGTH_LONG).show();
//				
//				for (Iterator<GameFile> iterator = arg0.iterator(); iterator.hasNext();) {
//					
//					GameFile gameFile = (GameFile) iterator.next();
//					GameInfo gameInfo = new GameInfo();
//					gameInfo.setGame_icon(gameFile.getGameIcon());
//					gameInfo.setGame_name(gameFile.getGameName());
//					gameInfo.setGame_rule(gameFile.getGameRule());
//					gameInfo.setGame_status(getGameStatus(gameFile.getPackageName(), gameFile.getGameName(), gameFile.getNotificationId()));
//					gameInfo.setGame_win_method(gameFile.getGameWinMethod());
//					gameInfo.setObject_id(gameFile.getObjectId());
//					gameInfo.setPackage_name(gameFile.getPackageName());
//					gameInfo.setGame_version(gameFile.getGameVersion());
//					gameInfo.setNotificationId(gameFile.getNotificationId());
//					
//					databaseUtil.insertGame(gameInfo);
//					
//				}
//			}
//			
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
//	}
	
	public int getGameStatus(String packageName, String gameName, int notificationId) {
		
		if (notificationId < 3) {
			return 2;
		}
		
		int gameStatus = 0;
		Boolean hasInstalled = isAppInstalled(getApplicationContext(), packageName);
		
		if (hasInstalled) {
			gameStatus = 2;
		}else {
			Boolean hasDownloaded = isApkDownloaded(gameName + "_" + notificationId);
			if (hasDownloaded) {
				gameStatus = 1;
			}
			else {
				gameStatus = 0;
			}
		}
		
		return gameStatus;
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
	
	public List<String> getSelectAbleGame() {
		
		List<String> list = new ArrayList<String>();
		for (Iterator<GameInfo> iterator = gameList.iterator(); iterator.hasNext();) {
			
			GameInfo gameInfo = (GameInfo) iterator.next();
			if (gameInfo.getGame_status() == 2 || gameInfo.getGame_status() == 3) {
				list.add(gameInfo.getGame_name());
			}
		}
		
		return list;
		
	}
	
	public int getCurrentNetType() {
		
		int state = 0;
		
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			state = 0;
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			state = 2;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			state = 1;
		}
		return state;
	}
	
	public DisplayImageOptions getOptions(int drawableId){
		return new DisplayImageOptions.Builder()
		.showImageOnLoading(drawableId)
		.showImageForEmptyUri(drawableId)
		.showImageOnFail(drawableId)
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	public User getCurrentUser() {
		User user = BmobUser.getCurrentUser(this, User.class);
		if(user != null){
			return user;
		}
		return null;
	}
	
	public Activity getTopActivity(){
		return ActivityManagerUtils.getInstance().getTopActivity();
	}

	public QiangYu getCurrentQiangYu() {
		return currentQiangYu;
	}

	public void setCurrentQiangYu(QiangYu currentQiangYu) {
		this.currentQiangYu = currentQiangYu;
	}
	

}
