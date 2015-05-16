package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.QiangContentAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

public class LifeCircleActivity extends BaseActivity implements OnPageChangeListener{
	
	private ViewPager mViewPager;
	private QiangContentAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_circle);
		
		mViewPager = (ViewPager)findViewById(R.id.life_circle_viewpager);
		mAdapter = new QiangContentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(4);
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
	
	public void writeState(View view) {
		Intent intent = new Intent();
		intent.setClass(LifeCircleActivity.this, WriteStateActivity.class);
		startActivity(intent);
	}
}
