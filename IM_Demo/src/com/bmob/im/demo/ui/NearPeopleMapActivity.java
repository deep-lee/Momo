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
	
		// ���������б�
		List<User> nears = new ArrayList<User>();
		// Ĭ�ϲ�ѯ1���ﷶΧ�ڵ���
		private double QUERY_KILOMETERS = 1;
	
		View layout_marker;
		TextView markerText;
		
		View markerView;
		// ��λ���
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
		
		// Bitmap������
		BitmapDescriptor mCurrentMarker;

		MapView mMapView;
		BaiduMap mBaiduMap;

		// ע��㲥�����������ڼ��������Լ���֤key
		private BaiduReceiver mReceiver;

		// ����ģ�飬��Ϊ�ٶȶ�λsdk�ܹ��õ���γ�ȣ�����ȴ�޷��õ��������ϸ��ַ�������Ҫ��ȡ�����뷽ʽȥ�����˾�γ�ȴ���ĵ�ַ
		GeoCoder mSearch = null; 

		// ��һ�ζ�λ
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
		
		initTopBarForLeft("��������");
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
						
						if (gameType.equals("ˮ��������")) {
							intent.setClass(NearPeopleMapActivity.this, GameFruitActivity.class);
						}
						else if (gameType.equals("������")) {
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
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.location_bmapView);
		mBaiduMap = mMapView.getMap();
		
		
		//�������ż���
		mBaiduMap.setMaxAndMinZoomLevel(19, 3);
		
		
		// ע�� SDK �㲥������
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
		
		initNearByList(false);
		
		// ��ʾ�������˵�ͼ
		

		initLocClient();
		
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		

	}
	
	LatLng point;
	OverlayOptions textOption;
	// Bitmap markerBitmap;
	Bundle markerData = new Bundle();
	
	
	// �ڵ�ͼ����ʾ�������˵���Ϣ
	private void initNearsOnMap() {
		// ShowToast(nears.size() + "");
		
		Marker marker;
		
		// ͨ��ѭ�����������˵��ǳ���ʾ�ڵ�ͼ��
		for (User nearUser : nears) {
			
			// ShowToast(nearUser.getNick());
			
			markerText.setText(nearUser.getNick());
			// Toast.makeText(NearPeopleMapActivity.this, nearUser.getNick(), Toast.LENGTH_SHORT).show();
			
			//���û�ͼ����
		    layout_marker.setDrawingCacheEnabled(true);		
		    //����������������ǳ���Ҫ�����û�е�������������õ���bitmapΪnull
		    layout_marker.measure(MeasureSpec.makeMeasureSpec(150, MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(60, MeasureSpec.EXACTLY));
		    //�������Ҳ�ǳ���Ҫ�����ò��ֵĳߴ��λ��
		    layout_marker.layout(0, 0, layout_marker.getMeasuredWidth(), layout_marker.getMeasuredHeight());
		    //��û�ͼ�����е�Bitmap
		    layout_marker.buildDrawingCache();
		    Bitmap markerBitmap = layout_marker.getDrawingCache();
			
			ShowLog("+++++++++++++++" + markerBitmap.getWidth() + "  " + markerBitmap.getHeight());
			
			// ��ȡ�������˵ĵ���λ����Ϣ
			BmobGeoPoint location = nearUser.getLocation();
			point = new LatLng(location.getLatitude(), location.getLongitude());
			
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(markerBitmap);
			
			//����MarkerOption�������ڵ�ͼ�����Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(point)  
			    .icon(bitmap);  
			
			//�ڵ�ͼ�����Marker������ʾ  
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
			progress.setMessage("���ڲ�ѯ��������...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
		// ��ǰ�û��ĵ���λ����Ϣ��Ϊ��
		if(!mApplication.getLatitude().equals("")&&!mApplication.getLongtitude().equals("")){
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double.parseDouble(mApplication.getLongtitude());
			BRequest.QUERY_LIMIT_COUNT = 20;
			//��װ�Ĳ�ѯ�������������ҳ��ʱ isUpdateΪfalse��������ˢ�µ�ʱ������Ϊtrue���С�
			//�˷���Ĭ��ÿҳ��ѯ10������,�����ѯ����10�������ڲ�ѯ֮ǰ����BRequest.QUERY_LIMIT_COUNT���磺BRequest.QUERY_LIMIT_COUNT=20
			// �˷����������Ĳ�ѯָ��1�����ڵ��Ա�ΪŮ�Ե��û��б�Ĭ�ϰ��������б�
			//����㲻���ѯ�Ա�ΪŮ���û������Խ�equalProperty��Ϊnull����equalObj��Ϊnull����
			
//		    ��ҳ����ָ�����ﷶΧ���û����ų��Լ����Ƿ��ų������ɿ����߾�����������Ӷ����ѯ����
//
//		    ������
//		        isPull���Ƿ�������ˢ�¶��������������������������������Ϊtrue,��������Ϊfalse - 
//		        page����ǰ��ѯҳ�� - 
//		        property����ѯ�������Լ������λ������ - 
//		        longtitude������ - 
//		        latitude��γ�� - 
//		        isShowFriends���Ƿ���ʾ�����ĺ��� - 
//		        kilometers - ��������
//		        equalProperty���Լ�������������ԣ�ʹ�÷���AddWhereEqualTo��Ӧ���������� - 
//		        equalObj����ѯequalProperty���Զ�Ӧ������ֵ - 
//		        findCallback - ���ص� 
			userManager.queryKiloMetersListByPage(isUpdate,0,"location", longtitude, latitude, true, QUERY_KILOMETERS,"sex", sexValue, new FindListener<User>() {
			
				
//			    ��ҳ����ȫ�����û��б��ų��Լ����Ƿ��ų������ɿ����߾�����������Ӷ����ѯ����
//
//			    ������
//			        isPull���Ƿ�������ˢ�¶��������������������������������Ϊtrue,��������Ϊfalse - 
//			        page����ǰ��ѯҳ�� - 
//			        locationProperty���Լ������λ������ - 
//			        longtitude������ - 
//			        latitude��γ�� - 
//			        isShowFriends���Ƿ���ʾ�����ĺ��� - 
//			        equalProperty���Լ�������������ԣ�ʹ�÷���AddWhereEqualTo��Ӧ���������� - 
//			        equalObj����ѯequalProperty���Զ�Ӧ������ֵ - 
//			        findCallback - ���ص� 
				
			//�˷���Ĭ�ϲ�ѯ���д�����λ����Ϣ�����Ա�ΪŮ���û��б�����㲻����������б�Ļ�������ѯ�����е�isShowFriends����Ϊfalse����
//			userManager.queryNearByListByPage(isUpdate,0,"location", longtitude, latitude, true,"sex",false,new FindListener<User>() {

				// ��ѯ�ɹ�
				@Override
				public void onSuccess(List<User> arg0) {
					// TODO Auto-generated method stub
					if (CollectionUtils.isNotNull(arg0)) {
						// �����ˢ�µĻ�
						if(isUpdate){
							nears.clear();
						}
						
						
						
						nears.addAll(arg0);
						
						initNearsOnMap();
						
						if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
							// mListView.setPullLoadEnable(true);
							// ShowToast("���������������!");
						}else{
							// mListView.setPullLoadEnable(true);
						}
					}else{
						ShowToast("���޸�������!");
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
					ShowToast("��ѯ�������˳���!");
					// mListView.setPullLoadEnable(false);
					if(!isUpdate){
						progress.dismiss();
					}else{
						// refreshPull();
					}
				}

			});
		}else{
			ShowToast("���޸�������!");
			progress.dismiss();
			// refreshPull();
		}
		
	}

	/**
	 * �ص��������
	 * @Title: gotoChatPage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void gotoChatPage() {
		if(lastLocation!=null){
			Intent intent = new Intent();
			intent.putExtra("y", lastLocation.getLongitude());// ����
			intent.putExtra("x", lastLocation.getLatitude());// ά��
			intent.putExtra("address", lastLocation.getAddrStr());
			setResult(RESULT_OK, intent);
			this.finish();
		}else{
			ShowToast("��ȡ����λ����Ϣʧ��!");
		}
	}

	private void initLocClient() {
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
				com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// ���ò�Ʒ��
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			// ����λ
		    mLocClient.requestLocation();

		if (lastLocation != null) {
			// ��ʾ�ڵ�ͼ��
			LatLng ll = new LatLng(lastLocation.getLatitude(),
					lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
						.getLongitude()) {
					BmobLog.i("��ȡ������ͬ");// �����������ȡ���ĵ���λ����������ͬ�ģ����ٶ�λ
					mLocClient.stop();
					return;
				}
			}
			lastLocation = location;
			
			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
					+ location.getLatitude() + ",��ַ = "
					+ lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// ��Geo����
				// �����ַΪ�գ���ͨ�����Ⱥ�γ����Ϣ���з���Geo����
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// ��ʾ�ڵ�ͼ��
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			//���ð�ť�ɵ��
			mHeaderLayout.getRightImageButton().setEnabled(true);
		}

	}

	/**
	 * ����㲥�����࣬���� SDK key ��֤�Լ������쳣�㲥
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("�������");
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
			// �˳�ʱ���ٶ�λ
			mLocClient.stop();
		}
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// ȡ������ SDK �㲥
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// ���� bitmap ��Դ
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
