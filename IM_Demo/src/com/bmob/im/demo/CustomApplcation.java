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
 * 自定义全局Applcation类
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @author smile
 * @date 2014-5-19 下午3:25:00
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	
	// 上一次定位到的经纬度
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
		// 是否开启debug模式--默认开启状态
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
	 	
		init();
	}

	private void init() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		myWallPhoto = new ArrayList<String>();
		
		loveList.add("热恋");
		loveList.add("单身");
		loveList.add("失恋");
		loveList.add("保密");
		
		gameDifficultyList.add("简单");
		gameDifficultyList.add("一般");
		gameDifficultyList.add("困难");
		
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
		
		// 查询游戏数据库
		// 更新本地游戏信息
//		updateLocalGameDB();
		
		// 初始话gameList
		initGameList();
		
		// 初始化ImageLoader
		initImageLoader(getApplicationContext());
		
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
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
		gameInfo1.setGame_name("水果连连看");
		gameInfo1.setGame_rule(getString(R.string.game_lianliankan_rule_details));
		gameInfo1.setGame_status(2);
		gameInfo1.setGame_win_method(getString(R.string.game_lianliankan_win_method));
		gameInfo1.setObject_id("lianliankan");
		gameInfo1.setPackage_name("gamelianliankan");
		gameInfo1.setGame_version("1.0");
		gameInfo1.setNotificationId(0);
		
		GameInfo gameInfo2 = new GameInfo();
		gameInfo2.setGame_icon("game_icon.png");
		gameInfo2.setGame_name("猜数字");
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
				// 插入游戏
				
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
	
//	// 查询游戏数据库
//	// 更新本地游戏信息
//	public void updateLocalGameDB() {
//		// 先判断本地数据库是否为空
//		
//		// 如果本地数据库是空的，就从网络读取数据，并写入本地数据库
//		if (databaseUtil.isDBNull()) {
//			
//			Log.i("NNNNNNNNNNNNNNNNNNNNNNNNN", "本地数据库为空");
//			
//			// 先插入三个基本的游戏信息
//			insertThreeBasicGame();
//			
//			// 插入扩展的游戏信息
//			insertExtendGame();
//			
//		}else {  // 如果本地游戏数据库不为空
//			// 判断是否需要更新，需要就更新，如果数据库没有某条信息就从插入
//			Log.i("FFFFFFFFFFFFFFFFFFFFFFFF", "本地数据库不为空");
//			insertExtendGame();
//		}
//	}
//	
//	public void insertThreeBasicGame() {
//		
//		GameInfo gameInfo1 = new GameInfo();
//		gameInfo1.setGame_icon("game_icon.png");
//		gameInfo1.setGame_name("水果连连看");
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
//		gameInfo2.setGame_name("猜数字");
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
//				// 插入游戏
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
	 * 初始化百度相关sdk initBaidumap
	 * @Title: initBaidumap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}

	/**
	 * 初始化百度定位sdk
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
	 * 实现实位回调监听
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
//					BmobLog.i("两次获取坐标相同");
					
					// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocationClient.stop();
					return;
				}
			}
			
			// 设置本次定位信息
			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}

	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		// 获取到缓存的目录地址
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");
		
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	// 单例模式，才能及时返回数据
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
	
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private String longtitude = "";

	/**
	 * 获取经度
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
	 * 设置经度
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

	public final String PREF_LATITUDE = "latitude";// 纬度
	private String latitude = "";

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置维度
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
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setLatitude(null);
		setLongtitude(null);
	}
	
	// 判断应用是否安装
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
