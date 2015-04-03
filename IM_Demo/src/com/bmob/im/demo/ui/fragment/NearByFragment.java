package com.bmob.im.demo.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.ui.NearPeopleMapActivity;
import com.bmob.im.demo.ui.ShakeActivity;
import com.bmob.im.demo.util.ActionItem;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.TitlePopup;

public class NearByFragment extends FragmentBase implements OnClickListener{
	
	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	
	Context mContext;
	TitlePopup titlePopup;
	
	
	public NearByFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_shake, container, false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initData();
		initView();
	}
	
	private void initData(){
		titlePopup.addAction(new ActionItem(mContext, R.string.only_female, R.drawable.only_female));
		titlePopup.addAction(new ActionItem(mContext, R.string.only_male, R.drawable.only_male));
		titlePopup.addAction(new ActionItem(mContext, R.string.all_female_male, R.drawable.all_female_male));
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		titlePopup = new TitlePopup(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		initTopBarForRight("¸½½üµÄÈË", R.drawable.title_btn_function, new onRightImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				
			}
		});
		
		mVibrator = (Vibrator)((MainActivity)mContext).getApplication().getSystemService(mContext.VIBRATOR_SERVICE);
		
		mImgUp = (RelativeLayout) findViewById(R.id.fragment_shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.fragment_shakeImgDown);
		
		
		mShakeListener = new ShakeListener((MainActivity)mContext);
        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
				mShakeListener.stop();
				
				startVibrato();
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
					mVibrator.cancel();
					mShakeListener.start();
					
					Intent intent = new Intent();
					intent.setClass(mContext, NearPeopleMapActivity.class);
					mContext.startActivity(intent);
					
					}
				}, 1000);
			}
		});
	}

	public void startAnim () {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(500);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(500);
		mytranslateanimup1.setStartOffset(500);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(500);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(500);
		mytranslateanimdn1.setStartOffset(500);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	public void startVibrato(){	
		MediaPlayer player;
		player = MediaPlayer.create(mContext, R.raw.awe);
		player.setLooping(false);
        player.start();
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); 
	}
	
}
