package com.deep.ui.update;


import com.bmob.im.demo.R;
import com.deep.ui.fragment.update.NaviFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity2 extends BaseSlidingFragmentActivity implements OnClickListener{
	
	public static final String TAG = "MainActivity";
	private NaviFragment naviFragment;
	private ImageView leftMenu;
	private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_2);
		
		leftMenu = (ImageView) findViewById(R.id.topbar_menu_left);
		leftMenu.setOnClickListener(this);
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
		default:
			break;
		}
	}

}
