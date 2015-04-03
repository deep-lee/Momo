package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
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
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.LocationActivity.BaiduReceiver;
import com.bmob.im.demo.ui.LocationActivity.MyLocationListenner;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NearPeopleMapActivity extends BaseActivity implements OnGetGeoCoderResultListener, onLeftImageButtonClickListener {
	
		// 附近的人列表
		List<User> nears = new ArrayList<User>();
		// 默认查询1公里范围内的人
		private double QUERY_KILOMETERS = 1;
	
		View layout_marker;
		TextView markerText;
		
		View markerView;
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
		
		BmobGeoPoint currentGeoPoint;
	
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_people_map);
		
		layout_marker = LayoutInflater.from(this).inflate(R.layout.item_nears_map_marker, null);
		markerText = (TextView) layout_marker.findViewById(R.id.marker_text);
		
		nearsSex = getIntent().getIntExtra("nearsSex", 2);
		ShowToast("SEX:" + nearsSex);
		switch (nearsSex) {
		case 0:
			sexValue = false;
			break;
		case 1:
			sexValue = true;
		case 2:
			sexValue = null;

		default:
			break;
		}
		
		currentGeoPoint = (BmobGeoPoint) getIntent().getSerializableExtra("currentGeoPoint");
		
		initTopBarForLeft("附近的人");
		initBaiduMap();
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				
				Bundle data = arg0.getExtraInfo();
				final String objectId = data.getString("ObjectId");
				final String username = data.getString("username");
				
				userManager.queryUser(username, new FindListener<User>() {

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<User> arg0) {
						// TODO Auto-generated method stub
//						ShowToast(arg0.get(0).getBirthday());
						User user = arg0.get(0);
						Intent intent = new Intent();
						
						String gameType = user.getGameType();
						
						if (gameType.equals("水果连连看")) {
							intent.setClass(NearPeopleMapActivity.this, GameFruitActivity.class);
						}
						else if (gameType.equals("猜数字")) {
							intent.setClass(NearPeopleMapActivity.this, GuessNumberActivity.class);
						}
						else if (gameType.equals("mixed color")) {
							intent.setClass(NearPeopleMapActivity.this, MixedColorMenuActivity.class);
						}

						
						Bundle data = new Bundle();
						data.putString("from", "other");
						data.putString("username", user.getUsername());
						data.putString("gamedifficulty", user.getGameDifficulty());
						
						intent.putExtras(data);
						
						ShowToast(user.getGameType() + "");
						startActivity(intent);
						
					}
				});
			    			
				return false;
				
				
				
			}
		});
		
		
	}

	
	private void initBaiduMap() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.location_bmapView);
		mBaiduMap = mMapView.getMap();
		
		
		//设置缩放级别
		mBaiduMap.setMaxAndMinZoomLevel(19, 3);
		
		
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
	OverlayOptions textOption;
	// Bitmap markerBitmap;
	Bundle markerData = new Bundle();
	
	
	// 在地图上显示附近的人的信息
	private void initNearsOnMap() {
		// ShowToast(nears.size() + "");
		
		Marker marker;
		
		// 通过循环将附近的人的昵称显示在地图上
		for (User nearUser : nears) {
			
			// ShowToast(nearUser.getNick());
			
			markerText.setText(nearUser.getNick());
			// Toast.makeText(NearPeopleMapActivity.this, nearUser.getNick(), Toast.LENGTH_SHORT).show();
			
			//启用绘图缓存
		    layout_marker.setDrawingCacheEnabled(true);		
		    //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
		    layout_marker.measure(MeasureSpec.makeMeasureSpec(150, MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(60, MeasureSpec.EXACTLY));
		    //这个方法也非常重要，设置布局的尺寸和位置
		    layout_marker.layout(0, 0, layout_marker.getMeasuredWidth(), layout_marker.getMeasuredHeight());
		    //获得绘图缓存中的Bitmap
		    layout_marker.buildDrawingCache();
		    Bitmap markerBitmap = layout_marker.getDrawingCache();
			
			ShowLog("+++++++++++++++" + markerBitmap.getWidth() + "  " + markerBitmap.getHeight());
			
			// 获取附近的人的地理位置信息
			BmobGeoPoint location = nearUser.getLocation();
			point = new LatLng(location.getLatitude(), location.getLongitude());
			
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(markerBitmap);
			
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(point)  
			    .icon(bitmap);  
			
			//在地图上添加Marker，并显示  
			marker = (Marker)mBaiduMap.addOverlay(option);
			
			markerData.putString("ObjectId", nearUser.getObjectId());
			markerData.putString("username", nearUser.getUsername());
			marker.setExtraInfo(markerData);
			
			layout_marker.destroyDrawingCache();
			
			// ShowToast(nearUser.getNick());
			// ShowToast(nearUser.getGameType() + "");
		}
	}
	

	
	ProgressDialog progress ;
	private void initNearByList(final boolean isUpdate){
		if(!isUpdate){
			progress = new ProgressDialog(NearPeopleMapActivity.this);
			progress.setMessage("正在查询附近的人...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
		// 当前用户的地理位置信息不为空
		if(!mApplication.getLatitude().equals("")&&!mApplication.getLongtitude().equals("")){
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double.parseDouble(mApplication.getLongtitude());
			BRequest.QUERY_LIMIT_COUNT = 20;
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
			userManager.queryKiloMetersListByPage(isUpdate,0,"location", longtitude, latitude, true, QUERY_KILOMETERS,"sex", sexValue, new FindListener<User>() {
			
				
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

	/**
	 * 回到聊天界面
	 * @Title: gotoChatPage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void gotoChatPage() {
		if(lastLocation!=null){
			Intent intent = new Intent();
			intent.putExtra("y", lastLocation.getLongitude());// 经度
			intent.putExtra("x", lastLocation.getLatitude());// 维度
			intent.putExtra("address", lastLocation.getAddrStr());
			setResult(RESULT_OK, intent);
			this.finish();
		}else{
			ShowToast("获取地理位置信息失败!");
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
		if (mLocClient != null && mLocClient.isStarted())
			// 请求定位
		    mLocClient.requestLocation();

		if (lastLocation != null) {
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
			
			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
					+ location.getLatitude() + ",地址 = "
					+ lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// 反Geo搜索
				// 如果地址为空，就通过经度和纬度信息进行反向Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// 显示在地图上
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			//设置按钮可点击
			mHeaderLayout.getRightImageButton().setEnabled(true);
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
}
