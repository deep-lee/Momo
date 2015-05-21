package com.bmob.im.demo.ui;


import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameTopicContentAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;


public class GameCommunityActivity extends BaseMainActivity implements OnPageChangeListener{
	
	private ViewPager mViewPager;
	private GameTopicContentAdapter mAdapter;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_game_community);
		
		initView();
	}
	
	public void initView() {
		mViewPager = (ViewPager)findViewById(R.id.game_community_viewpager);
		mAdapter = new GameTopicContentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(4);
	}
	
	
	
	public void back(View view) {
   	 	finish();
   	 	overridePendingTransition(0, R.anim.base_slide_right_out);
    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		ShowToast("" + arg0);
		// Log.i(TAG, arg0+"--->");
	}
	
	public void setCurrentPage(int targetIndex){
		mViewPager.setCurrentItem(targetIndex, false);
	}
	
	public void writeTopic(View view) {
		Intent intent = new Intent();
		intent.setClass(GameCommunityActivity.this, WriteGameTopicActivity.class);
		startActivity(intent);
	}
}
