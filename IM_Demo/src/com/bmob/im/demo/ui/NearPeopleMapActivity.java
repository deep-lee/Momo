package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.NearPeopleAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NearPeopleMapActivity extends ActivityBase implements OnGetGeoCoderResultListener, onLeftImageButtonClickListener,
		OnMenuItemClickListener, OnMenuItemLongClickListener, IXListViewListener,OnItemClickListener {
	
		private FragmentManager fragmentManager;
		private DialogFragment mMenuDialogFragment;
		
		protected int mScreenWidth;
		protected int mScreenHeight;
		
		PopupWindow popupWindow;
		
		NearPeopleAdapter adapter;
		private View layout_all;
		
		TextView tv_address_name;
	
		public static int clickedUser = 0;
		
		// 附近的人列表
		List<User> nears = new ArrayList<User>();
		// 默认查询1公里范围内的人
		private double QUERY_KILOMETERS = 1;
	
		View layout_marker;
		TextView markerNumber;

		ImageView iv_more;
		
		View layout_nears_info;
		TextView tv_nick, tv_distance;
		
		// 定位相关
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
		
		// Bitmap描述符
		BitmapDescriptor mCurrentMarker;

		MapView mMapView;
		BaiduMap mBaiduMap;

		// 注册广播接收器，用于监听网络以及验证key
		private BaiduReceiver mReceiver;

		// 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，因此需要采取反编码方式去搜索此经纬度代表的地址
		GeoCoder mSearch = null; 

		// 上一次定位
		static BDLocation lastLocation = null;

		BitmapDescriptor bdgeo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo); 
		
		int nearsSex = 2;
		Boolean sexValue = null;
		
		String equalProperty = null;
		
		BmobGeoPoint currentGeoPoint = null;
		BmobGeoPoint randomGeoPoint = null;
		
		SharedPreferences sharedPreferences;
		SharedPreferences.Editor editor;
	
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_people_map);
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		
		layout_all = findViewById(R.id.nears_layout_all);
		
		layout_marker = LayoutInflater.from(this).inflate(R.layout.item_nears_marker, null);
		layout_nears_info = LayoutInflater.from(this).inflate(R.layout.item_nears_map_marker_2, null);
		markerNumber = (TextView) layout_marker.findViewById(R.id.item_nears_marker_number_tv);
		  
		tv_nick = (TextView) layout_nears_info.findViewById(R.id.nears_marker_nick_tv);
		tv_distance = (TextView) layout_nears_info.findViewById(R.id.nears_marker_distance_tv);
		
		iv_more = (ImageView) findViewById(R.id.iv_nears_selector_show);
		
		fragmentManager = getSupportFragmentManager();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), 
        		getMenuObjects());
        
        iv_more.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
			}
		});
		
		sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		nearsSex = sharedPreferences.getInt("nearsSex", 2);
		
		switch (nearsSex) {
		case 0:
			sexValue = false;
			equalProperty = "sex";
			break;
		case 1:
			sexValue = true;
			equalProperty = "sex";
			break;
		case 2:
			sexValue = null;
			equalProperty = null;
			break;

		default:
			break;
		}
		
		currentGeoPoint = (BmobGeoPoint) getIntent().getSerializableExtra("currentGeoPoint");
		randomGeoPoint = (BmobGeoPoint) getIntent().getSerializableExtra("randomGeoPoint");
		
		initBaiduMap();
		
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mBaiduMap.hideInfoWindow();
			}
		});
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				
				int position = marker.getZIndex();
				
				ShowToast("" + position);
				
				clickedUser = position;
				
				final User user = nears.get(position);
				
				tv_nick.setText(user.getNick());
				Double distance = getDistance(new BmobGeoPoint(Double.parseDouble(CustomApplcation.getInstance().getLongtitude()), 
						Double.parseDouble(CustomApplcation.getInstance().getLatitude())), user.getLocation());
				
				tv_distance.setText(distance.intValue() + "米");
				
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				//创建InfoWindow , 传入 view， 地理坐标， click监听器
//				
				Bitmap markerBitmap = getBitmapFromView(layout_nears_info);
				
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(markerBitmap);
				
				InfoWindow mInfoWindow = new InfoWindow(bitmap, llInfo, new InfoWindow.OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick() {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						
						String gameType = user.getGameType();
						
						int gamedifficultNum = 0;
						if (user.getGameDifficulty().equals("简单")) {
							gamedifficultNum = 0;
						}else if (user.getGameDifficulty().equals("一般")) {
							gamedifficultNum = 1;
						}else if (user.getGameDifficulty().equals("困难")) {
							gamedifficultNum = 2;
						}
						
						if (gameType.equals("水果连连看")) {
							intent.setClass(NearPeopleMapActivity.this, GameFruitActivity.class);
						}
						else if (gameType.equals("猜数字")) {
							intent.setClass(NearPeopleMapActivity.this, GuessNumberActivity.class);
						}
						else if (gameType.equals("mixed color")) {
							intent.setClass(NearPeopleMapActivity.this, MixedColorMenuActivity.class);
						}
						else if (gameType.equals("oh my egg")) {
							// 先判断有没有安装这个游戏
							Boolean flag = CustomApplcation.isAppInstalled(NearPeopleMapActivity.this,"com.nsu.ttgame.ohmyeggs");
							
							if (flag) {
								intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
								        .parse("info://调用其他应用程序的Activity" ));  
								//  传递数据   
								intent.putExtra("value", gamedifficultNum);  
							}
							else {
								
								DialogTips dialogTips = new DialogTips(NearPeopleMapActivity.this, 
										"对方设置的游戏是：" + gameType + "，您还没有安装该游戏！请到游戏中心进行安装！", "确认");
								dialogTips.SetOnSuccessListener(new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								});
								dialogTips.SetOnCancelListener(new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								});
								
								dialogTips.show();
								
								return;
							} 
						}

						
						Bundle data = new Bundle();
						data.putString("from", "other");
						data.putString("username", user.getUsername());
						data.putString("gamedifficulty", user.getGameDifficulty());
						
						intent.putExtras(data);
						
						ShowToast(user.getGameType() + "");
						if (gameType.equals("oh my egg")) {
							startActivityForResult(intent, 1); 
						}else {
							startActivity(intent);
						}
					}
				} );
				
				MapStatusUpdate m = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.setMapStatus(m);
				mBaiduMap.showInfoWindow(mInfoWindow);
				
				layout_nears_info.setDrawingCacheEnabled(false);
				layout_nears_info.destroyDrawingCache();
			    			
				return false;
			}
		});
	}
	
	private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<MenuObject>();

        MenuObject close = new MenuObject();
        close.setResource(CustomApplcation.sex? R.drawable.icn_close : R.drawable.icn_close_female);
        close.setBgResource(R.drawable.menu_object_bg);

        MenuObject send = new MenuObject("刷新");
        send.setResource(CustomApplcation.sex? R.drawable.icon_nears_refresh : R.drawable.icon_info_female_female);
        send.setBgResource(R.drawable.menu_object_bg);

        MenuObject like = new MenuObject("切换视图");
        like.setResource(CustomApplcation.sex? R.drawable.icon_nears_change_show : R.drawable.icon_info_male_female);
        like.setBgResource(R.drawable.menu_object_bg);


        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        
        return menuObjects;
    }

	
	private void initBaiduMap() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.location_bmapView);
		mBaiduMap = mMapView.getMap();
		
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(19).build()));
		
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
		
		initNearByList(false);
		
		// 显示附近的人地图
		
		initLocClient();
		
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		

	}
	
	LatLng point;
	
	// 在地图上显示附近的人的信息
	private void initNearsOnMap() {
		// ShowToast(nears.size() + "");
		
		Marker marker;
		
		// 通过循环将附近的人的昵称显示在地图上
		for (int i = 0; i < nears.size(); i++) {
			
			// ShowToast(nearUser.getNick());
			
			User nearUser = nears.get(i);
			
			markerNumber.setText(String.valueOf(i + 1));
			
			ShowToast("" + "当前i："  + markerNumber.getText());
			
			
			// 获取联系人的地理位置信息
			BmobGeoPoint location = nearUser.getLocation();
			
			Bitmap markerBitmap = getBitmapFromView(layout_marker);
			
			point = new LatLng(location.getLatitude(), location.getLongitude());
			
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(markerBitmap);
			
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(point)  
			    .icon(bitmap);  
			
			//在地图上添加Marker，并显示  
			marker = (Marker)mBaiduMap.addOverlay(option);
			marker.setZIndex(i);
			
			layout_marker.setDrawingCacheEnabled(false);
			layout_marker.destroyDrawingCache();
		}
	}
	

	
	CustomProgressDialog progress ;
	private void initNearByList(final boolean isUpdate){
		if(!isUpdate){
			progress = new CustomProgressDialog(NearPeopleMapActivity.this, "正在刷新...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
		// 当前用户的地理位置信息不为空
		if(!mApplication.getLatitude().equals("")&&!mApplication.getLongtitude().equals("")){
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double.parseDouble(mApplication.getLongtitude());
			
			if (randomGeoPoint != null) {
				latitude = randomGeoPoint.getLatitude();
				longtitude = randomGeoPoint.getLongitude();
				QUERY_KILOMETERS = 10;
			}
			BRequest.QUERY_LIMIT_COUNT = 100;
			//封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
			//此方法默认每页查询10条数据,若想查询多于10条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
			// 此方法是新增的查询指定1公里内的性别为女性的用户列表，默认包含好友列表
			//如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可
			
//		    分页加载指定公里范围内用户：排除自己，是否排除好友由开发者决定，可以添加额外查询条件
//
//		    参数：
//		        isPull：是否是属于刷新动作：如果是上拉或者下拉动作则设在为true,其他设在为false - 
//		        page：当前查询页码 - 
//		        property：查询条件，自己定义的位置属性 - 
//		        longtitude：经度 - 
//		        latitude：纬度 - 
//		        isShowFriends：是否显示附近的好友 - 
//		        kilometers - ：公里数
//		        equalProperty：自己定义的其他属性：使用方法AddWhereEqualTo对应的属性名称 - 
//		        equalObj：查询equalProperty属性对应的属性值 - 
//		        findCallback - ：回调 
			// ShowToast(equalProperty + ": " + sexValue);
			userManager.queryKiloMetersListByPage(isUpdate,0,"location", longtitude, latitude, false, QUERY_KILOMETERS, equalProperty,
					sexValue, new FindListener<User>() {
			
				
//			    分页加载全部的用户列表：排除自己，是否排除好友由开发者决定，可以添加额外查询条件
//
//			    参数：
//			        isPull：是否是属于刷新动作：如果是上拉或者下拉动作则设在为true,其他设在为false - 
//			        page：当前查询页码 - 
//			        locationProperty：自己定义的位置属性 - 
//			        longtitude：经度 - 
//			        latitude：纬度 - 
//			        isShowFriends：是否显示附近的好友 - 
//			        equalProperty：自己定义的其他属性：使用方法AddWhereEqualTo对应的属性名称 - 
//			        equalObj：查询equalProperty属性对应的属性值 - 
//			        findCallback - ：回调 
				
			//此方法默认查询所有带地理位置信息的且性别为女的用户列表，如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行
//			userManager.queryNearByListByPage(isUpdate,0,"location", longtitude, latitude, true,"sex",false,new FindListener<User>() {

				// 查询成功
				@Override
				public void onSuccess(List<User> arg0) {
					// TODO Auto-generated method stub
					if (CollectionUtils.isNotNull(arg0)) {
						// 如果是刷新的话
						if(isUpdate){
							nears.clear();
						}
						
						
						
						nears.addAll(arg0);
						
						initNearsOnMap();
						
						if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
							// mListView.setPullLoadEnable(true);
							// ShowToast("附近的人搜索完成!");
						}else{
							// mListView.setPullLoadEnable(true);
						}
					}else{
						ShowToast("暂无附近的人!");
					}
					
					if(!isUpdate){
						progress.dismiss();
					}else{
						// refreshPull();
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ShowToast("查询附近的人出错!");
					// mListView.setPullLoadEnable(false);
					if(!isUpdate){
						progress.dismiss();
					}else{
						// refreshPull();
					}
				}

			});
		}else{
			ShowToast("暂无附近的人!");
			progress.dismiss();
			// refreshPull();
		}
		
	}

	private void initLocClient() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
				com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// 设置产品线
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (randomGeoPoint == null && mLocClient != null && mLocClient.isStarted())
			// 请求定位
		    mLocClient.requestLocation();

		if (lastLocation != null && randomGeoPoint == null) {
			// 显示在地图上
			LatLng ll = new LatLng(lastLocation.getLatitude(),
					lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
						.getLongitude()) {
					BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocClient.stop();
					return;
				}
			}
			lastLocation = location;
			
			if (currentGeoPoint != null) {
				currentGeoPoint.setLongitude(location.getLongitude());
				currentGeoPoint.setLatitude(location.getLatitude());
			}
			
			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
					+ location.getLatitude() + ",地址 = "
					+ lastLocation.getAddrStr());
			
			MyLocationData locData = null;
			LatLng ll = null;
			
			if (randomGeoPoint == null) {
				locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);
				ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				String address = location.getAddrStr();
				if (address != null && !address.equals("")) {
					lastLocation.setAddrStr(address);
				} else {
					// 反Geo搜索
					// 如果地址为空，就通过经度和纬度信息进行反向Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
				}
			}
			else {
				locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(randomGeoPoint.getLatitude())
				.longitude(randomGeoPoint.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);
				ll = new LatLng(randomGeoPoint.getLatitude(),
						randomGeoPoint.getLongitude());
				String address = location.getAddrStr();
				if (address != null && !address.equals("")) {
					lastLocation.setAddrStr(address);
				} else {
					// 反Geo搜索
					// 如果地址为空，就通过经度和纬度信息进行反向Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
				}
			}

			
			
			
			// 显示在地图上
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			//设置按钮可点击
//			mHeaderLayout.getRightImageButton().setEnabled(true);
		}

	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("网络出错");
			}
		}
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(mLocClient!=null && mLocClient.isStarted()){
			// 退出时销毁定位
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// 回收 bitmap 资源
		bdgeo.recycle();
	}


	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		if (currentGeoPoint != null) {
			mApplication.setLongtitude(currentGeoPoint.getLongitude() + "");
		    mApplication.setLatitude(currentGeoPoint.getLatitude() + "");
		}
	}
	
	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	
	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 距离：单位为米
	 */
	public static double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	/**
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.setDrawingCacheEnabled(true);	
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
	}
	
	public Double getDistance(BmobGeoPoint point1, BmobGeoPoint point2) {
		
		double distance;
		if (point1 != null && point2 != null) {
			// 算与附近的人之间的距离
			distance = DistanceOfTwoPoints(
					point1.getLatitude(),
					point1.getLongitude(),
					point2.getLatitude(), 
					point2.getLongitude());
		}
		else {
			distance = 0;
		}
		
		
		return distance;
		
	}
	
	
	
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public void showPopUpWindow() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_up_nears_layout,
				null);
		
		ListView mListView = (ListView) view.findViewById(R.id.list_near);
		
		mListView.setOnItemClickListener(this);
		
		adapter = new NearPeopleAdapter(this, nears);
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(this);
		
		tv_address_name = (TextView) view.findViewById(R.id.tv_position_name);
		
		// 创建地理编码检索实例  
        GeoCoder geoCoder = GeoCoder.newInstance(); 
        
        // 设置地理编码检索监听者  
        geoCoder.setOnGetGeoCodeResultListener(listener);  
        //  
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(Double.parseDouble(CustomApplcation.getInstance().getLatitude()),
        		Double.parseDouble(CustomApplcation.getInstance().getLongtitude()))));  
        // 释放地理编码检索实例  
//        geoCoder.destroy(); 
        
        
		
		popupWindow = new PopupWindow(view, mScreenWidth, 800);
		
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		switch (position) {
		// 刷新
		case 1:
			nears.clear();
			mBaiduMap.clear();
			initNearByList(false);
			initNearsOnMap();
			break;
			
		// 切换视图，弹出窗口
		case 2:
			showPopUpWindow();
			break;

		default:
			break;
		}
		
		ShowToast("" + position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		
		User user = nears.get(position);
		
		clickedUser = position;
		
		String gameType = user.getGameType();
		
		int gamedifficultNum = 0;
		if (user.getGameDifficulty().equals("简单")) {
			gamedifficultNum = 0;
		}else if (user.getGameDifficulty().equals("一般")) {
			gamedifficultNum = 1;
		}else if (user.getGameDifficulty().equals("困难")) {
			gamedifficultNum = 2;
		}
		
		if (gameType.equals("水果连连看")) {
			intent.setClass(NearPeopleMapActivity.this, GameFruitActivity.class);
		}
		else if (gameType.equals("猜数字")) {
			intent.setClass(NearPeopleMapActivity.this, GuessNumberActivity.class);
		}
		else if (gameType.equals("mixed color")) {
			intent.setClass(NearPeopleMapActivity.this, MixedColorMenuActivity.class);
		}
		else if (gameType.equals("oh my egg")) {
			
			// 先判断有没有安装这个游戏
			Boolean flag = CustomApplcation.isAppInstalled(NearPeopleMapActivity.this,"com.nsu.ttgame.ohmyeggs");
			
			if (flag) {
				intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
				        .parse("info://调用其他应用程序的Activity" ));  
				//  传递数据   
				intent.putExtra("value", gamedifficultNum);  
			}
			else {
				
				DialogTips dialogTips = new DialogTips(NearPeopleMapActivity.this, 
						"对方设置的游戏是：" + gameType + "，您还没有安装该游戏！请到游戏中心进行安装！", "确认");
				dialogTips.SetOnSuccessListener(new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				dialogTips.SetOnCancelListener(new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				dialogTips.show();
				
				return;
			}
			
			
		}

		
		Bundle data = new Bundle();
		data.putString("from", "other");
		data.putString("username", user.getUsername());
		data.putString("gamedifficulty", user.getGameDifficulty());
		
		intent.putExtras(data);
		
		ShowToast(user.getGameType() + "");
		if (gameType.equals("oh my egg")) {
			startActivityForResult(intent, 1); 
		}else {
			startActivity(intent);
		}
	}
	
	@Override   
	protected  void  onActivityResult(int  requestCode, int  resultCode, Intent data)  {  
		
		switch (requestCode) {
		case 1:
			
			int gameResult = data.getExtras().getInt("result");
			
			// 赢了
			if (gameResult == 1) {
				User user = nears.get(clickedUser);
				Intent intent = new Intent();
				intent.setClass(NearPeopleMapActivity.this, SetMyInfoActivity2.class);
				intent.putExtra("from", "add");
				intent.putExtra("username", user.getUsername());
				startActivity(intent);
			}
			// 输了
			else if(gameResult == 0){
				
			}
			
			break;

		default:
			break;
		}
	      
	} 

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	 OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
         // 反地理编码查询结果回调函数  
         @Override  
         public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
             if (result == null  
                     || result.error != SearchResult.ERRORNO.NO_ERROR) {  
                 // 没有检测到结果  
 
             }  
             // ShowToast("位置：" + result.getAddress());
             tv_address_name.setText(result.getAddress());
             
         }  

         // 地理编码查询结果回调函数  
         @Override  
         public void onGetGeoCodeResult(GeoCodeResult result) {  
             if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
                 // 没有检测到结果  
             }  
         }  
     };  
}
