package com.bmob.im.demo.ui;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.bmob.im.demo.R;
import com.bmob.im.demo.config.Attractions;
import com.bmob.im.demo.config.BmobConstants;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.sliders.SlideInRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInUpAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutUpAnimator;
import com.nineoldandroids.animation.Animator;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AttractionsRomaActivity2 extends ActivityBase {
	
	ImageView plant, sky_bg, plant2;
	TextView tv_tip1, tv_tips2;
	Button btn_begin_roma;
	
	Attractions attractions;
	int duration = 200;
	
	int duration2= 2000;
	
	Boolean flag = false;
	
	Boolean flag2 = true;
	
	int finalRandom;
	
	int times = 15;
	
	YoYo.AnimationComposer animationSlideInUp, animationSlideOutUp, animationSlideOutLeft, animationSlideInRight;
	
	BmobGeoPoint randomGeoPoint;
	BmobGeoPoint currentGeoPoint;
	
	Handler tipsHandler1 = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 0:
            		if (times > 0) {
            			// tv_tip1.setText(Attractions.attractionsName.get(Attractions.getRandom()));
                		animationSlideOutUp.playOn(tv_tip1);
                		
                		Message msg1 = new Message();
                		msg1.what = 1;
                		tipsHandler2.sendMessageDelayed(msg1, 300);
                		
                		times--;
					}
            		else {
						// ���븽�����˲鿴���棬�ı����λ����Ϣ
            			randomGeoPoint = Attractions.attarctionsSet.get(finalRandom);
         			    mApplication.setLongtitude(randomGeoPoint.getLongitude() + "");
         			    mApplication.setLatitude(randomGeoPoint.getLatitude() + "");
         			    
         			    Intent intent = new Intent();
	   					intent.setClass(AttractionsRomaActivity2.this, NearPeopleMapActivity.class);
	   					intent.putExtra("currentGeoPoint", currentGeoPoint);
	   					intent.putExtra("randomGeoPoint", randomGeoPoint);
	   					startActivity(intent);
					}
            		
            		break;
            
            }   
            super.handleMessage(msg); 
		}
	};
	
	Handler tipsHandler2 = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 1:
            		finalRandom = Attractions.getRandom();
            		tv_tip1.setText(Attractions.attractionsName.get(finalRandom));
            		animationSlideInUp.duration(duration);
            		animationSlideInUp.playOn(tv_tip1);

            		Message msg1 = new Message();
            		msg1.what = 0;
            		tipsHandler1.sendMessageDelayed(msg1, duration);
            		
            		duration = duration + 50;
            		
            		break;
            
            }   
            super.handleMessage(msg); 
		}
	};
	
	// ��һ��plant��ȥ��֪ͨ�ڶ���plant����
	Handler tipsHandler3 = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 2:
            		
            		// �ڶ���plant����
            		plant2.setVisibility(View.VISIBLE);
            		animationSlideInRight.playOn(plant2);
            		
            		////////////////
            		Message msg1 = new Message();
            		msg1.what = 3;
            		tipsHandler4.sendMessageDelayed(msg1, duration2);
            		
            		break;
            
            }   
            super.handleMessage(msg); 
		}
	};
	
	// �ڶ���plant������֪ͨ�ڶ���plant��ȥ
		Handler tipsHandler4 = new Handler(){
			public void handleMessage(Message msg) {   
	            switch (msg.what) {   
	            	case 3:
	            		
	            		// �ڶ���plant��ȥ
	            		animationSlideOutLeft.playOn(plant2);
	            		
	            		////////////////
	            		Message msg1 = new Message();
	            		msg1.what = 4;
	            		tipsHandler5.sendMessageDelayed(msg1, duration2);
	            		
	            		break;
	            
	            }   
	            super.handleMessage(msg); 
			}
		};
		
		// �ڶ���plant��ȥ��֪ͨ��һ��plant����
				Handler tipsHandler5 = new Handler(){
					public void handleMessage(Message msg) {   
			            switch (msg.what) {   
			            	case 4:
			            		animationSlideInRight.playOn(plant);
			            		plant2.setVisibility(View.INVISIBLE);
			            		
			            		////////////////
			            		Message msg1 = new Message();
			            		msg1.what = 5;
			            		tipsHandler6.sendMessageDelayed(msg1, duration2);
			            		
			            		break;
			            
			            }   
			            super.handleMessage(msg); 
					}
				};
				
				// ��һ��plant������֪ͨ��һ��plant��ȥ
				Handler tipsHandler6 = new Handler(){
					public void handleMessage(Message msg) {   
			            switch (msg.what) {   
			            	case 5:
			            		animationSlideOutLeft.playOn(plant);
			            		
			            		////////////////
			            		Message msg1 = new Message();
			            		msg1.what = 2;
			            		tipsHandler3.sendMessageDelayed(msg1, duration2);
			            		
			            		break;
			            
			            }   
			            super.handleMessage(msg); 
					}
				};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions_roma_2);
		initView();
	}
	
	private void initView() {
		
		initTopBarForLeft("��������");
		attractions = new Attractions();
		
		plant = (ImageView) findViewById(R.id.roma_sky_bg_plante);
		plant2 = (ImageView) findViewById(R.id.roma_sky_bg_plante2);
		plant2.setVisibility(View.INVISIBLE);
		sky_bg = (ImageView) findViewById(R.id.roma_sky_round_bg);
		tv_tip1 = (TextView) findViewById(R.id.roma_tips_1);
		tv_tips2 = (TextView) findViewById(R.id.roma_tips_2);
		btn_begin_roma = (Button) findViewById(R.id.roma_start_btn);
		
		currentGeoPoint = new BmobGeoPoint(Double.parseDouble(mApplication.getLongtitude()), Double.parseDouble(mApplication.getLatitude()));
		
		animationSlideInUp = new AnimationComposer(new SlideInUpAnimator())
		.duration(100).interpolate(new AccelerateDecelerateInterpolator())
		.withListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		animationSlideOutUp = new AnimationComposer(new SlideOutUpAnimator())
		.duration(100).interpolate(new AccelerateDecelerateInterpolator())
		.withListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		animationSlideOutLeft = new AnimationComposer(new SlideOutLeftAnimator())
		.duration(2000).interpolate(new AccelerateDecelerateInterpolator())
		.withListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		animationSlideInRight = new AnimationComposer(new SlideInRightAnimator())
		.duration(2000).interpolate(new AccelerateDecelerateInterpolator())
		.withListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_begin_roma.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				// ��ʼ����
				
				// �Ѿ����˿�ʼ���Σ���������һ�ε��
				if (flag) {
					times = 15;
					duration = 200;
				}
				else {
					if (flag2) {
						Message msg1= new Message();
						msg1.what = 5;
						tipsHandler6.sendMessage(msg1);
					}
					
					tv_tips2.setText("���ڽ�������...");
					
					Message msg = new Message();
	        		msg.what = 1;
	        		tipsHandler2.sendMessageDelayed(msg, duration);
	        		
	        		flag = true;
				}
			}
		});
	}
	
	
	
	@Override
	public void onResume(){
		super.onResume();
		
		flag = false;
		times = 15;
		duration = 200;
		tv_tip1.setText(R.string.roma_tips_1);
		tv_tips2.setText(R.string.roma_tips_2);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		flag2 = false;
		
	}
	
}