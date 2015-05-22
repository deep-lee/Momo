package com.deep.ui.update;


import java.io.File;

import com.bmob.im.demo.R;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.ui.EditMyInfoActivity;
import com.deep.ui.fragment.update.NaviFragment;
import com.deep.ui.fragment.update.PersonInfoUpdateFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.soundcloud.android.crop.Crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity2 extends BaseSlidingFragmentActivity implements OnClickListener{
	
	public static int ETIT_MY_INFO = 100;
	
	public static final String TAG = "MainActivity";
	private NaviFragment naviFragment;
	private ImageView leftMenu;
	private SlidingMenu mSlidingMenu;
	
	public static View layout_all;
	public static TextView tv_edit_my_info;

	public static float y;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_2);
		
		layout_all = findViewById(R.id.layout_all);
		tv_edit_my_info = (TextView) findViewById(R.id.topbar_edit_my_info);
		tv_edit_my_info.setClickable(true);
		leftMenu = (ImageView) findViewById(R.id.topbar_menu_left);
		leftMenu.setOnClickListener(this);
		tv_edit_my_info.setOnClickListener(this);
		initFragment();
		
	}
	
	private void initFragment() {
		mSlidingMenu = getSlidingMenu();
		setBehindContentView(R.layout.frame_navi); // ��������slidingmenu��fragment�ƶ�layout
		naviFragment = new NaviFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_navi, naviFragment).commit();
		// ����slidingmenu������
		mSlidingMenu.setMode(SlidingMenu.LEFT);// ����slidingmeni���Ĳ����
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// ֻ���ڱ��ϲſ��Դ�
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// ƫ����
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setFadeDegree(0.5f);
		mSlidingMenu.setMenu(R.layout.frame_navi);

		Bundle mBundle = null;
		// �����򿪼����¼�
		mSlidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				naviFragment.showOpenAnimation();
				//ShowToast("Opened");
			}
		});
		
		// �����رռ����¼�
		mSlidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.topbar_menu_left:
			mSlidingMenu.toggle();
			break;
		case R.id.topbar_edit_my_info:
			Intent intent2 = new Intent();
			intent2.setClass(MainActivity2.this, EditMyInfoActivity.class);
			startActivityForResult(intent2, ETIT_MY_INFO);
			break;
		default:
			break;
		}
	}
	
	public void setLayoutAnimation(Animation animation) {
		layout_all.startAnimation(animation);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case 100:
			Boolean update = data.getBooleanExtra("update", true);
			PersonInfoUpdateFragment.update = update;
			break;
			
		case 200:
			TranslateAnimation animation = new TranslateAnimation(0, 0, -y, 0);
			animation.setDuration(200);
			animation.setFillAfter(true);
			layout_all.startAnimation(animation);
			super.onActivityResult(requestCode, resultCode, data);
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		mSlidingMenu.toggle();
	}
	
	public static void showEditMyInfo() {
		tv_edit_my_info.setVisibility(View.VISIBLE);
	}
	
	public static void hideEditMyInfo() {
		tv_edit_my_info.setVisibility(View.INVISIBLE);
	}

}
