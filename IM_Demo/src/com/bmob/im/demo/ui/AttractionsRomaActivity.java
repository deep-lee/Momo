package com.bmob.im.demo.ui;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.config.Attractions;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AttractionsRomaActivity extends ActivityBase {
	
	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	
	ImageView tips, fly;
	

    Animation operatingAnim;
    LinearInterpolator lin;
    RotateAnimation rAnimation;
    AnimationSet set;
    
    SharedPreferences preferences;
    Attractions attractions;
    
    BmobGeoPoint randomGeoPoint;
    BmobGeoPoint currentGeoPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions_roma);
		
		preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		attractions = new Attractions();
		
//		initTopBarForLeft("¾°µãÂþÓÎ");
		
		tips = (ImageView) findViewById(R.id.attractions_roma_tips);
		
		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
		fly = (ImageView) findViewById(R.id.attractions_roma_fly);
		fly.setScaleX((float) 0.5);
		fly.setScaleY((float) 0.5);
		
		set = new AnimationSet(true);
		
		operatingAnim = AnimationUtils.loadAnimation(this, R.anim.attractions_roma_airship_rotate);  
		lin = new LinearInterpolator();  
		operatingAnim.setInterpolator(lin);
		
		rAnimation = new RotateAnimation(0,359,Animation.RELATIVE_TO_PARENT,0.2f,Animation.RELATIVE_TO_PARENT,0.3f);
		
		rAnimation.setDuration(2000);
		
		set.addAnimation(operatingAnim);
		set.addAnimation(rAnimation);
		
		mShakeListener = new ShakeListener(AttractionsRomaActivity.this);
        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				
				startVibrato();
				
			    runFly();
			    
			    mShakeListener.stop();
			    
			    currentGeoPoint = new BmobGeoPoint(Double.parseDouble(mApplication.getLongtitude()), Double.parseDouble(mApplication.getLatitude()));
			    
			    randomGeoPoint = attractions.getRandomAttractions();
			    mApplication.setLongtitude(randomGeoPoint.getLongitude() + "");
			    mApplication.setLatitude(randomGeoPoint.getLatitude() + "");
			    
			    ShowToast(randomGeoPoint.getLongitude() + "");

			   				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						
					stopFly();
					mVibrator.cancel();
					
					
					Intent intent = new Intent();
					intent.setClass(AttractionsRomaActivity.this, NearPeopleMapActivity.class);
					intent.putExtra("currentGeoPoint", currentGeoPoint);
					intent.putExtra("randomGeoPoint", randomGeoPoint);
					startActivity(intent);
					
					}
				}, 2500);
			}
		});
	}
	
	@Override
	public void	onResume()
	{
		super.onResume();
		mShakeListener.start();
	}
	
	public void startVibrato(){	
		MediaPlayer player;
		player = MediaPlayer.create(this, R.raw.awe);
		player.setLooping(false);
        player.start();
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); 
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	
	private void runFly() {

	    if (operatingAnim != null) {  
	    	tips.setVisibility(View.INVISIBLE);
	    	fly.setVisibility(View.VISIBLE);
	        fly.startAnimation(set);  
	    }  

	}
	
	private void stopFly() {
		fly.clearAnimation();  
	}
}
